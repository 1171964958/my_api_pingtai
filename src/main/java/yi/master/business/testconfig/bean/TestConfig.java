package yi.master.business.testconfig.bean;

import java.io.Serializable;
import java.util.Set;

import org.apache.log4j.Logger;

import yi.master.util.PracticalUtils;

/**
 * 接口自动化测试配置
 * @author xuwangcheng
 * @version 1.0.0.0,2017.07.09
 *
 */
public class TestConfig implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(TestConfig.class);
	
	private Integer configId;
	
	/**
	 * 所属用户id,若为0则为全局配置
	 */
	private Integer userId;
	
	/**
	 * 请求地址选择
	 * <br>0 - 默认 ,按照优先级选择：报文中设置的requestUrl > 接口中设置的requestMockUrl > 接口中设置的requestRealUrl
	 * <br>1 - 选择接口中的requestMockUrl 没有设置则不测试
	 * <br>2 - 选择接口中设置的requestRealUrl
	 */
	@Deprecated
	private String requestUrlFlag;
	
	/**
	 * 连接超时时间ms
	 * 
	 */
	private Integer connectTimeOut;
	
	/**
	 * 读取返回超时时间ms
	 */
	private Integer readTimeOut;
	
	/**
	 * 请求方式<br>
	 * 已废弃<br>
	 * 可以使用Message中的callParameter参数来设置
	 * 
	 */
	@Deprecated
	private String httpMethodFlag;
	
	/**
	 * 测试集用配置
	 * <br>请求地址全局配置
	 */
	private String customRequestUrl;
	
	/**
	 * 是否在测试之前检查测试数据<br>
	 * 在系统进行定时任务时该配置是无效的<br>
	 * 0-需要检查<br>
	 * 1-不需要检查
	 */
	private String checkDataFlag;
	
	/**
	 * 测试模式<br>
	 *  0-并行  1-串行
	 *
	 */
	private String runType;
	/**
	 * 重试次数
	 */
	private Integer retryCount;
	
	/**
	 * 全局配置无效,只针对测试集的运行时配置<br>
	 * 配置当前生效的测试环境,在测试测试集时，如果测试场景包含多个测试环境，
	 * 已此配置中为准,默认配置或者此值为空时会执行该场景的所有测试环境
	 */
	private String systems;
	/**
	 * 置顶的测试场景，置顶的场景采用串行方式按照顺序运行
	 */
	private String topScenes;
	
	/**
	 * 邮件通知时：收件人地址列表，逗号分隔
	 */
	private String mailReceiveAddress;
	
	/**
	 * 邮件通知时：抄送人地址列表,逗号分隔
	 */
	private String mailCopyAddress;
	
	 /**
     * 所属测试环境
     */
    private Set<BusinessSystem> businessSystems;
	
	public TestConfig(Integer userId, String requestUrlFlag,
			Integer connectTimeOut, Integer readTimeOut, String httpMethodFlag,
			String customRequestUrl, String checkDataFlag,
			String runType, Integer retryCount) {
		super();
		this.userId = userId;
		this.requestUrlFlag = requestUrlFlag;
		this.connectTimeOut = connectTimeOut;
		this.readTimeOut = readTimeOut;
		this.httpMethodFlag = httpMethodFlag;
		this.customRequestUrl = customRequestUrl;
		this.checkDataFlag = checkDataFlag;
		this.runType = runType;
		this.retryCount = retryCount;
	}

	public TestConfig() {
		super();
		
	}

	public void setTopScenes(String topScenes) {
		this.topScenes = topScenes;
	}
	
	public String getTopScenes() {
		return topScenes;
	}
	
	public Set<BusinessSystem> getBusinessSystems() {
		return PracticalUtils.getSystems(this.systems);
	}
	
	public void setSystems(String systems) {
		this.systems = systems;
	}
	
	public String getSystems() {
		return systems;
	}
	
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}
	
	public Integer getRetryCount() {
		return retryCount;
	}
	
	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getRequestUrlFlag() {
		return requestUrlFlag;
	}

	public void setRequestUrlFlag(String requestUrlFlag) {
		this.requestUrlFlag = requestUrlFlag;
	}

	public Integer getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(Integer connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public Integer getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(Integer readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public String getHttpMethodFlag() {
		return httpMethodFlag;
	}

	public void setHttpMethodFlag(String httpMethodFlag) {
		this.httpMethodFlag = httpMethodFlag;
	}

	

	public String getCustomRequestUrl() {
		return customRequestUrl;
	}

	public void setCustomRequestUrl(String customRequestUrl) {
		this.customRequestUrl = customRequestUrl;
	}

	public String getCheckDataFlag() {
		return checkDataFlag;
	}

	public void setCheckDataFlag(String checkDataFlag) {
		this.checkDataFlag = checkDataFlag;
	}

	public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}
	
	public String getMailReceiveAddress() {
		return mailReceiveAddress;
	}

	public void setMailReceiveAddress(String mailReceiveAddress) {
		this.mailReceiveAddress = mailReceiveAddress;
	}

	public String getMailCopyAddress() {
		return mailCopyAddress;
	}

	public void setMailCopyAddress(String mailCopyAddress) {
		this.mailCopyAddress = mailCopyAddress;
	}

	@Override
	public Object clone() {
		TestConfig config = null;
		try {
			config = (TestConfig) super.clone();
		} catch (Exception e) {
			
			LOGGER.warn("clone exception!", e);
		}
		return config;
	}

	@Override
	public String toString() {
		return "TestConfig [configId=" + configId + ", userId=" + userId + ", connectTimeOut=" + connectTimeOut
				+ ", readTimeOut=" + readTimeOut + ", customRequestUrl=" + customRequestUrl + ", checkDataFlag="
				+ checkDataFlag + ", runType=" + runType + ", retryCount=" + retryCount + ", systems=" + systems
				+ ", topScenes=" + topScenes + ", mailReceiveAddress=" + mailReceiveAddress + ", mailCopyAddress="
				+ mailCopyAddress + "]";
	}
}
