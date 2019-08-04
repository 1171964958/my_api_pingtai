var BATCH_AUTO_TEST_URL = "test-scenesTest"; //批量测试   测试集或者全量
var GET_TEST_CONFIG_URL = "test-getConfig";//获取当前用户的测试配置
var UPDATE_TEST_CONFIG_URL = "test-updateConfig";//更新指定测试配置
var CHECK_DATA_URL = "test-checkHasData";//测试之前检查指定的测试集中所有的测试场景是否有足够的测试数据
var TEST_SCENE_URL = "test-sceneTest";//单场景测试
var TEST_COMPLEX_SCENE_URL = "test-complexSceneTest";//测试组合场景

var PARAMS_GET_URL = "param-getParams"; //根据interfaceId来 获取parameters
var PARAM_SAVE_URL = "param-save";   //保存新增的接口参数
var PARAM_DEL_URL = "param-del";   //删除指定参数
var PARAM_GET_URL = "param-get";
var PARAM_EDIT_URL = "param-edit";  //编辑参数的指定属性
var PARAM_JSON_IMPORT_URL = "param-batchImportParams"; //导入json串
var PARAM_DEL_ALL_URL = "param-delInterfaceParams";//删除指定接口下的所有参数


var INTERFACE_LIST_URL = "interface-list"; //获取接口列表
var INTERFACE_CHECK_NAME_URL = "interface-checkName"; //检查新增接口名是否重复
var INTERFACE_EDIT_URL = "interface-edit";  //接口编辑
var INTERFACE_GET_URL = "interface-get"; //获取指定接口信息
var INTERFACE_DEL_URL = "interface-del"; //删除指定接口
var INTERFACE_IMPORT_FROM_EXCEL_URL = "interface-importFromExcel";//从已经上传完成的excel中导入接口数据
var INTERFACE_EXPORT_DOCUMENT_EXCEL_URL = "interface-exportInterfaceDocument";//批量导出接口文档
var INTERFACE_GET_PARAMETERS_JSON_TREE_URL = "interface-getParametersJsonTree";//获取jsonTree数据
var INTERFACE_UPDATE_CHILDREN_BUSINESS_SYSTEMS_URL = "interface-updateChildrenBusinessSystems";//更新接口下属报文场景等的测试环境信息



var MESSAGE_LIST_URL = "message-list"; //获取报文列表
var MESSAGE_EDIT_URL = "message-edit";  //报文信息编辑
var MESSAGE_GET_URL = "message-get"; //获取指定报文信息
var MESSAGE_DEL_URL = "message-del"; //删除指定报文
var MESSAGE_FORMAT_URL = "message-format";//格式化报文串
var MESSAGE_VALIDATE_JSON_URL = "message-validateJson";//报文串验证
var MESSAGE_IMPORT_FROM_EXCEL = "message-importFromExcel";//从已经上传完成的excel中导入接口数据
var MESSAGE_CREATE_MESSAE_BY_NODES_URL = "message-createMessage";


var SCENE_LIST_URL = "scene-list"; //获取场景列表
var SCENE_EDIT_URL = "scene-edit";  //场景编辑
var SCENE_GET_URL = "scene-get"; //获取指定场景信息
var SCENE_DEL_URL = "scene-del"; //删除指定场景
var SCENE_GET_TEST_OBJECT_URL = "scene-getTestObject"; //获取场景的测试数据和测试地址
var SCENE_LIST_NO_DATA_SCENES_URL = "scene-listNoDataScenes"; //获取指定测试集中没有测试数据的测试场景列表
var SCENE_IMPORT_FROM_EXCEL = "scene-importFromExcel";//从已经上传完成的excel中导入场景数据
var SCENE_UPDATE_RESPONSE_EXAMPLE = "scene-updateResponseExample";//更新返回示例
var SCENE_GET_REQUEST_MSG_JSON_TREE_URL = "scene-getRequestMsgJsonTree";//获取请求入参节点树信息
var SCENE_GET_RESPONSE_MSG_JSON_TREE_URL = "scene-getResponseMsgJsonTree";//获取请求入参节点树信息


var GET_SETTING_DATA_URL = "data-getSettingData"; //获取当前场景所属报文的所有可编辑入参节点 包含其他信息
var IMPORT_DATA_VALUES_URL = "data-importDatas";//批量导入测试数据
var DATA_LIST_URL = "data-list"; //获取指定测试场景数据列表
var DATA_EDIT_URL = "data-edit";  //测试数据编辑
var DATA_GET_URL = "data-get"; //获取指定测试数据信息
var DATA_DEL_URL = "data-del"; //删除指定测试数据
var DATA_CHECK_NAME_URL = "data-checkName"; //验证数据标记是否重复
var GET_SETTING_DATA_URL = "data-getSettingData";//获取设置参数数据时需要用到的相关数据
var CREATE_NEW_DATA_MSG_URL = "data-createDataMsg";//根据传入的json数据字符串和场景id生成新得带有数据的报文
var DATA_CHANGE_STATUS_URL = "data-changeStatus";//修改指定数据状态
var DATA_UPDATE_PARAMS_DATA_URL = "data-updateParamsData";//修改设置数据内容

var REPORT_LIST_URL = "report-list";//测试报告列表
var REPORT_GET_URL = "report-get";//获取指定测试报告信息
var REPORT_DEL_URL = "report-del";//删除指定测试报告
var REPORT_DOWNLOAD_STATIS_HTML = "report-generateStaticReportHtml";//获取静态测试报告路径（不存在就后台执行生成）
var REPORT_GET_DETAILS_URL = "report-getReportDetail";//获取生成完整测试报告所需数据
var REPORT_SEND_MAIL_URL = "report-sendMail";//将生成的离线报告进行邮件推送

var RESULT_LIST_URL = "result-list";//获取测试结果列表
var RESULT_GET_URL = "result-get";//获取指定测试结果详情信息


var SET_OP_SCENE_URL = "set-opScene";//操作测试场景，添加到测试集或者从测试集删除
var LIST_MY_SETS_URL = "set-getMySet";//获取测试集列表
var SET_SCENE_LIST_URL = "set-listScenes";//展示存在测试集或者不存在于测试集的测试场景
var SET_LIST_URL = "set-list"; //获取测试集列表
var SET_EDIT_URL = "set-edit";  //测试集信息编辑
var SET_GET_URL = "set-get"; //获取指定测试集信息
var SET_DEL_URL = "set-del"; //删除指定测试集
var SET_NAME_CHECK_URL = "set-checkName"; //验证测试集名称是否重复
var SET_RUN_SETTING_CONFIG_URL = "set-settingConfig";//配置测试集运行时配置
var SET_GET_CATEGORY_NODES_URL = "set-getCategoryNodes";//获取测试集目录树
var SET_MOVE_TO_FOLDER = "set-moveFolder"; //将指定测试集移动到指定的目录文件夹下

//组合场景信息
var COMPLEX_SCENE_LIST_URL = "complexScene-list";
var COMPLEX_SCENE_GET_URL = "complexScene-get";
var COMPLEX_SCENE_EDIT_URL = "complexScene-edit";
var COMPLEX_SCENE_DEL_URL = "complexScene-del";
var COMPLEX_SCENE_LIST_SET_SCENES_URL="complexScene-listSetScenes";//获取指定测试集的组合场景
var COMPLEX_SCENE_ADD_TO_SET_URL = "complexScene-addToSet";//添加组合场景到指定测试集
var COMPLEX_SCENE_DEL_FROM_SET_URL = "complexScene-delFromSet";//从测试集中删除组合场景
var COMPLEX_SCENE_LIST_SCENES_URL = "complexScene-listScenes";//获取组合场景中的所有测试场景
var COMPLEX_SCENE_UPDATE_CONFIG_INFO_URL = "complexScene-updateConfigInfo";//更新配置信息
var COMPLEX_SCENE_ADD_SCENE_URL = "complexScene-addScene";//添加测试场景到组合场景
var COMPLEX_SCENE_DEL_SCENE_URL = "complexScene-delScene";//从组合场景中删除测试场景
var COMPLEX_SCENE_SORT_SCENES_URL = "complexScene-sortScenes";//测试场景执行顺序排序
var COMPLEX_SCENE_UPDATE_SCENE_CONFIG_URL = "complexScene-updateSceneConfig";//更新指定测试场景的配置信息
var COMPLEX_SCENE_LIST_SAVE_VARIABLES_URL = "complexScene-getSaveVariables";//获取组合场景中指定的保存变量名称

//场景验证
var VALIDATE_GET_URL = "validate-getValidate";//获取指定测试场景的验证规则(只限全文验证和边界验证)
var VALIDATE_FULL_EDIT_URL = "validate-validateFullEdit";//全文验证规则更新
var VALIDATE_RULE_LIST_URL = "validate-getValidates"; //获取验证规则列表
var VALIDATE_RULE_EDIT_URL = "validate-edit";//新增或者编辑验证规则
var VALIDATE_RULE_GET_URL = "validate-get";//获取指定验证规则信息
var VALIDATE_RULE_DEL_URL = "validate-del";//删除指定验证规则
var VALIDATE_FULL_RULE_GET_URL = "validate-getValidate";//获取全文验证规则
var VALIDATE_RULE_UPDATE_STATUS = "validate-updateValidateStatus";//更新验证规则状态

//定时任务
var TASK_CHECK_NAME_URL = "task-checkName";//验证测试任务是否重名
var TASK_EDIT_URL = "task-edit";//编辑定时任务
var TASK_DEL_URL = "task-del";//删除指定定时任务
var TASK_LIST_URL = "task-list";//定时任务列表
var TASK_GET_URL = "task-get";//获取指定定时任务列表
var TASK_STOP_TASK_URL = "task-stopRunningTask";//停止运行中的定时任务
var TASK_ADD_RUNABLE_TASK_URL = "task-startRunableTask";//运行可运行的定时任务
var TASK_START_QUARTZ_URL = "task-startQuartz";//开启quartz定时器
var TASK_STOP_QUARTZ_URL = "task-stopQuartz";//停止quartz定时器
var TASK_GET_QUARTZ_STATUS_URL = "task-getQuartzStatus";//获取quartz定时器当前的状态
var TASK_UPDATE_CRON_EXPRESSION_URL = "task-updateCronExpression";//更新定时规则

var GLOBAL_SETTING_EDIT_URL = "global-edit";//全局配置编辑
var GLOBAL_SETTING_LIST_ALL_URL = "global-listAll";//获取全部的全局配置

var OP_INTERFACE_LIST_URL = "op-listOp"; //操作接口列表
var OP_INTERFACE_DEL_URL = "op-del"; //删除操作接口
var OP_INTERFACE_GET_URL = "op-get"; //获取操作接口详细信息
var OP_INTERFACE_EDIT_URL = "op-edit"; //编辑操作接口
var OP_INTERFACE_DEIT_GET_NODE_TREE_URL = "op-getNodeTree";//获取节点树数据
var OP_INTERFACE_LIST_ALL_URL = "op-listAll"; //获取全部的操作接口

var QUERY_DB_LINK_TEST_URL = "db-testDB";//测试指定查询数据库是否可连接
var QUERY_DB_DEL_URL = "db-del";//删除指定查询数据库信息
var QUERY_DB_LIST_URL = "db-list"//查询数据库列表
var QUERY_DB_EDIT_URL = "db-edit"//编辑指定查询数据库信息
var QUERY_DB_GET_URL = "db-get";//获取指定查询数据库信息
var QUERY_DB_LIST_ALL_URL = "db-listAll";//获取所有当前可用的查询数据数据信息


var MAIL_LIST_URL = "mail-list";//信息列表
var MAIL_DEL_URL = "mail-del";//删除信息
var MAIL_CHANGE_STATUS = "mail-changeStatus";//改变已读状态

var ROLE_DEL_URL = "role-del";//删除指定角色信息
var ROLE_GET_NODES_INTERFACE_URL = "role-getInterfaceNodes";//获取当前所有操作接口，并标记哪些是当前角色拥有的
var ROLE_GET_NODES_MENU_URL = "role-getMenuNodes"; //获取当前所有菜单，并标记哪些是当前角色拥有的
var ROLE_EDIT_URL = "role-edit";//编辑指定角色信息
var ROLE_LIST_URL = "role-list";//角色信息列表
var ROLE_GET_URL = "role-get";//指定角色信息
var ROLE_UPDATE_POWER_URL = "role-updateRolePower";//更新操作接口与角色之间的关系（角色的权限信息）
var ROLE_UPDATE_MENU_URL = "role-updateRoleMenu";//更新菜单与角色之间的关系
var ROLE_LIST_ALL_URL = "role-listAll";//展示所有角色

var USER_LIST_URL = "user-list";//用户列表
var USER_LOCK_URL = "user-lock";//锁定用户或者解锁用户
var USER_GET_URL = "user-get";//获取用户信息
var USER_EDIT_URL = "user-edit";//编辑用户信息
var USER_RESET_PASSWD_URL = "user-resetPwd";//重置指定用户的密码为111111


var GLOBAL_VARIABLE_LIST_URL = "variable-listAll";
var GLOBAL_VARIABLE_EDIT_URL = "variable-edit";
var GLOBAL_VARIABLE_DEL_URL = "variable-del";
var GLOBAL_VARIABLE_GET_URL = "variable-get";
var GLOBAL_VARIABLE_CHECK_NAME_URL = "variable-checkName";//检查key是否重复
var GLOBAL_VARIABLE_UPDATE_VALUE_URL = "variable-updateValue";//更新指定变量模板的value值
var GLOBAL_VARIABLE_CREATE_VARIABLE_URL = "variable-createVariable";//生成变量结果

var BUSINESS_SYSTEM_EDIT_URL = "system-edit";//编辑或者新增测试环境信息
var BUSINESS_SYSTEM_GET_URL = "system-get";//获取测试环境信息
var BUSINESS_SYSTEM_LIST_URL = "system-list";//查询测试环境列表
var BUSINESS_SYSTEM_LIST_ALL_URL = "system-listAll";//查询所有测试环境列表
var BUSINESS_SYSTEM_DEL_URL = "system-del";//删除测试环境信息

var BUSINESS_SYSTEM_INTERFACE_LIST_URL = "system-listInterface";//根据mode来查询当前测试环境包含的或者不包含的接口信息
var BUSINESS_SYSTEM_OP_INTERFACE_URL = "system-opInterface";//从指定的测试环境中删除或者增加接口信息

var PROBE_TASK_GET_URL = "probe-get";
var PROBE_TASK_EDIT_URL = "probe-edit";
var PROBE_TASK_DEL_URL = "probe-del";
var PROBE_TASK_LIST_ALL_URL = "probe-listAll";
var PROBE_TASK_LIST_URL = "probe-list";
var PROBE_TASK_UPDATE_CONFIG = "probe-updateConfig"//更新探测配置
var PROBE_TASK_START_URL = "probe-startTask";//开启任务
var PROBE_TASK_STOP_URL = "probe-stopTask";//停止任务
var PROBE_TASK_GET_SINGLE_REPORT_DATA_URL = "probe-getProbeResultReportData";
var PROBE_TASK_GET_PROBE_RESULT_SYSNOPSIS_VIEW_DATA_URL = "probe-getProbeResultSynopsisViewData";
var PROBE_TASK_BATCH_ADD_URL = "probe-batchAdd";

var UPLOAD_FILE_URL = "file-upload";//上传文件
var DOWNLOAD_FILE_URL = "file-download";//下载文件


//系统日志记录
var LOG_RECORD_LIST_URL = "log-list";
var LOG_RECORD_GET_URL = "log-get";
var LOG_RECORD_DEL_URL = "log-del";

//接口mock功能
var INTERFACE_MOCK_LIST_URL = "mock-list";
var INTERFACE_MOCK_EDIT_URL = "mock-edit";
var INTERFACE_MOCK_GET_URL = "mock-get";
var INTERFACE_MOCK_DEL_URL = "mock-del";
var INTERFACE_MOCK_CHECK_NAME_URL = "mock-checkName";
var INTERFACE_MOCK_UPDATE_STATUS_URL = "mock-updateStatus";
var INTERFACE_MOCK_UPDATE_SETTING_URL = "mock-updateSetting";
var INTERFACE_MOCK_PARSE_MESSAGE_TO_CONFIG_URL = "mock-parseMessageToConfig";
var INTERFACE_MOCK_PARSE_MESSAGE_TO_NODES_URL = "mock-parseMessageToNodes";


//性能测试配置
var PERFORMANCE_TEST_CONFIG_EDIT_URL = "ptc-edit";
var PERFORMANCE_TEST_CONFIG_LIST_URL = "ptc-list";
var PERFORMANCE_TEST_CONFIG_DEL_URL = "ptc-del";
var PERFORMANCE_TEST_CONFIG_GET_URL = "ptc-get";

var PERFORMANCE_TEST_TASK_LIST_URL = "ptc-listTest" //获取当前用户的性能测试任务列表
var PERFORMANCE_TEST_TASK_STOP_URL = "ptc-stopTest";//停止指定性能测试任务
var PERFORMANCE_TEST_TASK_DEL_URL = "ptc-delTest";//删除指定性能测试任务-并且不会保存测试结果
var PERFORMANCE_TEST_TASK_INIT_URL = "ptc-initTest";//初始化性能测试任务
var PERFORMANCE_TEST_TASK_ACTION_URL = "ptc-actionTest";//开始执行性能测试任务
var PERFORMANCE_TEST_TASK_VIEW_URL = "ptc-viewTest";//查看性能测试任务的实时状态

//性能测试结果
var PERFORMANCE_TEST_RESULT_LIST_URL = "ptr-list";
var PERFORMANCE_TEST_RESULT_GET_URL = "ptr-get";
var PERFORMANCE_TEST_RESULT_DEL_URL = "ptr-del";
var PERFORMANCE_TEST_RESULT_ANAYLZE_URL = "ptr-anaylzeView";//分析结果
var PERFORMANCE_TEST_RESULT_SUMMARIZED_URL = "ptr-summarizedView";//汇总结果到excel
var PERFORMANCE_TEST_RESULT_DETAILS_LIST_ALL_URL = "ptr-detailsList";//详细结果查看


/*************************************WEB自动化**************************************************************/
//web自动化-山西-模块管理
var WEB_SCRIPT_MODULE_LIST_URL = "webModule-list";
var WEB_SCRIPT_MODULE_GET_URL = "webModule-get";
var WEB_SCRIPT_MODULE_DEL_URL = "webModule-del";
var WEB_SCRIPT_MODULE_EDIT_URL = "webModule-edit";
var WEB_SCRIPT_MODULE_CHECK_NAME_URL = "webModule-checkName"; //检查moduleCode是否重复

//web自动化-山西-任务管理
var WEB_SCRIPT_TASK_LIST_URL = "webTask-list";
var WEB_SCRIPT_TASK_DEL_URL = "webTask-del";


//元素对象管理
var WEB_ELEMENT_LIST_ALL_URL = "element-listAll";
var WEB_ELEMENT_LIST_URL = "element-list";
var WEB_ELEMENT_GET_URL = "element-get";
var WEB_ELEMENT_DEL_URL = "element-del";
var WEB_ELEMENT_EDIT_URL = "element-edit";
var WEB_ELEMENT_MOVE_URL = "element-move";
var WEB_ELEMENT_COPY_URL = "element-copy";


//测试用例
var WEB_CASE_LIST_ALL_URL = "webcase-listAll";
var WEB_CASE_LIST_URL = "webcase-list";
var WEB_CASE_GET_URL = "webcase-get";
var WEB_CASE_DEL_URL = "webcase-del";
var WEB_CASE_EDIT_URL = "webcase-edit";
var WEB_CASE_CHANGE_BROSWER_TYPE_URL = "webcase-changeBroswerType";
var WEB_CASE_UPDATE_CONFIG_JSON_URL = "webcase-updateConfig";

//测试步骤
var WEB_STEP_LIST_ALL_URL = "webstep-listAll";
var WEB_STEP_LIST_URL = "webstep-list";
var WEB_STEP_GET_URL = "webstep-get";
var WEB_STEP_DEL_URL = "webstep-del";
var WEB_STEP_EDIT_URL = "webstep-edit";
var WEB_STEP_UPDATE_CONFIG_URL = "webstep-updateConfig";

//测试用例集
var WEB_SUITE_LIST_ALL_URL = "websuite-listAll";
var WEB_SUITE_LIST_URL = "websuite-list";
var WEB_SUITE_GET_URL = "websuite-get";
var WEB_SUITE_DEL_URL = "websuite-del";
var WEB_SUITE_EDIT_URL = "websuite-edit";
var WEB_SUITE_CHANGE_BROSWER_TYPE_URL = "websuite-changeBroswerType";
var WEB_SUITE_UPDATE_CONFIG_JSON_URL = "websuite-updateConfig";
var WEB_SUITE_LIST_CASE_URL = "websuite-listCase";
var WEB_SUITE_OP_CASE_URL = "websuite-opCase";
var WEB_SUITE_CASE_UPDATE_SETTING_URL = "websuite-updateCaseSetting";

//运行时配置
var WEB_CONFIG_GET_URL = "webconfig-get";
var WEB_CONFIG_EDIT_URL = "webconfig-edit";

//菜单相关
var BUSI_MENU_EDIT_URL = "menu-edit";
var BUSI_MENU_GET_URL = "menu-get";
var BUSI_MENU_LIST_ALL_URL = "menu-listAll";
var BUSI_MENU_DEL_URL = "menu-del";
var BUSI_MENU_GET_USER_MENUS_URL = "menu-getUserMenus";
