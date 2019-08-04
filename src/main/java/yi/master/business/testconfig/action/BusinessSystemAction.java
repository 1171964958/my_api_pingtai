package yi.master.business.testconfig.action;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.base.bean.PageModel;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.service.BusinessSystemService;
import yi.master.business.user.bean.User;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class BusinessSystemAction extends BaseAction<BusinessSystem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BusinessSystemService businessSystemService;
	
	private String mode;
	
	private Integer interfaceId;
	
	@Autowired
	public void setBusinessSystemService(
			BusinessSystemService businessSystemService) {
		super.setBaseService(businessSystemService);
		this.businessSystemService = businessSystemService;
	}
	

	@Override
	public String listAll() {
		
		if (model.getProtocolType() != null) {
			List<BusinessSystem> ts = businessSystemService.findAll("protocolType='" + model.getProtocolType() + "'");
			jsonMap.put("data", processListData(ts));
			jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
			
			return SUCCESS;
		}
		return super.listAll();
	}

	@Override
	public String edit() {
		
		User user = (User)FrameworkUtil.getSessionMap().get("user");
		model.setLastModifyUser(user.getRealName());
		businessSystemService.edit(model);
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}


	@Override
	public String del() {
		
		model = businessSystemService.get(id);
		if (model.getInfoCount() > 0) {
			jsonMap.put("msg", "该测试环境尚有接口信息,请先解除关联!");
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			return SUCCESS;
		}
		return super.del();
	}
	
	/**
	 * 操作指定测试环境的接口信息(添加或删除)
	 * @return
	 */
	public String opInterface() {
		//增加接口到测试环境
		if ("1".equals(mode)) {
			businessSystemService.addInterfaceToSystem(model.getSystemId(), interfaceId == null ? id : interfaceId);
		}
		
		//从测试环境删除接口
		if ("0".equals(mode)) {
			businessSystemService.delInterfaceFromSystem(model.getSystemId(), interfaceId == null ? id : interfaceId);
		}
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
		return SUCCESS;
	}
	
	/**
	 * 查询指定测试环境包含或者不包含的接口信息
	 * @return
	 */
	public String listInterface() {
		model = businessSystemService.get(model.getSystemId());		
		Map<String,Object>  dt = FrameworkUtil.getDTParameters(InterfaceInfo.class);
		PageModel<InterfaceInfo> pm = businessSystemService.listSystemInterface(model.getSystemId(), start, length 
				,(String)dt.get("orderDataName"),(String)dt.get("orderType")
				,(String)dt.get("searchValue"),(List<List<String>>)dt.get("dataParams"),Integer.parseInt(mode),  model.getProtocolType());
		
		jsonMap.put("draw", draw);
		jsonMap.put("data", processListData(pm.getDatas()));
		jsonMap.put("recordsTotal", pm.getRecordCount());		
		jsonMap.put("recordsFiltered", pm.getFilteredCount());
		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	public void setInterfaceId(Integer interfaceId) {
		this.interfaceId = interfaceId;
	}
}
