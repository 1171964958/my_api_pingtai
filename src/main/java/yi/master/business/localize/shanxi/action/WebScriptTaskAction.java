package yi.master.business.localize.shanxi.action;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.localize.shanxi.bean.WebScriptTask;
import yi.master.business.localize.shanxi.service.WebScriptTaskService;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class WebScriptTaskAction extends BaseAction<WebScriptTask> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WebScriptTaskService webScriptTaskService;

	@Autowired
	public void setWebScriptTaskService(
			WebScriptTaskService webScriptTaskService) {
		super.setBaseService(webScriptTaskService);
		this.webScriptTaskService = webScriptTaskService;
	}

	@Override
	public String del() {
		
		//查看是否有报告文件，否则一并删除
		model = webScriptTaskService.get(id);
		FileUtils.deleteQuietly(new File(FrameworkUtil.getProjectPath() + File.separator + model.getReportPath()));		
		return super.del();
	}
	
	
	
}
