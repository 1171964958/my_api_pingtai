package yi.master.business.system.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.business.user.bean.Role;
import yi.master.business.user.bean.User;

/**
 *     业务菜单信息
 * @author xuwangcheng
 * @version 2018.12.27
 */
public class BusiMenuInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer menuId;
	private String menuName;
	private String menuUrl;
	private String iconName;
	private Integer nodeLevel;
	private Integer seq;
	private Timestamp createTime;
	private User createUser;
	private String mark;
	private BusiMenuInfo parentNode;
	private String status;
	private Set<BusiMenuInfo> childs = new HashSet<BusiMenuInfo>();

	private String parentNodeName;
	private Integer parentNodeId;
	
	private boolean isParent;
	private boolean isOwn;
	
	/**
	 * 对应的角色
	 */
	private Set<Role> roles = new HashSet<Role>();
	
	public BusiMenuInfo() {
		super();
	}

	public BusiMenuInfo(Integer menuId) {
		super();
		this.menuId = menuId;
	}
	
	public boolean getIsParent() {
		if (this.nodeLevel == 2) {
			return false;
		}
		return true;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public boolean getIsOwn() {
		return isOwn;
	}

	public void setIsOwn(boolean isOwn) {
		this.isOwn = isOwn;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	@JSON(serialize=false)
	public Set<Role> getRoles() {
		return roles;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setParentNodeName(String parentNodeName) {
		this.parentNodeName = parentNodeName;
	}
	
	public String getParentNodeName() {
		if (this.parentNode != null) {
			return this.parentNode.getMenuName();
		}
		return "";
	}
	
	public void setParentNodeId(Integer parentNodeId) {
		this.parentNodeId = parentNodeId;
	}
	
	public Integer getParentNodeId() {
		if (this.parentNode != null) {
			return this.parentNode.getMenuId();
		}
		return null;
	}
	
	public Integer getParentNodeId2() {
		return this.parentNodeId;
	}
	
	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public Integer getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(Integer nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	
	@JSON(serialize=false)
	public BusiMenuInfo getParentNode() {
		return parentNode;
	}

	public void setParentNode(BusiMenuInfo parentNode) {
		this.parentNode = parentNode;
	}
	
	@JSON(serialize=false)
	public Set<BusiMenuInfo> getChilds() {
		return childs;
	}

	public void setChilds(Set<BusiMenuInfo> childs) {
		this.childs = childs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((menuId == null) ? 0 : menuId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusiMenuInfo other = (BusiMenuInfo) obj;
		if (menuId == null) {
			if (other.menuId != null)
				return false;
		} else if (!menuId.equals(other.menuId))
			return false;
		return true;
	}
	
	
}
