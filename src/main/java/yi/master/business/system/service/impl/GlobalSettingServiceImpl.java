package yi.master.business.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yi.master.business.base.service.impl.BaseServiceImpl;
import yi.master.business.system.bean.GlobalSetting;
import yi.master.business.system.dao.GlobalSettingDao;
import yi.master.business.system.service.GlobalSettingService;


/**
 * 全局设置项Service接口实现类
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Service("globalSettingService")
public class GlobalSettingServiceImpl extends BaseServiceImpl<GlobalSetting> implements GlobalSettingService {
	
	private GlobalSettingDao globalSettingDao;
	
	@Autowired
	public void setGlobalSettingDao(GlobalSettingDao globalSettingDao) {
		super.setBaseDao(globalSettingDao);
		this.globalSettingDao = globalSettingDao;
	}
	
	/**
	 * 更新单个全局设置项
	 * @param settingName
	 * @param settingValue
	 */
	@Override
	public void updateSetting(String settingName, String settingValue) {
		globalSettingDao.updateSetting(settingName, settingValue);		
	}

}
