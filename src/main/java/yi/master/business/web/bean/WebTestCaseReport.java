package yi.master.business.web.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.struts2.json.annotations.JSON;

import yi.master.business.user.bean.User;

/**
 * 测试用例的报告
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestCaseReport implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer caseReportId;
	/**
	 * 所属测试集报告
	 */
	private WebTestSuiteReport suiteReport;
	/**
	 * 对应测试集
	 */
	private WebTestCase testCase;
	private String caseName;
	/**
	 * 实际测试用到的浏览器类型，可能不是测试用例中定义的类型
	 */
	private String browserType;
	/**
	 * 运行结果<br>
	 * success(1)/fail(0)
	 */
	private String runStatus = "1";
	/**
	 * 该测试用例拥有的测试步骤个数
	 */
	private Integer stepCount;
	/**
	 * 实际执行的测试步骤个数
	 */
	private Integer executeStepCount;
	/**
	 * 开始时间
	 */
	private Timestamp startTime;
	/**
	 * 结束时间
	 */
	private Timestamp finishTime;
	private String mark;
	private User testUser;
	/**
	 * 结果详情<br>
	 * json格式
	 */
	private String resultDetails;
	/**
	 * 运行日志
	 */
	private String runLog;
	
	public WebTestCaseReport(Integer caseReportId, WebTestSuiteReport suiteReport, WebTestCase testCase,
			String caseName, String browserType, String runStatus, Integer stepCount, Integer executeStepCount,
			Timestamp startTime, Timestamp finishTime, String mark, User testUser, String resultDetails,
			String runLog) {
		super();
		this.caseReportId = caseReportId;
		this.suiteReport = suiteReport;
		this.testCase = testCase;
		this.caseName = caseName;
		this.browserType = browserType;
		this.runStatus = runStatus;
		this.stepCount = stepCount;
		this.executeStepCount = executeStepCount;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.mark = mark;
		this.testUser = testUser;
		this.resultDetails = resultDetails;
		this.runLog = runLog;
	}

	public WebTestCaseReport() {
		super();
		
	}

	public Integer getCaseReportId() {
		return caseReportId;
	}

	public void setCaseReportId(Integer caseReportId) {
		this.caseReportId = caseReportId;
	}

	@JSON(serialize=false)
	public WebTestSuiteReport getSuiteReport() {
		return suiteReport;
	}

	public void setSuiteReport(WebTestSuiteReport suiteReport) {
		this.suiteReport = suiteReport;
	}

	@JSON(serialize=false)
	public WebTestCase getTestCase() {
		return testCase;
	}

	public void setTestCase(WebTestCase testCase) {
		this.testCase = testCase;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public String getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}

	public Integer getStepCount() {
		return stepCount;
	}

	public void setStepCount(Integer stepCount) {
		this.stepCount = stepCount;
	}

	public Integer getExecuteStepCount() {
		return executeStepCount;
	}

	public void setExecuteStepCount(Integer executeStepCount) {
		this.executeStepCount = executeStepCount;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public User getTestUser() {
		return testUser;
	}

	public void setTestUser(User testUser) {
		this.testUser = testUser;
	}

	@JSON(serialize=false)
	public String getResultDetails() {
		return resultDetails;
	}

	public void setResultDetails(String resultDetails) {
		this.resultDetails = resultDetails;
	}

	public String getRunLog() {
		return runLog;
	}

	public void setRunLog(String runLog) {
		this.runLog = runLog;
	}

	@Override
	public String toString() {
		return "WebTestCaseReport [caseReportId=" + caseReportId + ", caseName=" + caseName + ", browserType="
				+ browserType + ", runStatus=" + runStatus + ", stepCount=" + stepCount + ", executeStepCount="
				+ executeStepCount + ", startTime=" + startTime + ", finishTime=" + finishTime + ", mark=" + mark
				+ ", resultDetails=" + resultDetails + ", runLog=" + runLog + "]";
	}
}
