var setId; //当前测试id
var currIndex;//当前正在操作的layer窗口的index
var addMode;
var templateParams = {
		tableTheads:["名称", "包含场景", "创建时间", "独立客户端测试","备注", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			markClass:"add-object",
			iconFont:"&#xe600;",
			name:"添加组合场景"
		},{
			type:"danger",
			size:"M",
			markClass:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		formControls:[
		{
			edit:true,
			label:"ID",  	
			objText:"idText",
			input:[{	
				hidden:true,
				name:"id"
				}]
		},
		{
			required:true,
			label:"组合场景名称",  
			input:[{	
				name:"complexSceneName",
				placeholder:"输入组合场景名称"
				}]
		},
		{
			 name:"createTime",
			 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
			
		 },
		 {
			name:"configInfo",
			value:"{}"
		 },
		 {	
			required:true,
			label:"成功条件",  			
			select:[{	
				name:"successFlag",
				option:[{
					value:"0",
					text:"全部场景测试通过"
				},{
					value:"2",
					text:"单独统计各场景测试结果"
				}]
				}]
		 },
		 {	
				required:true,
				label:"使用独立客户端测试",  
				reminder:"<strong>此选项只对http/https协议的接口场景有效，为了避免测试集中所有测试场景在并发执行时采用同一个测试客户端造成的cookie信息覆盖的问题，你可以选择此项。</strong>",
				select:[{	
					name:"newClient",
					option:[{
						value:"1",
						text:"否"
					},{
						value:"0",
						text:"是"
					}]
					}]
		},
		 {
			label:"备注",  
			textarea:[{	
				name:"mark"
				}]
		}
		]		
	};

var columnsSetting = [
                      {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.complexSceneName, data.id, "selectComplexScene");
                          }},
                      {"data":"id"},
                      ellipsisData("complexSceneName"),                     
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
            	       ellipsisData("createTime"),
            	       {
            		    	"data":"newClient",
            		    	"render":function(data){
            		    		var option = {
                    		  			"0":{
                    		  				btnStyle:"success",
                    		  				status:"是"
                    		  			},
                    		  			"1":{
                    		  				btnStyle:"default",
                    		  				status:"否"
                    		  			},
                    		  			"default":{
                    		  				btnStyle:"default",
                    		  				status:"否"
                    		  			}
                    		  				
                    		  	};	
                    		  	return labelCreate(data, option);	
            		    	}
            	       },
                      {
            		    "data":"mark",
            		    "className":"ellipsis",
            		    "render":function(data, type, full, meta) { 
            		    	if (data != "" && data != null) {
                		    	return '<a href="javascript:;" onclick="showMark(\'' + full.complexSceneName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
            		    	}
            		    	return "";
            		    }
                      },
                      {
                          "data":null,
                          "render":function(data, type, full, meta){
                        	if (addMode != null) {
                        		return btnIconTemplate([{
                        			title:"添加场景到测试集",
                    	    		markClass:"add-scene-to-set",
                    	    		iconFont:"&#xe61f;"
                        		}]);
                        	}  
                        	  
                            var context = [{
                            	title:"组合场景测试",
                	    		markClass:"object-test",
                	    		iconFont:"&#xe603;"
                            },{
                	    		title:"组合场景删除",
                	    		markClass:"object-del",
                	    		iconFont:"&#xe6e2;"
                	    	}];
                            if (setId == null) {
                            	context.push({
                    	    		title:"场景编辑",
                    	    		markClass:"object-edit",
                    	    		iconFont:"&#xe6df;"
                    	    	});
                            } 
                          	return btnIconTemplate(context);
                          }}
                  ];
var eventList = {
		".object-test":function(){//组合场景测试	
			var data = table.row( $(this).parents('tr') ).data();
			layer.confirm('确认测试该组合场景吗？<br>组合场景的测试时间长度和包含的场景数有关,请耐心等待测试完成!', {title:'提示', icon:3}, function(index){				
				var loadIndex = layer.msg('正在测试,请耐心等待...', {icon:16, time:9999999, shade:0.4});
				$.post(top.TEST_COMPLEX_SCENE_URL, {id:data.id}, function(json){
					layer.close(loadIndex);
					if (json.returnCode == 0 && json.result != null) {
						var results = json.result;
						if (json.result.length == undefined) {
							results = json.result.complexSceneResults;
						}
						layer_show(data.complexSceneName + '-测试结果  点击查看对应详情', templates["complex-scene-results-view"]({results:results}), 600, 300, 1, function(layero, index){							
							layero.find('.result-view').bind('click', function(){
								renderResultViewPage(results[$(this).attr("data-id")]);
							});
						}, function(index, layero) {
							layero.find('.result-view').unbind('click');
						}, null)
						
						
					} else {
						layer.alert(strIsNotEmpty(json.msg) ? json.msg : '测试出错!', {icon:5});
					}
				});
				layer.close(index);
			});			
		},
		".add-object":function() {//添加组合场景或者给测试集添加组合场景
			if (setId == null) {
				publish.renderParams.editPage.modeFlag = 0;					
				currIndex = layer_show("添加组合场景", editHtml, editPageWidth, editPageHeight.add, 1);
				publish.init();	
			} else {
				layer_show("添加组合场景到测试集", "complexScene.html?setId=" + setId + "&addMode=0", null, null, 2, null, null, function () {
					refreshTable();
				});
			}
					
		},
		".add-scene-to-set":function () {//添加该组合场景到测试集
			var data = table.row( $(this).parents('tr') ).data();
			opObj("确认添加该组合场景到指定测试集吗？", top.COMPLEX_SCENE_ADD_TO_SET_URL, {id:data.id, setId:setId}, this, "添加成功!");
		},
		".batch-del-object":function() {//批量删除组合场景或者从测试集中批量删除组合场景
			var checkboxList = $(".selectComplexScene:checked");
			if (setId == null) {
				batchDelObjs(checkboxList, top.COMPLEX_SCENE_DEL_URL);
			} else {
				batchOp(checkboxList, top.COMPLEX_SCENE_DEL_FROM_SET_URL, "删除", null, null, {setId:setId});
			}
			
		},
		".object-edit":function() {//编辑组合场景
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;
			publish.renderParams.editPage.objId = data.id;
			layer_show("编辑组合场景信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function() {//删除组合场景或者从测试集中删除指定组合场景
			var data = table.row( $(this).parents('tr') ).data();
			if (setId == null) {
				delObj("确定删除此组合场景？ 请慎重操作!", top.COMPLEX_SCENE_DEL_URL, data.id, this);
			} else {
				opObj("确定从当前测试集删除该组合场景中吗？", top.COMPLEX_SCENE_DEL_FROM_SET_URL, {id:data.id, setId:setId}, this, "删除成功!");
			}
			
		},
		".show-scenes":function() { //管理组合中的测试场景
			if (setId != null) {//测试集中展示组合场景不能管理
				return false;
			}			
			var data = table.row( $(this).parents('tr') ).data();
			layer_show(data.complexSceneName + "-组合场景设定", "messageScene.html?complexSceneId=" + data.id, null, null, 2, null, null, function() {
				refreshTable();
			})
		}
};


var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){			
			setId = GetQueryString("setId");
			addMode = GetQueryString("addMode");
			if (setId != null && addMode == null) {
				publish.renderParams.listPage.listUrl = top.COMPLEX_SCENE_LIST_SET_SCENES_URL + "?setId=" + setId;
				publish.renderParams.listPage.dtOtherSetting.serverSide = false;
			}
			if (addMode != null) {
				$(".cl").hide();
			}
			df.resolve();			   		 	
   	 	},
		editPage:{
			editUrl:top.COMPLEX_SCENE_EDIT_URL,
			getUrl:top.COMPLEX_SCENE_GET_URL,
			rules:{
				complexSceneName:{
					required:true,
					minlength:2,
					maxlength:255
				}										
			}
		},		
		listPage:{
			listUrl:top.COMPLEX_SCENE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 3, 6, 7],
			dtOtherSetting:{
				"bStateSave": false
			}
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/**********************************************************************************************************************/




