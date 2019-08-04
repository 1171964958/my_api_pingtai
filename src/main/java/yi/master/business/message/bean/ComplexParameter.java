package yi.master.business.message.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.json.annotations.JSON;

import yi.master.constant.MessageKeys;


/**
 * 复杂参数组成
 * @author xuwangcheng
 * @version 1.0.0.0,20170716
 *
 */
public class ComplexParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	/**
	 * 当前节点参数
	 */
	private Parameter selfParameter;
	
	/**
	 * 拥有的子节点参数
	 */
	private Set<ComplexParameter> childComplexParameters = new HashSet<ComplexParameter>();
	
	/**
	 * 父节点参数
	 */
	private ComplexParameter parentComplexParameter;
	
	/**
	 * 所属报文
	 */
	private Message message;
	

	public ComplexParameter(Integer id, Parameter selfParameter,
			Set<ComplexParameter> childComplexParameters,
			ComplexParameter parentComplexParameter) {
		super();
		this.id = id;
		this.selfParameter = selfParameter;
		this.childComplexParameters = childComplexParameters;
		this.parentComplexParameter = parentComplexParameter;
	}
	
	public ComplexParameter(Parameter selfParameter,
			Set<ComplexParameter> childComplexParameters,
			ComplexParameter parentComplexParameter) {
		super();
		this.selfParameter = selfParameter;
		this.childComplexParameters = childComplexParameters;
		this.parentComplexParameter = parentComplexParameter;
	}




	public ComplexParameter() {
		super();
	}

	/**
	 * 获取能够被设置值的参数列表<br>
	 * 即参数类型为String或者Number的
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	@JSON(serialize=false)
	public List<Parameter> getEnableSettingDataParameter(List<Parameter> params) throws Exception {
		
		if (params == null) {
			params = new ArrayList<Parameter>();
		}
		
		if (this.selfParameter == null) {
			throw new Exception("接口参数有变动，导致报文生成不正确!");
		}
		
		String parameterType = this.selfParameter.getType();
		if (parameterType.equalsIgnoreCase(MessageKeys.MESSAGE_PARAMETER_TYPE_NUMBER)
				 || parameterType.equalsIgnoreCase(MessageKeys.MESSAGE_PARAMETER_TYPE_STRING)
				 || parameterType.equalsIgnoreCase(MessageKeys.MESSAGE_PARAMETER_TYPE_CDATA)) {
			params.add(this.selfParameter);
		} else {
			for (ComplexParameter cp:this.getChildComplexParameters()) {
				cp.getEnableSettingDataParameter(params);
			}
		}
		return params;
	}
	
	/**
	 * 获取Array类型的参数
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<Object> getArrayTypeDataParameter(List<Object> obj) throws Exception {
		if (obj == null) {
			obj = new ArrayList<Object>();
		}
		if (this.selfParameter == null) {
			throw new Exception("接口参数有变动，导致报文生成不正确!");
		}
		
		String parameterType = this.selfParameter.getType();
		
		if (MessageKeys.MESSAGE_PARAMETER_TYPE_ARRAY.equalsIgnoreCase(parameterType)) {
			//如果是Array类型的就获取Array下的所有可设置数据的参数
			List<Parameter> params = new ArrayList<Parameter>();
			getEnableSettingDataParameter(params);
			Map<String, Object> info = new HashMap<String, Object>();
			info.put("parameter", this.getSelfParameter());
			info.put("childEnableSettingDataParameters", params);
			obj.add(info);			
		} 
		
		for (ComplexParameter cp:this.getChildComplexParameters()) {
			cp.getArrayTypeDataParameter(obj);
		}
				
		return obj;
	}
	
	@JSON(serialize=false)
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	@JSON(serialize=false)
	public ComplexParameter getParentComplexParameter() {
		return parentComplexParameter;
	}

	public void setParentComplexParameter(ComplexParameter parentComplexParameter) {
		this.parentComplexParameter = parentComplexParameter;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Parameter getSelfParameter() {
		return selfParameter;
	}

	public void setSelfParameter(Parameter selfParameter) {
		this.selfParameter = selfParameter;
	}
	
	@JSON(serialize=false)
	public Set<ComplexParameter> getChildComplexParameters() {
		return childComplexParameters;
	}

	public void setChildComplexParameters(
			Set<ComplexParameter> childComplexParameters) {
		this.childComplexParameters = childComplexParameters;
	}

	public void addChildComplexParameter(ComplexParameter p) {
		if (p != null) {
			this.childComplexParameters.add(p);
		}
	}

	@Override
	public String toString() {
		return "ComplexParameter [id=" + id + ", selfParameter="
				+ selfParameter + "]";
	}

	
	
}
