package yi.master.business.system.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldRealSearch;
import yi.master.business.user.bean.Role;

/**
 * 操作接口信息
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */
public class OperationInterface implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer opId;
	
	/**
	 * 名称
	 */
	private String opName;
	
	/**
	 * 调用名
	 */
	private String callName;
	
	/**
	 * 是否为父节点
	 */
	private String isParent;
	
	/**
	 * 操作接口类型
	 * 通用接口 
	 */
	private String opType; 
	
	/**
	 * 备注
	 */
	private String mark;
	
	/**
	 * 当前状态
	 */
	@FieldRealSearch(names = {"正常", "禁用"}, values = {"0", "1"})
	private String status;
	
	/**
	 * 父节点
	 */
	private OperationInterface oi;
	
	/**
	 * 对应的角色
	 */
	private Set<Role> roles = new HashSet<Role>();
	
	/**
	 * 子节点
	 */
	private Set<OperationInterface> ois = new HashSet<OperationInterface>();
	
	/**
	 * 属于标记
	 */
	private Boolean isOwn;
	
	/**
	 * 父节点id
	 */
	public Integer parentOpId;
	
	/**
	 * 父节点名称
	 */
	private String parentOpName;
	

	/**
	 * 获取当前操作接口的所有子接口
	 * 暂时只有一层父节点
	 * 不用递归获取
	 * @return
	 */
	@JSON(serialize=false)
	public Set<OperationInterface> getAllOis() {
		Set<OperationInterface> ops1 = new HashSet<OperationInterface>();
		for (OperationInterface op:this.getOis()) {
			ops1.add(op);
			if (op.getIsParent().equals("true")) {
				ops1.addAll(op.getOis());
			}
		}
		return ops1;
	}

	public OperationInterface(String opName, String callName, String isParent,
			String mark, String opType,String status, OperationInterface oi) {
		super();
		this.opName = opName;
		this.callName = callName;
		this.isParent = isParent;
		this.mark = mark;
		this.opType = opType;
		this.status = status;
		this.oi = oi;
	}

	public OperationInterface(Integer opId) {
		super();
		this.opId = opId;
	}
	
	public OperationInterface() {
		super();
		
	}
	
	
	public String getParentOpName() {
		return this.getOi().getOpName();
	}

	public void setParentOpName(String parentOpName) {
		this.parentOpName = parentOpName;
	}

	public Integer getParentOpId() {
		return this.getOi().getOpId();
	}

	@JSON(serialize=false)
	public Integer getParentOpId2() {
		return this.parentOpId;
	}
	
	public void setParentOpId(Integer parentOpId) {
		this.parentOpId = parentOpId;
	}

	public Boolean getIsOwn() {
		return isOwn;
	}

	public void setIsOwn(Boolean isOwn) {
		this.isOwn = isOwn;
	}

	@JSON(serialize=false)
	public Set<Role> getRoles() {
		return roles;
	}
		
	
	public void setOpType(String opType) {
		this.opType = opType;
	}
	
	public String getOpType() {
		return opType;
	}
	
	
	public String getIsParent() {
		return isParent;
	}

	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}

	@JSON(serialize=false)
	public Set<OperationInterface> getOis() {
		return ois;
	}

	public void setOis(Set<OperationInterface> ois) {
		this.ois = ois;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	@JSON(serialize=false)
	public OperationInterface getOi() {
		return oi;
	}

	public void setOi(OperationInterface oi) {
		this.oi = oi;
	}

	public Integer getOpId() {
		return opId;
	}
	public void setOpId(Integer opId) {
		this.opId = opId;
	}
	public String getOpName() {
		return opName;
	}
	public void setOpName(String opName) {
		this.opName = opName;
	}
	public String getCallName() {
		if (callName == null) {
			return "";
		}
		return callName;
	}
	public void setCallName(String callName) {
		this.callName = callName;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "OperationInterface [opId=" + opId + ", opName=" + opName
				+ ", callName=" + callName + ", isParent=" + isParent
				+ ", opType=" + opType + ", mark=" + mark + ", status="
				+ status + ", isOwn=" + isOwn + ", parentOpId=" + parentOpId
				+ ", parentOpName=" + parentOpName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opId == null) ? 0 : opId.hashCode());
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
		OperationInterface other = (OperationInterface) obj;
		if (opId == null) {
			if (other.opId != null)
				return false;
		} else if (!opId.equals(other.opId))
			return false;
		return true;
	}
}
