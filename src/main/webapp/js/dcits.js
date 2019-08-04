/* ----------接口自动化测试平台-------------
* dcits.js V1.0
* Created & Modified by xuwangcheng
* Date modified 2017-01.22
*
*/


/****单页面的DT表格对象****/
var table;
/****当前正在操作的layer窗口********/
var currIndex;
/********添加页面和编辑页面的自动宽高***********/
var editPageWidth = 800;
var editPageHeight;//自动根据页面的form控件来计算页面高度
var maxHeight = $(document).height();
var maxWidth = $(document).width();
/***********所有模板**************/
var templates = top.templates;
/*************公用模板***************/
var tableTheadTemplate;
var btnTextTemplate;
var btnIconTemplate;
var formControlTemplate;

if (templates != null) {
	tableTheadTemplate = templates["table-thead-template"];
	btnTextTemplate = templates["btn-text-template"];
	btnIconTemplate = templates["btn-icon-template"];
	formControlTemplate = templates["form-control-template"];
}
/********所有子页面代码**************/
var htmls = top.htmls;

var editHtml = "";//编辑页面代码,防止重复渲染
var advancedQueryFormHtml = "";//高级查询页面代码，防止重复渲染

/**************说明文档*****************/
/**
 * 写法：<a mark-name="parameterizedFilePath" class="btn btn-default radius explanation-mark"><i class="Hui-iconfont">&#xe6cd;</i></a>
 */
/**
 * {
 * 	"title":"测试步骤配置", //文档标题
	"html":true,  //是否为模板
	"parameters":{}  //如果为模板,则会使用这里的参数渲染模板
	"content":"explanation-mark-webstep-config", //如果为模板，此为模板ID，否则为文档的html内容
	"width":600, //初始宽度  默认400
	"height":600, //初始高度 默认350
	"style":"default" //说明文档的面板样式，默认为default,可选primary、secondary、success、warning、danger
 * 
 * }
 * 
 */
var marks = top.explanationMarks;
/********************************************/
$(function() {
	//加载对应的js文件		
	var r = (window.location.pathname.split("."))[0].split("/");
	r = r[r.length-1] + ".js";
	dynamicLoadScript(r);
});

/**
 * 初始化方式
 * 通过以下方式来设置相关参数 
 * publish.renderParams = $.extend(true,publish.renderParams,mySetting);
 * 通过publish.init();来进行初始化
 */
var publish = {
     //模块初始化入口
     init : function(){
    	 var that = this;
    	//模板渲染只有一次
		 var df1 = $.Deferred();
		 df1.done(function(){
			 var df = $.Deferred();
	    	 df.done(function(){
	    		 that.renderData(that.renderParams.customCallBack);
	    		 //防止事件被绑定多次
	    		 (that.renderParams.ifFirst == true) && (that.initListeners(that.renderParams.eventList));
	    	 }); 
	    	 if (that.renderParams.renderType == "list") {
	    		 that.renderParams.listPage.beforeInit(df);
	    	 } else if (that.renderParams.renderType == "edit") {
	    		 that.renderParams.editPage.beforeInit(df);
	    	 }	    	 	   
		 });
		 if (that.renderParams.ifFirst == true) {
			 that.renderTemplate(df1,that.renderParams.templateCallBack); 
		 } else {
			 df1.resolve();
		 }	
     },
     /**
      * 渲染所需参数
      * 不可选：
      * ifFirst: true 当前是否为本页面第一次初始化  防止事件被重复绑定 默认为true
      * 可选:
      * customCallBack:自定义数据渲染 不是list  edit页面的自定义数据渲染方法 或者在DT初始化完毕之后进行二次渲染  参数p = renderParams
      * templateCallBack:自定义模板渲染 默认模板渲染之后的二次渲染
      * eventList:页面event绑定 默认{}
      * renderType: 当前渲染模式 listPage还是editPage 默认为list 可选edit
      * userDefaultRender:是否使用默认的数据渲染  默认为true  可选 false为不使用,仅使用使用提供customCallBack回调方法中的渲染
      * userDefaultTemplate:是否使用默认的模板渲染  默认为true 可选false为不使用,仅使用templateCallBack回调中的默认渲染
      * 
      * 
      * list页面
      * 必须：
      * listUrl: Datatables请求数据地址 必要
      * tableObj:对应的DT容器jquery对象 必要
      * columnsSetting:列的设置
      * 
      * 可选:
      * beforeInit:执行list页面的init方法之前执行的方法  请在该方法最后调用df.resolve();   
      * columnsJson:不参与排序的列
      * dtOtherSetting:DT其他的自定义设置
      * dtAjaxCallback:DataTables在每次通过ajax.reload返回之后的回调
      * exportExcel:是否在工具栏添加  导出到excel 的按钮工具  默认为true
      * dblclickEdit:开启双击行打开编辑页面  默认为true
      * 
      * edit页面
      * 必须：
      * editUrl:编辑请求地址 必要
      * ifUseValidate:是否使用jqueryValidate插件  默认true  可选false  如果不使用,需要自己定义提交事件
      * 可选：
      * beforeInit:执行edit页面的init方法之前执行  请在该方法最后调用df.resolve();
      * formObj:对应的form容器 默认为 ".form-horizontal" 可选
      * modeFlag:指定时add还是edit操作  默认为0-add  可选 1-edit
      * objId: get实体时需要的id
      * getUrl:获取实体对象时的请求地址
      * rules:rules规则 定义验证规则
      * messages 提示 定义验证时展示的提示信息
      * beforeSubmitCallback 提交表单数据之前的回调，带一个参数modeFlag表示当前是新增(0)还是编辑数据(1)
      * closeFlag:成功提交并返回之后是否关闭当前编辑窗口  默认为true  可选false
      * ajaxCallbackFun: ajax提交中的回调函数  如传入null,则使用默认
      * renderCallback:function(obj){}  如果默认的渲染结果不是完整或者正确的,可以传入该回调重新或者附加渲染  obj=通过get方法获取的对应实体对象
      * currentObj:null  当前正在编辑的实体对象
      * 
      * 
      * templateParams参数：
      * tableTheads:必须的  传入字符串数组,渲染成指定的表头  
      * btnTools:表格上方的工具栏 带文字和图标样式按钮
	  * formControls: edit页面的表单控件渲染,参见下面的数据格式  
	  *		   [{
				edit:flase,
				required:false,
				label:"",   	//如果没有label 该控件为隐藏input 同时需要填写下面的name 可选value
				name:"",
				objText:"",
				input:[{	
					flag:flase,
					hidden:false,
					value:"",
					placeholder:"",
					name:""	
					}],
				textarea:[{
					flag:false,
					placeholder:"",
					value:"",
					name:""	
					}],
				select:[{
					flag:false,
					name:""	,
					option:[{
						value:"",
						text:"",
						selected:""  //可选"selected"
						}]
					}],
				button:[{
					flag:false,
					style:"",
					markClass:"",
					value:""		
					}]	
			}]
      * 
      */
     renderParams:{
    	 renderType:"list",
    	 userDefaultRender:true,    
    	 userDefaultTemplate:true,
    	 customCallBack:function(p) {},
    	 templateCallBack:function(df) {
    		 df.resolve();
    	 },
    	 eventList:{
    		 "table>tbody>tr":{
    			 "dblclick":function () {
    				 if (strIsNotEmpty(editHtml) && $(this).find('.object-edit') && publish.renderParams.listPage.dblclickEdit) {
    					 //触发编辑事件
    					 $(this).find('.object-edit').click();   					 
    				 }
    			 }
    		 },
    		 ".explanation-mark":function() {
    			 var name = $(this).attr("mark-name");
    			 if (marks == null || marks[name] == null) {
    				 return;
    			 }
    			 if (marks[name]['html']) {
    				 layer_show('说明', templates[marks[name]['content']](marks[name]['parameters'] || {}), marks[name]['width'] || 460, marks[name]['height'] || 350, 1, null, null, null, {shade:0, maxmin:true});
    			 } else {
    				 layer_show('说明', templates['explanation-mark-panel'](marks[name]), marks[name]['width'] || 460, marks[name]['height'] || 350, 1, null, null, null, {shade:0, maxmin:true}); 
    			 }   			 
    		 }
    	 },
    	 ifFirst: true,    	 
    	 listPage:{
    		 listUrl:"",
    		 beforeInit:function(df) {
        		 df.resolve();
        	 },
        	 dtAjaxCallback:function() {},      	 
        	 tableObj:null,
        	 columnsSetting:{},
        	 columnsJson:[],
        	 dtOtherSetting:{},
        	 dt:null,
        	 exportExcel:true,
        	 dblclickEdit:true
    	 },
    	 editPage:{
    		 beforeInit:function(df) {
        		 df.resolve();
        	 },
        	 ifUseValidate:true,
    		 modeFlag:0,
    		 objId:null,
    		 editUrl:"",
    		 saveUrl:"",
        	 formObj:".form-horizontal",
        	 getUrl:"",
        	 rules:{},
        	 messages:{},
        	 beforeSubmitCallback:function(modeFlag) {},
        	 closeFlag:true,
        	 ajaxCallbackFun:null,
        	 renderCallback:function(obj) {},
        	 currentObj:null
    	 },  
    	 templateParams:{
    		 tableTheads:[],
    		 btnTools:[],
    		 formControls:[],
    		 advancedQuery:{  //高级查询
        		 enable:false,  //是否开启,默认为false不开启
        		 //如果开启需要补充下面的相关参数
        		 pageOpenSuccessCallback:function(layero, index){},
        		 formTitle:"高级查询", //查询表单的标题
        		 formControls:[] //查询表单空间列表,三种类型input select datetime, 每行2个   		 
        	 }
    	 }
     },
     /**
      * 在进行数据渲染之前先进行静态的模板渲染
      * @param callback
      */
     renderTemplate:function (df, callback) {    	 
    	 if(this.renderParams.userDefaultTemplate) {
    		 //$(".page-container").spinModal();
    		 var t = this.renderParams.templateParams;
    		 editPageHeight = countEditPageHeight();
			
			if (!$.isEmptyObject(t.tableTheads)) {
				//渲染表头
		    	$("#table-thead").append(tableTheadTemplate(t.tableTheads));
			}
			
			if (t.advancedQuery.enable) {
				//渲染高级查询表单控件
				advancedQueryFormHtml = templates["advanced-query-form"](t.advancedQuery.formControls);
				
				//添加工具栏高级查询按钮
				t.btnTools.push({
					type:"primary",
					size:"M",
					id:"advanced-query",
					iconFont:"&#xe665;",
					name:"高级查询"
				});
				
				
				//定义临时保存的查询条件
				window.advancedQueryParameters = null;
				
				//绑定相关的事件
				
				//打开高级查询页面
				publish.renderParams.eventList["#advanced-query"] = function () {
					currIndex = layer_show(t.advancedQuery.formTitle, advancedQueryFormHtml, 600, (150 + 70 * t.advancedQuery.formControls.length), 1
							, function(layero, index) {
								if (advancedQueryParameters == null) {// 获取列表
									advancedQueryParameters = {};
									$.each(layero.find("select,input"), function (i, obj) {
										advancedQueryParameters[$(obj).attr("name")] = "";
									});
								}
								$.each(advancedQueryParameters, function(name, value) {
									layero.find("#" + name).val(value);
									if (layero.find("#" + name).attr("query-datetime") == "true") {
										//渲染日期控件
										laydate.render({
										    elem: "#" + name // 指定元素
										    ,value: value
										    ,range: '~'
										  });
									}
								});	
								publish.renderParams.templateParams.advancedQuery.pageOpenSuccessCallback(layero, index);
							}, function(index, layero) {
								saveFormValue(advancedQueryParameters);
							});
				};				
				//提交高级查询按钮
				publish.renderParams.eventList["#submit-advanced-query"] = function () {
					if (!customFormControlValidate("#advanced-query-form")) {
						return false;
					};
					saveFormValue(advancedQueryParameters);
					layer.close(currIndex);
					refreshTable(publish.renderParams.listPage.listUrl + "?queryMode=advanced&" + $("#advanced-query-form").serialize());
				};
				//查询全部按钮
				publish.renderParams.eventList["#submit-advanced-query-reset"] = function () {
					advancedQueryParameters = null
					layer.close(currIndex);
					refreshTable(publish.renderParams.listPage.listUrl);
				};											
			};
			
			if (!$.isEmptyObject(t.btnTools)) {
				//渲染表格上方工具栏
		    	 $("#btn-tools").append(btnTextTemplate(t.btnTools));
			}
			
			if (!$.isEmptyObject(t.formControls)) {  			
       	    	 //渲染editPage的表单控件
       	    	 editHtml = formControlTemplate(t.formControls);
			}						
    	 }
    	//传入回调进行二次的渲染如果对editPage做过自定义的渲染  那么必须重新定义全局变量 editHtml	    	
    	 callback(df);
     },
     /**
      * 内部所用的函数-渲染数据 不同的页面的渲染模式  通用为list(列表页) edit(编辑增加页) 其他类型自己扩展
      * @param callback  
      */
     renderData : function (callback) { 
    	 var p = this.renderParams;
    	 //默认渲染
    	 if (p.userDefaultRender == true) {
    		 if (p.renderType == "list") {
    			 var l = p.listPage;
    			 table = initDT(l.tableObj, l.listUrl, l.columnsSetting, l.columnsJson, l.dtOtherSetting); 
    			 
    			 /***添加导入excel插件按钮**/
    			 if (l.exportExcel) {
    				 new $.fn.dataTable.Buttons( table, {
    	    			    buttons: [
    	    			              { extend: 'excelHtml5', className: 'btn btn-primary radius', text:'导出到Excel', title:(new Date()).Format("yyyy-MM-dd-hhmmssS")}
    	    			    ]
    	    			} );  
    	    		 table.buttons().container().appendTo( $('.cl:eq(0)', table.table().container()) ); 
    			 }    			 
    		 }       		 
    		 if (p.renderType == "edit") {
    			 var e = p.editPage;
    			 var sUrl = e.editUrl;
    			 if (e.modeFlag == 1) {
    				 ObjectEditPage(e.objId, e.getUrl, e.renderCallback);
    			 } else {
    				 (e.saveUrl != null && e.saveUrl != "") && (sUrl = e.saveUrl);
    			 }    			 
    			 e.ifUseValidate && formValidate(e.formObj, e.rules, e.messages, null, e.closeFlag, e.ajaxCallbackFun, e.beforeSubmitCallback);
    		 }    		 
    	 } 
    	callback(p); 
     },
     /**
      * 统一绑定监听事件
      * @param eventList  事件列表 jsonObject
      * 没有传入{}
      * 格式: 
      * '.btn' : function(){}  点击事件
      * '.checkbox' : {'change' : function(){}}  其他事件  请参照jquery的event
      */
     initListeners : function (eventList) {
         $(document.body).delegates(eventList);
         this.renderParams.ifFirst = false;
         this.renderParams.renderType = "edit";
     }
};

//设置jQuery Ajax全局的参数  
$.ajaxSetup({
    error: function (jqXHR, textStatus, errorThrown) {  
    	layer.closeAll('dialog');
    	$(".page-container").spinModal(false);
        switch (jqXHR.status) {  
            case(500):  
                layer.alert("服务器系统内部错误", {icon:5});  
                break;  
            case(401):  
            	layer.alert("未登录或者身份认证过期", {icon:5});  
                break;  
            case(403):  
            	layer.alert("你的权限不够", {icon:5});  
                break;  
            case(408):  
            	layer.alert("AJAX请求超时",{icon:5});
                break;  
            default:  
            	layer.alert("AJAX调用失败", {icon:5});
        }        
    }
});

//DT的配置常量
var CONSTANT = {
		DATA_TABLES : {	
			DEFAULT_OPTION:{
			"aaSorting": [[ 1, "desc" ]],//默认第几个排序
            "bStateSave": true,//状态保存
            "processing": false,   //显示处理状态
    		"serverSide": true,  //服务器处理
        	"autoWidth": false,   //自动宽度
        	"scrollX": false,//水平滚动条
            "responsive": false,   //自动响应
            "searchDelay": 500, //搜索间隔,默认为400ms
            "language": {
                "url": "../../js/json/zh_CN.txt"
            },
            "lengthMenu": [[5, 10, 50, 100, -1], ['5', '10', '50','100', '所有']],  //显示数量设置
            "pageLength": 10,
            //行回调
            "createdRow": function ( row, data, index ){
                $(row).addClass('text-c');
            }},
            //常用的COLUMN
			COLUMNFUN:{
				//过长内容省略号替代
				ELLIPSIS:function (data, type, row, meta) {
                	data = data||"";
                	return '<span title="' + data + '">' + data + '</span>';
                }
			}
		}
};


/**
 * 自定义扩展的jquery方法
 * 已配置的方式代理事件
 */
$.fn.delegates = function(configs) {
     el = $(this[0]);
     for (var name in configs) {
          var value = configs[name];
          if (typeof value == 'function') {
               var obj = {};
               obj.click = value;
               value = obj;
          };
          for (var type in value) {
               el.delegate(name, type, value[type]);
          }
     }
     return this;
};


/**
 * 
 * @param tableObj 对应表格dom的jquery对象
 * @param ajaxUrl  ajax请求数据的地址  string
 * @param columnsSetting columns设置  jsonArray
 * @param columnsJson  不参与排序的列 jsonArray
 * @returns table 返回对应的DataTable实例
 */
function initDT (tableObj, ajaxUrl, columnsSetting, columnsJson, dtOtherSetting) {
	var data = [];
	var table = $(tableObj)
	/*//发送ajax请求时
	.on('perXhr.dt', function() {
		
	})*/
	//ajax完成时
	.on('xhr.dt', function ( e, settings, json, xhr ) {
        if(json.returnCode != 0){  
        	$(".page-container").spinModal(false);
        	if (json.returnCode == 7) {
        		top.layer.alert('会话过期，请重新登录', {icon:5, title:'警告'}, function () {
                    sessionStorage.clear();
                    top.window.location = 'login.html';
                });
        		return false;
        	}        	
        	layer.alert(json.msg, {icon:5});       	
        	return false;
        }
        data = json.data;
        
    })
   /* //重绘完毕
    .on('draw.dt', function () { //初始化和刷新都会触发
    	publish.renderParams.listPage.dtAjaxCallback();
    })*/
    //初始化完毕
    .on( 'init.dt', function () {  //刷新表格不会触发此事件  只存在一次
    	//添加动态拖拽改变列宽的插件
    	$('.table').colResizable({
    		partialRefresh:true,
    		minWidth:35,
    		liveDrag:true,
    		disabledColumns:[0]
    	});
    	$(".page-container").spinModal(false); 	    	
    })
	.DataTable($.extend(true, {}, CONSTANT.DATA_TABLES.DEFAULT_OPTION, {
		"ajax":ajaxUrl,
        "columns":columnsSetting,                                           
        "columnDefs": [{"orderable":false, "aTargets":columnsJson}]
        }, dtOtherSetting));
	
	return table;
}


/**
 * 返回DT中checkbox的html
 * @param name  name属性,对象的name或者其他
 * @param val   value属性,对象的id
 * @param className  class属性,一般为select+对象名称
 */
function checkboxHmtl(name,val,className) {
	return '<input type="checkbox" name="'+name+'" value="'+val+'" class="'+className+'">';
}

/**
 * 操作指定对象
 * @param tip
 * @param url
 * @param sendData
 * @param obj
 * @param successTip
 */
function opObj (tip, url, sendData, obj, successTip, successCallback) {
	layer.confirm(tip, {icon:0, title:'提示'}, function(index) {
		$(".table").spinModal();
		
		$.post(url, sendData, function(data) {
    		if (data.returnCode == 0) {
    			$(".table").spinModal(false);
    			if (obj != null) {
    				table.row($(obj).parents('tr')).remove().draw();
    			}    
    			typeof successCallback == 'function' && successCallback(data);
                layer.msg(successTip,{icon:1,time:1500});
    		} else {
    			$(".table").spinModal(false);
    			layer.alert(data.msg, {icon: 5});
    		}
    	});
	});
}


/**
 * 单个删除功能，通用
 * tip 确认提示
 * url 删除请求地址
 * id 删除的实体ID
 * obj 表格删除对应的内容
 */
function delObj(tip, url, id, obj) {
	opObj(tip, url, {id:id}, obj, '已删除!');
}

/**
 * 按照设定显示浮动选择条
 * @param data 展示的数据 数组
 * @param idName option的value取值字段名
 * @param titleNames option的text取值字段名 传入数组的话将会用'-'号连接起多个字段值
 * @param chooseCallback 选择之后的回调，带三个参数：id - id值，obj - 选择的对应实体 ， index - 打开的layer窗体index
 */
function showSelectBox (data, idName, titleNames, chooseCallback, layerTitle, noDataTip) {
	if (data.length < 1) {
		layer.msg(noDataTip || '无可用数据!', {icon:0, time:2000});
		return;
	}
	var show_html = '<div class="row cl" style="width:340px;margin:15px;">'		 
		+ '<div class="formControls col-xs-10"><span class="select-box radius mt-0">'
		+ '<select class="select" size="1" name="select-object" id="select-object">';	
	if (!(titleNames instanceof Array)) {
		titleNames = [titleNames];
	}
	var count = titleNames.length;
	$.each(data, function(i, n) {
		show_html += '<option value="' + n[idName] + '" data-id="' + i + '">';
		$.each(titleNames, function(i1, n1) {
			show_html += n[n1];
			if (count > i1 + 1) {
				show_html += "-";
			}
		});		
		show_html += '</option>';
	});
	show_html += '</select></span></div><div class="form-label col-xs-2">'
		+ '<input type="button" class="btn btn-primary radius" onclick="" id="show-select-box-choose" value="选择"/></div></div>';
	
	var index = layer_show(layerTitle || "请选择", show_html, '400', '120', 1, function() {
		$(document).delegate("#show-select-box-choose", 'click', function() {
			var data_id = $('#select-object option:selected').attr('data-id');
			chooseCallback($('#select-object').val(), data[data_id], index);
		});
	})
}

/**
 * 批量方法-表格为DT时
 * @param checkboxList  checkBox被选中的列表
 * @param url   url 远程接口
 * @param tableObj   TD对象，默认名为table
 * @param opName 操作方式名称 默认为 删除
 * @param idName 对应实体的ID名称
 * @param otherSendData 其他要随着ID发送的参数
 * @param callback 操作全部完成之后，刷新表格之前的回调操作
 * @returns {Boolean}
 */
function batchOp (checkboxList, url, opName, tableObj, idName, otherSendData, finishCallback, customSendFun) {
	if (checkboxList.length < 1) {
		return false;
	}
	
	if (opName == null) {
		opName = "删除";
	}
	layer.confirm('确认' + opName + '选中的' + checkboxList.length + '条记录?', {icon:0, title:'警告'}, function(index) {
		layer.close(index);
		var loadindex = layer.msg('正在进行批量' + opName + "...", {icon:16, time:60000, shade:0.35});
		var delCount = 0;
		var totalCount = 0;
		var errorTip = "";
		$.each(checkboxList ,function(i, n) {			
			var objId = $(n).val();//获取id
			var objName = $(n).attr("name");	//name属性为对象的名称	
			var params = {id: objId};
			if (strIsNotEmpty(idName) && idName != "id") {
				params[idName] = objId;
			}
			if (otherSendData != null && otherSendData instanceof Object) {
/*				$.each(otherSendData, function(i, n) {
					params[i] = n;
				});*/
				$.extend(true, params, otherSendData);
			}
			
			if (typeof customSendFun == 'function') {
				customSendFun(n, params);
			}
			
			//layer.msg("正在" + opName + objName + "...", {time: 999999});    
				$.ajax({
					type:"post",
					url:url,
					data:params,
					//async:false,
					success:function(data) {
						totalCount++;
						if(data.returnCode != 0) {	
							errorTip += "[" + objName + "]<br>";
						}else{
							delCount++;
						}
					}
					});			
		});
		
		var intervalID = setInterval(function() {
			if (totalCount == checkboxList.length) {
				clearInterval(intervalID);
				if (typeof finishCallback == 'function') {
					finishCallback();
				}
				refreshTable(null, function(json) {
					layer.close(loadindex);
					if (errorTip != "") {
						errorTip = "在" + opName + "<br>" + errorTip + "时发生了错误<br>请执行单笔" + opName + "操作!";
						layer.alert(errorTip, {icon:5}, function(index) {
							layer.close(index);							
							layer.msg("共" + opName + delCount + "条数据!", {icon:1, time:2000});
						});
					} else {
						layer.msg("共" + opName + delCount + "条数据!", {icon:1, time:2000});
					}
				});	
			}
		}, 500);				
	});
}

/**
 * 批量删除
 * @param checkboxList
 * @param url
 * @param tableObj
 * @param opName
 */
function batchDelObjs(checkboxList, url, tableObj, opName, callback) {
	batchOp(checkboxList, url, opName, tableObj, null, null, callback);	
}

/**
 * 编辑页面 当modeflag为1(编辑)时 默认的页面渲染
 * @param id  指定实体的id
 * @param ajaxUrl  ajax地址
 * @param callback(function(obj){}) 如果默认的渲染结果不是完整或者正确的,可以传入该回调重新渲染  obj 实体对象
 */
function ObjectEditPage(id, ajaxUrl, callback) {
	$(".form-horizontal").spinModal();
	//编辑模式时将某些隐藏的控件展示
	$(".editFlag").css("display","block");
	$.post(ajaxUrl, {id:id}, function(data) {
		if (data.returnCode == 0) {
			var o = data.object;
			//默认的渲染  object中同名id 控件 以及id名为 idText
			iterObj(o);		
			//该回调可以自行渲染默认没有渲染完整的控件
			callback(o);	
			$(".form-horizontal").spinModal(false);
		} else {
			layer.alert(data.msg, {icon:5});
		}
	});
	
	
}

/**
 * 迭代循环遍历json对象中属性并设置为页面中对应ID的form控件的value
 * @param jsonObj
 * @param parentName 
 */
function iterObj(jsonObj, parentName) {
	$.each(jsonObj, function(k, v) {
		if (parentName != null&&parentName != "") {
			k = parentName + "\\." + k;
		}
		if (!(v instanceof Object)) {	
			if ($("#" + k)) {
				$("#" + k).val(v);
			}
			if ($("#" + k + "Text")) {
				$("#" + k + "Text").text(v);
			}
		} else {
			iterObj(v, k);
		}
	});	
}



/**
 * 所有实体edit页面的jqueryValidate方法
 * @param formObj  表单obj
 * @param rules   rules规则 定义验证规则
 * @param messages 提示 定义验证时展示的提示信息
 * @param ajaxUrl  验证成功 ajax提交地址
 * @param closeFlag  成功提交并返回之后是否关闭当前窗口  true  false
 * @param ajaxCallbackFun  ajax提交中的回调函数  如传入null,则使用默认
 * @param beforeSubmitCallback 提交给远程接口之前的回调操作
 * @param submitHandlerCallbackFun 替换默认的提交函数
 */
function formValidate(formObj, rules, messages, ajaxUrl, closeFlag, ajaxCallbackFun, beforeSubmitCallback, submitHandlerCallbackFun) {
	var callbackFun = function(data) {
		if (data.returnCode == 0) {	
			refreshTable();
			if (closeFlag) {
				//关闭当前的所有页面层
				layer.closeAll('page');
			}					
		} else {
			layer.alert(data.msg, {icon: 5});
		}			
	};
	if (ajaxCallbackFun != null) {
		callbackFun = ajaxCallbackFun;
	}
	
	typeof formObj != 'object' && (formObj = $(formObj));
	if (ajaxUrl == null) {
		ajaxUrl = publish.renderParams.editPage.editUrl;
	}
	
	formObj.validate({
		rules:rules,
		messages:messages,
		ignore: "",
		onkeyup:false,
		focusCleanup:true,
		success:"valid",
		submitHandler:function(form) {
			if (submitHandlerCallbackFun != null) {
				submitHandlerCallbackFun($(form).serialize());
				return false;
			}
			
			if (beforeSubmitCallback != null) {
				beforeSubmitCallback(publish.renderParams.editPage.modeFlag);
			}
			var formData = $(form).serialize();
			$.post(ajaxUrl, formData, callbackFun);			
		}
	});
}

/**
 * 刷新表格
 * 所有表格页面都是的DT对象名称都要命名为table
 * @param ajaxUrl2 传入指定的ajax数据地址,刷新将会使用这个地址获取表格数据
 * @param callback 刷新完表格后的回调函数
 * @param tableObject 当前页面的DT对象
 * @param resetPaging 是否重置分页信息 false不重置  true 重置
 */
function refreshTable(ajaxUrl2, callback, tableObject, resetPaging){
	$(".table").spinModal();
	
	if (tableObject == null && table == null) {
		$(".table").spinModal(false);
		return false;
	}
	
	if (tableObject == null) {
		tableObject = table;
	}
	
	if (callback == null) {
		callback = function(o){};
	}
	
	if (resetPaging == null) {
		resetPaging = false;
	}
	
	
	if (ajaxUrl2 != null) {
		tableObject.ajax.url(ajaxUrl2).load(function(json) {
			publish.renderParams.listPage.dtAjaxCallback();
			callback(json);
			$(".table").spinModal(false);
		}, resetPaging);
	} else {
		tableObject.ajax.reload(function(json) {
			publish.renderParams.listPage.dtAjaxCallback();
			callback(json);
			$(".table").spinModal(false);
		}, resetPaging);
	}
}

/**
 * 获取json长度
 * @param jsonData
 * @returns {Number}
 */
function getJsonLength(jsonData) {
	var jsonLength = 0;
	for(var item in jsonData){
		jsonLength ++;
	}
	return jsonLength;
}

/**
 * 生成标签模板
 * 
 * @param option   
 * {
	"0":{
		btnStyle:"success",
		status:"正常"
		},
	"1":{
		btnStyle:"default",
		status:"失败"
		},
	"default":{
		btnStyle:"danger",
		status:"未知"
		}
	}
 以上是默认的配置,你也可以传入自定义的配置
 * @param data   ["1","0"] 数组或者 字符串 number都可以
 * @returns {String}
 */
function labelCreate(data, option){
	var html = '';
	var datas = [];
	if (!(data instanceof Array)) {
		datas.push(data);
	} else {
		datas = data;
	}
	
	if (option == null) {
		option = {
				"default":{
					btnStyle:"primary",
					status:datas[0]
				}};
	}
	
	if (!option["default"]) {
		option["default"] = {
				btnStyle:"default",
				status:"未知"
		};
	}
	
	$.each(datas, function(i, n) {
        if (option && option[n]) {
            html += '<span class="label label-'+option[n]["btnStyle"]+' radius">'+option[n]["status"]+'</span>';
        } else {
            if (n == "0") {
            	html += '<span class="label label-success radius">正常</span>';
            }
            if (n == "1") {
            	html += '<span class="label label-danger radius">禁用</span>';
            } 
            
            if (n != "0" && n != "1") {
            	html += '<span class="label label-' + option["default"]["btnStyle"] + ' radius">' + option["default"]["status"] + '</span>';
            }                       
        }
        if (datas.length > (i+1) ) {
        	html += "&nbsp;";
        }
    });
	return html;
}

/**
 * 
 * @param dataFieldName
 * @param titleFieldName
 * @param titleName
 * @param 如果取得不是字符串或者需要处理，则需要传入一个处理函数
 * @returns {___anonymous25435_25831}
 */
function longTextData(dataFieldName, titleFieldName, titleName, handleObjectFun) {
	return {
	    "data":dataFieldName,
	    "className":"ellipsis",
	    "render":function(data, type, full, meta) { 
	    	if (data != "" && data != null) {
	    		data = data.replace(/>/g, '&gt;').replace(/</g, '&lt;');
		    	return '<a href="javascript:;" onclick="showMark(\'' + full[titleFieldName] + '\', \'' + dataFieldName + '\', this, \'' + titleName + '\');"><span title="' + data + '">' + data + '</span></a>';
	    	}
	    	return "";
	    }
      };
}

/**
 * 获取远程地址的html文件
 */
function jqueryLoad(url, dom, callback) {
	if (typeof dom != 'object') {
		dom = $(dom);
	}
	dom.load(url, function() {
		var domHmtl = dom.html();
		dom.html('');
		callback(domHmtl);		
	});	
}

/**
 * 获取地址栏参数
 * @param name
 * @returns
 */
function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r != null) return decodeURIComponent(r[2]); return null;
}

/**
 * DataTable辅助，省略列内容
 * @param dataName
 * @returns {___anonymous19689_19800}
 */
function ellipsisData(dataName) {
	return {
  		"className":"ellipsis",
	    "data":dataName,
	    "render":CONSTANT.DATA_TABLES.COLUMNFUN.ELLIPSIS
	};
}

/**
 * 原生js动态加载脚本或者css
 * @param scriptPath 脚本css路径
 * @param type  默认为javascript 可选css
 */
function dynamicLoadScript (scriptPath, type) {
	var new_element;
	if (type == null || type == "javascript") {
		//加载对应的js文件	
		new_element = document.createElement("script");
		new_element.setAttribute("type", "text/javascript");	
		new_element.setAttribute("src", scriptPath);
		document.body.appendChild(new_element);
	} 
	
	if (type == "css") {
		//加载对应的css文件
		new_element = document.createElement("link");
		new_element.setAttribute("rel", "stylesheet");
		new_element.setAttribute("type", "text/css");
		new_element.setAttribute("href", scriptPath);
		document.head.appendChild(new_element);
	}
	
}


/**
 * layer弹出层
 * 参数解释
 * @param title 标题
 * @param url  url地址或者html页面
 * @param w 宽度,默认值
 * @param h 高度,默认值
 * @param type 类型 1-页面层 2-frame层
 * @param success 成功打开之后的回调函数
 * @param cancel 右上角关闭层的回调函数
 * @param end 层销毁之后的回调
 * @param other 其他DT设置
 * @returns index
 */
function layer_show (title, url, w, h, type, success, cancel, end, other) {
	
	if (other == null) {
		other = {};
	}
	
	if (title == null || title == '') {
		title = false;
	};
	if (url == null || url == '') {
		url="/404.html";
	};
	if (w == null || w == '' || w >= maxWidth) {
		w =	maxWidth * 0.8
	};
	if (h == null || h == '' || h >= maxHeight) {
		h= (maxHeight * 0.86) ;
	};
	if (type == null || type == '') {
		type = 2;
	}
	index = layer.open($.extend(true, {
		type: type,
		area: [w + 'px', h + 'px'],
		fix: false, //不固定
		maxmin: false,
		shade:0.4,
		anim:5,
		shadeClose:true,
		title: title,
		content: url,
		offset:'30px',
		success:function(layero, index){
			$(layero).find('#layerIndex').val(index);
			success && success(layero, index);
		},
		cancel:cancel,
		end:end
	} ,other));
	return index;
}


/**
 * 显示备注或者其他
 * @param itemName 窗口标题1
 * @param markName
 * @param obj
 * @param tipName 窗口标题2
 */
function showMark(itemName, markName, obj, tipName) {
	var data = table.row( $(obj).parents('tr') ).data();
	if (tipName == null) {
		tipName = "备注";
	}
	
	createViewWindow(data[markName], {
		title:itemName + '-' + tipName,
		copyBtn:true		
	})
}


/**
 * 关联验证
 * 取值顺序页面按键  减一
 */
function reduceSeq() {
	var seq=$("#objectSeqText").text();
	if(seq==1){
		return;
	}
	$("#objectSeqText").text(seq-1);
	$("#ORDER").val(seq-1);
}

/**
 * 关联验证
 * 取值顺序页面按键  加一
 */
function addSeq() {
	var seq=$("#objectSeqText").text();
	$("#objectSeqText").text(parseInt(seq)+1);
	$("#ORDER").val(parseInt(seq)+1);
}

//对Date的扩展，将 Date 转化为指定格式的String   
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，   
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)   
//例子：   
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423   
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18  
//author: meizz
Date.prototype.Format = function(fmt)   
{    
	var o = {   
	 "M+" : this.getMonth()+1,                 //月份   
	 "d+" : this.getDate(),                    //日   
	 "h+" : this.getHours(),                   //小时   
	 "m+" : this.getMinutes(),                 //分   
	 "s+" : this.getSeconds(),                 //秒   
	 "q+" : Math.floor((this.getMonth()+3)/3), //季度   
	 "S"  : this.getMilliseconds()             //毫秒   
	};   
	if(/(y+)/.test(fmt))   
	 fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	for(var k in o)   
	 if(new RegExp("("+ k +")").test(fmt))   
	fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	return fmt;   
} 


String.prototype.replaceAll = function(s1,s2){
	　　return this.replace(new RegExp(s1,"gm"),s2);
}

/**
 * 创建查看内容的窗口
 */
function createViewWindow (data, option) {
	var defaultOption = {
			title:"", //标题
			placeholder:"",//占位内容
			width:840,//宽度
			height:470,//高度
			refreshBtn:false,//是否显示刷新按钮
			copyBtn:false//是否显示复制按钮
	};
	
	if (option != null) {
		$.extend(true, defaultOption, option);
	}
	
	layer_show(!strIsNotEmpty(defaultOption.title) ? "查看" : defaultOption.title,
			htmls["viewWindow"], defaultOption.width, defaultOption.height, 1, function(layero, index){
		
		var renderView = function () {
			if (typeof data == "function") {
				data(layero.find("#viewWindow-data-info"));
			}
			
			if (typeof data == "string") {
				layero.find("#viewWindow-data-info").val(data);
				layero.find("#viewWindow-data-info").attr("placeholder", defaultOption.placeholder);
			}
		};
		
		renderView();
				
		if (!defaultOption.refreshBtn) {
			layero.find("#viewWindow-refresh-data").hide();
		} else {
			layero.find("#viewWindow-refresh-data").click(function () {
				renderView();
			});
		}
		
		if (defaultOption.copyBtn) {	
			layero.find("#viewWindow-copy-data").zclip({
				path: "../../libs/ZeroClipboard.swf",
				copy: function(){
					return layero.find("#viewWindow-data-info").val();
				},
				afterCopy:function(){/* 复制成功后的操作 */
					layer.msg('复制成功,CTRL+V粘贴',{icon:1,time:1500});
		        }
			});
		} else {
			layero.find("#viewWindow-copy-data").hide();
		}
	});
}

/**
 * 上传excel导入数据的说明页面
 * @param title 窗口标题
 * @param templatePath 模板文件路径
 * @param uploadUrl 上传接口
 * @param importUrl 导入接口
 */
function createImportExcelMark(title, templatePath, uploadUrl, importUrl) {
	var html = '<div style="margin:18px;" id="show-import-from-excel-content"><p>请在导入之前下载Excel模板，并在模板第二页阅读相关字段说明.</p><p><a class="btn radius btn-primary size-M" '
		+ 'href="' + templatePath + '"><i class="Hui-iconfont">&#xe640;</i> 下载Excel模板'
		+ '</a></p><p><span class="label label-warning radius">注意：</span>请务必按照模板样式进行排版填写,导入完成之后根据提示检查失败的数据的有效性。<span class="c-red">名称(一般为第一列)为空的将不会添加到数据库。</span></p>'
		+ '<br><p>请点击&nbsp;<a href="javascript:;" class="btn btn-success radius size-S " id="upload-data-excel"><i class="Hui-iconfont ">&#xe642;</i> 导入Excel</a>&nbsp;开始上传文档</p></div>';
	var showIndex = layer_show(title, html, "710", "270", 1, function() {
		//excel上传数据
		var loadIndex;
		layui.use('upload', function(){
			  var upload = layui.upload;				   
			  //执行实例
			  var uploadInst = upload.render({
			    elem: '#upload-data-excel' //绑定元素
			    ,url: uploadUrl //上传接口
			    ,accept:"file"
			    ,exts:"xlsx|xls"
			    ,size:"102400"
			    ,drag:false
			    ,before:function(obj) {
			    	loadIndex = layer.msg('正在上传文件中...', {icon:16, time:99999, shade:0.4});
			    }
			    ,done: function(res){
			      //上传完毕回调
			    	if (res.returnCode == 0) {//上传成功
			    		 layer.close(loadIndex);
			    		 loadIndex = layer.msg('正在导入数据...', {icon:16, time:99999, shade:0.4});
			    		 $.post(importUrl, {path:res.path}, function(json) {
			    				if (json.returnCode == 0) {
			    					$("#show-import-from-excel-content").html("");
			    					var showResultHtml = '<p><span class="label label-primary radius">导入总数 :</span>&nbsp;&nbsp;' + json.result.totalCount + '</p>'
			    						+ '<p><span class="label label-success radius">导入成功数 :</span>&nbsp;&nbsp;' + json.result.successCount + '</p>'
			    						+ '<p><span class="label label-danger radius">导入失败数:</span>&nbsp;&nbsp;' + json.result.failCount + '</p>'
			    						+ '<p><span class="label label-secondary radius">导入详情信息:</span><br>' + json.result.msg + '</p>';
			    					$("#show-import-from-excel-content").html(showResultHtml);
			    					layer.close(loadIndex);
			    					refreshTable();
			    				} else {
			    					layer.alert(json.msg, {icon:5});
			    				}
			    			});
			    	} else {
			    		layer.close(loadIndex);
			    		layer.alert(res.msg, {icon:5});
			    	}
			    }
			  });
			});
	}, null, null, {skin: 'layui-layer-rim', shadeClose:true});
}

/**
 * 上传文件
 * @param setting
 */
function uploadFile(setting) {
	var loadIndex;
	var defaultSetting = {
		elem:'',
		url:top.UPLOAD_FILE_URL,
		exts:'xlsx|xls',
		acceptMime:"*",
		size:'102400',
		drag:false,
		before:null,
		done:null
	};
	
	$.extend(defaultSetting, setting, true);
	
	layui.use('upload', function(){
		  var upload = layui.upload;				   
		  //执行实例
		  var uploadInst = upload.render({
		    elem: defaultSetting.elem //绑定元素
		    ,url: defaultSetting.url //上传接口
		    ,accept:"file"
		    ,acceptMime:defaultSetting.acceptMime
		    ,exts:defaultSetting.exts
		    ,size:defaultSetting.size
		    ,drag:defaultSetting.drag
		    ,before:function(obj) {
		    	if (typeof defaultSetting.before == 'function') {
		    		defaultSetting.before(obj);
		    	}
		    	loadIndex = layer.msg('正在上传文件中...', {icon:16, time:99999, shade:0.4});
		    }
		    ,done: function(res){
		    	if (res.returnCode == 0) {//上传成功
		    		 layer.close(loadIndex);
		    		 if (typeof defaultSetting.done == 'function') {
		    			 defaultSetting.done(res.path, res.relativePath);
				     }
		    	} else {
		    		layer.close(loadIndex);
		    		layer.alert(res.msg, {icon:5});
		    	}
		    }
		  });
	});
}

/**
 * 判断字符串是否为空 为null undefined 或者 只有空字符/制表符等均为空
 * @param str
 * @returns {Boolean}
 */
function strIsNotEmpty (str) {
	if (str == "null" && str == null || str == undefined || str.replace(/(^s*)|(s*$)/g, "").length == 0) {
		return false;
	}
	return true;
}

/**
 * 计算 编辑页面的弹出层高度
 */
function countEditPageHeight () {
	var returnHeight = {add:null, edit:null};
	if (!$.isEmptyObject(publish.renderParams.templateParams.formControls)) {			
		//添加页面的高度
		var addPageHeight = 140;
		//编辑页面的高度
		var editPageHeight = 140;
		$.each(publish.renderParams.templateParams.formControls, function (i, n) {
			if (strIsNotEmpty(n.label)) {
				editPageHeight = editPageHeight + (n.textarea ? 116 : 45);
				if (!(n.edit)) {
					addPageHeight = addPageHeight + (n.textarea ? 116 : 45);
				}
				
			}
		});
		if (addPageHeight >= maxHeight) {
			addPageHeight = null;
		}
		if (editPageHeight >= maxHeight) {
			editPageHeight = null;
		}
		returnHeight.add = addPageHeight;
		returnHeight.edit = editPageHeight;
	}
	return returnHeight;
}

/**
 * 将{key:value}转换成{key:"key", value:"value"}形式
 */
function parseJsonObjectMap (jsonObj) {
	if (typeof jsonObj != "object") {
		return;
	}
	var obj = [];
	$.each(jsonObj, function (key, value) {
		obj.push({key:key, value:value});
	});
	
	return obj;
}

/**
 * 将形式为"abc=123;bcd=9883"的字符串转换为json对象
 * @param s
 * @returns
 */
function parseHttpParameterToJson(s, sign) {
	sign == null && (sign = ";")
	var returnObj = {};
	var arr = s.split(sign);
	$.each(arr, function(i, n) {
		if (n != null && n != "") {
			returnObj[n.substring(0, n.indexOf("="))] = n.substring(n.indexOf("=") + 1);
		}				
	});
	return returnObj;
}

/**
 * 将单层的json对象转换为格式为"abc=123{sign}bcd=9883"的字符串
 * @param obj
 * @param sign 分隔标识
 * @returns {String}
 */
function parseHttpParameterJsonToString (obj, sign) {
	sign == null && (sign = ";");
	var s = "";
	$.each(obj, function(name, value) {
		s += name + "=" + value + sign
	});
	return s.substring(0, s.length - sign.length);
}

/**
 * 打开一个页面展示参数的节点树，并可以进行选择<br>
 * 返回的内容格式应该相同<br>
 * {
 * 	data:[{}],
 * 	error:"",
 * 	returnCode:0,
 * 	rootPid:2222
 * }
 * @param options
 */
function chooseParameterNodePath (getPath, sendDatas, chooseOption) {
	var defaltChooseOption = {
			titleName:"",
			isChoosePath:false, //是否可选择
			ifCheckbox:false, //显示checkbox
			notChooseTypes:[],//不能选择的类型
			ifChooseSelf:true,
			choosenCallback:null,//function (path, zTreeObj) {}
			nodeClickCallback:null //function(node, infoDom, zTreeObj) {}
	};
	var zTreeObj;
	if (chooseOption != null) {
		$.extend(true, defaltChooseOption, chooseOption);
	}
	var rootPid;
	var zTreeSetting = {
			data: {
				simpleData: {
					enable:true,
					idKey: "parameterId",
					pIdKey: "parentId",
					rootPId: rootPid
				},
				key: {
					name:"parameterIdentify",
					title:"parameterName"
				}		
			},
			callback: {
				onClick: function (event, treeId, treeNode) {
					$.each(treeNode, function (name, value) {	
						var that = $("#show-parameter-info td[data-name='" + name + "']");
						if (that) {
							that.html(value);
						}	
						if (name == "path") {
							that.html(value.replace('TopRoot.', '').replace('TopRoot', ''));
						}
						if (name == "type") {
							that.html('<span class="label label-success radius">' + value + '</span>');
						}
					});
					var func = defaltChooseOption.nodeClickCallback;
					typeof func === 'function' && func(treeNode, $("#show-parameter-info"), zTreeObj);
				}
			}
	};
	
	if (defaltChooseOption.ifCheckbox) {
		zTreeSetting.check = {
				enable: true,
				chkboxType:  { "Y" : "ps", "N" : "ps" },
				autoCheckTrigger: true	
		};
	}
	
	var paramsTreeLayerIndex = layer_show((strIsNotEmpty(defaltChooseOption.titleName) ? defaltChooseOption.titleName : "参数节点树"), htmls["interfaceParameter-viewTree"], null, null, 1, function() {
		if (!(defaltChooseOption.isChoosePath)) {
			$("#show-parameter-info #choose-this-path").hide();
		} else {
			$("#show-parameter-info #choose-this-path").click(function () {
				/*if (!strIsNotEmpty($("#show-parameter-info td[data-name='type']").text())) {			
					return false;
				}*/
				
				if (defaltChooseOption.notChooseTypes != null && (defaltChooseOption.notChooseTypes instanceof Array)) {
					var paramType = $("#show-parameter-info td[data-name='type'] span").text();
					var flag = true;
					$.each(defaltChooseOption.notChooseTypes, function (i, typeName) {
						if (paramType == typeName) {					
							flag = false;
							return false;
						}
					});
					
					if (!flag) {
						layer.msg('不能选择当前类型的节点,请重新选择!', {title:'提示', icon:0, time:1800});
						return false;
					}
				}
				
				//不能选择自身节点作为父节点
				if (!(defaltChooseOption.ifChooseSelf) && publish.renderParams.editPage.objId == $("#show-parameter-info td[data-name='parameterId']").text()) {
					layer.msg('不能选择自身节点为父节点,请更换路径!', {title:'提示', icon:0, time:1800});
					return false;
				}
				
				var choosePath = $("#show-parameter-info td[data-name='path']").text() + ($("#show-parameter-info td[data-name='path']").text() == "" ? "" : ".") + $("#show-parameter-info td[data-name='parameterIdentify']").text();
				
				var func = defaltChooseOption.choosenCallback;
				typeof func === 'function' && func(choosePath, zTreeObj);
				
				layer.close(paramsTreeLayerIndex);
			});
		}
		$.get(getPath, sendDatas, function(json) {
			$("#ztree-json-view").spinModal();
			if (json.returnCode == 0) {
				//初始化ztree												
				if (strIsNotEmpty(json.error)) {
					$("#errorInfo").html('<pre class="c-red">' + json.error + '</pre>');
				}
				zTreeSetting.data.simpleData.rootPId = json.rootPid;
				var nodes = json.data;
				$.each(nodes, function (i, node) {
					if (node.type == "Map" || node.type == "Array" || node.type == "List") {
						node["isParent"] = true;
						node["open"] = true;
					}
				});
				zTreeObj = $.fn.zTree.init($("#treeDemo"), zTreeSetting, nodes);
				$("#ztree-json-view").spinModal(false);
			} else {
				layer.alert(json.msg, {icon:5}, function(index) {
					layer.close(paramsTreeLayerIndex);
					layer.close(index);
				});
			}
		});
	}, null, function () {
		$("#show-parameter-info #choose-this-path").unbind('click');
	});	
	
}

/**
 * 通过layer弹出层选择多选内容<br>
 * 可替换之前的selectBox选择
 * @param options
 */
function layerMultipleChoose (options) {
	var defaultOption = {
			title:"请选择",
			layerWidth:600,
			layerHeight:360,
			simpleData:{}, //简单数据类型，对应json对象为key-value,key将会作为控件的text值，value将会作为控件的value值
			customData:{//自定义数据，Array数组对象
				enable:false,
				data:[],
				textItemName:"",//checkbox的title,可传字符串或者数组
				valueItemName:""
			},
			choosedValues:[],//已被选择的数据合集
			maxChooseCount:-1,//最大可选择数量, -1代表不限	
			minChooseCount:-1,//至少选择数量, -1代表不限
			closeLayer:true,//是否在确认之后自动关闭窗口
			confirmCallback:function (chooseValues, chooseObjects, index) {} //选择之后的回调						
	};
	
	if (options != null) {
		$.extend(true, defaultOption, options);
	}
	var qu = function (item, itemNameArr) {
		var text = "";
			$.each(itemNameArr, function(i1, n1) {
				text += "[" + item[n1] + "]&nbsp;"
			});
		return text;
	}
	
	var html = '<div class="page-container" style="padding:18px;">';
	html += '<div style="margin-bottom:12px;"><button class="btn btn-danger radius" id="confirm-choose">确认</button>&nbsp;&nbsp;<button class="btn btn-default radius" id="reset-choose">重置</button></div>';	
	html += '<div class="check-box"><input type="checkbox" id="all-choose"><label for="all-choose">全选</label></div>';
	html += '<div class="skin-minimal">';
	if (defaultOption.customData.enable) {//优先选择自定义数据
		var itemNameArr = [];
		typeof defaultOption.customData.textItemName === "string" ? itemNameArr.push(defaultOption.customData.textItemName) : itemNameArr = defaultOption.customData.textItemName;
		$.each(defaultOption.customData.data, function (i, n) {
			html += '<div class="check-box"><input type="checkbox" id="' + i + '" value="' 
				+ n[defaultOption.customData.valueItemName] + '"><label for="' + i + '">' 
				+ qu(n, itemNameArr) + '</label></div>';
		});
	} else {
		$.each(defaultOption.simpleData, function (key, value) {
			html += '<div class="check-box"><input type="checkbox" id="' + value + '" value="' 
			+ value + '"><label for="' + value + '">' 
			+ key + '</label></div>';
		});
	}
	
	html += '</div></div>';
	
	var index, that;
	var $checkbox;
	var cofirmEvent = function () {//确认事件
		var checkbox = that.find('input:checked').not("#all-choose");
		if (defaultOption.maxChooseCount != -1 && defaultOption.maxChooseCount < checkbox.length) {
			layer.msg('最多选择' + defaultOption.maxChooseCount + '项', {icon:0, time:1800});
			return false;
		}
		if (defaultOption.minChooseCount != -1 && defaultOption.minChooseCount > checkbox.length) {
			layer.msg('至少选择' + defaultOption.minChooseCount + '项', {icon:0, time:1800});
			return false;
		}
		var chooseValues = [], chooseObjects = [];
		$.each(checkbox, function (i, n) {
			chooseValues.push($(n).val());
			if (defaultOption.customData.enable) {
				chooseObjects.push(defaultOption.customData.data[$(n).attr('id')]);
			}
		});
		
		if (defaultOption.closeLayer) {
			layer.close(index);
		}
		(defaultOption.confirmCallback)(chooseValues, chooseObjects, index);
	};
	
	
	var resetEvent = function () {//重置事件
		$.each($checkbox.not("#all-choose"), function (i, n) {
			if ($.inArray($(n).val(), defaultOption.choosedValues) != -1) {
				$(n).iCheck('check');
			} else {
				$(n).iCheck('uncheck');
			}
		});
				
	};
	
	var renderEvent = function () {//打开页面的渲染
		$checkbox.iCheck({
			checkboxClass: 'icheckbox-blue',
			radioClass: 'iradio-blue',
			increaseArea: '20%'
		});//启用jquery.icheck插件
	};
	
	index = layer_show(defaultOption.title, html, defaultOption.layerWidth, defaultOption.layerHeight, 1,
			function (layero, index) {
				that = layero;
				$checkbox = that.find('input[type="checkbox"]');
				renderEvent();
				resetEvent();
				//绑定事件
				layero.find('#confirm-choose').click(cofirmEvent);
				layero.find('#reset-choose').click(resetEvent);
				layero.find('#all-choose').on("click", function(event) {
					  if(event.target.checked){
					    $checkbox.not("#all-choose").iCheck('check');
					  }else{
					    $checkbox.not("#all-choose").iCheck('uncheck');
					  }
				});
		}, function (index, layero) {
			//解绑事件
			layero.find('#confirm-choose').unbind('click');
			layero.find('#reset-choose').unbind('click');
			layero.find('#all-choose').unbind('ifChecked ifUnchecked');
		}, null, {shadeClose:false});	
}


/**
 * 从form控件保存指定的值
 */
function saveFormValue (objct) {
	$.each(objct, function(name, value) {
		objct[name] = $("#" + name).val();
	});
}

/**
 * 自定义的表单验证<br>
 * 
 */
function customFormControlValidate (formObj) {
	if (typeof formObj != "object") {
		formObj = $(formObj);
	}
	var validateObj = formObj.find("input[datatype]");
	var flag = true;
	$.each(validateObj, function (i, n) {
		var validateType = $(n).attr("datatype");
		var validateValue = $(n).val();
		
		if (!strIsNotEmpty($(n).val())) {
			return true;
		}		
		var regu;
		var tip;
		switch (validateType) {
		case "number":
			regu = /^[1-9]\d*$/;
			tip = "需要输入正整数!";
			break;
		default:
			break;
		}
		
		var reg = new RegExp(regu);
		if (!reg.test(validateValue)) {
			flag = false;
			layer.tips(tip, '#' + $(n).attr("name"), {
				  tips: [1, '#FF8442']
			});
		}
	});
	
	return flag;
}

/**
 * 打开不同echarts图表切换
 * @param option
 */
function echartsViewPage(option) {
	/*var defaultOption = {
	 *  title:'',
		btns:[],
		echartsOptions:[],
		minTime:'',
		maxTime:'',
		changeTimeCallback:function(timeRange, echartsInstances){}
	};*/

	layer_show(option.title, '<div class="page-container"><div class="echart-view-btn"><span class="l"></span><span class="r"><input style="width:330px;" class="input-text radius size-M" id="view-date-pick"></span></div><div class="echarts-view"></div></div>', null, null, 1, function(layero, index){
		var maxHeight = layero.height();
		var maxWidth = layero.width();
		var echartsInstances = [];
		
		//初始化日期选择器
		laydate.render({
			  elem: '#view-date-pick', //指定元素
			  type: 'datetime',
			  range: '~',
			  min: option.minTime,
			  max: option.maxTime,
			  value:option.minTime + " ~ " + option.maxTime,
			  done:function(value, date, endDate){
				  option.changeTimeCallback(value, echartsInstances);
			  }
			});

		var viewBtn = layero.find('.l');
		//创建按钮
		$.each(option.btns, function(i, n) {
			var btnType = "default";
			if (i == 0) {
				btnType = "primary";
			}
			viewBtn.append('<button data-id="' + i + '" class="btn btn-' + btnType + ' radius">' + n + '</button>');
		});
		
		//创建echart图表
		var viewEcharts = layero.find('.echarts-view');
		$.each(option.echartsOptions, function(i, n) {
			var hideFlag = "hide";
			if (i == 0) {
				hideFlag = "";
			}
			
			viewEcharts.append('<div id="result-echarts-' + i + '" class="' + hideFlag + '" style="width:' + maxWidth * 0.95 + 'px;height:' + maxHeight * 0.76 + 'px;"></div>');
			var echartsObj = echarts.init(document.getElementById('result-echarts-' + i), 'shine');
			echartsObj.setOption(n);
			echartsInstances.push(echartsObj);
		});
		
		//绑定点击事件
		layero.find('button').click(function() {
			var dataId = $(this).attr('data-id');
			var that = this;
			$('#result-echarts-' + dataId).show('normal').siblings('div').hide('normal', function() {
				$(that).addClass('btn-primary').siblings('button').removeClass('btn-primary');
			});
		});
		
	}, null, null);
	
}

/************************************************************************************/
/**
 * 渲染测试结果的展示页面
 */
function renderResultViewPage(result, messageSceneId) {
	var color="";
	var flag="";
	if (result.runStatus == "0") {
		color="success";
		flag="SUCCESS";
	} else if (result.runStatus == "1"){
		color="danger";
		flag="FAIL";
	} else {
		color="default";
		flag="STOP";
	}						
	var resultData = {
		requestMessage:(result.requestMessage == "null") ? "" : result.requestMessage,
		requestUrl:result.requestUrl,
		businessSystemName:result.businessSystemName,
		color:color,
		flag:flag,
		useTime:result.useTime,
		statusCode:result.statusCode,
		responseMessage:(result.responseMessage == "null") ? "" : result.responseMessage,
		mark:result.mark,
	};			
	var html = templates["scene-test-result"](resultData);
	layer_show(result.messageInfo + ' - 测试结果', html, 700, 600 , 1, function(layero, index) {
		layero.find("#view-http-headers").click(function(){
			if (!strIsNotEmpty(result.headers)) {
				layer.msg("没有头信息!", {icon:0, time:1500});
				return;
			}
			layer_show('HTTP头信息', templates["scene-test-result-headers"](JSON.parse(result.headers)), 750, 440, 1);
		});		
		if (messageSceneId != null) {
			layero.find('tbody').prepend('<tr"><td colspan="2"><button id="save-response-example" class="btn btn-primary radius size-S"><strong>保存出参示例</strong></button></td></tr>');						
			layero.find('#save-response-example').click(function () {
				if (!strIsNotEmpty(result.responseMessage)) {
					layer.msg('没有返回报文!', {icon:5, time:1800});
					return false;
				}
				$.post(top.SCENE_UPDATE_RESPONSE_EXAMPLE, {messageSceneId:messageSceneId, responseExample:result.responseMessage}, function (json) {
					if (json.returnCode == 0) {
						layer.msg('更新成功!', {icon:1, time:1800});
					} else {
						layer.alert(json.msg, {icon:5});
					}
				});
			});
		}		
	}, function (index, layero) {
		layero.find('#save-response-example').unbind('click');
		layero.find('#view-http-headers').unbind('click');
	}, null, {
		shade: 0.25,
		skin: 'layui-layer-rim', //加上边框
		anim:-1
	});
}


/**
 * 测试环境在编辑页面的显示
 */
function appendSystem (systems) {
	var systemIds = [];
	$("#choose-business-system").siblings('p').remove();
	$.each(systems, function (i, n) {								
		$("#choose-business-system").before('<p>' + n.systemName + "[" + n.systemHost + ":" + n.systemPort + ']&nbsp;' + n.protocolType + '</p>');
		systemIds.push(n.systemId);
	});							
	$("#systems").val(systemIds.join(','));
}

/**
 * 使用layer的带文字的加载条
 */
var loadingIndex = null;
function loading(flag, tips){
	tips = tips || '正在加载中...';
	if (flag) {
		loadingIndex = layer.msg(tips, {icon:16, shade:0.45, time:999999999});
	} else {
		layer.close(loadingIndex);
	}
}

/**
 * 删除数组中空值
 * @param arr
 * @returns
 */
function clearNullArr(arr){ 
	if (arr == null) return false;
	for(var i=0,len=arr.length;i<len;i++){ 
		if(!arr[i]||arr[i]==''||arr[i] === undefined){ 
			arr.splice(i,1); 
			len--; 
			i--; 
		} 
	} 
	return arr; 
} 

/**
 * 判断是否为json格式字符串
 * @param str
 */
function isJSON(str) {
    if (typeof str == 'string') {
        try {
            var obj=JSON.parse(str);
            if(typeof obj == 'object' && obj ){
                return true;
            }else{
                return false;
            }

        } catch(e) {
            console.log('error：'+str+'!!!'+e);
            return false;
        }
    }
    console.log('It is not a string!')
}

/*********************判断两个json对象是否一样*****************************/
function isObj(object) {
    return object && typeof(object) == 'object' && Object.prototype.toString.call(object).toLowerCase() == "[object object]";
}

function isArray(object) {
    return object && typeof(object) == 'object' && object.constructor == Array;
}

function getLength(object) {
    var count = 0;
    for(var i in object) count++;
    return count;
}

function CompareJsonObj(objA, objB) {
    if(!isObj(objA) || !isObj(objB)) return false; //判断类型是否正确
    if(getLength(objA) != getLength(objB)) return false; //判断长度是否一致
    return CompareObj(objA, objB, true); //默认为true
}

function CompareObj(objA, objB, flag) {
	if(getLength(objA) != getLength(objB)) return false;
    for(var key in objA) {
        if(!flag) //flag为false，则跳出整个循环
            break;
        if(!objB.hasOwnProperty(key)) {//是否有自身属性，而不是继承的属性
            flag = false;
            break;
        }
        if(!isArray(objA[key])) { //子级不是数组时,比较属性值        	
        	if (isObj(objA[key])) {
        		if (isObj(objB[key])) {
        			if(!flag) //这里跳出循环是为了不让递归继续
                        break;
        			flag = CompareObj(objA[key], objB[key], flag);
        		} else {
        			flag = false;
                    break;
        		}
        	} else {
        		if(String(objB[key]) != String(objA[key])) { //排除数字比较的类型差异
            		flag = false;
                    break;
                }
        	}
        } else {
            if(!isArray(objB[key])) {
                flag = false;
                break;
            }
            var oA = objA[key],
                oB = objB[key];
            if(oA.length != oB.length) {
                flag = false;
                break;
            }
            for(var k in oA) {
                if(!flag) //这里跳出循环是为了不让递归继续
                    break;
                flag = CompareObj(oA[k], oB[k], flag);
            }
        }
    }
    return flag;
}
/***************************************************************************/