package org.openhab.ui.webapp.cloud;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.repeatSecondlyForever;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudConfigDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(CloudConfigDispatcher.class);

	// by default, we use the "configurations" folder in the home directory, but this location
	// might be changed in certain situations (especially when setting a config folder in the
	// openHAB Designer).
	private static String configFolder =	"configurations"; 
	
	/** the last refresh timestamp in milliseconds */
	private static long lastReload = -1;

	/** the refresh interval. A value of '-1' deactivates the scan (optional, defaults to '-1' hence scanning is deactivated) */
	private static int refreshInterval = -1;
	
	/** the name of the scheduler group under which refresh jobs are being registered */
	private static final String SCHEDULER_GROUP = "CloudConfigDispatcher";
	
	/** the {@link JobKey} for the quartz job to refresh the main configuration file */
	private final static JobKey REFRESH_JOB_KEY = new JobKey("Refresh", SCHEDULER_GROUP);
	
	/** the {@link TriggerKey} for the quartz job to refresh the main configuration file */
	private final static TriggerKey REFRESH_TRIGGER_KEY = new TriggerKey("Refresh", SCHEDULER_GROUP);
	
	
	public void activate() {
		initializeBundleConfigurations();
		if (refreshInterval > -1) {
			scheduleRefreshJob();
		}
	}
	
	public void deactivate() {
		cancelRefreshJob();
	}

	
	/**
	 * Returns the configuration folder path name. The main config folder 
	 * <code>&lt;openhabhome&gt;/configurations</code> could be overwritten by setting
	 * the System property <code>openhab.configdir</code>.
	 * 
	 * @return the configuration folder path name
	 */
	public static String getConfigFolder() {
		String CONFIG_FILE_PROG_ARGUMENT = "openhab.configfile";
		String progArg = System.getProperty(CONFIG_FILE_PROG_ARGUMENT);
		if (progArg != null) {
			return progArg;
		} else {
			return configFolder;
		}
	}

	/**
	 * Sets the configuration folder to use. Calling this method will automatically
	 * trigger the loading and dispatching of the contained configuration files.
	 * 
	 * @param configFolder the path name to the new configuration folder
	 */
	public static void setConfigFolder(String configFolder) {
		CloudConfigDispatcher.configFolder = configFolder;
		initializeBundleConfigurations();
	}

	public static void initializeBundleConfigurations() {
		initializeDefaultConfiguration();			
		initializeMainConfiguration(lastReload);			
	}

	private static void initializeDefaultConfiguration() {
		String defaultConfigFilePath = getDefaultConfigurationFilePath();
		File defaultConfigFile = new File(defaultConfigFilePath);
		try {
			logger.debug("Processing openHAB default configuration file '{}'.", defaultConfigFile.getAbsolutePath());
			System.out.println("\n***CloudConfigDispatcher-initializeDefaultConfiguration -> defaultConfigfile "+defaultConfigFile.getAbsolutePath());			
			processConfigFile(defaultConfigFile);
		} catch (FileNotFoundException e) {
			// we do not care if we do not have a default file
		} catch (IOException e) {
			logger.error("Default openHAB configuration file '{}' cannot be read.", defaultConfigFilePath, e);
		}
	}

	private static void initializeMainConfiguration(long lastReload) {
		String mainConfigFilePath = getMainConfigurationFilePath();
		File mainConfigFile = new File(mainConfigFilePath);

		if (lastReload > -1 && mainConfigFile.lastModified() <= lastReload) {
			logger.trace(
				"main configuration file '{}' hasn't been changed since '{}' (lasModified='{}') -> initialization aborted.",
				new Object[] { mainConfigFile.getAbsolutePath(), lastReload, mainConfigFile.lastModified() });
			CloudConfigDispatcher.lastReload = System.currentTimeMillis();
			return;
		}
		
		try {
			logger.debug("Processing openHAB main configuration file '{}'.", mainConfigFile.getAbsolutePath());
			processConfigFile(mainConfigFile);
			CloudConfigDispatcher.lastReload = System.currentTimeMillis();
		} catch (FileNotFoundException e) {
			logger.warn("Main openHAB configuration file '{}' does not exist.", mainConfigFilePath);
		} catch (IOException e) {
			logger.error("Main openHAB configuration file '{}' cannot be read.", mainConfigFilePath, e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void processConfigFile(File configFile) throws IOException, FileNotFoundException {
//		ConfigurationAdmin configurationAdmin = 
//			(ConfigurationAdmin) ConfigActivator.configurationAdminTracker.getService();
//		if (configurationAdmin != null) {
			// we need to remember which configuration needs to be updated because values have changed.
			Map<Configuration, Dictionary> configsToUpdate = new HashMap<Configuration, Dictionary>();
			
			// also cache the already retrieved configurations for each pid
			Map<Configuration, Dictionary> configMap = new HashMap<Configuration, Dictionary>();
			
			List<String> lines = IOUtils.readLines(new FileInputStream(configFile));
			for(String line : lines) {					
				String[] contents = parseLine(configFile.getPath(), line);
				// no valid configuration line, so continue
				if(contents==null) continue;
				String pid = contents[0];
				String property = contents[1];
				String value = contents[2];
				Configuration configuration = null;//configurationAdmin.getConfiguration(pid, null);
				if(configuration!=null) {
					Dictionary configProperties = configMap.get(configuration);
					if(configProperties==null) {
						configProperties = new Properties();
						configMap.put(configuration, configProperties);
					}
					if(!value.equals(configProperties.get(property))) {
						configProperties.put(property, value);
						configsToUpdate.put(configuration, configProperties);
					}
				}
			}
			
			for(Entry<Configuration, Dictionary> entry : configsToUpdate.entrySet()) {
				entry.getKey().update(entry.getValue());
			}
//		}
	}

	private static String[] parseLine(final String filePath, final String line) {
		String trimmedLine = line.trim();
		
		if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) {
			return null;
		}
		
		if (trimmedLine.substring(1).contains(":")) { 
			String pid = StringUtils.substringBefore(line, ":");
			String rest = line.substring(pid.length() + 1);
			if(!pid.contains(".")) {
				pid = "org.openhab." + pid;
			}
			if(!rest.isEmpty() && rest.substring(1).contains("=")) {
				String property = StringUtils.substringBefore(rest, "=");
				String value = rest.substring(property.length() + 1);
				return new String[] { pid.trim(), property.trim(), value.trim() };
			}
		}
		
		logger.warn("Cannot parse line '{}' of main configuration file '{}'.", line, filePath);
		return null;
	}

	private static String getDefaultConfigurationFilePath() {
		String DEFAULT_CONFIG_FILENAME = "openhab_default.cfg";
		return configFolder + "/" + DEFAULT_CONFIG_FILENAME;
	}

	private static String getMainConfigurationFilePath() {
		 String CONFIG_FILE_PROG_ARGUMENT = "openhab.configfile";
		String progArg = System.getProperty(CONFIG_FILE_PROG_ARGUMENT);
		if (progArg != null) {
			return progArg;
		} else {
			String MAIN_CONFIG_FILENAME = "openhab.cfg";
			return getConfigFolder() + "/" + MAIN_CONFIG_FILENAME;
		}
	}
	
	
	/**
	 * Schedules a quartz job which is triggered every minute.
	 */
	public static void scheduleRefreshJob() {
		try {
			Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
			JobDetail job = newJob(RefreshJob.class)
			    .withIdentity(REFRESH_JOB_KEY)
			    .build();

			SimpleTrigger trigger = newTrigger()
			    .withIdentity(REFRESH_TRIGGER_KEY)
			    .withSchedule(repeatSecondlyForever(refreshInterval))
			    .build();

			sched.scheduleJob(job, trigger);
			logger.debug("Scheduled refresh job '{}' in DefaulScheduler", job.getKey());
		} catch (SchedulerException e) {
			logger.warn("Could not schedule refresh job: {}", e.getMessage());
		}
	}
	
	/**
	 * Reschedules a quartz job which is triggered every minute.
	 */
	public static void rescheduleRefreshJob() {
		try {
			Scheduler sched = StdSchedulerFactory.getDefaultScheduler();

			SimpleTrigger trigger = newTrigger()
			    .withIdentity(REFRESH_TRIGGER_KEY)
			    .withSchedule(repeatSecondlyForever(refreshInterval))
			    .build();
			
			sched.rescheduleJob(REFRESH_TRIGGER_KEY, trigger);
			logger.debug("Rescheduled refresh job '{}' in DefaulScheduler", REFRESH_TRIGGER_KEY);
		} catch (SchedulerException e) {
			logger.warn("Could not reschedule refresh job: {}", e.getMessage());
		}
	}
	
	private static void scheduleOrRescheduleRefreshJob() {
		try {
			Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
			if (sched.checkExists(REFRESH_JOB_KEY)) {
				rescheduleRefreshJob();
			} else {
				scheduleRefreshJob();
			}
		} catch (SchedulerException e) {
			logger.warn("Could not check if job exists: {}", e.getMessage());
		}
	}

	/**
	 * Deletes all quartz refresh jobs containing to group 'CloudConfigDispatcher'
	 */
	public static void cancelRefreshJob() {
		try {
			Scheduler sched = StdSchedulerFactory.getDefaultScheduler();
			Set<JobKey> jobKeys = sched.getJobKeys(jobGroupEquals(SCHEDULER_GROUP));
			if (jobKeys.size() > 0) {
				sched.deleteJobs(new ArrayList<JobKey>(jobKeys));
				logger.debug("Found {} refresh jobs to delete from DefaulScheduler (keys={})", jobKeys.size(), jobKeys);
			}
		} catch (SchedulerException e) {
			logger.warn("Could not remove refresh job: {}", e.getMessage());
		}		
	}
	
	
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			String refreshIntervalString = (String) config.get("refresh");
			if (isNotBlank(refreshIntervalString)) {
				try {
					CloudConfigDispatcher.refreshInterval = Integer.valueOf(refreshIntervalString);
				}
				catch (IllegalArgumentException iae) {
					logger.warn("couldn't parse '{}' to an integer");
				}
				
				if (CloudConfigDispatcher.refreshInterval == -1) {
					cancelRefreshJob();
				} else {
					scheduleOrRescheduleRefreshJob();
				}
			}
		}
	}
	
	
	/**
	 * A quartz scheduler job to refresh the Configuration (via {@link ConfigurationAdmin})
	 * when it changed.
	 */
	@DisallowConcurrentExecution
	public static class RefreshJob implements Job {
		
		public void execute(JobExecutionContext context) throws JobExecutionException {
			initializeMainConfiguration(lastReload);
		}
		
	}


}
