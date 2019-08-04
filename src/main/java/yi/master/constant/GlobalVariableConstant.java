package yi.master.constant;
/**
 * 
 * 全局变量常量值<br>
 * 主要是保存的json串的key值
 * @author xuwangcheng
 * @version 20171201,1.0.0.0
 *
 */
public class GlobalVariableConstant {
	
	/*UUID*/
	public static final String UUID_SEPARATOR_ATTRIBUTE_NAME = "uuidSeparator";
	
	/*随机数*/
	public static final String RANDOM_MIN_NUM_ATTRIBUTE_NAME = "randomMin";
	public static final String RANDOM_MAX_NUM_ATTRIBUTE_NAME = "randomNumMax";
	
	/*格式化日期*/
	public static final String DATETIME_FORMAT_ATTRIBUTE_NAME = "datetimeFormat";
	
	/*随机字符串*/
	public static final String RANDOM_STRING_MODE_ATTRIBUTE_NAME = "randomStringMode";
	public static final String RANDOM_STRING_NUM_ATTRIBUTE_NAME = "randomStringNum";
	
	/*测试运行时配置*/
	public static final String SET_RUNTIME_SETTING_REQUEST_URL_FLAG = "requestUrlFlag";
	public static final String SET_RUNTIME_SETTING_CONNECT_TIMEOUT = "connectTimeOut";
	public static final String SET_RUNTIME_SETTING_READ_TIMEOUT = "readTimeOut";
	public static final String SET_RUNTIME_SETTING_RETRY_COUNT = "retryCount";
	public static final String SET_RUNTIME_SETTING_RUN_TYPE = "runType";
	public static final String SET_RUNTIME_SETTING_CHECK_DATA_FLAG = "checkDataFlag";
	public static final String SET_RUNTIME_SETTING_CUSTOMR_EQUEST_URL = "customRequestUrl";
	
	/*验证模板*/
	public static final String RELATED_KEYWORD_VALIDATE_VALUE = "validateValue";
	public static final String RELATED_KEYWORD_VALUE_GET_METHOD = "getValueMethod";
	
	/*动态接口*/
	public static final String DYNAMIC_INTERFACE_SYSTEM_ID = "systemId";
	public static final String DYNAMIC_INTERFACE_SCENE_ID = "sceneId";
	public static final String DYNAMIC_INTERFACE_VALUE_EXPRESSION = "valueExpression";
	
	//使用方式，左边界
	public static final String USE_VARIABLE_LEFT_BOUNDARY = "${__";
	
	//使用方式，右边界
	public static final String USE_VARIABLE_RIGHT_BOUNDARY = "}";
	
	
}
