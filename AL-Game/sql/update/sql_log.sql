/*
Navicat MySQL Data Transfer

Source Server         : new
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : aion_site

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2018-07-16 18:20:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for log_exchange_player
-- ----------------------------
DROP TABLE IF EXISTS `log_exchange_player`;
CREATE TABLE `log_exchange_player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NOT NULL DEFAULT '0',
  `player_name` varchar(255) DEFAULT '',
  `partner_id` int(11) DEFAULT '0',
  `partner_name` varchar(255) DEFAULT '',
  `item_id` int(11) DEFAULT '0',
  `item_name` varchar(255) DEFAULT '',
  `item_count` varchar(255) DEFAULT '0',
  `description` varchar(255) DEFAULT '',
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_mail_admin
-- ----------------------------
DROP TABLE IF EXISTS `log_mail_admin`;
CREATE TABLE `log_mail_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_id` int(11) NOT NULL DEFAULT '0',
  `admin_name` varchar(255) DEFAULT '',
  `item_id` int(11) DEFAULT '0',
  `item_name` varchar(255) DEFAULT '',
  `item_count` varchar(255) DEFAULT '0',
  `player_recive_name` varchar(255) DEFAULT '',
  `description` varchar(255) DEFAULT '',
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for log_mail_player
-- ----------------------------
DROP TABLE IF EXISTS `log_mail_player`;
CREATE TABLE `log_mail_player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) NOT NULL DEFAULT '0',
  `sender_name` varchar(255) DEFAULT '',
  `item_id` int(11) DEFAULT '0',
  `item_name` varchar(255) DEFAULT '',
  `item_count` varchar(255) DEFAULT '0',
  `player_recive_name` varchar(255) DEFAULT '',
  `description` varchar(255) DEFAULT '',
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;