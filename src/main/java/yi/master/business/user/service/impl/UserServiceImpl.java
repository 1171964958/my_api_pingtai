package yi.master.business.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.user.bean.User;
import yi.master.business.user.dao.UserDao;
import yi.master.business.user.service.UserService;

/**
 * 用户信息Service接口实现
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
	
	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		super.setBaseDao(userDao);
		this.userDao = userDao;
	}

	@Override
	public User login(String userName, String passWord) {
		
		return userDao.login(userName, passWord);
	}

	@Override
	public void resetPasswd(Integer userId, String passwd) {
		
		userDao.resetPasswd(userId, passwd);
	}

	@Override
	public void lockUser(Integer userId, String status) {
		
		userDao.lockUser(userId, status);
	}

	@Override
	public User validateUsername(String username, Integer userId) {		
		if (userId==null) {
			return userDao.validateUsername(username);
		}
		return userDao.validateUsername(username, userId);
	}

	@Override
	public void updateRealName(String realName, Integer userId) {
		
		userDao.updateRealName(realName, userId);
		
	}

	@Override
	public List<User> findByRealName(String realName) {
		
		return userDao.findByRealName(realName);
	}

	@Override
	public User loginByIdentification(String username,
			String loginIdentification) {
		
		return userDao.loginByIdentification(username, loginIdentification);
	}
	
	@Override
	public User loginSSO(String userId_t, String passwd) {
		
		return userDao.loginSSO(userId_t, passwd);
	}

	@Override
	public void delByUserId_t(String userId_t) {
		
		userDao.delByUserId_t(userId_t);
	}

	@Override
	public User findByUserId_t(String userId_t) {
		
		return userDao.findByUserId_t(userId_t);
	}

	
}
