package yi.master.business.system.service;

import yi.master.business.base.service.BaseService;
import yi.master.business.system.bean.GlobalSetting;

/**
 * 全局设置项Service接口
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public interface GlobalSettingService extends BaseService<GlobalSetting> {
	
	/**
	 * 更新某一项设置
	 * @param settingName  设置名
	 * @param settingValue 设置的值
	 */
	void updateSetting(String settingName,String settingValue);
}
