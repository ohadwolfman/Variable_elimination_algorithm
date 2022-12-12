import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Xml_reader {
    public static void readXml(String xmlDirectory){
        try {
            /*String path = "C:\\Users\\ohad1\\OneDrive\\Documents\\";
            String filename = "alarm_net.xml";
            String xmlDirectory = path + filename;*/
            File xmlDoc = new File(xmlDirectory);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            Document doc = builder.parse(xmlDoc);

            //Read root element                     doc locate root       give me it's name
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            //read array of students elements
            //this array called NodeList
            NodeList nlist = doc.getElementsByTagName("VARIABLE");

            for (int i = 0; i < nlist.getLength(); i++) {
                Node nNode = nlist.item(i); //grabbing one Variable
                System.out.println("Node name: " + nNode.getNodeName() + " " + (i + 1)); //i+1 because we start from 0

                //we want to read the subElements only if the element actually has subElements instead of text only
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Name: " + eElement.getAttribute("NAME"));
                    System.out.println("Outcome 1: " + eElement.getElementsByTagName("OUTCOME").item(0).getTextContent());
                    //System.out.println("Outcome 2: " + eElement.getElementsByTagName("OUTCOME").item(0).getTextContent());
                    System.out.println("-----------");
                }
            }
        }
        catch (Exception e) {
        }
    }
}
