/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server.JStreamerServer;

/**
 *
 * @author patrick
 */
public class JStreamerMain
{
    public static Document initParser(File f) throws ParserConfigurationException,
            SAXException, IOException
    {
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        return builder.parse(f);
    }

    public static String getTextValue(Element e, String tagName)
    {
        String textVal = null;

        NodeList nodeList = e.getElementsByTagName(tagName);

        if(nodeList != null && nodeList.getLength() > 0)
        {
            Element tmpElement = (Element)nodeList.item(0);
            textVal = tmpElement.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    public static void printUsage()
    {
        System.out.println("JStreamer usage:");
        System.out.println("    java -jar JStreamer <config_file.xml> -- read config from xml file");
        System.out.println("    java -jar JStreamer <port> <username> <password> <ip> -- read config from cli");
    }

    public static void main(String[] args)
    {
        JStreamerServer streamerServer = null;
        if(args.length == 0 || args.length == 2 || args.length > 4)
        {
            JStreamerMain.printUsage();
            System.exit(0);
        }

        if(args.length == 4)
        {
            try
            {
                streamerServer = new JStreamerServer(Integer.parseInt(args[0]), args[1], args[2], args[3]);
                streamerServer.run();
            }
            catch (IOException ex)
            {
                System.err.println("Bad Configuration, I can create a proxy");
            }
        }
        else if(args.length == 1)
        {
            try
            {
                Document document = JStreamerMain.initParser(new File(args[0]));
                int port = 0;
                String username = null;
                String password = null;
                String link = null;
                Element nodeElement = document.getDocumentElement();
                NodeList nodeList = nodeElement.getElementsByTagName("webcam");

               
                Element el = (Element)nodeList.item(0);
                
                port = Integer.parseInt(JStreamerMain.getTextValue(el, "port"));
                username = JStreamerMain.getTextValue(el, "username");
                password = JStreamerMain.getTextValue(el, "password");
                
                link = JStreamerMain.getTextValue(el, "url");
                streamerServer = new JStreamerServer(port, username, password, link);
               // System.out.println("Create " + streamerServer);
                streamerServer.run();
            }
            catch (ParserConfigurationException ex)
            {
                System.err.println("Bad xml file");
            }
            catch (SAXException ex)
            {
                System.err.println("Bad xml file");
            }
            catch (IOException ex)
            {
                System.err.println("I can't open xml file");
            }
        }

    }
}
