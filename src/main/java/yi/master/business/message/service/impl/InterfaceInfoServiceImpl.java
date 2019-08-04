package yi.master.business.message.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.dao.InterfaceInfoDao;
import yi.master.business.message.service.InterfaceInfoService;

/**
 * 接口信息Service实现
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */
@Service("interfaceInfoService")
public class InterfaceInfoServiceImpl extends BaseServiceImpl<InterfaceInfo> implements InterfaceInfoService {
	
	private InterfaceInfoDao interfaceInfoDao;
	
	@Autowired
	public void setInterfaceInfoDao(InterfaceInfoDao interfaceInfoDao) {
		super.setBaseDao(interfaceInfoDao);
		this.interfaceInfoDao = interfaceInfoDao;
	}
	
	

	@Override
	public List<InterfaceInfo> findInterfaceByCondition(String condition) {
		
		return interfaceInfoDao.findInterfaceByCondition(condition);
	}

	@Override
	public void changeStatus(int id, String status) {
		interfaceInfoDao.changeStatus(id, status);
		
	}

	@Override
	public InterfaceInfo findInterfaceByName(String interfaceName) {
		
		return interfaceInfoDao.findInterfaceByName(interfaceName);
	}
	
	
}
