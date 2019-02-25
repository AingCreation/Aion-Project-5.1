/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 100113
Source Host           : localhost:3306
Source Database       : anu

Target Server Type    : MYSQL
Target Server Version : 100113
File Encoding         : 65001

Date: 2018-06-28 10:44:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for weddings_log
-- ----------------------------
DROP TABLE IF EXISTS `weddings_log`;
CREATE TABLE `weddings_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `player_id` int(11) NOT NULL,
  `partner_name` text,
  `wedding_start` timestamp NULL DEFAULT NULL,
  `wedding_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of weddings_log
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
