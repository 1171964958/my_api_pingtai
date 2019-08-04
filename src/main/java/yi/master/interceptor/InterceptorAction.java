package yi.master.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Controller;

import yi.master.constant.ReturnCodeConsts;
import yi.master.util.PracticalUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 根据跳转请求action返回前台指定的returnCode和msg
 * 该action主要将一些通用的返回集合起来供全局调用
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
public class InterceptorAction extends ActionSupport{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(InterceptorAction.class.getName());
	
	/**
	 * ajax调用返回给前台的map
	 */
	private Map<String,Object> jsonMap=new HashMap<String,Object>();
	
	/**
	 * 用户未登录或者登录失效	
	 * @return
	 */
	public String noLogin() {
		jsonMap.put("returnCode", ReturnCodeConsts.NOT_LOGIN_CODE);
		jsonMap.put("msg", "你还没有登录或者登录已失效,请重新登录");
		
		return SUCCESS;
		
	}
	
	/**
	 * 权限不够
	 * @return
	 */
	public String noPower() {
		
		jsonMap.put("returnCode", ReturnCodeConsts.NO_POWER_CODE);
		jsonMap.put("msg", "你没有权限进行此操作");

		return SUCCESS;
	}
	
	/**
	 * 系统错误
	 * @return
	 */
	public String error() {
		
		jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
		jsonMap.put("msg", "系统内部错误,请稍后再试！");		
		
	    logger.error("系统内部错误:\n" + PracticalUtils.getExceptionAllinformation((Exception)ActionContext.getContext().getValueStack().findValue("exception")));

		return SUCCESS;
	}
	
	/**
	 * 操作接口已被禁用
	 * @return
	 */
	public String opDisable() {
		
		jsonMap.put("returnCode", ReturnCodeConsts.OP_DISABLE_CODE);
		jsonMap.put("msg", "该操作接口已被设置禁止调用!");

		return SUCCESS;
	}
	
	/**
	 * 未找到的操作接口
	 * @return
	 */
	public String opNotfound() {
		
		jsonMap.put("returnCode", ReturnCodeConsts.OP_NOTFOUND_CODE);
		jsonMap.put("msg", "未定义的操作接口");
		
		logger.info("未定义的操作接口");
		
		return SUCCESS;
	}
	
	/**
	 * 参数验证失败
	 * @return
	 */
	public String parameterValidateFail() {
		
		jsonMap.put("returnCode", ReturnCodeConsts.PARAMETER_VALIDATE_FAIL_CODE);
		jsonMap.put("msg", "请求参数不正确,请检查!");
		
		logger.info("请求参数不正确");
		
		return SUCCESS;
	}
	
	/**
	 * 调用api接口时token验证不正确
	 * @return
	 */
	public String apiTokenValidateFail() {
		jsonMap.put("returnCode", ReturnCodeConsts.PARAMETER_VALIDATE_FAIL_CODE);
		jsonMap.put("msg", "token不正确!");
		
		logger.info("token不正确!");
		
		return SUCCESS;
	}
	
	/**
	 * 不存在的mock接口
	 * @return
	 */
	public String nonMockInterface() {
		jsonMap.put("returnCode", ReturnCodeConsts.OP_NOTFOUND_CODE);
		jsonMap.put("msg", "未定义的mock接口");
		
		logger.info("未定义的mock接口");
		
		return SUCCESS;
	}
	
	
	/**
	 * mock接口被禁止调用
	 * @return
	 */
	public String mockInterfaceDisabled() {
		jsonMap.put("returnCode", ReturnCodeConsts.OP_DISABLE_CODE);
		jsonMap.put("msg", "禁止调用该mock接口");
		
		logger.info("mock接口被禁止调用");
		
		return SUCCESS;
	}
	
	/**
	 * 接口mock出错
	 * @return
	 */
	public String mockError() {
		jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
		jsonMap.put("msg", "接口mock出错,请联系接口自动化测试平台!");
		
		logger.info("接口mock出错,请联系接口自动化测试平台!");
		
		return SUCCESS;
	}
	
	public Map<String, Object> getJsonMap() {		
		return jsonMap;
	}	
}
