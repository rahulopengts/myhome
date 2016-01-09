package com.openhab.core.internal.event.processor.ext;

import org.openhab.core.autoupdate.internal.AutoUpdateBinding;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.items.ItemRegistry;
import org.openhab.model.core.ModelRepository;

public class ExtAutoUpdateBinding extends AutoUpdateBinding {

	private ItemRegistry	cloudItemRegistry		=	null;
	private ModelRepository	cloudModelRepository	=	null;
	public ItemRegistry getCloudItemRegistry() {
		return cloudItemRegistry;
	}
	public void setCloudItemRegistry(ItemRegistry cloudItemRegistry) {
		this.cloudItemRegistry = cloudItemRegistry;
		super.itemRegistry	=	cloudItemRegistry;
	}
	public ModelRepository getCloudModelRepository() {
		return cloudModelRepository;
	}
	public void setCloudModelRepository(ModelRepository cloudModelRepository) {
		this.cloudModelRepository = cloudModelRepository;
	}
	
}
