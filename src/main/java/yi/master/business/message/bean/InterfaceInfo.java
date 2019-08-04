package yi.master.business.message.bean;
// default package

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import yi.master.annotation.CustomConditionSetting;
import yi.master.annotation.FieldNameMapper;
import yi.master.annotation.FieldRealSearch;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.user.bean.User;


/**
 * 接口自动化
 * 接口信息表
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public class InterfaceInfo implements Serializable {


    // Fields    

	private static final long serialVersionUID = 1L;
	
	/**
	 * 接口id
	 */
	private Integer interfaceId;
	
	/**
	 * 创建用户
	 */
	private User user;
	
	/**
	 * 接口名称，必须为英文
	 */
	private String interfaceName;
	
	/**
	 * 接口中文名
	 */
	private String interfaceCnName;
	
	/**
	 * 模拟请求地址
	 */
	@Deprecated
	private String requestUrlMock;
	
	/**
	 * 请求路径
	 */
	private String requestUrlReal;
	
	/**
	 * 接口类型
	 * CX 查询类
	 * SL 受理类
	 */
	@FieldRealSearch(names = {"查询类", "受理类"}, values = {"CX", "SL"})
	@CustomConditionSetting(operator="=")
	private String interfaceType;
	
	/**
	 * 创建时间
	 */
	private Timestamp createTime;
	
	/**
	 * 当前状态
	 * 0 正常
	 * 1 禁用
	 */
	@FieldRealSearch(names = {"正常", "禁用"}, values = {"0", "1"})
	@CustomConditionSetting(operator="=")
	private String status;
	
	/**
	 * 最后一次修改用户的realName
	 */
	private String lastModifyUser;
    
	
	/**
	 * 当前接口下的参数
	 */
	private Set<Parameter> parameters = new HashSet<Parameter>();
	
	/**
	 * 当前接口下的报文
	 */
	private Set<Message> messages = new HashSet<Message>();
    // Constructors

	/**
	 * 接口协议类型
	 */
	@CustomConditionSetting(operator="=")
	private String interfaceProtocol;
	
	private String mark;
	
	@FieldNameMapper(fieldPath="createTime")
	@CustomConditionSetting(conditionType=CustomConditionSetting.DATETIME_TYPE)
	private String createTimeText;
	
	@FieldNameMapper(fieldPath="user.realName")
	private String createUserName;
	
	@FieldNameMapper(fieldPath="size(parameters)",ifSearch=false)
	@CustomConditionSetting(conditionType="")
	private Integer parametersNum;
	
	@FieldNameMapper(fieldPath="size(messages)",ifSearch=false)
	@CustomConditionSetting(conditionType="")
	private Integer messagesNum;
	
	/**
	 * 所属测试环境
	 */
	private Set<BusinessSystem> systems = new HashSet<BusinessSystem>();
	
	
	/******特殊成员变量，某些情况下使用-excel导入报文信息*********/
	private String requestMsg; //入参报文
	private String createMessage;//是否创建默认报文
	private String createScene;//是否创建默认场景
	private String MessageType;//报文格式类型	
	/**************************/
	
    /** default constructor */
    public InterfaceInfo() {
    }

    
    public BusinessSystem checkSystems (String systemId) {
    	for (BusinessSystem system:this.systems) {
    		if (String.valueOf(system.getSystemId()).equals(systemId)) {
    			return system;    			
    		}
    	}
    	return null;
    }
    
    public Set<BusinessSystem> getSystems() {
		return systems;
	}
    
    public void setSystems(Set<BusinessSystem> systems) {
		this.systems = systems;
	}
    
    public InterfaceInfo(Integer interfaceId) {
    	this.interfaceId = interfaceId;
    }
    
	/** minimal constructor */
    public InterfaceInfo(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    /** full constructor */
    public InterfaceInfo(User user, String interfaceName, String interfaceCnName, String requestUrlMock, String requestUrlReal, String interfaceType, Timestamp createTime, String status, String lastModifyUser) {
        this.user = user;
        this.interfaceName = interfaceName;
        this.interfaceCnName = interfaceCnName;
        this.requestUrlMock = requestUrlMock;
        this.requestUrlReal = requestUrlReal;
        this.interfaceType = interfaceType;
        this.createTime = createTime;
        this.status = status;
        this.lastModifyUser = lastModifyUser;
    }

    // Property accessors
    
    public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
    
    public String getCreateUserName() {
    	if (user == null) {
    		return this.createUserName;
    	}
		return user.getRealName();
	}
    
    public void setCreateTimeText(String createTimeText) {
		this.createTimeText = createTimeText;
	}
    
    public String getCreateTimeText() {
		return createTimeText;
	}
    
    public Integer getMessagesNum() {
		return this.messages.size();
	}
    
    public Integer getParametersNum() {
		return this.parameters.size();
	}
    
    public Integer getInterfaceId() {
        return this.interfaceId;
    }
    
    public String getInterfaceProtocol() {
		return interfaceProtocol;
	}

	public void setInterfaceProtocol(String interfaceProtocol) {
		this.interfaceProtocol = interfaceProtocol;
	}

	@JSON(serialize=false)
    public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	@JSON(serialize=false)
    public Set<Parameter> getParameters() {
		return parameters;
	}
    
    
	public void setParameters(Set<Parameter> parameters) {
		this.parameters = parameters;
	}

	public void setInterfaceId(Integer interfaceId) {
        this.interfaceId = interfaceId;
    }

    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }
    
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceCnName() {
        return this.interfaceCnName;
    }
    
    public void setInterfaceCnName(String interfaceCnName) {
        this.interfaceCnName = interfaceCnName;
    }

    public String getRequestUrlMock() {
        return this.requestUrlMock;
    }
    
    public void setRequestUrlMock(String requestUrlMock) {
        this.requestUrlMock = requestUrlMock;
    }

    public String getRequestUrlReal() {
        return this.requestUrlReal;
    }
    
    public void setRequestUrlReal(String requestUrlReal) {
        this.requestUrlReal = requestUrlReal;
    }

    public String getInterfaceType() {
        return this.interfaceType;
    }
    
    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    @JSON(format="yyyy-MM-dd HH:mm:ss")
    public Timestamp getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastModifyUser() {
        return this.lastModifyUser;
    }
    
    public void setLastModifyUser(String lastModifyUser) {
        this.lastModifyUser = lastModifyUser;
    }

    @JSON(serialize=false)
	public String getRequestMsg() {
		return requestMsg;
	}

	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}
	
	
	@JSON(serialize=false)
	public String getCreateMessage() {
		return createMessage;
	}

	public void setCreateMessage(String createMessage) {
		this.createMessage = createMessage;
	}
	
	@JSON(serialize=false)
	public String getCreateScene() {
		return createScene;
	}

	public void setCreateScene(String createScene) {
		this.createScene = createScene;
	}

	@JSON(serialize=false)
	public String getMessageType() {
		return MessageType;
	}

	public void setMessageType(String messageType) {
		MessageType = messageType;
	}
	
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public String getMark() {
		if (StringUtils.isBlank(mark)) {
			return "";
		}
		return mark;
	}

	@Override
	public String toString() {
		return "InterfaceInfo [interfaceName=" + interfaceName
				+ ", interfaceCnName=" + interfaceCnName + ", requestUrlMock="
				+ requestUrlMock + ", requestUrlReal=" + requestUrlReal
				+ ", interfaceType=" + interfaceType + ", status=" + status
				+ ", interfaceProtocol=" + interfaceProtocol + ", requestMsg="
				+ requestMsg + ", createMessage=" + createMessage
				+ ", createScene=" + createScene + ", MessageType="
				+ MessageType + "]";
	}
	
	
	
}