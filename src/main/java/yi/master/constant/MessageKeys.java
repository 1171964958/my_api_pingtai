package yi.master.constant;

/**
 * 接口报文相关常量
 * @author xuwangcheng
 * @version 2017.12.12,1.0.0.6
 * 
 *
 */
public class MessageKeys {
	
	/*接口类型  CX-查询  SL-办理*/
	public static final String INTERFACE_TYPE_CX = "CX";	
	public static final String INTERFACE_TYPE_SL = "SL";
	
	/*报文格式*/
	public static final String MESSAGE_TYPE_JSON = "JSON";	
	public static final String MESSAGE_TYPE_XML = "XML";	
	public static final String MESSAGE_TYPE_URL = "URL";	
	public static final String MESSAGE_TYPE_FIXED = "FIXED";	
	public static final String MESSAGE_TYPE_OPT = "OPT";
	
	/*排序方式*/
	public static final String QUERY_ORDER_DESC = "desc";	
	public static final String QUERY_ORDER_ASC = "asc";
	
	/*默认path路径根节点*/
	public static final String MESSAGE_PARAMETER_DEFAULT_ROOT_PATH = "TopRoot";
	
	/*节点类型*/
	public static final String MESSAGE_PARAMETER_TYPE_MAP = "MAP";	
	public static final String MESSAGE_PARAMETER_TYPE_ARRAY = "ARRAY";	
	public static final String MESSAGE_PARAMETER_TYPE_NUMBER = "NUMBER";	
	public static final String MESSAGE_PARAMETER_TYPE_STRING = "STRING";	
	public static final String MESSAGE_PARAMETER_TYPE_OBJECT = "OBJECT";	
	public static final String MESSAGE_PARAMETER_TYPE_LIST = "LIST";	
	public static final String MESSAGE_PARAMETER_TYPE_MAP_IN_ARRAY = "ARRAY_MAP";	
	public static final String MESSAGE_PARAMETER_TYPE_ARRAY_IN_ARRAY = "ARRAY_ARRAY";
	public static final String MESSAGE_PARAMETER_TYPE_CDATA = "CDATA";
	
	/*xml报文默认头编码*/
	public static final String XML_MESSAGE_ENCODING = "UTF-8";
	/*xml默认根节点*/
	public static final String XML_MESSAGE_DEFAULT_ROOT_NODE = "ROOT";
	
	/*协议类型*/
	public static final String MESSAGE_PROTOCOL_HTTP = "http";	
	public static final String MESSAGE_PROTOCOL_WEBSERVICE = "webservice";	
	public static final String MESSAGE_PROTOCOL_SOCKET = "socket";	
	public static final String MESSAGE_PROTOCOL_TUXEDO = "tuxedo";
	public static final String MESSAGE_PROTOCOL_HTTPS = "https";
	
	/*HTTP协议调用参数*/
	public static final String HTTP_PARAMETER_HEADER = "Headers";	
	public static final String HTTP_PARAMETER_QUERYS = "Querys";
	public static final String HTTP_PARAMETER_AUTHORIZATION = "Authorization";	
	public static final String HTTP_PARAMETER_ENC_TYPE = "EncType";	
	public static final String HTTP_PARAMETER_REC_ENC_TYPE = "RecEncType";
	
	/*webservice协议调用参数*/
	public static final String WEB_SERVICE_PARAMETER_NAMESPACE = "Namespace";
	
	/*协议公共调用参数*/
	public static final String PUBLIC_PARAMETER_CONNECT_TIMEOUT = "ConnectTimeOut";	
	public static final String PUBLIC_PARAMETER_READ_TIMEOUT = "ReadTimeOut";	
	public static final String PUBLIC_PARAMETER_USERNAME = "Username";	
	public static final String PUBLIC_PARAMETER_PASSWORD = "Password";	
	public static final String PUBLIC_PARAMETER_METHOD = "Method";
	
	/*报文处理类型*/
	public static final String PROCESS_TYPE_SHANXI_OPEN_API = "ShanXiOpenApi";
	public static final String PROCESS_TYPE_ANHUI_APP = "AnhuiApp";
	
	/*报文处理类型参数-山西能力开放平台加密*/
	public static final String SHANXI_OPEN_API_PARAMETER_PEM_FILE_PATH = "pemFilePath";
	/*安徽掌厅App接口加密key*/
	public static final String ANHUI_APP_ENCRYPT_KEY = "key";
	/*安徽掌厅App接口加密-需要加密的敏感信息字段名称,使用逗号分隔*/
	public static final String ANHUI_APP_ENCRYPT_SENSITIVE_INFORMATION = "sensitiveInformation";
	/*安徽掌厅App接口加密-公钥*/
	public static final String ANHUI_APP_ENCRYPT_PUBLIC_KEY = "publicKey";
	/*安徽掌厅App接口加密-加密类型 默认RSA*/
	public static final String ANHUI_APP_ENCRYPT_ALGORITHM_TYPE = "algorithmType";
	
	/*测试环境中默认路径中的替换变量*/
	public static final String BUSINESS_SYSTEM_DEFAULTPATH_NAME_ATTRIBUTE = "\\$\\{name\\}";
	public static final String BUSINESS_SYSTEM_DEFAULTPATH_PATH_ATTRIBUTE = "\\$\\{path\\}";
	
	
	/*responseMessage-返回报文、返回内容 
	useTime-请求到返回过程耗时 
	statusCode-返回码，可以是通用的或者自定义的 
	opTime-完成时间 
	headers-请求头和返回头
	mark-出错时的错误记录*/	
	public static final String RESPONSE_MAP_PARAMETER_MESSAGE = "responseMessage";
	public static final String RESPONSE_MAP_PARAMETER_USE_TIME = "useTime";	
	public static final String RESPONSE_MAP_PARAMETER_STATUS_CODE = "statusCode";	
	public static final String RESPONSE_MAP_PARAMETER_TEST_MARK = "mark";
	public static final String RESPONSE_MAP_PARAMETER_HEADERS = "headers";
	
	/*
	 * 执行结果中对应的状态<br>
	 * 2 -异常结束   1 - 执行失败或者验证不通过  0 - 正常
	 */
	public static final String TEST_RUN_STATUS_STOP = "2";	
	public static final String TEST_RUN_STATUS_FAIL = "1";	
	public static final String TEST_RUN_STATUS_SUCCESS = "0";
	
	
	/*
	 * 使用节点参数时需要再参数路径左右加上以下左右边界
	 */
	public static final String CUSTOM_PARAMETER_BOUNDARY_SYMBOL_LEFT = "#";
	public static final String CUSTOM_PARAMETER_BOUNDARY_SYMBOL_RIGHT = "#";
	
	@Deprecated
	public static final String CUSTOM_REQUEST_URL_REPLACE_PARAMETER = "\\$\\{interfaceName\\}";
	
	/*
	 * quartz定时任务执行的测试将会在对应的测试报告添加下面的备注
	 */
	public static final String QUARTZ_AUTO_TEST_REPORT_MARK = "自动化定时任务";
	
	/*
	 * 缺少测试数据时测试详情中的备注
	 */
	public static final String NO_ENOUGH_TEST_DATA_RESULT_MARK = "缺少测试数据";
	
}
