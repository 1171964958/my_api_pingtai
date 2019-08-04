package yi.master.business.message.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.advanced.bean.InterfaceProbe;
import yi.master.business.advanced.service.InterfaceProbeService;
import yi.master.business.base.action.BaseAction;
import yi.master.business.message.bean.TestResult;
import yi.master.business.message.service.TestResultService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.statement.AnalyzeUtil;

/**
 * 接口自动化<br>
 * 测试结果详情Action
 * @author Administrator
 *
 */

@Controller
@Scope("prototype")
public class TestResultAction extends BaseAction<TestResult> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TestResultService testResultService;
	@Autowired
	private InterfaceProbeService interfacePorbeService;
	
	private Integer reportId;
	
	private Integer probeId;
	
	@Autowired
	public void setTestResultService(TestResultService testResultService) {
		super.setBaseService(testResultService);
		this.testResultService = testResultService;
	}
		
	@Override
	public String[] prepareList() {
		
		List<String> conditions = new ArrayList<String>();
		if (reportId != null && model.getRunStatus() != null) {
			conditions.add("protocolType!='FIXED'");
			conditions.add("testReport.reportId=" + reportId);
			if (!"all".equalsIgnoreCase(model.getRunStatus())) {
				conditions.add("runStatus='" + model.getRunStatus() + "'");
			}			
		}
		
		if (probeId != null) {
			InterfaceProbe task = interfacePorbeService.get(probeId);	
			conditions.add("interfaceProbe.probeId=" + probeId);
			conditions.add("opTime>='" + task.getFirstCallTime() + "'");
		}
		this.filterCondition = conditions.toArray(new String[0]);
		return this.filterCondition;
	}

	
	public String getCount() {
		jsonMap.put("data", AnalyzeUtil.analyzeProbeResultSynopsisView(interfacePorbeService.listProbeBeforeResultInfo(7)));
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		return SUCCESS;
	}

	/*****************************************************************************/
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	
	public void setProbeId(Integer probeId) {
		this.probeId = probeId;
	}

}
