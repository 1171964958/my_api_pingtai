package yi.master.business.advanced.bean.config.mock;


import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import yi.master.coretest.message.parse.MessageParse;
import yi.master.util.PracticalUtils;
import yi.master.util.xeger.Xeger;

public class MockGenerateRuleConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 路径
	 */
	private String path;
	/**
	 * 生成类型:<br>
	 * node - 入参节点<br>
	 * variable - 全局变量<br>
	 * constant - 常量<br>
	 * regular - 正则表达式，根据表达式随机生成符合的字符串
	 * 
	 */
	private String generateType;
	
	/**
	 * 根据generateType来填写，如果生成失败或者无效，统一当做常量处理
	 */
	private String generateValue;
	
	public MockGenerateRuleConfig(String name, String path,
			String generateType, String generateValue) {
		super();
		this.name = name;
		this.path = path;
		this.generateType = generateType;
		this.generateValue = generateValue;
	}
	public MockGenerateRuleConfig() {
		super();
	}
	
	public String generateValue(String requestMsg) {
		switch (this.generateType) {
		case "node":
			if (StringUtils.isBlank(requestMsg)) return this.generateValue;
			MessageParse parseUtil = MessageParse.getParseInstance(MessageParse.judgeType(requestMsg));
			return parseUtil.getObjectByPath(requestMsg, this.generateValue);
		case "variable":
			return PracticalUtils.replaceGlobalVariable(this.generateValue, null);
		case "regular":
			return new Xeger(this.generateValue).generate();
		default:
			break;
		}
		
		return this.generateValue;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getGenerateType() {
		return generateType;
	}
	public void setGenerateType(String generateType) {
		this.generateType = generateType;
	}
	public String getGenerateValue() {
		return generateValue;
	}
	public void setGenerateValue(String generateValue) {
		this.generateValue = generateValue;
	}
	@Override
	public String toString() {
		return "MockGenerateRuleConfig [name=" + name + ", path=" + path
				+ ", generateType=" + generateType + ", generateValue="
				+ generateValue + "]";
	}
}
