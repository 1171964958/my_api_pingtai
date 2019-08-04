package yi.master.business.web.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.business.user.bean.User;

/**
 * 测试集报告
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestSuiteReport implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer suiteReportId;
	/**
	 * 所属测试集
	 */
	private WebTestSuite testSuite;
	/**
	 * 测试集名称
	 */
	private String suiteName;
	/**
	 * 浏览器状态
	 */
	private String browserType;
	/**
	 * 开始时间
	 */
	private Timestamp startTime;
	/**
	 * 结束时间
	 */
	private Timestamp finishTime;
	/**
	 * 结束标记
	 */
	private String finishFlag = "N";
	/**
	 * 总测试用例数
	 */
	private Integer totalCount;
	/**
	 * 成功用例数
	 */
	private Integer successCount;
	/**
	 * 失败用例数
	 */
	private Integer failCount;
	/**
	 * 备注
	 */
	private String mark;
	private User testUser;
	/**
	 * 运行日志
	 */
	private String runLog;
	
	/**
	 * 静态测试报告地址
	 */
	private String reportPath;
	
	/**
	 * 结果详情
	 */
	private String reportDetails;
	
	private Set<WebTestCaseReport> caseReports = new HashSet<WebTestCaseReport>();
	
	public WebTestSuiteReport(Integer suiteReportId, WebTestSuite testSuite, String suiteName, String browserType,
			Timestamp startTime, Timestamp finishTime, String finishFlag, Integer totalCount, Integer successCount,
			Integer failCount, String mark, User testUser, String runLog) {
		super();
		this.suiteReportId = suiteReportId;
		this.testSuite = testSuite;
		this.suiteName = suiteName;
		this.browserType = browserType;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.finishFlag = finishFlag;
		this.totalCount = totalCount;
		this.successCount = successCount;
		this.failCount = failCount;
		this.mark = mark;
		this.testUser = testUser;
		this.runLog = runLog;
	}

	public WebTestSuiteReport() {
		super();
		
	}

	public void setCaseReports(Set<WebTestCaseReport> caseReports) {
		this.caseReports = caseReports;
	}
	
	@JSON(serialize=false)
	public Set<WebTestCaseReport> getCaseReports() {
		return caseReports;
	}
	
	public Integer getSuiteReportId() {
		return suiteReportId;
	}

	public void setSuiteReportId(Integer suiteReportId) {
		this.suiteReportId = suiteReportId;
	}

	@JSON(serialize=false)
	public WebTestSuite getTestSuite() {
		return testSuite;
	}

	public void setTestSuite(WebTestSuite testSuite) {
		this.testSuite = testSuite;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
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

	public String getFinishFlag() {
		return finishFlag;
	}

	public void setFinishFlag(String finishFlag) {
		this.finishFlag = finishFlag;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
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

	public String getRunLog() {
		return runLog;
	}

	public void setRunLog(String runLog) {
		this.runLog = runLog;
	}
	
	public void setReportDetails(String reportDetails) {
		this.reportDetails = reportDetails;
	}
	
	@JSON(serialize=false)
	public String getReportDetails() {
		return reportDetails;
	}
	
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	
	public String getReportPath() {
		return reportPath;
	}

	@Override
	public String toString() {
		return "WebTestSuiteReport [suiteReportId=" + suiteReportId + ", suiteName=" + suiteName + ", browserType="
				+ browserType + ", startTime=" + startTime + ", finishTime=" + finishTime + ", finishFlag=" + finishFlag
				+ ", totalCount=" + totalCount + ", successCount=" + successCount + ", failCount=" + failCount
				+ ", mark=" + mark + ", runLog=" + runLog + "]";
	}
}
