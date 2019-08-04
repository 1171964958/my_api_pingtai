package yi.master.business.web.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.user.bean.User;
import yi.master.business.web.bean.config.WebTestCaseConfig;

import net.sf.json.JSONObject;

/**
 * 测试用例
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestCase implements Serializable,Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(WebTestCase.class);
	
	private Integer caseId;
	/**
	 * 用例名称
	 */
	private String caseName;
	/**
	 * 浏览器类型，会被Suite中的browserType覆盖
	 */
	private String browserType;
	/**
	 * 用例类型：common(普通的用例)和snippet(用例片段)
	 */
	@FieldRealSearch(names = {"通用", "用例片段"}, values = {"common", "snippet"})
	@CustomConditionSetting(operator="=")
	private String caseType;
	private Timestamp createTime;
	/**
	 * 配置json
	 */
	private String configJson;
	private User createUser;
	private String mark;
	/**
	 * success/fail=1/0
	 */
	@FieldRealSearch(names = {"成功", "失败"}, values = {"1", "0"})
	@CustomConditionSetting(operator="=")
	private String lastRunStatus = "1";
	
	private Timestamp lastRunTime;
	
	/**
	 * 当前测试用例的状态，0 - 禁用， 1 - 可用<br>
	 * 被禁用的测试用例在所有的测试集中都不会被执行,只针对于普通的测试用例，不影响用例片段的执行
	 */
	@FieldRealSearch(names = {"可用", "禁用"}, values = {"1", "0"})
	@CustomConditionSetting(operator="=")
	private String status = "1";
	
	private Set<WebTestStep> steps = new HashSet<WebTestStep>();
	
	private WebTestCaseConfig caseConfig;
	
	@FieldNameMapper(fieldPath="size(steps)",ifSearch=false)
	@CustomConditionSetting(conditionType="")
	private Integer stepNum;
	
	/************高级查询相关*******************/
	@FieldNameMapper(fieldPath="createTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String createTimeText;
	
	@FieldNameMapper(fieldPath="createUser.realName")
	private String createUserName;
	
	@FieldNameMapper(fieldPath="lastRunTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String lastRunTimeText;
	
	/*******************************/
	
	/******************测试集用例相关*********************/
	private String groupName;
	private Integer execSeq;
	private String skipFlag;
	private Integer compId;
	/****************************************/
	
	private Set<WebSuiteCaseComp> suiteCaseComps = new HashSet<WebSuiteCaseComp>();
	
	/**
	 * 更新配置信息
	 */
	public void updateConfigJson() {
		if (caseConfig != null) {
			configJson = JSONObject.fromObject(caseConfig).toString();
		}
	}
	
	public WebTestCase(Integer caseId, String caseName, String browserType, String caseType, Timestamp createTime,
			String configJson, User createUser, String mark, String lastRunStatus, Timestamp lastRunTime) {
		super();
		this.caseId = caseId;
		this.caseName = caseName;
		this.browserType = browserType;
		this.caseType = caseType;
		this.createTime = createTime;
		this.configJson = configJson;
		this.createUser = createUser;
		this.mark = mark;
		this.lastRunStatus = lastRunStatus;
		this.lastRunTime = lastRunTime;
	}

	public WebTestCase() {
		super();
		
	}
	
	
	public void setSuiteCaseComps(Set<WebSuiteCaseComp> suiteCaseComps) {
		this.suiteCaseComps = suiteCaseComps;
	}
	
	@JSON(serialize=false)
	public Set<WebSuiteCaseComp> getSuiteCaseComps() {
		return suiteCaseComps;
	}
	
	public void setCompId(Integer compId) {
		this.compId = compId;
	}
	
	public Integer getCompId() {
		return compId;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getExecSeq() {
		return execSeq;
	}

	public void setExecSeq(Integer execSeq) {
		this.execSeq = execSeq;
	}

	public String getSkipFlag() {
		return skipFlag;
	}

	public void setSkipFlag(String skipFlag) {
		this.skipFlag = skipFlag;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public Integer getStepNum() {
		return this.steps.size();
	}

	public void setCaseConfig(WebTestCaseConfig caseConfig) {
		this.caseConfig = caseConfig;
	}
	
	@JSON(serialize=false)
	public WebTestCaseConfig getCaseConfig() {
		if (caseConfig == null) {
			if (StringUtils.isNotBlank(configJson)) {
				caseConfig = (WebTestCaseConfig) JSONObject.toBean(JSONObject.fromObject(configJson), WebTestCaseConfig.class);
			} else {
				caseConfig = new WebTestCaseConfig();
			}
		}
		return caseConfig;
	}
	
	public void setSteps(Set<WebTestStep> steps) {
		this.steps = steps;
	}
	
	@JSON(serialize=false)
	public Set<WebTestStep> getSteps() {
		return steps;
	}
	
	public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
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

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getConfigJson() {
		if (StringUtils.isBlank(configJson) && caseConfig != null) {
			configJson = JSONObject.fromObject(caseConfig).toString();
		}
		return configJson;
	}

	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(String lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(Timestamp lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	@Override
	public String toString() {
		return "WebTestCase [caseId=" + caseId + ", caseName=" + caseName + ", browserType=" + browserType
				+ ", caseType=" + caseType + ", createTime=" + createTime + ", configJson=" + configJson + ", mark="
				+ mark + ", lastRunStatus=" + lastRunStatus + ", lastRunTime=" + lastRunTime + "]";
	}
	
	@Override
	public Object clone() {
		
		Object o = null;
		try {
			o = super.clone();
		} catch (Exception e) {
			
			LOGGER.warn("clone exceptin!", e);
		}
		return o;
	}
	
}
