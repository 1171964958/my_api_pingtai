var templateParams = {
		btnTools:[]
};

var columnsSetting = [];

var eventList = {
		
};

var mySetting = {
		eventList:eventList,
		userDefaultRender:false,    
   	 	userDefaultTemplate:false,
   	 	customCallBack:function(params){
   	 		$('#treeDemo').scrollTop( $('#treeDemo')[0].scrollHeight);   	 		
   	 		refreshZtree();
   	 	},
		editPage:{

		}	
	};


$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams, mySetting);
	publish.init();
});

/*****************************************************************************************/
var zTreeObj;
var demoIframe;
var beforeNodeName;
var currentNode;
var setting = {
		view: {
			dblClickExpand: false,
			showLine: true,
			selectedMulti: false,
			addHoverDom: addHoverDom,
			removeHoverDom: removeHoverDom
		},
		data: {
			simpleData: {
				enable:true,
				idKey: "elementId",
				pIdKey: "parentId",
				rootPId: 1
			},
			key: {
				name:"elementName"
			}
		},
		edit:{
			enable: true,			
			showRemoveBtn: true,
			showRenameBtn: true,
			removeTitle:"删除节点",
			renameTitle: "编辑节点名称",
			drag:{
				isCopy: false,
				isMove: false
			}
		},
		callback: {
			beforeClick: function(treeId, treeNode) {
				if (filter(treeNode)) {
					$("#element-frame").attr("src","webElement.html?elementId=" + treeNode.elementId);
					currentNode = treeNode;
					return true;
				}
			},
			beforeEditName:beforeEditName,
			beforeRemove:beforeRemove,
			onRename:onRename,
			onRemove:onRemove
		}
};


/**
 * 鼠标移动到节点上在显示增加的按钮
 * @param treeId
 * @param treeNode
 * @returns
 */
function addHoverDom(treeId, treeNode) {
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length>0) return;
	
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId + "' title='增加节点' onfocus='this.blur();'></span>";
	sObj.after(addStr);	
	var btn = $("#addBtn_" + treeNode.tId);
	if (btn) btn.bind('click', function(event) {
			event.stopPropagation();
			addChildElement(this, treeId, treeNode);
		}
	); 	
}

/**
 * 增加子节点的操作
 * @returns
 */
function addChildElement(domObj, treeId, treeNode) {
	showSelectBox(
		[
		 {
			 "id":"website",
			 "name":"网站、系统"
		 },
		 {
			 "id":"module",
			 "name":"模块"
		 },
		 {
			 "id":"feature",
			 "name":"功能、场景"
		 },
		 {
			 "id":"page",
			 "name":"页面"
		 },
		 {
			 "id":"frame",
			 "name":"frame/iframe/frameset框架"
		 }
		 ],
		 "id", "name", function(id, obj, index){
			layer.confirm('确定在  ' + treeNode.elementName + ' 下创建一个' + id + '类型的节点吗?', function(index2) {
				layer.prompt({
					  formType: 0,
					  value: '新节点',
					  title: '请输入节点名称'
					}, function(name, index3, elem){
						$.post(top.WEB_ELEMENT_EDIT_URL, {"elementType":id, "parentId":treeNode.elementId, "elementName":name}, function(json){						
							if (json.returnCode == 0) {
								var newNode = json.object;
								newNode["iconOpen"] = "../../img/element/" + newNode.elementType + "_open.png";
								newNode["iconClose"] = "../../img/element/" + newNode.elementType + "_close.png";
								zTreeObj.addNodes(treeNode, newNode);								
								layer.msg('创建成功!', {icon:1, time:1800});
							} else {
								layer.alert(json.msg, {icon:5});
							}							
						});						
						layer.close(index3);
					});
				layer.close(index2);
			});
			layer.close(index);
		}, "请选择创建的节点类型:"
	);
}

/**
 * 鼠标移开时的显示
 * @returns
 */
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_" + treeNode.tId).unbind().remove();
}
/**
 * 修改名称之前的操作
 * @returns
 */
function beforeEditName(treeId, treeNode) {
	if(treeNode.elementId == 1 || treeNode.elementId == 2){//顶层节点和通用页面不能修改
		layer.msg('不能修改预设节点信息!', {time:1800});
		return false;
	}
	beforeNodeName = treeNode.elementName;
	return true;
}

/**
 * 修改名称之后
 * @returns
 */
function onRename(event, treeId, treeNode, isCancel) {
	if(!isCancel && (beforeNodeName != treeNode.elementName)){
		var sendData = {
			elementName:treeNode.elementName,
			elementType:treeNode.elementType,
			parentId:treeNode.parentId,
			byType:treeNode.byType,
			byValue:treeNode.byValue,
			seq:treeNode.seq,
			createTime:treeNode.createTime,
			mark:treeNode.mark,
			modifyLog:treeNode.modifyLog,
			"createUser.userId":treeNode["createUser"]["userId"],
			elementId:treeNode.elementId			
		};
		$.post(top.WEB_ELEMENT_EDIT_URL, sendData, function(json){
			if (json.returnCode == 0) {
				layer.msg('修改成功!', {icon:1, time:1800});
			} else {
				treeNode.elementName = beforeNodeName;
				zTreeObj.updateNode(treeNode);
				layer.alert(json.msg, {icon:5});
			}
		});		
	}
}

/**
 * 删除之前
 * @returns
 */
function beforeRemove(treeId, treeNode) {
	if(treeNode.elementId == 1 || treeNode.elementId == 2){//顶层节点和通用页面不能修改
		layer.msg('不能删除预设节点!', {time:1800});
		return false;
	}
	var x = confirm('确认删除该节点及下属所有节点吗?');
	var flag = true;
	if(x){
		$.ajax({
			url:top.WEB_ELEMENT_DEL_URL,
			type:"POST",
			data:{id:treeNode.elementId},
			async: false,
			success:function(data){
				if(data.returnCode == 0){				
					layer.msg("删除成功!",{icon:1, time:1800});
				}else{				
					layer.alert(data.msg,{icon:5});
					flag = false;
				}
			}
		});
	} else {
		flag = false;
	}
	
	return flag;
}
/**
 * 删除之后
 * @param event
 * @param treeId
 * @param treeNode
 * @returns
 */
function onRemove(event, treeId, treeNode) {	
	//如果是右侧当前的节点被删除了，或者右侧显示的节点在被删除的节点下面，则默认显示第一个页面
	if (currentNode != null 
			&& ((currentNode.elementId == treeNode.elementId) 
					|| (zTreeObj.getNodeByParam('elementId', currentNode.elementId, treeNode) != null))) {
		var node = zTreeObj.getNodesByFilter(filter, true);
		if (node != null) {
			currentNode = node;
			demoIframe.attr("src","webElement.html?elementId=" + node.elementId);
			demoIframe.click();
		}
	}
}

//判断是否为page和frame类型，如果为此两种类型，则在右侧可以显示他们下属的tag节点列表
function filter(node) {
    return (node.elementType == "page" || node.elementType == "frame");
}

/**
 * 重新加载Ztree
 * @returns
 */
function refreshZtree() {
	$("#treeDemo").spinModal();
	if (zTreeObj != null) {		
		zTreeObj.destroy();
		zTreeObj = null;
	}

	$.post(top.WEB_ELEMENT_LIST_ALL_URL, {nodeFlag:"true"}, function(json){
		$("#treeDemo").spinModal(false);
		if (json.returnCode == 0) {
			var nodes = json.data;  	 				
			$.each(nodes, function(i, n){
				n["iconOpen"] = "../../img/element/" + n.elementType + "_open.png";
				n["iconClose"] = "../../img/element/" + n.elementType + "_close.png";
				
			});
			
			zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, nodes);
			demoIframe = $("#element-frame");
			demoIframe.bind("load", loadReady);							
			if (currentNode == null) {
				var node = zTreeObj.getNodesByFilter(filter, true);
   				if (node != null) {
   					currentNode = node;
   					demoIframe.attr("src","webElement.html?elementId=" + node.elementId);
	   				demoIframe.click();
   				}				
			}
			(currentNode != null) && zTreeObj.selectNode(currentNode);
		} else {
			layer.alert(json.msg, {icon:5});
		}
	});
}

function loadReady() {
	var bodyH = demoIframe.contents().find("body").get(0).scrollHeight,
	htmlH = demoIframe.contents().find("html").get(0).scrollHeight,
	maxH = Math.max(bodyH, htmlH), minH = Math.min(bodyH, htmlH),
	h = demoIframe.height() >= maxH ? minH:maxH ;
	if (h < 530) h = 530;
	demoIframe.height(h);
}