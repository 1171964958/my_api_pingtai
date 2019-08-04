package yi.master.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionContext;

import yi.master.annotation.util.AnnotationUtil;
import yi.master.business.base.service.BaseService;
import yi.master.business.user.bean.User;
import yi.master.constant.SystemConsts;

/**
 * 框架工具类
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 *
 */

public class FrameworkUtil {
	
	private FrameworkUtil() {
		throw new Error("Please don't instantiate me！");
	}
	
	private static String projectPath;
	
	/**
	 * spring上下文
	 */
	private static ApplicationContext ctx;
	
	private static ServletContext webContext;
	
	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static User getLoginUser() {
		return (User) getSessionMap().get(SystemConsts.SESSION_ATTRIBUTE_LOGIN_USER);
	}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * 获取requestMap
	 * @return
	 */
	public static Map<String,Object> getRequestMap() {
		return (Map<String, Object>)ActionContext.getContext().get("request");
	}
	
	/**
	 * 获取sessionMap
	 * @return
	 */
	public static Map<String,Object> getSessionMap() {
		return ActionContext.getContext().getSession();
	}
	
	/**
	 * 获取applicationMap
	 * @return
	 */
	public static Map<String,Object> getApplicationMap() {
		return ActionContext.getContext().getApplication();
	}
	
	/**
	 * 获取parameterMap
	 * @return
	 */
	public static Map<String,Object> getParametersMap() {		
		return ActionContext.getContext().getParameters();
	}
	
	/**
	 * 获取前端Datatables发送的参数
	 * @return Map&lt;String,Object&gt;
	 * <br>orderDataName  需要排序的那一列属性名称
	 * <br>orderType  排序方式 asc或者desc
	 * <br>searchValue 全局搜索条件
	 * <br>dataParams List&lt;String&gt; 当前所有的展示字段
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String,Object> getDTParameters(Class clazz) {
		
		Map<String,Object> returnMap = new HashMap<String,Object>();		
		//排序的那一列位置
		String orderColumnNum = ServletActionContext.getRequest().getParameter("order[0][column]");
		//排序方式 asc或者desc
		String orderType = ServletActionContext.getRequest().getParameter("order[0][dir]");
		//全局搜索条件
		String searchValue = ServletActionContext.getRequest().getParameter("search[value]");
		
		//需要排序的那一列属性名称
		String orderDataName = ServletActionContext.getRequest().getParameter("columns[" + orderColumnNum + "][data]");
		List<String> orderDataNameL = AnnotationUtil.getRealColumnName(clazz, orderDataName, 1, null);
		//获取当前所有的展示字段
		//必须是当前实体类所拥有的的字段，不考虑其他情况
		Map<String, String[]> params = ServletActionContext.getRequest().getParameterMap();
		List<List<String>> dataParams = new ArrayList<List<String>>();

		for (Map.Entry<String, String[]> entry:params.entrySet()) {
			if (entry.getKey().indexOf("][data]") != -1) {
				String a = (params.get(entry.getKey()))[0];		
				
				List<String> columnSearchValueL = AnnotationUtil.getRealColumnName(clazz, a, 0, searchValue);
					
				if (columnSearchValueL != null) {
					dataParams.add(columnSearchValueL);
				}				

			}
		}
		
		returnMap.put("orderDataName", orderDataNameL == null ? "" : orderDataNameL.get(0));
				
		returnMap.put("orderType", orderType);
		returnMap.put("searchValue", searchValue);
		returnMap.put("dataParams", dataParams);
		return returnMap;
	}
	
	/**
	 * 获取actionName
	 * @return
	 */
	public static String getActionName() {
		return ActionContext.getContext().getName();
	}
	
	/**
	 * 获取spring上下文
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		HttpServletRequest request = ServletActionContext.getRequest();
		ServletContext sc = request.getSession().getServletContext();
		return WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	}
	
	/**
	 * 获取项目根路径
	 * @return
	 */
	public static String getProjectPath() {
		if (StringUtils.isNotEmpty(projectPath)) {
			return projectPath;
		}
		ActionContext ac = ActionContext.getContext();
        ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);
        projectPath = sc.getRealPath("");
        return projectPath;
	}
	
	
	public static void setProjectPath(String projectPath) {
		FrameworkUtil.projectPath = projectPath;
	}
	
	/**
	 * 获取指定springBean
	 * @param beanName
	 * @return
	 */
	public static Object getSpringBean (String beanName) {
		return ctx.getBean(beanName);
	}
	
	/**
	 * 获取指定spring bean
	 * @param clazz
	 * @return
	 */
	public static Object getSpringBean (Class clazz) {
		return ctx.getBean(clazz);
	}
	
	public static ApplicationContext getCtx () {
		return ctx;
	}
	
	
	public static void setCtx(ApplicationContext ctx) {
		FrameworkUtil.ctx = ctx;
	}
	
	public static void setWebContext(ServletContext webContext) {
		FrameworkUtil.webContext = webContext;
	}
	
	public static ServletContext getWebContext() {
		return webContext;
	}
	
	/**
	 * 通过service名称和对象主键获取实体
	 * @param serviceName
	 * @param objId
	 * @return
	 */
	public static Object getObjectByServiceNameAndId(String serviceName, Integer objId) {
		BaseService service = (BaseService) getSpringBean(serviceName);
		return service.get(objId);
	}
	
	/**
	 * 通过service类和对象主键获取实体
	 * @param serviceClass
	 * @param objId
	 * @return
	 */
	public static Object getObjectByServiceClassNameAndId(Class serviceClass, Integer objId) {
		BaseService service = (BaseService) getSpringBean(serviceClass);
		return service.get(objId);
	}
	
}
