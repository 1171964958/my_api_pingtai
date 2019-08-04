package yi.master.business.api.service.user;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.api.bean.ApiReturnInfo;
import yi.master.business.user.bean.Role;
import yi.master.business.user.bean.User;
import yi.master.business.user.service.UserService;
import yi.master.constant.SystemConsts;
import yi.master.util.MD5Util;
import yi.master.util.FrameworkUtil;

@Service
public class ApiUserService {
	
	@Autowired
	private UserService userService;

	public ApiReturnInfo syncModify(String userid, String username,
			String passwd) {
		User user = userService.findByUserId_t(userid);
		if (user != null) {
			user.setPassword(passwd);
			user.setRealName(username);
			userService.edit(user);
			return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "更新信息成功!",
					null);
		}
		return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "更新失败:查询不到此用户信息!",
				null);
	}

	public ApiReturnInfo syncAdd(String userid, String username, String passwd) {
		User user = userService.findByUserId_t(userid);
		if (user == null) {			
			user = new User();
			user.setIfNew(userid);
			user.setCreateTime(new Timestamp(System.currentTimeMillis()));
			user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			user.setPassword(passwd);
			user.setUsername("");
			user.setRealName(username);
			user.setStatus("0");
			user.setRole(new Role(SystemConsts.ADMIN_ROLE_ID));			
			userService.edit(user);
			return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "添加用户信息成功!", null);
		}
		return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "添加失败:userid已存在!", "{\"userid\":\"" + user.getIfNew() + "\",\"username\":\"" + user.getRealName() + "\"}");
	}
	
	public ApiReturnInfo syncDel(String userid) {
		User user = userService.findByUserId_t(userid);
		if (user != null) {
			userService.delByUserId_t(userid);
			return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "删除用户成功!",
					null);
		}
		return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "用户不存在!",
				null);
	}
	
	public ApiReturnInfo createToken(String userid, String passwd) throws NoSuchAlgorithmException {
		User user = userService.loginSSO(userid, passwd);
		if (user == null) {
			return new ApiReturnInfo(ApiReturnInfo.ERROR_CODE, "userid或者密码不正确!", null);
		}
		String token = MD5Util.code(userid + passwd + System.currentTimeMillis());
		FrameworkUtil.getApplicationMap().put(token, user);
		return new ApiReturnInfo(ApiReturnInfo.SUCCESS_CODE, "token创建成功", token);
	}
}
