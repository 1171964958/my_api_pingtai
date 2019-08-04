package yi.master.coretest.message.parse;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import yi.master.business.message.bean.ComplexParameter;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.service.ParameterService;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;


/**
 * 自定义报文
 * <br>无限定报文，针对于目前无法识别的格式报文，报文不做任何验证
 * @author xuwangcheng
 * @version 1.0.0.0, 20171030
 *
 */
public class OPTMessageParse extends FixedMessageParse {
	
	protected OPTMessageParse() {
		
	}

	@Override
	public String checkParameterValidity(List<Parameter> params, String message) {
		
		return "true";
	}
	
	@Override
	public ComplexParameter parseMessageToObject(String message,
			List<Parameter> params) {
		
		ParameterService ps = (ParameterService) FrameworkUtil.getSpringBean("parameterService");
		int pid = ps.save(new Parameter(message, "name", "defaultValue", "path", "String"));
		return new ComplexParameter(new Parameter(pid), null, null);
	}

	@Override
	public String depacketizeMessageToString(ComplexParameter complexParameter,
			String paramsData) {
		
		if (StringUtils.isNotEmpty(paramsData)) {
			Map<String, Object> params = PracticalUtils.jsonToMap(paramsData);
			for (Object o:params.values()) {
				return messageFormatBeautify(o.toString());
			}
		}
		
		return messageFormatBeautify(complexParameter.getSelfParameter().getParameterIdentify());
	}
	
}
