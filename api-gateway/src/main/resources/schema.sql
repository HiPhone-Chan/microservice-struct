CREATE TABLE `user` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`activated` BIT(1) NOT NULL,
	`email` VARCHAR(100) NULL DEFAULT NULL,
	`nick_name` VARCHAR(50) NULL DEFAULT NULL,
	`first_name` VARCHAR(50) NULL DEFAULT NULL,
	`last_name` VARCHAR(50) NULL DEFAULT NULL,
	`image_url` VARCHAR(256) NULL DEFAULT NULL,
	`lang_key` VARCHAR(5) NULL DEFAULT NULL,
	`lock_date` DATETIME NULL DEFAULT NULL,
	`locked` BIT(1) NOT NULL,
	`activation_key` VARCHAR(20) NULL DEFAULT NULL,
    `activation_date` DATETIME NULL DEFAULT NULL,
	`failed_date` DATETIME NULL DEFAULT NULL,
	`failed_times` INT NULL DEFAULT NULL,
	`login` VARCHAR(50) NOT NULL,
	`id_card` VARCHAR(20) NULL DEFAULT NULL,
	`mobile` VARCHAR(63) NULL DEFAULT NULL,
	`password_hash` VARCHAR(60) NOT NULL,
	`reset_date` DATETIME NULL DEFAULT NULL,
	`reset_key` VARCHAR(20) NULL DEFAULT NULL,
    `created_by` VARCHAR(63) NOT NULL,
	`created_date` DATETIME NULL DEFAULT NULL,
	`last_modified_by` VARCHAR(63) NULL DEFAULT NULL,
	`last_modified_date` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UK_ew1hvam8uwaknuaellwhqchhb` (`login`),
	UNIQUE INDEX `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
)
;

CREATE TABLE `authority` (
	`name` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`name`)
)
;

CREATE TABLE `user_authority` (
	`user_id` BIGINT NOT NULL,
	`authority_name` VARCHAR(31) NOT NULL,
	PRIMARY KEY (`user_id`, `authority_name`),
	INDEX `FK6ktglpl5mjosa283rvken2py5` (`authority_name`)
)
;