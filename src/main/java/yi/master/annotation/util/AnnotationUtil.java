package yi.master.annotation.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;

/**
 * 前台DataTables中表格列与后端实体类的字段映射处理
 * @author xuwangcheng
 * @version 2017.1.1,1.0.0.0
 *
 */
public class AnnotationUtil {
	
	private static final Logger LOGGER = Logger.getLogger(AnnotationUtil.class);
	
	/**
	 * 获取当前字段的真实查询名称(HQL中对应的名称)
	 * <br>查询该字段是否需要增加搜索条件
	 * @param clazz
	 * @param fieldName
	 * @param getType  0-查询展示字段名  1-查询排序字段
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getRealColumnName(Class clazz, String fieldName, int getType, String searchValue) {
		
		Field field = null;
		if (!isHaveField(clazz, fieldName)) {
			return null;
		}
		
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			LOGGER.warn("NoSuchFieldException", e);
			return null;
		} catch (SecurityException e) {
			LOGGER.warn("SecurityException", e);
			return null;
		}
		//由于mysql中 Datetime字段不能通过like查询，目前暂时不对Datetime字段进行搜索
		if ((field.getType().getCanonicalName().equals("java.sql.Timestamp") 
				|| field.getType().getCanonicalName().equals("java.sql.Date")
				|| field.getType().getCanonicalName().equals("java.util.Timestamp")) && getType == 0) {
			/*if (StringUtils.isNotBlank(searchValue)) {
				//判断是否为yyyy-MM-dd HH:mm:ss的日期格式
				
			}*/
			return null;
		}
		String columPath = fieldName;
		
		List<String> columnValues = new ArrayList<String>();
		columnValues.add(columPath);
		if (getType == 0 && searchValue != null && field.isAnnotationPresent(FieldRealSearch.class)) {
			FieldRealSearch frs = field.getAnnotation(FieldRealSearch.class);
			for (int i = 0;i < frs.names().length;i++) {
				if (searchValue.equalsIgnoreCase(frs.names()[i])) {
					columnValues.add(frs.values()[i]);
				}
			}
		}
		
		if (field.isAnnotationPresent(FieldNameMapper.class)) {
			FieldNameMapper fnp = field.getAnnotation(FieldNameMapper.class);
			if (!fnp.fieldPath().isEmpty()) {
				if ((getType == 1 && fnp.ifOrder()) || (getType == 0 && fnp.ifSearch())) {
					columPath = fnp.fieldPath();
				} else {
					return null;
				}
	
			}	
			if ((getType == 0 && !fnp.ifSearch()) || (getType == 1 && !fnp.ifOrder())) {
				return null;
			}
		}
		columnValues.set(0, columPath);
		
		return columnValues;
	}
	
	/**
	 * 高级查询条件处理
	 * @param object
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<String> advancedQueryConditions (Object object) {
		List<String> querys = new ArrayList<String>();
		Class clazz = object.getClass();
		//默认判断对象实例中值不为空的都是高级查询的条件,所有对于初始化就有值/get方法返回总会有值的成员变量上
		//要加上注解@CustomConditionSetting(conditionType="")以排除
		for (Field f:clazz.getDeclaredFields()) {		
			//复杂类型或者自定义的复杂条件需要自行解析
			if (!Pattern.matches("String|Integer|Boolean|Double|Long", f.getType().getSimpleName())
					&& !f.getType().isPrimitive()) {
				continue;
			}
			String getMethodName = "get" + StringUtils.capitalize(f.getName());
			try {
				Method m = clazz.getDeclaredMethod(getMethodName);
				String value = m.invoke(object).toString();
				if (StringUtils.isBlank(value)) {
					continue;					
				}	
				String fieldName = f.getName();
				//获取HQL查询名称
				if (f.isAnnotationPresent(FieldNameMapper.class)) {					
					FieldNameMapper fnp = f.getAnnotation(FieldNameMapper.class);
					if (StringUtils.isNotEmpty(fnp.fieldPath())) {
						fieldName = fnp.fieldPath();
					}
				}
				String condition = " like '%" + value + "%'";
				//判断格式和运算符
				if (f.isAnnotationPresent(CustomConditionSetting.class)) {
					CustomConditionSetting ccs = f.getAnnotation(CustomConditionSetting.class);
					
					if (StringUtils.isBlank(ccs.conditionType())) {
						continue;
					}
					
					//判断是不是时间格式
					if (CustomConditionSetting.DATETIME_TYPE.equalsIgnoreCase(ccs.conditionType())) {
						String[] dates = value.split("~");
						querys.add(fieldName + ">='" + dates[0].trim() + " 00:00:00" +"'");
						querys.add(fieldName + "<'" + dates[1].trim() + " 23:59:59" +"'");
						continue;
					}
					
					//判断运算符
					if ("=".equals(ccs.operator())) {
						condition = ccs.operator() + "'" + value + "'";
						if (Pattern.matches("java\\.lang\\.(Integer|Double|Long|Float)", f.getDeclaringClass().getName())) {
							condition = ccs.operator() + value;
						}
					}
					if (Pattern.matches(">|<", ccs.operator())) {
						condition = ccs.operator() + value;
					}					
				}
				querys.add(fieldName + condition);
				
			} catch (Exception e) {
				LOGGER.warn(e.getMessage(), e);
			}
			
		}
				
		return querys;
	}
	
	@SuppressWarnings("rawtypes")
	private static boolean isHaveField(Class clazz, String fieldName) {
		for (Field f:clazz.getDeclaredFields()) {
			if (f.getName().equals(fieldName)) {
				return true;
			}
		}
		
		return false;
	}
}
