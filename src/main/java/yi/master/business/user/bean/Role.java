package yi.master.business.user.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;
// default package

import yi.master.annotation.FieldNameMapper;
import yi.master.business.system.bean.BusiMenuInfo;
import yi.master.business.system.bean.OperationInterface;



/**
 * 角色信息
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */

public class Role implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer roleId;
	
	/**
	 * 角色组
	 * 默认
	 */
	private String roleGroup;
	
	/**
	 * 角色名
	 */
	private String roleName;
	
	/**
	 * 备注
	 */
	private String mark;
	
	/**
	 * 拥有的操作接口
	 */
	private Set<OperationInterface> ois = new HashSet<OperationInterface>();
	
	/**
	 * 拥有的菜单
	 */
	private Set<BusiMenuInfo> menus = new HashSet<BusiMenuInfo>();
	
	/**
	 * 对应用户
	 */
	private Set<User> users = new HashSet<User>();
	
	/**
	 * 拥有的权限个数
	 */
	@FieldNameMapper(fieldPath="size(ois)",ifSearch=false)
	private Integer oiNum;
	
	/**
	 * 拥有的菜单个数
	 */
	@FieldNameMapper(fieldPath="size(menus)",ifSearch=false)
	private Integer menuNum;
	
	@JSON(serialize=false)
	public Set<BusiMenuInfo> getMenus() {
		return menus;
	}
	public void setMenus(Set<BusiMenuInfo> menus) {
		this.menus = menus;
	}
	public Integer getMenuNum() {
		return menuNum;
	}
	public void setMenuNum() {
		this.menuNum = this.menus.size();
	}
	@JSON(serialize=false)
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}	
		
	public Integer getOiNum() {
		return oiNum;
	}
	public void setOiNum() {
		this.oiNum = ois.size();
	}
	@JSON(serialize=false)
	public Set<OperationInterface> getOis() {
		return ois;
	}
	public void setOis(Set<OperationInterface> ois) {
		this.ois = ois;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getRoleGroup() {
		return roleGroup;
	}
	public void setRoleGroup(String roleGroup) {
		this.roleGroup = roleGroup;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public Role(String roleGroup, String roleName, String mark) {
		super();
		this.roleGroup = roleGroup;
		this.roleName = roleName;
		this.mark = mark;
	}
	
	
	
	public Role(Integer roleId) {
		super();
		this.roleId = roleId;
	}
	public Role() {
		super();
		
	}
	@Override
	public String toString() {
		return "Role [roleId=" + roleId + ", roleGroup=" + roleGroup
				+ ", roleName=" + roleName + ", mark=" + mark + "]";
	}	
}