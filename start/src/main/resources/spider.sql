CREATE TABLE `spider_function` (
                                   `id` varchar(64)  NOT NULL COMMENT 'id' ,
                                   `name` varchar(32)  NOT NULL DEFAULT '' COMMENT '名称',
                                   `version` varchar(20) NOT NULL DEFAULT '' COMMENT '版本',
                                   `start_id` varchar(32) NOT NULL DEFAULT '' COMMENT '开始id',
                                   `status` varchar(10) NOT NULL DEFAULT '' COMMENT '状态',
                                   `desc` varchar(64) NOT NULL DEFAULT '' COMMENT '描述',
                                   `bpmn_name` varchar(64) NOT NULL DEFAULT '' COMMENT 'bpmn名称',
                                   `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2220 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='spider功能表';

CREATE TABLE `sdk` (
                       `id` varchar(64)  NOT NULL COMMENT 'id' ,
                       `jar_name` varchar(32)  NOT NULL DEFAULT '' COMMENT '名称',
                       `class_path` varchar(32)  NOT NULL DEFAULT '' COMMENT '类路径',
                       `url` varchar(128) NOT NULL DEFAULT '' COMMENT 'url',
                       `status` varchar(32) NOT NULL DEFAULT '' COMMENT '开始id',
                       `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2220 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='spider-sdk';


CREATE TABLE `bpmn` (
                        `id` varchar(64) NOT NULL COMMENT 'id',
                        `bpmn_name` varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
                        `url` varchar(128) NOT NULL DEFAULT '' COMMENT '版本',
                        `status` varchar(32) NOT NULL DEFAULT '' COMMENT '开始id',
                        `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                        PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='spider-bpmn';






spider-1.05
DROP TABLE spider_area;
create table spider_area (
                             `id` varchar(64)  NOT NULL COMMENT 'id' ,
                             `area_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域名称',
                             `desc` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域描述',
                             `sdk_url` varchar(128)  DEFAULT NULL DEFAULT '' COMMENT '领域sdk',
                             `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间'
)ENGINE=InnoDB AUTO_INCREMENT=2220 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='spider-功能域';

DROP TABLE spider_function_version;
create table spider_function_version (
                                         `id` varchar(64)  NOT NULL COMMENT 'id' ,
                                         `function_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能名称',
                                         `desc` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '领域描述',
                                         `version` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能版本',
                                         `function_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能id',
                                         `bpmn_url` varchar(128)  NOT NULL DEFAULT '' COMMENT '模型地址',
                                         `start_event_id` varchar(128)  NOT NULL DEFAULT '' COMMENT '功能启动id',
                                         `bpmn_name` varchar(128)  NOT NULL DEFAULT '' COMMENT '模型名称',
                                         `status` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '状态',
                                         `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间'
)ENGINE=InnoDB AUTO_INCREMENT=2220 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='spider功能';

DROP TABLE spider_function;

create table spider_function (
                                 `id` varchar(64)  NOT NULL COMMENT 'id' ,
                                 `function_name` varchar(64)  NOT NULL DEFAULT '' COMMENT '功能名称',
                                 `desc` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '领域描述',
                                 `director` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '负责人',
                                 `status` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '状态',
                                 `area_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域id',
                                 `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间'
)ENGINE=InnoDB AUTO_INCREMENT=2220 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='spider功能相关';

DROP TABLE spider_node;

create table spider_node (
                             `id` varchar(64)  NOT NULL COMMENT 'id' ,
                             `name` varchar(64)  NOT NULL DEFAULT '' COMMENT '节点名称',
                             `area_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域id',
                             `desc` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '领域描述',
                             `async` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '是否异步',
                             `task_component` varchar(64)  NOT NULL DEFAULT '' COMMENT '组件名称',
                             `task_service` varchar(64)  NOT NULL DEFAULT '' COMMENT '组件方法',
                             `status` varchar(32)  DEFAULT NULL DEFAULT '' COMMENT '状态',
                             `service_task_type` varchar(64)  DEFAULT NULL DEFAULT '' COMMENT '组件方法类型/NORMAL/POLL/DELAY/APPROVE',
                             `area_id` varchar(64)  NOT NULL DEFAULT '' COMMENT '领域id',
                             `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间'
)ENGINE=InnoDB AUTO_INCREMENT=2220 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='节点配置类';

