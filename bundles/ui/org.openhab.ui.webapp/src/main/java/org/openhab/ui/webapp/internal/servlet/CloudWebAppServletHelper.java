package org.openhab.ui.webapp.internal.servlet;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.emf.common.util.EList;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.ItemNotFoundException;
import org.openhab.core.items.ItemRegistry;
import org.openhab.core.items.StateChangeListener;
import org.openhab.core.types.State;
import org.openhab.model.sitemap.Frame;
import org.openhab.model.sitemap.Widget;
import org.openhab.ui.webapp.cloud.exception.CloudException;
import org.openhab.ui.webapp.cloud.session.CloudSessionManager;

import com.openhab.core.event.dto.EventObject;

public class CloudWebAppServletHelper {

	
	/**
	 * This method only returns when a change has occurred to any item on the page to display
	 * 
	 * @param widgets the widgets of the page to observe
	 */
	public static boolean waitForChanges(EList<Widget> widgets,HttpServletRequest	request) throws CloudException {
		ItemRegistry	cloudItemRegistry	=	(ItemRegistry)CloudSessionManager.getAttribute(CloudSessionManager.getSession(request,null), CloudSessionManager.ITEMREGISTRY);
		//System.out.println("\nWebAppServlet->waitForChanges->Adding Listeners Now");
		long startTime = (new Date()).getTime();
		boolean timeout = false;
		BlockingStateChangeListener listener = new BlockingStateChangeListener();
		// let's get all items for these widgets
		Set<GenericItem> items = getAllItems(widgets,cloudItemRegistry);
		//System.out.println("\nWebAppServlet->waitForChanges->items.size()"+items.size());
		for(GenericItem item : items) {	
			//System.out.println("\nWebAppServlet->waitForChanges->"+items.toString()+"->Listener->"+listener.getClass().getName());
			item.addStateChangeListener(listener);
		}
		do {
			timeout = (new Date()).getTime() - startTime > WebAppServlet.TIMEOUT_IN_MS;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				timeout = true;
				break;
			}
		} while(!listener.hasChangeOccurred() && !timeout);
		for(GenericItem item : items) {
			item.removeStateChangeListener(listener);
		}
		return !timeout;
	}

	/**
	 * Collects all items that are represented by a given list of widgets
	 * 
	 * @param widgets the widget list to get the items for
	 * @return all items that are represented by the list of widgets
	 */
	public static Set<GenericItem> getAllItems(EList<Widget> widgets,ItemRegistry cloudItemRegistry) {
		Set<GenericItem> items = new HashSet<GenericItem>();
		if(cloudItemRegistry!=null) {
			for(Widget widget : widgets) {
				String itemName = widget.getItem();
				if(itemName!=null) {
					try {
						Item item = cloudItemRegistry.getItem(itemName);
						if (item instanceof GenericItem) {
							final GenericItem gItem = (GenericItem) item;
							items.add(gItem);
						}
					} catch (ItemNotFoundException e) {
						// ignore
					}
				} else {
					if(widget instanceof Frame) {
						items.addAll(getAllItems(((Frame) widget).getChildren(),cloudItemRegistry));
					}
				}
			}
		}
		return items;
	}


	
	
	private static class BlockingStateChangeListener implements StateChangeListener {
		
		private boolean changed = false;
		
		/**
		 * {@inheritDoc}
		 */
		public void stateChanged(Item item, State oldState, State newState) {
			changed = true;
		}

		/**
		 * determines, whether a state change has occurred since its creation
		 * 
		 * @return true, if a state has changed
		 */
		public boolean hasChangeOccurred() {
			return changed;
		}

		/**
		 * {@inheritDoc}
		 */
		public void stateUpdated(Item item, State state) {
			changed = true;
		}
		
	}
	
}
