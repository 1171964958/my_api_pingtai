package yi.master.business.web.bean;

import java.io.Serializable;

/**
 * 测试集-测试用例关联
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebSuiteCaseComp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer compId;
	/**
	 * 测试集 测试用例 一对多
	 */
	private WebTestSuite testSuite;
	private WebTestCase testCase;
	/**
	 * 执行顺序
	 */
	private Integer execSeq = 1;
	/**
	 * 组名，如果分布式执行，会根据组名来分配置脚本执行策略
	 */
	private String groupName = "default";
	/**
	 * 跳过测试标记<br>
	 * 1 - 跳过 ，0 - 不跳过
	 */
	private String skipFlag = "0";
	
	public WebSuiteCaseComp(Integer compId, WebTestSuite testSuite, WebTestCase testCase, Integer execSeq,
			String groupName, String skipFlag) {
		super();
		this.compId = compId;
		this.testSuite = testSuite;
		this.testCase = testCase;
		this.execSeq = execSeq;
		this.groupName = groupName;
		this.skipFlag = skipFlag;
	}

	public WebSuiteCaseComp() {
		super();
		
	}

	public Integer getCompId() {
		return compId;
	}

	public void setCompId(Integer compId) {
		this.compId = compId;
	}

	public WebTestSuite getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(WebTestSuite testSuite) {
		this.testSuite = testSuite;
	}

	public WebTestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(WebTestCase testCase) {
		this.testCase = testCase;
	}

	public Integer getExecSeq() {
		return execSeq;
	}

	public void setExecSeq(Integer execSeq) {
		this.execSeq = execSeq;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSkipFlag() {
		return skipFlag;
	}

	public void setSkipFlag(String skipFlag) {
		this.skipFlag = skipFlag;
	}

	@Override
	public String toString() {
		return "WebSuiteCaseComp [compId=" + compId + ", testSuite=" + testSuite + ", testCase=" + testCase
				+ ", execSeq=" + execSeq + ", groupName=" + groupName + ", skipFlag=" + skipFlag + "]";
	}
	
}
