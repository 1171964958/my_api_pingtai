package yi.master.business.testconfig.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.bean.PageModel;
import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.dao.BusinessSystemDao;
import yi.master.business.testconfig.service.BusinessSystemService;

@Service("businessSystemService")
public class BusinessSystemServiceImpl extends BaseServiceImpl<BusinessSystem> implements BusinessSystemService {
	
	private BusinessSystemDao businessSystemDao;
	
	@Autowired
	public void setBusinessSystemDao(BusinessSystemDao businessSystemDao) {
		super.setBaseDao(businessSystemDao);
		this.businessSystemDao = businessSystemDao;
	}

	@Override
	public PageModel<InterfaceInfo> listSystemInterface(Integer systemId,
			int dataNo, int pageSize, String orderDataName, String orderType,
			String searchValue, List<List<String>> dataParams, int mode, String procotolType,
			String... filterCondition) {
		
		return businessSystemDao.listSystemInterface(systemId, dataNo, pageSize, orderDataName, orderType
				, searchValue, dataParams, mode, procotolType, filterCondition);
	}

	@Override
	public void addInterfaceToSystem(Integer systemId, Integer interfaceId) {
		
		businessSystemDao.addInterfaceToSystem(systemId, interfaceId);
	}

	@Override
	public void delInterfaceFromSystem(Integer systemId, Integer interfaceId) {
		
		businessSystemDao.delInterfaceFromSystem(systemId, interfaceId);
		
	}
}
