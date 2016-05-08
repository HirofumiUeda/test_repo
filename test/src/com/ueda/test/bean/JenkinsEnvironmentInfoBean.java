package com.ueda.test.bean;


public class JenkinsEnvironmentInfoBean {

	private String jenkinsRepoHome;
	private String repoName;
	private int baseBuildNum;
	private int targetBuildNum;
	
	public String getJenkinsRepoHome() {
		return jenkinsRepoHome;
	}
	public void setJenkinsRepoHome(String jenkinsRepoHome) {
		this.jenkinsRepoHome = jenkinsRepoHome;
	}
	public String getRepoName() {
		return repoName;
	}
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	public int getBaseBuildNum() {
		return baseBuildNum;
	}
	public void setBaseBuildNum(int baseBuildNum) {
		this.baseBuildNum = baseBuildNum;
	}
	public int getTargetBuildNum() {
		return targetBuildNum;
	}
	public void setTargetBuildNum(int targetBuildNum) {
		this.targetBuildNum = targetBuildNum;
	}
	
	
}
