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
  `id` int NOT NULL AUTO_INCREMENT,
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
  `user_name` int NOT NULL
);

ALTER TABLE `oa`.`t_sys_role_permission`
    ADD FOREIGN KEY (`permission_id`) REFERENCES `oa`.`t_sys_permission` (`id`),
    ADD FOREIGN KEY (`role_id`) REFERENCES `oa`.`t_sys_role` (`id`);

ALTER TABLE `oa`.`t_sys_user_role`
  ADD FOREIGN KEY (`role_id`) REFERENCES `oa`.`t_sys_role` (`id`),
  ADD FOREIGN KEY (`user_name`) REFERENCES `oa`.`t_sys_user` (`user_name`);

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
  `md5` varchar(255) NULL,
  `size` int NULL,
  `url` varchar(255) NULL,
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

CREATE TABLE `t_car_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `vehicle_id` int NOT NULL,
  `create_time` datetime NULL,
  `username` varchar(255) NULL,
  `type` varchar(255) NULL,
  `remarks` varchar(255) NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `t_vehicle_information`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NULL COMMENT '车辆名称',
  `number` varchar(255) NULL COMMENT '车辆编号',
  `plate_number` varchar(10) NULL COMMENT '车牌号',
  `company` varchar(10) NULL COMMENT '所属单位',
  `model` varchar(10) NULL COMMENT '车辆型号',
  `status` varchar(20) NULL COMMENT '状态，NotInUse：未使用，NotLeaving：未离场，NotReturned：未归还',
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` varchar(50) NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

ALTER TABLE `oa`.`t_car_record`
  ADD FOREIGN KEY (`vehicle_id`) REFERENCES `oa`.`t_vehicle_information` (`id`),
  ADD FOREIGN KEY (`username`) REFERENCES `oa`.`t_sys_user` (`user_name`);

CREATE TABLE `oa`.`t_document_approval`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `initiator_username` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `sort` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_document_approval_node`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `document_approval_id` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `sort` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `completed_time` datetime,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_document_approval_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `creator` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `document_approval_id` int NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_document_approval_file`  (
  `document_approval` int NOT NULL,
  `file_id` int NOT NULL
);

CREATE TABLE `oa`.`t_document_approval_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `document_approval_id` int NOT NULL,
  `operation_id` int,
  `operation` varchar(255) NOT NULL,
  `operation_message` text NOT NULL,
  `operation_type` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `oa`.`t_document_approval_file`
  ADD FOREIGN KEY (`document_approval`) REFERENCES `oa`.`t_document_approval` (`id`),
  ADD FOREIGN KEY (`file_id`) REFERENCES `oa`.`t_attachment_file` (`id`);

ALTER TABLE `oa`.`t_document_approval_node`
  ADD FOREIGN KEY (`document_approval_id`) REFERENCES `oa`.`t_document_approval` (`id`);

ALTER TABLE `oa`.`t_document_approval_comment`
  ADD FOREIGN KEY (`document_approval_id`) REFERENCES `oa`.`t_document_approval` (`id`);

ALTER TABLE `oa`.`t_document_approval_log`
  ADD FOREIGN KEY (`document_approval_id`) REFERENCES `oa`.`t_document_approval` (`id`);