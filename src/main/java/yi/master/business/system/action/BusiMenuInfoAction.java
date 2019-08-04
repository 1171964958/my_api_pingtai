package yi.master.business.system.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import yi.master.business.base.action.BaseAction;
import yi.master.business.system.bean.BusiMenuInfo;
import yi.master.business.system.service.BusiMenuInfoService;
import yi.master.business.user.bean.Role;
import yi.master.business.user.bean.User;
import yi.master.business.user.service.RoleService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.util.FrameworkUtil;


@Controller
@Scope("prototype")
public class BusiMenuInfoAction extends BaseAction<BusiMenuInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private RoleService roleService;
	private BusiMenuInfoService busiMenuInfoService;
	
	@Autowired
	public void setBusiMenuInfoService(BusiMenuInfoService busiMenuInfoService) {
		super.setBaseService(busiMenuInfoService);
		this.busiMenuInfoService = busiMenuInfoService;
	}
	
	@Override
	public String edit() {
		if (model.getParentNodeId2() != null) {
			model.setParentNode(new BusiMenuInfo(model.getParentNodeId2()));
		}
		if (model.getMenuId() == null) {
			model.setCreateUser((User) FrameworkUtil.getSessionMap().get("user"));
		}		
		return super.edit();
	}
	
	/**
	 * 获取当前用户的菜单列表，符合前端要求的格式
	 * @return
	 */
	public String getUserMenus() {
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		Role role = roleService.get(user.getRole().getRoleId());
		//获取一级菜单信息
		List<BusiMenuInfo> oneLevMenus = busiMenuInfoService.findAll("status='1'", "nodeLevel=0");
		//获取当前用户的菜单信息
		List<BusiMenuInfo> userMenus = new ArrayList<BusiMenuInfo>(role.getMenus());
		
		//组装菜单格式
		JSONObject json = formatMenuJson(oneLevMenus, userMenus);
		
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "").setData("data", json);
		return SUCCESS;
	}
	
	
	private JSONObject formatMenuJson(List<BusiMenuInfo> oneLevMenus, List<BusiMenuInfo> userMenus) {
		JSONObject json = new JSONObject();
		for (BusiMenuInfo m1:oneLevMenus) { //一级节点
			JSONObject jobj = new JSONObject();
			jobj.put("name", m1.getMenuName());
			jobj.put("icon", m1.getIconName());
			jobj.put("menu", new JSONArray());
			for (BusiMenuInfo m2:m1.getChilds()) { //二级节点
				JSONObject jobj2 = new JSONObject();
				jobj2.put("id", m1.getMenuId() + "-" + m2.getMenuId());
				jobj2.put("name", m2.getMenuName());
				jobj2.put("icon", m2.getIconName());
				
				jobj2.put("childs", new JSONArray());
				for (BusiMenuInfo m3:m2.getChilds()) { //三级节点
					for (BusiMenuInfo userM:userMenus) {
						if (userM.getMenuId().equals(m3.getMenuId())) {
							JSONObject jobj3 = new JSONObject();
							jobj3.put("_href", m3.getMenuUrl());
							jobj3.put("child_name", m3.getMenuName());
							jobj3.put("data-title", m3.getMenuName() + "-" + m2.getMenuName());
							
							jobj2.getJSONArray("childs").add(jobj3);
						}
					}
				}
				
				if (jobj2.getJSONArray("childs").size() > 0) {
					jobj.getJSONArray("menu").add(jobj2);
				}
			}
			if (jobj.getJSONArray("menu").size() > 0) {
				json.put(m1.getMenuId(), jobj);
			}
		}	
		
		System.out.println(json.toString());
		return json;
	}

}
