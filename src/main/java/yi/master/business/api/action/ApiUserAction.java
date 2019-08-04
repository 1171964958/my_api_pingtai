package yi.master.business.api.action;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.api.bean.ApiReturnInfo;
import yi.master.business.api.service.user.ApiUserService;

/**
 * 外部api调动用户同步接口
 * @author xuwangcheng
 * @version 20180407,1.0.0
 *
 */
@Controller
@Scope("prototype")
public class ApiUserAction extends BaseApiAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ApiUserAction.class);
	@Autowired
	private ApiUserService service;
	
	private String userid;
	
	private String passwd;
	
	private String username;
	
	private String mode;
	
	/**
	 * 用户信息同步
	 * @return
	 */
	public String sync () {
		switch (StringUtils.isEmpty(mode) ? "null" : mode) {
		case "a":
			returnInfo = service.syncAdd(userid, username, passwd);
			break;
		case "m":
			returnInfo = service.syncModify(userid, username, passwd);
			break;
		case "d":
			returnInfo = service.syncDel(userid);
			break;
		default:
			returnInfo = new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "mode参数不正确:可选a-添加用户,m-修改用户,d-删除用户", null);
			break;
		}
		
		return SUCCESS;
	}
	
	/**
	 * 创建登录token
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String createToken() throws NoSuchAlgorithmException {
		returnInfo = service.createToken(userid, passwd);		
		return SUCCESS;
	}
	
	/*******************************************************************************/
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
}
