package yi.master.business.advanced.bean.config.mock;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import yi.master.business.message.bean.Parameter;
import yi.master.util.PracticalUtils;

public class MockValidateRuleConfig implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 参数名称，可能为Header的key、Query的key、入参节点名
	 */
	private String name;
	/**
	 * 规定路径，这个必须明确，在为Header和Query的key时为空<br>
	 * 如果该值为json格式字符串则使用边界关联获取值
	 */
	private String path;
	/**
	 * 类型:String Map Number Array
	 */
	private String type = "String";
	/**
	 * 验证类型：<br>
	 * exist - 必须存在<br>
	 * none - 不用验证<br>
	 * equal - 等于<br>
	 * contain - 包含<br>
	 * regexp - 正则
	 * 
	 */
	private String validateType = "none";
	/**
	 * 验证值：可写常量或者全局变量
	 */
	private String validateValue;
	/**
	 * 验证时是否忽略大小写
	 */
	private boolean ignoreCase = true;
	/**
	 * 最小长度：为空时不限制
	 */
	private String minLength;
	/**
	 * 最大长度：为空时不限制
	 */
	private String maxLength;
	/**
	 * 最大值：为空时不限制
	 */
	private String max;
	/**
	 * 最小值：为空时不限制
	 */
	private String min;
	public MockValidateRuleConfig(String name, String path, String type,
			String validateType, String validateValue, boolean ignoreCase,
			String minLength, String maxLength, String max, String min) {
		super();
		this.name = name;
		this.path = path;
		this.type = type;
		this.validateType = validateType;
		this.validateValue = validateValue;
		this.ignoreCase = ignoreCase;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.max = max;
		this.min = min;
	}
	public MockValidateRuleConfig() {
		super();
	}
	
	/**
	 * 对参数做验证：header或者入参节点或者查询参数
	 * @param param 参数对象
	 * @param requestMsg 请求报文
	 * @param tipPrefix 提示信息前缀
	 * @return
	 */
	public String validate(Parameter param, String requestMsg, String tipPrefix) {					
		if ("none".equalsIgnoreCase(this.validateType)) {
			return "";
		}
		StringBuilder errorMsg = new StringBuilder();
		
		String defaultValue = (param != null ? param.getDefaultValue() : "");
		
		Map<String, Object> maps = PracticalUtils.jsonToMap("{" + path + "}");		
		if (maps != null && maps.size() > 0) {
			defaultValue = PracticalUtils.getValueByRelationKeyWord(maps, requestMsg);
		}

		if (StringUtils.isBlank(defaultValue)) {
			return errorMsg.append(tipPrefix + this.name + "必须存在可用值!").toString();
		}		
		
		//类型验证
		if (param != null & StringUtils.isNotBlank(this.type) && !this.type.equalsIgnoreCase(param.getType())) {
			errorMsg.append(tipPrefix + param.getParameterIdentify() + "类型必须为" + this.type + ";");
		}
		
		//值比对
		String validateValue = PracticalUtils.replaceGlobalVariable(this.validateValue, null);
		switch (this.validateType) {
		case "none":
		case "exist":
			break;
		case "equal":			
			if ((ignoreCase && !defaultValue.equalsIgnoreCase(validateValue))
						|| (!ignoreCase && !defaultValue.equals(validateValue))) {
				errorMsg.append(tipPrefix + this.name + "的值必须为" + validateValue + ";");
			}
			break;
		case "contain":
			if ((ignoreCase && !defaultValue.toLowerCase().contains(validateValue.toLowerCase()))
						|| (!ignoreCase && !defaultValue.contains(validateValue))) {
				errorMsg.append(tipPrefix + this.name  + "的值必须包含" + validateValue + ";");
			}
			break;
		case "regexp":
			if (!Pattern.matches(validateValue, defaultValue)) {
				errorMsg.append(tipPrefix + this.name  + "的值不匹配正则表达式(" + validateValue + ");");
			}
			break;
		default:
			break;
		}
		
		//长度验证
		if (StringUtils.isNotBlank(this.minLength) && StringUtils.isNumeric(this.minLength)
				&& defaultValue.length() < Integer.valueOf(this.minLength)) {
			errorMsg.append(tipPrefix + this.name  + "的值长度不小于" + this.minLength + ";");
		}
		
		if (StringUtils.isNotBlank(this.maxLength) && StringUtils.isNumeric(this.maxLength)
				&& defaultValue.length() > Integer.valueOf(this.maxLength)) {
			errorMsg.append(tipPrefix + this.name  + "的值长度不大于" + this.maxLength + ";");
		}
		
		//大小验证,目前只判断整数
		if (StringUtils.isNumeric(defaultValue)) {
			int intValue = Integer.valueOf(defaultValue);
			if (StringUtils.isNotBlank(this.max) && StringUtils.isNumeric(this.max)
					&& intValue > Integer.valueOf(this.max)) {
				errorMsg.append(tipPrefix + this.name  + "的值不大于" + this.max + ";");
			}
			if (StringUtils.isNotBlank(this.min) && StringUtils.isNumeric(this.min)
					&& intValue < Integer.valueOf(this.min)) {
				errorMsg.append(tipPrefix + this.name  + "的值不小于" + this.min + ";");
			}
		}
		
		
		return errorMsg.toString();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValidateType() {
		return validateType;
	}
	public void setValidateType(String validateType) {
		this.validateType = validateType;
	}
	public String getValidateValue() {
		return validateValue;
	}
	public void setValidateValue(String validateValue) {
		this.validateValue = validateValue;
	}
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	public String getMinLength() {
		return minLength;
	}
	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}
	public String getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	@Override
	public String toString() {
		return "ParameterRuleConfig [name=" + name + ", path=" + path
				+ ", type=" + type + ", validateType=" + validateType
				+ ", validateValue=" + validateValue + ", ignoreCase="
				+ ignoreCase + ", minLength=" + minLength + ", maxLength="
				+ maxLength + ", max=" + max + ", min=" + min + "]";
	}
}
