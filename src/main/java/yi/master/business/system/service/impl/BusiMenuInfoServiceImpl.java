package yi.master.business.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.system.bean.BusiMenuInfo;
import yi.master.business.system.dao.BusiMenuInfoDao;
import yi.master.business.system.service.BusiMenuInfoService;

@Service("busiMenuInfoService")
public class BusiMenuInfoServiceImpl extends BaseServiceImpl<BusiMenuInfo> implements BusiMenuInfoService  {
	
	private BusiMenuInfoDao busiMenuInfoDao;
	
	@Autowired
	public void setBusiMenuInfoDao(BusiMenuInfoDao busiMenuInfoDao) {
		super.setBaseDao(busiMenuInfoDao);
		this.busiMenuInfoDao = busiMenuInfoDao;
	}

}
