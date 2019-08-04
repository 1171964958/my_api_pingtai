var suiteId;
var mode = 0; //0-管理  1-添加

var templateParams = {
		tableTheads:["测试用例", "步骤数", "状态", "最近运行", "最近结果", "备注", "组名", "执行顺序", "跳过执行", "操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"manger-case",
			iconFont:"&#xe60c;",
			name:"管理场景"
		},{
			size:"M",
			id:"add-case",
			iconFont:"&#xe600;",
			name:"添加场景"
		},{
			size:"M",
			id:"batch-op",
			iMarkClass:"Hui-iconfont-del3",
			name:"批量操作"
		}]		
};

var columnsSetting = [
					{
					  	"data":null,
					  	"render":function(data, type, full, meta){                       
					          return checkboxHmtl(data.caseName, data.caseId, "selectCase");
					 }},
					 {
						"data":"caseId",
						"render":function(data, type, full, meta){
							return full.compId || data;
						}
					 },
					 ellipsisData("caseName"),					 
					 {
						  "data":"stepNum",
			                "render":function(data, type, full, meta){
			                	var context =
			                		[{
			              			type:"default",
			              			size:"M",
			              			name:data
			              		}];
			                    return btnTextTemplate(context);
			                }
					  },
					  {
						   	"data":"status",
							"render":function(data) {
								var option = {
		    		  			"1":{
		    		  				btnStyle:"success",
		    		  				status:"可用"
		    		  				},
		    		  			"0":{
		    		  				btnStyle:"danger",
		    		  				status:"禁用"
		    		  				}
								};	
								return labelCreate(data, option);
					  }},
					  ellipsisData("lastRunTime"),
					  {
						  "data":"lastRunStatus",
						  "render":function(data) {
							  if (!strIsNotEmpty(data)) {
								  return "";
							  }
							  var option = {
		      		  			"1":{
		      		  				btnStyle:"success",
		      		  				status:"成功"
		      		  				},
		      		  			"0":{
			      		  			btnStyle:"danger",
			  		  				status:"失败"
		      		  			}};	
							 return labelCreate(data, option);
						  }
					  },
					  longTextData("mark", "caseName", "备注信息"),
					  {
						  "data":null,
						  "render":function(data, type, full, meta){
							  return data.groupName || '';
						  }
					  },
					  {
						  "data":null,
						  "render":function(data, type, full, meta){
							  return data.execSeq || '';
						  }
					  },
					  {
						  "data":"skipFlag",
						  "render":function(data, type, full, meta){
							  if (!strIsNotEmpty(data)) {
								  return "";
							  }
							  var option = {
		      		  			"1":{
		      		  				btnStyle:"danger",
		      		  				status:"是"
		      		  				},
		      		  			"0":{
			      		  			btnStyle:"primary",
			  		  				status:"否"
		      		  			}};	
							 return labelCreate(data, option);
						  }
					  },
					  {
                         "data":null,
                         "render":function(data, type, full, meta){    
                        	 var context;
                           //管理
                           if (mode == 0) {
                        	   context = [{
                        		   	title:"删除",
	   	               	    		markClass:"op-case",
	   	               	    		iconFont:"&#xe6e2;"
                        	   },{
                        		    title:"配置",
	   	               	    		markClass:"setting-case",
	   	               	    		iconFont:"&#xe62e;"
                        	   }];
                           }
                           
                           //添加
                           if (mode == 1) {
                        	   context = [{
	   	               	    		title:"添加",
	   	               	    		markClass:"op-case",
	   	               	    		iconFont:"&#xe600;"
                 	    		}];
                           }
                         	return btnIconTemplate(context);
                       }}										     
];


var eventList = {		
		"#batch-op":function() { //批量操作 删除或者添加
			var checkboxList = $(".selectCase:checked");
			var opName = "删除";
			if (mode == 1) {
				opName = "添加";
			}
			//batchDelObjs(checkboxList, top.WEB_SUITE_OP_CASE_URL + "?mode=" + mode + "&suiteId=" + suiteId, table, opName);
			batchOp (checkboxList, top.WEB_SUITE_OP_CASE_URL, opName, table, null
					, {mode:mode, suiteId:suiteId}, null, function(dom, params){
						var data = table.row( $(dom).parents('tr') ).data();
						params['caseId'] = data.caseId;
						if (mode == 0) params['compId'] = data.compId;
					});
			
		},		
		"#manger-case":function() { //管理测试集 - 从测试集中删除
			var that = this;
			mode = 0;				
			refreshTable(top.WEB_SUITE_LIST_CASE_URL + "?suiteId=" + suiteId, function(json) {
				$(that).addClass('btn-primary').siblings("#add-case").removeClass('btn-primary');
			}, null, true);	
			$("#batch-op").children("i").removeClass("Hui-iconfont-add").addClass("Hui-iconfont-del3");
		},
		"#add-case":function() { //添加测试用例
			var that = this;
			mode = 1;			
			refreshTable(top.WEB_CASE_LIST_ALL_URL + "?caseType=common", function(json) {
				$(that).addClass('btn-primary').siblings("#manger-case").removeClass('btn-primary');
			}, null, true);	
			$("#batch-op").children("i").removeClass("Hui-iconfont-del3").addClass("Hui-iconfont-add");
		},	
		".op-case":function() {//单条删除或者添加
			var data = table.row( $(this).parents('tr') ).data();
			var tip = '添加';		
			var sendData = {mode:mode, suiteId:suiteId, caseId:data.caseId};
			if (mode == 0) {
				tip = "删除";
				sendData['compId'] = data.compId;
			}						
			
			var that = this;
			layer.confirm('确定要' + tip + '此用例吗?', {icon:0, title:'警告'}, function(index) {
				layer.close(index);
				$.get(top.WEB_SUITE_OP_CASE_URL, sendData, function(json) {
					if (json.returnCode == 0) {
						if (mode == 0) {
							table.row($(that).parents('tr')).remove().draw();
						}
						layer.msg(tip + '成功!', {icon:1, time:1500});
					} else {
						layer.alert(json.msg, {icon:5});
					}
				});
			});						
		},
		".setting-case":function(){//设置测试集用例的配置
			var that = this;
			var data = table.row($(that).parents('tr')).data();
			layer_show('测试集用例配置', templates['web-suite-case-setting'](data), 700, 330, 1, function(layero, index){
				$(layero).find('#skipFlag').val(data.skipFlag);
				
				$(layero).find('#update-setting').one('click', function(){
					var sendData = {
						compId:$(layero).find('#compId').val(),
						skipFlag:$(layero).find('#skipFlag').val(),
						execSeq:$(layero).find('#execSeq').val(),
						groupName:$(layero).find('#groupName').val()
					};
					$.post(top.WEB_SUITE_CASE_UPDATE_SETTING_URL, sendData, function(json){
						layer.close(index);
						if (json.returnCode == 0) {
							$.extend(true, data, sendData);
							table.row($(that).parents('tr')).data(data).draw();
							layer.msg('更新成功!', {icon:1, time:1800});
						} else {
							layer.alert(json.msg, {icon:5});
						}
					})
				});
			});
		}
};


var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			suiteId = GetQueryString("suiteId");
			publish.renderParams.listPage.listUrl = top.WEB_SUITE_LIST_CASE_URL + "?suiteId=" + suiteId;
			//publish.renderParams.listPage.listUrl = top.WEB_CASE_LIST_ALL_URL + "?caseType=common";			
			df.resolve();			   		 	
   	 	},
		listPage:{
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 11],
			dtOtherSetting:{
				serverSide:false
			},
			exportExcel:false
		},
		templateParams:templateParams		
	};


$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});