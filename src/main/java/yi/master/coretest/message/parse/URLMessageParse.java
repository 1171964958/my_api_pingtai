package yi.master.coretest.message.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.service.ParameterService;
import yi.master.constant.MessageKeys;
import yi.master.constant.SystemConsts;
import yi.master.util.PracticalUtils;
import yi.master.util.FrameworkUtil;

/**
 * 接口自动化<br>
 * Url格式报文相关的解析等方法实现
 * @author xuwangcheng
 * @version 2017.04.11,1.0.0.0
 *
 */
public class URLMessageParse extends MessageParse {
	
	protected URLMessageParse() {
		
	}
	
	@Override
	public ComplexParameter parseMessageToObject(String message, List<Parameter> params) {
		
		if (!messageFormatValidation(message)) {
			return null;
		}
		Map<String, String> urlParams = parseUrlToMap(message, null);
		
		ParameterService service = (ParameterService) FrameworkUtil.getSpringBean("parameterService");
		ComplexParameter cp =  new ComplexParameter(service.get(SystemConsts.PARAMETER_OBJECT_ID), 
				new HashSet<ComplexParameter>(), null);
		for (String key:urlParams.keySet()) {
			cp.addChildComplexParameter(new ComplexParameter(findParamter(params, key, MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH),  new HashSet<ComplexParameter>(), cp));
		}
		
		return cp;
	}

	@Override
	public String depacketizeMessageToString(ComplexParameter complexParameter, String paramsData) {
			
		return messageFormatBeautify(paraseUrlMessage(complexParameter, new StringBuilder(""), PracticalUtils.jsonToMap(paramsData)).toString().substring(1));		
	}

	@Override
	public String checkParameterValidity(List<Parameter> params, String message) {
		
		if (!messageFormatValidation(message)) {
			return "不是合法的url入参格式,请检查!";
		}
		
		Map<String, String> urlParams = parseUrlToMap(message, null);
		
		boolean paramCorrectFlag = false;
		boolean allCorrectFlag = true;
		
		String returnMsg = "入参节点:";
		
		for (String key:urlParams.keySet()) {
			for (Parameter p:params) {
				if (key.equalsIgnoreCase(p.getParameterIdentify())
						&& Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING + "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER
						+ "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA, p.getType().toUpperCase())) {
					paramCorrectFlag = true;
				}
			}
			
			if (!paramCorrectFlag) {
				allCorrectFlag = false;
				returnMsg += "[" + key + "] ";
			} else {
				paramCorrectFlag = false;
			}
		}
		
		if (!allCorrectFlag) {
			return returnMsg + "未在接口参数中定义或者类型不匹配,请检查!";
		} 
		
		return "true";
	}
	
	private StringBuilder paraseUrlMessage(ComplexParameter parameter, StringBuilder message, Map<String, Object> messageData) {
		List<ComplexParameter> childParams = new ArrayList<ComplexParameter>(parameter.getChildComplexParameters());
		
		if (childParams.size() == 0) {
			message.append("&").append(parameter.getSelfParameter().getParameterIdentify()).append("=").append(findParameterValue(parameter.getSelfParameter(), messageData));	
			return message;
		}
		
		for (int i = 0; i < childParams.size(); i++) {
			if (childParams.get(i).getSelfParameter() == null) {
				continue;
			}
			
			paraseUrlMessage(childParams.get(i), message, messageData);						
		}
		
		return message;
	}

	@Override
	public boolean messageFormatValidation(String message) {
		
		String[] params = parseMessageToSingleRow(message).split("&");
		for (String s:params) {
			String[] parameter = StringUtils.split(s, "=", 2);
			if (parameter.length != 2) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<Parameter> importMessageToParameter(String message, Set<Parameter> existParams) {
		
		if (!messageFormatValidation(message)) {
			return null;
		}
		
		Set<Parameter> params = new HashSet<Parameter>();
		Map<String, String> urlParams = parseUrlToMap(parseMessageToSingleRow(message), null);
		
		for (String key:urlParams.keySet()) {
			Parameter p = new Parameter(key, key, urlParams.get(key), MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH, "String");
			if (StringUtils.isNumeric(urlParams.get(key))) {
				p.setType("Number");
			}
			if (validateRepeatabilityParameter(params, p) && validateRepeatabilityParameter(existParams, p)) {
				params.add(p);
			}
		}
		
		return params;
	}

	@Override
	public String getObjectByPath(String message, String path) {
		
		Map<String, String> urlParams = parseUrlToMap(message, null);
		path = path.substring(path.lastIndexOf(".") < 0 ? 0 : path.lastIndexOf("."));		
		return urlParams.get(path);
	}
	
	/**
	 * 将url入参转换成map
	 * @param message
	 * @param excludeAttributeNames 排除掉的属性名
	 * @return
	 */
	public static Map<String, String> parseUrlToMap(String message, String[] excludeAttributeNames) {
		
		Map<String, String> params = new HashMap<String, String>();
		
		String[] urlParams = message.split("&");
		
		loop:
		for (String s:urlParams) {
			String[] parameter = StringUtils.split(s, "=", 2);
			if (excludeAttributeNames != null) {
				for (String name:excludeAttributeNames) {
					if (name.equals(parameter[0])) {
						continue loop;
					}
				}
			}
			if (parameter.length > 1) {
				params.put(parameter[0], parameter[1]);
			}			
		}
		
		return params;
	}

	@Override
	public String createMessageByNodes(JSONObject nodes) {
		
		StringBuilder message = new StringBuilder();
		for (Object key:nodes.keySet()) {
			if ("rootId".equals(key.toString())) continue;
			JSONObject node = nodes.getJSONObject(key.toString());
			if (Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING + "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA 
					+ "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER, node.getString("type").toUpperCase())) {				
				if (message.length() > 0) {
					message.append("&");
				}
				message.append(node.getString("parameterIdentify") + "=" + node.getString("defaultValue"));
			}
		}
		return message.toString();
	}
}
