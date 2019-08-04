package yi.master.business.system.dao;

import java.util.List;

import yi.master.business.base.dao.BaseDao;
import yi.master.business.system.bean.OperationInterface;

/**
 * 操作接口DAO接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

public interface OperationInterfaceDao extends BaseDao<OperationInterface> {

	/**
	 * 获取指定role拥有的操作接口
	 * @param roleId
	 * @return
	 */
	List<OperationInterface> listByRoleId(Integer roleId);
}
