package com.ueda.test;

import java.util.List;
import java.util.Map;

import java.util.Properties;

import com.ueda.test.bean.JenkinsEnvironmentInfoBean;
import com.ueda.test.bean.OpenTasksXMLBean;
import com.ueda.test.senario.JenkinsTaskAnalizer;
import com.ueda.test.senario.PropertyLoader;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args == null || (args != null && args.length == 0)) {
				return;
			}
			PropertyLoader loader = new PropertyLoader(args[0]);
			Properties prop = loader.loadProperties();
			JenkinsEnvironmentInfoBean jenkinsEnvironmentInfoBean = loader.getValues(prop);
			JenkinsTaskAnalizer test = new JenkinsTaskAnalizer(jenkinsEnvironmentInfoBean);
			Map<Integer, List<OpenTasksXMLBean>> parameter = test.createParam();
			test.execute(parameter);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} catch (Throwable th) {
			System.err.println(th.getMessage());
		}
	}

}
