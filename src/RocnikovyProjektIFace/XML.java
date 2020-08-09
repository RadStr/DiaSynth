package RocnikovyProjektIFace;

import RocnikovyProjektIFace.SpecialSwingClasses.ErrorFrame;
import Rocnikovy_Projekt.MyLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class XML {
	static Document xmlDoc;
	
	public static Document getXMLDoc() {
		return xmlDoc;
	}
	// TODO: Predavat si ten callingFrame neni vubec idealni ... spravne by se mela vyhodit exception a ta se osetri v tom framu
	// Kde uz ten frame vime
	public static void setXMLDoc(String xmlFilename, JFrame callingFrame, String tagName) {
		xmlDoc = loadXMLToDoc(xmlFilename, callingFrame, tagName);
	}
	
	   // TODO: https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
	   public static void createXMLFile(String filename, Node node, JFrame callingFrame) {
	       // write the content into xml file
	       TransformerFactory transformerFactory = TransformerFactory.newInstance();
	       Transformer transformer = null;
	       try {
	    	   transformer = transformerFactory.newTransformer();
	       } catch (TransformerConfigurationException e) {
	    	   new ErrorFrame(callingFrame, "Unknown error in createXMLFile"); // TODO:
	    	   e.printStackTrace();
	       }
	       DOMSource source = new DOMSource(node);
	       StreamResult result = new StreamResult(new File(filename));
	       try {
	    	   transformer.transform(source, result);
	       } catch (TransformerException e) {
	    	   new ErrorFrame(callingFrame, "Unknown error in createXMLFile"); // TODO:
	    	   e.printStackTrace();
	       }
	   	}
	   
	   
	   // TODO: ani neni potreba
	   public static Node findNodeXML(NodeList list, String name) {
		   int len = list.getLength();
		   for(int i = 0; i < len; i++) {
			   Node node = list.item(i);
			   if (node.getNodeName().equals(name)) {			// TODO: Nevim jestli to najde to co ma
				   return node;
			   }
	 	   }
		   
		   return null;
	   }
	   
	   
	   public static int findNodeWithValue(NodeList nList, String val) {
		   int len = nList.getLength();
		   for(int i = 0; i < len; i++) {
			   Node n = nList.item(i);
			   System.out.println("Text content:\t" + n.getTextContent());
			   if(n.getTextContent().equals(val)) {
				   return i;
			   }
		   }
		   
		   return -1;
	   }


    /**
     * Loads xml file with filename to document, which is then returned. If the file doesn't exist then it is created
     * with the same name and first tag is of name tagName.
     * @param filename is the name of xml file.
     * @param callingFrame is the frame which is calling this method.
     * @param tagName is the tag which will be used if the file doesn't exists
     * @return Returns file loaded to Document.
     */
	   public static Document loadXMLToDoc(String filename, JFrame callingFrame, String tagName) {
	       File file = new File(filename);
	       if(!file.exists()) {
				try {
					file.createNewFile();
					PrintWriter writer = new PrintWriter(file);
					writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator() +
                        "<" + tagName + ">" +  System.lineSeparator() + "</" + tagName + ">");
					writer.close();
				} catch (IOException e1) {
					new ErrorFrame(callingFrame, "Error when creating file, because given file didn't exist");
					e1.printStackTrace();
				}
	       }
	       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	       DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			new ErrorFrame(callingFrame, "Unknown error in loadXMLToDoc"); // TODO:
			e1.printStackTrace();
		}
	       Document document = null;
		try {
			document = dBuilder.parse(file);
		} catch (SAXException e) {
			new ErrorFrame(callingFrame, "SAX error");		// TODO: Jeste dat minuly frame
			e.printStackTrace();
		} catch (IOException e) {
			new ErrorFrame(callingFrame, "IO error in loadXMLToDoc");		// TODO:
			e.printStackTrace();
		}
		
	       document.getDocumentElement().normalize();  
	       return document;
	   }
	   
	   
	   public static void addAnalyzedFileToXML(Document xmlDoc, List<Pair<String, String>> list, String mainTag, String underMainTag) {
	/*	// First argument should be name (for speed reason don't check)
		   if(map[0].getKey() != "name") {		
			   return;
		   }
	*/	   
		   ListIterator<Pair<String, String>> iter = list.listIterator();
		   if(iter.hasNext()) {
			   Pair<String, String> val;// TODO: // = iter.next();
			   Node root = xmlDoc.getElementsByTagName(mainTag).item(0);
			   Element songElem = null;	 
			   Element elem;
			   if (root.getNodeType() == Node.ELEMENT_NODE) {
				   songElem = xmlDoc.createElement(underMainTag);
				   Element roote = (Element) root;
				   roote.appendChild(songElem);
				   
				   // TODO: Zkousim xml formatovani
/*			         Attr attr = xmlDoc.createAttribute("zkouska");
			         attr.setValue("zkouska_Value");
			         songElem.setAttributeNode(attr);
*/				   // TODO:
				   
				   // TODO:
			       //elem = xmlDoc.createElement(val.getKey());
			       //elem.appendChild(xmlDoc.createTextNode(val.getValue()));
			       //songElem.appendChild(elem);
			       // TODO:   
/*				   
				   //xmlDoc.createTextNode(val.getKey());
				   elem = xmlDoc.createElement(val.getKey());
//				   elem.setTextContent(val.getValue());				   
				   elem.setNodeValue(val.getValue());
				   roote.appendChild(elem);
				   System.out.println(val.getKey());
				   System.out.println(val.getValue());
*/				   
				   
			   }
			   else {
				   return;
			   }			   
		   
			  
			   while(iter.hasNext()) {
				   val = iter.next();
				   System.out.println(val.getKey());
                   System.out.println(val.getValue());

                   try {
                       elem = xmlDoc.createElement(val.getKey());
                   }
                    catch(Exception e) {
						MyLogger.logWithoutIndentation("Invalid name in xml tree");
                        MyLogger.logException(e);
                        continue;
                    }

				   elem.appendChild(xmlDoc.createTextNode(val.getValue()));
				   songElem.appendChild(elem);
				  
				   // Attr attr = xmlDoc.createAttribute(val.getKey());
				  // attr.setValue(val.getValue());
				  // elem.setAttributeNode(attr);
				  // elem.appendChild(xmlDoc.createTextNode(attr.getValue()));
				   System.out.println(val.getKey() + "\t" + val.getValue());
// TODO:				   e.setAttribute(val.getKey(), val.getValue());		   
			   }
		
	// TODO: Musim to delat takhle kvuli problemum s genericnosti v jave	   
	/*	   
		   Element e = xmlDoc.createElement(list.get(0).getKey());
		   e.setTextContent(list.get(0).getValue());
		   
		   for(int i = 1; i < list.size(); i++) {
			   e.setAttribute(map[i].getKey(), map[i].getValue());
		   }
	*/	
		   }
	   }
	   
	   
	   
	   public static void deleteSongsFromDoc(Node[] nodes, Document xmlDoc) {
		   for(int i = 0; i < nodes.length; i++) {
			   xmlDoc.removeChild(nodes[i]);
		   }
	   }
	   
	   
	   public static List<Pair<String, Node>> getPairs(Document xmlDoc) {
		   NodeList nList = xmlDoc.getElementsByTagName("song");
		   List<Pair<String, Node>> retList = new ArrayList<>();
		   
	// TODO: Java ...	   Pair<String, Node>[] retArr;// = new Pair<String, Node>[list.getLength()];
	// TODO: Java	   retArr = new Pair[1];
		   
		   for(int i = 0; i < nList.getLength(); i++) {
			   Node n = nList.item(i);
			   NodeList childs = n.getChildNodes();
			   int len = childs.getLength();
			   for(int j = 0; j < len; j++) {
				   Node n1 = childs.item(j);
				   if("name".equals(n1.getNodeName())) {			   
					   Pair<String, Node> pair = new Pair<String, Node>(n1.getTextContent(), n);		// TODO: nevim jestli getNodeName da to co ma
					   retList.add(pair);			   
				   }
			   }
		   }
		  
		   
		   return retList;
	   }
}
