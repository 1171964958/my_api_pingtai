var ptResultId;
var templateParams = {
		tableTheads:["接口", "结果", "状态码", "耗时ms", "测试时间", "备注","详情"]			
};

var columnsSetting = [
					 {
					  	"data":null,
					  	"render":function(data, type, full, meta){                       
					          return checkboxHmtl(data.messageInfo, data.resultId, "selectResult");
					      }},
					  {	
						  "data":"messageInfo",
						  "className":"ellipsis probe-scene-name",
						  "render":function(data, type, full, meta){
						    	data = (data.split(","))[0];
						    	return  '<span title="' + data + '">' + data + '</span>';
						    }
						  
					  },
					  {
						  "data":"runStatus",
						  "render":function(data) {
							  var option = {
                  		  			"0":{
                  		  				btnStyle:"success",
                  		  				status:"SUCCESS"
                  		  				},
              		  				"1":{
              		  					btnStyle:"danger",
              		  					status:"FAIL"
              		  					},
              		  				"2":{
	              		  				btnStyle:"disabled",
	          		  					status:"STOP"
              		  					}
                  		  		};	
                  		  	return labelCreate(data, option);
						  }
					  
					  },
					  {"data":"statusCode"},
					  {"data":"useTime"},
					  ellipsisData("opTime"),
					  {
            		    "data":"mark",
            		    "className":"ellipsis",
            		    "render":function(data, type, full, meta) { 
            		    	if (data != "" && data != null) {
                		    	return '<a href="javascript:;" onclick="showMark(\'' + full.messageInfo + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
            		    	}
            		    	return "";
            		    }
                      },
					  {
						  "data":null,
						  "render":function(data) {
							  var context = [{
	                	    		title:"详情查看",
	                	    		markClass:"show-result-detail",
	                	    		iconFont:"&#xe685;"
	                	    	}];                           
	                          	return btnIconTemplate(context);
						  }
					  }
];


var eventList = {	
	'.probe-scene-name:gt(0)':{
		"mouseenter":function () {
			var data = table.row( $(this).parents('tr') ).data();
			var that = this;
			var infoList = data.messageInfo.split(",");
			layer.tips(infoList[1] + "<br>" + infoList[2], that, {
				  tips: [1, '#0FA6D8'],
				  time:9999999
				});
		},
		"mouseleave":function () {
			layer.closeAll('tips');
		}
	},
	'.show-result-detail':function() {
		var data = table.row( $(this).parents('tr') ).data();
		var color = "";
		var flag = "";
		if (data.runStatus == "0") {
			color = "success";
			flag = "SUCCESS";
		} else if (data.runStatus == "1"){
			color = "danger";
			flag = "FAIL";
		} else {
			color = "default";
			flag = "STOP";
		}				
		var resultData = {
			requestMessage:(data.requestMessage == "null") ? "" : data.requestMessage,
			requestUrl:data.requestUrl,
			businessSystemName:data.businessSystemName,
			color:color,
			flag:flag,
			useTime:data.useTime,
			statusCode:data.statusCode,
			responseMessage:(data.responseMessage != "null") ? data.responseMessage : "",
			mark:data.mark
		};			
		
		layer_show('测试结果', templates["scene-test-result"](resultData), null, null, 1, null, null, null, {
			shade: 0.35,
			shadeClose:true,
			skin: 'layui-layer-rim', //加上边框
		})
	}			
};

var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			ptResultId = GetQueryString("ptResultId");
				
			publish.renderParams.listPage.listUrl = top.PERFORMANCE_TEST_RESULT_DETAILS_LIST_ALL_URL + "?ptResultId=" + ptResultId;								
			df.resolve();			   		 	
   	 	},
		listPage:{
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 6, 7],
			dtOtherSetting:{
				"bStateSave": false,//状态保存
				"serverSide": false
			},
			exportExcel:false,
			dblclickEdit:false
		},
		templateParams:templateParams		
	};


$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/******************************************************************************/


