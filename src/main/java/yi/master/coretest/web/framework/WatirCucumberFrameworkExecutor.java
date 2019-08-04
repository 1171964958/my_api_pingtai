package yi.master.coretest.web.framework;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import yi.master.business.localize.shanxi.bean.WebScriptModule;
import yi.master.business.localize.shanxi.bean.WebScriptTask;
import yi.master.business.localize.shanxi.service.WebScriptTaskService;
import yi.master.constant.SystemConsts;
import yi.master.constant.WebScriptConsts;
import yi.master.util.FrameworkUtil;
import yi.master.util.PracticalUtils;
import yi.master.util.cache.CacheUtil;

/**
 * 编程语言-ruby脚本<br>
 * web自动化框架-watir<br>
 * bdd框架-cucumber
 * @author xuwangcheng
 * @version 2018.04.23,1.0.0
 *
 */
public class WatirCucumberFrameworkExecutor extends BaseFrameworkExecutor {
	private static final Logger logger = Logger.getLogger(WatirCucumberFrameworkExecutor.class);
	
	protected WatirCucumberFrameworkExecutor () {}

	@Override
	public WebScriptTask startTest(final WebScriptModule module, String guid) {
		final WebScriptTask task = new WebScriptTask();
		task.setFinishFlag("N");
		task.setModule(module);
		task.setStartTime(new Timestamp(System.currentTimeMillis()));
		Thread t = new Thread(new Runnable() {			
			@Override
			public void run() {
				
				String workHome = CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_WEB_SCRIPT_WORKPLACE);
				String modulePath =  CacheUtil.getSettingValue(SystemConsts.GLOBAL_SETTING_WEB_SCRIPT_MODULE_PATH) + File.separator + module.getFolderName();
				String reportHtmlPath = WebScriptConsts.WEB_SCRIPT_REPORT_FOLDER_PATH + File.separator + module.getModuleId() + "_" + System.currentTimeMillis() + ".html";	
				
				String command = "cd " + workHome + " && " + "cucumber " + modulePath + " -f html -o " + FrameworkUtil.getProjectPath() + File.separator + reportHtmlPath; 
				
				String returnInfo = PracticalUtils.execCmdCommadn(command);
				
				task.setFinishFlag("Y");
				task.setFinishTime(new Timestamp(System.currentTimeMillis()));
				task.setMark(returnInfo);
				task.setReportPath(reportHtmlPath);
				
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {	
						logger.warn("Interrupted", e);;
						Thread.currentThread().interrupt();
					}
					if (task.getTaskId() != null) {						
						break;
					}
				}
				//解析详细结果
				//web自动化脚本需要同步生成一个txt文件，里面包含本次测试的详细结果
				File txtFile = new File(FrameworkUtil.getProjectPath() + File.separator + reportHtmlPath + ".txt");
				if (txtFile.exists()) {
					try {
						List<String> infos = FileUtils.readLines(txtFile, "utf-8");
						JSONObject json = new JSONObject();
						json.put("totalCount", infos.size());
						int failCount = 0;
						int successCount = 0;
						
						JSONArray jsonArr = new JSONArray();
						
						for (String info:infos) {
							String[] ss = info.split("\\s+");
							JSONObject obj = new JSONObject();
							obj.put("moduleCode", ss[1]);
							obj.put("moduleName", ss[2]);
							obj.put("status", ss[5].toUpperCase());
							jsonArr.add(obj);	
							
							if ("SUCCESS".equalsIgnoreCase(ss[5])) {
								successCount++;
							} else {
								failCount++;
							}
						}
						json.put("details", jsonArr);
						json.put("failCount", failCount);
						json.put("successCount", successCount);
						json.put("successPercent", String.format("%.2f", Double.valueOf(String.valueOf(((double)successCount / infos.size() * 100)))));
						
						task.setDetailJson(json.toString());
					} catch (Exception e) {
						logger.error("web自动化测试结果解析出错", e);
					}
					
				}
				BaseFrameworkExecutor.setRunningWebScriptId(null);
				WebScriptTaskService service = (WebScriptTaskService) FrameworkUtil.getSpringBean(WebScriptTaskService.class);
				service.edit(task);
			}
		});
		t.start();
		if (StringUtils.isNotBlank(guid)) {
			try {
				t.join();
			} catch (InterruptedException e) {
				logger.warn("InterruptedException", e);
			}
		}
		
		
		return task;
	}
}
