CREATE DATABASE `oa`;
ALTER DATABASE `oa` CHARACTER SET 'utf8';
CREATE TABLE `oa`.`t_sys_permission`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NOT NULL,
  `name` varchar(50) NOT NULL,
  `val` varchar(50) NOT NULL,
  `url` varchar(255) NOT NULL,
  `icon` varchar(50) NULL,
  `order_number` int NOT NULL,
  `type` int NOT NULL,
  `creator` int NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` int NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_sys_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `comments` varchar(50),
  `creator` int NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` int NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);

CREATE TABLE `oa`.`t_sys_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `status` varchar(1) NOT NULL DEFAULT 'Y',
  `password` varchar(255) NOT NULL,
  `creator` int NOT NULL,
  `create_time` datetime NOT NULL,
  `updater` int NOT NULL,
  `update_time` datetime NOT NULL,
  `is_delete` varchar(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
);


CREATE TABLE `oa`.`t_sys_role_permission` (
  `permission_id` int NOT NULL,
  `role_id` int NOT NULL
);

CREATE TABLE `oa`.`t_sys_user_role` (
  `role_id` int NOT NULL,
  `user_id` int NOT NULL
);

ALTER TABLE `oa`.`t_sys_role_permission`
    ADD FOREIGN KEY (`permission_id`) REFERENCES `oa`.`t_sys_permission` (`id`),
    ADD FOREIGN KEY (`role_id`) REFERENCES `oa`.`t_sys_role` (`id`);

ALTER TABLE `oa`.`t_sys_user_role`
  ADD FOREIGN KEY (`role_id`) REFERENCES `oa`.`t_sys_role` (`id`),
  ADD FOREIGN KEY (`user_id`) REFERENCES `oa`.`t_sys_user` (`id`);

