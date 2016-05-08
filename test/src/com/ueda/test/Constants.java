package com.ueda.test;

import java.io.File;

public class Constants {

//	Jenkins リポジトリのホームをパラメータ化
//	public static String JENKINS_REPO_HOME = "C:\\Program Files (x86)\\Jenkins\\jobs";
// Jenkins リポジトリ対象をパラメータ化	
//	final public static String REPO = File.separator + "test_repo";
	final public static String BUILDS = File.separator + "builds";
	final public static String OPEN_TASKS_XML_FILE = "open-tasks.xml";

	final public static String JENKINS_REPOSITORY_HOME = "jenkins.repo.home";
	final public static String REPOSITORY_NAME ="repository.name";
	final public static String BASE_BUILD_NUM = "base.build.num";
	final public static String TARGET_BUILD_NUM ="target.build.num";

}
