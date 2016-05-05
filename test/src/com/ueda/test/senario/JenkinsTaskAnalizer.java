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
		int baseBuildNum = getBaseBuildNum(arguments, parameter);
		int targetBuildNum = getTargetBuildNum(arguments, parameter);
		if (baseBuildNum == Integer.MAX_VALUE
				|| targetBuildNum == Integer.MIN_VALUE
				|| baseBuildNum >= targetBuildNum) {
			// èIóπ
			System.exit(1);
		}
		// î‰ärèàóù
		// FIXME
	}

	private int getTargetBuildNum(String[] arguments,
			Map<Integer, List<OpenTasksXMLBean>> parameter) {
		int targetBuildNum = Integer.MIN_VALUE;
		if (arguments != null && arguments.length > 1) {
			try {
				targetBuildNum = Integer.parseInt(arguments[1]);
				if (!parameter.containsKey(targetBuildNum)) {
					targetBuildNum = getMaxBuildNum(parameter);
				}
			} catch (NumberFormatException nfe) {
				targetBuildNum = getMaxBuildNum(parameter);
			}
		}
		return targetBuildNum;
	}

	private int getMaxBuildNum(Map<Integer, List<OpenTasksXMLBean>> parameter) {
		int max = Integer.MIN_VALUE;
		for (Integer buildNum : parameter.keySet()) {
			if (max < buildNum
					&& parameter.get(buildNum) != null
					&& parameter.get(buildNum).size() > 0) {
				max = buildNum;
			}
		}
		return max;
	}

	private int getBaseBuildNum(String[] arguments, Map<Integer, List<OpenTasksXMLBean>> parameter) {
		int baseBuildNum = Integer.MAX_VALUE;
		if (arguments != null && arguments.length > 0) {
			try {
				baseBuildNum = Integer.parseInt(arguments[0]);
				if (!parameter.containsKey(baseBuildNum)) {
					baseBuildNum = getMinBuildNum(parameter);
				}
			} catch (NumberFormatException nfe) {
				baseBuildNum = getMinBuildNum(parameter);
			}
		}
		return baseBuildNum;
	}

	private int getMinBuildNum(Map<Integer, List<OpenTasksXMLBean>> parameter) {
		int min = Integer.MAX_VALUE;
		for (Integer buildNum : parameter.keySet()) {
			if (min > buildNum
					&& parameter.get(buildNum) != null
					&& parameter.get(buildNum).size() > 0) {
				min = buildNum;
			}
		}
		return min;
	}

	
}
