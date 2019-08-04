package yi.master.coretest.message.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.service.ParameterService;
import yi.master.constant.MessageKeys;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;

/**
 * 报文解析工具类
 * 新格式报文继承此类即可
 * @author xuwangcheng
 * @version 2017.04.06,1.0.0.0
 *
 */

public abstract class MessageParse {
	
	public static final Logger LOGGER = Logger.getLogger(MessageParse.class.getName());
	
	private static final JSONMessageParse jsonParse = new JSONMessageParse();
	private static final XMLMessageParse xmlParse = new XMLMessageParse(); 
	private static final URLMessageParse urlParse = new URLMessageParse();
	private static final FixedMessageParse fixedParse = new FixedMessageParse(); 
	private static final OPTMessageParse optParse = new OPTMessageParse();
	
	
	/**
	 * 判断报文格式并返回指定的解析实例
	 * @param message
	 * @return
	 */
	public static final MessageParse judgeMessageType(String message) {
		if (StringUtils.isEmpty(message)) {
			return optParse;
		}
		if (jsonParse.messageFormatValidation(message)) {
			return jsonParse;
		}
		if (xmlParse.messageFormatValidation(message)) {
			return xmlParse;
		}
		if (urlParse.messageFormatValidation(message)) {
			return urlParse;
		}

		return optParse;
	}
	
	/**
	 * 判断报文格式并返回指定的报文格式名称
	 * @param message
	 * @return
	 */
	public static String judgeType(String message) {
		if (StringUtils.isEmpty(message)) {
			return null;
		}
		if (jsonParse.messageFormatValidation(message)) {
			return MessageKeys.MESSAGE_TYPE_JSON;
		}
		if (xmlParse.messageFormatValidation(message)) {
			return MessageKeys.MESSAGE_TYPE_XML;
		}
		if (urlParse.messageFormatValidation(message)) {
			return MessageKeys.MESSAGE_TYPE_URL;
		}

		return null;
	}
	
	/**
	 * 美化指定格式的报文
	 * @param message
	 * @return 
	 */
	public String messageFormatBeautify (String message) {
		if (StringUtils.isEmpty(message)) {
			return message;
		}
		
		if (jsonParse.messageFormatValidation(message)) {
			return JSONMessageParse.JSONMessageFormatBeautify(message);
		}

		if (xmlParse.messageFormatValidation(message)) {
			return XMLMessageParse.XMLMessageFormatBeautify(message);
		}
		
		return message;
	};
	
	/**
	 * 验证报文是否为指定的格式
	 * @param message 验证的报文串
	 * @return boolean 是否为合法的指定格式
	 */
	public abstract boolean messageFormatValidation (String message);
	
	/**
	 * 根据传入的不同格式报文，获取各个参数属性
	 * @param message 导入的报文串
	 * @param existParams 已存在的参数信息
	 * @return List&lt;Parameter&gt  解析指定的参数列表
	 */
	public abstract Set<Parameter> importMessageToParameter (String message, Set<Parameter> existParams);
	
	/**
	 * 将报文解析成ComplexParameter对象
	 * @param message 报文串
	 * @param params 参数列表
	 * @return ComplexParameter 根节点对应的复杂参数对象 
	 */
	public abstract ComplexParameter parseMessageToObject(String message, List<Parameter> params);
	
	/**
	 * 根据提供的根节点对应的复杂参数对象生成报文入参串
	 * @param complexParameter 根节点对应的复杂参数对象
	 * @param paramsData 提供的填充参数数据，如果传入的为null，则使用默认值 为json字符串
	 * @return String 解析成的报文串
	 */
	public abstract String depacketizeMessageToString(ComplexParameter complexParameter, String paramsData);
	
	/**
	 * 验证指定的报文串是否符合接口参数要求
	 * <br>默认要求在报文串中出现的节点名称都必须存在于接口的参数库中<br>
	 * 并且节点的类型也必须一样<br>
	 * 同名节点通过path路径来区别
	 * @param params 接口下的参数列表
	 * @param message 需要验证的报文串
	 * @return String 返回字符串'true'验证成功,其他内容为错误信息
	 */
	public abstract String checkParameterValidity(List<Parameter> params, String message);
	
	/**
	 * 根据传入的参数信息来创建指定格式的报文
	 * @param nodes  格式<br>parameterId:node.parameterId,<br>
							parameterIdentify:node.parameterIdentify,<br>
							defaultValue:node.defaultValue,<br>
							parentId:node.parentId,<br>
							type:node.type,<br>
							path:node.path<br>
	 * @return
	 */
	public abstract String createMessageByNodes(JSONObject nodes);
	
	/**
	 * 在提供的报文中查找指定路径的节点值，并替换字符串中需要替换的参数
	 * @param message 指定格式报文
	 * @param str 该字符串中包含节点路径，需要对此标记的各个节点路径替换成值
	 * @return
	 */
	public String parameterReplaceByNodePath(String message, String str) {
		
		String regex = MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_LEFT + "(.*?)" + MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_RIGHT;
		Pattern pattern = Pattern.compile(regex);
		List<String> regStrs = new ArrayList<String>();
		Matcher matcher = pattern.matcher(str);
 
		while (matcher.find()) {
			regStrs.add(matcher.group(1));
		}
		
		for (String regS:regStrs) {
			String s = MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_LEFT + regS + MessageKeys.CUSTOM_PARAMETER_BOUNDARY_SYMBOL_RIGHT;
			String replaceValue = getObjectByPath(message, regS);
			
			if (s != null) {
				str = str.replaceAll(s, replaceValue);
			}
			
		}
		
		return str;
	};
	
	/**
	 * 根据路径获取指定格式报文中的值
	 * @param message
	 * @param path 路径
	 * @return
	 */
	public abstract String getObjectByPath(String message, String path);
	
	
	/**
	 * 将已经美化过的报文或者其他格式的报文转换成一行<br>
	 * 注意：没有去除单行中的空格
	 * @param message
	 * @return
	 */
	public String parseMessageToSingleRow(String message) {
		 if(message!=null && !"".equals(message)) {      
	           Pattern p = Pattern.compile("\t|\r|\n");      
	           Matcher m = p.matcher(message);      
	           String strNoBlank = m.replaceAll("");      
	           return strNoBlank;      
	       }else {      
	           return message;      
	        } 
	};
	
	/**
	 * 根据参数名和参数路径在参数列表中查找是否存在
	 * @param params
	 * @param parameterName
	 * @param parameterPath
	 * @return
	 */
	public Parameter findParamter (List<Parameter> params, String parameterName, String parameterPath ) {
		
		if (params == null) {
			return null;
		}
		for (Parameter p:params) {
			if (p.getParameterIdentify().equalsIgnoreCase(parameterName) 
					&& p.getPath().equalsIgnoreCase(parameterPath)) {
				return p;
			}
		}
				
		return null;
	}
	
	/**
	 * 根据提供的参数信息在TestData的数据信息中查找指定的value,如果没有查询到则使用这个参数的默认值
	 * <br>需要注意的是对于Array类型参数下的String和Number参数可能有同名同路径的参数,取值的时候需要判断是否是List存储的value
	 * @param param
	 * @param messageData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String findParameterValue (Parameter param, Map<String, Object> messageData) {
		
		if (messageData == null) {
			return param.getDefaultValue();
		}
		String path = param.getPath() + "." + param.getParameterIdentify();
		if (messageData.get(path) == null) {
			return param.getDefaultValue();
		}
		
		if (messageData.get(path) instanceof String ) {
			return  StringUtils.isBlank(messageData.get(path).toString()) ? param.getDefaultValue() : messageData.get(path).toString();
		}
		
		//List类型，多个相同的值的话，用一个就删除一个
		List<String> values = (List<String>) messageData.get(path);
		
		if (values.size() < 1) {
			//如果已经取完，就用默认数据
			return param.getDefaultValue();
		}
		String value = values.get(0);
		values.remove(0);
		
		return StringUtils.isBlank(value) ? param.getDefaultValue() : value;
		
	}	
	
	public String findParameterValue2 (Parameter param, Map<String, Object> messageData) {
		if (messageData == null) {
			return "";
		}
		String path = param.getPath() + "." + param.getParameterIdentify();
		if (messageData.get(path) == null) {
			return "";
		}
		
		if (messageData.get(path) instanceof String) {
			return  messageData.get(path).toString();
		}
		
		//List类型，多个相同的值的话，用一个就删除一个
		List<String> values = (List<String>) messageData.get(path);
		
		if (values.size() < 1) {
			//如果已经取完，就用默认数据
			return "";
		}
		
		String value = values.get(0);
		values.remove(0);
		
		return  value;
	}
	
	/**
	 * 比较是否有重复的入参
	 * @param params
	 * @param p
	 * @return true-没有重复的<br>
	 * false-有重复的
	 */
	protected boolean validateRepeatabilityParameter(Set<Parameter> params, Parameter p) {
		if (params == null) {
			return true;
		}
		for (Parameter param:params) {
			if (param.equals(p)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 将Object转换成ComplexParameter<br>
	 * 递归方法
	 * @param obj
	 * @param parentComplexParameter
	 * @param params
	 * @param parentNodePath
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected ComplexParameter parseObjectToComplexParameter(Object obj, ComplexParameter parentComplexParameter, List<Parameter> params, StringBuilder parentNodePath) {
		
		ComplexParameter selfComplexParameter = null;
		if (obj != null) {
			Map mp = null;
            List ls = null;
            
            
            if (obj instanceof Map || obj instanceof LinkedHashMap) {
            	
            	mp = (LinkedHashMap)obj;           	           	
            	
            	for (Iterator ite = mp.entrySet().iterator();ite.hasNext();) {
            		Map.Entry entry = (Map.Entry) ite.next();
            		
            		selfComplexParameter = new ComplexParameter(findParamter(params, entry.getKey().toString(), parentNodePath.toString())
            				, new HashSet<ComplexParameter>(), parentComplexParameter);
            		parentComplexParameter.addChildComplexParameter(selfComplexParameter);
            		
            		if (entry.getValue() instanceof ArrayList || entry.getValue() instanceof LinkedHashMap) {
            			parseObjectToComplexParameter(entry.getValue(), selfComplexParameter, params, new StringBuilder(parentNodePath.toString() + "." + entry.getKey().toString()));
            		}
            		
            		
            	}
            }
            
            if (obj instanceof List || obj instanceof ArrayList) {
            	
            	ls = (ArrayList)obj;
            	for (int i = 0;i < ls.size();i++) {     		
            		selfComplexParameter = null;
            		ParameterService service = (ParameterService) FrameworkUtil.getSpringBean("parameterService");
                    if (ls.get(i) instanceof LinkedHashMap) {
                    	selfComplexParameter = new ComplexParameter(service.get(SystemConsts.PARAMETER_MAP_IN_ARRAY_ID)
                				, new HashSet<ComplexParameter>(), parentComplexParameter);
                    }   
                    
                    if (ls.get(i) instanceof ArrayList) {
                    	selfComplexParameter = new ComplexParameter(service.get(SystemConsts.PARAMETER_ARRAY_IN_ARRAY_ID)
                				, new HashSet<ComplexParameter>(), parentComplexParameter);
                		
                    }
                    parentComplexParameter.addChildComplexParameter(selfComplexParameter);
	               	
                    parseObjectToComplexParameter(ls.get(i), selfComplexParameter, params, parentNodePath);
                }
            }
			
			
		}
		return parentComplexParameter;
	}
	
	
	
	/**
	 * 获取对应的解析实例
	 * @param type
	 * @return
	 */
	public static MessageParse getParseInstance(String type) {
		if (StringUtils.isBlank(type)) return optParse;
		switch (type.toUpperCase()) {
		case MessageKeys.MESSAGE_TYPE_JSON:
			return jsonParse;
		case MessageKeys.MESSAGE_TYPE_XML:
			return xmlParse;
		case MessageKeys.MESSAGE_TYPE_URL:
			return urlParse;
		case MessageKeys.MESSAGE_TYPE_FIXED:
			return fixedParse;
		case MessageKeys.MESSAGE_TYPE_OPT:
			return optParse;
		default:
			break;
		}
		return optParse;
	}
	
}
