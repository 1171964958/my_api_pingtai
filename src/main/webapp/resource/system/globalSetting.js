var editInfo = {};
var beforeSettings = {};

var styleMark = {
		"time":{
			"value":'',
			"title":'测试集测试报告',
			"marks":{
				"${time}":"测试时间",
				"${totalCount}":"场景总数",
				"${successCount}":"测试成功数",
				"${failCount}":"测试失败数",
				"${stopCount}":"测试异常数"
			}
		},
		"probe":{
			"value":'',
			"title":"接口探测任务",
			"marks":{
				"${id}":"任务id",
				"${interfaceScene}":"接口场景名称",
				"${level}":"探测级别",
				"${time}":"探测时间",
				"${status}":"状态",
				"${useTime}":"耗时",
				"${code}":"状态码"
			}
		}		
};

var eventList = {
		".save-setting":function(){
			if(getJsonLength(editInfo) > 0){
				$.post(publish.renderParams.editPage.editUrl, editInfo, function(data) {
					if(data.returnCode == 0){	
						editInfo = {};
						layer.msg("修改成功!",{icon:1, time:1500});
					}else{
						layer.alert(data.msg,{icon:5});
					}
				});
			}
		},
		".setting-mail-style":function(){
			var type = $(this).attr('style-type');
			var obj = JSON.parse($("#messageMailStyle").val());
			styleMark[type]['value'] = obj[type];
			layer_show("设置推送 " + styleMark[type]['title'] + " 邮件格式", templates["global-setting-mail-style"](styleMark[type]), 1065, 460, 1, null, function(index, layero){
				obj[type] = $("#settingStyleValue").val() || '';
				$("#messageMailStyle").val(JSON.stringify(obj));
				$("#messageMailStyle").change();
				layer.msg('以保存内容,请点击更新按钮更新!', {time:1600, icon:1});
			}, null, {shadeClose:false});
		}
};



var mySetting = {
		eventList:eventList,
		userDefaultRender:false,    
   	 	userDefaultTemplate:false,
   	 	customCallBack:function(params){
   	 		$.Huitab("#tab-system .tabBar span", "#tab-system .tabCon", "current","click", "0");
	   	 	$.post(params.editPage.getUrl,function(data){
				if(data.returnCode==0){
					var o=data.data;
					$.each(o,function(i,n){								
						if ($("#" + n.settingName)) {
							$("#" + n.settingName).val((strIsNotEmpty(n.settingValue) ? n.settingValue : n.defaultValue));
							beforeSettings[n.settingName] = n.settingValue;
						}						
					});
					$("input,select,textarea").change(function(){
						if (beforeSettings[$(this).attr("name")] == $(this).val()){
							delete editInfo[$(this).attr("name")];
						} else {
							editInfo[$(this).attr("name")] = $(this).val();
						}					
					});
				}else{
					layer.alert(data.msg,{icon:5});
				}
			});
   	 	},
		editPage:{
			editUrl:top.GLOBAL_SETTING_EDIT_URL,
			getUrl:top.GLOBAL_SETTING_LIST_ALL_URL
		}	
	};


$(function(){			
	publish.renderParams = $.extend(true,publish.renderParams, mySetting);
	publish.init();
});