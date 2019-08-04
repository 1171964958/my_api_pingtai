package yi.master.util.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import yi.master.constant.MessageKeys;
import yi.master.util.message.JsonUtil.TypeEnum;

/**
 * 使用dom4j对xml报文进行解析
 * 
 * @author
 * @version 1.0.0.0,2017.0719
 * 
 */

public class XmlUtil {

	/**
	 * 根据输入的xml串获取指定的节点信息<br>
	 * mode=1,返回包含所有节点名称的list<br>
	 * mode=0,返回包含所有节点的类型的list<br>
	 * mode=4，返回包含所有节点的路径的list<br>
	 * mode=2,返回key为节点名称,value为节点值的map<br>
	 * mode=5,返回节点属性
	 * <br>
	 * mode=3,返回包含所有信息的Array<br>
	 * 
	 * @param xml
	 * @param mode
	 * @return
	 */
	private static final Logger LOGGER = Logger.getLogger(XmlUtil.class);
	
	@SuppressWarnings("rawtypes")
	public static Object getXmlList(String xml, int mode) throws Exception {

		// 返回的都是子节点的map或者list
		Map<String, String> jsonTreeMap = new HashMap<String, String>();

		List<String> jsonTreeList = new ArrayList<String>();
		List<String> jsonTreeType = new ArrayList<String>();
		List<String> jsonTreePath = new ArrayList<String>();
		Map<String, Map<String, String>> attributes = new HashMap<String, Map<String, String>>();
		
		Map maps = Dom2Map(xml, attributes, MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH);

		JsonUtil.viewJsonTree(maps, jsonTreeMap, jsonTreeList, jsonTreeType,
				jsonTreePath, MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH);

		if (mode == 1) {
			return jsonTreeList;
		} else if (mode == 2) {
			return jsonTreeMap;
		} else if (mode == 0) {
			return jsonTreeType;
		} else if (mode == 4) {
			return jsonTreePath;
		} else if (mode == 5) {
			return attributes;
		} else {
			Object[] a = { jsonTreeList, jsonTreeType, jsonTreePath,
					jsonTreeMap, attributes};
			return a;
		}

	}

	/**
	 * 复杂嵌套Xml获取Object数据
	 * 
	 * @param xmlStr
	 * @param argsPath
	 * @param argsType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getObjectByXml(String xmlStr, String argsPath,
			TypeEnum argsType) {
		if (argsPath == null || argsPath.equals("") || argsType == null) {
			return null;
		}
		int i = 0;
		Object obj = null;
		try {
			Map maps = Dom2Map(xmlStr, null, "TopRoot");
			// 多层获取
			if (argsPath.indexOf(".") >= 0) {
				// 类型自适应
				obj = getObject(maps, argsPath, argsType, i);
			} else { // 第一层获取
				if (argsType == TypeEnum.string) {
					obj = maps.get(argsPath).toString();
				} else if (argsType == TypeEnum.map) {
					obj = maps.get(argsPath);
				} else if (argsType == TypeEnum.arrayList) {
					obj = maps.get(argsPath);
				} else if (argsType == TypeEnum.arrayMap) {
					obj = maps.get(argsPath);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("###[Error] getObjectByJson() "+e.getMessage());
			return null;
		}
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}

	}

	/**
	 * 递归获取object
	 * 
	 * @param m
	 * @param key
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getObject(Object m, String key, TypeEnum type, int count) {
		if (m == null) {
			return null;
		}
		Object o = null;
		Map mp = null;
		List ls = null;
		try {
			// 对象层级递归遍历解析
			if (m instanceof Map || m instanceof LinkedHashMap) {
				mp = (LinkedHashMap) m;
				for (Iterator ite = mp.entrySet().iterator(); ite.hasNext();) {
					Map.Entry e = (Map.Entry) ite.next();

					if (count < key.split("\\.").length
							&& e.getKey().equals(key.split("\\.")[count])) {
						count++;
						if (e.getValue() instanceof String
								|| e.getValue() instanceof Number) {
							if (count == key.split("\\.").length) {
								o = e.getValue();
								return o;
							}
						} else if (e.getValue() instanceof LinkedHashMap) {
							if (count == key.split("\\.").length) {
								if (type == TypeEnum.map) {
									o = e.getValue();
									return o;
								}
							} else {
								o = getObject(e.getValue(), key, type, count);
							}
							return o;
						} else if (e.getValue() instanceof ArrayList) {
							if (count == key.split("\\.").length) {
								if (type == TypeEnum.arrayList) {
									o = e.getValue();
									return o;
								}
								if (type == TypeEnum.arrayMap) {
									o = e.getValue();
									return o;
								}
							} else {
								o = getObject(e.getValue(), key, type, count);
							}
							return o;
						}
					}
				}
			}
			// 数组层级递归遍历解析
			if (m instanceof List || m instanceof ArrayList) {
				ls = (ArrayList) m;
				for (int i = 0; i < ls.size(); i++) {
					if (ls.get(i) instanceof LinkedHashMap) {
						if (i == key.split("\\.").length) {
							if (type == TypeEnum.map) {
								o = ls.get(i);
								return o;
							}
						} else {
							o = getObject(ls.get(i), key, type, count);
						}
						return o;
					} else if (ls.get(i) instanceof ArrayList) {
						if (i == key.split("\\.").length) {
							if (type == TypeEnum.arrayList) {
								o = ls.get(i);
								return o;
							}
							if (type == TypeEnum.arrayMap) {
								o = ls.get(i);
								return o;
							}
						} else {
							o = getObject(ls.get(i), key, type, count);
						}
						return o;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("###[Error] getObject() " + e.getMessage(), e);
		}

		return o;
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> Dom2Map(String docStr, Map<String, Map<String, String>> attributes, String parentName) throws Exception {
		Document doc = null;
		if (attributes == null) {
			attributes = new HashMap<String, Map<String, String>>();
		}
		if (StringUtils.isEmpty(parentName)) {
			parentName = MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH;
		}
		try {
			doc = DocumentHelper.parseText(docStr);
		} catch (Exception e) {
			
			e.printStackTrace();
			throw e;
		}
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (doc == null) {
			return map;
		}

		Element root = doc.getRootElement();
		getElementAttributes(root, attributes, parentName);
		Map<String, Object> rootMap = new LinkedHashMap<String, Object>();

		map.put(root.getName(), rootMap);
		parentName = parentName + "." + root.getName();

		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			
			getElementAttributes(e, attributes, parentName);
			
			List list = e.elements();
			if (list.size() > 0) {
				putList(rootMap, e.getName(), Dom2Map(e, attributes, parentName));
			} else {
				putList(rootMap, e.getName(), getElementText(e, attributes, parentName));
			}

		}
		return map;
	}

	/**
	 * 已经存在的key则转换成list
	 * 
	 * @param map
	 * @param key
	 * @param value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void putList(Map map, String key, Object value) {
		List mapList = new ArrayList();
		if (map.get(key) != null) {
			Object object = map.get(key);
			if (object instanceof ArrayList) {
				mapList = (List) object;
				mapList.add(value);
			} else {
				mapList.add(map.get(key));
				mapList.add(value);
			}

			map.put(key, mapList);

		} else {
			map.put(key, value);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object Dom2Map(Element e, Map<String, Map<String, String>> attributes, String parentName) {
		getElementAttributes(e, attributes, parentName);
		
		Map map = new LinkedHashMap();
		List list = e.elements();
		parentName = parentName + "." + e.getName();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = (Element) list.get(i);

				if (iter.elements().size() > 0) {
					putList(map, iter.getName(), Dom2Map(iter, attributes, parentName));
				} else {
					putList(map, iter.getName(), getElementText(iter, attributes, parentName));
				}
			}
		} else {
			putList(map, e.getName(), getElementText(e, attributes, parentName));
		}
		return map;
	}

	private static Object getElementText(Element e, Map<String, Map<String, String>> attributes, String parentName) {
		
		getElementAttributes(e, attributes, parentName);
		
		String type = e.attributeValue("type");
		String text = e.getTextTrim();	
		
		if (StringUtils.isEmpty(text)) {
			return "";
		}
			
		//判断是否为整数
		if (type == null && StringUtils.isNumeric(text)) {
			type = "NUMBER";
		}
		//判断是否为小数
		if (type == null && Pattern.matches("[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+", text)) {
			type = "DOUBLE";
		}
		
		if (type == null) {
			return text;
		}
		
		switch (type.toUpperCase()) {
		case "INT":
		case "INTEGER":
			return Integer.valueOf(text);
		case "NUMBER":
		case "LONG":
			return Long.valueOf(text);
		case "DOUBLE":
		case "FLOAT":
			return Double.valueOf(text);
		default:
			return text;
		}
	}
	
	private static void getElementAttributes (Element e, Map<String, Map<String, String>> attributes, String parentName) {
		//获取节点属性
		List<Attribute> attrs = e.attributes();
		if (attrs.size() > 0) {
			Map<String, String> eattrs = new HashMap<String, String>();
			for (Attribute attr:attrs) {
				eattrs.put(attr.getName(), attr.getValue());
			}
			attributes.put(parentName + "." + e.getName(), eattrs);
		}
	}
}
