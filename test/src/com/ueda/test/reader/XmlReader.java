package com.ueda.test.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ueda.test.bean.OpenTasksXMLBean;

public class XmlReader {


	public List<OpenTasksXMLBean> domReadForJenkinsAnalyzer(int buildNum, String file) throws SAXException, IOException, ParserConfigurationException {

		List<OpenTasksXMLBean> list = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		Element root = document.getDocumentElement();
		//ルート要素の子ノードを取得する
		NodeList rootChildren = root.getChildNodes();
		for(int i=0; i < rootChildren.getLength(); i++) {
			Node node = rootChildren.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				if (element.getNodeName().equals("task")) {
					NodeList taskChildren = node.getChildNodes();
					OpenTasksXMLBean opeanTasksXMLBean = new OpenTasksXMLBean();
					for (int j=0; j < taskChildren.getLength(); j++) {
						Node taskChildNode = taskChildren.item(j);
						if (taskChildNode.getNodeType() == Node.ELEMENT_NODE) {
							if (taskChildNode.getNodeName().equals("priority")) {
//								System.out.println("年齢：" + taskChildNode.getTextContent());
								opeanTasksXMLBean.setPriority(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("primaryLineNumber")) {
//								System.out.println("趣味:" + taskChildNode.getTextContent());
								opeanTasksXMLBean.setPrimaryLineNumber(Integer.parseInt(taskChildNode.getTextContent()));
							} else if (taskChildNode.getNodeName().equals("fileName")) {
								opeanTasksXMLBean.setFileName(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("packageName")) {
								opeanTasksXMLBean.setPackageName(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("type")) {
								opeanTasksXMLBean.setType(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("contextHashCode")) {
								opeanTasksXMLBean.setContextHashCode(Long.parseLong(taskChildNode.getTextContent()));
							} else if (taskChildNode.getNodeName().equals("pathName")) {
								opeanTasksXMLBean.setPathName(taskChildNode.getTextContent());
							}
						}
					}
					opeanTasksXMLBean.setBuildNum(buildNum);
					list.add(opeanTasksXMLBean);
				}
			}
		}
		return list;
	}

	public List<OpenTasksXMLBean> domReadForJenkinsAnalyzer(String file) throws SAXException, IOException, ParserConfigurationException {

		List<OpenTasksXMLBean> list = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		Element root = document.getDocumentElement();
		//ルート要素のノード名を取得する
//		System.out.println("ノード名：" +root.getNodeName());
		//ルート要素の子ノードを取得する
		NodeList rootChildren = root.getChildNodes();
		for(int i=0; i < rootChildren.getLength(); i++) {
			Node node = rootChildren.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				if (element.getNodeName().equals("task")) {
					NodeList taskChildren = node.getChildNodes();
					OpenTasksXMLBean opeanTasksXMLBean = new OpenTasksXMLBean();
					for (int j=0; j < taskChildren.getLength(); j++) {
						Node taskChildNode = taskChildren.item(j);
						if (taskChildNode.getNodeType() == Node.ELEMENT_NODE) {
							if (taskChildNode.getNodeName().equals("priority")) {
//								System.out.println("年齢：" + taskChildNode.getTextContent());
								opeanTasksXMLBean.setPriority(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("primaryLineNumber")) {
//								System.out.println("趣味:" + taskChildNode.getTextContent());
								opeanTasksXMLBean.setPrimaryLineNumber(Integer.parseInt(taskChildNode.getTextContent()));
							} else if (taskChildNode.getNodeName().equals("fileName")) {
								opeanTasksXMLBean.setFileName(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("packageName")) {
								opeanTasksXMLBean.setPackageName(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("type")) {
								opeanTasksXMLBean.setType(taskChildNode.getTextContent());
							} else if (taskChildNode.getNodeName().equals("contextHashCode")) {
								opeanTasksXMLBean.setContextHashCode(Long.parseLong(taskChildNode.getTextContent()));
							} else if (taskChildNode.getNodeName().equals("pathName")) {
								opeanTasksXMLBean.setPathName(taskChildNode.getTextContent());
							}
						}
					}
					list.add(opeanTasksXMLBean);
				}
			}
		}
		return list;
	}
}