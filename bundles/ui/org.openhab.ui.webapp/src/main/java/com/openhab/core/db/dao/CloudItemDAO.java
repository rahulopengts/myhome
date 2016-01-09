package com.openhab.core.db.dao;

import java.util.Collection;

import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.model.core.ModelRepository;

import com.openhab.core.db.CloudPersistenceManager;

public class CloudItemDAO extends CloudAbstractDAO {

	public void initialzeItem(ModelRepository model,ItemRegistry itemRegistry){
		CloudPersistenceManager	cloudPerisCloudPersistenceManager	=	new CloudPersistenceManager();
		Collection<Item> itemMap	=	itemRegistry.getItems();
		CloudPersistenceManager	manager	=	new CloudPersistenceManager();
		manager.initializeItems(itemRegistry);
	}

	@Override
	public void storeItem(ModelRepository model, ItemRegistry itemRegistry,Item item) {
		// TODO Auto-generated method stub
		CloudPersistenceManager	cloudPerPersistenceManager	=	new CloudPersistenceManager();
		Collection<Item> itemMap	=	itemRegistry.getItems();
		CloudPersistenceManager	manager	=	new CloudPersistenceManager();
		System.out.println("\nCloudItemDAO->storeItem->"+item.getState());
		//manager.initializeItems(itemRegistry);
	}
	
}
