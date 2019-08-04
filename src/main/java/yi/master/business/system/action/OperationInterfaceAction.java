package yi.master.business.system.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.system.bean.OperationInterface;
import yi.master.business.system.service.OperationInterfaceService;
import yi.master.constant.ReturnCodeConsts;

/**
 * 系统操作接口Action
 * @author xuwangcheng
 * @version 1.0.0.0,2017.2.13
 */

@Controller
@Scope("prototype")
public class OperationInterfaceAction extends BaseAction<OperationInterface> {

	private static final long serialVersionUID = 1L;
		
	/**
	 * 操作接口类型
	 */
	private Integer opType;
	
	private OperationInterfaceService operationInterfaceService;
	
	@Autowired
	public void setOperationInterfaceService(OperationInterfaceService operationInterfaceService) {
		super.setBaseService(operationInterfaceService);
		this.operationInterfaceService = operationInterfaceService;
	}
	
	@Override
	public String edit() {
		model.setOi(new OperationInterface(model.getParentOpId2()));
		return super.edit();
	}

	/**
	 * 根据传入opType查找不同类型的操作接口信息
	 * @return
	 */
	public String listOp() {
		jsonMap.put("data", operationInterfaceService.findAll());
		jsonMap.put("returnCode", ReturnCodeConsts.SUCCESS_CODE);
		
		return SUCCESS;
	}
	
	/**************************************************************************/
	
	public void setOpType(Integer opType) {
		this.opType = opType;
	}
	
	
}
