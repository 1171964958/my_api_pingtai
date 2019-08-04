package yi.master.business.testconfig.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.FieldNameMapper;
import yi.master.business.message.bean.InterfaceInfo;
import yi.master.constant.MessageKeys;

/**
 * 测试环境信息类
 * @author xuwangcheng
 * @version 1.0.0.0,20180114
 *
 */
public class BusinessSystem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer systemId;
	
	/**
	 * 系统名称，包含详细<br>
	 * 例如 服务集成平台-测试环境
	 */
	private String systemName;
	/**
	 * IP地址
	 */
	private String systemHost;
	/**
	 * 业务端口
	 */
	private String systemPort;
	/**
	 * 支持协议
	 */
	private String protocolType;
	/**
	 * 应用名称，自定义<br>
	 * 如nginx、f5
	 */
	private String softwareName;
	/**
	 * 修改用户
	 */
	private String lastModifyUser;
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	private String mark;
	
	@FieldNameMapper(fieldPath="size(interfaceInfos)",ifSearch=false)
	private Integer infoCount;
	
	/**
	 * 当前状态<br>
	 * 1 - 不可用
	 * 0 - 可用
	 */
	private String status;
	/**
	 * 默认路径,如果在接口中没有配置路径则使用该路径<br>使用${name}表示接口名称,${path}表示在接口中或者报文中定义的路径
	 */
	private String defaultPath;
	
	
	/**
	 * 拥有的接口信息
	 */
	private Set<InterfaceInfo> interfaceInfos = new HashSet<InterfaceInfo>();
	
	public BusinessSystem(Integer systemId, String systemName,
			String systemHost, String systemPort, String protocolType,
			String softwareName, String lastModifyUser, Timestamp createTime,
			String mark) {
		super();
		this.systemId = systemId;
		this.systemName = systemName;
		this.systemHost = systemHost;
		this.systemPort = systemPort;
		this.protocolType = protocolType;
		this.softwareName = softwareName;
		this.lastModifyUser = lastModifyUser;
		this.createTime = createTime;
		this.mark = mark;
	}

	public BusinessSystem() {
		super();
	}

	public String getReuqestUrl(String requestPath, String defaultPath, String interfaceName) {
				
		if (MessageKeys.MESSAGE_PROTOCOL_HTTP.equalsIgnoreCase(this.protocolType) || 
				MessageKeys.MESSAGE_PROTOCOL_WEBSERVICE.equalsIgnoreCase(this.protocolType) || 
				MessageKeys.MESSAGE_PROTOCOL_HTTPS.equalsIgnoreCase(this.protocolType)) {
			if (StringUtils.isBlank(requestPath) || requestPath.indexOf("/") != 0) {
				requestPath = defaultPath.replaceAll(MessageKeys.BUSINESS_SYSTEM_DEFAULTPATH_NAME_ATTRIBUTE, interfaceName)
						.replaceAll(MessageKeys.BUSINESS_SYSTEM_DEFAULTPATH_PATH_ATTRIBUTE, requestPath);
			}
			return (MessageKeys.MESSAGE_PROTOCOL_HTTPS.equalsIgnoreCase(this.protocolType) ? "https://" : "http://") 
					+ this.systemHost + ":" + this.systemPort + requestPath;
		}
		
		if (MessageKeys.MESSAGE_PROTOCOL_SOCKET.equalsIgnoreCase(this.protocolType)) {
			return this.systemHost + ":" + this.systemPort;
		}
		
		
		return "";
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setInfoCount(Integer infoCount) {
		this.infoCount = infoCount;
	}
	
	public Integer getInfoCount() {
		return interfaceInfos.size();
	}
	
	@JSON(serialize=false)
	public Set<InterfaceInfo> getInterfaceInfos() {
		return interfaceInfos;
	}

	public void setInterfaceInfos(Set<InterfaceInfo> interfaceInfos) {
		this.interfaceInfos = interfaceInfos;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemHost() {
		return systemHost;
	}

	public void setSystemHost(String systemHost) {
		this.systemHost = systemHost;
	}

	public String getSystemPort() {
		return systemPort;
	}

	public void setSystemPort(String systemPort) {
		this.systemPort = systemPort;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public String getSoftwareName() {
		return softwareName;
	}

	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}

	public String getLastModifyUser() {
		return lastModifyUser;
	}

	public void setLastModifyUser(String lastModifyUser) {
		this.lastModifyUser = lastModifyUser;
	}

	@JSON(format="yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public void setDefaultPath(String defaultPath) {
		this.defaultPath = defaultPath;
	}
	
	public String getDefaultPath() {
		return defaultPath;
	}

	@Override
	public String toString() {
		return "BusinessSystem [systemId=" + systemId + ", systemName="
				+ systemName + ", systemHost=" + systemHost + ", systemPort="
				+ systemPort + ", protocolType=" + protocolType
				+ ", softwareName=" + softwareName + ", lastModifyUser="
				+ lastModifyUser + ", createTime=" + createTime + ", mark="
				+ mark + ", infoCount=" + infoCount + ", status=" + status
				+ ", defaultPath=" + defaultPath + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((systemId == null) ? 0 : systemId.hashCode());
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
		BusinessSystem other = (BusinessSystem) obj;
		if (systemId == null) {
			if (other.systemId != null)
				return false;
		} else if (!systemId.equals(other.systemId))
			return false;
		return true;
	}
	
	
	
	
}
