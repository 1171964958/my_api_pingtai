var choosedCallBackFun;//成功选择之后的回调函数
var templateParams = {
		btnTools:[],
		tableTheads:["元素名称", "元素类型", "页面", "备注", "操作"]			
	};

var columnsSetting = [
	  {
	  	  	"data":null,
	  	  	"render":function(data, type, full, meta){                       
	  	  		return checkboxHmtl(data.elementName, data.elementId, "selectElement");
	  }},
	  {"data":"elementId"},
	  ellipsisData("elementName"),
	  {
		   	"data":"elementType",
		   	"render":function(data) {
		   		var option = {
				"tag":{
					btnStyle:"secondary",
					status:"TAG"
					},
				"url":{
					btnStyle:"success",
					status:"URL"
	  					}
		   		};	
		   		return labelCreate(data, option);
	  }},
	  {
		  "data":"parentElementName"
	  },
	  longTextData("mark", "elementName", "备注信息"),
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
			choosedCallBackFun(data, 'element');
			parent.layer.close(parent.layer.getFrameIndex(window.name));
		}
};


var mySetting = {
		eventList:eventList,
		templateCallBack:function(df) {
			choosedCallBackFun = parent[GetQueryString("callbackFun")];			
			df.resolve();
		},
		listPage:{
			listUrl:top.WEB_ELEMENT_LIST_URL + "?chooseFlag=true",
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 6],
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