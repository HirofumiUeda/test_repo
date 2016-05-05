package com.ueda.test;

import java.util.List;
import java.util.Map;

import com.ueda.test.bean.OpenTasksXMLBean;
import com.ueda.test.senario.JenkinsTaskAnalizer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JenkinsTaskAnalizer test = new JenkinsTaskAnalizer(args);
			Map<Integer, List<OpenTasksXMLBean>> parameter = test.createParam();
			test.execute(parameter);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
