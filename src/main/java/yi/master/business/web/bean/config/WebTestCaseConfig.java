package yi.master.business.web.bean.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试用例的一些自定义配置
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestCaseConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 在测试用例中定义的变量，可以在下属所有步骤中自由使用
	 */
	private Map<String, String> caseVariables = new HashMap<String, String>();

	public WebTestCaseConfig(Map<String, String> caseVariables) {
		super();
		this.caseVariables = caseVariables;
	}

	public WebTestCaseConfig() {
		super();
		
	}

	public Map<String, String> getCaseVariables() {
		return caseVariables;
	}

	public void setCaseVariables(Map<String, String> caseVariables) {
		this.caseVariables = caseVariables;
	}

	
}
