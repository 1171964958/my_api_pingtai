package yi.master.coretest.message.parse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.bean.Parameter;
import yi.master.constant.MessageKeys;

import net.sf.json.JSONObject;

/**
 * 固定报文
 * <br>针对于目前无法识别的格式报文，报文固定
 * @author xuwangcheng
 * @version 1.0.0.0, 20171030
 *
 */
public class FixedMessageParse extends MessageParse {
	
	protected FixedMessageParse() {
	}

	@Override
	public boolean messageFormatValidation(String message) {
		
		return true;
	}

	@Override
	public Set<Parameter> importMessageToParameter(String message,
			Set<Parameter> existParams) {
		
		Set<Parameter> params = new HashSet<Parameter>();
		Parameter param = new Parameter(message, "name", "defaultValue", "path", "String");
		if (validateRepeatabilityParameter(existParams, param)) {
			params.add(param);
		}
		return params;
	}

	@Override
	public ComplexParameter parseMessageToObject(String message,
			List<Parameter> params) {
		
		return new ComplexParameter(params.get(0), null, null);
	}

	@Override
	public String depacketizeMessageToString(ComplexParameter complexParameter,
			String paramsData) {
		
		Parameter param = complexParameter.getSelfParameter();
		return messageFormatBeautify(param.getParameterIdentify());
	}

	@Override
	public String checkParameterValidity(List<Parameter> params, String message) {
		
		
		for (Parameter p:params) {
			if (message.equals(p.getParameterIdentify())) {
				return "true";
			}
		}
		
		return "报文已固定为：<br>" + params.get(0).getParameterIdentify();
	}

	@Override
	public String getObjectByPath(String message, String path) {
		
		return message;
	}

	@Override
	public String createMessageByNodes(JSONObject nodes) {
		
		for (Object key:nodes.keySet()) {
			if ("rootId".equals(key.toString())) continue;
			JSONObject node = nodes.getJSONObject(key.toString());
			if (Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING + "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA 
					+ "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER, node.getString("type").toUpperCase())) {				
				return node.getString("defaultValue");
			}
		}
		return ""; 
	}

}
