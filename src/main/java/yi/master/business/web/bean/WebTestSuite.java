package yi.master.business.web.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.business.user.bean.User;
import yi.master.business.web.bean.config.WebTestSuiteConfig;
import yi.master.constant.WebTestKeys;

import net.sf.json.JSONObject;

/**
 * 测试用例集、测试组件
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestSuite implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer suiteId;
	/**
	 * 测试集名称
	 */
	private String suiteName;
	/**
	 * 浏览器类型，覆盖下属case中定义的类型
	 */
	private String browserType = WebTestKeys.WEB_BROSWER_TYPE_CHROME;
	/**
	 * 测试配置<br>
	 * 创建测试集时，先创建一份初始的配置
	 */
	private WebTestConfig testConfig;
	private User createUser;
	private Timestamp createTime;
	private String mark;
	/**
	 * 运行次数
	 */
	@CustomConditionSetting(operator=">")
	private Integer runCount = 0;
	/**
	 * 最近一次测试
	 */
	private Timestamp lastRunTime;
	/**
	 * 其他配置内容
	 */
	@CustomConditionSetting(conditionType="")
	private String configJson = "{}";
	
	private WebTestSuiteConfig suiteConfig;
	
	@FieldNameMapper(fieldPath="size(suiteCaseComps)",ifSearch=false)
	@CustomConditionSetting(conditionType="")
	private Integer caseNum;
	
	private Set<WebSuiteCaseComp> suiteCaseComps = new HashSet<WebSuiteCaseComp>();
	
	
	/*****************高级查询有关***********************/
	@FieldNameMapper(fieldPath="createUser.realName")
	private String createUserName;
	
	@FieldNameMapper(fieldPath="lastRunTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String lastRunTimeText;
	
	@FieldNameMapper(fieldPath="createTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String createTimeText;
	/*****************高级查询有关***********************/
	
	/**
	 * 更新配置信息
	 */
	public void updateConfigJson() {
		if (suiteConfig != null) {
			configJson = JSONObject.fromObject(suiteConfig).toString();
		}
	}
	
	public WebTestSuite(Integer suiteId, String suiteName, String browserType, WebTestConfig testConfig,
			User createUser, Timestamp createTime, String mark, Integer runCount, Timestamp lastRunTime,
			String configJson) {
		super();
		this.suiteId = suiteId;
		this.suiteName = suiteName;
		this.browserType = browserType;
		this.testConfig = testConfig;
		this.createUser = createUser;
		this.createTime = createTime;
		this.mark = mark;
		this.runCount = runCount;
		this.lastRunTime = lastRunTime;
		this.configJson = configJson;
	}

	public WebTestSuite() {
		super();
		
	}

	public void setSuiteCaseComps(Set<WebSuiteCaseComp> suiteCaseComps) {
		this.suiteCaseComps = suiteCaseComps;
	}
	
	@JSON(serialize=false)
	public Set<WebSuiteCaseComp> getSuiteCaseComps() {
		return suiteCaseComps;
	}
	
	public void setSuiteConfig(WebTestSuiteConfig suiteConfig) {
		this.suiteConfig = suiteConfig;
	}
	
	@JSON(serialize=false)
	public WebTestSuiteConfig getSuiteConfig() {
		if (suiteConfig == null) {
			if (StringUtils.isNotBlank(configJson)) {
				suiteConfig = (WebTestSuiteConfig) JSONObject.toBean(JSONObject.fromObject(configJson), WebTestSuiteConfig.class);
			} else {
				suiteConfig = new WebTestSuiteConfig();
			}
		}
		return suiteConfig;
	}
	
	public Integer getCaseNum() {
		return this.suiteCaseComps.size();
	}
	
	public Integer getSuiteId() {
		return suiteId;
	}

	public void setSuiteId(Integer suiteId) {
		this.suiteId = suiteId;
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

	public WebTestConfig getTestConfig() {
		return testConfig;
	}

	public void setTestConfig(WebTestConfig testConfig) {
		this.testConfig = testConfig;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Integer getRunCount() {
		return runCount;
	}

	public void setRunCount(Integer runCount) {
		this.runCount = runCount;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(Timestamp lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	public String getConfigJson() {
		if (StringUtils.isBlank(configJson) && suiteConfig != null) {
			configJson = JSONObject.fromObject(suiteConfig).toString();
		}
		return configJson;
	}

	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}

	@Override
	public String toString() {
		return "WebTestSuite [suiteId=" + suiteId + ", suiteName=" + suiteName + ", browserType=" + browserType
				+ ", createTime=" + createTime + ", mark=" + mark + ", runCount=" + runCount + ", lastRunTime="
				+ lastRunTime + ", configJson=" + configJson + "]";
	}
}
