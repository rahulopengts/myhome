package com.openhab.core.event.handler;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openhab.binding.mqtt.internal.MqttGenericBindingProvider;
import org.openhab.binding.mqtt.internal.MqttItemConfig;
import org.openhab.binding.mqtt.internal.MqttMessagePublisher;
import org.openhab.core.internal.items.ItemRegistryImpl;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemProvider;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.persistence.internal.PersistenceManager;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.model.core.ModelRepository;
import org.openhab.model.item.binding.BindingConfigReader;
import org.openhab.model.item.internal.GenericItemProvider;
import org.openhab.model.rule.internal.engine.RuleEngine;

import com.openhab.core.constants.CloudAppConstants;
import com.openhab.core.event.admin.AdminEventImpl;
import com.openhab.core.event.dto.DBEventObject;
import com.openhab.core.event.dto.EventObject;
import com.openhab.core.event.dto.UIEventObject;
import com.openhab.core.threadstore.CloudThreadLocalStorage;

public class EventManager	{// implements IEventManager{

	
	private PersistenceManager	cloudPersistenceManager	=	null;
	
	private RuleEngine	cloudRuleEngine	=	null;
	
	public void handleEvent(String itemName, Command command,ItemRegistry	itemRegistry) {
		// TODO Auto-generated method stub
	}
	
	public void persistData(String itemName, Command command,ItemRegistry	itemRegistry){
		
	}
	
	public void publishData(String itemName, Command command,ItemRegistry itemRegistry,ModelRepository modelRepository,PersistenceManager persistenceManager,RuleEngine ruleEngine,EventObject eventObject){
	
		EventObject	eventObject1	=	CloudThreadLocalStorage.getLocalEventObject();
		System.out.println("\nCmdCloudEventPunblisherImpl->eventObject->"+eventObject1);
		System.out.println("\nCmdCloudEventPunblisherImpl->HomeID->"+Thread.currentThread().getId()+":Local:"+CloudThreadLocalStorage.getLocalHomeId());
		
		
		cloudRuleEngine=	ruleEngine;
		cloudPersistenceManager=	persistenceManager;
		
		Map<ItemProvider, Collection<Item>> itemMaps	=	((ItemRegistryImpl)itemRegistry).getItemMap();
		//ItemRegistry has the map of providers.
		if(itemMaps!=null && itemMaps.size()==1){
			System.out.println("\n PublishData->");
			Set<ItemProvider> genericItemKey	=	itemMaps.keySet();
			Iterator<ItemProvider> iterate	=	genericItemKey.iterator();
			
			//Get the GenericItemProvider
			GenericItemProvider	genericItemProvider	=	(GenericItemProvider)iterate.next();
			Collection<Item> items = itemMaps.get(genericItemProvider);
			if(items!=null){
				System.out.println("\nEventManager->publishData->items->"+items.size());
			}
			//Map<String, BindingConfigReader> bindingConfigReaders = new HashMap<String, BindingConfigReader>();
			
			//Every GenericItemProvider has MqttGenericBindingProvider for CloudChange
			Map<String, BindingConfigReader> bindingConfigReaders = genericItemProvider.getBindingConfigReaders();
			Set<String> mqttGenericBindingProviderKey	=	bindingConfigReaders.keySet();
			Iterator<String> iteratebindingProvider	=	mqttGenericBindingProviderKey.iterator();
			String bindingProvider	=	iteratebindingProvider.next();
			MqttGenericBindingProvider	mqttGenericBindingProvider	=	 (MqttGenericBindingProvider)bindingConfigReaders.get(bindingProvider);
			
			Map<String, MqttItemConfig> itemConfigMap	=	mqttGenericBindingProvider.getMqttItemConfigList();
			MqttItemConfig	itemConfig	=	itemConfigMap.get(itemName);
			java.util.List<MqttMessagePublisher> pubList	=	itemConfig.getMessagePublishers();
			
			String uiCommand	=	command.toString();
			int commandMessageIndex	=	0;
			
			if(CloudAppConstants.ON_COMMAND.equals(uiCommand)){
				commandMessageIndex	=	0;
			} else if(CloudAppConstants.OFF_COMMAND.equals(uiCommand)){
				commandMessageIndex	=	1;
			}

			MqttMessagePublisher	publisher	=	pubList.get(0);
			MqttMessagePublisher	p1=	(MqttMessagePublisher)pubList.get(0);
			//CloudChange->As per configuration in .items, mqttmessagepublisher at index 0 is for OUTBOUND OFF COMMAND			
			MqttMessagePublisher	p2=	(MqttMessagePublisher)pubList.get(1);
			MqttMessagePublisher	messageToBePublished	=	(MqttMessagePublisher)pubList.get(commandMessageIndex);
			
			MqttMessagePublisher	publisherTopic	=	pubList.get(commandMessageIndex);
			
			
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:0:"+pubList.get(0)+":1:"+pubList.get(1));
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Size:"+pubList.size());
			System.out.println("\nEventManager->publishData->MqttMessagePublisher-0->"+p1.getTransformationRule()+"->p2->"+p2.getTransformationRule()+"->MessageType->"+p1.getMessageType());
			
			//CoudChange Outbound OFF Command
			eventObject.setMqttMessageToBePublished(messageToBePublished);
			
			dispatchEvent(itemRegistry,messageToBePublished.getTransformationRule(), publisherTopic.getTopic(),command,itemName,modelRepository,itemConfig,eventObject);
			
			//MqttGenericBindingProvider->processBindingConfiguration->
			//context: demo-7-Aug-15.items:->
			//item:->Node04:->
			//bindingConfig:>[mosquitto:/raspberry:command:ON:OL1N4L9N9S1100000000],>[mosquitto:/raspberry:command:OFF:OL1N4L9N9S1000000000],<[mosquitto:/L1N4L9N9S1:state:default]			
		}
	}

	public void publishDataOutNOTREQUIRED(String itemName, Command command,ItemRegistry itemRegistry,ModelRepository modelRepository,EventObject eventObject){
		
		Map<ItemProvider, Collection<Item>> itemMaps	=	((ItemRegistryImpl)itemRegistry).getItemMap();
		//ItemRegistry has the map of providers.
		if(itemMaps!=null && itemMaps.size()==1){
			Set<ItemProvider> genericItemKey	=	itemMaps.keySet();
			Iterator<ItemProvider> iterate	=	genericItemKey.iterator();
			
			//Get the GenericItemProvider
			GenericItemProvider	genericItemProvider	=	(GenericItemProvider)iterate.next();
			Collection<Item> items = itemMaps.get(genericItemProvider);
			if(items!=null){
				System.out.println("\nEventManager->publishData->items->"+items.size());
			}
			//Map<String, BindingConfigReader> bindingConfigReaders = new HashMap<String, BindingConfigReader>();
			
			//Every GenericItemProvider has MqttGenericBindingProvider for CloudChange
			Map<String, BindingConfigReader> bindingConfigReaders = genericItemProvider.getBindingConfigReaders();
			Set<String> mqttGenericBindingProviderKey	=	bindingConfigReaders.keySet();
			Iterator<String> iteratebindingProvider	=	mqttGenericBindingProviderKey.iterator();
			String bindingProvider	=	iteratebindingProvider.next();
			MqttGenericBindingProvider	mqttGenericBindingProvider	=	 (MqttGenericBindingProvider)bindingConfigReaders.get(bindingProvider);
			System.out.println("\nEventManager->publishData->MqttGenericBindingProvider->"+mqttGenericBindingProvider);
			
			Map<String, MqttItemConfig> itemConfigMap	=	mqttGenericBindingProvider.getMqttItemConfigList();
			MqttItemConfig	itemConfig	=	itemConfigMap.get(itemName);
			java.util.List<MqttMessagePublisher> pubList	=	itemConfig.getMessagePublishers();
			
			//CloudChange->As per configuration in .items, mqttmessagepublisher at index 0 is for OUTBOUND ON COMMAND
//			List<MqttMessagePublisher> mqttMesasgeList	=	itemConfig.getPublishConfigurations();
//			if(mqttMesasgeList!=null){
//				Iterator<MqttMessagePublisher> iterateMqttMessageList	=	mqttMesasgeList.iterator();
//				while(iterateMqttMessageList.hasNext()){
//					String content	=	(String)iterateMqttMessageList.next();
//				}
//				
//			}
			
			String uiCommand	=	command.toString();
			int commandMessageIndex	=	0;
			
			if(CloudAppConstants.ON_COMMAND.equals(uiCommand)){
				commandMessageIndex	=	0;
			} else if(CloudAppConstants.OFF_COMMAND.equals(uiCommand)){
				commandMessageIndex	=	1;
			}

			MqttMessagePublisher	publisher	=	pubList.get(0);
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:0:"+pubList.get(0)+":1:"+pubList.get(1));
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Size:"+pubList.size());
			
			MqttMessagePublisher	p1=	(MqttMessagePublisher)pubList.get(0);
			//CloudChange->As per configuration in .items, mqttmessagepublisher at index 0 is for OUTBOUND OFF COMMAND			
			MqttMessagePublisher	p2=	(MqttMessagePublisher)pubList.get(1);
			
			MqttMessagePublisher	messageToBePublished	=	(MqttMessagePublisher)pubList.get(commandMessageIndex);
			
			MqttMessagePublisher	publisherTopic	=	pubList.get(commandMessageIndex);
			
			//CoudChange Outbound ON Command
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Publish:"+messageToBePublished.getTransformationRule());
			
			//CoudChange Outbound OFF Command
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Size:P2:"+p2.getTransformationRule());
			System.out.println("\nEventManager->publishData->MqttMessagePublisher->broker->"+publisher.getBroker()+"->:topic:"+publisher.getTopic()+":Command:"+command.toString());
			
			dispatchEvent(itemRegistry,messageToBePublished.getTransformationRule(), publisherTopic.getTopic(),command, itemName,modelRepository,itemConfig,eventObject);
			
			//MqttGenericBindingProvider->processBindingConfiguration->
			//context: demo-7-Aug-15.items:->
			//item:->Node04:->
			//bindingConfig:>[mosquitto:/raspberry:command:ON:OL1N4L9N9S1100000000],>[mosquitto:/raspberry:command:OFF:OL1N4L9N9S1000000000],<[mosquitto:/L1N4L9N9S1:state:default]			
		}
	}

	private void dispatchEvent(ItemRegistry itemRegistry,String messageContent,String topicName,Command command,String itemName,ModelRepository modelRepository,MqttItemConfig itemConfig,EventObject p_EventObject){
		
		AdminEventImpl	admin	=	new AdminEventImpl();
		EventObject	eventObject	=	new UIEventObject();
		eventObject.setModelRepository(p_EventObject.getModelRepository());
		eventObject.setItemRegistry(p_EventObject.getItemRegistry());
		eventObject.setPersistanceManager(p_EventObject.getPersistanceManager());
		eventObject.setRuleEngine(p_EventObject.getRuleEngine());
		
		eventObject.setMessageContent(messageContent);
		
		eventObject.setTopicName(topicName);
		IEventHandler	eventHandler	=	new UIEventMessageHandler();
		admin.dispatchEvent(eventObject, eventHandler);

		
		EventObject	dbObject	=	new DBEventObject();
		dbObject.setCommand(command);
		dbObject.setItemName(itemName);
		State s	=	(State)command;
		dbObject.setNewState(s);
		dbObject.setModelRepository(p_EventObject.getModelRepository());
		dbObject.setItemRegistry(p_EventObject.getItemRegistry());
		dbObject.setPersistanceManager(p_EventObject.getPersistanceManager());
		dbObject.setRuleEngine(p_EventObject.getRuleEngine());

		dbObject.setMessageContent(messageContent);
		dbObject.setTopicName(topicName);
		IEventHandler	dataEventHandler	=	new DataStoreHandler();
		admin.dispatchEvent(dbObject, dataEventHandler);
		
		
		
//	try{
//		System.out.println("n dispatch-1");
//		AsyncEventBus asyncEventBus	=	new AsyncEventBus(Executors.newCachedThreadPool());
//		asyncEventBus.register(new UIEventMessageHandler());
//		asyncEventBus.post(new EventObject());
//		System.out.println("n dispatch-2");
//	} catch (Throwable e){
//		e.printStackTrace();
//	}
	}
}
