package com.ueda.test.senario;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ueda.test.Constants;
import com.ueda.test.bean.OpenTasksXMLBean;
import com.ueda.test.reader.XmlReader;

public class JenkinsTaskAnalizer {

	private String[] arguments;
	public JenkinsTaskAnalizer(String[] args) {
		this.arguments = args;
	}

	public Map<Integer, List<OpenTasksXMLBean>> createParam() throws SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		Map<Integer, List<OpenTasksXMLBean>> map = new HashMap<>();
		String repoPath = Constants.JENKINST_REPO_HOME + Constants.REPO + Constants.BUILDS;
		File dir = new File(repoPath);
		File[] filelist = dir.listFiles();
		for (File file : filelist) {
			if (file.isDirectory()) {
				if (isTargetDirectory(file)) {
					int buildNum = Integer.parseInt(file.getName());
					List<OpenTasksXMLBean> list = getOpenTasksXMLBeanList(file);
					for (OpenTasksXMLBean bean : list) {
						bean.setBuildNum(buildNum);
						if (!map.containsKey(buildNum)) {
							map.put(buildNum, new ArrayList<OpenTasksXMLBean>());
						}
						map.get(buildNum).add(bean);
					}
				}
			}
		}
		System.out.println(map);
		return map;
	}

	private List<OpenTasksXMLBean> getOpenTasksXMLBeanList(File file) throws SAXException, IOException, ParserConfigurationException {
		String filePath = file.getAbsolutePath() + File.separator + Constants.OPEN_TASKS_XML_FILE;
		XmlReader reader = new XmlReader();
		return reader.domReadForJenkinsAnalyzer(filePath);
	}

	private boolean isTargetDirectory(File file) {
		boolean isTargetDirectory = false;
		String name = file.getName();
		try {
			Integer.valueOf(name);
			String filePath = file.getAbsolutePath() + File.separator + Constants.OPEN_TASKS_XML_FILE;
			File openTasksFile = new File(filePath);
			if (openTasksFile.exists()) {
				isTargetDirectory = true;
			}
		} catch (NumberFormatException nfe) {
		}
		return isTargetDirectory;
	}

	public void execute(Map<Integer, List<OpenTasksXMLBean>> parameter) {
		// TODO Auto-generated method stub
		
	}

}
