package yi.master.business.web.bean;

import java.io.Serializable;

import org.apache.struts2.json.annotations.JSON;

import yi.master.business.user.bean.User;

/**
 * 测试配置
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer configId;
	/**
	 * 所属用户，如果该配置属于测试集则该值为null
	 */
	private User user;
	/**
	 * 各浏览器的webdriver的路径，如果没有设置到环境变量
	 */
	private String chromeDriverPath = "";
	private String firefoxDriverPath = "";
	private String operaDriverPath = "";
	private String ieDriverPath = "";
	
	/**
	 * 各浏览器的执行bin路径，如果没有默认安装并且没有配置环境变量的话
	 */
	private String chromeExcutePath = "";
	private String firefoxExcutePath = "";
	private String operaExcutePath = "";
	private String ieExcutePath = "";
	
	/**
	 * 是否需要最大化浏览器窗口
	 */
	private String windowMaximize = "1";
	/**
	 * 页面加载的超时时间
	 */
	private Integer pageLoadTimeout = 30;
	/**
	 * 隐式等待元素的超时时间
	 */
	private Integer implicitlyWaitTimeout = 30;	
	
	public WebTestConfig(Integer configId, User user, String chromeDriverPath, String firefoxDriverPath,
			String operaDriverPath, String ieDriverPath, String windowMaximize, Integer pageLoadTimeout,
			Integer implicitlyWaitTimeout) {
		super();
		this.configId = configId;
		this.user = user;
		this.chromeDriverPath = chromeDriverPath;
		this.firefoxDriverPath = firefoxDriverPath;
		this.operaDriverPath = operaDriverPath;
		this.ieDriverPath = ieDriverPath;
		this.windowMaximize = windowMaximize;
		this.pageLoadTimeout = pageLoadTimeout;
		this.implicitlyWaitTimeout = implicitlyWaitTimeout;
	}

	public WebTestConfig() {
		super();
		
	}

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	@JSON(serialize=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getFirefoxDriverPath() {
		return firefoxDriverPath;
	}

	public void setFirefoxDriverPath(String firefoxDriverPath) {
		this.firefoxDriverPath = firefoxDriverPath;
	}

	public String getOperaDriverPath() {
		return operaDriverPath;
	}

	public void setOperaDriverPath(String operaDriverPath) {
		this.operaDriverPath = operaDriverPath;
	}

	public String getIeDriverPath() {
		return ieDriverPath;
	}

	public void setIeDriverPath(String ieDriverPath) {
		this.ieDriverPath = ieDriverPath;
	}

	public String getWindowMaximize() {
		return windowMaximize;
	}

	public void setWindowMaximize(String windowMaximize) {
		this.windowMaximize = windowMaximize;
	}

	public Integer getPageLoadTimeout() {
		return pageLoadTimeout;
	}

	public void setPageLoadTimeout(Integer pageLoadTimeout) {
		this.pageLoadTimeout = pageLoadTimeout;
	}

	public Integer getImplicitlyWaitTimeout() {
		return implicitlyWaitTimeout;
	}

	public void setImplicitlyWaitTimeout(Integer implicitlyWaitTimeout) {
		this.implicitlyWaitTimeout = implicitlyWaitTimeout;
	}

	public String getChromeExcutePath() {
		return chromeExcutePath;
	}

	public void setChromeExcutePath(String chromeExcutePath) {
		this.chromeExcutePath = chromeExcutePath;
	}

	public String getFirefoxExcutePath() {
		return firefoxExcutePath;
	}

	public void setFirefoxExcutePath(String firefoxExcutePath) {
		this.firefoxExcutePath = firefoxExcutePath;
	}

	public String getOperaExcutePath() {
		return operaExcutePath;
	}

	public void setOperaExcutePath(String operaExcutePath) {
		this.operaExcutePath = operaExcutePath;
	}

	public String getIeExcutePath() {
		return ieExcutePath;
	}

	public void setIeExcutePath(String ieExcutePath) {
		this.ieExcutePath = ieExcutePath;
	}

	@Override
	public String toString() {
		return "WebTestConfig [configId=" + configId  + ", chromeDriverPath=" + chromeDriverPath
				+ ", firefoxDriverPath=" + firefoxDriverPath + ", operaDriverPath=" + operaDriverPath
				+ ", ieDriverPath=" + ieDriverPath + ", chromeExcutePath=" + chromeExcutePath + ", firefoxExcutePath="
				+ firefoxExcutePath + ", operaExcutePath=" + operaExcutePath + ", ieExcutePath=" + ieExcutePath
				+ ", windowMaximize=" + windowMaximize + ", pageLoadTimeout=" + pageLoadTimeout
				+ ", implicitlyWaitTimeout=" + implicitlyWaitTimeout + "]";
	}

}	
