package yi.master.business.api.action;

import org.apache.log4j.Logger;

import yi.master.business.api.bean.ApiReturnInfo;
import com.opensymphony.xwork2.ActionSupport;

public class BaseApiAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * LOGGER
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(BaseApiAction.class.getName());
	
	
	/**
	 * ajax调用返回
	 */
	protected ApiReturnInfo returnInfo;
	
	
	public ApiReturnInfo getReturnInfo() {
		return returnInfo;
	}
	
}
