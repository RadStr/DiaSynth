package analyzer;

import util.Pair;
import util.swing.ErrorFrame;
import util.logging.MyLogger;
import test.ProgramTest;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

// This is very old class so the design is kind of weird. We work with this class only in static way.
public class AnalyzerXML {
    private AnalyzerXML() {
        // Can't make instance of this class
    }

    private static Document xmlDoc;

    public static Document getXMLDoc() {
        return xmlDoc;
    }

    public static void setXMLDoc(String xmlFilename, JFrame callingFrame, String tagName) {
        xmlDoc = loadXMLToDoc(xmlFilename, callingFrame, tagName);
    }

    // a bit of inspiration https://www.tutorialspoint.com/java_xml/java_dom_create_document.htm
    public static void createXMLFile(String filename, Node node, JFrame callingFrame) {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            MyLogger.logException(e);
            new ErrorFrame(callingFrame, "Unknown error in createXMLFile");
        }


        // Otherwise the xml content is on one line - it doesn't really matter, unless you are viewing in text editor.
        // So maybe in future I will just put it on 1 line, since adding the indent adds small problems to the
        // processing using XML dom.
        // The newly added indentation nodes match "\n\\s+" regex pattern
        // https://stackoverflow.com/questions/8085006/how-to-add-a-carriage-return-to-xml-output-in-java
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(node);
        StreamResult result = new StreamResult(new File(filename));
        try {
            transformer.transform(source, result);
        }
        catch (TransformerException e) {
            MyLogger.logException(e);
            new ErrorFrame(callingFrame, "Unknown error in createXMLFile");
        }
    }


    /**
     * Invalid nodes are the nodes which don't have any content - they are just indentation in the XML).
     *
     * @param root
     */
    public static void removeInvalidNodes(Node root) {
        boolean isPreviousNodeIndentation = false;
        NodeList rootChilds = root.getChildNodes();
        List<Integer> emptyNodesIndices = new ArrayList<>();
        for (int i = 0; i < rootChilds.getLength(); i++) {
            Node currNode = rootChilds.item(i);
            String currNodeText = currNode.getTextContent();
            if (currNodeText.matches("\n\\s+")) {
                if (isPreviousNodeIndentation) {
                    emptyNodesIndices.add(i);
                }
                isPreviousNodeIndentation = true;
            }
            else {
                isPreviousNodeIndentation = false;
            }
        }

        for (int i = emptyNodesIndices.size() - 1; i >= 0; i--) {
            root.removeChild(rootChilds.item(emptyNodesIndices.get(i)));
        }
    }


    public static Node findNodeXML(NodeList list, String name) {
        int len = list.getLength();
        for (int i = 0; i < len; i++) {
            Node node = list.item(i);
            if (node.getNodeName().equals(name)) {
                return node;
            }
        }

        return null;
    }


    public static int findNodeWithValue(NodeList nList, String val) {
        int len = nList.getLength();
        for (int i = 0; i < len; i++) {
            Node n = nList.item(i);
            if (n.getTextContent().equals(val)) {
                return i;
            }
        }

        return -1;
    }

    // The attrName is in most of these methods redundant since all attributes are called "name" in our case,
    // it is there just to make it more general
    public static List<String> getSongNames() {
        return getInfoNodeValuesMatchingGivenAttribute("name", SongLibraryPanel.HEADER_NAME_COLUMN_TITLE);
    }


    public static List<String> getInfoNodeValuesMatchingGivenAttribute(String attrName, String attrVal) {
        List<String> names = new ArrayList<>();
        NodeList nList = xmlDoc.getElementsByTagName("song");
        int len = nList.getLength();
        for (int i = 0; i < len; i++) {
            Node n = nList.item(i);
            NodeList nnList = n.getChildNodes();
            for (int j = 0; j < nnList.getLength(); j++) {
                Node nn = nnList.item(j);
                if (isMatchingGivenAttribute(nn, attrName, attrVal)) {
                    names.add(nn.getFirstChild().getTextContent());            // Add the name of the song
                    break;
                }
            }
        }

        return names;
    }

    public static List<String> getInfoNodeValuesMatchingGivenAttribute(String attrVal) {
        return getInfoNodeValuesMatchingGivenAttribute("name", attrVal);
    }


    public static Node getFirstSongNodeMatchingGivenName(String songName) {
        NodeList nList = xmlDoc.getElementsByTagName("song");
        int len = nList.getLength();
        for (int i = 0; i < len; i++) {
            Node n = nList.item(i);
            NodeList nnList = n.getChildNodes();
            for (int j = 0; j < nnList.getLength(); j++) {
                Node nn = nnList.item(j);

                if (isMatchingGivenAttribute(nn, "name", SongLibraryPanel.HEADER_NAME_COLUMN_TITLE)) {
                    if (getInfoNodeValue(nn).equals(songName)) {
                        return n;
                    }
                    else {
                        break;
                    }
                }
            }
        }

        return null;
    }


    public static Node findFirstNodeWithGivenAttribute(NodeList nList, String attrName, String attrVal) {
        int len = nList.getLength();
        for (int j = 0; j < len; j++) {
            Node n = nList.item(j);
            if (isMatchingGivenAttribute(n, attrName, attrVal)) {
                return n;
            }
        }

        return null;
    }

    public static Node findFirstNodeWithGivenAttribute(NodeList nList, String attrVal) {
        return findFirstNodeWithGivenAttribute(nList, "name", attrVal);
    }


    public static String getAttributeValueFromInfoNode(Node infoNode, String attrName) {
        // Added because when we are using the indentation, the node can be just the white characters representing the
        // indentation, so then it doesn't have any attributes. (although this check might be useful even for other cases)
        if (infoNode.getAttributes() == null) {
            return null;
        }
        return infoNode.getAttributes().getNamedItem(attrName).getNodeValue();
    }

    // Name is the part in "" name = "name of algorithm"
    public static String getInfoNodeName(Node infoNode) {
        return getAttributeValueFromInfoNode(infoNode, "name");
    }

    /**
     * Returns number of valid info nodes inside the given list.
     *
     * @param nList
     * @return
     */
    public static int getValidInfoNodeCount(NodeList nList) {
        int count = 0;
        for (int i = 0; i < nList.getLength(); i++) {
            if (getInfoNodeName(nList.item(i)) != null) {
                count++;
            }
        }
        return count;
    }


    // Value is the part in <value> this is value </value> inside the node marked as infoNode
    public static Node getValueNodeFromInfoNode(Node infoNode) {
        return findNodeXML(infoNode.getChildNodes(), "value");
    }

    public static String getInfoNodeValue(Node infoNode) {
        return getValueNodeFromInfoNode(infoNode).getTextContent();
    }

    public static boolean isMatchingGivenAttribute(Node n, String attrName, String attrVal) {
        // Added because when we are using the indentation, the node can be just the white characters representing the
        // indentation, so then it doesn't have any attributes. (although this check might be useful even for other cases)
        if (n.getAttributes() == null) {
            return false;
        }
        return attrVal.equals(n.getAttributes().getNamedItem(attrName).getNodeValue());
    }


    /**
     * Loads xml file with filename to document, which is then returned. If the file doesn't exist then it is created
     * with the same name and first tag is of name tagName.
     *
     * @param filename     is the name of xml file.
     * @param callingFrame is the frame which is calling this method.
     * @param tagName      is the tag which will be used if the file doesn't exists
     * @return Returns file loaded to Document.
     */
    public static Document loadXMLToDoc(String filename, JFrame callingFrame, String tagName) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
                PrintWriter writer = new PrintWriter(file);
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator() +
                               "<" + tagName + ">" + System.lineSeparator() + "</" + tagName + ">");
                writer.close();
            }
            catch (IOException e1) {
                new ErrorFrame(callingFrame, "Error when creating file, because given file didn't exist");
                e1.printStackTrace();
            }
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e1) {
            MyLogger.logException(e1);
            new ErrorFrame(callingFrame, "Unknown error in loadXMLToDoc");
        }
        Document document = null;
        try {
            document = dBuilder.parse(file);
        }
        catch (SAXException e) {
            MyLogger.logException(e);
            new ErrorFrame(callingFrame, "SAX error");
        }
        catch (IOException e) {
            MyLogger.logException(e);
            new ErrorFrame(callingFrame, "IO error in loadXMLToDoc");
        }

        document.getDocumentElement().normalize();
        return document;
    }


    public static void addAnalyzedFileToXML(Document xmlDoc, List<Pair<String, String>> list, String mainTag, String underMainTag) {
        ListIterator<Pair<String, String>> iter = list.listIterator();
        if (iter.hasNext()) {
            Pair<String, String> val;
            Node root = xmlDoc.getElementsByTagName(mainTag).item(0);
            Element songElem = null;
            if (root.getNodeType() == Node.ELEMENT_NODE) {
                songElem = xmlDoc.createElement(underMainTag);
                Element roote = (Element) root;
                roote.appendChild(songElem);
            }
            else {
                return;
            }


            while (iter.hasNext()) {
                val = iter.next();
                addNewNode(songElem, val);
            }
        }
    }


    public static void deleteSongsFromDoc(Node[] nodes, Document xmlDoc) {
        for (int i = 0; i < nodes.length; i++) {
            xmlDoc.removeChild(nodes[i]);
        }
    }


    public static void addNewNode(Node node, Pair<String, String> nodeToAdd) {
        Element elem;
        elem = xmlDoc.createElement("info");
        elem.setAttribute("name", nodeToAdd.getKey());
        Element elemInsideElem;
        elemInsideElem = xmlDoc.createElement("value");
        elemInsideElem.appendChild(xmlDoc.createTextNode(nodeToAdd.getValue()));
        elem.appendChild(elemInsideElem);
        node.appendChild(elem);
    }


    /**
     * Pairs are name of the node and node, where name is the name attribute which corresponds to the name of analyzed file.
     *
     * @param xmlDoc
     * @return
     */
    public static List<Pair<String, Node>> getPairs(Document xmlDoc) {
        NodeList nList = xmlDoc.getElementsByTagName("song");
        List<Pair<String, Node>> retList = new ArrayList<>();

        for (int i = 0; i < nList.getLength(); i++) {
            Node n = nList.item(i);
            NodeList childs = n.getChildNodes();
            int len = childs.getLength();
            for (int j = 0; j < len; j++) {
                Node n1 = childs.item(j);
                if (isMatchingGivenAttribute(n1, "name", SongLibraryPanel.HEADER_NAME_COLUMN_TITLE)) {
                    Pair<String, Node> pair = new Pair<String, Node>(getInfoNodeValue(n1), n);
                    retList.add(pair);
                }
            }
        }


        return retList;
    }
}