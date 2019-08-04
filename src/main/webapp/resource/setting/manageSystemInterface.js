var mode = 0;// 0 - 管理/删除  1 - 添加
var systemId;
var protocolType;

var templateParams = {
		btnTools:[{
			type:"primary",
			size:"M",
			id:"manage-interface",
			iconFont:"&#xe60c;",
			name:"管理接口"
		},{
			size:"M",
			id:"add-interface",
			iconFont:"&#xe600;",
			name:"添加接口"
		},{
			size:"M",
			id:"batch-op",
			iMarkClass:"Hui-iconfont-del3",
			name:"批量操作"
		},{
			size:"M",
			type:"danger",
			id:"update-interface-system",
			iconFont:"&#xe6ce;",
			name:"更新接口下属报文场景的测试环境"
		}],
		tableTheads:["名称", "中文名","类型","协议","创建时间","状态","创建用户", "最后修改", "备注","操作"]			
	};

var columnsSetting = [
                    {
                      	"data":null,
                      	"render":function(data, type, full, meta){                       
                              return checkboxHmtl(data.interfaceName+'-'+data.interfaceCnName,data.interfaceId,"selectInterface");
                    }},
                    {"data":"interfaceId"},
                    ellipsisData("interfaceName"),
                    ellipsisData("interfaceCnName"),
                      {
                      	"data":"interfaceType",
                      	"render":function(data, type, full, meta ){
                      		var option = {
                      				"SL":{
                      					btnStyle:"warning",
                      					status:"受理类"
                      					},
                      				"CX":{
                      					btnStyle:"success",
                      					status:"查询类"
                      				}
                      		};                  
                      		return labelCreate(data, option);
                      }},
                      {
                        "data":"interfaceProtocol",
                        "render":function(data) {
                        	return labelCreate(data.toUpperCase());
                        }
                      },
                      ellipsisData("createTime"),
                      {
                      	"data":"status",
                      	"render":function(data, type, full, meta ){
                              return labelCreate(data);
                      }},
                      ellipsisData("createUserName"),ellipsisData("lastModifyUser"),
                      {
              		    "data":"mark",
              		    "className":"ellipsis",
              		    "render":function(data, type, full, meta) { 
              		    	if (data != "" && data != null) {
                  		    	return '<a href="javascript:;" onclick="showMark(\'' + full.interfaceName + '\', \'mark\', this);"><span title="' + data + '">' + data + '</span></a>';
              		    	}
              		    	return "";
              		    }
                        },
                      {
                          "data":null,
                          "render":function(data, type, full, meta){
                        	  //管理-删除
                              if (mode == 0) {
                           	   return btnIconTemplate([{
   	   	               	    		title:"删除",
   	   	               	    		markClass:"op-interface",
   	   	               	    		iconFont:"&#xe6e2;"
                     	    		}]);
                              }
                              
                              //添加
                              if (mode == 1) {
                           	   	return btnIconTemplate([{
   	   	               	    		title:"添加",
   	   	               	    		markClass:"op-interface",
   	   	               	    		iconFont:"&#xe600;"
                    	    		}]);
                              }
                              
                              return "";
                          }}
                  ];

var eventList = {
	"#manage-interface":function(){
		var that = this;
		mode = 0;				
		refreshTable(top.BUSINESS_SYSTEM_INTERFACE_LIST_URL + "?mode=" + mode + "&systemId=" + systemId, function(json) {
			$(that).addClass('btn-primary').siblings("#add-interface").removeClass('btn-primary');
		}, null, true);	
		$("#batch-op").children("i").removeClass("Hui-iconfont-add").addClass("Hui-iconfont-del3");
	},
	"#add-interface":function(){
		var that = this;
		mode = 1;			
		refreshTable(top.BUSINESS_SYSTEM_INTERFACE_LIST_URL + "?mode=" + mode + "&systemId=" + systemId, function(json) {
			$(that).addClass('btn-primary').siblings("#manage-interface").removeClass('btn-primary');
		}, null, true);	
		$("#batch-op").children("i").removeClass("Hui-iconfont-del3").addClass("Hui-iconfont-add");
	},
	"#batch-op":function(){
		var checkboxList = $(".selectInterface:checked");
		var opName = "删除";
		if (mode == 1) {
			opName = "添加";
		}
		batchDelObjs(checkboxList, top.BUSINESS_SYSTEM_OP_INTERFACE_URL + "?mode=" + mode + "&systemId=" + systemId, table, opName);
	},
	//更新接口下的报文和场景的测试环境
	"#update-interface-system":function(){
		batchOp($(".selectInterface:checked"), top.INTERFACE_UPDATE_CHILDREN_BUSINESS_SYSTEMS_URL, "更新", null, "interfaceId");
	},
	".op-interface":function() {
		var tip = '删除';
		
		if (mode == 1) {
			tip = "添加";
		}
					
		var data = table.row( $(this).parents('tr') ).data();
		var that = this;
		layer.confirm('确定要' + tip + '此接口吗?', {icon:0, title:'警告'}, function(index) {
			layer.close(index);
			$.get(top.BUSINESS_SYSTEM_OP_INTERFACE_URL + "?mode=" + mode + "&systemId=" + systemId + "&interfaceId=" + data.interfaceId, function(json) {
				if (json.returnCode == 0) {
					table.row($(that).parents('tr')).remove().draw();
					layer.msg(tip + '成功!', {icon:1, time:1500});
				} else {
					layer.alert(json.msg, {icon:5});
				}
			});
		});	
	}
};


var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			systemId = GetQueryString("systemId");
			publish.renderParams.listPage.listUrl = top.BUSINESS_SYSTEM_INTERFACE_LIST_URL + "?mode=" + mode + "&systemId=" + systemId;
			df.resolve();
		},
		listPage:{
			listUrl:"",
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 10, 11],
			dblclickEdit:false,
			exportExcel:false
		},
		templateParams:templateParams		
	};

$(function(){
	publish.renderParams = $.extend(true, publish.renderParams, mySetting);
	publish.init();
});

/**********************************************************************************************************************/