package yi.master.business.web.bean.config;

import java.io.Serializable;

/**
 * 测试步骤的一些自定义配置
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestStepConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 给当前打开的窗口或者标签页起一个名称：接下来的步骤中的页面都会被绑定到该窗口<br>
	 * 再多窗口的时候定位元素，会根据元素所在页面的所属窗口来切换<br>
	 * 如果一个窗口或者标签页没有被命名，在切换到另外的窗口的时候，该窗口会被关闭
	 */
	private String windowName;
	/**
	 * 在操作步骤为save的时候，在获取到值是，会已该名称保存，如果该值未填写，系统则会自动命名一个不重复的key
	 */
	private String saveVariableName;
	/**
	 * 是否截图
	 */
	private boolean screenshot = false;
	/**
	 * 执行该步骤前等待时间，秒
	 */
	private int beforeExecWaitTime = 0;
	/**
	 * 执行该步骤之后等待时间,秒
	 */
	private int afterExecWaitTime = 0;
	/**
	 * 是否清除浏览器缓存
	 */
	private boolean clearCache = false;
	/**
	 * 是否清楚文本内容
	 */
	private boolean clearText = true;
	/**
	 * 如果该该步骤为用例片段，则循环该用例片段多少次？
	 */
	private int snippetLoopCount = 1;
	/**
	 * 用例片段循环执行时退出的条件，默认为执行失败时退出（fail）<br>
	 * 可选：执行成功时退出-success,执行完指定次数才会退出，不论每次执行是否成功或者失败-whether
	 */
	private String snippetLoopExitCondition = "fail";
	/**
	 * 该步骤是否为控制流程，如果为true，则根据该步骤执行的结果来决定执行下个步骤还是下下一个步骤<br>
	 * 对于check步骤，在验证正确的时候，执行下一个步骤，验证错误的时候执行下下个步骤<br>
	 * 对于其他步骤，在执行成功的时候（没有报错）执行下一个步骤，执行失败的时候执行下下个步骤
	 */
	private boolean controlCondition = false;
	
	public WebTestStepConfig(String windowName, String saveVariableName, boolean screenshot, int beforeExecWaitTime,
			int afterExecWaitTime, boolean clearCache, boolean clearText, int snippetLoopCount,
			String snippetLoopExitCondition, boolean controlCondition) {
		super();
		this.windowName = windowName;
		this.saveVariableName = saveVariableName;
		this.screenshot = screenshot;
		this.beforeExecWaitTime = beforeExecWaitTime;
		this.afterExecWaitTime = afterExecWaitTime;
		this.clearCache = clearCache;
		this.clearText = clearText;
		this.snippetLoopCount = snippetLoopCount;
		this.snippetLoopExitCondition = snippetLoopExitCondition;
		this.controlCondition = controlCondition;
	}
	public WebTestStepConfig() {
		super();
		
	}
	public String getWindowName() {
		return windowName;
	}
	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}
	public String getSaveVariableName() {
		return saveVariableName;
	}
	public void setSaveVariableName(String saveVariableName) {
		this.saveVariableName = saveVariableName;
	}
	public boolean isScreenshot() {
		return screenshot;
	}
	public void setScreenshot(boolean screenshot) {
		this.screenshot = screenshot;
	}
	public int getBeforeExecWaitTime() {
		return beforeExecWaitTime;
	}
	public void setBeforeExecWaitTime(int beforeExecWaitTime) {
		this.beforeExecWaitTime = beforeExecWaitTime;
	}
	public int getAfterExecWaitTime() {
		return afterExecWaitTime;
	}
	public void setAfterExecWaitTime(int afterExecWaitTime) {
		this.afterExecWaitTime = afterExecWaitTime;
	}
	public boolean isClearCache() {
		return clearCache;
	}
	public void setClearCache(boolean clearCache) {
		this.clearCache = clearCache;
	}
	public boolean isClearText() {
		return clearText;
	}
	public void setClearText(boolean clearText) {
		this.clearText = clearText;
	}
	public int getSnippetLoopCount() {
		return snippetLoopCount;
	}
	public void setSnippetLoopCount(int snippetLoopCount) {
		this.snippetLoopCount = snippetLoopCount;
	}
	public String getSnippetLoopExitCondition() {
		return snippetLoopExitCondition;
	}
	public void setSnippetLoopExitCondition(String snippetLoopExitCondition) {
		this.snippetLoopExitCondition = snippetLoopExitCondition;
	}
	public boolean isControlCondition() {
		return controlCondition;
	}
	public void setControlCondition(boolean controlCondition) {
		this.controlCondition = controlCondition;
	}
	
	
	
}
