package com.ueda.test.senario;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.ueda.test.Constants;
import com.ueda.test.bean.JenkinsEnvironmentInfoBean;

public class PropertyLoader {

	private String propertyPath;
	public PropertyLoader(String propertyPath) {
		this.propertyPath = propertyPath;
	}
	public Properties loadProperties() {
		 final Properties prop = new Properties();
		InputStream inStream = null;
		try {
			inStream = new BufferedInputStream(
					new FileInputStream(propertyPath));
			prop.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop;
	}

	public JenkinsEnvironmentInfoBean getValues(Properties prop) {
		JenkinsEnvironmentInfoBean bean = new JenkinsEnvironmentInfoBean();
		String jenkinsRepoHome = prop.getProperty(Constants.JENKINS_REPOSITORY_HOME);
		if (jenkinsRepoHome != null) {
			bean.setJenkinsRepoHome(jenkinsRepoHome);
		} else {
			System.err.println("JenkinsのHomeを指定してください。");
			System.exit(1);
		}
		String repoName = prop.getProperty(Constants.REPOSITORY_NAME);
		if (repoName != null) {
			bean.setRepoName(repoName);
		} else {
			System.err.println("リポジトリ名を指定してください。");
			System.exit(1);
		}
		int baseBuildNum = getPropertyValueForInt(prop, Constants.BASE_BUILD_NUM);
		bean.setBaseBuildNum(baseBuildNum);
		int targetBuildNum = getPropertyValueForInt(prop, Constants.TARGET_BUILD_NUM);
		bean.setTargetBuildNum(targetBuildNum);
		String jenkinsWorkspace = prop.getProperty(Constants.JENKINS_WORKSPACE);
		bean.setJenkinsWorkspace(jenkinsWorkspace);
		return bean;
	}

	private int getPropertyValueForInt(Properties prop, String propertyName) {
		int value = -1;
		try {
			value = Integer.parseInt(prop.getProperty(propertyName));
		} catch (NumberFormatException nfe) {
			value = -1;
		}
		return value;
	}

}
