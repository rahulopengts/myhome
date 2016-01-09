package com.openhab.core.internal.event.processor;

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
import org.openhab.core.types.Command;
import org.openhab.model.core.ModelRepository;
import org.openhab.model.item.binding.BindingConfigReader;
import org.openhab.model.item.internal.GenericItemProvider;

import com.openhab.core.constants.CloudAppConstants;

public class CloudMessageProcHelper {

	public static MqttGenericBindingProvider getMqttGenericBindingProvider(String itemName, Command command,ItemRegistry itemRegistry,ModelRepository modelRepository){
		MqttItemConfig	itemConfig	=	null;
		MqttGenericBindingProvider	mqttGenericBindingProvider	=	 null;
		
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
			mqttGenericBindingProvider	=	 (MqttGenericBindingProvider)bindingConfigReaders.get(bindingProvider);
			
			Map<String, MqttItemConfig> itemConfigMap	=	mqttGenericBindingProvider.getMqttItemConfigList();
			itemConfig	=	itemConfigMap.get(itemName);
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
		}
		
		return mqttGenericBindingProvider;
	}

	
}
