package org.openhab.ui.webappprofile.internal.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLDocumentDomImpl implements XMLDocument {

	DocumentBuilderFactory docFactory = null;
	DocumentBuilder docBuilder = null;
	Document doc = null;

	public void initDocument() throws Exception{
		try{
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (Exception e){
			throw e;
		}
	}
	
	public void createDocument(String profileId,String profileName){
		try {
			 
	 
			// root elements
			
			Element rootElement = doc.createElement("profileId");
			doc.appendChild(rootElement);

			// set attribute to staff element
			Attr attrProfileId = doc.createAttribute("profileId");
			attrProfileId.setValue(profileId);
			rootElement.setAttributeNode(attrProfileId);

			// set attribute to staff element
			Attr attrprofileName = doc.createAttribute("profileName");
			attrprofileName.setValue(profileName);
			rootElement.setAttributeNode(attrprofileName);
			
			
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
	
	public boolean addChileNode(String nodeName){
		NodeList	nodeList	=	doc.getElementsByTagName(nodeName);
		if(nodeList!=null){
			Element test1 = doc.createElement("test1");
			
			Element	node	=	(Element)nodeList.item(0);
			
			node.appendChild(test1);
			
		}
		return true;
		
	}
	
	public void writeToFile(){
		try{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File("D:\\file.xml"));
 
		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
		} catch (TransformerException e){
			e.printStackTrace();
		}
	}
	public static void main(String str[]){
		try{
		XMLDocumentDomImpl x	=	new XMLDocumentDomImpl();
		x.initDocument();
		x.createDocument("morning","morning");
		x.addChileNode("switchon");
		x.writeToFile();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
