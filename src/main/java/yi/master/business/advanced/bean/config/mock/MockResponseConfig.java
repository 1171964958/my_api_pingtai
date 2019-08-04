package yi.master.business.advanced.bean.config.mock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.bean.Parameter;
import yi.master.constant.MessageKeys;
import yi.master.coretest.message.parse.MessageParse;

/**
 * 返回mock规则设定
 * @author xuwangcheng
 * @version 20180612
 *
 */
public class MockResponseConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(MockResponseConfig.class);
	
	/**
	 * 设置：JSON、XML、URL、不限(文本值)
	 */
	private String messageType = "";
	private List<MockGenerateRuleConfig> headers = new ArrayList<MockGenerateRuleConfig>();
	private List<MockGenerateRuleConfig> parameters = new ArrayList<MockGenerateRuleConfig>();
	private Long sleepTime = 0L;
	private String charset = "utf-8";
	private String exampleResponseMsg = "";
	private String exampleErrorMsg = "";
	private boolean format = true;
	
	private static final Map map = new HashMap();
	
	static {
		map.put("headers", MockGenerateRuleConfig.class);
		map.put("parameters", MockGenerateRuleConfig.class);
	}
	
	public MockResponseConfig(String messageType,
			List<MockGenerateRuleConfig> headers,
			List<MockGenerateRuleConfig> parameters, Long sleepTime) {
		super();
		this.messageType = messageType;
		this.headers = headers;
		this.parameters = parameters;
		this.sleepTime = sleepTime;
	}

	
	public static MockResponseConfig getInstance(String configJson) {
		if (StringUtils.isBlank(configJson) || JSONObject.fromObject(configJson).isEmpty()) {
			return new MockResponseConfig();
		}
		return (MockResponseConfig) JSONObject.toBean(JSONObject.fromObject(configJson), MockResponseConfig.class, map);
	}
	
	public MockResponseConfig() {
		super();
	}
	
	public String generate(HttpServletResponse response, String requestMsg) {	
		String responseMsg = new String(this.exampleResponseMsg);
		
		if (response != null) {
			//设定charset
			response.setCharacterEncoding(StringUtils.isBlank(charset) ? "UTF-8" : charset);		
			//设定响应头
			if (headers != null && headers.size() > 0) {
				for (MockGenerateRuleConfig config:headers) {
					response.addHeader(config.getName(), config.generateValue(requestMsg));
				}
			}
		}

		MessageParse parseUtil = MessageParse.getParseInstance(this.messageType);
		
		//**********生成入参报文******
		if (StringUtils.isNotBlank(this.exampleResponseMsg)) {
			if (!"text".equalsIgnoreCase(this.messageType)) {
				MessageParse responseMessageParseUtil = MessageParse.getParseInstance(MessageParse.judgeType(this.exampleResponseMsg));		
				//转换成复杂参数对象
				ComplexParameter complexParameter = responseMessageParseUtil.parseMessageToObject(exampleResponseMsg, new ArrayList<Parameter>(responseMessageParseUtil.importMessageToParameter(exampleResponseMsg, null)));
				
				//组装模拟值
				JSONObject paramsData = new JSONObject();
				for (MockGenerateRuleConfig config:parameters) {
					paramsData.put(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH + (StringUtils.isBlank(config.getPath()) ? "" : "." 
							+ config.getPath()) + "." + config.getName(), config.generateValue(requestMsg));
				}
				//生成最终的报文
				responseMsg = parseUtil.depacketizeMessageToString(complexParameter, paramsData.toString());						
			} else {
				for (MockGenerateRuleConfig config:parameters) {
					responseMsg = responseMsg.replaceAll("\\$\\{" + config.getName() + "\\}", config.generateValue(requestMsg));
				}
			}
		}

		//按照设定睡眠时间
		if (sleepTime > 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.warn("InterruptedException", e);
			}
		}
		//是否格式化
		if (format) {
			responseMsg = parseUtil.messageFormatBeautify(responseMsg);
		} else {
			responseMsg = parseUtil.parseMessageToSingleRow(responseMsg);
		}
		
		return responseMsg;
	}
	
	public void setExampleErrorMsg(String exampleErrorMsg) {
		this.exampleErrorMsg = exampleErrorMsg;
	}
	
	public String getExampleErrorMsg() {
		return exampleErrorMsg;
	}
	
	public boolean isFormat() {
		return format;
	}

	public void setFormat(boolean format) {
		this.format = format;
	}

	public void setExampleResponseMsg(String exampleResponseMsg) {
		this.exampleResponseMsg = exampleResponseMsg;
	}
	
	public String getExampleResponseMsg() {
		return exampleResponseMsg;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public String getCharset() {
		return charset;
	}
	
	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public List<MockGenerateRuleConfig> getHeaders() {
		return headers;
	}

	public void setHeaders(List<MockGenerateRuleConfig> headers) {
		this.headers = headers;
	}

	public List<MockGenerateRuleConfig> getParameters() {
		return parameters;
	}

	public void setParameters(List<MockGenerateRuleConfig> parameters) {
		this.parameters = parameters;
	}

	public Long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Long sleepTime) {
		this.sleepTime = sleepTime;
	}

	@Override
	public String toString() {
		return "MockResponseConfig [messageType=" + messageType + ", headers="
				+ headers + ", parameters=" + parameters + ", sleepTime="
				+ sleepTime + ", charset=" + charset + ", exampleResponseMsg="
				+ exampleResponseMsg + ", format=" + format + "]";
	}

}
