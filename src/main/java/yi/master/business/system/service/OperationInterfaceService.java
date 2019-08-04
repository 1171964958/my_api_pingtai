package yi.master.business.system.service;

import java.util.List;

import yi.master.business.base.service.BaseService;
import yi.master.business.system.bean.OperationInterface;

/**
 * 操作接口Service接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

public interface OperationInterfaceService extends BaseService<OperationInterface> {
	
	/**
	 * 获取指定role拥有的操作接口
	 * @param roleId
	 * @return
	 */
	List<OperationInterface> listByRoleId(Integer roleId);
	

}
