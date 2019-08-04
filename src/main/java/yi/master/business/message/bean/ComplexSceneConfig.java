package yi.master.business.message.bean;

import java.util.HashMap;
import java.util.Map;

public class ComplexSceneConfig {
	
	/**
	 * 对应messageSceneId
	 */
	private Integer messageSceneId;
	/**
	 * 保存变量列表
	 */
	private Map<String, String> saveVariables = new HashMap<String, String>();
	
	/**
	 * 使用变量列表
	 */
	private Map<String, String> useVariables = new HashMap<String, String>();
	
	/**
	 * 当前测试场景测试不成功(runStatus状态不为0)时的重试次数
	 */
	private Integer retryCount = 0;
	
	/**
	 * 每次重试的间隔时间，默认1000毫秒
	 */
	private Integer intervalTime = 1000;
	
	/**
	 * 如果当前场景执行失败了(runStatus状态不为0)时，如何执行下一步操作
	 * <br>0 - 退出当前组合场景的测试，并标记组合场景测试失败
	 * <br>1 - 继续执行当前组合场景的下一个场景测试，
	 * <br>2 - 直接执行当前组合场景中的最后一个场景
	 * <br>默认为0
	 */
	private String errorExecFlag = "0";
	
	private String systemId;

	public ComplexSceneConfig(Integer messageSceneId,
			Map<String, String> saveVariables,
			Map<String, String> useVariables, Integer retryCount,
			Integer intervalTime, String errorExecFlag) {
		super();
		this.messageSceneId = messageSceneId;
		this.saveVariables = saveVariables;
		this.useVariables = useVariables;
		this.retryCount = retryCount;
		this.intervalTime = intervalTime;
		this.errorExecFlag = errorExecFlag;
	}

	public ComplexSceneConfig (Integer messageSceneId) {
		super();
		this.messageSceneId = messageSceneId;
	}
	
	public ComplexSceneConfig() {
		super();
	}
	

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	
	public String getSystemId() {
		return systemId;
	}
	
	public Integer getMessageSceneId() {
		return messageSceneId;
	}

	public void setMessageSceneId(Integer messageSceneId) {
		this.messageSceneId = messageSceneId;
	}

	public Map<String, String> getSaveVariables() {
		return saveVariables;
	}

	public void setSaveVariables(Map<String, String> saveVariables) {
		this.saveVariables = saveVariables;
	}

	public Map<String, String> getUseVariables() {
		return useVariables;
	}

	public void setUseVariables(Map<String, String> useVariables) {
		this.useVariables = useVariables;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public Integer getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(Integer intervalTime) {
		this.intervalTime = intervalTime;
	}

	public String getErrorExecFlag() {
		return errorExecFlag;
	}

	public void setErrorExecFlag(String errorExecFlag) {
		this.errorExecFlag = errorExecFlag;
	}

	@Override
	public String toString() {
		return "ComplexSceneConfig [messageSceneId=" + messageSceneId
				+ ", saveVariables=" + saveVariables + ", useVariables="
				+ useVariables + ", retryCount=" + retryCount
				+ ", intervalTime=" + intervalTime + ", errorExecFlag="
				+ errorExecFlag + "]";
	}
}
