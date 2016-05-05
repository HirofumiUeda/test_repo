package com.ueda.test;

import java.io.File;

public class Constants {

	// TODO Jenkins リポジトリのホームをパラメータ化
	public static String JENKINST_REPO_HOME = "C:\\Program Files (x86)\\Jenkins\\jobs";
	// TODO Jenkins リポジトリ対象をパラメータ化	
	final public static String REPO = File.separator + "test_repo";
	final public static String BUILDS = File.separator + "builds";
	final public static String OPEN_TASKS_XML_FILE = "open-tasks.xml";
}
