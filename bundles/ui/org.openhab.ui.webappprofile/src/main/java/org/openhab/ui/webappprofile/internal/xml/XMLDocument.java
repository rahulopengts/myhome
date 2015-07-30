package org.openhab.ui.webappprofile.internal.xml;

import org.openhab.core.items.Item;

public interface XMLDocument {

	public boolean addChileNode(String parentNode,String childNode,String chiledNodeValue);	
	public void createDocument(String profileId,String profileName);
	public void initDocument() throws Exception;
	public void updateDocumentObject(String nodeId,String nodeBinding,String nodeState, String nodeType,Item item);
	public void writeToFile();
	
}
