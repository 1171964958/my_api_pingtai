package yi.master.business.web.bean.report;

import java.util.Date;

/**
 * 
 * 单个测试步骤的报告数据
 * @author xuwangcheng
 * @version 20180917,1.0.0.0
 *
 */
public class StepReportDetails {
private Integer stepId;
	
	/**
	 * 步骤名称
	 */
	private String stepName;
	/**
	 * 步骤状态<br>
	 * false-失败，true-成功
	 */
	private Boolean status;
	/**
	 * 步骤备注,可记录验证情况、失败原因等信息
	 */
	private String mark;
	/**
	 * 步骤截图<br>每个步骤只能有一个截图,在一个步骤中多次截图只会保存最后一张
	 */
	private String screenshot;
	/**
	 * 步骤执行时间
	 */
	private Date testTime;
}
