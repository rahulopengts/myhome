package com.openhab.core.event.handler;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;


import org.openhab.binding.mqtt.internal.MqttGenericBindingProvider;
import org.openhab.binding.mqtt.internal.MqttItemConfig;
import org.openhab.binding.mqtt.internal.MqttMessagePublisher;
import org.openhab.core.internal.items.ItemRegistryImpl;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemProvider;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.types.Command;
import org.openhab.model.item.binding.BindingConfigReader;
import org.openhab.model.item.internal.GenericItemProvider;

import com.google.common.eventbus.AsyncEventBus;
import com.openhab.core.event.admin.AdminEventImpl;
import com.openhab.core.event.admin.AdminEventManager;
import com.openhab.core.event.dto.EventObject;

public class EventManager	{// implements IEventManager{

	
	public void handleEvent(String itemName, Command command,ItemRegistry	itemRegistry) {
		// TODO Auto-generated method stub
	}
	
	public void persistData(String itemName, Command command,ItemRegistry	itemRegistry){
		
	}
	
	public void publishData(String itemName, Command command,ItemRegistry	itemRegistry){
		
		Map<ItemProvider, Collection<Item>> itemMaps	=	((ItemRegistryImpl)itemRegistry).getItemMap();
		if(itemMaps!=null && itemMaps.size()==1){
			Set<ItemProvider> genericItemKey	=	itemMaps.keySet();
			Iterator<ItemProvider> iterate	=	genericItemKey.iterator();
			GenericItemProvider	genericItemProvider	=	(GenericItemProvider)iterate.next();
			Collection<Item> items = itemMaps.get(genericItemProvider);
			if(items!=null){
				System.out.println("\nEventManager->publishData->items->"+items.size());
			}
			//Map<String, BindingConfigReader> bindingConfigReaders = new HashMap<String, BindingConfigReader>();
			Map<String, BindingConfigReader> bindingConfigReaders = genericItemProvider.getBindingConfigReaders();
			Set<String> mqttGenericBindingProviderKey	=	bindingConfigReaders.keySet();
			Iterator<String> iteratebindingProvider	=	mqttGenericBindingProviderKey.iterator();
			String bindingProvider	=	iteratebindingProvider.next();
			MqttGenericBindingProvider	mqttGenericBindingProvider	=	 (MqttGenericBindingProvider)bindingConfigReaders.get(bindingProvider);
			System.out.println("\nEventManager->publishData->MqttGenericBindingProvider->"+mqttGenericBindingProvider);
			
			Map<String, MqttItemConfig> itemConfigMap	=	mqttGenericBindingProvider.getMqttItemConfigList();
			MqttItemConfig	itemConfig	=	itemConfigMap.get(itemName);
			java.util.List<MqttMessagePublisher> pubList	=	itemConfig.getMessagePublishers();
			
			MqttMessagePublisher	publisher	=	pubList.get(0);
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:0:"+pubList.get(0)+":1:"+pubList.get(1));
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Size:"+pubList.size());
			MqttMessagePublisher	p1=	(MqttMessagePublisher)pubList.get(0);
			MqttMessagePublisher	p2=	(MqttMessagePublisher)pubList.get(1);
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Size:P1:"+p1.getTransformationRule());
			System.out.println("\nEventManager->publishData->MqttMessagePublisherList->:Size:P2:"+p2.getTransformationRule());
//			List<MqttMessagePublisher> mqttMesasgeList	=	itemConfig.getPublishConfigurations();
//			if(mqttMesasgeList!=null){
//				Iterator<MqttMessagePublisher> iterateMqttMessageList	=	mqttMesasgeList.iterator();
//				while(iterateMqttMessageList.hasNext()){
//					String content	=	(String)iterateMqttMessageList.next();
//				}
//				
//			}
			System.out.println("\nEventManager->publishData->MqttMessagePublisher->broker->"+publisher.getBroker()+"->:topic:"+publisher.getTopic()+":Command:"+command.toString());
			
			dispatchEvent(itemRegistry );
			
			//MqttGenericBindingProvider->processBindingConfiguration->
			//context: demo-7-Aug-15.items:->
			//item:->Node04:->
			//bindingConfig:>[mosquitto:/raspberry:command:ON:OL1N4L9N9S1100000000],>[mosquitto:/raspberry:command:OFF:OL1N4L9N9S1000000000],<[mosquitto:/L1N4L9N9S1:state:default]			
		}
	}
	
	private void dispatchEvent(ItemRegistry itemRegistry){
		
		AdminEventImpl	admin	=	new AdminEventImpl();
		EventObject	eventObject	=	new EventObject();
		eventObject.setItemRegistry(itemRegistry);
		IEventHandler	eventHandler	=	new DataStoreHandler();
		admin.dispatchEvent(eventObject, eventHandler);
	}
}
