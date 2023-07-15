SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `organisations`;
DROP TABLE IF EXISTS `people`;
DROP TABLE IF EXISTS `addresses`;
DROP TABLE IF EXISTS `people_social_media`;
DROP TABLE IF EXISTS `project`;
DROP TABLE IF EXISTS `social_media`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `organisations` (
    `organisation_id` INT NOT NULL,
    `name` VARCHAR(50),
    `addressline1` VARCHAR(50),
    `addressline2` VARCHAR(50),
    `addressline3` VARCHAR(50),
    `city` VARCHAR(50),
    `county` VARCHAR(50),
    `postcode` VARCHAR(50),
    `country` VARCHAR(50),
    PRIMARY KEY (`organisation_id`)
);

CREATE TABLE `people` (
    `person_id` INT NOT NULL,
    `lastname` VARCHAR(50),
    `middlenames` VARCHAR(50),
    `firstname` VARCHAR(50),
    `title` VARCHAR(50),
    PRIMARY KEY (`person_id`)
);

CREATE TABLE `addresses` (
    `address_id` INT NOT NULL,
    `person` VARCHAR(50),
    `addressline1` VARCHAR(50),
    `addressline2` VARCHAR(50),
    `addressline3` VARCHAR(50),
    `city` VARCHAR(50),
    `county` VARCHAR(50),
    `postcode` VARCHAR(50),
    `country` VARCHAR(50),
    PRIMARY KEY (`address_id`)
);

CREATE TABLE `people_social_media` (
    `person_id` INT NOT NULL,
    `social_media_id` INT NOT NULL,
    `username` VARCHAR(50),
    `URL` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`person_id`, `social_media_id`)
);

CREATE TABLE `project` (
    `project_id` INT NOT NULL,
    `project_name` VARCHAR(50),
    `startdate` VARCHAR(50),
    `stage_id` INT,
    `close_date` VARCHAR(50),
    PRIMARY KEY (`project_id`)
);

CREATE TABLE `social_media` (
    `socialmedia_id` INT NOT NULL,
    `name` VARCHAR(50),
    PRIMARY KEY (`socialmedia_id`)
);
