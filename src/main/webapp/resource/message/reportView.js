var resultData;
var reportId;

var currentIndex;

var REPORT_GET_DETAILS_URL = "report-getReportDetail";

var eventList = {
	'tbody > tr':function() {
		if ($(this).hasClass("fixed-complex-table")) {
			return false;
		}		
		var resultInfo;
		
		if ($(this).hasClass("complex-scene")) {
			resultInfo = resultData[$(this).attr("complex-id")]["complexSceneResults"][$(this).attr("result-id")]
			$(this).addClass('danger').siblings('tr').removeClass('danger');
		} else {
			var id = $(this).attr("id");   
			$(this).addClass('info').siblings('tr').removeClass('info');
	        resultInfo = resultData[id];
	        if (resultInfo["protocolType"] == "FIXED") {
	        	if ($(this).attr("hide-flag") == "false") {
	        		$(this).next('tr').removeClass("atp-show").addClass("hidden");
	        		$(this).attr("hide-flag", "true");
	        	} else {
	        		$(this).next('tr').removeClass("hidden").addClass("atp-show");
	        		$(this).attr("hide-flag", "false");
	        	}
	        	return false;
	        }
		}
		
        layer.open({
            type: 1,
            area: ['760px', '750px'],
            maxmin: false,
            fixed:false,
            //isOutAnim:false,
            //anim:-1,
            shade:0.4,
            title: resultInfo.messageInfo,
            shadeClose:true,
            content: $("#view-html").html(),
            success:function(layero) {            	
            	$(".layui-layer-content > .view-details > .row:eq(0) .col-sm-9").text(resultInfo.requestUrl);
            	$(".layui-layer-content > .view-details > .row:eq(1) .col-sm-9").text(resultInfo.businessSystemName);
                $(".layui-layer-content > .view-details > .row:eq(2) .col-sm-9").text(resultInfo.statusCode);
                $(".layui-layer-content > .view-details > .row:eq(3) .col-sm-9 textarea").text(resultInfo.requestMessage);
                $(".layui-layer-content > .view-details > .row:eq(4) .col-sm-9 textarea").text((resultInfo.responseMessage != null) ? resultInfo.responseMessage : "");
                $(".layui-layer-content > .view-details > .row:eq(5) .col-sm-9 textarea").text((resultInfo.mark != null) ? resultInfo.mark : "");
            }
        });
	},
	'.select-view-mark':{
		'change':function() {
			renderReportView();
		}
	}
};

var mySetting = {
		eventList:eventList,
		userDefaultRender:false,    
   	 	userDefaultTemplate:false,
   	 	customCallBack:function(params){
   	 		 currentIndex = layer.msg('正在努力加载中...', {icon:16, time:99999, shade:0.1});
   	 		 
	   	 	//esc关闭所有弹出层
	       	 $(window).keydown (function(e) {
	       		 var keycode = event.which;
	       		 if(keycode == 27){
	       			 layer.closeAll('page');
	       			 e.preventDefault();
	       		 } 
	       	 });
   	 		
   	 		reportId = GetQueryString("reportId"); 
	   	 	$.get(REPORT_GET_DETAILS_URL + "?reportId=" + reportId, function(data){
				if (data.returnCode == 0) {
					data = JSON.parse(data.details);
					$(".panel-heading").text(data.title);
			        $("#sceneNum").append(data.desc.sceneNum);
			        $("#successNum").append(data.desc.successNum);
			        $("#failNum").append(data.desc.failNum);
			        $("#stopNum").append(data.desc.stopNum);
			        $("#successRate").append(data.desc.successRate);
			        $("#testDate").append(data.desc.testDate);
			        var $selectSystems = $("#business-system-views");
			        $.each(data.desc.systems, function(i, name){
			        	$selectSystems.append('<option value="' + name + '">' + name + '</option>');
			        });
			        
			        resultData = data.data;
			        
			        renderReportView();
			         
				} else {
					layer.alert(data.msg,{icon:5});
				}
			});
   	 	}
	};


$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});


function renderReportView() {
	
    var dataHtml = '';
    var publicStatus = $("#status-views").val();
    var publicProtocol = $("#protocol-views").val();
    var publicSystemName = $("#business-system-views").val();
    $.each(resultData, function(i, report){
    	
    	if ((report.runStatus == publicStatus || publicStatus == "all")
    			&& (report.protocolType == publicProtocol || publicProtocol == "all")
    			&& (report.businessSystemName == publicSystemName || publicSystemName == "all")) {
    		var messageInfo = (report.messageInfo).split(",");        	
            dataHtml += '<tr id="' + i + '">'    
                    + '<td>' + messageInfo[0] + '</td>'
                    + '<td><span class="label label-primary">' + report.protocolType + '</span></td>'
                    + '<td><span class="label label-primary">' + (report.messageType || "") + '</span></td>'
                    + '<td>' + messageInfo[1] + '</td>'
                    + '<td>' + messageInfo[2] + '</td>'
                    + '<td>' + report.businessSystemName + '</td>'
                    + '<td class="status"><span>' + report.runStatus + '</span></td>'
                    + '<td>' + report.useTime + '</td>'
                    + '</tr>';
            if (report.protocolType == "FIXED") {
            	dataHtml += '<tr class="hidden fixed-complex-table"><td colspan="8"><table class="table table-bordered">';
            	$.each(report.complexSceneResults, function (n, result) {
            		var messageInfo = (result.messageInfo).split(",");
            		dataHtml += '<tr complex-id="' + i + '" result-id="' + n + '" class="complex-scene">'    
                    	+ '<td>' + messageInfo[0] + '</td>'
                    	+ '<td><span class="label label-primary">' + result.protocolType + '</span></td>'
                        + '<td><span class="label label-primary">' + (result.messageType || "") + '</span></td>'
                    	+ '<td>' + messageInfo[1] + '</td>'
                    	+ '<td>' + messageInfo[2] + '</td>'
                    	+ '<td>' + result.businessSystemName + '</td>'
                    	+ '<td class="status"><span>' + result.runStatus + '</span></td>'
                    	+ '<td>' + result.useTime + '</td>'
                    	+ '</tr>';
            	});
            	dataHtml += '</table></td></tr>';
            }
    	}    	
     });
     $("tbody").html(dataHtml);
     
     var statusList = $(".status");
     var status;
     $.each(statusList, function(i, n){
    	 var $status = $(n).children('span');
         status = $status.text();
         if (status == "0") {
        	 $status.addClass('label label-success');
        	 $status.text("Success");
         }

         if (status == "1") {
        	 $status.addClass('label label-danger');
        	 $status.text("Fail");
         }

         if (status == "2") {
        	 $status.addClass('label label-default');
        	 $status.text("Stop");
         }
     });
     $(".count-tips").text('当前共 ' + $('.status').length + ' 项测试结果,成功 ' + $('.status > .label-success').length 
      		+ ' 项,失败 ' + $('.status > .label-danger').length + ' 项,异常中断 ' + $('.status > .label-default').length + ' 项.');
     /*setTimeout(function() {
    	 layer.close(currIndex);
     }, 3000)*/
     layer.close(currentIndex);
}