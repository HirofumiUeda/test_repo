package com.ueda.test.senario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			System.err.println("Jenkins�̃z�[��������������܂���B�������́A���݂��Ȃ����|�W�g�����w�肳��Ă��܂��B�w����������Ă��������B");
			System.exit(1);
		}
		File[] filelist = dir.listFiles();
		for (File file : filelist) {
			if (file.isDirectory()) {
				if (isTargetDirectory(file)) {
					int buildNum = Integer.parseInt(file.getName());
					List<OpenTasksXMLBean> list = getOpenTasksXMLBeanList(buildNum, file);
					map.put(buildNum, list);
				}
			}
		}
//		System.out.println(map);
		return map;
	}

	private List<OpenTasksXMLBean> getOpenTasksXMLBeanList(int buildNum, File file) throws SAXException, IOException, ParserConfigurationException {
		String filePath = file.getAbsolutePath() + File.separator + Constants.OPEN_TASKS_XML_FILE;
		XmlReader reader = new XmlReader();
		return reader.domReadForJenkinsAnalyzer(buildNum, filePath);
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
			// �I��
			System.err.println("Jenkins�ɂ��r���h�񐔂�����Ă��Ȃ����A�w�肳�ꂽ�r���h�ԍ����s���ł��B");
			System.exit(1);
		}
		// TODO[PDR]�m�F���āA�����targetBuild���w�肳��Ă��Ȃ��ɂ��ւ�炸�A
		// ���݂̃r���h��菬�����l�Ȃ�A���o�[�W�����Ŗ������^�X�N�͑����Ă��Ȃ��Ǝv����B
		System.out.println("base : " + baseBuildNum + ", target : " + targetBuildNum);
		System.out.println("TODO[PDR]�m�F���āA�����targetBuild���w�肳��Ă��Ȃ��ɂ��ւ�炸�A���݂̃r���h��菬�����l�Ȃ�A���o�[�W�����Ŗ������^�X�N�͑����Ă��Ȃ��Ǝv����B");
		// ��r����
		List<OpenTasksXMLBean> targetList = parameter.get(targetBuildNum);
		List<OpenTasksXMLBean> baseList = parameter.get(baseBuildNum);
		List<Long> baseContextHashCodeList = getContextHashCodeList(baseList); 
		List<OpenTasksXMLBean> taskList = targetList.stream()
					.filter(new Predicate<OpenTasksXMLBean>() {
						@Override
						public boolean test(final OpenTasksXMLBean bean) {
							return !baseContextHashCodeList.contains(bean.getContextHashCode());
						}
				    })
					.collect(Collectors.toList());
		int count = taskList != null ? taskList.size() : 0;
		System.out.println("���o�[�W��������" + count + "�̃^�X�N�������Ă��܂��B�����ς݃^�X�N�̓N���[�Y���Ă��������B" +
				"�������^�X�N�͉������邩�A�`�P�b�g�Ǘ����Ă��������B");
		for (OpenTasksXMLBean bean : taskList) {
			File file = new File(bean.getFileName());
			boolean fileExists = !file.exists();
			if (fileExists) {
				boolean isNotExist = true;
				String fileNameForLog = bean.getFileName();
				if (jenkinsEnvironmentInfoBean.getJenkinsWorkspace() != null) {
					String fileName = getFileNameFromFilePath(bean.getFileName());
					String repoPathName = getPathName(bean.getPathName());
					String pathName = jenkinsEnvironmentInfoBean.getJenkinsWorkspace() + File.separator + repoPathName + File.separator + fileName;
					File workspaceFile = new File(pathName);
					if (workspaceFile.exists()) {
						isNotExist = false;
						printTaskContext(workspaceFile, bean.getPrimaryLineNumber());
					}
					fileNameForLog = pathName;
				}
				if (isNotExist) {
					System.out.println(fileNameForLog + "�́A�݂���܂���ł����B" + bean.getPrimaryLineNumber() + "�s�ڂ�����Ƀ^�X�N���c���Ă��Ȃ����m�F���Ă��������B");
				}
			} else {
				printTaskContext(file, bean.getPrimaryLineNumber());
			}
		}
	}

	private String getPathName(String pathName) {
		String path = pathName.replace("/", File.separator);
		return path;
	}

	private String getFileNameFromFilePath(String filePath) {
		String separator = "/";
		int index = filePath.lastIndexOf(separator);
		if (index == -1) {
			index = filePath.lastIndexOf(File.separator);
		}
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
					System.out.print(file.getName() + "��" + primaryLineNumber + "�s�ڂ�����Ƀ^�X�N:�u");
					System.out.print(str);
					System.out.println("�v���c���Ă��܂��B");
					
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
		List<Long> list = openTasksXMLBeanList.stream()
							.map(OpenTasksXMLBean::getContextHashCode)
							.collect(Collectors.toList());
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
		Stream<Integer> stream = parameter.keySet().stream();
		Stream<Integer> notNullStream = 
				stream.filter(new Predicate<Integer>() {
					@Override
					public boolean test(Integer t) {
						return parameter.get(t) != null && parameter.get(t).size() > 0;
					}
				});
		Optional<Integer> max = notNullStream.max((a, b) -> a.compareTo(b));
		return max.orElse(Integer.MIN_VALUE);
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

	private int getMinBuildNum(final Map<Integer, List<OpenTasksXMLBean>> parameter) {
		Stream<Integer> stream = parameter.keySet().stream();
		Stream<Integer> notNullStream = 
				stream.filter(new Predicate<Integer>() {
					@Override
					public boolean test(Integer t) {
						return parameter.get(t) != null && parameter.get(t).size() > 0;
					}
				});
		Optional<Integer> min = notNullStream.min((a, b) -> a.compareTo(b));
		return min.orElse(Integer.MAX_VALUE);
	}

	
}
