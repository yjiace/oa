CREATE DATABASE `oa`;
ALTER DATABASE `oa` CHARACTER SET 'utf8';
CREATE TABLE `oa`.`t_sys_permission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NOT NULL,
  `name` varchar(50) NOT NULL,
  `val` varchar(50) NOT NULL,
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` varchar(50) NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_sys_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `comments` varchar(50),
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` varchar(50) NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_sys_user` (
  `user_name` varchar(50) NOT NULL,
  `status` varchar(1) NOT NULL DEFAULT 'Y',
  `password` varchar(255) NOT NULL,
  `name` varchar(50),
  `phone` varchar(20),
  `mobile` varchar(20),
  `company` varchar(255),
  `section` varchar(255),
  `position` varchar(255),
  `department` varchar(255),
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` varchar(50) NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`user_name`)
);


CREATE TABLE `oa`.`t_sys_role_permission` (
  `permission_id` int NOT NULL,
  `role_id` int NOT NULL
);

CREATE TABLE `oa`.`t_sys_user_role` (
  `role_id` int NOT NULL,
  `user_name` varchar(50) NOT NULL
);

CREATE TABLE `oa`.`t_sys_operation_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `module` varchar(255),
  `method` varchar(255),
  `package_and_method` varchar(255),
  `params` json NULL,
  `before_data` json NULL,
  `after_data` json NULL,
  `content` json NULL,
  `start_time` datetime,
  `end_time` datetime,
  `result_status` varchar(255),
  `result_msg` varchar(255),
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_attachment_file`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NULL,
  `upload_file_name` varchar(255) NULL,
  `md5` varchar(255) NULL,
  `size` int NULL,
  `swf_name` varchar(255) NULL,
  `pdf_name` varchar(255) NULL,
  `document_number` varchar(255) NULL,
  `security_classification` varchar(255) NULL,
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` varchar(50) NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_message_notification`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `initiator_username` varchar(50) NOT NULL,
  `recipient_username` varchar(50) NOT NULL,
  `status` varchar(10) NOT NULL DEFAULT 'unread',
  `content` text NOT NULL,
  `source` varchar(50) NULL,
  `create_time` datetime NOT NULL,
  `reading_time` datetime NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_car_record_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `car_record_id` int NOT NULL,
  `username` varchar(255) NULL,
  `type` varchar(20) NULL,
  `create_time` datetime NULL,
  `remarks` varchar(1024) NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_car_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `approval_id` int NULL,
  `status` varchar(20) NULL COMMENT '状态，NotInUse：未使用，NotLeaving：未离场，NotReturned：未归还',
  `create_time` datetime NULL,
  `username` varchar(255) NULL,
  `destination` varchar(255) NULL COMMENT '目的地',
  `remarks` varchar(1024) NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_vehicle_information`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NULL COMMENT '车辆名称',
  `number` varchar(255) NULL COMMENT '车辆编号',
  `plate_number` varchar(10) NULL COMMENT '车牌号',
  `company` varchar(10) NULL COMMENT '所属单位',
  `model` varchar(10) NULL COMMENT '车辆型号',
  `creator` varchar(50) NOT NULL,
  `current_car_record_id` int NULL,
  `create_time` datetime NOT NULL,
  `updater` varchar(50) NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_approval`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `initiator_username` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `type` varchar(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `remarks` varchar(1024) NULL,
  `number` varchar(50) NOT NULL,
  `sort` int NOT NULL,
  `vehicle_id` int NULL,
  `extra` json NULL,
  `destination` varchar(255) NULL COMMENT '目的地',
  `node_id` int NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_approval_node`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `approval_id` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `sort` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `completed_time` datetime,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_approval_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `approval_id` int NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_approval_file`  (
  `approval_id` int NOT NULL,
  `file_id` int NOT NULL
);

CREATE TABLE `oa`.`t_approval_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `approval_id` int NOT NULL,
  `operation_id` int,
  `operation` varchar(255) NOT NULL,
  `operation_message` text NULL,
  `operation_type` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `oa`.`t_sys_role_permission`
    ADD FOREIGN KEY (`permission_id`) REFERENCES `oa`.`t_sys_permission` (`id`),
    ADD FOREIGN KEY (`role_id`) REFERENCES `oa`.`t_sys_role` (`id`);

ALTER TABLE `oa`.`t_sys_user_role`
  ADD FOREIGN KEY (`role_id`) REFERENCES `oa`.`t_sys_role` (`id`),
  ADD FOREIGN KEY (`user_name`) REFERENCES `oa`.`t_sys_user` (`user_name`);

ALTER TABLE `oa`.`t_car_record`
  ADD FOREIGN KEY (`vehicle_id`) REFERENCES `oa`.`t_vehicle_information` (`id`),
  ADD FOREIGN KEY (`approval_id`) REFERENCES `oa`.`t_approval` (`id`);

ALTER TABLE `oa`.`t_vehicle_information`
  ADD FOREIGN KEY (`current_car_record_id`) REFERENCES `oa`.`t_car_record` (`id`);

ALTER TABLE `oa`.`t_car_record_log`
  ADD FOREIGN KEY (`car_record_id`) REFERENCES `oa`.`t_car_record` (`id`);

ALTER TABLE `oa`.`t_approval`
  ADD FOREIGN KEY (`node_id`) REFERENCES `oa`.`t_approval_node` (`id`);

ALTER TABLE `oa`.`t_approval_file`
  ADD FOREIGN KEY (`approval_id`) REFERENCES `oa`.`t_approval` (`id`),
  ADD FOREIGN KEY (`file_id`) REFERENCES `oa`.`t_attachment_file` (`id`);

ALTER TABLE `oa`.`t_approval_node`
  ADD FOREIGN KEY (`approval_id`) REFERENCES `oa`.`t_approval` (`id`);

ALTER TABLE `oa`.`t_approval_comment`
  ADD FOREIGN KEY (`approval_id`) REFERENCES `oa`.`t_approval` (`id`);

ALTER TABLE `oa`.`t_approval_log`
  ADD FOREIGN KEY (`approval_id`) REFERENCES `oa`.`t_approval` (`id`);




INSERT INTO `oa`.`t_sys_user`(`user_name`, `status`, `password`, `name`, `phone`, `mobile`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`, `company`, `section`, `position`, `department`) VALUES ('smallyoung', 'Y', '$2a$10$uB5Jm.R9i3cpycwJ8II.tubFJrQuT93mmO246inpXEnzFX9bN.1pS', NULL, NULL, NULL, 'smallyoung', '2020-11-03 21:23:25', 'smallyoung', '2020-11-11 07:20:33', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (1, 0, '用户管理', 'ROLE_USER', 'smallyoung', '2020-11-04 13:07:16', 'smallyoung', '2020-11-04 13:07:19', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (2, 1, '用户管理-查询', 'ROLE_USER_FIND', 'smallyoung', '2020-11-04 13:08:27', 'smallyoung', '2020-11-04 13:08:30', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (3, 1, '用户管理-编辑', 'ROLE_USER_SAVE', 'smallyoung', '2020-11-04 13:10:25', 'smallyoung', '2020-11-04 13:10:33', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (4, 1, '用户管理-更改状态', 'ROLE_USER_UPDATE_STATUS', 'smallyoung', '2020-11-04 13:11:55', 'smallyoung', '2020-11-04 13:11:57', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (5, 1, '用户管理-重置密码', 'ROLE_USER_RESET_PASSWORD', 'smallyoung', '2020-11-04 13:13:16', 'smallyoung', '2020-11-04 13:13:13', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (6, 1, '用户管理-删除', 'ROLE_USER_DELETE', 'smallyoung', '2020-11-04 13:16:03', 'smallyoung', '2020-11-04 13:16:05', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (7, 1, '用户管理-设置角色', 'ROLE_USER_UPDATE_ROLE', 'smallyoung', '2020-11-04 13:16:41', 'smallyoung', '2020-11-04 13:16:43', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (8, 0, '角色管理', 'ROLE_ROLE', 'smallyoung', '2020-11-04 13:18:25', 'smallyoung', '2020-11-04 13:18:28', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (9, 8, '角色管理-查询', ' ROLE_ROLE_FIND', 'smallyoung', '2020-11-04 13:18:55', 'smallyoung', '2020-11-04 13:18:59', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (10, 8, '角色管理-编辑', ' ROLE_ROLE_SAVE', 'smallyoung', '2020-11-04 13:18:55', 'smallyoung', '2020-11-04 13:18:59', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (11, 8, '角色管理-删除', ' ROLE_ROLE_DELETE', 'smallyoung', '2020-11-04 13:18:55', 'smallyoung', '2020-11-04 13:18:59', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (12, 0, '系统操作日志', 'ROLE_SYS_LOG', 'smallyoung', '2020-11-09 12:58:02', 'smallyoung', '2020-11-09 12:58:07', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (13, 12, '系统操作日志-查询', ' ROLE_SYS_LOG_FIND', 'smallyoung', '2020-11-09 12:58:15', 'smallyoung', '2020-11-09 12:58:12', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (14, 0, '消息管理', 'ROLE_MESSAGE_NOTIFICATION', 'smallyoung', '2020-11-15 13:37:14', 'smallyoung', '2020-11-15 13:37:19', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (15, 14, '消息管理-查询', 'ROLE_MESSAGE_NOTIFICATION_FIND', 'smallyoung', '2020-11-15 13:38:14', 'smallyoung', '2020-11-15 13:38:09', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (16, 0, '车辆管理', 'ROLE_ VEHICLE', 'smallyoung', '2020-11-19 15:24:21', 'smallyoung', '2020-11-19 15:24:24', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (17, 16, '车辆管理-查询', 'ROLE_ VEHICLE_FIND', 'smallyoung', '2020-11-19 15:24:46', 'smallyoung', '2020-11-19 15:24:38', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (18, 16, '车辆管理-编辑', 'ROLE_VEHICLE_SAVE', 'smallyoung', '2020-11-19 15:25:32', 'smallyoung', '2020-11-19 15:25:34', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (19, 16, '车辆管理-添加记录', 'ROLE_VEHICLE_RECORD', 'smallyoung', '2020-11-19 15:31:21', 'smallyoung', '2020-11-19 15:31:24', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (20, 16, '车辆管理-查询记录', 'ROLE_VEHICLE_RECORD_FIND', 'smallyoung', '2020-11-19 15:31:21', 'smallyoung', '2020-11-19 15:31:24', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (21, 16, '车辆管理-下载记录', 'ROLE_VEHICLE_RECORD_EXCEL', 'smallyoung', '2020-11-19 15:31:21', 'smallyoung', '2020-11-19 15:31:24', 'N');
INSERT INTO `oa`.`t_sys_permission`(`id`, `parent_id`, `name`, `val`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (22, 16, '车辆管理-删除', 'ROLE_VEHICLE_DELETE', 'smallyoung', '2020-11-19 15:31:21', 'smallyoung', '2020-11-19 15:31:24', 'N');
INSERT INTO `oa`.`t_sys_role`(`id`, `name`, `comments`, `creator`, `create_time`, `updater`, `update_time`, `is_delete`) VALUES (1, 'admin', NULL, 'smallyoung', '2020-11-03 21:23:39', 'smallyoung', '2020-11-03 21:23:41', 'N');
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (1, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (2, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (3, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (4, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (5, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (6, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (7, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (8, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (9, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (10, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (11, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (12, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (13, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (14, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (15, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (16, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (17, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (18, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (19, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (20, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (21, 1);
INSERT INTO `oa`.`t_sys_role_permission`(`permission_id`, `role_id`) VALUES (22, 1);
INSERT INTO `oa`.`t_sys_user_role`(`role_id`, `user_name`) VALUES (1, 'smallyoung');
