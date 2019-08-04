package yi.master.business.web.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import yi.master.business.base.action.BaseAction;
import yi.master.business.user.bean.User;
import yi.master.business.web.bean.WebTestElement;
import yi.master.business.web.service.WebTestElementService;
import yi.master.constant.ReturnCodeConsts;
import yi.master.constant.WebTestKeys;
import yi.master.util.FrameworkUtil;

@Controller
@Scope("prototype")
public class WebTestElementAction extends BaseAction<WebTestElement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(WebTestElementAction.class);
	
	private WebTestElementService webTestElementService;
	
	@Autowired
	public void setWebTestElementService(WebTestElementService webTestElementService) {
		super.setBaseService(webTestElementService);
		this.webTestElementService = webTestElementService;
	}
	/**
	 * 获取ztree节点<br>
	 * 非tag、url类型的节点
	 */
	private String nodeFlag;
	/**
	 * 获取可以移动或者复制到节点的节点<br>
	 * page类型和frame类型
	 */
	private String copyOrMoveFlag;
	
	private WebTestElement targetElement;
	
	/**
	 * 选择指定的元素给测试步骤<br>
	 * 需要元素类型只能为tag和url
	 */
	private String chooseFlag;
	
	@Override
	public String[] prepareList() {
		
		List<String> conditions = new ArrayList<String>();
		if (StringUtils.isNotEmpty(nodeFlag)) {
			conditions.add("elementType not in ('" + WebTestKeys.WEB_ELEMENT_TYPE_TAG + "','" + WebTestKeys.WEB_ELEMENT_TYPE_URL + "')");
		}	
		if (StringUtils.isNotEmpty(copyOrMoveFlag)) {
			conditions.add("elementType in ('" + WebTestKeys.WEB_ELEMENT_TYPE_PAGE + "','" + WebTestKeys.WEB_ELEMENT_TYPE_FRAME + "')");
		}
		if (StringUtils.isNotEmpty(chooseFlag)) {
			conditions.add("elementType in ('" + WebTestKeys.WEB_ELEMENT_TYPE_TAG + "','" + WebTestKeys.WEB_ELEMENT_TYPE_URL + "')");
		}
		if (model.getElementId() != null) {
			conditions.add("parentElement.elementId=" + model.getElementId());
			conditions.add("elementType in ('" + WebTestKeys.WEB_ELEMENT_TYPE_URL + "','" + WebTestKeys.WEB_ELEMENT_TYPE_TAG + "','" + WebTestKeys.WEB_ELEMENT_TYPE_FRAME + "')");
		}
		
		this.filterCondition = conditions.toArray(new String[0]);
		return this.filterCondition;
	}
	
	@Override
	public String edit() {
			
		User user = (User) FrameworkUtil.getSessionMap().get("user");
		model.setParentElement(webTestElementService.get(model.getParentId()));
		if (model.getElementId() == null) {
			model.setCreateUser(user);
			model.setCreateTime(new Timestamp(System.currentTimeMillis()));
			model.setElementId(webTestElementService.save(model));			
		} else {
			if (model.getElementId().equals(1) || model.getElementId().equals(2)) {
				setReturnInfo(ReturnCodeConsts.ILLEGAL_HANDLE_CODE, "不能修改预置节点信息!");
				return SUCCESS;
			}
			
			model.logModify(user.getRealName(), webTestElementService.get(model.getElementId()));
			webTestElementService.edit(model);
		}
		setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "").setData("object", model);
		return SUCCESS;
	}
	
	
	@Override
	public String del() {
		if (id.equals(1) || id.equals(2)) {
			setReturnInfo(ReturnCodeConsts.ILLEGAL_HANDLE_CODE, "不能删除预置节点!");
			return SUCCESS;
		}
		return super.del();
	}
	
	/**
	 * 移动指定TAG、Frame、URL类型的节点到指定的PAGE/FRAME页面
	 * @return
	 */
	public String move() {
		if (!validateElementParams()) {
			return SUCCESS;
		}
		if (!targetElement.getElementId().equals(model.getParentId())) {
			model.setParentElement(targetElement);
			webTestElementService.edit(model);
		}
		
		return SUCCESS;
	}
	/**
	 * 复制指定TAG、Frame、URL类型的节点到指定的PAGE/FRAME页面
	 * @return
	 */
	public String copy() {
		if (!validateElementParams()) {
			return SUCCESS;
		}
		
		WebTestElement newEle = (WebTestElement) model.clone();
		newEle.setParentElement(targetElement);
		newEle.setElementId(null);
		newEle.setCreateTime(new Timestamp(System.currentTimeMillis()));
		newEle.setCreateUser((User) FrameworkUtil.getSessionMap().get("user"));
		webTestElementService.save(newEle);
		return SUCCESS;		
	}
	
	/**
	 * 移动或者复制是检查参数合法性
	 * @return
	 */
	private boolean validateElementParams () {
		WebTestElement parentElement = webTestElementService.get(model.getParentId());
		model = webTestElementService.get(model.getElementId());
		if (model != null && parentElement != null && parentElement.getElementType().matches(WebTestKeys.WEB_ELEMENT_TYPE_FRAME + "|" + WebTestKeys.WEB_ELEMENT_TYPE_PAGE)) {
			targetElement = parentElement;
			setReturnInfo(ReturnCodeConsts.SUCCESS_CODE, "");
			return true;
		}
		setReturnInfo(ReturnCodeConsts.ILLEGAL_HANDLE_CODE, "参数不正确!");
		return false;
	}
	
	public void setNodeFlag(String nodeFlag) {
		this.nodeFlag = nodeFlag;
	}
	
	public void setCopyOrMoveFlag(String copyOrMoveFlag) {
		this.copyOrMoveFlag = copyOrMoveFlag;
	}
	
	public void setChooseFlag(String chooseFlag) {
		this.chooseFlag = chooseFlag;
	}
}
