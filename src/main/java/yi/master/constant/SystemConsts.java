package yi.master.constant;




/**
 * 系统相关常量
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */
public class SystemConsts {
	
	/**
	 * 默认admin角色的roleId
	 */
	public static final Integer ADMIN_ROLE_ID = 1;
	
	/**
	 * 默认default角色的roleId
	 */
	public static final Integer DEFAULT_ROLE_ID = 3;
	
	/**
	 * 默认ADMIN账户的用户ID
	 */
	public static final Integer ADMIN_USER_ID = 1;
	
	//特殊参数的id
	//数组中的数组参数对象
	public static final Integer PARAMETER_ARRAY_IN_ARRAY_ID = 2;
	//数组中的Map参数对象
	public static final Integer PARAMETER_MAP_IN_ARRAY_ID = 3;
	//Object对象 对应外层
	public static final Integer PARAMETER_OBJECT_ID = 1;
	
	/**
	 * sessionMap中登录用户key值
	 */
	public static final String SESSION_ATTRIBUTE_LOGIN_USER = "user";
	
	/**
	 * 请求带上此token代表为内部自调用接口，不需要验证权限
	 */
	public static final String REQUEST_ALLOW_TOKEN = "ec189a1731d73dfe16d8f9df16d67187";
	
	
	/**
	 * 管理角色名
	 * 
	 */
	public static final String SYSTEM_ADMINISTRATOR_ROLE_NAME = "admin";
	
	//指定result name
	/**
	 * 用户未登录
	 */
	public static final String RESULT_NOT_LOGIN = "usernotlogin";
	
	/**
	 * 操作接口不可用
	 */
	public static final String RESULT_DISABLE_OP = "opisdisable";
	
	/**
	 * 没有权限
	 */
	public static final String RESULT_NO_POWER = "usernotpower";
	
	/**
	 * 不存在的请求接口
	 */
	public static final String RESULT_NON_EXISTENT_OP = "opnotfound"; 	
	
	/**
	 * 入参验证失败
	 */
	public static final String RESULT_PARAMETER_VALIDATE_FAIL = "parameterValidateFail";
	
	/**
	 * mock接口不存在
	 */
	public static final String RESULT_NON_EXISTENT_MOCK_INTERFACE = "nonMockInterface";
	
	/**
	 * mock接口已被禁用
	 */
	public static final String RESULT_MOCK_INTERFACE_DISABLED = "mockInterfaceDisabled";
	
	
	//ApplicationMap中指定属性名	
	public static final String APPLICATION_ATTRIBUTE_QUERY_DB = "queryDb";	
	public static final String APPLICATION_ATTRIBUTE_WEB_SETTING = "settingMap";	
	public static final String APPLICATION_ATTRIBUTE_OPERATION_INTERFACE = "ops";
	
	//SessionMap中指定属性名
	public static final String SESSION_ATTRIBUTE_VERIFY_CODE = "verifyCode";
	
	//定时任务相关标志词语	
	public static final String QUARTZ_TIME_TASK_NAME_PREFIX_KEY = "timeScheduleJob";
	public static final String QUARTZ_PROBE_TASK_NAME_PREFIX_KEY = "probeScheduleJob";	
	public static final String QUARTZ_SCHEDULER_START_FLAG = "quartzStatus";	
	public static final String QUARTZ_SCHEDULER_IS_START = "true"; 	
	public static final String QUARTZ_SCHEDULER_IS_STOP = "false";
	
	///////////////////////////////////全局设置指定设置名称/////////////////////////////////////////////////////////////////////
	//通用设置
	public static final String GLOBAL_SETTING_HOME = "home";	
	public static final String GLOBAL_SETTING_NOTICE = "notice";	
	public static final String GLOBAL_SETTING_VERSION = "version";	
	public static final String GLOBAL_SETTING_STATUS = "status";	
	public static final String GLOBAL_SETTING_LOGSWITCH = "logSwitch";	
	public static final String GLOBAL_SETTING_COPY_RIGHT = "copyright";
	public static final String GLOBAL_SETTING_SITE_NAME = "siteName";
	
	//接口自动化测试相关全局配置
	public static final String GLOBAL_SETTING_MESSAGE_ENCODING = "messageEncoding";
	public static final String GLOBAL_SETTING_MESSAGE_REPORT_TITLE = "messageReportTitle";
	public static final String GLOBAL_SETTING_MESSAGE_MAIL_STYLE = "messageMailStyle";//邮件推送格式
	public static final String GLOBAL_SETTING_MESSAGE_MAIL_STYLE_PROBE_REPORT = "probe";//探测邮件
	public static final String GLOBAL_SETTING_MESSAGE_MAIL_STYLE_TASK_REPORT = "time";//定时任务邮件
	
	//web自动化脚本相关
	public static final String GLOBAL_SETTING_WEB_SCRIPT_WORKPLACE = "webscriptWorkPlace";
	public static final String GLOBAL_SETTING_WEB_SCRIPT_MODULE_PATH = "webscriptModulePath";
	public static final String GLOBAL_SETTING_WEB_SCRIPT_EXECUTOR_HOST = "webscriptExecutorHost";
	
	//邮箱推送相关
	public static final String GLOBAL_SETTING_IF_SEND_REPORT_MAIL = "sendReportMail";
	public static final String GLOBAL_SETTING_MAIL_SERVER_HOST = "mailHost";
	public static final String GLOBAL_SETTING_MAIL_SERVER_PORT = "mailPort";
	public static final String GLOBAL_SETTING_MAIL_AUTH_USERNAME = "mailUsername";
	public static final String GLOBAL_SETTING_MAIL_AUTH_PASSWORD = "mailPassword";
	public static final String GLOBAL_SETTING_MAIL_RECEIVE_ADDRESS = "mailReceiveAddress";
	public static final String GLOBAL_SETTING_MAIL_COPY_ADDRESS = "mailCopyAddress";
	public static final String GLOBAL_SETTING_MAIL_SSL_FLAG = "mailSSL";
	public static final String GLOBAL_SETTING_MAIL_SEND_ADDRESS = "mailSendAddress";
	public static final String GLOBAL_SETTING_MAIL_PERSONAL_NAME = "mailPersonalName";
	
	/**
	 * 日志记录接口白名单
	 */
	public static final String GLOBAL_SETTING_LOG_RECORD_WHITELIST = "logRecordWhitelist";
	/**
	 * 日志记录接口黑名单
	 */
	public static final String GLOBAL_SETTING_LOG_RECORD_BLACKLIST = "logRecordBlacklist";
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 测试报告静态html存储文件夹
	 */
	public static final String REPORT_VIEW_HTML_FOLDER = "reportHtml";
	/**
	 * 静态报告模板
	 */
	public static final String REPORT_VIEW_HTML_FIXED_HTML = "viewTemplate.xml";
	/**
	 * 静态报告模板
	 */
	public static final String REPORT_VIEW_HTML_FIXED_HTML_NEW = "offlineReportTemplateNew.xml";
	/**
	 * 测试集测试请求地址
	 */
	public static final String AUTO_TASK_TEST_RMI_URL = "test-scenesTest";
	
	/**
	 * 接口探测测试请求地址
	 */
	public static final String PROBE_TASK_TEST_RMI_URL = "test-probeTest";
	
	/**
	 * 生成静态报告请求地址
	 */
	public static final String CREATE_STATIC_REPORT_HTML_RMI_URL = "report-generateStaticReportHtml";
	
	/**
	 * 上传或者下载 excel保存的文件夹
	 */
	public static final String EXCEL_FILE_FOLDER = "excel";
	
	/**
	 * 验证码图片厨存放文件夹
	 */
	public static final String VERIFY_CODE_FOLDER = "code";
}
