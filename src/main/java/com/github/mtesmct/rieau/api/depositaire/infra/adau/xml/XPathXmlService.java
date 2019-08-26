package com.github.mtesmct.rieau.api.depositaire.infra.adau.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class XPathXmlService implements XmlService {

    private NodeList compile(String messageFileName, String expression) throws XmlUnmarshallException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlUnmarshallException(messageFileName + " impossible à unmarshaller", e);
        }
        FileInputStream fileIS;
        try {
            fileIS = new FileInputStream(new File(messageFileName));
        } catch (FileNotFoundException e) {
            throw new XmlUnmarshallException(messageFileName + " impossible à unmarshaller", e);
        }
        Document xmlDocument;
        try {
            xmlDocument = builder.parse(fileIS);
            fileIS.close();
        } catch (SAXException | IOException e) {
            throw new XmlUnmarshallException(messageFileName + " impossible à unmarshaller", e);
        }
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new XmlUnmarshallException(messageFileName + " impossible à unmarshaller", e);
        }
        return nodeList;
    }

    private String toString(String messageFileName, String expression) throws XmlUnmarshallException {
        NodeList nodeList = this.compile(messageFileName, expression);
        if (nodeList == null || nodeList.getLength() < 1)
            throw new XmlUnmarshallException(messageFileName + " impossible de trouver le code: " + expression);
        return nodeList.item(0).getTextContent();
    }

    @Override
    public ADAUMessage unmarshall(String messageFileName) throws XmlUnmarshallException {
        ADAUMessage message = new ADAUMessage();
        String code = this.toString(messageFileName, "//*/Document/Code/text()");
        message.setCode(code);
        String cerfaFileName = this.toString(messageFileName, "//*/Document/FichierFormulaire/FichierDonnees/text()");
        message.setCerfaFileName(cerfaFileName);
        String id = this.toString(messageFileName, "//*/Teledemarche/NumeroTeledemarche/text()");
        message.setId(id);
        String date = this.toString(messageFileName, "//*/Teledemarche/Date/text()");
        message.setDate(date);
        return message;
    }

}