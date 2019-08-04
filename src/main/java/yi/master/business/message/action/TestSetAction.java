package yi.master.business.message.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.base.bean.PageModel;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.bean.TestConfig;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.business.testconfig.service.TestConfigService;
import yi.master.business.user.bean.User;
import yi.master.constant.ReturnCodeConsts;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;

/**
 * 接口自动化<br>
 * 测试集Action
 * @author xuwangcheng
 * @version 1.0.0.0,20170518
 *
 */
@Controller
@Scope("prototype")
public class TestSetAction extends BaseAction<TestSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TestSetService testSetService;
	
	@Autowired
	private TestConfigService testConfigService;
	@Autowired
	private GlobalVariableService globalVariableService;
	
	/**
	 * (1)、添加还是删除场景,0-增加 1-删除<br>
	 * (2)、查询存在于测试集或者不存在测试集中的测试场景,0-存在于测试集  1-不存在于测试集<br>
	 * (3)、更改测试集的运行时配置,0-更改为自定义  1-更改为默认
	 */
	private String mode;
	
	private Integer messageSceneId;
	
	private Integer variableId;
	
	private Integer parentId;
	
	@Autowired
	public void setTestSetService(TestSetService testSetService) {
		super.setBaseService(testSetService);
		this.testSetService = testSetService;
	}

	
	@Override
	public String[] prepareList() {
		
		List<String> conditions = new ArrayList<String>();
		conditions.add("parented=1");
		
		/*User user = (User) FrameworkUtil.getSessionMap().get("user");
		if (!SystemConsts.ADMIN_ROLE_ID.equals(user.getRole().getRoleId())) {
			conditions.add("user.userId=" + user.getUserId());
		}*/
		this.filterCondition = conditions.toArray(new String[0]);
		return this.filterCondition;
	}

	/**
	 * 获取目录树结构
	 * @return
	 */
	public String getCategoryNodes () {
		List<TestSet> rootSets = testSetService.getRootSet();
		
		List<Map<String, Object>> sets = new ArrayList<Map<String, Object>>();
		for (TestSet set:rootSets) {
			sets.add(set.getNodesMap(testSetService));
		}
		jsonMap.put("nodes", sets);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}


	@Override
	public String edit() {
				
		if (model.getSetId() == null) {
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			model.setUser((User)(FrameworkUtil.getSessionMap().get("user")));	
			if ("1".equals(model.getParented())) {
				//创建测试配置
				TestConfig config = (TestConfig) testConfigService.getConfigByUserId(0).clone();
				config.setConfigId(null);
				config.setUserId(null);
				testConfigService.save(config);
				model.setConfig(config);
			}
		} else {
			model.setMs(testSetService.get(model.getSetId()).getMs());
		}
		if (parentId != 0) {
			model.setParentSet(new TestSet(parentId));
		}		
		testSetService.edit(model);
		
		jsonMap.put("object", model);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 该测试集当前拥有的测试场景或不存在的测试场景<br>
	 * 根据mode参数 0-该测试集拥有的场景  1-该测试集可以添加的场景
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String listScenes() {				
		Map<String,Object>  dt = FrameworkUtil.getDTParameters(MessageScene.class);
		PageModel<MessageScene> pm = testSetService.listSetMessageScene(model.getSetId(), start, length 
				,(String)dt.get("orderDataName"),(String)dt.get("orderType")
				,(String)dt.get("searchValue"),(List<List<String>>)dt.get("dataParams"), Integer.parseInt(mode));
		
		jsonMap.put("draw", draw);
		jsonMap.put("data", processListData(pm.getDatas()));
		jsonMap.put("recordsTotal", pm.getRecordCount());		
		jsonMap.put("recordsFiltered", pm.getFilteredCount());
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 移动测试集到文件夹目录下
	 * @return
	 */
	public String moveFolder () {
		
		testSetService.moveFolder(model.getSetId(), parentId);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 操作测试场景到测试集
	 * @return
	 */
	public String opScene() {
		//增加场景到测试集
		if ("1".equals(mode)) {
			testSetService.addSceneToSet(model.getSetId(), messageSceneId == null ? id : messageSceneId);
		}
		
		//从测试集删除场景
		if ("0".equals(mode)) {
			testSetService.delSceneToSet(model.getSetId(), messageSceneId == null ? id : messageSceneId);
		}
		
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 获取当前用户拥有的测试集
	 * @return
	 */
	public String getMySet () {			
		jsonMap.put("data", testSetService.findAll("parented=1"));
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	
	/**
	 * 设置运行时配置
	 * @return
	 */
	public String settingConfig() {
		model = testSetService.get(model.getSetId());
		TestConfig config = null;
		
		if ("0".equals(mode)) {
			config = (TestConfig) testConfigService.getConfigByUserId(0).clone();
			config.setConfigId(null);
			config.setUserId(null);
			testConfigService.save(config);
			model.setConfig(config);
		} else {
			if (model.getConfig() != null) {
				Integer configId = model.getConfig().getConfigId();
				model.setConfig(null);
				testConfigService.delete(configId);
			}
		}
			
		if (variableId != null) {
			//配置模板
			GlobalVariable v = globalVariableService.get(variableId);
			config = (TestConfig) v.createSettingValue();
			testConfigService.save(config);
			model.setConfig(config);
		}
		
		testSetService.edit(model);
		
		jsonMap.put("config", config);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**************************************************************/
	public void setMode(String mode) {
		this.mode = mode;
	}
	public void setMessageSceneId(Integer messageSceneId) {
		this.messageSceneId = messageSceneId;
	}
	
	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
}
