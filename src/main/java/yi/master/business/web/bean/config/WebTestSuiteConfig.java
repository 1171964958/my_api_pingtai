package yi.master.business.web.bean.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试集的一些自定义配置
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestSuiteConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 测试集中定义变量，可在下属的测试用例和测试步骤中随意使用，且他的优先级大于测试用例定义的变量
	 */
	private Map<String, String> suiteVariables = new HashMap<String, String>();

	public WebTestSuiteConfig(Map<String, String> suiteVariables) {
		super();
		this.suiteVariables = suiteVariables;
	}

	public WebTestSuiteConfig() {
		super();
		
	}

	public Map<String, String> getSuiteVariables() {
		return suiteVariables;
	}

	public void setSuiteVariables(Map<String, String> suiteVariables) {
		this.suiteVariables = suiteVariables;
	}
}
