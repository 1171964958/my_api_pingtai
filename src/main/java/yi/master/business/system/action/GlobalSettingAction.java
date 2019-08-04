package yi.master.business.system.action;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.system.bean.GlobalSetting;
import yi.master.business.system.service.GlobalSettingService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.statement.AnalyzeUtil;
import yi.master.util.FrameworkUtil;
import yi.master.util.cache.CacheUtil;

/**
 * 全局设置Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
@Scope("prototype")
public class GlobalSettingAction extends BaseAction<GlobalSetting>{

	private static final long serialVersionUID = 1L;	
	
	private GlobalSettingService globalSettingService;
	
	/*@Autowired
	private TestRedisService testRedisService;*/
	
	@Autowired
	public void setGlobalSettingService(GlobalSettingService globalSettingService) {
		super.setBaseService(globalSettingService);
		this.globalSettingService = globalSettingService;
	}
	
	/**
	 * 将设置中字段值为null的转换成""
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object processListData(Object o) {
		List<GlobalSetting> settings = (List<GlobalSetting>) o;
		for (GlobalSetting g:settings) {
			if (g.getSettingValue() == null) {
				g.setSettingValue("");
			}
		}
		
		return o;
	}
	
	/**
	 * 测试统计
	 * @return
	 */
	public String getStatisticalQuantity () {
		
		jsonMap.put("counts", AnalyzeUtil.countStatistics());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 获取当前网站的所有设置属性
	 * @return
	 */
	public String getWebSettings() {
		Map<String,GlobalSetting> settingMap = CacheUtil.getGlobalSettingMap();
		
		for (GlobalSetting setting:settingMap.values()) {
			jsonMap.put(setting.getSettingName(), CacheUtil.getSettingValue(setting.getSettingName()));
		}
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
		
	}
	
	/**
	 * 编辑指定设置项
	 */
	@Override
	public String edit() {
		for (Map.Entry<String, Object> entry:FrameworkUtil.getParametersMap().entrySet()) {
			globalSettingService.updateSetting(entry.getKey(), ((String[])entry.getValue())[0]);
			CacheUtil.updateGlobalSettingValue(entry.getKey(), ((String[])entry.getValue())[0]);
		}
		/*List<GlobalSetting> settings = globalSettingService.findAll();
		Map<String,GlobalSetting> globalSettingMap = new HashMap<String,GlobalSetting>();
		//更新完成之后需要将更新的设置重新加载在session中
		for (GlobalSetting g:settings) {
			globalSettingMap.put(g.getSettingName(), g);
		}
		StrutsUtils.getApplicationMap().put("settingMap", globalSettingMap);*/
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/*****************************************************************************************************/
/*	public String setRedis () {
		List<GlobalSetting> settings = globalSettingService.findAll();
		Map<String, String> map = null;
		for (GlobalSetting s:settings) {
			map = new HashMap<String, String>();
			map.put("settingId", String.valueOf(s.getSettingId()));
			map.put("settingName", s.getSettingName());
			map.put("settingValue", s.getSettingValue());
			map.put("defaultValue", s.getDefaultValue());
			map.put("mark", s.getMark());
			testRedisService.set("setting" + s.getSettingId(), map, -1);
		}
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
		
	}
	
	public String listRedis () {
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}

	
	public String getRedis () {
		jsonMap.put("object", testRedisService.get("setting" + model.getSettingId()));
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}*/

}
