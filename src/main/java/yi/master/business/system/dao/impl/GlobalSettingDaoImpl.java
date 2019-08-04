package yi.master.business.system.dao.impl;

import org.springframework.stereotype.Repository;

import yi.master.business.base.dao.impl.BaseDaoImpl;
import yi.master.business.system.bean.GlobalSetting;
import yi.master.business.system.dao.GlobalSettingDao;

/**
 * 全局设置项Dao实现
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Repository("globalSettingDao")
public class GlobalSettingDaoImpl extends BaseDaoImpl<GlobalSetting> implements GlobalSettingDao {

	@Override
	public void updateSetting(String settingName, String settingValue) {
		String hql = "update GlobalSetting g "
				+ "set g.settingValue=:settingValue "
				+ "where g.settingName=:settingName";
		
		getSession().createQuery(hql)
			.setString("settingValue", settingValue)
			.setString("settingName",settingName)
			.executeUpdate();		
	}

}
