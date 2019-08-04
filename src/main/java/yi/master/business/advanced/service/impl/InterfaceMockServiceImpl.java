package yi.master.business.advanced.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.advanced.bean.InterfaceMock;
import yi.master.business.advanced.dao.InterfaceMockDao;
import yi.master.business.advanced.service.InterfaceMockService;
import yi.master.business.base.service.impl.BaseServiceImpl;

@Service("interfaceMockService")
public class InterfaceMockServiceImpl extends BaseServiceImpl<InterfaceMock> implements InterfaceMockService {
	
	private InterfaceMockDao interfaceMockDao;
	
	@Autowired
	public void setInterfaceMockDao(InterfaceMockDao interfaceMockDao) {
		super.setBaseDao(interfaceMockDao);
		this.interfaceMockDao = interfaceMockDao;
	}

	@Override
	public InterfaceMock findByMockUrl(String mockUrl) {
		
		return interfaceMockDao.findByMockUrl(mockUrl);
	}

	@Override
	public void updateStatus(Integer mockId, String status) {
		
		interfaceMockDao.updateStatus(mockId, status);
	}

	@Override
	public void updateSetting(Integer mockId, String settingType,
			String configJson) {
		
		if (StringUtils.isBlank(settingType) || StringUtils.isBlank(configJson)) return;
		interfaceMockDao.updateSetting(mockId, settingType, configJson);
	}

	@Override
	public List<InterfaceMock> getEnableSocketMock() {
		
		return interfaceMockDao.getEnableSocketMock();
	}
}
