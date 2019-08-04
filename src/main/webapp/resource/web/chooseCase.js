var choosedCallBackFun;//成功选择之后的回调函数
var templateParams = {
		btnTools:[],
		tableTheads:["用例片段名称", "类型","测试步骤", "创建时间", "创建用户", "最近测试结果", "最近测试时间", "备注","操作"]			
	};

var columnsSetting = [
	  {
	  	  	"data":null,
	  	  	"render":function(data, type, full, meta){                       
	  	  		return checkboxHmtl(data.caseName, data.caseId, "selectCase");
	  }},
	  {"data":"caseId"},
	  ellipsisData("caseName"),
	  {
		   	"data":"caseType",
			"render":function(data) {
				var option = {
	  			"common":{
	  				btnStyle:"primary",
	  				status:"通用"
	  				},
	  			"snippet":{
	  				btnStyle:"primary",
	  				status:"用例片段"
	  				}
				};	
				return labelCreate(data, option);
	  }},
	  {
		  "data":"stepNum",
            "render":function(data, type, full, meta){
            	var context =
            		[{
          			type:"default",
          			size:"M",
          			markClass:"show-case-step",
          			name:data
          		}];
                return btnTextTemplate(context);
            }
	  },
	  ellipsisData("createTime"),
	  ellipsisData("createUser.realName"),
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
		  		var context = [{
		    			title:"选择",
		        		markClass:"choose-this",
		        		iconFont:"&#xe6ab;"
		  			}];                     	                   	                 		                       	                        	
	               return btnIconTemplate(context);
		  		}
}];
	

var eventList = {
		".choose-this":function() {//选择
			var data = table.row( $(this).parents('tr') ).data();
			choosedCallBackFun(data, 'case');
			parent.layer.close(parent.layer.getFrameIndex(window.name));
		}
};

var caseId;//该测试步骤所在的测试用例
var mySetting = {
		eventList:eventList,
		templateCallBack:function(df) {
			choosedCallBackFun = parent[GetQueryString("callbackFun")];	
			caseId = GetQueryString("caseId");
			if (caseId != null) {
				publish.renderParams.listPage.listUrl = top.WEB_CASE_LIST_URL + "?caseType=snippet&caseId=" + caseId;
			}	
			df.resolve();
		},
		listPage:{
			listUrl:top.WEB_CASE_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 10],
			dblclickEdit:false,
			exportExcel:false,
			dtOtherSetting:{
				"stateSave": false
			}
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});

/**********************************************************************************************************************/