package yi.master.business.user.action;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.system.bean.BusiMenuInfo;
import yi.master.business.system.bean.OperationInterface;
import yi.master.business.system.service.BusiMenuInfoService;
import yi.master.business.user.bean.Role;
import yi.master.business.user.service.RoleService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.constant.SystemConsts;
import yi.master.util.FrameworkUtil;

/**
 * 角色信息Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
@Scope("prototype")
public class RoleAction extends BaseAction<Role> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private BusiMenuInfoService busiMenuInfoService;
	
	private RoleService roleService;
	@Autowired
	public void setRoleService(RoleService roleService) {
		super.setBaseService(roleService);
		this.roleService = roleService;
	}
	
	
	/**
	 * 指定角色被删除的操作权限的id集合
	 * 以逗号分隔
	 */
	private String delOpIds;
	
	/**
	 * 指定角色增加的操作权限的id集合
	 * 以逗号分隔
	 */
	private String addOpIds;
	
	/**
	 * 绑定每个角色的拥有权限个数
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object processListData(Object o) {
		List<Role> roles = (List<Role>) o;
		for (Role r:roles) {
			r.setOiNum();
			r.setMenuNum();
		}
		
		return roles;
	}
	
	/**
	 * 删除角色,但是不能删除预置的管理员角色和默认角色
	 */
	@Override
	public String del() {
		if (id == SystemConsts.ADMIN_ROLE_ID || id == SystemConsts.DEFAULT_ROLE_ID) {
			jsonMap.put("returnCode",ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			jsonMap.put("msg", "不能删除超级管理员角色或者默认角色");
			
			return SUCCESS;
		}		
		//删除其他角色,配置该角色的用户变更成default角色
		roleService.del(id);		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	@Override
	public void checkObjectName() {
		Role role = roleService.get(model.getRoleName());
		checkNameFlag = (role != null && !role.getRoleId().equals(model.getRoleId())) ? "重复的角色名" : "true";
		
		if (model.getRoleId() == null) {
			checkNameFlag = (role == null) ? "true" : "重复的接口名";
		}
	}
	
	/**
	 * 编辑角色信息
	 * 根据传入的id判断是否为新增或者更新
	 */
	@Override
	public String edit() {		
		if (model.getRoleId() == SystemConsts.ADMIN_ROLE_ID || model.getRoleId() == SystemConsts.DEFAULT_ROLE_ID) {			
			jsonMap.put("returnCode", ReturnCodeConsts.ILLEGAL_HANDLE_CODE);
			jsonMap.put("msg", "不能删除预置管理员或者默认角色信息");			
			return SUCCESS;
		}		
		checkObjectName();	
		
		if (!checkNameFlag.equals("true")) {
			jsonMap.put("returnCode", ReturnCodeConsts.NAME_EXIST_CODE);
			jsonMap.put("msg", "该角色名已存在,请更换!");			
			return SUCCESS;
		}
		
		if (model.getRoleId() != null) {
			//修改
			model.setOis(roleService.get(model.getRoleId()).getOis());
		}
		roleService.edit(model);		
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);		
		return SUCCESS;
	}
	
	/**
	 * 获取当前所有的操作接口列表
	 * 并且对当前角色已拥有的操作接口打标记
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getInterfaceNodes() {		
		List<OperationInterface> ops = (List<OperationInterface>) FrameworkUtil.getApplicationMap().get("ops");				
		Role role = roleService.get(model.getRoleId());
		Set<OperationInterface> ownOps = role.getOis();
		
		for (OperationInterface op : ops) {
			op.setIsOwn(false);			
			for (OperationInterface op1 : ownOps) {
				if (op1.getOpId().equals(op.getOpId())) {
					op.setIsOwn(true);
				}
			}
		}
				
		jsonMap.put("interfaces", ops);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}
	
	/**
	 * 获取当前用户的菜单树
	 * @return
	 */
	public String getMenuNodes() {
		List<BusiMenuInfo> menus = busiMenuInfoService.findAll();
		Role role = roleService.get(model.getRoleId());
		Set<BusiMenuInfo> ownMenus = role.getMenus();
		
		for (BusiMenuInfo m:menus) {
			m.setIsOwn(false);
			for (BusiMenuInfo ownM:ownMenus) {
				if (ownM.getMenuId().equals(m.getMenuId())) {
					m.setIsOwn(true);
				}
			}
		}
		
		jsonMap.put("menus", menus);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}

	/**
	 * 更新菜单与角色的关系
	 * @return
	 */
	public String updateRoleMenu() {
		Role role = roleService.get(model.getRoleId());
		Set<BusiMenuInfo> menus = role.getMenus();
		
		//更新增加的菜单
		BusiMenuInfo menu = null;
		if (addOpIds != null && !addOpIds.isEmpty()) {
			String[] addOpArray = addOpIds.split(",");
			for (String s : addOpArray) {
				menu = new BusiMenuInfo();
				menu.setMenuId(Integer.valueOf(s));
				menus.add(menu);
			}
		}
		
		//更新删除的权限
		if (delOpIds != null && !delOpIds.isEmpty()) {
			String[] delOpArray = delOpIds.split(",");
			for (String s : delOpArray) {
				menu = new BusiMenuInfo();
				menu.setMenuId(Integer.valueOf(s));
				menus.remove(menu);
			}
		}
		role.setMenus(menus);
		roleService.edit(role);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**
	 * 更新角色的权限信息
	 * 更新角色与操作接口的关联关系
	 * @return
	 */
	public String updateRolePower() {
		Role role = roleService.get(model.getRoleId());
		Set<OperationInterface> ops = role.getOis();
		//更新增加的权限
		OperationInterface o = null;
		if (addOpIds != null && !addOpIds.isEmpty()) {
			String[] addOpArray = addOpIds.split(",");
			for (String s : addOpArray) {
				o = new OperationInterface();
				o.setOpId(Integer.valueOf(s));
				ops.add(o);
			}
			
		}
		//更新删除的权限
		if (delOpIds != null && !delOpIds.isEmpty()) {
			String[] delOpArray = delOpIds.split(",");
			for (String s : delOpArray) {
				o = new OperationInterface();
				o.setOpId(Integer.valueOf(s));
				ops.remove(o);
			}
		}
		role.setOis(ops);
		roleService.edit(role);
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/********************************************************************************************/
	public void setDelOpIds(String delOpIds) {
		this.delOpIds = delOpIds;
	}
	
	public void setAddOpIds(String addOpIds) {
		this.addOpIds = addOpIds;
	}
	
	
}
