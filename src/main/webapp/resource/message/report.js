var currIndex;//当前正在操作的layer窗口的index


var templateParams = {
		tableTheads:["测试集", "场景数", "成功数", "失败数", "异常数", "进度", "开始时间", "结束时间", "测试人" ,"备注", "操作"],
		btnTools:[{
			type:"danger",
			size:"M",
			id:"batch-del-object",
			iconFont:"&#xe6e2;",
			name:"批量删除"
		}],
		advancedQuery:{
			enable:true, 
			formTitle:"测试报告-高级查询",
			formControls:[[
	                       {label:"测试集",
	                       	input:true,
	                       	name:"setName"},
	                       {label:"场景数不少于",
	                       	input:true,
	                       	name:"sceneNum",
	                       	datatype:"number"}
	                       ], [
	                        {label:"测试进度",
	                       	 select:true,
	                       	 name:"finishFlag",
	                       	 option:[{value:"Y", text:"已完成"}, {value:"N", text:"未完成"}]}, 
	                        {label:"测试时间",
	                       	 input:true,
	                       	 name:"createTimeText",
	                       	 datetime:true}
	                        ], [
	                        {label:"测试人",
	                       	 input:true,
	                       	 name:"createUserName"}, 
	                        {label:"测试类型",
	                       	 select:true,
	                       	 name:"mark",
	                       	 option:[{value:"自动化定时任务", text:"定时任务"},{value:"外部API调用", text:"API调用"}]}
	                        ]]
		}
	};


var columnsSetting = [
					{
					  	"data":null,
					  	"render":function(data, type, full, meta){                       
					          return checkboxHmtl(data.setName, data.reportId, "selectReport");
					      }},
					{"data":"reportId"},
					ellipsisData("setName"),
					{
						"data":"sceneNum",
						"render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"primary",
                        			size:"M",
                        			markClass:"show-result",
                        			name:data
                        		}];
                              return btnTextTemplate(context);
                              }
					},
					{
						"data":null,
						"render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"success",
                        			size:"M",
                        			markClass:"show-result",
                        			name:data.successNum
                        		}];
                              return btnTextTemplate(context);
                              }
					},
					{
						"data":null,
						"render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"warning",
                        			size:"M",
                        			markClass:"show-result",
                        			name:data.failNum
                        		}];
                              return btnTextTemplate(context);
                              }
					},
					{
						"data":null,
						"render":function(data, type, full, meta){
                          	var context =
                          		[{
                        			type:"danger",
                        			size:"M",
                        			markClass:"show-result",
                        			name:data.stopNum
                        		}];
                              return btnTextTemplate(context);
                              }
					},
					{
						"data":"finishFlag",
						"render":function(data) {
							var option = {
                		  			"Y":{
                		  				btnStyle:"success",
                		  				status:"已完成"
                		  				},
            		  				"N":{
            		  					btnStyle:"danger",
            		  					status:"未完成"
            		  					},
            		  				 	
                		  	};	
                		  	return labelCreate(data, option);
							
						}
							
					},
					ellipsisData("createTime"),
					ellipsisData("finishTime"),
					ellipsisData("createUserName"),
					ellipsisData("mark"),
					{
						"data":null,
                        "render":function(data, type, full, meta){
                          var context = [{
	              	    		title:"查看报告",
	              	    		markClass:"view-report",
	              	    		iconFont:"&#xe695;"
	              	    	},{
	              	    		title:"离线报告",
	              	    		markClass:"download-report",
	              	    		iconFont:"&#xe640;"
	              	    	},{
	              	    		title:"删除报告",
	              	    		markClass:"object-del",
	              	    		iconFont:"&#xe6e2;"
	              	    	},{
	              	    		title:"邮件推送",
	              	    		markClass:"send-mail",
	              	    		iconFont:"&#xe603;"
	              	    	}];                           
                        	return btnIconTemplate(context);
                        }
					}									
	];
var eventList = {		
		'#batch-del-object':function() {
			var checkboxList = $(".selectReport:checked");
			batchDelObjs(checkboxList, top.REPORT_DEL_URL);
		},
		'.send-mail':function(){ //邮件推送测试报告
			var data = table.row( $(this).parents('tr') ).data();
			if (data.sceneNum < 1) {
	    		layer.alert("当前测试报告中没有任何测试详情结果", {icon:5});
	    		return false;
	    	} 
			layer.confirm('确定将此测试报告进行邮件推送(优先使用测试集中配置的收信地址)?', {title:'提示', icon:3}, function(index){
				loading(true, '正在发送邮件...');
				$.post(top.REPORT_SEND_MAIL_URL, {reportId:data.reportId}, function(json){
					loading(false);
					if (json.returnCode == 0) {
						layer.msg('发送邮件成功,请注意查收!', {icon:1, time:1800});
					} else {
						layer.alert(json.msg, {icon:5});
					}
				});				
				layer.close(index);
			});
		},
		'.show-result':function() {
			var data = table.row( $(this).parents('tr') ).data();
			
			var statusName = "全部";
			var runStatus = "all";
			var count = data.sceneNum;
			
			if ($(this).hasClass("btn-success")) {
				statusName = "成功";
				runStatus = "0";
				count = data.successNum;
			}
			
			if ($(this).hasClass("btn-warning")) {
				statusName = "失败";
				runStatus = "1";
				count = data.failNum;
			}
			
			if ($(this).hasClass("btn-danger")) {
				statusName = "异常";
				runStatus = "2";
				count = data.stopNum;
			}
							
			openResults(data.reportId, runStatus, data.setName + " - " + statusName + "场景", count);
		},
		'.view-report':function() {
			var data = table.row( $(this).parents('tr') ).data();
			if (data.sceneNum < 1) {
	    		layer.alert("当前测试报告中没有任何测试详情结果", {icon:5});
	    		return false;
	    	}   	
	    	window.open("reportView.html?reportId=" + data.reportId);
		},
		'.object-del':function() {
			var data = table.row( $(this).parents('tr') ).data();
			delObj("确定删除此测试报告吗？", top.REPORT_DEL_URL, data.reportId, this);
		},
		'.download-report':function() { //下载离线报告
			var data = table.row( $(this).parents('tr') ).data();
			if (data.sceneNum < 1) {
	    		layer.alert("当前测试报告中没有任何测试详情结果", {icon:5});
	    		return false;
	    	} 
			$.post(top.REPORT_DOWNLOAD_STATIS_HTML, {reportId:data.reportId}, function(json) {
				if (json.returnCode == 0) {
					window.open("../../" + top.DOWNLOAD_FILE_URL + "?downloadFileName=" + json.path);
				} else {
					layer.alert(json.msg,{icon:5});
				}
			});
		}
};

var mySetting = {
		eventList:eventList,
		listPage:{
			listUrl:top.REPORT_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 4, 5, 6, 12]			
		},
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/***************************************************************************/
function openResults(reportId, type, title, count) {
	if (count < 1) {
		return false;
	}
	layer_show(title, "result.html?reportId=" + reportId + "&runStatus=" + type, 1100, null, 2);
}
