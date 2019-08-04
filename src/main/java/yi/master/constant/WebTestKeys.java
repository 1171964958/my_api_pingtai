package yi.master.constant;

/**
 * web自动化相关常量类
 * @author xuwangcheng
 * @version 2018.08.23
 *
 */
public class WebTestKeys {
	
	/****元素类型***/
	public static final String WEB_ELEMENT_TYPE_WEBSITE = "website";//网站
	public static final String WEB_ELEMENT_TYPE_MODULE = "module";//模块
	public static final String WEB_ELEMENT_TYPE_FEATURE = "feature";//功能
	public static final String WEB_ELEMENT_TYPE_PAGE = "page";//页面
	public static final String WEB_ELEMENT_TYPE_FRAME = "frame";//页面frame/iframe
	public static final String WEB_ELEMENT_TYPE_TAG = "tag";//节点
	public static final String WEB_ELEMENT_TYPE_URL = "url";//url地址
	
	/***定位元素的BY类型****/
	public static final String WEB_ELEMENT_BY_TYPE_CLASSNAME = "ClassName";
	public static final String WEB_ELEMENT_BY_TYPE_ID = "Id";
	public static final String WEB_ELEMENT_BY_TYPE_LINKTEXT = "LinkText";//链接文件
	public static final String WEB_ELEMENT_BY_TYPE_NAME = "Name";//name属性
	public static final String WEB_ELEMENT_BY_TYPE_TAGNAME = "TagName";//标签名
	public static final String WEB_ELEMENT_BY_TYPE_XPATH = "XPath";
	public static final String WEB_ELEMENT_BY_TYPE_PARTIALLINKTEXT = "PartialLinkText";//局部文件匹配
	public static final String WEB_ELEMENT_BY_TYPE_CSSSELECTOR = "CssSelector";	//css选择器
	public static final String WEB_ELEMENT_BY_TYPE_TITLE = "title";//获取当前窗口的标题
	public static final String WEB_ELEMENT_BY_TYPE_ALERT = "alert";//获取当前页面的弹出框
	public static final String WEB_ELEMENT_BY_TYPE_CURRENT_URL = "currentUrl";//获取当前的浏览器地址栏地址
	
	/***用例类型**/
	public static final String WEB_CASE_TYPE_COMMON = "common";//普通用例
	public static final String WEB_CASE_TYPE_SNIPPET = "snippet";//用例片段
	
	/***浏览器类型**/
	public static final String WEB_BROSWER_TYPE_CHROME = "chrome";
	public static final String WEB_BROSWER_TYPE_IE = "ie";
	public static final String WEB_BROSWER_TYPE_FIREFOX = "firefox";
	public static final String WEB_BROSWER_TYPE_OPERA = "opera";
	
	
	/***步骤执行关键字**/
	public static final String WEB_STEP_OP_TYPE_OPEN = "open";//打开浏览器地址
	public static final String WEB_STEP_OP_TYPE_CHECK = "check";//检查
	public static final String WEB_STEP_OP_TYPE_ACTION = "action";//执行-用例片段
	public static final String WEB_STEP_OP_TYPE_SCRIPT = "script";//执行js脚本
	public static final String WEB_STEP_OP_TYPE_INPUT = "input";//输入
	public static final String WEB_STEP_OP_TYPE_SAVE_VALUE = "save";//获取值并保存下来
	public static final String WEB_STEP_OP_TYPE_CLICK = "click";//点击
	public static final String WEB_STEP_OP_TYPE_HOVER = "hover";//移动到指定元素上并悬停
	public static final String WEB_STEP_OP_TYPE_ALERT_CANCEL = "alertCancel";//取消-确认框
	public static final String WEB_STEP_OP_TYPE_ALERT_CONFIRM = "alertConfirm";//确认框-确认
	public static final String WEB_STEP_OP_TYPE_ALERT_CLOSE = "alertClose";//关闭alert
	public static final String WEB_STEP_OP_TYPE_ALERT_INPUT = "alertInput";//输入到对话框，默认会确认
	public static final String WEB_STEP_OP_TYPE_UPLOAD_FILE = "upload";//上传文件
	public static final String WEB_STPE_OP_TYPE_SLIDE = "slide";//滑动
	
	/**取值关键字**/
	public static final String WEB_STEP_DATA_TYPE_KEYBOARD = "keyboard";//键盘组合按键
	public static final String WEB_STEP_DATA_TYPE_STRING = "string";//普通的字符串
	public static final String WEB_STEP_DATA_TYPE_GLOBAL_VARIABLE = "gloablVariable";//全局变量
	public static final String WEB_STEP_DATA_TYPE_REGEXP = "regexp";//正则表达式
	public static final String WEB_STEP_DATA_TYPE_ELEMENT_ATTRIBUTE = "attribute";//元素的属性
	public static final String WEB_STEP_DATA_TYPE_SAVE_VARIABLE = "saveVariable";//测试变量
	public static final String WEB_STEP_DATA_TYPE_DB_SQL = "dbSql";//数据库取值，该值应该为数据库id
	
	
	
	
	
}
