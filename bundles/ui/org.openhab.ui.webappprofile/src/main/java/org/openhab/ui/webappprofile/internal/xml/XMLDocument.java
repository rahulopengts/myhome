package org.openhab.ui.webappprofile.internal.xml;

public interface XMLDocument {

	public boolean addChileNode(String nodeName);	
	public void createDocument(String profileId,String profileName);
	public void initDocument() throws Exception;

}
