package yi.master.business.user.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.user.bean.Role;
import yi.master.business.user.dao.RoleDao;

/**
 * 角色信息DAO接口实现
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

@Repository("roleDao")
public class RoleDaoImpl extends BaseDaoImpl<Role> implements RoleDao {
	
	@Override
	public Role get(String roleName){
		String hql = "From Role r "
				+ "where r.roleName=:roleName";
		
		return (Role) getSession().createQuery(hql)
				.setString("roleName", roleName)
				.setFirstResult(0).setMaxResults(1)
				.setCacheable(true).uniqueResult();
	}
	
	@Override
	public void changeUserRole(int roleId) {
		String sql = "update at_user set role_id=3 "
				+ "where role_id="+roleId;
		
		getSession().createSQLQuery(sql).executeUpdate();
	}

}
