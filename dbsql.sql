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
  `name` varchar(255) NULL,
  `file_name` varchar(255) NULL,
  `md5` varchar(255) NULL,
  `size` int NULL,
  `url` varchar(255) NULL,
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