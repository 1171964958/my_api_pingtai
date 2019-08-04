package yi.master.business.message.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.user.bean.User;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;

/**
 * 测试报告
 * @author xuwangcheng
 * @version 1.0.0.0,2017.07.12
 *
 */
public class TestReport implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer reportId;
	
	/**
	 * 测试模式,即测试对应的测试集ID
	 */	
	private String testMode;
	
	/**
	 * 前台展示字段 本次测试场景总数量
	 */
	@FieldNameMapper(fieldPath="size(trs)",ifSearch=false)
	@CustomConditionSetting(operator=">")
	private Integer sceneNum;
	/**
	 * 前台展示字段 成功数
	 */
	private Integer successNum;
	/**
	 * 前台展示字段 失败数
	 */
	private Integer failNum;
	/**
	 * 前台展示字段 异常数
	 */
	private Integer stopNum;
	/**
	 * 测试完成标记<br>
	 * Y - 已完成<br>
	 * N - 未完成
	 */
	@FieldRealSearch(names = {"已完成", "未完成"}, values = {"Y", "N"})
	@CustomConditionSetting(operator="=")
	private String finishFlag;
	/**
	 * 测试开始时间
	 */
	private Timestamp createTime;
	/**
	 * 全部场景测试完成时的时间
	 */
	private Timestamp finishTime;
	/**
	 * 测试人
	 */
	private User user;
	
	private String mark;
	
	/**
	 * 前台展示字段,测试人姓名
	 */
	@FieldNameMapper(fieldPath="user.realName")
	private String createUserName;
	
	/**
	 * 前台展示字段,测试集名称
	 */
	@FieldNameMapper(fieldPath="testMode")
	@FieldRealSearch(names = {"全量测试"}, values = {"0"})
	@CustomConditionSetting(conditionType="")
	private String setName;
	
	/**
	 * json格式的测试详情数据，每次测试完成之后自动生成并保存，适用于在线或者离线测试报告的生成
	 */
	private String detailsJson;
	
	/**
	 * 全局唯一测试标识
	 */
	private String guid;
	
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	@FieldNameMapper(fieldPath="createTime")
	private String createTimeText;
	
	private Set<TestResult> trs = new HashSet<TestResult>();
	
	public TestReport(String testMode, String finishFlag,
			Timestamp createTime, User user) {
		super();
		this.testMode = testMode;
		this.finishFlag = finishFlag;
		this.createTime = createTime;
		this.user = user;
	}
	
	public TestReport() {
		super();
		
	}
	
	/**
	 * 获取静态文件路径
	 * @return
	 */
	public String getReportHtmlPath() {
		return SystemConsts.REPORT_VIEW_HTML_FOLDER + "/" + this.reportId + "_" + PracticalUtils.formatDate("yyyyMMddHHmm", this.createTime) + ".html";
	}
	
	
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	public String getGuid() {
		return guid;
	}
	
	public String getCreateTimeText() {
		return createTimeText;
	}
	
	public void setCreateTimeText(String createTimeText) {
		this.createTimeText = createTimeText;
	}
	
	/**
	 * 保存该测试报告的详情,使用JSON串保存
	 * @param detailsJson
	 */
	public void setDetailsJson(String detailsJson) {
		this.detailsJson = detailsJson;
	}
	
	@JSON(serialize=false)
	public String getDetailsJson() {
		return detailsJson;
	}
		
	public String getSetName() {
		return setName;
	}
	public void setSetName(String setName) {
		this.setName = setName;
	}
	public String getCreateUserName() {
		if (user == null) {
			return this.createUserName;
		}
		return this.user.getRealName();
	}
	
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	@JSON(serialize=false)
	public Set<TestResult> getTrs() {
		return trs;
	}
	public void setTrs(Set<TestResult> trs) {
		this.trs = trs;
	}
	public Integer getReportId() {
		return reportId;
	}
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
	
	////////////////////////////////////
	
	/**
	 * 统计当前状态下各测试结果
	 */
	public void countSceneNum () {
		this.sceneNum = this.getTrs().size();
		this.successNum = 0;
		this.failNum = 0;
		this.stopNum = 0;
		for(TestResult tr:this.getTrs()){
			switch (tr.getRunStatus()) {
			case "0":
				this.successNum++;
				break;
			case "1":
				this.failNum++;
				break;
			case "2":
				this.stopNum++;
				break;
			}
		}
	}
	
	public void setSceneNum(Integer sceneNum) {
		this.sceneNum = sceneNum;
	}
	
	public Integer getSceneNum() {
		return sceneNum;
	}

	public Integer getSuccessNum() {
		return successNum;
	}

	public Integer getFailNum() {
		return failNum;
	}

	public Integer getStopNum() {
		return stopNum;
	}

	///////////////////////////////////
	
	
	public String getFinishFlag() {
		return finishFlag;
	}
	public void setFinishFlag(String finishFlag) {
		this.finishFlag = finishFlag;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}
	
	@JSON(serialize=false)
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return "TestReport [reportId=" + reportId + ", testMode=" + testMode
				+ ", sceneNum=" + sceneNum + ", successNum=" + successNum
				+ ", failNum=" + failNum + ", stopNum=" + stopNum
				+ ", finishFlag=" + finishFlag + ", createTime=" + createTime
				+ ", finishTime=" + finishTime + ", user=" + user + ", mark="
				+ mark + ", createUserName=" + createUserName + ", setName="
				+ setName + ", createTimeText=" + createTimeText + "]";
	}
	
	
}
