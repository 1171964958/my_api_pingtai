package yi.master.business.user.dao;

import java.util.List;

import yi.master.business.base.dao.BaseDao;
import yi.master.business.user.bean.User;

/**
 * 用户信息DAO接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

public interface UserDao extends BaseDao<User> {
	
	/**
	 * 用户登录
	 * @param userName 用户名
	 * @param passWord 密码
	 * @return 指定用户
	 */
	User login(String userName,String passWord);
	
	/**
	 * 重置密码
	 * @param userId 指定用户
	 * @param passwd 重置成的加密密码
	 */
	void resetPasswd(Integer userId,String passwd);
	
	/**
	 * 锁定用户或者解锁用户
	 * @param userId
	 * @param status
	 */
	void lockUser(Integer userId,String status);
	
	/**
	 * 验证用户名唯一性
	 * @param username
	 * @return
	 */
	User validateUsername(String username);
	
	/**
	 * 验证用户名唯一性
	 * @param username
	 * @param userId
	 * @return
	 */
	User validateUsername(String username,Integer userId);
	
	/**
	 * 更新指定用户的真实姓名
	 * @param realName
	 * @param userId
	 */
	void updateRealName(String realName,Integer userId);
	
	/**
	 * 根据真实姓名查找用户
	 * @param realName
	 * @return 符合条件的用户集合
	 */
	List<User> findByRealName(String realName);
	
	/**
	 * 根据登录标识登录
	 * @param username
	 * @param loginIdentification
	 * @return
	 */
	User loginByIdentification(String username, String loginIdentification);
	
	/**
	 * 根据userId_t和密码登录
	 * @param userId_t
	 * @param passwd
	 * @return
	 */
	User loginSSO(String userId_t, String passwd);
	
	void delByUserId_t(String userId_t);
	
	User findByUserId_t(String userId_t);
}
