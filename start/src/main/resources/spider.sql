CREATE TABLE `worker` (
                          `id` varchar(64)  NOT NULL COMMENT 'id',
                          `worker_name` varchar(32)  NOT NULL DEFAULT '' COMMENT '服务名称',
                          `desc` varchar(32)  NOT NULL DEFAULT '' COMMENT '描述',
                          `status` varchar(32)  NOT NULL DEFAULT '' COMMENT '状态',
                          `rpc_port` int NOT NULL DEFAULT '9974' COMMENT 'rpc-端口号',
                          `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='spider-worker';



CREATE TABLE `spider_area_function` (
                                        `id` varchar(64)  NOT NULL COMMENT 'id',
                                        `name` varchar(64)  DEFAULT '' COMMENT '节点名称',
                                        `desc` varchar(64)  DEFAULT '' COMMENT '领域描述',
                                        `async` varchar(64)  DEFAULT '' COMMENT '是否异步',
                                        `task_component` varchar(64)  NOT NULL DEFAULT '' COMMENT '组件名称',
                                        `task_service` varchar(64)  NOT NULL DEFAULT '' COMMENT '组件方法',
                                        `status` varchar(32)  DEFAULT 'STOP' COMMENT '状态',
                                        `service_task_type` varchar(64)  DEFAULT 'NORMAL' COMMENT '组件方法类型/NORMAL/POLL/DELAY/APPROVE',
                                        `result_mapping` varchar(4980)  DEFAULT '{}' COMMENT '返回的字段信息',
                                        `param_mapping` varchar(4980)  DEFAULT '{}' COMMENT '入参的字段',
                                        `task_method` varchar(1280)  DEFAULT '' COMMENT '方法参数',
                                        `area_id` varchar(64)  DEFAULT '' COMMENT '领域id',
                                        `area_name` varchar(64)  DEFAULT '' COMMENT '领域名称',
                                        `worker_id` varchar(64)  DEFAULT '' COMMENT '服务id',
                                        `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `task_component_task_service` (`task_component`,`task_service`),
                                        UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='域功能';


CREATE TABLE `spider_area` (
                               `id` varchar(64)  NOT NULL COMMENT 'id',
                               `area_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域名称',
                               `desc` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域描述',
                               `sdk_url` varchar(256)  DEFAULT '' COMMENT '领域sdk',
                               `scan_class_path` varchar(128)  DEFAULT '' COMMENT '扫描类路径',
                               `sdk_name` varchar(128)  DEFAULT '' COMMENT 'sdk的名称',
                               `sdk_status` varchar(20)  DEFAULT 'INIT' COMMENT 'sdk状态',
                               `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `area_name` (`area_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='spider-功能域';


CREATE TABLE `spider_business_function` (
                                            `id` varchar(64)  NOT NULL COMMENT 'id',
                                            `function_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能名称',
                                            `service_name` varchar(64)  DEFAULT '' COMMENT '服务名称',
                                            `desc` varchar(64)  DEFAULT '' COMMENT '领域描述',
                                            `director` varchar(64)  DEFAULT '' COMMENT '负责人',
                                            `status` varchar(64)  DEFAULT 'STOP' COMMENT '状态',
                                            `area_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域id',
                                            `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `function_name` (`function_name`),
                                            KEY `create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='领域业务功能';


CREATE TABLE `spider_business_function_version` (
                                                    `id` varchar(64)  NOT NULL COMMENT 'id',
                                                    `function_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能名称',
                                                    `desc` varchar(64)  DEFAULT '' COMMENT '领域描述',
                                                    `version` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能版本',
                                                    `function_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能id',
                                                    `bpmn_url` varchar(256)  DEFAULT '' COMMENT '模型地址',
                                                    `start_event_id` varchar(128)  DEFAULT '' COMMENT '功能启动id',
                                                    `bpmn_name` varchar(128)  DEFAULT '' COMMENT '模型名称',
                                                    `bpmn_status` varchar(20)  DEFAULT '' COMMENT 'bpmn_状态',
                                                    `result_mapping` varchar(1280)  DEFAULT '' COMMENT '返回的字段信息',
                                                    `status` varchar(64)  DEFAULT 'STOP' COMMENT '状态',
                                                    `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                                    PRIMARY KEY (`id`),
                                                    UNIQUE KEY `version_function_name` (`version`,`function_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='spider功能';


CREATE TABLE `spider_business_node_run_example_log` (
                                                        `id` varchar(64) NOT NULL COMMENT 'id',
                                                        `request_id` varchar(64) NOT NULL DEFAULT '' COMMENT '请求id',
                                                        `flow_element_name` varchar(64) DEFAULT '' COMMENT '节点名称',
                                                        `flowElementId` varchar(64) NOT NULL DEFAULT '' COMMENT '节点id',
                                                        `function_id` varchar(64) NOT NULL DEFAULT '' COMMENT '功能id',
                                                        `request_param` varchar(6480) NOT NULL DEFAULT '' COMMENT '功能请求执行参数',
                                                        `function_name` varchar(128) DEFAULT '' COMMENT '功能名称',
                                                        `return_param` varchar(6480) DEFAULT '' COMMENT '功能启动id',
                                                        `bpmn_name` varchar(128) DEFAULT '' COMMENT '模型名称',
                                                        `bpmn_status` varchar(20) DEFAULT '' COMMENT 'bpmn_状态',
                                                        `exception` varchar(2480) default '' COMMENT '执行异常信息',
                                                        `status` varchar(24) default '' COMMENT '执行状态',
                                                        `start_time` datetime(3) DEFAULT NULL COMMENT '执行开始时间',
                                                        `end_time` datetime(3) DEFAULT NULL COMMENT '执行结束时间',
                                                        `final_end_time` datetime(3) DEFAULT NULL COMMENT '执行完成时间',
                                                        `transaction_group_id` varchar(64) default '' COMMENT '事务组id',
                                                        `branch_Id` varchar(64) default '' COMMENT '分支id',
                                                        `transaction_operate` varchar(24) default '' COMMENT '事务操作类型',
                                                        PRIMARY KEY (`id`),
                                                        KEY `version_function_name` (`start_time`,`request_id`,`function_name`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='spider功能';

drop table spider_flow_example_log;
CREATE TABLE `spider_flow_example_log` (
                                           `id` varchar(64) NOT NULL COMMENT 'id',
                                           `request_param` varchar(6480) NOT NULL DEFAULT '' COMMENT '功能请求执行参数',
                                           `result_mapping` varchar(6480) DEFAULT '' COMMENT '返回的字段信息',
                                           `broker_name` varchar(128) DEFAULT '' COMMENT '执行机器节点的名称',
                                           `status` varchar(24) default '' COMMENT '执行状态',
                                           `exception` varchar(2480) default '' COMMENT '执行异常信息',
                                           `transaction_status` varchar(64) default '' COMMENT '事务状态',
                                           `function_id` varchar(64) NOT NULL DEFAULT '' COMMENT '功能id',
                                           `function_name` varchar(128) DEFAULT '' COMMENT '功能名称',
                                           `start_time` datetime(3) DEFAULT NULL COMMENT '执行开始时间',
                                           `end_time` datetime(3) DEFAULT NULL COMMENT '执行结束时间',
                                           `final_end_time` datetime(3) DEFAULT NULL COMMENT '执行完成时间',
                                           `transaction_group_id` varchar(64) default '' COMMENT '事务组id',
                                           `branch_Id` varchar(64) default '' COMMENT '分支id',
                                           `transaction_operate` varchar(24) default '' COMMENT '事务操作类型',
                                           `take_time` bigint default 0 COMMENT '耗时',
                                           PRIMARY KEY (`id`),
                                           KEY `version_function_name` (`start_time`,`function_name`,`status`),
                                           KEY `take_time` (`take_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin ROW_FORMAT=DYNAMIC COMMENT='spider功能';




