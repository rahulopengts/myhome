package org.openhab.ui.webappprofile.internal.xml;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openhab.core.internal.ItemDataHolder;
import org.openhab.core.items.Item;
import org.openhab.ui.webappprofile.internal.common.HubUtility;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLDocumentDomImpl implements XMLDocument {

	DocumentBuilderFactory docFactory = null;
	DocumentBuilder docBuilder = null;
	Document doc = null;
	String profileId	=	null;
	
	public void initDocument() throws Exception{
		try{
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (Exception e){
			throw e;
		}
	}

	public void initEditDocument() throws Exception{
		try{
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			
		} catch (Exception e){
			throw e;
		}
	}
	
	public void createDocument(String profileId,String profileName){
		try {
			 
			this.profileId	=	profileId;
			// root elements
			
			Element rootElement = doc.createElement("profileId");
			doc.appendChild(rootElement);
			
			//rootElement.setTextContent(profileId);
			
			// set attribute to staff element
			Attr attrProfileId = doc.createAttribute("profileId");
			attrProfileId.setValue(profileId);
			rootElement.setAttributeNode(attrProfileId);

			// set attribute to staff element
			Element rootElementProfileName = doc.createElement("profileName");
			rootElement.appendChild(rootElementProfileName);
			rootElementProfileName.setTextContent(profileName);
			
//			Attr attrprofileName = doc.createAttribute("profileName");
//			attrprofileName.setValue(profileName);
//			rootElement.setAttributeNode(attrprofileName);
			
			
				// staff elements					  
				Element switchon = doc.createElement("switchon");
				rootElement.appendChild(switchon);
				
				// set attribute to staff element
				Attr attr = doc.createAttribute("id");
				attr.setValue("1");
				switchon.setAttributeNode(attr);

				// staff elements
				Element switchoff = doc.createElement("switchoff");
				rootElement.appendChild(switchoff);
			
				// set attribute to staff element
				Attr attrswitchoff = doc.createAttribute("id");
				attrswitchoff.setValue("1");
				switchoff.setAttributeNode(attrswitchoff);

				// staff elements
				Element dimmer = doc.createElement("dimmer");
				rootElement.appendChild(dimmer);
			
				// set attribute to staff element
				Attr attrdimmer = doc.createAttribute("id");
				attrdimmer.setValue("1");
				dimmer.setAttributeNode(attrdimmer);
				
				//Element reSwitchOn	=	doc.getElementById("1");					
				NodeList reSwitchOn	=	doc.getElementsByTagName("switchon");
				//doc.getElementsByTagName(tagname)
				System.out.println("\n Element 1: "+switchon);
				System.out.println("\n Element 2: "+reSwitchOn.item(0));
				
				
				
/*			
	 
			// shorten way
			// staff.setAttribute("id", "1");
	 
			// firstname elements
			Element firstname = doc.createElement("firstname");
			firstname.appendChild(doc.createTextNode("yong"));
			staff.appendChild(firstname);
	 
			// lastname elements
			Element lastname = doc.createElement("lastname");
			lastname.appendChild(doc.createTextNode("mook kim"));
			staff.appendChild(lastname);
	 
			// nickname elements
			Element nickname = doc.createElement("nickname");
			nickname.appendChild(doc.createTextNode("mkyong"));
			staff.appendChild(nickname);
	 
			// salary elements
			Element salary = doc.createElement("salary");
			salary.appendChild(doc.createTextNode("100000"));
			staff.appendChild(salary);
	*/ 
			// write the content into xml file
	 
		  } catch (Exception tfe) {
			tfe.printStackTrace();
		  }		
	}
	
	public boolean addChileNode(String strParentNode,String chileNode,String chiledNodeValue){
		NodeList	nodeList	=	doc.getElementsByTagName(strParentNode);
		HubUtility.printDebugMessage(this.toString(), "addChileNode : chileNode       : "+chileNode);
		HubUtility.printDebugMessage(this.toString(), "addChileNode : chiledNodeValue : "+chiledNodeValue);
		HashMap<String, String> profileDataMap	=	null;
		if(nodeList!=null && nodeList.getLength()>0){
			//First check if child node is existing.
			
			Element test1 = doc.createElement(chileNode);
			Node	parentNode	=	nodeList.item(0);
			Element	parentNodeElement	=	(Element)parentNode;
			
			NodeList childElementNode	=	parentNodeElement.getElementsByTagName(chileNode);
			if(childElementNode!=null && childElementNode.getLength()>0){
				HubUtility.printDebugMessage(this.toString(), "Chile is existing");
			}
			
			//isChildNodeExisting(parentNode.getN,chileNode);
			
			parentNodeElement.appendChild(test1);
			test1.setTextContent(chiledNodeValue);
			if(ItemDataHolder.getItemDataHolder().getProfileDataMap()==null){
				profileDataMap	=	new HashMap<String, String>();
				ItemDataHolder.getItemDataHolder().setProfileDataMap(profileDataMap);
			} else {
				profileDataMap	=	ItemDataHolder.getItemDataHolder().getProfileDataMap();
			}
			profileDataMap.put(chileNode, chiledNodeValue+"~"+parentNode);
		} else {
			//NOT IN NODE . ADD THIS NODE
			Element rootElement	=	doc.getDocumentElement();
			Element newParentNode = doc.createElement(strParentNode);
			rootElement.appendChild(newParentNode);
			// set attribute to staff element
			Attr attrswitchoff = doc.createAttribute("id");
			attrswitchoff.setValue("1");
			newParentNode.setAttributeNode(attrswitchoff);
			Element childNode = doc.createElement(chileNode);
			newParentNode.appendChild(childNode);
			childNode.setTextContent(chiledNodeValue);
			//
			if(ItemDataHolder.getItemDataHolder().getProfileDataMap()==null){
				profileDataMap	=	new HashMap<String, String>();
				ItemDataHolder.getItemDataHolder().setProfileDataMap(profileDataMap);
			} else {
				profileDataMap	=	ItemDataHolder.getItemDataHolder().getProfileDataMap();
			}
			profileDataMap.put(chileNode, chiledNodeValue+"~"+strParentNode);
		}
		ItemDataHolder.getItemDataHolder().setProfileDataMap(profileDataMap);
		return true;
		
	}
	
	private boolean isChildNodeExisting(NodeList nodeList,String chileNodeName){
		if(nodeList!=null){
			for(int nodeIndex	=	0	;nodeIndex<nodeList.getLength();nodeIndex++){
				Node childNode	=	nodeList.item(nodeIndex);
				
				HubUtility.printDebugMessage(this.toString(), "Checking Existing Node for "+"chileNodeName with" + childNode.getNodeName());
				if(childNode.getNodeName().equals(chileNodeName)){
					return true;
				}
			}
		}
		
		return false;
	}
	public static File[] getProfileList(){
		
		String dirLocation	=	System.getenv("ECLIPSEHOME")+File.separator+HubUtility.PROFILEDIR;//+File.separator+profileId+".xml";
		
		File folder = new File(dirLocation);
		File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        System.out.println("File " + listOfFiles[i].getName());
	        
	      } else if (listOfFiles[i].isDirectory()) {
	        System.out.println("Directory " + listOfFiles[i].getName());
	      }
	    }
	    
	    return listOfFiles;
	}
	
	public void writeToFile(){
		try{
			
			String ECLIPSEHOME	=	System.getenv("ECLIPSEHOME");
			System.out.println("\n ECLIPSEHOME : "+ECLIPSEHOME);
			
			String dirLocation	=	ECLIPSEHOME+File.separator+HubUtility.PROFILEDIR;//+File.separator+profileId+".xml";
			
			File	profileDir	=	new File(dirLocation);
			boolean isExisting	=	profileDir.isDirectory();
			if(!isExisting){
				profileDir.mkdir();
			}
			
			String fileLoaction	=	dirLocation+File.separator+profileId+".xml";
			
			
			//File	profileDir	=	
			System.out.println("\n ProfilePath : "+fileLoaction);
		
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		//StreamResult result = new StreamResult(new File("D:\\file.xml"));
		StreamResult result = new StreamResult(new File(fileLoaction));
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
		} catch (TransformerException e){
			e.printStackTrace();
		}
	
	}
	
	public void updateDocumentObject(String nodeId,String nodeBinding,String nodeState, String nodeType,Item item){

//		AdminEvent-0 : >[mosquitto:/raspberry:command:ON:OL1N0L2N1S1100000000
//		AdminEvent-1 : >[mosquitto:/raspberry:command:OFF:OL1N0L2N1S1000000000
//		AdminEvent Command:  : ON
		String bindingState	=	getBindingConfig(nodeBinding,nodeState);
		//Node02 (Type=SwitchItem, State=OFF)
		String itemDetails	=	item.toString();
		String itemType	=	itemDetails.substring(itemDetails.indexOf('=')+1, itemDetails.indexOf(','));
		HubUtility.printDebugMessage(this.toString(), "Type is : "+itemType);
		HubUtility.printDebugMessage(this.toString(), "Binding State : "+bindingState);
		
		if(bindingState!=null){
			addChileNode(itemType, nodeId,bindingState);
			//writeToFile();
		}
		
		
	}
	
	private String getBindingConfig(String nodeBinding,String state){
		String[] configurationStrings = nodeBinding.split("],");
		int tokenLength	=	configurationStrings.length;
		for(int tokenCount=0;tokenCount<tokenLength;tokenCount++){
			if(configurationStrings[tokenCount].contains(state)){
				return configurationStrings[tokenCount];
			} 
		}
		return null;
	}
	

	  public void readAndUpdateProfileDataIntoMemory(HttpServletRequest req,String fileName) {
		  HashMap<String, String> profileDataMap	=	null;
		  boolean isSuccess	=	false;
		  try {
	    	
			String fileLocation	=	System.getenv("ECLIPSEHOME")+File.separator+HubUtility.PROFILEDIR+File.separator+fileName+".xml";
			
			File fXmlFile = new File(fileLocation);
			
//			DocumentBuilderFactory docFactory = null;
//			DocumentBuilder docBuilder = null;
//			Document doc = null;
			
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = docBuilder.parse(fXmlFile);
	 
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();		 
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nList = doc.getElementsByTagName("profileId");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println("Profile id : " + eElement.getAttribute("profileId"));
					NodeList	profileNodeChild	=	eElement.getChildNodes();
					for(int nodeIndex=0;nodeIndex<profileNodeChild.getLength();nodeIndex++){
						Node profileIdChildNode	=	profileNodeChild.item(nodeIndex);
						if (profileIdChildNode.getNodeType() == Node.ELEMENT_NODE) {
							Element profileIdChildElement = (Element) profileIdChildNode;
							profileDataMap	=	processProfileDataMap(profileIdChildElement);
							ItemDataHolder.getItemDataHolder().setProfileDataMap(profileDataMap);
							isSuccess	=	true;
							System.out.println("\nCurrent Profile Element :" + profileIdChildElement.getNodeName());
							//This will have all child of profileId such as ProfileName, SwitchOn,SwitchOff,SwitchItem etc.
							
						}
					}
				}
			}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
		if(isSuccess){
			//HubUtility.cleanHttpSessionForNewProfile(req);
		}
	  }
	 
	  private static HashMap<String, String> processProfileDataMap(Element rootElement){
		  	HashMap	profileDataMap	=	new HashMap<String, String>();
			NodeList	profileNodeChild	=	rootElement.getChildNodes();
			String nodeTypeName	=	rootElement.getNodeName();
			
			for(int nodeIndex=0;nodeIndex<profileNodeChild.getLength();nodeIndex++){
				Node profileIdChildNode	=	profileNodeChild.item(nodeIndex);
				
				profileDataMap.put(profileIdChildNode.getNodeName(), profileIdChildNode.getTextContent()+"~"+nodeTypeName);
				
				HubUtility.printDebugMessage("XMLDocumentDomImpl", " Node Name "+profileIdChildNode.getNodeName());
				HubUtility.printDebugMessage("XMLDocumentDomImpl", " Node Value"+profileIdChildNode.getTextContent()+"~"+nodeTypeName);
			}		  
		  
		  return profileDataMap;
	  }
		public static void main(String str[]){
			try{
				/*
				File fileDir	=	new File("D:\\rahul1");
				
				boolean is	=	fileDir.isDirectory();
				if(!is){
					fileDir.mkdir();
				}
				System.out.println("\n Output : "+is);
				*/
				
				
				XMLDocumentDomImpl x	=	new XMLDocumentDomImpl();
				x.initEditDocument();
				x.readAndUpdateProfileDataIntoMemory(null,"eve");
				/*
				x.initDocument();
				x.createDocument("morning","morning");
				//x.addChileNode("SwitchTTT","Node01"+"~"+">[mosquitto:/raspberry:command:ON:OL1N0L2N1S1100000000");
				x.addChileNode("SwitchTTT","Node01",">[mosquitto:/raspberry:command:ON:OL1N0L2N1S1100000000");
				
				x.writeToFile();
				*/
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	
	
}
