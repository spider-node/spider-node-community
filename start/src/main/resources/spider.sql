-- 领域表,对领域进行控制-核心（sdk_url-sdk的http地址, scan_class_path扫描class信息的路径）
drop table if exists spider_area;
CREATE TABLE `spider_area`
(
    `id`              varchar(64) NOT NULL COMMENT 'id',
    `area_name`       varchar(64) NOT NULL DEFAULT '' COMMENT '领域名称',
    `desc`            varchar(64) NOT NULL DEFAULT '' COMMENT '领域描述',
    `sdk_url`         varchar(1024)         DEFAULT '' COMMENT '领域sdk',
    `scan_class_path` varchar(128)         DEFAULT '' COMMENT '扫描类路径',
    `sdk_name`        varchar(128)         DEFAULT '' COMMENT 'sdk的名称',
    `sdk_status`      varchar(20)          DEFAULT 'INIT' COMMENT 'sdk状态',
    `create_time`     datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `area_name` (`area_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='spider-功能域';

-- 域功能-主要存放一个领域中的能力-在领域列表进行刷新sdk就会更新（result_mapping与param_mapping），或者新增该表内容
drop table if exists spider_area_function;
CREATE TABLE `spider_area_function`
(
    `id`                varchar(64) NOT NULL COMMENT 'id',
    `name`              varchar(64)          DEFAULT '' COMMENT '节点名称',
    `desc`              varchar(64)          DEFAULT '' COMMENT '领域描述',
    `async`             varchar(64)          DEFAULT '' COMMENT '是否异步',
    `task_component`    varchar(64) NOT NULL DEFAULT '' COMMENT '组件名称',
    `task_service`      varchar(64) NOT NULL DEFAULT '' COMMENT '组件方法',
    `status`            varchar(32)          DEFAULT 'STOP' COMMENT '状态',
    `service_task_type` varchar(64)          DEFAULT 'NORMAL' COMMENT '组件方法类型/NORMAL/POLL/DELAY/APPROVE',
    `result_mapping`    varchar(4980)        DEFAULT '{}' COMMENT '返回的字段信息',
    `param_mapping`     varchar(4980)        DEFAULT '{}' COMMENT '入参的字段',
    `task_method`       varchar(1280)        DEFAULT '' COMMENT '方法参数',
    `area_id`           varchar(64)          DEFAULT '' COMMENT '领域id',
    `area_name`         varchar(64)          DEFAULT '' COMMENT '领域名称',
    `worker_id`         varchar(64)          DEFAULT '' COMMENT '服务id',
    `create_time`       datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `task_component_task_service` (`task_component`,`task_service`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='域功能';

-- 领域的业务功能表 主要提供该领域对外的访问的业务功能信息存储.基于area_id挂在某个领域中
drop table if exists spider_business_function;
CREATE TABLE `spider_business_function`
(
    `id`            varchar(64) NOT NULL COMMENT 'id',
    `function_name` varchar(64)          DEFAULT '' NOT NULL DEFAULT '' COMMENT '功能名称',
    `service_name`  varchar(64)          DEFAULT '' COMMENT '服务名称',
    `desc`          varchar(64)          DEFAULT '' COMMENT '领域描述',
    `director`      varchar(64)          DEFAULT '' COMMENT '负责人',
    `status`        varchar(64)          DEFAULT 'STOP' COMMENT '状态',
    `area_id`       varchar(64) NOT NULL DEFAULT '' COMMENT '领域id',
    `create_time`   datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `function_name` (`function_name`),
    KEY             `create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='领域业务功能';

-- spider业务功能版本控制表，核心基于function_id 挂在某个领域功能下面 实现一个业务功能，支持多个版本
drop table if exists spider_business_function_version;
CREATE TABLE `spider_business_function_version`
(
    `id`             varchar(64) NOT NULL COMMENT 'id',
    `function_name`  varchar(64) NOT NULL DEFAULT '' COMMENT '功能名称',
    `desc`           varchar(64)          DEFAULT '' COMMENT '领域描述',
    `version`        varchar(64) NOT NULL DEFAULT '' COMMENT '功能版本',
    `function_id`    varchar(64) NOT NULL DEFAULT '' COMMENT '功能id',
    `bpmn_url`       varchar(1024)         DEFAULT '' COMMENT '模型地址',
    `start_event_id` varchar(128)         DEFAULT '' COMMENT '功能启动id',
    `bpmn_name`      varchar(128)         DEFAULT '' COMMENT '模型名称',
    `bpmn_status`    varchar(20)          DEFAULT '' COMMENT 'bpmn_状态',
    `result_mapping` varchar(1280)        DEFAULT '' COMMENT '返回的字段信息',
    `status`         varchar(64)          DEFAULT 'STOP' COMMENT '状态',
    `create_time`    datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP (3) COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `version_function_name` (`version`,`function_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='spider领域功能版本';