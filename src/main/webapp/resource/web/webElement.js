var templateParams = {
		tableTheads:["元素名称", "元素类型", "定位类型", "定位值", "取值序号", "创建时间", "创建用户", "备注", "更新日志","操作"],
		btnTools:[{
			type:"primary",
			size:"M",
			id:"add-object",
			iconFont:"&#xe600;",
			name:"添加该页面上的元素"
		},{
			type:"danger",
			size:"M",
			id:"batch-op",
			iconFont:"&#xe6bf;",
			name:"批量操作"
		}],
		formControls:[{
					edit:true,
					label:"ID",  	
					objText:"elementIdText",
					input:[{	
						hidden:true,
						name:"elementId"
						}]
					},
					{
						required:true,
						label:"元素名称",  
						input:[{	
							name:"elementName"
							}]					
					},{
						required:true,
						label:"元素类型",  
						select:[{	
							name:"elementType",
							option:[{
								value:"frame",
								text:"frame/iframe/frameset"
								},
								{
									value:"tag",
									text:"页面元素"
								},
								{
									value:"url",
									text:"url地址"
								}]
							}]					
					},
					{
						required:true,
						label:"定位类型",  
						select:[{	
							name:"byType",
							option:[
								{
								value:"ClassName",
								text:"ClassName"
								},
								{
									value:"Id",
									text:"Id"
								},
								{
									value:"LinkText",
									text:"LinkText"
								},
								{
									value:"Name",
									text:"Name"
								},
								{
									value:"TagName",
									text:"TagName"
								},
								{
									value:"XPath",
									text:"XPath"
								},
								{
									value:"PartialLinkText",
									text:"PartialLinkText"
								},
								{
									value:"CssSelector",
									text:"CssSelector"
								},
								{
									value:"",
									text:"url"
								}]
							}]					
					},
					{
						label:"定位值",  
						input:[{	
							name:"byValue"
						}]					
					},				
					{
						label:"取值序号",  
						input:[{	
							name:"seq",
							placeholder:"不需要请填0或者不填"
						}]					
					},
					 {
						 name:"createTime",
						 value:new Date().Format("yyyy-MM-dd hh:mm:ss")
						
					 },
					 {
						 name:"createUser.userId"											
					 },
					 {
						 name:"parentId"
					 },
					 {
						 name:"modifyLog"											
					 },
					 {
						label:"备注",  			
						textarea:[{
							name:"mark"
						}]
					},
				
]};

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
        		  			"frame":{
        		  				btnStyle:"warning",
        		  				status:"FRAME"
        		  				},
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
				   	"data":"byType",
					"render":function(data) {
						if (!strIsNotEmpty(data)) {
							return "";
						}
						var option = {
      		  			"default":{
      		  				btnStyle:"primary",
      		  				status:data
      		  				}
						};	
						return labelCreate(data, option);
			  }},
			  ellipsisData("byValue"),
			  {"data":"seq"},
			  ellipsisData("createTime"),
			  ellipsisData("createUser.realName"),
			  longTextData("mark", "elementName", "备注信息"),
			  longTextData("modifyLogText", "elementName", "更新日志(最新10条)"),			 
			  {
				  	"data":null,
                    "render":function(data, type, full, meta){                    		                        	
                    	var context = [{
	                			title:"编辑",
	                    		markClass:"object-edit",
	                    		iconFont:"&#xe6df;"
                			},{
	                			title:"删除",
	                    		markClass:"object-del",
	                    		iconFont:"&#xe6e2;"
                			}];                     	                   	                 		                       	                        	
                    	return btnIconTemplate(context);
           }
}];

var eventList = {
		"#add-object":function(){
			publish.renderParams.editPage.modeFlag = 0;					
			layer_show("添加页面元素", editHtml, editPageWidth, editPageHeight.add, 1, function() {
				$("#parentId").val(parentElementId);
			});
			publish.init();			
		},
		"#batch-op":function(){
			layer.confirm(
				'请选择你需要进行的批量操作:',
				{
					title:'批量操作',
					btn:['复制','移动','删除'],
					shadeClose:true,
					btn3:function(index){
						layer.close(index);
						batchOp($(".selectElement:checked"), top.WEB_ELEMENT_DEL_URL, "删除", null, "elementId", {parentId:parentElementId});
					}
				},function(index){ 
					layer.close(index);
					$.post(top.WEB_ELEMENT_LIST_ALL_URL, {copyOrMoveFlag:"true"}, function(json) {
						if (json.returnCode == 0) {
							showSelectBox (json.data, "elementId", "elementPath", function(id, obj, index){
								batchOp($(".selectElement:checked"), top.WEB_ELEMENT_COPY_URL, "复制", null, "elementId", {parentId:id});
								layer.close(index);
							}, "请选择目标页面:")							
						} else {
							layer.alert(json.msg, {icon:5});
						}
					});		
				},function(index){
					layer.close(index);
					$.post(top.WEB_ELEMENT_LIST_ALL_URL, {copyOrMoveFlag:"true"}, function(json) {
						if (json.returnCode == 0) {
							showSelectBox (json.data, "elementId", "elementPath", function(id, obj, index){
								batchOp($(".selectElement:checked"), top.WEB_ELEMENT_MOVE_URL, "移动", null, "elementId", {parentId:id});
								layer.close(index);
							}, "请选择目标页面:")							
						} else {
							layer.alert(json.msg, {icon:5});
						}
					});						
				});
		},		
		".object-edit":function(){
			var data = table.row( $(this).parents('tr') ).data();
			publish.renderParams.editPage.modeFlag = 1;	
  			publish.renderParams.editPage.objId = data.elementId;
  			publish.renderParams.editPage.currentObj = data;
			layer_show("编辑元素对象信息", editHtml, editPageWidth, editPageHeight.edit, 1);
			publish.init();	
		},
		".object-del":function(){
			var data = table.row( $(this).parents('tr') ).data();			
			opObj("确认要删除此mock接口吗？", top.WEB_ELEMENT_DEL_URL, {id:data.elementId}, this, "删除成功!");
		}	
};

var parentElementId;
var parentElement;
var mySetting = {
		eventList:eventList,
		templateCallBack:function(df){
			parentElementId = GetQueryString("elementId");				
			publish.renderParams.listPage.listUrl = top.WEB_ELEMENT_LIST_URL + "?elementId=" + parentElementId;	
			
			$.post(top.WEB_ELEMENT_GET_URL, {id:parentElementId}, function(json) {
				if (json.returnCode == 0) {
					parentElement = json.object;
					$('.breadcrumb > i:eq(0)').after(" " + parentElement.elementPath);
				}
			});
			
			df.resolve();			   		 	
   	 	},
		listPage:{
			listUrl:top.WEB_ELEMENT_LIST_URL,
			tableObj:".table-sort",
			columnsSetting:columnsSetting,
			columnsJson:[0, 10, 11]			
		},
		editPage:{
			editUrl:top.WEB_ELEMENT_EDIT_URL,
			getUrl:top.WEB_ELEMENT_GET_URL,
			rules:{
				elementName:{
					required:true,
					minlength:2,
					maxlength:255
				},
				seq:{
					digits:true
				},
				byValue:{
					required:true,
					maxlength:1000
				}
			}
		},
		templateParams:templateParams	
};

$(function(){
	publish.renderParams = $.extend(true,publish.renderParams,mySetting);
	publish.init();
});
