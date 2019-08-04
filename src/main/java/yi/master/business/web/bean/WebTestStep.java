package yi.master.business.web.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldNameMapper;
import yi.master.business.testconfig.bean.DataDB;
import yi.master.business.user.bean.User;
import yi.master.business.web.bean.config.WebTestStepConfig;
import yi.master.util.FrameworkUtil;
import yi.master.util.LogModifyUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 测试步骤
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestStep implements Serializable, LogModifyUtil {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer stepId;
	/**
	 * 步骤名称
	 */
	private String stepName;
	/**
	 * 所属测试用例
	 */
	private WebTestCase webTestCase;
	/**
	 * 执行顺序
	 */
	private Integer execSeq;
	/**
	 * 操作类型：关键字驱动
	 */
	private String opType;
	/**
	 * 对应元素对象
	 */
	private WebTestElement element;
	/**
	 * 用例片段：如果本次操作是  执行用例片段
	 */
	private WebTestCase snippetCase;
	/**
	 * 一般是操作或者元素需要用到的值类型：<br>
	 * 组合按键、字符串、全局变量、正则表达式、元素属性、case中或者suite中定义的变量（suite中定义的优先级大于case中定义的）、之前步骤取得的值、数据库取值（数据库id）
	 */
	private String requiredDataType;
	private String requiredDataValue;
	/**
	 * 同requiredDataType
	 */
	private String validateDataType;
	private String validateDataValue;
	/**
	 * 步骤的其他配置，方便扩展
	 */
	private String configJson;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	private User createUser;
	/**
	 * 该步骤出错了或者失败是否需要停止该用例<br>
	 * 0 - 不停止，继续执行下一个步骤<br>
	 * 1 - 停止，并将该用例设置为失败
	 */
	private String errorInterruptFlag = "1";
	private String mark;
	/**
	 * 保存最近10次的修改记录
	 */
	private String modifyLog;
	
	@FieldNameMapper(fieldPath="modifyLog")
	private String modifyLogText;
	/**
	 * 跳过执行标记<br>
	 * 1 - 跳过， 0 - 不跳过
	 */
	private String skipFlag = "0";
	
	private WebTestStepConfig stepConfig;
	
	@FieldNameMapper(ifSearch=false,ifOrder=false)
	private String objectName;
	
	private String requiredDbName;
	private String validateDbName;
	
	/**
	 * 更新配置信息
	 */
	public void updateConfigJson() {
		if (stepConfig != null) {
			configJson = JSONObject.fromObject(stepConfig).toString();
		}
	}
	
	public WebTestStep(Integer stepId, String stepName, WebTestCase webTestCase, Integer execSeq, String opType,
			WebTestElement element, WebTestCase snippetCase, String requiredDataType, String requiredDataValue,
			String validateDataType, String validateDataValue, String configJson, Timestamp createTime, User createUser,
			String errorInterruptFlag, String mark, String modifyLog) {
		super();
		this.stepId = stepId;
		this.stepName = stepName;
		this.webTestCase = webTestCase;
		this.execSeq = execSeq;
		this.opType = opType;
		this.element = element;
		this.snippetCase = snippetCase;
		this.requiredDataType = requiredDataType;
		this.requiredDataValue = requiredDataValue;
		this.validateDataType = validateDataType;
		this.validateDataValue = validateDataValue;
		this.configJson = configJson;
		this.createTime = createTime;
		this.createUser = createUser;
		this.errorInterruptFlag = errorInterruptFlag;
		this.mark = mark;
		this.modifyLog = modifyLog;
	}

	public WebTestStep() {
		super();
		
	}

	public String getObjectName() {
		if (element != null) {
			element = (WebTestElement) FrameworkUtil.getObjectByServiceNameAndId("webTestElementService", element.getElementId());
			return "页面元素-" + element.getElementName();
		}
		if (snippetCase != null) {
			snippetCase = (WebTestCase) FrameworkUtil.getObjectByServiceNameAndId("webTestCaseService", snippetCase.getCaseId());
			return "用例片段-" + snippetCase.getCaseName();
		}
		return "";
	}
	
	public String getRequiredDbName() {
		if (requiredDataType.startsWith("999999")) {
			DataDB db =CacheUtil.getQueryDBById(requiredDataType);
			if (db != null) {
				return db.getDbName() + "[" + db.getDbUrl() + "]";
			} else {
				return "数据源信息不存在";
			}
		}
		return requiredDbName;
	}
	
	public String getValidateDbName() {
		if (validateDataType.startsWith("999999")) {
			DataDB db =CacheUtil.getQueryDBById(validateDataType);
			if (db != null) {
				return db.getDbName() + "[" + db.getDbUrl() + "]";
			} else {
				return "数据源信息不存在";
			}
		}
		return validateDbName;
	}
	
	public void setModifyLogText(String modifyLogText) {
		this.modifyLogText = modifyLogText;
	}
	
	public String getModifyLogText() {
		JSONArray arr = JSONArray.fromObject(getModifyLog());
		if (!arr.isEmpty()) {
			return StringUtils.join(arr.toArray(), "\n\n");
		}
		return "";
	}
	
	public Integer getStepId() {
		return stepId;
	}

	public void setStepId(Integer stepId) {
		this.stepId = stepId;
	}

	public void setStepConfig(WebTestStepConfig stepConfig) {
		this.stepConfig = stepConfig;
	}
	
	@JSON(serialize=false)
	public WebTestStepConfig getStepConfig() {
		if (stepConfig == null) {
			if (StringUtils.isNotBlank(configJson)) {
				stepConfig = (WebTestStepConfig) JSONObject.toBean(JSONObject.fromObject(configJson), WebTestStepConfig.class);
			} else {
				stepConfig = new WebTestStepConfig();
			}
		}
		return stepConfig;
	}
	
	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public WebTestCase getWebTestCase() {
		return webTestCase;
	}

	public void setWebTestCase(WebTestCase webTestCase) {
		this.webTestCase = webTestCase;
	}

	public Integer getExecSeq() {
		return execSeq;
	}

	public void setExecSeq(Integer execSeq) {
		this.execSeq = execSeq;
	}

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public WebTestElement getElement() {
		return element;
	}

	public void setElement(WebTestElement element) {
		this.element = element;
	}

	public WebTestCase getSnippetCase() {
		return snippetCase;
	}

	public void setSnippetCase(WebTestCase snippetCase) {
		this.snippetCase = snippetCase;
	}

	public String getRequiredDataType() {
		return requiredDataType;
	}

	public void setRequiredDataType(String requiredDataType) {
		this.requiredDataType = requiredDataType;
	}

	public String getRequiredDataValue() {
		return requiredDataValue;
	}

	public void setRequiredDataValue(String requiredDataValue) {
		this.requiredDataValue = requiredDataValue;
	}

	public String getValidateDataType() {
		return validateDataType;
	}

	public void setValidateDataType(String validateDataType) {
		this.validateDataType = validateDataType;
	}

	public String getValidateDataValue() {
		return validateDataValue;
	}

	public void setValidateDataValue(String validateDataValue) {
		this.validateDataValue = validateDataValue;
	}

	public String getConfigJson() {
		if (StringUtils.isBlank(configJson) && stepConfig != null) {
			configJson = JSONObject.fromObject(stepConfig).toString();
		}
		return configJson;
	}
	
	public void setConfigJson(String configJson) {
		this.configJson = configJson;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getErrorInterruptFlag() {
		return errorInterruptFlag;
	}

	public void setErrorInterruptFlag(String errorInterruptFlag) {
		this.errorInterruptFlag = errorInterruptFlag;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getModifyLog() {
		if (StringUtils.isBlank(modifyLog)) {
			modifyLog = "[]";
		}
		return modifyLog;
	}

	public void setModifyLog(String modifyLog) {
		this.modifyLog = modifyLog;
	}

	public String getSkipFlag() {
		return skipFlag;
	}
	
	public void setSkipFlag(String skipFlag) {
		this.skipFlag = skipFlag;
	}
	
	@Override
	public String toString() {
		return "WebTestStep [stepId=" + stepId + ", stepName=" + stepName + ", execSeq=" + execSeq + ", opType="
				+ opType + ", requiredDataType=" + requiredDataType + ", requiredDataValue=" + requiredDataValue
				+ ", validateDataType=" + validateDataType + ", validateDataValue=" + validateDataValue
				+ ", configJson=" + configJson + ", createTime=" + createTime + ", errorInterruptFlag="
				+ errorInterruptFlag + ", mark=" + mark + ", modifyLog=" + modifyLog + ", skipFlag=" + skipFlag + "]";
	}

	@Override
	public void logModify(String userName, Object oldObject) {
		
		if (oldObject == null || !(oldObject instanceof WebTestStep)) {
			return;
		}
		WebTestStep oldStep = (WebTestStep) oldObject;
		StringBuilder log = new StringBuilder();
		log.append("用户" + userName + "在" + PracticalUtils.formatDate("yyyy-MM-dd HH:mm:ss", new Date()) + "修改了以下内容:\n");
		boolean modifyFlag = false;
		
		if (!StringUtils.equals(this.stepName, oldStep.getStepName())) {
			modifyFlag = true;
			log.append("步骤名称: [" + oldStep.getStepName() + "] ==> [" + this.stepName + "]\n");
		}
		if (!oldStep.getExecSeq().equals(this.execSeq)) {
			modifyFlag = true;
			log.append("执行顺序: [" + oldStep.getExecSeq() + "] ==> [" + this.execSeq + "]\n");
		}
		if (!StringUtils.equals(oldStep.getOpType(), this.opType)) {
			modifyFlag = true;
			log.append("操作类型: [" + oldStep.getOpType() + "] ==> [" + this.opType + "]\n");
		}
		if (!StringUtils.equals(oldStep.getObjectName(), this.getObjectName())) {
			modifyFlag = true;
			log.append("操作元素/用例片段: [" + oldStep.getObjectName() + "] ==> [" + this.getObjectName() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getRequiredDataType(), this.getRequiredDataType())) {
			modifyFlag = true;
			log.append("请求值类型: [" + oldStep.getRequiredDataType() + "] ==> [" + this.getRequiredDataType() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getRequiredDataValue(), this.getRequiredDataValue())) {
			modifyFlag = true;
			log.append("请求值: [" + oldStep.getRequiredDataValue() + "] ==> [" + this.getRequiredDataValue() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getValidateDataType(), this.getValidateDataType())) {
			modifyFlag = true;
			log.append("验证值类型: [" + oldStep.getValidateDataType() + "] ==> [" + this.getValidateDataType() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getValidateDataValue(), this.getValidateDataValue())) {
			modifyFlag = true;
			log.append("验证值: [" + oldStep.getValidateDataValue() + "] ==> [" + this.getValidateDataValue() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getSkipFlag(), this.getSkipFlag())) {
			modifyFlag = true;
			log.append("是否跳过: [" + oldStep.getSkipFlag() + "] ==> [" + this.getSkipFlag() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getErrorInterruptFlag(), this.getErrorInterruptFlag())) {
			modifyFlag = true;
			log.append("是否出错中断: [" + oldStep.getErrorInterruptFlag() + "] ==> [" + this.getErrorInterruptFlag() + "]\n");
		}
		if (!StringUtils.equals(oldStep.getMark(), this.getMark())) {
			modifyFlag = true;
			log.append("备注信息: [" + oldStep.getMark() + "] ==> [" + this.getMark() + "]\n");
		}
		
		if (modifyFlag) {
			JSONArray modifyLogArr = JSONArray.fromObject(this.getModifyLog());
			if (modifyLogArr.size() >= 10) {
				modifyLogArr.remove(0);
			}
			modifyLogArr.add(log.toString());
			this.modifyLog = modifyLogArr.toString();
		}
	}
	
	
}
