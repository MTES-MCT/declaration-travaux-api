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

    private Document createDocument(String messageFileName) throws XmlUnmarshallException {
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
        return xmlDocument;
    }

    private NodeList compile(String messageFileName, String expression, XPath xPath, Document xmlDocument) throws XmlUnmarshallException {
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new XmlUnmarshallException(messageFileName + " impossible à unmarshaller", e);
        }
        return nodeList;
    }

    private String getContent(String messageFileName, String expression, XPath xPath, Document xmlDocument) throws XmlUnmarshallException {
        NodeList nodeList = this.compile(messageFileName, expression, xPath, xmlDocument);
        if (nodeList == null || nodeList.getLength() < 1)
            throw new XmlUnmarshallException(messageFileName + " impossible de trouver le code: " + expression);
        return nodeList.item(0).getTextContent();
    }

    @Override
    public ADAUMessage unmarshall(String messageFileName) throws XmlUnmarshallException {
        ADAUMessage message = new ADAUMessage();
        Document xmlDocument = this.createDocument(messageFileName);
        XPath xPath = XPathFactory.newInstance().newXPath();
        String code = this.getContent(messageFileName, "//*/Document/Code/text()", xPath, xmlDocument);
        message.setCode(code);
        String cerfaFileName = this.getContent(messageFileName, "//*/Document/FichierFormulaire/FichierDonnees/text()", xPath, xmlDocument);
        message.setCerfaFileName(cerfaFileName);
        String id = this.getContent(messageFileName, "//*/Teledemarche/NumeroTeledemarche/text()", xPath, xmlDocument);
        message.setId(id);
        String date = this.getContent(messageFileName, "//*/Teledemarche/Date/text()", xPath, xmlDocument);
        message.setDate(date);
        return message;
    }

}