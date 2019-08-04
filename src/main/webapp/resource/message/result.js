var reportId;
var probeId;

var runStatus = "all";

var templateParams = {
		btnTools:[{
			type:"primary",
			size:"M",
			id:"quality-count-view",
			iconFont:"&#xe61e;",
			name:"统计视图"
		}],
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
	'#quality-count-view':function() {//探测质量统计视图
		$.post(top.PROBE_TASK_GET_SINGLE_REPORT_DATA_URL, {probeId:probeId}, function(json) {
			if (json.returnCode == 0) {	
				if (json.data == null) {
					throw "数据不足!";
				}
				echartsViewPage({
					title:json.data.probeName + " 探测结果统计视图",
					btns:["质量统计", "结果统计", "响应时间统计"],
					echartsOptions:createEchartsOption(json.data),
					minTime:json.data.startTime,
					maxTime:json.data.lastTime,
					changeTimeCallback:function(timeRange, echartsInstances){
						$.post(top.PROBE_TASK_GET_SINGLE_REPORT_DATA_URL, {probeId:probeId, probeTimeRange:timeRange}, function (data) {
							if (json.returnCode == 0) {
								var options = createEchartsOption(data.data);
								$.each(echartsInstances, function(i, n) {
									n.setOption(options[i]);
								});
							} else {
								layer.alert(data.msg, {icon:5});
							}
						});
					}
				});
			} else {
				layer.alert(json.msg, {icon:5});
			}
		});

	},
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
			reportId = GetQueryString("reportId");
			runStatus = GetQueryString("runStatus");
			probeId = GetQueryString("probeId");
			
			if (probeId != null) {
				publish.renderParams.listPage.listUrl = top.RESULT_LIST_URL + "?probeId=" + probeId;
			} else {
				$("#quality-count-view").hide();
				publish.renderParams.listPage.listUrl = top.RESULT_LIST_URL + "?reportId=" + reportId + "&runStatus=" + runStatus;
			}			
			
			df.resolve();			   		 	
   	 	},
		listPage:{
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 6, 7],
			dtOtherSetting:{
				"bStateSave": false,//状态保存			
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
function createEchartsOption(probeResultAnalyzeView) {
	/**
	 * 质量分析配置
	 */
	var qualityEchartsOption = {
		title : {
		    text: '探测质量统计',
		    x:'right'
		},
		tooltip : {
		    trigger: 'item',
		    formatter: "{a} <br/>{b} : {c} ({d}%)"
		},
		legend: {
		    orient: 'vertical',
		    left: 'left',
		    data: ['ExcellentLevel(优秀)','NormalLevel(正常)','ProblematicLevel(有问题)','SeriousLevel(严重)']
		},
		series : [
		    {
		        name: '探测质量统计',
		        type: 'pie',
		        radius : '55%',
		        center: ['50%', '60%'],
		        data:[
		            {
		            	value:probeResultAnalyzeView.qualityCount.excellentLevelCount, 
		            	name:'ExcellentLevel(优秀)',
		            	itemStyle:{
		                    normal:{
		                        color:'#27D765'
		                    }
		                }
		            },
		            {
		            	value:probeResultAnalyzeView.qualityCount.normalLevelCount, 
		            	name:'NormalLevel(正常)',
		            	itemStyle:{
		                    normal:{
		                        color:'#008442'
		                    }
		                }
		            },
		            {
		            	value:probeResultAnalyzeView.qualityCount.problematicLevelCount, 
		            	name:'ProblematicLevel(有问题)',
		            	itemStyle:{
		                    normal:{
		                        color:'#FF8442'
		                    }
		                }
		            },
		            {
		            	value:probeResultAnalyzeView.qualityCount.seriousLevelCount, 
		            	name:'SeriousLevel(严重)',
		            	itemStyle:{
		                    normal:{
		                        color:'#F72209'
		                    }
		                }
		            }
		        ],
		        itemStyle: {
		            emphasis: {
		                shadowBlur: 10,
		                shadowOffsetX: 0,
		                shadowColor: 'rgba(0, 0, 0, 0.5)'
		            }
		        }
		    }
		]	
	};

	/**
	 * 接口稳定性分析配置
	 */
	var stabilityEchartsOption = {
		    title: {
		        text: '探测结果统计'
		    },
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: { // 坐标轴指示器，坐标轴触发有效
		            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
		        }
		    },
		    legend: {
		        data: ['探测成功', '探测失败', '异常中断'],
		        align: 'right',
		        right: 10
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis: [{
		        type: 'category',
		        data: probeResultAnalyzeView.stabilityCount.timeBucket
		    }],
		    yAxis: [{
		        type: 'value',
		        name: '数量(次)',
		        axisLabel: {
		            formatter: '{value}'
		        }
		    }],
		    series: [{
		        name: '探测成功',
		        type: 'bar',
		        data: probeResultAnalyzeView.stabilityCount.successCount,
		        itemStyle:{
	                normal:{
	                    color:'#27D765'
	                }
	            }
		    }, {
		        name: '探测失败',
		        type: 'bar',
		        data: probeResultAnalyzeView.stabilityCount.failCount,
		        itemStyle:{
	                normal:{
	                    color:'#F72209'
	                }
	            }
		    }, {
		        name: '异常中断',
		        type: 'bar',
		        data: probeResultAnalyzeView.stabilityCount.stopCount,
		        itemStyle:{
	                normal:{
	                    color:'#848484'
	                }
	            }
		    }]
		};

	/**
	 * 响应时间分析配置
	 */
	var responseTimeEchartOption = {
			title: {
		        text: '响应时间统计',
		        x:'right'
		    },
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross'
		        }
		    },
		    xAxis:  {
		        type: 'category',
		        boundaryGap: false,
		        data: probeResultAnalyzeView.opTime
		    },
		    yAxis: {
		        type: 'value',
		        axisLabel: {
		            formatter: '{value} ms'
		        },
		        axisPointer: {
		            snap: true
		        }
		    },
		    series: [
		        {
		            name:'探测响应时间(毫秒)',
		            type:'line',
		            smooth: true,
		            data: probeResultAnalyzeView.responseTime
		        }
		    ]	
	};
	
	return [qualityEchartsOption, stabilityEchartsOption, responseTimeEchartOption];
	
}


