var setId; //当前正在操作的set
var setName; //当前正在操作的setName
var currIndex;//当前正在操作的layer窗口的index


var selectMode = "1";//是否为选择模式，只供选择0-是   1-不是

var templateParams = {
		tableTheads:["名称", "场景数", "组合场景数","状态", "创建用户", "创建时间", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"batch-move-folder",
			iconFont:"&#xe66c;",
			name:"批量移动"
		},{
			type:"danger",
			size:"M",
			id:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		formControls:[
		{
			edit:true,
			label:"ID",  	
			objText:"setIdText",
			input:[{	
				hidden:true,
				name:"setId"
				}]
		},
		{
			required:true,
			label:"测试集名称",  
			input:[{	
				name:"setName",
				placeholder:"2-100字符"
				}]
		},
		{	
			required:true,
			label:"状态",  			
			select:[{	
				name:"status",
				option:[{
					value:"0",
					text:"正常"
				},{
					value:"1",
					text:"禁用"
				}]
				}]
		},
		{
			label:"备注",  			
			textarea:[{
				name:"mark"
			}]
		},
		{
			edit:true,
			label:"创建用户",  	
			objText:"user.realNameText",
			
		},
		{
			edit:true,
			label:"创建时间",  	
			objText:"createTimeText",
			input:[{	
				hidden:true,
				name:"createTime"
			}]
		},
		{
			name:"user.userId",
		}]		
	};


var columnsSetting = [
                      {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.setName, data.setId, "selectSet");
                          }},
                      {"data":"setId"},
                      ellipsisData("setName"),
                      {
                    	  "data":"sceneNum",
                          "render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"default",
                        			size:"M",
                        			markClass:"show-scenes",
                        			name:data
                        		}];
                              return btnTextTemplate(context);
                              }
                      },
                      {
                    	  "data":"complexSceneNum",
                          "render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"default",
                        			size:"M",
                        			markClass:"show-complex-set-scenes",
                        			name:data
                        		}];
                              return btnTextTemplate(context);
                              }
                      },
                      {
                    	  "data":"status",
                    	  "render":function(data) {
                    		  	var option = {
                    		  			"0":{
                    		  				btnStyle:"success",
                    		  				status:"正常"
                    		  				},
                		  				"1":{
                		  					btnStyle:"danger",
                		  					status:"禁用"
                		  					}
                    		  	};	
                    		  	return labelCreate(data, option);							
                    	  }
                      },
                      ellipsisData("user.realName"),ellipsisData("createTime"),
                      {
                          "data":null,
                          "render":function(data, type, full, meta){
                            var context;                            
                            if (selectMode == "0") {
                            	context = [{
                            		title:"选择测试集",
                    	    		markClass:"object-select",
                    	    		iconFont:"&#xe676;"
                            	}];
                            } else {
                            	context = [{
                    	    		title:"测试集删除",
                    	    		markClass:"object-del",
                    	    		iconFont:"&#xe6e2;"
                    	    	}]; 
                            }
                                                                                
                          	return btnIconTemplate(context);
                          }}
                  ];

var eventList = {
		"#add-object":function() {
			publish.renderParams.editPage.modeFlag = 0;					
			currIndex = layer_show("增加测试集", editHtml, "600", "400", 1);
			//layer.full(index);
			publish.init();			
		},
		"#batch-del-object":function() {
			var checkboxList = $(".selectSet:checked");
			batchDelObjs(checkboxList, top.SET_DEL_URL, null, null, function() {
				parent.createNodeTree();
			});
		},
		"#batch-move-folder":function() {
			var checkboxList = $(".selectSet:checked");
			if (checkboxList.length < 1) {
				return false;
			}
			
			var show_html = '<div class="row cl" style="width:340px;margin:15px;">'		 
				+ '<div class="formControls col-xs-10"><span class="select-box radius mt-0">'
				+ '<select class="select" size="1" name="select-object" id="select-object">'
				+ '</select></span></div><div class="form-label col-xs-2">'
				+ '<input type="button" class="btn btn-primary radius" onclick="" id="show-select-box-choose" value="选择"/></div></div>';
				
			var index = layer_show("请选择要移动到的目录", show_html, '400', '120', 1, function() {				
				$.get(top.SET_GET_CATEGORY_NODES_URL, function(json) {
					$(".page-container").spinModal(); 
       	 			if (json.returnCode == 0) {
       	 				$.each(json.nodes, function(i, node) {
       	 					createOption(node, $("#select-object"), "");
       	 				});
       	 				$(".page-container").spinModal(false); 
       	 			} else {
       	 				$(".page-container").spinModal(false); 
       	 				layer.alert(json.msg, {icon:5});
       	 			}
       	 		});	
				
				$(document).delegate("#show-select-box-choose", 'click', function() {
					var parentId = $('#select-object option:selected').val();
					batchOp(checkboxList, top.SET_MOVE_TO_FOLDER, "移动", table, "setId", {parentId:parentId}
							, function() {
								layer.close(index);
								parent.createNodeTree();
					})
				});
			})		
		},
		".object-edit":function() {
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.setId;
			layer_show("编辑测试集信息", editHtml, "600", "480",1);
			publish.init();	
		},
		".object-del":function() {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此测试集(删除测试集不会删除下属测试场景)？请慎重操作!", top.SET_DEL_URL, data.setId, this);
		},
		".show-scenes":function() { //打开场景页,可在此页添加删除场景
			if (selectMode == "0") {
				return false;
			}
			var data = table.row($(this).parents('tr')).data();
			currIndex = layer_show(data.setName + "-测试场景", "setScene.html?setId=" + data.setId, null, null, 2, null, function() {
				refreshTable();
			});			
		},		
		".object-select":function() {//选择指定场景-定时任务模块
			var data = table.row( $(this).parents('tr') ).data();
			parent.$("#relatedId").val(data.setId);
			parent.$("#choose-task-set").siblings("span").remove();
			parent.$("#choose-task-set").before('<span>' + data.setName + '&nbsp;</span>');	
			parent.layer.close(parent.layer.getFrameIndex(window.name));
		}, 
		".show-complex-set-scenes":function() { //组合场景
			if (selectMode == "0") {
				return false;
			}
			var data = table.row( $(this).parents('tr') ).data();			
			/*$(this).attr("data-title", data.setName + " - 测试集 - 组合场景");
			$(this).attr("_href", "resource/message/complexScene.html?setId=" + data.setId);
			parent.Hui_admin_tab(this);*/
			parent.layer_show(data.setName + " - 测试集 - 组合场景", "complexScene.html?setId=" + data.setId, null, null, 2);
		}
};

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			selectMode = GetQueryString("selectMode");
			if (selectMode == "0") {
				$(".breadcrumb").hide();
				$("#btn-tools").parent('.cl').hide();
			}
			df.resolve();
		},
		editPage:{
			editUrl:top.SET_EDIT_URL,
			getUrl:top.SET_GET_URL,
			rules:{
				setName:{
					required:true,
					minlength:2,
					maxlength:100,
					remote:{
						url:top.SET_NAME_CHECK_URL,
						type:"post",
						dataType: "json",
						data: {                   
					        setName: function() {
					            return $("#setName").val();
					        },
					        setId:function(){
					        	return $("#setId").val();
					        }
					}}
				}				
			}
		},		
		listPage:{
			listUrl:top.SET_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 8],
			exportExcel:false
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});


/********************************************************************************************************/


/**
 * 父目录选择下拉框
 * @param node
 * @param selectObj
 * @param sign
 * @returns {Boolean}
 */
function createOption(node, selectObj, sign) {
	//不能将当前节点移动到自己节点下和自己的子节点下
	if (node.id == $("#setId").val()) {		
		return false;
	}
	if (!node.parented) {
		return false;
	}
	selectObj.append('<option value="' + node.id + '">' + sign + "╟&nbsp;" + node.name + '</option>');
	if (node.children != null && node.children.length > 0) {
		$.each(node.children, function(i, n) {
			createOption(n, selectObj, sign + "&nbsp;&nbsp;");
		});
	}
}