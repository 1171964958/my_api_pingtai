package yi.master.business.advanced.bean.config.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import yi.master.business.message.bean.Parameter;
import yi.master.constant.MessageKeys;
import yi.master.coretest.message.parse.MessageParse;

/**
 * mock入参校验规则配置
 * @author xuwangcheng
 * @version 20180612
 *
 */
@SuppressWarnings("unchecked")
public class MockRequestValidateConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(MockRequestValidateConfig.class);
	
	@SuppressWarnings("rawtypes")
	private static final Map map = new HashMap();
	
	/**
	 * 请求方法:get post 为空不限
	 */
	private String method = "";
	/**
	 * 请求头信息验证规则
	 */
	private List<MockValidateRuleConfig> headers = new ArrayList<MockValidateRuleConfig>();
	/**
	 * 报文格式 为空不限
	 */
	private String messageType = "";	
	/**
	 * 入参节点验证规则
	 */
	private List<MockValidateRuleConfig> parameters = new ArrayList<MockValidateRuleConfig>();
	
	/**
	 * 查询参数验证规则
	 */
	private List<MockValidateRuleConfig> querys = new ArrayList<MockValidateRuleConfig>();
	
	
	static {
		map.put("headers", MockValidateRuleConfig.class);
		map.put("parameters", MockValidateRuleConfig.class);
		map.put("querys", MockValidateRuleConfig.class);		
	}

	public MockRequestValidateConfig(String method,
			List<MockValidateRuleConfig> headers, String messageType,
			List<MockValidateRuleConfig> parameters) {
		super();
		this.method = method;
		this.headers = headers;
		this.messageType = messageType;
		this.parameters = parameters;
	}

	public MockRequestValidateConfig() {
		super();
	}

	public static MockRequestValidateConfig getInstance(String configJson) {
		if (StringUtils.isBlank(configJson) || JSONObject.fromObject(configJson).isEmpty()) {
			return new MockRequestValidateConfig();
		}
		return (MockRequestValidateConfig) JSONObject.toBean(JSONObject.fromObject(configJson), MockRequestValidateConfig.class, map);
	}
	
	/**
	 * 通过HttpServletRequest获取报文来验证
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String validate(HttpServletRequest request, String requestMessage) {
		//判断请求方式
		if (request != null && StringUtils.isNotEmpty(this.method) && !this.method.equalsIgnoreCase(request.getMethod())) {
			return "HTTP请求方式必须为" + this.method + "!";
		}
				
		//判断是否为空
		if (parameters != null && parameters.size() > 0 && StringUtils.isBlank(requestMessage)) {
			return "没有获取到入参报文!";
		}
		
		//判断格式
		String type = MessageParse.judgeType(requestMessage);
		if (StringUtils.isNotEmpty(this.messageType) && !this.messageType.equalsIgnoreCase(type)) {
			return "入参报文格式不匹配,请传入" + this.messageType + "格式的报文!";
		}
		
		StringBuilder errorMsg = new StringBuilder();
		//query参数验证
		if (request != null && querys != null && querys.size() > 0) {
			Map querysParameters = request.getParameterMap();
			for (MockValidateRuleConfig config:querys) {
				Object queryValuesObject = querysParameters.get(config.getName());
				/*if (queryValuesObject == null) {
					errorMsg.append("查询参数" + config.getName() + "必须存在;");
					continue;
				}*/
				Parameter param = new Parameter(config.getName(), "", queryValuesObject == null ? null : ((String[])queryValuesObject)[0] , "Query." + config.getName(), "string");
				errorMsg.append(config.validate(param, requestMessage, "URL参数"));
			}
		}
		
		
		//头信息验证
		if (request != null && headers != null && headers.size() > 0) {
			for (MockValidateRuleConfig config:headers) {
				String headerValue = request.getHeader(config.getName());
				/*if (headerValue == null) {
					errorMsg.append("请求头" + config.getName() + "是必须存在;");
					continue;
				}*/
				Parameter param = new Parameter(config.getName(), "", headerValue, "Header." + config.getName(), "string");
				errorMsg.append(config.validate(param, requestMessage, "请求头"));
			}	
		}
		
		//验证报文信息		
		if (parameters != null && parameters.size() > 0) {
			MessageParse parseUtil = MessageParse.getParseInstance(type);
			List<Parameter> requestParameters = null;
			try {
				requestParameters = new ArrayList<Parameter>(parseUtil.importMessageToParameter(requestMessage, new HashSet<Parameter>()));
			} catch (Exception e) {
				
				logger.error(e);
			}
			for (MockValidateRuleConfig config:parameters) {
				Parameter param = parseUtil.findParamter(requestParameters, config.getName(), MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + (StringUtils.isBlank(config.getPath()) ? "" : "." + config.getPath()));
				errorMsg.append(config.validate(param, requestMessage, "节点参数"));
			}
		}
		
		//errorMsg为空代表全部验证通过
		if (StringUtils.isNotBlank(errorMsg)) {
			return errorMsg.toString();
		}
		
		return "true";
	}
	
	
	public void setQuerys(List<MockValidateRuleConfig> querys) {
		this.querys = querys;
	}
	
	public List<MockValidateRuleConfig> getQuerys() {
		return querys;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<MockValidateRuleConfig> getHeaders() {
		return headers;
	}

	public void setHeaders(List<MockValidateRuleConfig> headers) {
		this.headers = headers;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public List<MockValidateRuleConfig> getParameters() {
		return parameters;
	}

	public void setParameters(List<MockValidateRuleConfig> parameters) {
		this.parameters = parameters;
	}


	@Override
	public String toString() {
		return "MockRequestValidateConfig [method=" + method + ", headers="
				+ headers + ", messageType="
				+ messageType + ", parameters=" + parameters + "]";
	}
	
	
}
