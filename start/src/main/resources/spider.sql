create table spider.area_datasource_info
(
    id                int auto_increment
        primary key,
    datasource        varchar(64)                        not null comment '数据源名称',
    url               varchar(1024)                      not null comment '数据库的连接地址',
    name              varchar(128)                       not null comment '数据库的用户名',
    password          varchar(128)                       not null comment '数据库密码',
    initial_size      int                                not null comment '数据库的连接初始化值',
    min_idle          int                                not null comment '数据库连接最小值',
    max_idle          int                                not null comment '数据库连接最大值',
    driver_class_name varchar(128)                       not null comment '驱动名称',
    create_time       datetime default CURRENT_TIMESTAMP null
);

create table spider.area_domain_base_info
(
    id                                 int auto_increment
        primary key,
    datasource_id                      int                                not null comment '数据源id',
    table_name                         varchar(255)                       null comment '表名称',
    datasource_name                    varchar(255)                       null comment '数据源名称',
    domain_object                      text                               null comment '域对象',
    domain_object_package              varchar(255)                       null comment '域对象包名称',
    domain_object_entity_name          varchar(255)                       null comment '域对象实体名称',
    domain_object_service_name         varchar(255)                       null comment '领域基础操作的接口名称',
    domain_object_service_package      varchar(255)                       null comment '领域基础操作的接口包名称',
    domain_object_service_impl_name    varchar(255)                       null comment '领域基础操作的接口实现类名称',
    domain_object_service_impl_package varchar(255)                       null comment '领域基础操作的接口实现类包名',
    version                            varchar(20)                        null comment '版本',
    group_id                           varchar(128)                       null comment 'pom文件中的group_id',
    artifact_id                        varchar(128)                       null comment 'pom文件中的artifact_id',
    create_time                        datetime default CURRENT_TIMESTAMP null,
    son_area_name                      varchar(255)                       null comment '子域名称',
    son_area_id                        int                                not null comment '子领域id',
    area_id                            varchar(64)                        null comment '领域id',
    area_name                          varchar(64)                        null comment '主领域名称'
)
    comment 'spider领域基础信息' charset = utf8mb4;

create index datasource_id
    on spider.area_domain_base_info (datasource_id);

create index son_area_id
    on spider.area_domain_base_info (son_area_id);

create index table_name
    on spider.area_domain_base_info (table_name);

create table spider.area_domain_function_info
(
    id                         int auto_increment
        primary key,
    datasource_id              int                                     not null comment '数据源id',
    table_name                 varchar(128)                            null comment '表名称',
    function_name              varchar(255)                            null comment '功能名称',
    function_desc              varchar(255)                            null comment '功能描述',
    datasource_name            varchar(255)                            null comment '数据源名称',
    area_function_class        text                                    null comment '业务功能方法提供的类',
    area_function_param_class  varchar(2580)                           null comment '业务方法的入参',
    area_function_result_class varchar(2580)                           null comment '业务方法的出参',
    status                     varchar(30)                             null comment '状态-init,init_fail,init_suss',
    version                    varchar(20)                             null comment '版本',
    group_id                   varchar(128)                            null comment 'pom文件中的group_id',
    artifact_id                varchar(128)                            null comment 'pom文件中的artifact_id',
    son_domain_info            varchar(1280) default '{}'              null comment '子域信息',
    task_component             varchar(128)                            null comment '组件名称',
    task_service               varchar(128)                            null comment '组件方法',
    create_time                datetime      default CURRENT_TIMESTAMP null,
    area_id                    varchar(64)                             null comment '领域id',
    area_name                  varchar(128)                            null comment '领域id',
    file_Url                   varchar(256)                            null comment '文件地址',
    biz_Url                    varchar(564)                            null comment '文件地址',
    instance_num               int           default 1                 null comment '实例数量',
    domain_function_version_id varchar(64)   default ''                null comment '领域id',
    task_id                    int                                     not null comment '任务id',
    project_param              text                                    null comment '部署的yaml',
    deploy_yaml                varchar(3650)                           null comment '部署的yaml',
    other_code                 text                                    null comment '其他类',
    biz_version                varchar(20)                             null comment '插件的版本',
    biz_name                   varchar(64)                             null comment '插件名称',
    constraint un_domain_function_version_id
        unique (domain_function_version_id)
)
    comment 'spider领域基础信息' charset = utf8mb4;

create table spider.area_domain_info
(
    id                                          int auto_increment
        primary key,
    datasource_id                               int                                null,
    table_name                                  varchar(255)                       null,
    domain_object                               text                               null,
    domain_object_package                       varchar(255)                       null,
    domain_object_entity_name                   varchar(255)                       null,
    domain_object_service_name                  varchar(255)                       null,
    domain_object_service_package               varchar(255)                       null,
    domain_object_service_impl_name             varchar(255)                       null,
    domain_object_service_impl_package          varchar(255)                       null,
    domain_object_business_class_package        varchar(255)                       null,
    domain_object_business_method_param_package varchar(255)                       null,
    create_time                                 datetime default CURRENT_TIMESTAMP null
);

create table spider.spider_application_task
(
    id               int auto_increment
        primary key,
    ip               varchar(64)                              null comment '宿主应用的地址',
    task_business_id int         default 0                    null comment '功能id',
    task_type        varchar(30)                              null comment '任务类型 INSTALL/UNINSTALL/DELETE_HOST/INSERT_HOST',
    status           varchar(64)                              not null comment 'INIT/ING/SUSS/FAIL',
    error            varchar(5680)                            null comment '异常信息',
    create_time      datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间'
)
    comment '宿主应用';

create table spider.spider_area
(
    id          varchar(64)                              not null comment 'id'
        primary key,
    area_name   varchar(64) default ''                   not null comment '领域名称',
    `desc`      varchar(64) default ''                   not null comment '领域描述',
    create_time datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    constraint area_name
        unique (area_name)
)
    comment 'spider-功能域' collate = utf8mb4_bin
                            row_format = DYNAMIC;

create table spider.spider_area_function
(
    id              varchar(64)                               not null comment 'id'
        primary key,
    name            varchar(64)  default ''                   null comment '节点名称',
    `desc`          varchar(64)  default ''                   null comment '领域描述',
    task_component  varchar(64)  default ''                   not null comment '组件名称',
    task_service    varchar(64)  default ''                   not null comment '组件方法',
    status          varchar(32)  default 'STOP'               null comment '状态',
    task_method     varchar(128) default ''                   null comment '方法参数',
    son_domain_Info varchar(512) default ''                   null comment '子域信息',
    area_id         varchar(64)  default ''                   null comment '领域id',
    area_name       varchar(64)  default ''                   null comment '领域名称',
    worker_id       varchar(64)  default ''                   null comment '服务id',
    worker_type     varchar(64)  default ''                   null comment 'MICROSERVICE(微服务)/HOST_APPLICATION(宿主应用)',
    create_time     datetime(3)  default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    constraint name
        unique (name),
    constraint task_component_task_service
        unique (task_component, task_service)
)
    comment '域功能' collate = utf8mb3_bin
                     row_format = DYNAMIC;

create table spider.spider_area_function_version
(
    id                   varchar(64)                                not null comment 'id'
        primary key,
    domain_function_id   varchar(64)   default ''                   not null comment '领域功能id',
    son_domain_id        int           default 0                    not null comment '子域id',
    son_domain_version   varchar(64)   default ''                   null comment '子域版本',
    function_id          varchar(64)   default ''                   null comment '功能id(代表是那个业务能做的新增)',
    version              varchar(64)   default ''                   not null comment '版本号',
    version_desc         varchar(512)  default ''                   not null comment '描述',
    function_functional  varchar(2580) default '{}'                 not null comment '功能需求',
    test_case            varchar(2580) default '{}'                 not null comment '测试场景',
    result_mapping       varchar(5280) default '{}'                 null comment '返回的字段信息',
    run_mapping          varchar(5280) default '{}'                 null comment '执行参数',
    status               varchar(64)   default 'INIT'               null comment '状态',
    create_time          datetime(3)   default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    predict_deploy_num   int           default 1                    null comment '部署数量',
    domain_function_name varchar(64)   default ''                   not null comment '领域功能名称',
    son_domain_functions varchar(1280) default ''                   not null comment '子域数组',
    data_flow_id         int                                        null comment '数据流id',
    data_flow_name       varchar(128)  default ''                   null comment '数据流名称',
    result_analysis      text                                       null comment '表狗的解析信息',
    datasource_id        varchar(64)   default ''                   null comment 'mysql_url?之前内容'
)
    comment 'spider-领域功能版本' collate = utf8mb3_bin
                                  row_format = DYNAMIC;

create index domain_function_id
    on spider.spider_area_function_version (domain_function_id);

create table spider.spider_business_function
(
    id            varchar(64)                              not null comment 'id'
        primary key,
    function_name varchar(64) default ''                   not null comment '功能名称',
    service_name  varchar(64) default ''                   null comment '服务名称',
    `desc`        varchar(64) default ''                   null comment '领域描述',
    director      varchar(64) default ''                   null comment '负责人',
    status        varchar(64) default 'STOP'               null comment '状态',
    area_id       varchar(64) default ''                   not null comment '领域id',
    create_time   datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    constraint function_name
        unique (function_name)
)
    comment '领域业务功能' collate = utf8mb3_bin
                           row_format = DYNAMIC;

create index create_time
    on spider.spider_business_function (create_time);

create table spider.spider_business_function_version
(
    id                     varchar(64)                                                    not null comment 'id'
        primary key,
    function_name          varchar(64)                       default ''                   not null comment '功能名称',
    `desc`                 varchar(64)                       default ''                   null comment '领域描述',
    version                varchar(64)                       default ''                   not null comment '功能版本',
    function_id            varchar(64)                       default ''                   not null comment '功能id',
    bpmn_url               varchar(1024) collate utf8mb4_bin default ''                   null comment 'bpmn-url',
    start_event_id         varchar(128)                      default ''                   null comment '功能启动id',
    bpmn_name              varchar(128)                      default ''                   null comment '模型名称',
    bpmn_status            varchar(20)                       default ''                   null comment 'bpmn_状态',
    result_mapping         varchar(2280)                     default '{}'                 null comment '返回的字段信息',
    status                 varchar(64)                       default 'STOP'               null comment '状态',
    create_time            datetime(3)                       default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    run_mapping            varchar(2280)                     default '{}'                 null comment '入参',
    data_flow_id           int                                                            not null comment '数据流的id',
    data_flow_name         varchar(60)                       default ''                   null comment '模型名称',
    domain_function_name   varchar(64)                       default ''                   not null comment '领域功能名称',
    run_class              varchar(3280)                     default '{}'                 null comment '入参',
    result_class           varchar(3280)                     default '{}'                 null comment '返回的字段信息',
    run_object_config      varchar(3280)                     default '{}'                 null comment '入参',
    result_object_config   varchar(3280)                     default '{}'                 null comment '返回的字段信息',
    bpmn_xml               text                                                           null comment 'bpmn的xml信息',
    node_info              text                                                           null comment 'bpmn中的节点信息',
    input_param_java_class text                                                           null comment '入参类',
    out_param_java_class   text                                                           null comment '出参类',
    node_js_function_info  text                                                           null comment '节点-js的函数信息',
    constraint version_function_name
        unique (version, function_name)
)
    comment 'spider领域功能版本' collate = utf8mb3_bin
                                 row_format = DYNAMIC;

create table spider.spider_data_flow
(
    id                       int auto_increment
        primary key,
    data                     text                                       not null comment '数据流',
    son_area_ids             varchar(562)                               null comment '子域数组',
    status                   varchar(64)                                not null comment 'INIT初始化/ENABLE启用',
    flow_data_name           varchar(64)                                not null comment '数据流名称',
    create_time              datetime(3)   default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    flow_data_desc           varchar(2650) default '[]'                 null comment '描述',
    data_flow_analysis_model text                                       null comment '数据流解析的后的内容'
)
    comment '数据流';

create table spider.spider_data_flow_area_function
(
    id                  int auto_increment
        primary key,
    data_flow_id        int                                      not null comment '数据流id',
    function_id         varchar(64)                              not null comment '功能id',
    function_version_id varchar(64)                              not null comment '版本id',
    status              varchar(64)                              not null comment 'INIT初始化/ENABLE启用',
    create_time         datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间'
)
    comment '数据流与业务功能版本';

create table spider.spider_domain_function_ai_coder_step
(
    id                             int auto_increment
        primary key,
    step                           varchar(64) default 'LOAD_DOMAIN_INFO'   null comment '步骤LOAD_DOMAIN_INFO/CODER/CHECK/CODER_ARRANGEMENT/COMPILE/COMPILE_ERROR/TEST/END',
    spider_domain_function_task_id int                                      not null comment '任务id',
    error                          varchar(5680)                            null comment '异常信息',
    step_status                    varchar(30) default 'SUSS'               null comment '状态 SUSS/FAIL',
    create_time                    datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间'
)
    comment '代码生成过程表' charset = utf8mb4;

create index task_domain_id
    on spider.spider_domain_function_ai_coder_step (spider_domain_function_task_id);

create table spider.spider_domain_function_task
(
    id                         int auto_increment
        primary key,
    son_domain_info            varchar(1280) default '{}'                 null comment '领域id',
    domain_function_version_id varchar(64)                                null comment '功能版本id',
    task_domain_id             varchar(64)                                null comment '领域id',
    domain_function_id         varchar(64)                                null comment '功能id',
    task_type                  varchar(30)                                null comment '任务类型 NEWLY_ADDED/ITERATION',
    status                     varchar(64)                                not null comment '/DATA_INIT(数据准备)/CODING(编码钟)/COMPILE(编译)/TEST_DATA_INIT(测试数据准备)/TEST(测试)/FINISH(完成)',
    error                      varchar(5680)                              null comment '异常信息',
    create_time                datetime(3)   default CURRENT_TIMESTAMP(3) not null comment '创建时间'
)
    comment '代码任务表' charset = utf8mb4;

create index task_domain_id
    on spider.spider_domain_function_task (task_domain_id);

create table spider.spider_host_application
(
    id int auto_increment
        primary key,
    ip varchar(64) not null comment 'ip',
    constraint ip
        unique (ip)
)
    comment '宿主应用' collate = utf8mb4_bin
                       row_format = DYNAMIC;

create table spider.spider_plugin_deploy_info
(
    id                         int auto_increment
        primary key,
    ip                         varchar(64)                              not null comment 'ip',
    function_id                int                                      not null comment '功能id',
    task_component             varchar(128)                             null comment '组件名称',
    task_service               varchar(128)                             null comment '组件方法',
    version                    varchar(20)                              null comment '版本',
    status                     varchar(64)                              null comment '部署状态ING/DELETE',
    create_time                datetime(3) default CURRENT_TIMESTAMP(3) not null comment '创建时间',
    domain_function_version_id varchar(64) default ''                   null comment '领域id'
)
    comment '宿主应用';

create table spider.spider_son_area
(
    id            int auto_increment
        primary key,
    area_id       varchar(64)                            not null comment '领域id',
    area_name     varchar(256)                           not null comment '主域名称',
    son_area_name varchar(108)                           not null comment '领域名称',
    table_name    varchar(108)                           not null comment '表名称',
    datasource    varchar(108)                           not null comment '数据源名称',
    create_time   datetime     default CURRENT_TIMESTAMP null,
    son_area_desc varchar(256) default ''                null comment '子域描述'
)
    comment '子域';

create table spider.spider_task_coder_info
(
    id             int auto_increment
        primary key,
    input_param    varchar(5480) null comment '入参',
    out_param      varchar(5480) null comment '出参',
    business_coder text          null comment '代码',
    mvn            varchar(652)  null comment 'mvn 依赖',
    task_id        int           not null comment '任务id'
)
    comment '领域功能代码';

create index task_id
    on spider.spider_task_coder_info (task_id);

create table spider.spider_task_test_info
(
    id                         int auto_increment
        primary key,
    cases                      varchar(1280)                           null comment '测试用例',
    case_input_param           varchar(1280)                           null comment '入参',
    case_sql                   varchar(1280)                           null comment 'sql',
    case_sql_param             varchar(1280)                           null comment 'sql参数',
    task_id                    int                                     not null comment '任务id',
    test_status                varchar(1280) default 'INIT'            null comment 'INIT(初始化)/RUN(执行)/SUSS(执行成功)/FAIL(执行失败)',
    domain_function_version_id varchar(64)                             null comment '领域功能对应的版本id',
    run_result                 varchar(2680) default '{}'              null comment '执行的结果',
    error                      varchar(3000) default ''                null comment '异常',
    reject_reason              varchar(1280)                           null comment '驳回原因',
    create_time                datetime      default CURRENT_TIMESTAMP null,
    expect                     varchar(20)   default 'INIT'            null comment '是否符合预期'
)
    comment '测试用例的信息';

create index create_time
    on spider.spider_task_test_info (create_time);

create index task_id
    on spider.spider_task_test_info (task_id);

