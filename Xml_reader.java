import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Xml_reader{
    public static void readXml(String xmlFile){
        try {
            /*String path = "C:\\Users\\ohad1\\OneDrive\\Documents\\";
            String filename = "alarm_net.xml";
            String xmlDirectory = path + filename;*/
            File xmlDoc = new File(xmlFile);
            DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fact.newDocumentBuilder();
            Document doc = builder.parse(xmlDoc);

            Network bayesianNetwork = new Network();

            NodeList variableList = doc.getElementsByTagName("VARIABLE");
            for (int i = 0; i < variableList.getLength(); i++) {
                Node nNode = variableList.item(i); //grabbing one Variable

                //we want to read the subElements only if the element actually has subElements instead of text only
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String Name = eElement.getElementsByTagName("NAME").item(0).getTextContent();
                    System.out.println("Name: " + Name);
                    Variable var = new Variable(Name);

                    for(int j=0; j<2; j++){
                        String outcome = eElement.getElementsByTagName("OUTCOME").item(j).getTextContent();
                        var.setOutcomeList(outcome);
                    }
                    bayesianNetwork.addVar(var);
                    System.out.println(var);
                }
            }
            NodeList definitionList = doc.getElementsByTagName("DEFINITION");
            for (int i = 0; i < definitionList.getLength(); i++) {
                Node nNode = definitionList.item(i); //grabbing one Definition

                //we want to read the subElements only if the element actually has subElements instead of text only
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String FOR = eElement.getElementsByTagName("FOR").item(0).getTextContent();
                    System.out.println("FOR: " + FOR);
                    Definition def = new Definition(FOR);

                    int j=0;
                    while (eElement.getElementsByTagName("GIVEN").item(j)!=null){
                        String given = eElement.getElementsByTagName("GIVEN").item(j).getTextContent();
                        def.setGivenList(given);
                    }

                    j=0;
                    while (eElement.getElementsByTagName("TABLE").item(j)!=null){
                        String table = eElement.getElementsByTagName("TABLE").item(j).getTextContent();
                        //def.setTableList(table);
                    }

                    String t = eElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    System.out.println("TABLE: " + t);
                    def.getTableList();

                    bayesianNetwork.addDef(def);
                }
            }
        }
        catch (Exception e) {
            System.out.println("something went wrong");
        }
    }
}
