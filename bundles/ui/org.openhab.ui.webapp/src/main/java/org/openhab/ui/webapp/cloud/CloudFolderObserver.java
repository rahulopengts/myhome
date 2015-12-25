package org.openhab.ui.webapp.cloud;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.openhab.model.core.ModelCoreConstants;
import org.openhab.model.core.ModelRepository;
import org.openhab.model.core.internal.ModelRepositoryImpl;
import org.openhab.model.core.internal.util.MathUtils;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFolderObserver {
	private static final Logger logger = LoggerFactory
			.getLogger(CloudFolderObserver.class);

	/* map that lists all foldernames that should be observed and the frequency for checks in seconds */
	private final Map<String, Integer> folderRefreshMap = new ConcurrentHashMap<String, Integer>();

	/* map that stores a list of valid file extensions for each folder */
	private final Map<String, String[]> folderFileExtMap = new ConcurrentHashMap<String, String[]>();

	/* map that stores the time of the last check of a filename in milliseconds */
	private Map<String, Long> lastCheckedMap = new ConcurrentHashMap<String, Long>();
	
	/* map that remembers all filenames of the last check, so that it can detect file deletions */
	private Map<String, Set<String>> lastFileNames = new ConcurrentHashMap<String, Set<String>>();

	/* the greatest common divisor of all folder refresh rates */
	private int gcdRefresh = 1;
	
	/* the least common multiple of all folder refresh rates */
	private int lcmRefresh = 1;
	
	/* a counter to know which folders need to be refreshed when waking up */
	private int refreshCount = 0;
	
	/* the model repository is provided as a service */
	private ModelRepository modelRepo = null;
	
	
	public CloudFolderObserver() {
		modelRepo	=	new ModelRepositoryImpl();
	}
	
	public void setModelRepository(ModelRepository modelRepo) {
		this.modelRepo = modelRepo;
	}
	
	public ModelRepository getModelRepository() {
		return this.modelRepo;
	}

	
	public void run() {
		System.out.println("\n CloudFolderObserver : ");
		while(!folderRefreshMap.isEmpty()) { // keep the thread running as long as there are folders to observe
			
			try {
				for(String foldername : folderRefreshMap.keySet()) {
					// if folder has been checked at least once and it is not time yet to refresh, skip
					if( lastFileNames.get(foldername) != null  && 
							(refreshCount % folderRefreshMap.get(foldername) > 0)) {
						
						logger.debug("skipping refresh of folder '{}' folderRefreshMap={}",
								foldername, folderRefreshMap.get(foldername));
						continue;
					} 
					System.out.println("\n CloudFolderObserver->run->"+foldername+"::"+folderRefreshMap.get(foldername));
					logger.debug("Refreshing folder '{}'", foldername);
					checkFolder(foldername);
				}

				// increase the counter and set it to 0, if it reaches the max value
				refreshCount = (refreshCount + gcdRefresh) % lcmRefresh;
			} catch(Throwable e) {
				logger.error("An unexpected exception has occured", e);
			}			
			try {
				if(gcdRefresh <= 0) break;
				synchronized(CloudFolderObserver.this) {
					wait(gcdRefresh * 1000L);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	//CloudChange
	public void checkFolder(String foldername) {
		File folder = getFolder(foldername);
		if(!folder.exists()) {
			return;
		}
		String[] extensions = folderFileExtMap.get(foldername);
		System.out.println("\n CloudFolderObserver -> checkFolder-> extensions "+extensions[0]+":"+extensions[0]);
		// check current files and add or refresh them accordingly
		Set<String> currentFileNames = new HashSet<String>();
		for(File file : folder.listFiles()) {
			if(file.isDirectory()) continue;
			if(!file.getName().contains(".")) continue;
			if(file.getName().startsWith(".")) continue;
			
			System.out.println("\n CloudFolderObserver -> checkFolder-> fileName "+file.getName());
			// if there is an extension filter defined, continue if the file has a different extension
			String fileExt = getExtension(file.getName());
			if(extensions!=null && extensions.length>0 && !ArrayUtils.contains(extensions, fileExt)) continue;
			
			currentFileNames.add(file.getName());
			Long timeLastCheck = lastCheckedMap.get(file.getName());
			if(timeLastCheck==null) timeLastCheck = 0L;
			if(FileUtils.isFileNewer(file, timeLastCheck)) {
				if(modelRepo!=null) {
					try {
						System.out.println("\n CloudFolderObserver -> checkFolder-> addOrRefreshmodel "+file.getAbsolutePath());
						if(modelRepo.addOrRefreshModel(file.getName(), FileUtils.openInputStream(file))) {
							lastCheckedMap.put(file.getName(), new Date().getTime());							
						}
					} catch (IOException e) {
						logger.warn("Cannot open file '"+ file.getAbsolutePath() + "' for reading.", e);
					}
				}
			}
		}
		
		// check for files that have been deleted meanwhile
		if (lastFileNames.get(foldername) != null) {
			for (String fileName : lastFileNames.get(foldername)) {
				if (!currentFileNames.contains(fileName)) {
					logger.info("File '{}' has been deleted", fileName);
					if (modelRepo != null) {
						modelRepo.removeModel(fileName);
						lastCheckedMap.remove(fileName);
					}
				}
			}
		}
		lastFileNames.put(foldername, currentFileNames);
	}

	private String getExtension(String filename) {
		String fileExt = filename.substring(filename.lastIndexOf(".") + 1);
		return fileExt;
	}

	@SuppressWarnings("rawtypes")
	public void updated(Dictionary config) throws ConfigurationException {
		if (config != null) {
			// make sure to clear the caches first
			lastFileNames.clear();
			lastCheckedMap.clear();
			folderFileExtMap.clear();
			folderRefreshMap.clear();
			System.out.println("\n CloudFolderObserver->updated->config "+config);
			Enumeration keys = config.keys();
			while (keys.hasMoreElements()) {
				
				
				String foldername = (String) keys.nextElement();
				//System.out.println("\n CloudFolderObserver->updated->Dictionary "+foldername+"::"+(String) config.get(foldername));
				if(foldername.equals("service.pid")) continue;
				
				System.out.println("\n CloudFolderObserver->config.get(foldername)->"+config.get(foldername));
				String[] values = ((String) config.get(foldername)).split(",");
				try {
					Integer refreshValue = Integer.valueOf(values[0]);
					String[] fileExts = (String[]) ArrayUtils.remove(values, 0);
					File folder = getFolder(foldername);
					if (folder.exists() && folder.isDirectory()) {
						folderFileExtMap.put(foldername, fileExts);
						if (refreshValue > 0) {
							folderRefreshMap.put(foldername, refreshValue);
								// make sure that we notify the sleeping thread and directly refresh the folders
									//notify();
									System.out.println("\n CloudFoldeObserver->CheckFolder ->"+foldername);
									checkFolder(foldername);
						} else {
							// deactivate the refresh for this folder
							folderRefreshMap.remove(foldername);
							checkFolder(foldername);
						}
					} else {
						logger.warn(
								"Directory '{}' does not exist in '{}'. Please check your configuration settings!",
								foldername, "");
					}
					
					// now update the refresh information for the thread
					Integer[] refreshValues = folderRefreshMap.values().toArray(new Integer[0]);
//					if(refreshValues.length>0) {
//						gcdRefresh = MathUtils.gcd(refreshValues);
//						lcmRefresh = MathUtils.lcm(refreshValues);
//					}
					refreshCount = 0;
				} catch (NumberFormatException e) {
					logger.warn(
							"Invalid value '{}' for configuration '{}'. Integer value expected!",
							values[0], ModelCoreConstants.SERVICE_PID + ":"
									+ foldername);
				}

			}
		}
	}

	/**
	 * returns the {@link File} object for a given foldername
	 * @param foldername the foldername to get the {@link File} for
	 * @return the corresponding {@link File}
	 */
	private File getFolder(String foldername) {
		String configFileRootFolder	=	"D:\\Home_Auto\\raspberry\\Latest\\openHAB_Installer\\openhab-master\\distribution\\openhabhome\\configurations";
		File folder = new File(configFileRootFolder
				+ File.separator + foldername);
		System.out.println("\n CloudFolderObserver -> getFolder-> extensions "+foldername+":"+folder.getPath());
		return folder;
	}

/*	
	
	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.chart

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.mqtt

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.tcp

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.hue

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.logging

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.ntp

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.folder

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.persistence

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.mqtt-eventbus

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.chart

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.mqtt

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.tcp

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.hue

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.logging

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.xmpp

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.security

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.ntp

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.mail

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.folder

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.nma

	***ConfigDispather->processConfigFile->configuration.getPid() org.openhab.persistence
	
*/}
