package yi.master.business.message.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.constant.MessageKeys;


/**
 * 接口自动化
 * 接口参数信息表
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public class Parameter implements Serializable {

	private static final long serialVersionUID = 1L;   
	
	/**
	 * id
	 */
	private Integer parameterId;
	
	/**
	 * 参数标识
	 */
	private String parameterIdentify;
	
	/**
	 * 参数名
	 */
	private String parameterName;
	
	/**
	 * 默认值
	 */
	private String defaultValue;
	
	/**
	 * 类型 <br>
	 * Map List Array Number String
	 */
	private String type;
	
	/**
	 * 路径
	 */
	private String path;
	
	/**
	 * 节点属性，json格式保存
	 */
	private String attributes;
	
	/**
	 * 所属接口
	 */
	private InterfaceInfo interfaceInfo;
	
	private String mark;
	
	private Set<ComplexParameter> complexParameters = new HashSet<ComplexParameter>();

     
	private Integer parentId;
    // Constructors

    /** default constructor */
    public Parameter() {
    }

    public Parameter(Integer parameterId) {
    	this.parameterId = parameterId;
    };
    
    public Parameter(Integer parameterId, String type) {
    	this.parameterId = parameterId;
    	this.type = type;
    };
    
	/** minimal constructor */
    public Parameter(String parameterIdentify) {
        this.parameterIdentify = parameterIdentify;
    }
    
    /** full constructor */
    public Parameter(String parameterIdentify, String parameterName, String defaultValue, String path,String type) {
        this.parameterIdentify = parameterIdentify;
        this.parameterName = parameterName;
        this.defaultValue = defaultValue; 
        this.path = path;
        this.type = type;
    }
    
    
    /**
     * 根据自身path获取父节点id
     * @param path
     * @return
     */
    public static Integer getParentId (String path, Set<Parameter> parameters) {
    	if (MessageKeys.MESSAGE_PARAMETER_DEFAULT_ROOT_PATH.equals(path)) {
    		return 0; //根节点
    	}
    	
    	String parentIdentify = path.substring(path.lastIndexOf(".") + 1);
    	String parentPath = path.substring(0, path.lastIndexOf("."));
    	Parameter p = findParam(parentIdentify, parentPath, parameters);
    	if (p != null) {
    		return p.getParameterId();
    	}
    	return null; //找不到父节点
    }
    
    private static Parameter findParam (String identify, String path, Set<Parameter> parameters) {
    	for (Parameter p:parameters) {
    		if (identify.equalsIgnoreCase(p.getParameterIdentify()) && path.equalsIgnoreCase(p.getPath())) {
    			return p;
    		}
    	}
    	
    	return null;
    }
    
    // Property accessors
    
    public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
    
    public String getAttributes() {
		return attributes;
	}
    
    public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
    
    public Integer getParentId() {
		return parentId;
	}

    public Integer getParameterId() {
        return this.parameterId;
    }
    @JSON(serialize=false)
    public Set<ComplexParameter> getComplexParameters() {
		return complexParameters;
	}
    
    
	public void setComplexParameters(Set<ComplexParameter> complexParameters) {
		this.complexParameters = complexParameters;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }

    public String getParameterIdentify() {
        return this.parameterIdentify;
    }
    
    public void setParameterIdentify(String parameterIdentify) {
        this.parameterIdentify = parameterIdentify;
    }

    public String getParameterName() {
        return this.parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

    public void setInterfaceInfo(InterfaceInfo interfaceInfo) {
		this.interfaceInfo = interfaceInfo;
	}
   
    @JSON(serialize=false)
    public InterfaceInfo getInterfaceInfo() {
		return interfaceInfo;
	}

    public void setMark(String mark) {
		this.mark = mark;
	}
    
    public String getMark() {
		return mark;
	}
    
	@Override
	public String toString() {
		return "Parameter [parameterId=" + parameterId + ", parameterIdentify="
				+ parameterIdentify + ", parameterName=" + parameterName
				+ ", defaultValue=" + defaultValue + ", type=" + type
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((parameterIdentify == null) ? 0 : parameterIdentify
						.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		Parameter other = (Parameter) obj;
		if (parameterIdentify == null) {
			if (other.parameterIdentify != null)
				return false;
		} else if (!parameterIdentify.equals(other.parameterIdentify))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}