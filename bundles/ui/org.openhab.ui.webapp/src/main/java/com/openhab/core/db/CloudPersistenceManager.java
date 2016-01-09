package com.openhab.core.db;

import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openhab.core.items.GenericItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.persistence.FilterCriteria;
import org.openhab.core.persistence.HistoricItem;
import org.openhab.core.persistence.PersistenceService;
import org.openhab.core.persistence.QueryablePersistenceService;
import org.openhab.core.persistence.internal.PersistenceManager;
import org.openhab.core.types.State;
import org.openhab.model.core.EventType;
import org.openhab.model.core.ModelRepository;
import org.openhab.persistence.rrd4j.internal.RRD4jService;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openhab.core.internal.event.processor.CloudAbstractEventSubscriber;

public class CloudPersistenceManager extends CloudAbstractEventSubscriber {
	//extends AbstractEventSubscriber implements ModelRepositoryChangeListener, ItemRegistryChangeListener, StateChangeListener, PersistentStateRestorer {
		
		private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

		private static PersistenceManager instance;
		
		// the scheduler used for timer events
		private Scheduler scheduler;
		
		/*default */ ModelRepository modelRepository;

		private ItemRegistry itemRegistry;

		/*default */ Map<String, PersistenceService> persistenceServices = new HashMap<String, PersistenceService>();
		
//		/** keeps a list of configurations for each persistence service */
//		protected Map<String, List<PersistenceConfiguration>> persistenceConfigurations = new ConcurrentHashMap<String, List<PersistenceConfiguration>>();
//
//		/** keeps a list of default strategies for each persistence service */
//		protected Map<String, List<Strategy>> defaultStrategies = 
//				Collections.synchronizedMap(new HashMap<String, List<Strategy>>());
//		
		
		public CloudPersistenceManager() {
			// TODO Auto-generated constructor stub
		}
		
		
		public void activate() {
		}
		
		public void deactivate() {
		}
		
		
		public void setModelRepository(ModelRepository modelRepository) {
			this.modelRepository = modelRepository;
		}

		public void unsetModelRepository(ModelRepository modelRepository) {
			this.modelRepository = null;
		}

		public void setItemRegistry(ItemRegistry itemRegistry) {
			this.itemRegistry = itemRegistry;
			
			//allItemsChanged(null);
		}

		public void unsetItemRegistry(ItemRegistry itemRegistry) {
			//itemRegistry.removeItemRegistryChangeListener(this);
			this.itemRegistry = null;
		}

		public void addPersistenceService(PersistenceService persistenceService) {
			logger.debug("Initializing {} persistence service.", persistenceService.getName());
			persistenceServices.put(persistenceService.getName(), persistenceService);
			stopEventHandling(persistenceService.getName());
			startEventHandling(persistenceService.getName());
		}

		public void removePersistenceService(PersistenceService persistenceService) {
			stopEventHandling(persistenceService.getName());
			persistenceServices.remove(persistenceService.getName());
		}
		
		
		public void modelChanged(String modelName, EventType type) {
			if(modelName.endsWith(".persist")) {
				String serviceName = modelName.substring(0, modelName.length()-".persist".length());
				if(type==EventType.REMOVED || type==EventType.MODIFIED) {
					stopEventHandling(serviceName);
				}
		
				if(type==EventType.ADDED || type==EventType.MODIFIED) {
					if(itemRegistry!=null && persistenceServices.containsKey(serviceName)) {
						startEventHandling(serviceName);
					}
				}
			}
		}

		/**
		 * Registers a persistence model file with the persistence manager, so that it becomes active.
		 * 
		 * @param modelName the name of the persistence model without file extension
		 */
		private void startEventHandling(String modelName) {
//			PersistenceModel model = (PersistenceModel) modelRepository.getModel(modelName + ".persist");
//			//System.out.println("\nPersistenceManager->startEventHandling-> modelName->"+modelName+"->modelRepository "+modelRepository);
//			if(model!=null) {
//				//System.out.println("\nPersistenceManager->startEventHandling-> model.getConfigs() "+model.getConfigs());
//				persistenceConfigurations.put(modelName, model.getConfigs());
//				defaultStrategies.put(modelName, model.getDefaults());
//				initializeItems(model, modelName);
//				createTimers(modelName);
//			}
		}

		/**
		 * Unregisters a persistence model file from the persistence manager, so that it is not further regarded.
		 * 
		 * @param modelName the name of the persistence model without file extension
		 */
		private void stopEventHandling(String modelName) {
//			persistenceConfigurations.remove(modelName);
//			defaultStrategies.remove(modelName);
//			removeTimers(modelName);
		}

		//@Override
		public void initializeItems(String modelName) {
//			PersistenceModel model = (PersistenceModel) modelRepository.getModel(modelName + ".persist");
//			if(model!=null) {
//				initializeItems(model, modelName);
//			}		
		}
		
		public void initializeItems(ItemRegistry	itemRegistry) {
			System.out.println("\nCloudPersistenceManager->initializeItems-> "+itemRegistry);
			this.itemRegistry	=	itemRegistry;
			Collection<Item> itemMap	=	itemRegistry.getItems();
			if(itemMap!=null){
			Iterator	iterate	=	itemMap.iterator();
				while(iterate.hasNext()){
					Item item	=	(Item)iterate.next();
					System.out.println("\nCloudPersistenceManager->initializeItems-> "+item.getName());
					initialize(item);
				}
			}
		}

		public void stateChanged(Item item, State oldState, State newState) {
			handleStateEvent(item, true);
		}

		public void stateUpdated(Item item, State state) {
			handleStateEvent(item, false);
		}

		/**
		 * Calls all persistence services which use change or update policy for the given item
		 * 
		 * @param item the item to persist
		 * @param onlyChanges true, if it has the change strategy, false otherwise
		 */
		private void handleStateEvent(Item item, boolean onlyChanges) {
//			synchronized (persistenceConfigurations) {
//				for(Entry<String, List<PersistenceConfiguration>> entry : persistenceConfigurations.entrySet()) {
//					String serviceName = entry.getKey();
//					if(persistenceServices.containsKey(serviceName)) {				
//						for(PersistenceConfiguration config : entry.getValue()) {
//							if(hasStrategy(serviceName, config, onlyChanges ? GlobalStrategies.CHANGE : GlobalStrategies.UPDATE)) {
//								if(appliesToItem(config, item)) {
									RRD4jService	persistenceServices	=	new RRD4jService();
									persistenceServices.store(item, null);
//								}
//							}
//						}
//					}
//				}
//			}
		}
		
		/**
		 * Checks if a given persistence configuration entry has a certain strategy for the given service
		 * 
		 * @param serviceName the service to check the configuration for
		 * @param config the persistence configuration entry
		 * @param strategy the strategy to check for
		 * @return true, if it has the given strategy
		 */
//		protected boolean hasStrategy(String serviceName, PersistenceConfiguration config, Strategy strategy) {
//			if(defaultStrategies.get(serviceName).contains(strategy) && config.getStrategies().isEmpty()) {
//				return true;
//			} else {
//				for(Strategy s : config.getStrategies()) {
//					if(s.equals(strategy)) {
//						return true;
//					}
//				}
//				return false;
//			}
//		}

		/**
		 * Checks if a given persistence configuration entry is relevant for an item
		 * 
		 * @param config the persistence configuration entry
		 * @param item to check if the configuration applies to
		 * @return true, if the configuration applies to the item
		 */
//		protected boolean appliesToItem(PersistenceConfiguration config, Item item) {
//			for(EObject itemCfg : config.getItems()) {
//				if (itemCfg instanceof AllConfig) {
//					return true;
//				}
//				if (itemCfg instanceof ItemConfig) {
//					ItemConfig singleItemConfig = (ItemConfig) itemCfg;
//					if(item.getName().equals(singleItemConfig.getItem())) {
//						return true;
//					}
//				}
//				if (itemCfg instanceof GroupConfig) {
//					GroupConfig groupItemCfg = (GroupConfig) itemCfg;
//					String groupName = groupItemCfg.getGroup();
//					try {
//						Item gItem = itemRegistry.getItem(groupName);
//						if (gItem instanceof GroupItem) {
//							GroupItem groupItem = (GroupItem) gItem;
//							if(groupItem.getAllMembers().contains(item)) {
//								return true;
//							}
//						}
//					} catch (Exception e) {}
//				}
//			}
//			return false;
//		}
//
		/**
		 * Retrieves all items for which the persistence configuration applies to.
		 * 
		 * @param config the persistence configuration entry
		 * @return all items that this configuration applies to
		 */
//			// first check, if we should return them all
//			for(EObject itemCfg : config.getItems()) {
//				if (itemCfg instanceof AllConfig) {
//					return itemRegistry.getItems();
//				}
//			}
//			
//			// otherwise, go through the detailed definitions
//			Set<Item> items = new HashSet<Item>();
//			for(EObject itemCfg : config.getItems()) {
//				if (itemCfg instanceof ItemConfig) {
//					ItemConfig singleItemConfig = (ItemConfig) itemCfg;
//					try {
//						Item item = itemRegistry.getItem(singleItemConfig.getItem());
//						items.add(item);
//					} catch (ItemNotFoundException e) {
//						logger.debug("Item '{}' does not exist.", singleItemConfig.getItem());
//					}
//				}
//				if (itemCfg instanceof GroupConfig) {
//					GroupConfig groupItemCfg = (GroupConfig) itemCfg;
//					String groupName = groupItemCfg.getGroup();
//					try {
//						Item gItem = itemRegistry.getItem(groupName);
//						if (gItem instanceof GroupItem) {
//							GroupItem groupItem = (GroupItem) gItem;
//							items.addAll(groupItem.getAllMembers());
//						}
//					} catch (ItemNotFoundException e) {
//						logger.debug("Item group '{}' does not exist.", groupName);
//					}
//				}
//			}
//			return items;
//		}

//		public void allItemsChanged(Collection<String> oldItemNames) {
//			for(Item item : itemRegistry.getItems()) {
//				itemAdded(item);
//			}
//		}

//		public void itemAdded(Item item) {
//			initialize(item);
//			if (item instanceof GenericItem) {
//				GenericItem genericItem = (GenericItem) item;
//				genericItem.addStateChangeListener(this);
//			}
//		}

		/**
		 * Handles the "restoreOnStartup" strategy for the item.
		 * If the item state is still undefined when entering this method, all persistence configurations are checked,
		 * if they have the "restoreOnStartup" strategy configured for the item. If so, the item state will be set
		 * to its last persisted value.
		 * 
		 * @param item the item to restore the state for
		 */
		protected void initialize(Item item) {
			// get the last persisted state from the persistence service if no state is yet set
			QueryablePersistenceService queryService = new RRD4jService();//(QueryablePersistenceService) service;
			((RRD4jService)queryService).setItemRegistry(itemRegistry);
			FilterCriteria filter = new FilterCriteria().setItemName(item.getName()).setPageSize(1);
			Iterable<HistoricItem> result = queryService.query(filter);
			Iterator<HistoricItem> it = result.iterator();
			if(it.hasNext()) {
				HistoricItem historicItem = it.next();
				GenericItem genericItem = (GenericItem) item;
				//genericItem.removeStateChangeListener(this);
				genericItem.setState(historicItem.getState());
				System.out.println("\n CloudPersistenceManager->initialize->serviceName->"+item.getName()+"->:State:->>"+historicItem.getState());
				//genericItem.addStateChangeListener(this);
				logger.debug("Restored item state from '{}' for item '{}' -> '{}'", 
						new Object[] { DateFormat.getDateTimeInstance().format(historicItem.getTimestamp()), 
						item.getName(), historicItem.getState().toString() } );
				return;
			}
		}

//		public void itemRemoved(Item item) {
//			if (item instanceof GenericItem) {
//				GenericItem genericItem = (GenericItem) item;
//				genericItem.removeStateChangeListener(this);
//			}
//		}
		
		/**
		 * Creates and schedules a new quartz-job and trigger with model and rule name as jobData.
		 * 
		 * @param rule the rule to schedule
		 * @param trigger the defined trigger 
		 * 
		 * @throws SchedulerException if there is an internal Scheduler error.
		 */
//		private void createTimers(String modelName) {
//			PersistenceModel persistModel = (PersistenceModel) modelRepository.getModel(modelName + ".persist");
//			if(persistModel!=null) {
//				for(Strategy strategy : persistModel.getStrategies()) {
//					if (strategy instanceof CronStrategy) {
//						CronStrategy cronStrategy = (CronStrategy) strategy;
//						String cronExpression = cronStrategy.getCronExpression();
//						JobKey jobKey = new JobKey(strategy.getName(), modelName);
//						try {
//					        JobDetail job = newJob(PersistItemsJob.class)
//					        	.usingJobData(PersistItemsJob.JOB_DATA_PERSISTMODEL, cronStrategy.eResource().getURI().trimFileExtension().path())
//					        	.usingJobData(PersistItemsJob.JOB_DATA_STRATEGYNAME, cronStrategy.getName())
//					            .withIdentity(jobKey)
//					            .build();
//					
//					        Trigger quartzTrigger = newTrigger()
//						            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
//						            .build();
//				
//					        scheduler.scheduleJob(job, quartzTrigger);
//				
//							logger.debug("Scheduled strategy {} with cron expression {}", new String[] { jobKey.toString(), cronExpression });
//						} catch(SchedulerException e) {
//							logger.error("Failed to schedule job for strategy {} with cron expression {}", new String[] { jobKey.toString(), cronExpression }, e);
//						}
//					}
//				}
//			}
//		}

		/**
		 * Delete all {@link Job}s of the group <code>persistModelName</code>
		 * 
		 * @throws SchedulerException if there is an internal Scheduler error.
		 */
		private void removeTimers(String persistModelName) {
			try {
				Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(persistModelName));
				for(JobKey jobKey : jobKeys) {
					try {
						boolean success = scheduler.deleteJob(jobKey);
						if(success) {
							logger.debug("Removed scheduled cron job for strategy '{}'", jobKey.toString());						
						} else {
							logger.warn("Failed to delete cron jobs '{}'", jobKey.getName());
						}
					} catch (SchedulerException e) {
						logger.warn("Failed to delete cron jobs '{}'", jobKey.getName());
					}
				}
			} catch (SchedulerException e) {
				logger.warn("Failed to delete cron jobs of group '{}'", persistModelName);
			}
		}
			

}
