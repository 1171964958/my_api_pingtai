package yi.master.business.system.bean;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * 全局设置项
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

public class GlobalSetting implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * id
	 */
	private Integer settingId;
	
	/**
	 * 设置名称
	 */
	private String settingName;
	
	/**
	 * 设置默认值
	 */
	private String defaultValue;
	
	/**
	 * 用户设定值
	 */
	private String settingValue;
	
	/**
	 * 备注
	 */
	private String mark;
	
	public GlobalSetting(String settingName, String defaultValue,
			String settingValue, String mark) {
		super();
		this.settingName = settingName;
		this.defaultValue = defaultValue;
		this.settingValue = settingValue;
		this.mark = mark;
	}

	public GlobalSetting() {
		super();
	}

	public Integer getSettingId() {
		return settingId;
	}

	public void setSettingId(Integer settingId) {
		this.settingId = settingId;
	}

	public String getSettingName() {
		return settingName;
	}

	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}

	public String getDefaultValue() {
		if (StringUtils.isBlank(defaultValue)) {
			return "";
		}
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getSettingValue() {
		if (StringUtils.isBlank(settingValue)) {
			return "";
		}
		return settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return "GlobalSetting [settingId=" + settingId + ", settingName="
				+ settingName + ", defaultValue=" + defaultValue
				+ ", settingValue=" + settingValue + ", mark=" + mark + "]";
	}
		
}
