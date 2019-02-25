/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : f2p_games

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2018-06-04 05:40:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for log_command_add
-- ----------------------------
DROP TABLE IF EXISTS `log_command_add`;
CREATE TABLE `log_command_add` (
  `admin_id` int(11) NOT NULL DEFAULT '0',
  `admin_name` varchar(255) DEFAULT '',
  `player_id` int(11) DEFAULT '0',
  `player_name` varchar(255) DEFAULT '',
  `item_unique` int(255) DEFAULT '0',
  `item_id` int(11) DEFAULT '0',
  `item_name` varchar(255) DEFAULT '',
  `item_count` varchar(255) DEFAULT '0',
  `description` varchar(255) DEFAULT '',
  `date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_combat_points
-- ----------------------------
DROP TABLE IF EXISTS `player_combat_points`;
CREATE TABLE `player_combat_points` (
  `player_id` int(11) NOT NULL,
  `slot_id` int(11) NOT NULL,
  `cp_point` int(3) NOT NULL DEFAULT '1',
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`player_id`,`slot_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
