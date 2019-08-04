package yi.master.business.log.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.system.bean.OperationInterface;
import yi.master.business.user.bean.User;

/**
 * 用户操作记录信息表
 * @author xuwangcheng
 * @version 2018.4.26,1.0.0
 * 
 */
public class LogRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer recordId;
	/**
	 * 调用接口用户,对于调用api的为null
	 */
	private User user;
	/**
	 * 关联操作接口对象,可能为null
	 */
	private OperationInterface opInterface;
	/**
	 * 调用url
	 */
	private String callUrl;
	/**
	 * 调用时间
	 */
	private Timestamp opTime;
	/**
	 * 拦截状态<br>
	 * 0 - 正常<br>
	 * 1 - 无权限<br>
	 * 2 - 未登录<br>
	 * 3 - 放行<br>
	 * 4 - token不正确<br>
	 * 5 - 禁用接口<br>
	 * 6 - 系统错误
	 */
	@FieldRealSearch(names = {"正常", "无权限", "未登录", "放行", "token不正确", "禁用接口", "系统错误", "mock出错", "接口不存在"}, values = {"0", "1", "2", "3", "4", "5", "6", "7", "8"})
	@CustomConditionSetting(operator="=")
	private String interceptStatus;
	/**
	 * 调用类型<br>
	 * 0 - 用户调用<br>
	 * 1 - 外部调用api接口<br>
	 * 2 - 内部自调
	 */
	@FieldRealSearch(names = {"用户调用", "外部API", "内部自调", "接口MOCK"}, values = {"0", "1", "2", "3"})
	@CustomConditionSetting(operator="=")
	private String callType;
	/**
	 * 用户ip
	 */
	private String userHost;
	/**
	 * 浏览器信息
	 */
	private String browserAgent;
	/**
	 * 后台验证耗时
	 */
	private Integer validateTime;
	/**
	 * 后台执行耗时
	 */
	private Integer executeTime;
	/**
	 * 入参
	 */
	private String requestParams;
	/**
	 * 出参
	 */
	private String responseParams;
	/**
	 * 备注
	 */
	private String mark;
	
	@FieldNameMapper(fieldPath="user.realName")
	private String callUserName;
	
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	@FieldNameMapper(fieldPath="opTime")
	private String opTimeText;
	
	
	public void setCallUserName(String callUserName) {
		this.callUserName = callUserName;
	}
	
	public String getCallUserName() {
		return callUserName;
	}
	
	public void setOpTimeText(String opTimeText) {
		this.opTimeText = opTimeText;
	}
	
	public String getOpTimeText() {
		return opTimeText;
	}
	
	public void setCallUrl(String callUrl) {
		this.callUrl = callUrl;
	}
	
	public String getCallUrl() {
		return callUrl;
	}

	public Integer getRecordId() {
		return recordId;
	}
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public OperationInterface getOpInterface() {
		return opInterface;
	}
	public void setOpInterface(OperationInterface opInterface) {
		this.opInterface = opInterface;
	}
	
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getOpTime() {
		return opTime;
	}
	public void setOpTime(Timestamp opTime) {
		this.opTime = opTime;
	}
	public String getInterceptStatus() {
		return interceptStatus;
	}
	public void setInterceptStatus(String interceptStatus) {
		this.interceptStatus = interceptStatus;
	}
	public String getCallType() {
		return callType;
	}
	public void setCallType(String callType) {
		this.callType = callType;
	}
	public String getUserHost() {
		return userHost;
	}
	public void setUserHost(String userHost) {
		this.userHost = userHost;
	}
	public String getBrowserAgent() {
		return browserAgent;
	}
	public void setBrowserAgent(String browserAgent) {
		this.browserAgent = browserAgent;
	}
	public Integer getValidateTime() {
		return validateTime;
	}
	public void setValidateTime(Integer validateTime) {
		this.validateTime = validateTime;
	}
	public Integer getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Integer executeTime) {
		this.executeTime = executeTime;
	}
	
	public String getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}
	
	public String getResponseParams() {
		return responseParams;
	}
	public void setResponseParams(String responseParams) {
		this.responseParams = responseParams;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return "LogRecord [recordId=" + recordId + ", user=" + user
				+ ", opInterface=" + opInterface + ", callUrl=" + callUrl
				+ ", opTime=" + opTime + ", interceptStatus=" + interceptStatus
				+ ", callType=" + callType + ", userHost=" + userHost
				+ ", browserAgent=" + browserAgent + ", validateTime="
				+ validateTime + ", executeTime=" + executeTime
				+ ", requestParams=" + requestParams + ", responseParams=\"\"" + ", mark=" + mark + "]";
	}
}
