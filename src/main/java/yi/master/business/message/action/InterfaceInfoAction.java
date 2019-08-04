package yi.master.business.message.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.message.bean.Message;
import yi.master.business.message.bean.MessageScene;
import yi.master.business.message.bean.TestData;
import yi.master.business.message.service.InterfaceInfoService;
import yi.master.business.message.service.MessageSceneService;
import yi.master.business.message.service.MessageService;
import yi.master.business.message.service.TestDataService;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.service.BusinessSystemService;
import yi.master.business.user.bean.User;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.excel.ExportInterfaceInfo;
import yi.master.util.excel.ImportInterfaceInfo;
import yi.master.util.excel.PoiExcelUtil;

/**
 * 接口自动化
 * 接口信息Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
@Scope("prototype")
public class InterfaceInfoAction extends BaseAction<InterfaceInfo> {

	private static final long serialVersionUID = 1L;	
	
	private InterfaceInfoService interfaceInfoService;
	
	@Autowired
	private BusinessSystemService businessSystemService;
	
	@Autowired
	private MessageService messageService;
	@Autowired
	private TestDataService testDataService;
	@Autowired
	private MessageSceneService messageSceneService;
	
	private String path;	
	
	private String ids;
	
	private String oldIds;
	
	private Integer messageId;
	
	private String updateSystems;
	
	private Integer systemId;
	
	@Autowired
	public void setInterfaceInfoService(InterfaceInfoService interfaceInfoService) {
		super.setBaseService(interfaceInfoService);
		this.interfaceInfoService = interfaceInfoService;
	}
	
	@Override
	public String[] prepareList() {
		
		List<String> conditions = new ArrayList<String>();
		if (systemId != null) {
			conditions.add("exists (select 1 from InterfaceInfo o join o.systems s where s.systemId=" + systemId 
					+ " and o.interfaceId=t.interfaceId)");
		}
//		User user = (User) FrameworkUtil.getSessionMap().get("user");
//		if (!SystemConsts.ADMIN_ROLE_ID.equals(user.getRole().getRoleId())) {
//			conditions.add("user.userId=" + user.getUserId());
//		}
		this.filterCondition = conditions.toArray(new String[0]);
		return this.filterCondition;
	}


	/**
	 * 获取参数jsonTree数据
	 * @return
	 */
	public String getParametersJsonTree () {
		Object[] os = PracticalUtils.getParameterZtreeMap(interfaceInfoService.get(model.getInterfaceId()).getParameters());
		
		if (os == null) {
			jsonMap.put("msg", "没有可用的参数,请检查!");
			jsonMap.put("returnCode", ReturnCodeConsts.NO_RESULT_CODE);
			return SUCCESS;
		}
		
		jsonMap.put("data", os[0]);
		jsonMap.put("rootPid", Integer.parseInt(os[1].toString()));
		jsonMap.put("error", os[2].toString());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);	
		return SUCCESS;
	}
	
	/**
	 * 批量导出接口文档
	 * @return
	 */
	public String exportInterfaceDocument () {
		if (ids == null) {
			ids = "";
		}
		
		String[] ids_s = ids.split(",");
		
		List<InterfaceInfo> infos = new ArrayList<InterfaceInfo>();
		for (String s:ids_s) {
			infos.add(interfaceInfoService.get(Integer.valueOf(s)));
		}
		
		if (infos.size() < 1) {
			jsonMap.put("msg", "没有足够的数据可供导出,请刷新表格并重试!");
			jsonMap.put("returnCode", ReturnCodeConsts.MISS_PARAM_CODE);	
			return SUCCESS;
		}
		
		String path = null;
		
		try {
			path = ExportInterfaceInfo.exportDocuments(infos, PoiExcelUtil.XLSX);
		} catch (Exception e) {
			
			jsonMap.put("returnCode", ReturnCodeConsts.SYSTEM_ERROR_CODE);
			jsonMap.put("msg", "后台写文件出错:<br>" + e.getMessage() + "<br>请联系管理员!");
			return SUCCESS;
		}
		
		jsonMap.put("path", path);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);	
		return SUCCESS;
	}
	
	/**
	 * 从指定excel中导入数据
	 * @return
	 */
	public String importFromExcel () {
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		Map<String, Object> result = null;
		try {
			result = ImportInterfaceInfo.importToDB(path);
		} catch (Exception e) {
			
			setReturnInfo(ReturnCodeConsts.PARAMETER_VALIDATE_FAIL_CODE, "读取文件内容失败!");
		}
				
		jsonMap.put("result", result);
		
		
		return SUCCESS;
	}

	/**
	 * 更新接口
	 * 根据传入的interfaceId判断修改还是新增
	 */
	@Override
	public String edit() {
		User user = (User)FrameworkUtil.getSessionMap().get("user");
		//判断接口名是否重复
		checkObjectName();
		if (!checkNameFlag.equals("true")) {
			jsonMap.put("returnCode", ReturnCodeConsts.NAME_EXIST_CODE);
			jsonMap.put("msg", "该接口名已存在,请更换!");
			
			return SUCCESS;
		}
		if (model.getInterfaceId() == null) {
			//新增									
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			model.setUser(user);				
		}
		
		model.setLastModifyUser(user.getRealName());	
		
		//设置测试环境
		Set<BusinessSystem> systems = new HashSet<BusinessSystem>();
		for (String id:ids.split(",")) {
			BusinessSystem system = businessSystemService.get(Integer.valueOf(id));
			if (system != null) {
				systems.add(system);
			}
		}
		model.setSystems(systems);
		
		interfaceInfoService.edit(model);
		
		/***********************************/	
		if (model.getInterfaceId() != null) {
			Map<String, List<Integer>> updateSystems = checkBusinessSystemUpdate();
			if (updateSystems != null) {
				jsonMap.put("msg", "你刚刚更新了接口的测试环境关联:<br><strong>新增关联" + updateSystems.get("addSystems").size() 
						+ "个,解除关联" + updateSystems.get("removeSystems").size() + "个。</strong><br>"
						+ "<span class=\"c-red\">是否需要同步更新该接口下属对象的测试环境信息？</span>");
				jsonMap.put("updateSystems", updateSystems);
			}
		}		
		/*****************************************/
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
		
	
	/**
	 * 判断接口名重复性
	 * 新增或者修改状态下均可用
	 */
	@Override
	public void checkObjectName() {
		InterfaceInfo info = interfaceInfoService.findInterfaceByName(model.getInterfaceName());
		checkNameFlag = (info != null && !info.getInterfaceId().equals(model.getInterfaceId())) ? "重复的接口名" : "true";
		
		if (model.getInterfaceId() == null) {
			checkNameFlag = (info == null) ? "true" : "重复的接口名";
		}
	}
	
	/**
	 * 根据接口所属的测试环境更新下属报文、场景、数据的相关信息<br>
	 * 更新规则：下属所有报文场景数据没有的就增加，多余的就删除
	 * @return
	 */
	public String updateChildrenBusinessSystems () {
		model = interfaceInfoService.get(model.getInterfaceId());
		
		if (model != null) {
			Collection<String> removeSystems = new HashSet<String>();
			Collection<String> addSystems = new HashSet<String>();
			if (StringUtils.isNotBlank(updateSystems)) {
				JSONObject updateSystemsObj = JSONObject.fromObject(updateSystems);
				removeSystems = JSONArray.toCollection(updateSystemsObj.getJSONArray("removeSystems"), String.class);
				addSystems = JSONArray.toCollection(updateSystemsObj.getJSONArray("addSystems"), String.class);
			} else {
				for (BusinessSystem sys:model.getSystems()) {
					addSystems.add(String.valueOf(sys.getSystemId()));					
				}
			}
			for (Message msg:model.getMessages()) {
				msg.setSystems(removeBusinessSystems(addBusinessSystems(msg.getSystems(), addSystems), removeSystems));
				messageService.edit(msg);
				
				for (MessageScene scene:msg.getScenes()) {
					scene.setSystems(removeBusinessSystems(addBusinessSystems(scene.getSystems(), addSystems), removeSystems));
					messageSceneService.edit(scene);
					
					for (TestData data:scene.getTestDatas()) {
						data.setSystems(removeBusinessSystems(addBusinessSystems(data.getSystems(), addSystems), removeSystems));;
						testDataService.edit(data);
					}
				}
				
			}
		}
				
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);		
		
		return SUCCESS;
	}
	
	@Override
	public String get() {
		
		if (id == null) {
			jsonMap.put("object", messageService.get(messageId).getInterfaceInfo());
		} 
		
		if (id != null) {
			jsonMap.put("object", interfaceInfoService.get(id));
		}
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);		
		
		return SUCCESS;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	
	public void setPath(String path) {
		this.path = path;
	}	
	
	public void setIds(String ids) {
		this.ids = ids;
	}
	
	public void setOldIds(String oldIds) {
		this.oldIds = oldIds;
	}
	
	public void setUpdateSystems(String updateSystems) {
		this.updateSystems = updateSystems;
	}
	
	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}
	
	public Map<String, List<Integer>> checkBusinessSystemUpdate () {
		
		Map<String, List<Integer>> returnObj = null;
		String[] idsNew = ids.split(",");
		String[] idsOld = oldIds.split(",");
		
		Arrays.sort(idsNew);
		Arrays.sort(idsOld);
		
		if (Arrays.equals(idsNew, idsOld)) {
			return returnObj;
		}
		
		returnObj = new HashMap<String, List<Integer>>();
		List<Integer> addIds = new ArrayList<Integer>();
		List<Integer> delIds = new ArrayList<Integer>();
		
		//判断有没有新增的
		label1:
		for (String idNew:idsNew) {
			for (String idOld:idsOld) {
				if (idNew.equals(idOld)) {
					continue label1;
				}				
			}
			if (PracticalUtils.isNumeric(idNew)) {
				addIds.add(Integer.valueOf(idNew));
			}			
		}
		
		//判断有没有删除的
		label1:
		for (String idOld:idsOld) {
			for (String idNew:idsNew) {
				if (idOld.equals(idNew)) {
					continue label1;
				}				
			}
			if (PracticalUtils.isNumeric(idOld)) {
				delIds.add(Integer.valueOf(idOld));
			}			
		}
		
		
		returnObj.put("addSystems", addIds);
		returnObj.put("removeSystems", delIds);
		
		return returnObj;
	
	}
	
	/**
     * 添加测试环境
     * @param addSystems
     */
    public String addBusinessSystems (String systems, Collection<String> addSystems) {
    	Set<String> systemsSet = new HashSet<String>();
    	if (StringUtils.isNotBlank(systems)) {
    		systemsSet = new HashSet<String>(Arrays.asList(systems.split(",")));
    	} 
    	  
    	label:
    	for (String s:addSystems) {
    		for (Iterator iter = systemsSet.iterator(); iter.hasNext();) {
    			if (s.equals((String)iter.next())) {
    				continue label;
    			}
        	}
    		systemsSet.add(s);
	     }
	
    	return StringUtils.join(systemsSet, ",");
    }
    
    /**
     * 删除测试环境
     * @param removeSystems
     */
    public String removeBusinessSystems (String systems, Collection<String> removeSystems) {
    	Set<String> systemsSet = new HashSet<String>();
    	if (StringUtils.isNotBlank(systems)) {
    		systemsSet = new HashSet<String>(Arrays.asList(systems.split(",")));
    	}   	
    	
    	label:
    	for (String s:removeSystems) {
    		for (Iterator iter = systemsSet.iterator(); iter.hasNext();) {
    			if (s.equals((String)iter.next())) {
    				iter.remove();
    				continue label;
    			}
        	}
	     }
	
    	return StringUtils.join(systemsSet, ",");
    }
}
