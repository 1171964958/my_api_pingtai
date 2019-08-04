package yi.master.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperic.sigar.Sigar;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import yi.master.business.advanced.bean.config.probe.ProbeConfig;
import yi.master.business.message.bean.ComplexSceneConfig;
import yi.master.business.message.bean.Parameter;
import yi.master.business.message.bean.TestReport;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.bean.TestSet;
import yi.master.business.message.service.TestResultService;
import yi.master.business.message.service.TestSetService;
import yi.master.business.testconfig.bean.BusinessSystem;
import yi.master.business.testconfig.bean.GlobalVariable;
import yi.master.business.testconfig.service.BusinessSystemService;
import yi.master.business.testconfig.service.GlobalVariableService;
import yi.master.constant.SystemConsts;
import yi.master.coretest.message.parse.MessageParse;
import yi.master.coretest.message.protocol.HTTPTestClient;
import yi.master.coretest.message.protocol.TestClient;
import yi.master.util.cache.CacheUtil;
import yi.master.util.jsonlib.JsonDateValueProcessor;
import yi.master.util.message.JsonUtil;

/**
 * 常用工具类
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.14
 */
public class PracticalUtils {
	
	private static final Logger LOGGER = Logger.getLogger(PracticalUtils.class);

	public static final String DEFAULT_DATE_PATTERN = "HH:mm:ss";
	public static final String FULL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static final JsonConfig jsonConfig = new JsonConfig();
	
	public static final Sigar sigar = initSigar();
	
	static {
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor("yyyy-MM-dd HH:mm:ss"));
	}

	private PracticalUtils() {
		throw new Error("Please don't instantiate me！");
	}

	/**
	 * 初始化Sigar工具
	 * @return
	 */
	public static Sigar initSigar() {
		try {
            //此处只为得到依赖库文件的目录，可根据实际项目自定义
            String file = Paths.get(FrameworkUtil.getProjectPath(),  "tools", "sigar",".sigar_shellrc").toString();
            File classPath = new File(file).getParentFile();

            String path = System.getProperty("java.library.path");
            String sigarLibPath = classPath.getCanonicalPath();
            //为防止java.library.path重复加，此处判断了一下
            if (!path.contains(sigarLibPath)) {
                if (isOSWin()) {
                    path += ";" + sigarLibPath;
                } else {
                    path += ":" + sigarLibPath;
                }
                System.setProperty("java.library.path", path);
            }
            return new Sigar();
        } catch (Exception e) {
        	LOGGER.error("Sigar初始化失败!", e);
            return null;
        }
	}
	
	/**
	 * OS 版本判断
	 * @return
	 */
	public static boolean isOSWin(){
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0) {
            return true;
        } else return false;
    }
	
	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(Object str) {
		if (str == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9].*");
		Matcher isNum = pattern.matcher(str.toString());
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * arrayList的toString工具
	 * 
	 * @param list
	 * @return
	 */
	public static String arrayListToString(ArrayList<String> list) {
		StringBuffer returnStrs = new StringBuffer();
		for (String s : list) {
			returnStrs.append("[" + s + "]");
		}
		return returnStrs.toString();
	}


	/**
	 * 删除指定字符串中的指定字符
	 * 
	 * @param s
	 *            要操作的字符串
	 * @param string
	 *            要删除的字符
	 * @param i
	 *            删除第几个
	 * @return
	 */
	public static String removeChar(String s, String string, int i) {
		if (i == 1) {

			int j = s.indexOf(string);
			s = s.substring(0, j) + s.substring(j + 1);
			i--;
			return s;

		} else {

			int j = s.indexOf(string);
			i--;
			return s.substring(0, j + 1)
					+ removeChar(s.substring(j + 1), string, i);
		}
	}

	/**
	 * 替换sql中需要替换的参数 参数格式 <参数名> Web自动化测试中使用的
	 * 
	 * @param sqlStr
	 * @param map
	 * @return
	 */
	public static String replaceSqlStr(String sqlStr, Map<String, Object> map) {

		String regex = "(<[a-zA-Z0-9]*>)";
		Pattern pattern = Pattern.compile(regex);
		List<String> regStrs = new ArrayList<String>();
		Matcher matcher = pattern.matcher(sqlStr);// 匹配类

		while (matcher.find()) {
			regStrs.add(matcher.group());
		}
		for (String s : regStrs) {
			if (map.get(s) != null) {
				sqlStr = sqlStr.replaceAll(s, (String) map.get(s));
			}
		}
		return sqlStr;
	}

	/**
	 * 替换sql语句中的参数 参数格式 <节点名称或者路径> 接口自动化中使用的
	 * 
	 * @param sqlStr
	 * @param requestMap
	 *            入参报文中所有节点名集合
	 * @param requestJson
	 *            请求入参报文
	 * @return
	 */
	public static String replaceSqlStr(String sqlStr,
			Map<String, String> requestMap, String requestJson) {
		String regex = "(<[a-zA-Z0-9_.]*>)";
		Pattern pattern = Pattern.compile(regex);
		List<String> regStrs = new ArrayList<String>();
		Matcher matcher = pattern.matcher(sqlStr);

		while (matcher.find()) {
			regStrs.add(matcher.group());
		}
		for (String s : regStrs) {
			String regS = s.substring(1, s.length() - 1);
			if (s.indexOf(".") != -1) {
				regS = JsonUtil.getObjectByJson(requestJson, regS,
						JsonUtil.TypeEnum.string);
				if (regS != null) {
					sqlStr = sqlStr.replaceAll(s, regS);
				}
			} else {
				if (requestMap.get(regS) != null) {
					sqlStr = sqlStr.replaceAll(s, requestMap.get(regS));
				}
			}
		}
		return sqlStr;
	}

	/**
	 * 时间格式化
	 * 
	 * @param fromat
	 * @param date
	 * @return
	 */
	public static String formatDate(String format, Date date) {
		if (date == null) {
			return "";
		}
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	/**
	 * 字符串转日期
	 * @param format
	 * @param date
	 * @return
	 */
	public static Date convertDate (String format, String date) {
		if (StringUtils.isBlank(date) || StringUtils.isBlank(format)) {
			return null;
		}		
		DateFormat dateFormat = new SimpleDateFormat(format);
		try {
			return dateFormat.parse(date);
		} catch (Exception e) {
			
			return null;
		}
		
	}

	/**
	 * 是否为正常字符串，不为null并且不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNormalString(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 生成用户登录标识
	 * 
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String createUserLoginIdentification(String password)
			throws NoSuchAlgorithmException {
		return MD5Util.code(password + System.currentTimeMillis());
	}

	/**
	 * 生成静态报告文件<br>
	 * 2018-01-01报告模板有改动<br>
	 * 参见 {@link #createReportNew(Map)}
	 * @param report
	 * @param title
	 *            html标题
	 * @param successRate
	 *            成功率
	 * @return 返回生成的html部分
	 */
	@Deprecated
	public static StringBuilder createReport(TestReport report, String title,
			String successRate) {
		StringBuilder str = new StringBuilder();
		Set<TestResult> results = report.getTrs();

		str.append("<div class=\"panel-heading\">")
				.append(title)
				.append("</div>")
				.append("<div class=\"panel-body text-left\"><div class=\"row\"><div class=\"col-sm-6\"><span>测试场景数:</span>&nbsp;&nbsp;<span id=\"sceneNum\">")
				.append(report.getSceneNum())
				.append("</span></div><div class=\"col-sm-6\"><span>执&nbsp;行&nbsp;日&nbsp;期:</span>&nbsp;&nbsp;<span id=\"testDate\">")
				.append(report.getCreateTime())
				.append("</span></div></div><div class=\"row\"><div class=\"col-sm-6\"><span>执行成功数:</span>&nbsp;&nbsp;<span id=\"successNum\">")
				.append(report.getSuccessNum())
				.append("</span></div><div class=\"col-sm-6\"><span>异常中断数:</span>&nbsp;&nbsp;<span id=\"stopNum\">")
				.append(report.getStopNum())
				.append("</span></div></div><div class=\"row\"><div class=\"col-sm-6\"><span>执行失败数:</span>&nbsp;&nbsp;<span id=\"failNum\">")
				.append(report.getFailNum())
				.append("</span></div><div class=\"col-sm-6\"><span>测试成功率:</span>&nbsp;&nbsp;<span id=\"successRate\">")
				.append(successRate)
				.append("</span>%</div></div><hr><table class=\"table table-hover\"><thead><tr><th>接口</th><th>协议</th><th>报文</th><th>场景</th><th>状态</th><th>耗时(ms)</th></tr></thead><tbody>");

		int count = 0;
		for (TestResult result : results) {
			str.append("<tr id=\"" + count + "\" class=\"result-view\">");
			String[] infos = result.getMessageInfo().split(",");
			str.append("<td>" + infos[0] + "</td>")
					.append("<td>" + result.getProtocolType() + "</td>")
					.append("<td>" + infos[1] + "</td>")
					.append("<td>" + infos[2] + "</td>")
					.append("<td class=\"status\" style=\"display:table-cell; vertical-align:middle;\"><span class=\"label label-");

			switch (result.getRunStatus()) {
			case "0":
				str.append("success\">Success");
				break;
			case "1":
				str.append("danger\">Fail");
				break;
			case "2":
				str.append("default\">Stop");
				break;
			default:
				break;
			}

			str.append("</span></td><td>" + result.getUseTime() + "</td></tr>");

			// 详情显示
			str.append(
					"<tr class=\"hidden\"><td colspan=\"6\"><div class=\"view-details\"><div class=\"row\"><div class=\"col-sm-3\"><strong>请求地址:</strong></div><div class=\"col-sm-9\">")
					.append(result.getRequestUrl())
					.append("</div></div><br><div class=\"row\"><div class=\"col-sm-3\"><strong>请求返回码:</strong></div><div class=\"col-sm-9\">")
					.append(result.getStatusCode())
					.append("</div></div><br/><div class=\"row\"><div class=\"col-sm-3\"><strong>入参报文:</strong></div><div class=\"col-sm-9\"><textarea class=\"form-control\" cols=\"30\" rows=\"8\">")
					.append(result.getRequestMessage())
					.append("</textarea></div></div><br/><div class=\"row\"><div class=\"col-sm-3\"><strong>出参报文:</strong></div><div class=\"col-sm-9\"><textarea name=\"\" class=\"form-control\" cols=\"30\" rows=\"8\">")
					.append(result.getResponseMessage() == null ? "" : result.getResponseMessage())
					.append("</textarea></div></div><br/><div class=\"row\"><div class=\"col-sm-3\"><strong>备注:</strong></div><div class=\"col-sm-9\"> <textarea name=\"\" class=\"form-control\" cols=\"30\" rows=\"4\">")
					.append(result.getMark() == null ? "" : result.getMark())
					.append("</textarea></div></div></div></td></tr>");

			count++;
		}

		str.append("</tbody></table></div><div class=\"panel-footer\">接口自动化测试平台@2018 Created by baikapala </div></div></div></body></html>");
		return str;
	}

	/**
	 * 生成离线的测试报告<br>
	 * @param reportObject 包含报告所需的各种信息，同接口getReportDetail返回内容
	 * @return
	 * @throws Exception 
	 */
	public static void createReportNew (String reportDetails, File reportFile) throws Exception {
		//存储文件夹
		String saveFolder = FrameworkUtil.getProjectPath() + File.separator + SystemConsts.REPORT_VIEW_HTML_FOLDER;
		//获取模板
		File templateFile = new File(saveFolder + File.separator + SystemConsts.REPORT_VIEW_HTML_FIXED_HTML_NEW);
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		FileInputStream fis = null;
		String encoding = "UTF-8";
		try {
			fos = new FileOutputStream(reportFile);
			bos = new BufferedOutputStream(fos);
			fis = new FileInputStream(templateFile);
			//读取模板内容
			Long fileLength = templateFile.length();
			byte[] filecontent = new byte[fileLength.intValue()];

			fis.read(filecontent);			
			String html = new String(filecontent, encoding);
			
			//替换重要信息
			html = html.replace("#JSON_STRING__JSON_STRING#", reportDetails);
			
			//写入文件
			bos.write(html.toString().getBytes(encoding));
			bos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 设置报告详情数据
	 * @param report
	 * @return
	 */
	public static String setReportDetails (TestReport report) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		report.countSceneNum();
		TestSetService setService = (TestSetService) FrameworkUtil.getSpringBean("testSetService");
		TestResultService resultService = (TestResultService) FrameworkUtil.getSpringBean("testResultService");
		
		TestSet set = setService.get(Integer.valueOf(report.getTestMode()));
		String title = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_MESSAGE_REPORT_TITLE);
		
		jsonMap.put("title", "全量测试  " + report.getCreateTime() + " - " + title);
		
		if (!"0".equals(report.getTestMode())) {
			if (set == null) {
				jsonMap.put("title", "接口测试  " + report.getCreateTime() + " - " + title);
			} else {
				jsonMap.put("title", set.getSetName() + " - " + title);
			}
		}
		
		Map<String, Object> desc = new HashMap<>();
		
		
		desc.put("systems", resultService.getReportSystemNames(report.getReportId()));
		desc.put("sceneNum", report.getSceneNum());
		desc.put("testDate", report.getCreateTime());
		desc.put("successNum", report.getSuccessNum());
		desc.put("failNum", report.getFailNum());
		desc.put("stopNum", report.getStopNum());
		desc.put("successRate",  String.format("%.2f", Double.valueOf(String.valueOf(((double)report.getSuccessNum() / report.getSceneNum() * 100)))));
		
		jsonMap.put("desc", desc);
		jsonMap.put("data", resultService.findAll("testReport.reportId=" + report.getReportId()));
		
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[]{"testReport", "messageScene", "complexResult"});
		jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor("yyyy-MM-dd HH:mm:ss"));
		
		return JSONObject.fromObject(jsonMap, jsonConfig).toString();
	}
	
	/**
	 * 获取当天零点时间
	 * 
	 * @return
	 */
	public static Date getTodayZeroTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date zero = calendar.getTime();
		return zero;
	}

	/**
	 * 获取昨天零点时间
	 * 
	 * @return 昨天零点时间戳
	 */
	public static Date getYesterdayZeroTime() {
		Date date = new Date();
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		if ((gc.get(GregorianCalendar.HOUR_OF_DAY) == 0)
				&& (gc.get(GregorianCalendar.MINUTE) == 0)
				&& (gc.get(GregorianCalendar.SECOND) == 0)) {
			return new Date(date.getTime() - (24 * 60 * 60 * 1000));
		} else {
			Date date2 = new Date(date.getTime()
					- gc.get(GregorianCalendar.HOUR_OF_DAY) * 60 * 60 * 1000
					- gc.get(GregorianCalendar.MINUTE) * 60 * 1000
					- gc.get(GregorianCalendar.SECOND) * 1000 - 24 * 60 * 60
					* 1000);
			return date2;
		}
	}

	/**
	 * 获取当月第一天零点时刻
	 * 
	 * @return
	 */
	public static Date getThisMonthFirstDayZeroTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		// 将小时至0
		c.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		c.set(Calendar.MINUTE, 0);
		// 将秒至0
		c.set(Calendar.SECOND, 0);
		// 将毫秒至0
		c.set(Calendar.MILLISECOND, 0);
		// 获取本月第一天的时间戳
		return c.getTime();
	}

	/**
	 * 获取本周第一天零点时刻
	 * 
	 * @return
	 */
	public static Date getThisWeekFirstDayZeroTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}
	
	/**
	 * 获取距今n天前的日期
	 * @return
	 */
	public static Date getSevenDayTimeBefore(int n) {
		long current = System.currentTimeMillis();
		return new Date(current - (1000 * 60 * 60 * 24 * n));
	}

	/**
	 * 生成随机字符串
	 * 
	 * @param mode
	 *            模式 0-只包含大写字母 1-只包含小写字母 2-包含大小写字母 3-包含字母数字
	 * @param length
	 *            字符串长度
	 * @return
	 */
	public static String createRandomString(String mode, int length) {
		// 小写字母0-25 大写字母26-51 数字52-61
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		int count = 0;
		StringBuilder randomStr = new StringBuilder();
		while (count < length) {
			count++;
			switch (mode) {
			case "0":
				randomStr.append(str.charAt(getRandomNum(51, 26)));
				break;
			case "1":
				randomStr.append(str.charAt(getRandomNum(25, 0)));
				break;
			case "2":
				randomStr.append(str.charAt(getRandomNum(51, 0)));
				break;
			case "3":
				randomStr.append(str.charAt(getRandomNum(61, 0)));
				break;
			default:
				break;
			}
		}
		return randomStr.toString();

	}

	/**
	 * 获取随机数
	 * 
	 * @param max
	 *            最大值
	 * @param min
	 *            最小值
	 * @return
	 */
	public static int getRandomNum(int max, int min) {
		Random ran = new Random();
		return ran.nextInt(max) % (max - min + 1) + min;
	}

	/**
	 * 替换报文入参中的全局变量
	 * 
	 * @param msg
	 * @param globalVariableService
	 * @return
	 */
	public static String replaceGlobalVariable(String msg,
			GlobalVariableService globalVariableService) {
		if (StringUtils.isBlank(msg)) return msg; 
		
		if (globalVariableService == null) {
			globalVariableService = (GlobalVariableService) FrameworkUtil.getSpringBean("globalVariableService");
		}
		
		String regex = "\\$\\{__(.*?)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(msg);

		GlobalVariable variable = null;
		String useVariable = null;
		while (matcher.find()) {
			useVariable = "\\$\\{__" + matcher.group(1) + "\\}";
			if (!msg.contains("${__" + matcher.group(1) + "}")) {
				continue;
			}
			variable = globalVariableService.findByKey(matcher.group(1));
			if (variable == null) {
				continue;
			}
			msg = msg.replaceAll(useVariable, String.valueOf(variable.createSettingValue()));
		}
		return msg;
	}

	/**
	 * 发送http请求-get方式
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	public static String doGetHttpRequest(String requestUrl) {
		HTTPTestClient client = (HTTPTestClient) TestClient.getTestClientInstance("HTTP");
		HttpRequestBase request = null;
		try {
			Object[] responseContext = client.doGet(requestUrl, null, null, null, (DefaultHttpClient) client.getTestClient());
			HttpResponse response = (HttpResponse) responseContext[0];
			request = (HttpRequestBase) responseContext[2];

			StringBuilder returnMsg = new StringBuilder();
			InputStream is = response.getEntity().getContent();
			Scanner scan = new Scanner(is, "UTF-8");
			while (scan.hasNext()) {
				returnMsg.append(scan.nextLine());
			}

			return returnMsg.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			if (request != null) {
				request.releaseConnection();
			}
		}
	}

	/**
	 * 组合场景中的json串配置转换为对应的Map
	 * @param configJson
	 * @return
	 */
	@SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	public static Map<String, ComplexSceneConfig> getComplexSceneConfigs (String configJson) {
		JSONObject obj = new JSONObject().fromObject(configJson);
		Map<String, ComplexSceneConfig>	configs = new HashMap<String, ComplexSceneConfig>();
		
		Map<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("useVariables", Map.class);
		classMap.put("saveVariables", Map.class);
		
		Map<String, Object> objs = (Map<String, Object>) obj.toBean(obj, Map.class);
		for (String key:objs.keySet()) {
			JSONObject o = obj.getJSONObject(key);
			configs.put(key, (ComplexSceneConfig) o.getJSONObject(key).toBean(o, ComplexSceneConfig.class, classMap));
		}
		
		return configs;
	}
	
	 /**
     * 获取参数的树型json树<br>由于接口的参数库中参数不是按照父子关系存储，则需要根据节点path来梳理父子关系
     * 
     * @return Object[] 0 - 适用于Ztree的简单json数据格式  1 - 出错的信息
     */
	public static Object[] getParameterZtreeMap (Set<Parameter> parameters) {
		if (parameters.size() < 1) {//没有参数
    		return null;
    	}    	
    	Integer rootPid = 0;
    	StringBuilder errorInfo = new StringBuilder();
    	for (Parameter p:parameters) {
    		Integer parentId = Parameter.getParentId(p.getPath(), parameters);
    		
    		if (parentId == null) {
    			errorInfo.append("节点参数&nbsp;" + p.getParameterIdentify() + "&nbsp;[" + p.getPath() + "]不存在父节点或者父节点已被删除,请检查该节点的路径是否正确!<br>" );
    		} else if (parentId == 0) {
    			rootPid = p.getParameterId();
    		}
    			   			
    		p.setParentId(parentId);
    	}
    	
    	return new Object[]{parameters, rootPid, errorInfo};
	}

    /**
     * 根据id合集查找所属测试环境集合
     * @param ids
     * @return
     */
	public static Set<BusinessSystem> getSystems (String ids) {
		Set<BusinessSystem> systems = new HashSet<BusinessSystem>();
		if (StringUtils.isNotBlank(ids)) {
			BusinessSystemService service = (BusinessSystemService) FrameworkUtil.getSpringBean("businessSystemService");
			for (String id:ids.split(",")) {
				BusinessSystem system = service.get(Integer.valueOf(id));
				if (system != null) {
					systems.add(system);
				}
			}
		}
		return systems;
	}
	/**
	 * 从request中获取body内容
	 * @param request
	 * @return
	 */
	public String readRequestBody(HttpServletRequest request) {		
		BufferedReader br = null;
		try {
			br = request.getReader();
			String str, wholeStr = "";
			while ((str = br.readLine()) != null) {
				wholeStr += str;
			}
			return wholeStr;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 生成UUID 
	 * @param separator 分隔符
	 * @return
	 */
	public static String getUUID(String separator) {
		String uuid = UUID.randomUUID().toString();
		if (uuid != null) {
			uuid = uuid.replaceAll("-", separator);
		}
		
		return uuid;
	}
	
	/**
	 * 执行cmd命令
	 * @param command
	 * @return
	 */
	public static String execCmdCommadn(final String command) {
		BufferedReader br = null;
		StringBuilder returnStr = new StringBuilder();
		try {
			ProcessBuilder pb = new ProcessBuilder();	
			pb.redirectErrorStream(true);
			
			
			List<String> list = new ArrayList<>();
			list.add("cmd.exe");
			list.add("/c");
			list.addAll(Arrays.asList(command.split("\\s+")));
			
			Process p = pb.command(list).start();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				returnStr.append(line + "\n");
			}	
		} catch (Exception e) {
			LOGGER.error("执行本地命令失败:" + command, e);
			return "执行命令：【" + command + "】出错！\n" + e.toString();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		return returnStr.toString();
	}
	
	/**
	 * json转map
	 * @param json
	 * @return
	 */
	public static Map<String, Object> jsonToMap(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		Map<String, Object> maps = null;
		try {
			maps = mapper.readValue(json, Map.class);
		} catch (Exception e) {
			LOGGER.error("json串解析失败:" + json, e);
		}
		
		return maps;
	}
	
	/**
	 * 返回完整的异常信息
	 * @param ex
	 * @return
	 */
	public static String getExceptionAllinformation(Exception ex) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        ex.printStackTrace(pout);
        String ret = new String(out.toByteArray());
        pout.close();
        try {
             out.close();
        } catch (Exception e) {
        }
        return ret;
	}
	
	/**
	 * 获取请求客户端的IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) { 
	    String ip = request.getHeader("x-forwarded-for"); 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("Proxy-Client-IP"); 
	    } 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getHeader("WL-Proxy-Client-IP"); 
	    } 
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	        ip = request.getRemoteAddr(); 
	    } 
	    return ip; 
	} 
	
	
	/** 
	* 按指定大小，分隔集合，将集合按规定个数分为n个部分 
	*  
	* @param list 
	* @param len 
	* @return 
	*/  
	public static List<List<Map<String, String>>> splitList(List<Map<String, String>> list, int len) {
		if (list == null || list.size() == 0 || len < 1) {
			return null;
		}

		List<List<Map<String, String>>> result = new ArrayList<List<Map<String, String>>>();

		int size = list.size();
		int count = (size + len - 1) / len;

		for (int i = 0; i < count; i++) {
			List<Map<String, String>> subList = list.subList(i * len,
					((i + 1) * len > size ? size : len * (i + 1)));
			result.add(subList);
		}
		return result;
	} 
	
	/**
	 * 截取list指定部分<br>
	 * 因为在使用List.subList有点问题从而采用这种最基本的办法
	 * @param list
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static List subList(List list, int startIndex, int endIndex) {
		List returnList = new ArrayList();
		if (list == null) {
			return list;
		}
		if (startIndex == 0 && endIndex == (list.size() + 1)) {
			return list;
		}
		if (startIndex >= endIndex) {
			return list;
		}
		
		for (int i = 0;i < list.size();i++) {
			if (startIndex <= i && i < endIndex) {
				returnList.add(list.get(i));
			}
		}
		
		return returnList;
	}
	
	/**
	 * 使用关联配置获取指定内容
	 * @param map 关联规则
	 * @param msg 
	 * @return
	 */
	public static String getValueByRelationKeyWord(Map<String, Object> maps, String msg) {
		String getValue = "";		
		try {
			String leftBoundary = (String) maps.get("LB");
			String rightBoundary = (String) maps.get("RB");
			Integer order = Integer.valueOf((String) maps.get("ORDER"));
			String offset = (String) maps.get("OFFSET");
			String length = (String) maps.get("LENGHT");
					
			String singleRowResponseMessage = MessageParse.judgeMessageType(msg).parseMessageToSingleRow(msg);
			if (StringUtils.isBlank(leftBoundary) && StringUtils.isBlank(rightBoundary)) {
				getValue = singleRowResponseMessage;
			} else {
				String regex = leftBoundary + "(.*?)" + rightBoundary;
				Pattern pattern = Pattern.compile(regex);
				List<String> regStrs = new ArrayList<String>();
				Matcher matcher = pattern.matcher(singleRowResponseMessage);
				
				while (matcher.find()) {
					regStrs.add(matcher.group(1));			
				}
				
				if (regStrs.size() < order) {
					return null;
				}
				getValue = regStrs.get(order - 1);			
			}
			//根据offset取值
			if (PracticalUtils.isNumeric(offset) && Integer.valueOf(offset) < getValue.length()) {
				getValue = getValue.substring(Integer.valueOf(offset));
			}
			
			//根据length取值
			if (PracticalUtils.isNumeric(length)) {
				getValue = getValue.substring(0, Integer.valueOf(length));
			}
		} catch (Exception e) {
			return null;
		}
		return getValue;
	}
	
	/**
	 * 根据质量等级获取html显示代码
	 * @param qualityLevel 质量等级 
	 * @return
	 */
	public static String getProbeResultQualityLevelHtml (Integer qualityLevel) {
		if (ProbeConfig.PROBE_EXCELLENT_LEVEL.equals(qualityLevel)) {
			return "<span class=\"label label-success radius\">ExcellentLevel(优秀的)</span>";
		}
		if (ProbeConfig.PROBE_NORMAL_LEVEL.equals(qualityLevel)) {
			return "<span class=\"label label-primary radius\">NormalLevel(正常的)</span>";
		}
		if (ProbeConfig.PROBE_PROBLEMATIC_LEVEL.equals(qualityLevel)) {
			return "<span class=\"label label-warning radius\">ProblematicLevel(有问题的)</span>";
		}
		if (ProbeConfig.PROBE_SERIOUS_LEVEL.equals(qualityLevel)) {
			return "<span class=\"label label-danger radius\">SeriousLevel(严重的)</span>";
		}
		return "";
	}
}
