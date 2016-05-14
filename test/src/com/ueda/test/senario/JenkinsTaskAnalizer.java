package com.ueda.test.senario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ueda.test.Constants;
import com.ueda.test.bean.JenkinsEnvironmentInfoBean;
import com.ueda.test.bean.OpenTasksXMLBean;
import com.ueda.test.reader.XmlReader;

public class JenkinsTaskAnalizer {

	private JenkinsEnvironmentInfoBean jenkinsEnvironmentInfoBean;
	public JenkinsTaskAnalizer(JenkinsEnvironmentInfoBean jenkinsEnvironmentInfoBean) {
		this.jenkinsEnvironmentInfoBean = jenkinsEnvironmentInfoBean;
	}

	public Map<Integer, List<OpenTasksXMLBean>> createParam()
			throws SAXException, IOException, ParserConfigurationException {
		Map<Integer, List<OpenTasksXMLBean>> map = new HashMap<>();
		String repoPath = jenkinsEnvironmentInfoBean.getJenkinsRepoHome()
				+ File.separator + jenkinsEnvironmentInfoBean.getRepoName() + Constants.BUILDS;
		File dir = new File(repoPath);
		if (!dir.exists()) {
			System.err.println("Jenkinsのホームが正しくありません。もしくは、存在しないリポジトリが指定されています。指定を見直してください。");
			System.exit(1);
		}
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
//		System.out.println(map);
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
		int baseBuildNum = getBaseBuildNum(jenkinsEnvironmentInfoBean.getBaseBuildNum(), parameter);
		int targetBuildNum = getTargetBuildNum(jenkinsEnvironmentInfoBean.getTargetBuildNum(), parameter);
		if (baseBuildNum == Integer.MAX_VALUE
				|| targetBuildNum == Integer.MIN_VALUE
				|| baseBuildNum >= targetBuildNum) {
			// 終了
			System.err.println("Jenkinsによるビルド回数が足りていないか、指定されたビルド番号が不正です。");
			System.exit(1);
		}
		// 比較処理
		List<OpenTasksXMLBean> targetList = parameter.get(targetBuildNum);
		List<OpenTasksXMLBean> baseList = parameter.get(baseBuildNum);
		List<Long> baseContextHashCodeList = getContextHashCodeList(baseList); 
		int count = 0;
		List<OpenTasksXMLBean> taskList = new ArrayList<>();
		for (OpenTasksXMLBean targetBean : targetList) {
			long contextHashCode = targetBean.getContextHashCode();
			if (baseContextHashCodeList.contains(contextHashCode)) {
				// 当時から存在したためOK
			} else {
				taskList.add(targetBean);
				count++;
			}
		}
		System.out.println("今バージョンから" + count + "個のタスクが増えています。解決済みタスクはクローズしてください。" +
				"未解決タスクは解決するか、チケット管理してください。");
		for (OpenTasksXMLBean bean : taskList) {
			File file = new File(bean.getFileName());
			if (!file.exists()) {
				// TODO 動作確認
				boolean isNotExist = true;
				if (jenkinsEnvironmentInfoBean.getJenkinsWorkspace() != null) {
					String fileName = getFileNameFromFilePath(bean.getFileName());
					String pathName = jenkinsEnvironmentInfoBean.getJenkinsWorkspace() + File.separator + fileName;
					File workspaceFile = new File(pathName);
					if (workspaceFile.exists()) {
						isNotExist = false;
						printTaskContext(workspaceFile, bean.getPrimaryLineNumber());
					}
				}
				if (isNotExist) {
					System.out.println(bean.getFileName() + "は、みつかりませんでした。" + bean.getPrimaryLineNumber() + "行目あたりにタスクを残していないか確認してください。");
				}
			} else {
				printTaskContext(file, bean.getPrimaryLineNumber());
			}
		}
	}

	private String getFileNameFromFilePath(String filePath) {
		int index = filePath.lastIndexOf(File.separator);
		String fileName = filePath.substring(index + 1);
		return fileName;
	}

	private void printTaskContext(File file, int primaryLineNumber) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str = br.readLine();
			int line = 1;
			while (str != null) {
				if (line == primaryLineNumber) {
					System.out.print(file.getName() + "の" + primaryLineNumber + "行目あたりにタスク:「");
					System.out.print(str);
					System.out.println("」が残っています。");
					
				}
				str = br.readLine();
				line++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private List<Long> getContextHashCodeList(List<OpenTasksXMLBean> openTasksXMLBeanList) {
		List<Long> list = new ArrayList<>();
		for (OpenTasksXMLBean bean : openTasksXMLBeanList) {
			long contextHashCode = bean.getContextHashCode();
			list.add(contextHashCode);
		}
		return list;
	}

	private int getTargetBuildNum(int propValue,
			Map<Integer, List<OpenTasksXMLBean>> parameter) {
		int targetBuildNum = Integer.MIN_VALUE;
		if (propValue != -1) {
			try {
				targetBuildNum = propValue;
				if (!parameter.containsKey(targetBuildNum)) {
					targetBuildNum = getMaxBuildNum(parameter);
				}
			} catch (NumberFormatException nfe) {
				targetBuildNum = getMaxBuildNum(parameter);
			}
		} else {
			targetBuildNum = getMaxBuildNum(parameter);
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

	private int getBaseBuildNum(int propValue, Map<Integer, List<OpenTasksXMLBean>> parameter) {
		int baseBuildNum = Integer.MAX_VALUE;
		if (propValue != -1) {
			try {
				baseBuildNum = propValue;
				if (!parameter.containsKey(baseBuildNum)) {
					baseBuildNum = getMinBuildNum(parameter);
				}
			} catch (NumberFormatException nfe) {
				baseBuildNum = getMinBuildNum(parameter);
			}
		} else {
			baseBuildNum = getMinBuildNum(parameter);
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
