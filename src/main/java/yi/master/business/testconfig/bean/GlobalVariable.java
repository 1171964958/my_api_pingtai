package yi.master.business.testconfig.bean;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import net.sf.json.JSONObject;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.message.bean.SceneValidateRule;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.business.user.bean.User;
import yi.master.constant.GlobalVariableConstant;
import yi.master.coretest.message.test.MessageAutoTest;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;

/**
 * 
 * 全局变量表<br>
 * <strong>目前可使用的场景(有key值)：</strong><br>
 * 	1、所有字段的测试数据<br>
 * 	2、请求地址(报文中的mock/real/接口中定义的/测试集运行时配置中配置的自定义请求地址)<br>
 * 	3、接口参数中的默认值<br>
 * 	4、定时任务中的Cron表达式<br>
 *  5、关联验证中验证值<br>
 *  6、节点验证中验证值(取值方式为字符串、数据库时均有效)<br>
 *  7、全文验证中验证报文
 *  8、UUID
 * @author xuwangcheng
 * @version 2017.11.29,1.0.0.0
 *
 */
public class GlobalVariable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(GlobalVariable.class);
	
	/**
	 * http接口调用参数
	 */
	public static final String VARIABLE_TYPE_CALL_PARAMETER_HTTP = "httpCallParameter";
	/**
	 * socket接口调用参数
	 */
	public static final String VARIABLE_TYPE_CALL_PARAMETER_SOCKET = "socketCallParameter";
	/**
	 * webservice接口调用参数
	 */
	public static final String VARIABLE_TYPE_CALL_PARAMETER_WEBSERVICE = "webServiceCallParameter";
	/**
	 *  关联配置
	 */
	public static final String VARIABLE_TYPE_VALIDATE_RELATED_KEY_WORD = "relatedKeyWord";
	/**
	 * 测试集运行配置
	 */
	public static final String VARIABLE_TYPE_SET_RUNTIME_SETTING = "setRuntimeSetting";
	/**
	 * 常量
	 */
	public static final String VARIABLE_TYPE_CONSTANT = "constant";
	/**
	 * 时间日期
	 */
	public static final String VARIABLE_TYPE_DATETIME = "datetime";
	/**
	 * 随机数
	 */
	public static final String VARIABLE_TYPE_RANDOM_NUM = "randomNum";
	/**
	 * 当前时间戳
	 */
	public static final String VARIABLE_TYPE_CURRENT_TIMESTAMP = "currentTimestamp";
	/**
	 * 随机字符串
	 */
	public static final String VARIABLE_TYPE_RANDOM_STRING = "randomString";
	/**
	 * uuid
	 */
	public static final String VARIABLE_TYPE_UUID = "uuid";
	/**
	 * 动态接口
	 */
	public static final String VARIABLE_DYNAMIC_INTERFACE = "dynamicInterface";
	
	private ObjectMapper mapper = new ObjectMapper();
	
	private Integer variableId;
	/**
	 * 自定义的名称<br>
	 * 例如：当前日期、正常号码1
	 */
	private String variableName;
	/**
	 * 变量类型,目前分为以下几种类型：<br>
	 * <strong>配置模板类型：</strong><br>
	 * <i>httpCallParameter</i> 	HTTP协议调用参数模板<br>
	 * <i>socketCallParameter</i> 	socket协议调用参数模板<br>
	 * <i>webServiceCallParameter</i> 	webService调用参数模板<br>
	 * <i>relatedKeyWord</i> 	验证规则中的关联设定模板<br>
	 * <i>setRuntimeSetting</i> 	测试集运行时配置模板<br>
	 * <i>dynamicInterface</i> 	动态接口<br>
	 * 
	 * <strong>可以使用的变量：</strong><br>
	 * <i>constant</i>  常量<br>
	 * <i>datetime</i> 	当前日期时间 <br>
	 * <i>randomNum</i> 	随机数,目前只能是整数<br>
	 * <i>currentTimestamp</i> 	当前时间戳，<br>
	 * <i>randomString</i> 	随机字母组成的字符串<br>
	 * <i>uuid</i> 	uuid<br>
	 * 
	 */
	@FieldRealSearch(names = {"HTTP调用参数", "Socket调用参数", "WebService调用参数", "验证关联规则", "测试集运行时配置", "常量", "日期", "随机数", "时间戳", "随机字符串", "动态接口"},
			values = {"httpCallParameter", "socketCallParameter", "webServiceCallParameter", "relatedKeyWord", "setRuntimeSetting", "constant", "datetime", "randomNum", "currentTimestamp", "randomString", "dynamicInterface"})
	private String variableType;
	/**
	 * 如果这个变量可以被使用,则需要设置key值<br>
	 * datetime、constant、randomNum类型
	 */
	private String key;
	/**
	 * 根据variableType的不同，该值表示含义不同<br>
	 * <i>datetime</i>  	该值为日期格式表达式：yyyy-MM-dd HH:mm:ss<br>
	 * <i>httpCallParameter/socketCallParameter/relatedKeyWord</i> 	对应模板的JSON字符串<br>
	 * <i>setRuntimeSetting</i> 	对应配置内容的JSON字符串<br>
	 * <i>constant</i> 	任何常量内容<br>
	 * <i>randomNum</i>  	两个数字，分别表示最小值，最大值，用逗号分隔<br>
	 * <i>currentTimestamp</i> 	为空<br>
	 * <i>randomString</i> 	两个数字，第一位表示字符串长度，第二位表示组成：0-只有大写字母 1-只有小写字母  2-大小写字母混合 3-数字和字母<br>
	 * <i>uuid</i> 间隔符<br>
	 * <i>dynamicInterface</i> 对应配置的JSON字符串
	 */
	private String value;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	/**
	 * 创建人
	 */
	private User user;
	/**
	 * 备注
	 */
	private String mark;
	/**
	 * 该变量的有效期，在有效期内不会再动态生成
	 */
	private Timestamp expiryDate;
	/**
	 * 唯一性范围，在指定的范围内不会再动态生成，0 - 全局的，1 - 同一个测试集， 2 - 同一个组合场景， 3 - 同一个测试场景
	 */
	private String uniqueScope;
	/**
	 * 最近一次动态生成的变量值
	 */
	private String lastCreateValue;
	/**
	 * 有效期，单位秒
	 */
	private Integer validityPeriod;
	
	private String variableUseName;
	/**
	 * 变量创建失败原因
	 */
	private String createErrorInfo;
	
	public GlobalVariable(String variableName, String variableType, String key,
			String value, Timestamp createTime, User user, String mark) {
		super();
		this.variableName = variableName;
		this.variableType = variableType;
		this.key = key;
		this.value = value;
		this.createTime = createTime;
		this.user = user;
		this.mark = mark;
	}

	public GlobalVariable() {
		super();
		
	}

	public void setValidityPeriod(Integer validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	
	public Integer getValidityPeriod() {
		return validityPeriod;
	}
	
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getUniqueScope() {
		return uniqueScope;
	}

	public void setUniqueScope(String uniqueScope) {
		this.uniqueScope = uniqueScope;
	}

	public String getLastCreateValue() {
		return lastCreateValue;
	}

	public void setLastCreateValue(String lastCreateValue) {
		this.lastCreateValue = lastCreateValue;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public String getVariableUseName() {
		return GlobalVariableConstant.USE_VARIABLE_LEFT_BOUNDARY
				+ this.key + GlobalVariableConstant.USE_VARIABLE_RIGHT_BOUNDARY;
	}
	
	
	public void setCreateErrorInfo(String createErrorInfo) {
		this.createErrorInfo = createErrorInfo;
	}
	
	public String getCreateErrorInfo() {
		return createErrorInfo;
	}
	
	/**
	 * 将value的JSON转换成Map
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> parseJsonToMap() throws JsonParseException, JsonMappingException, IOException {		
		if (StringUtils.isNotBlank(this.value) && !VARIABLE_TYPE_CONSTANT.equalsIgnoreCase(this.variableType)) {
			return mapper.readValue(this.value, Map.class);
		}
		return null;
	}
	
	private void update() {
		GlobalVariableService service = (GlobalVariableService) FrameworkUtil.getSpringBean(GlobalVariableService.class);
		if (this.validityPeriod > 0) {
			this.expiryDate = new Timestamp(System.currentTimeMillis() + this.validityPeriod * 1000);
		}
		service.edit(this);
	}
	
	public Object createSettingValue() {
		return createSettingValue(false);
	}
	
	/**
	 * 根据类型与规则生成指定内容返回
	 * @return
	 */
	public Object createSettingValue (boolean forceCreate) {
		try {
			Map<String, String> maps = parseJsonToMap();
			switch (this.variableType) {
			case VARIABLE_TYPE_SET_RUNTIME_SETTING: //生成一个测试集运行时设定
				TestConfig config = new TestConfig();
				config.setCheckDataFlag(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_CHECK_DATA_FLAG));
				config.setConnectTimeOut(Integer.parseInt(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_CONNECT_TIMEOUT)));
				config.setCustomRequestUrl(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_CUSTOMR_EQUEST_URL));
				config.setReadTimeOut(Integer.parseInt(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_READ_TIMEOUT)));
				config.setRequestUrlFlag(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_REQUEST_URL_FLAG));
				config.setRetryCount(Integer.parseInt(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_RETRY_COUNT)));
				config.setRunType(maps.get(GlobalVariableConstant.SET_RUNTIME_SETTING_RUN_TYPE));
				
				return config;
			case VARIABLE_TYPE_VALIDATE_RELATED_KEY_WORD://生成一个关联规则
				SceneValidateRule rule = new SceneValidateRule();
				
				rule.setValidateValue(maps.get(GlobalVariableConstant.RELATED_KEYWORD_VALIDATE_VALUE));
				maps.remove(GlobalVariableConstant.RELATED_KEYWORD_VALIDATE_VALUE);
				rule.setGetValueMethod(maps.get(GlobalVariableConstant.RELATED_KEYWORD_VALUE_GET_METHOD));
				maps.remove(GlobalVariableConstant.RELATED_KEYWORD_VALUE_GET_METHOD);
				
				rule.setParameterName(JSONObject.fromObject(maps).toString());
				rule.setValidateMethodFlag("0");
				rule.setStatus("0");
				rule.setMark("模板创建的关联验证");
				return rule;
			case VARIABLE_TYPE_CALL_PARAMETER_HTTP:
			case VARIABLE_TYPE_CALL_PARAMETER_SOCKET:
			case VARIABLE_TYPE_CALL_PARAMETER_WEBSERVICE:			
			case VARIABLE_TYPE_CONSTANT:
				return this.value;
			case VARIABLE_TYPE_CURRENT_TIMESTAMP:
				if (this.expiryDate.getTime() > System.currentTimeMillis()
						&& StringUtils.isNotBlank(this.lastCreateValue)
						&& !forceCreate) {
					return this.lastCreateValue;
				}
				this.lastCreateValue = String.valueOf(System.currentTimeMillis());
				update();
				return this.lastCreateValue;
			case VARIABLE_TYPE_DATETIME:
				if (this.expiryDate.getTime() > System.currentTimeMillis() 
						&& StringUtils.isNotBlank(this.lastCreateValue)
						&& !forceCreate) {
					return this.lastCreateValue;
				}
				this.lastCreateValue = PracticalUtils.formatDate(parseJsonToMap().get(GlobalVariableConstant.DATETIME_FORMAT_ATTRIBUTE_NAME), new Date());
				update();
				return this.lastCreateValue;
			case VARIABLE_TYPE_RANDOM_NUM:
				if (this.expiryDate.getTime() > System.currentTimeMillis() 
						&& StringUtils.isNotBlank(this.lastCreateValue)
						&& !forceCreate) {
					return this.lastCreateValue;
				}
				int min = Integer.parseInt(maps.get(GlobalVariableConstant.RANDOM_MIN_NUM_ATTRIBUTE_NAME));
				int max = Integer.parseInt(maps.get(GlobalVariableConstant.RANDOM_MAX_NUM_ATTRIBUTE_NAME));

				this.lastCreateValue = String.valueOf(PracticalUtils.getRandomNum(max, min));
				update();
				return this.lastCreateValue;
			case VARIABLE_TYPE_RANDOM_STRING:
				if (this.expiryDate.getTime() > System.currentTimeMillis() 
						&& StringUtils.isNotBlank(this.lastCreateValue)
						&& !forceCreate) {
					return this.lastCreateValue;
				}
				
				this.lastCreateValue = PracticalUtils.createRandomString(maps.get(GlobalVariableConstant.RANDOM_STRING_MODE_ATTRIBUTE_NAME)
						, Integer.parseInt(maps.get(GlobalVariableConstant.RANDOM_STRING_NUM_ATTRIBUTE_NAME)));
				update();
				return this.lastCreateValue;
			case VARIABLE_TYPE_UUID:
				if (this.expiryDate.getTime() > System.currentTimeMillis() 
						&& StringUtils.isNotBlank(this.lastCreateValue)
						&& !forceCreate) {
					return this.lastCreateValue;
				}
				
				this.lastCreateValue = PracticalUtils.getUUID(maps.get(GlobalVariableConstant.UUID_SEPARATOR_ATTRIBUTE_NAME));
				update();
				return this.lastCreateValue;
			case VARIABLE_DYNAMIC_INTERFACE:
				if (this.expiryDate.getTime() > System.currentTimeMillis() 
						&& StringUtils.isNotBlank(this.lastCreateValue)
						&& !forceCreate) {
					return this.lastCreateValue;
				}
				MessageAutoTest autoTest = (MessageAutoTest) FrameworkUtil.getSpringBean(MessageAutoTest.class);
				
				this.lastCreateValue = autoTest.dynamicInterfaceGetValue(maps.get(GlobalVariableConstant.DYNAMIC_INTERFACE_SCENE_ID), maps.get(GlobalVariableConstant.DYNAMIC_INTERFACE_SYSTEM_ID)
						, maps.get(GlobalVariableConstant.DYNAMIC_INTERFACE_VALUE_EXPRESSION));
				update();
				return this.lastCreateValue;
			default:
				break;
			}
		} catch (Exception e) {			
			LOGGER.error("获取变量" + this.variableName + "[" + this.key + "]失败：" + this.value, e);
			createErrorInfo = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "系统异常";
			return null;
		}
		
		
		return this.value;
	}
	
	/**
	 * 判断指定类型是否需要唯一的key
	 * @return
	 */
	public static boolean ifHasKey(String type) {
		if (VARIABLE_TYPE_CALL_PARAMETER_HTTP.equals(type) ||
				VARIABLE_TYPE_CALL_PARAMETER_SOCKET.equals(type) ||
				VARIABLE_TYPE_CALL_PARAMETER_WEBSERVICE.equals(type) ||
				VARIABLE_TYPE_SET_RUNTIME_SETTING.equals(type) ||
				VARIABLE_TYPE_VALIDATE_RELATED_KEY_WORD.equals(type)) {						
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GlobalVariable [variableId=" + variableId + ", variableName="
				+ variableName + ", variableType=" + variableType + ", key="
				+ key + ", value=" + value + ", createTime=" + createTime
				+ ", user=" + user + ", mark=" + mark + ", variableUseName="
				+ variableUseName + ", createErrorInfo=" + createErrorInfo
				+ "]";
	}
	
}
