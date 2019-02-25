/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 100113
Source Host           : localhost:3306
Source Database       : anu

Target Server Type    : MYSQL
Target Server Version : 100113
File Encoding         : 65001

Date: 2018-06-28 10:44:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for weddings
-- ----------------------------
DROP TABLE IF EXISTS `weddings`;
CREATE TABLE `weddings` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NOT NULL,
  `partner_id` int(11) NOT NULL,
  `teleport_time` timestamp NULL DEFAULT NULL,
  `teleport_time_partner` timestamp NULL DEFAULT NULL,
  `wedding_time` timestamp NULL DEFAULT NULL,
  `wedding_data` timestamp NULL DEFAULT NULL,
  `text` text,
  `p_text` text,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `player_id` (`player_id`),
  KEY `partner_id` (`partner_id`),
  CONSTRAINT `weddings_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `weddings_ibfk_2` FOREIGN KEY (`partner_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of weddings
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
