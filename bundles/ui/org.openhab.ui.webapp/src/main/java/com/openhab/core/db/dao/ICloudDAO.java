package com.openhab.core.db.dao;

import org.openhab.core.items.Item;
import org.openhab.core.items.ItemRegistry;
import org.openhab.model.core.ModelRepository;

public interface ICloudDAO {

	public void initialzeItem(ModelRepository model,ItemRegistry itemRegistry);
	
	public void storeItem(ModelRepository model,ItemRegistry itemRegistry,Item item);
}
