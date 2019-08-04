package yi.master.coretest.message.process;

import java.net.URLEncoder;
import java.security.PrivateKey;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import yi.master.constant.MessageKeys;
import yi.master.util.PracticalUtils;
import com.open.common.util.MD5;
import com.open.common.util.RSAUtils;
import com.open.common.util.StringUtil;

/**
 * @author xuwangcheng
 * @version 2018.4.19
 *
 */
public class ShanXiOpenApiMessageProcess extends MessageProcess {
	private static final Logger LOGGER = Logger.getLogger(ShanXiOpenApiMessageProcess.class);
	
	protected ShanXiOpenApiMessageProcess() {
	}

	@Override
	public String processRequestMessage(String requestMessage, String processParameter) {
		try {
			JSONObject obj = JSONObject.fromObject(processParameter);
			PrivateKey skPrivateKey = RSAUtils.fileToPrivateKey(PracticalUtils.replaceGlobalVariable(obj.getString(MessageKeys.SHANXI_OPEN_API_PARAMETER_PEM_FILE_PATH), null));	
			String sign = StringUtil.sortOrginReqStr(requestMessage);
			String md5str = MD5.ToMD5(URLEncoder.encode(sign, "utf-8"));
			
			String signStr = RSAUtils.sign(md5str.getBytes(), skPrivateKey);
			
			return requestMessage + "&sign=" + URLEncoder.encode(signStr, "UTF-8");
		} catch (Exception e) {
			LOGGER.error("接口参数加密失败：" + requestMessage + "\n处理配置参数：" + processParameter, e);
		}
		return requestMessage;
	}

	@Override
	public String processResponseMessage(String responseMessage,
			String processParameter) {
		return responseMessage;
	}
	
}
