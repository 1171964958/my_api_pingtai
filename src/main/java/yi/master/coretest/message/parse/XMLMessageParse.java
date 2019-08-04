package yi.master.coretest.message.parse;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.service.ParameterService;
import yi.master.constant.MessageKeys;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;
import yi.master.util.message.JsonUtil.TypeEnum;
import yi.master.util.message.XmlUtil;

import net.sf.json.JSONObject;

/**
 * 接口自动化<br>
 * xml格式报文相关的解析等方法实现
 * @author xuwangcheng
 * @version 2017.04.11,1.0.0.0
 *
 */
public class XMLMessageParse extends MessageParse {
	
	private static final Logger LOGGER = Logger.getLogger(XMLMessageParse.class);
	
	
	protected XMLMessageParse() {
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ComplexParameter parseMessageToObject(String message, List<Parameter> params) {
		
		Map maps = null;
		try {
			maps = XmlUtil.Dom2Map(message, null, null);
		} catch (Exception e) {
			LOGGER.error("xml报文解析失败:" + message, e);
			return null;
		}
		
		
		if (maps.isEmpty()) {
			return null;
		}
		ParameterService service = (ParameterService) FrameworkUtil.getSpringBean("parameterService");
		return parseObjectToComplexParameter(maps, new ComplexParameter(service.get(SystemConsts.PARAMETER_OBJECT_ID), 
				new HashSet<ComplexParameter>(), null), params, new StringBuilder(MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH));
	}

	@Override
	public String depacketizeMessageToString(ComplexParameter complexParameter, String paramsData) {
		
		//return MessageKeys.XML_MESSAGE_HEAD_STRING + parseXmlMessage(complexParameter, new StringBuilder(""), messageData).toString();
		return messageFormatBeautify(parseXmlMessage(complexParameter, new StringBuilder(""), PracticalUtils.jsonToMap(paramsData)).toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public String checkParameterValidity(List<Parameter> params, String message) {
		
		Object[] o = null;
		try {
			o = (Object[]) XmlUtil.getXmlList(message, 3);
		} catch (Exception e) {
			LOGGER.error("解析xml报文失败:" + message, e);
			return "解析xml报文失败";
		}
		
		if (o == null) {
			return "不是合法的XML格式或者无内容!";
		}

		List<String> paramNames = (List<String>) o[0];
		List<String> paramPaths = (List<String>) o[2];
		
		boolean paramCorrectFlag = false;
		boolean allCorrectFlag = true;
		
		String returnMsg = "入参节点:";
		
		for (int i = 0; i < paramNames.size(); i++) {
			for (Parameter p:params) {
				if (paramNames.get(i).equalsIgnoreCase(p.getParameterIdentify()) && paramPaths.get(i).equalsIgnoreCase(p.getPath())) {
					paramCorrectFlag = true;
				}				
			}
			if (!paramCorrectFlag) {
				allCorrectFlag = false;
				returnMsg += "[" + paramNames.get(i) + "] ";
			} else {
				paramCorrectFlag = false;
			}
		}
		
		if (!allCorrectFlag) {
			return returnMsg + "未在接口参数中定义或者类型/路径不匹配,请检查!";
		} 
		
		return "true";
	}
	
	@SuppressWarnings("unchecked")
	private StringBuilder parseXmlMessage(ComplexParameter parameter, StringBuilder message, Map<String, Object> messageData) {		
		
		if (parameter.getSelfParameter() == null) {
			return null;
		}
		
		String parameterType = parameter.getSelfParameter().getType().toUpperCase();
		String nodeName = findValidParameterIdentify(parameter);
		boolean flag = Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_ARRAY_IN_ARRAY + "|" 
				+ MessageKeys.MESSAGE_PARAMETER_TYPE_ARRAY + "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_OBJECT, parameterType)
				|| (nodeName == null);
		
		if (!flag) {
			message.append("<" + nodeName);
			//属性参数
			String attributes = parameter.getSelfParameter().getAttributes();
			if (StringUtils.isNotBlank(attributes)) {
				//转换成jsonObject
				Map<String, String> attributesMap = null;
				try {
					JSONObject jo = JSONObject.fromObject(attributes);
					attributesMap = (Map<String, String>) JSONObject.toBean(jo, Map.class);
				} catch (Exception e) {
					
					LOGGER.error("属性参数转换成JSONObject失败:" + attributes, e);
				}	
				if (attributesMap != null) {
					message.append(" ");
					for (String key:attributesMap.keySet()) {
						message.append(key + "=\"" + attributesMap.get(key) + "\" ");
					}
				}
			}
			
			message.append(">");
		}
				
		if (Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING + "|" 
				+ MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER, parameterType)) {	
			message.append(findParameterValue(parameter.getSelfParameter(), messageData));							
		} else if (MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA.equals(parameterType)) {
			message.append("<![CDATA[" + findParameterValue(parameter.getSelfParameter(), messageData) + "]]>");
		} else {
			for (ComplexParameter p:parameter.getChildComplexParameters()) {
				if (p.getSelfParameter() == null) {
					continue;
				}
				parseXmlMessage(p, message, messageData);
			}
			
		}
		if (!flag) {
			message.append("</" + nodeName + ">");
		}
				
		return message;
	}
	
	private String findValidParameterIdentify(ComplexParameter parameter) {
		
		if (!parameter.getSelfParameter().getParameterIdentify().isEmpty()) {
			return parameter.getSelfParameter().getParameterIdentify();
		}
		
		if (parameter.getSelfParameter().getType().equalsIgnoreCase(MessageKeys.MESSAGE_PARAMETER_TYPE_OBJECT) 
				&& parameter.getParentComplexParameter() == null) {
			//return Keys.XML_MESSAGE_DEFAULT_ROOT_NODE;
			return null;
		}
		
		return findValidParameterIdentify(parameter.getParentComplexParameter());
	}

	@Override
	public boolean messageFormatValidation(String message) {
		
		Document doc = null;
	  	try {
			doc = DocumentHelper.parseText(message);
		} catch (Exception e) {
			
			
		}
		
	  	if (doc == null) {
	  		return false;
	  	} 
	  	
	  	return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Parameter> importMessageToParameter(String message, Set<Parameter> existParams) {
		
		Object[] paramsInfo = null;
		try {
			paramsInfo = (Object[]) XmlUtil.getXmlList(message, 3);
		} catch (Exception e) {
			LOGGER.error("解析xml报文失败:" + message, e);		
			return null;
		}
		
		Set<Parameter> params = new HashSet<Parameter>();
		
		if (paramsInfo != null) {			
			Map<String,String> valueMap = (Map<String, String>)paramsInfo[3];
			List<String> paramList = (List<String>) paramsInfo[0];
			List<String> typeList = (List<String>) paramsInfo[1];
			List<String> pathList = (List<String>) paramsInfo[2];
			Map<String, Map<String, String>> attributes = (Map<String, Map<String, String>>) paramsInfo[4];

			Parameter param = null;
			for (int i = 0;i < paramList.size();i++) {
				param = new Parameter(paramList.get(i), "", valueMap.get(paramList.get(i)), pathList.get(i), typeList.get(i));
				if (attributes.get(param.getPath() + "." + param.getParameterIdentify()) != null) {
					//获取节点属性
					param.setAttributes(JSONObject.fromObject(attributes.get(param.getPath() + "." + param.getParameterIdentify())).toString());
				}
				if (validateRepeatabilityParameter(params, param) && validateRepeatabilityParameter(existParams, param)) {
					params.add(param);
				}								
			}		
		} 		
		return params;
	}

	@Override
	public String getObjectByPath(String message, String path) {
		
		return XmlUtil.getObjectByXml(message, path, TypeEnum.string);
	}
	
	protected static String XMLMessageFormatBeautify (String message) {
		try {			
			SAXReader reader = new SAXReader();  
		    StringReader in = new StringReader(message); 
		    Document doc = reader.read(in);  
		    OutputFormat formater = OutputFormat.createPrettyPrint();  
		    formater.setEncoding(CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MESSAGE_ENCODING));
		    StringWriter out = new StringWriter();  
		    XMLWriter writer = new XMLWriter(out,formater);  
		    writer.write(doc);  
		    writer.close();  
		    return out.toString();			
		} catch (Exception e) {
			
			LOGGER.warn("format xml message fail!", e);
			return message;
		}		
	}

	@Override
	public String parseMessageToSingleRow(String message) {
		
		String msg = super.parseMessageToSingleRow(message);
		return msg.replaceAll(">\\s+<", "><");		
	}

	@Override
	public String createMessageByNodes(JSONObject nodes) {
		
		Document document = DocumentHelper.createDocument();
		//创建根节点
		String rootName = nodes.getJSONObject(nodes.getString("rootId")).getString("parameterIdentify");
		Element message = document.addElement(rootName);
		for (Object key:nodes.keySet()) {
			if ("rootId".equals(key.toString())) continue;
			JSONObject node = nodes.getJSONObject(key.toString());
			if (Pattern.matches(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING + "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA 
					+ "|" + MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER, node.getString("type").toUpperCase())) {
				String value = node.getString("defaultValue");
				if (MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA.equals(node.getString("type").toUpperCase())) {
					value = "<![CDATA[" + value + " ]]>";
				}
				createMessageElement(node.getString("path"), message).addElement(node.getString("parameterIdentify")).setText(value);
				
			}
		}
		return XMLMessageFormatBeautify(document.asXML());
	}
	
	
	private Element createMessageElement (String path, Element message) {
		String[] pathNames = path.split("\\.");
		Element nodeObj = message;
		for (int i = 2;i < pathNames.length;i++) {
			if (MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH.equals(pathNames[i])) continue;
			Element nodeObj_l = nodeObj.element(pathNames[i]);
			if (nodeObj_l == null) {
				nodeObj_l = nodeObj.addElement(pathNames[i]);
			}
			nodeObj = nodeObj_l;
		}
		
		return nodeObj;
	}
	
	
	
}
