/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 100113
Source Host           : localhost:3306
Source Database       : anu

Target Server Type    : MYSQL
Target Server Version : 100113
File Encoding         : 65001

Date: 2018-06-29 16:09:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for transaction_convert_history
-- ----------------------------
DROP TABLE IF EXISTS `transaction_convert_history`;
CREATE TABLE `transaction_convert_history` (
  `account_id` int(11) NOT NULL,
  `player_id` int(11) DEFAULT NULL,
  `player_name` varchar(255) DEFAULT NULL,
  `price_id` bigint(255) DEFAULT NULL,
  `from_val` bigint(255) DEFAULT NULL,
  `value` bigint(255) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `type` varchar(255) DEFAULT 'NONE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of transaction_convert_history
-- ----------------------------
INSERT INTO `transaction_convert_history` VALUES ('2210', '513174', 'Inori', '1', '20000', '200', '2017-10-22 13:09:34', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2223', '513328', 'Cimory', '1', '20000', '200', '2017-10-22 13:10:16', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2244', '377875', 'Coli', '1', '10000', '100', '2017-10-22 13:43:32', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2236', '357092', 'Colesom', '1', '10000', '100', '2017-10-23 17:32:26', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2143', '359395', 'Munchausen', '0', '6000000', '6000', '2017-10-29 16:27:15', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2143', '359395', 'Munchausen', '0', '4000000', '4000', '2017-10-29 16:27:26', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2143', '359395', 'Munchausen', '1', '20000', '200', '2017-10-29 16:28:05', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2143', '359395', 'Munchausen', '0', '6000000', '6000', '2017-10-29 16:28:45', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2143', '359395', 'Munchausen', '1', '10000', '100', '2017-10-29 16:29:10', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '0', '6000000', '6000', '2017-11-02 22:31:44', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '0', '4000000', '4000', '2017-11-02 22:31:56', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '1', '10000', '100', '2017-11-02 22:32:21', 'GC');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401632', 'Bee', '0', '6000000', '6000', '2017-11-02 22:36:04', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '0', '6000000', '6000', '2017-11-02 22:37:21', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '0', '6000000', '6000', '2017-11-02 22:38:27', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '0', '6000000', '6000', '2017-11-02 22:39:03', 'GP');
INSERT INTO `transaction_convert_history` VALUES ('2280', '401645', 'Munchausen', '1', '10000', '100', '2017-11-02 22:39:26', 'GC');
SET FOREIGN_KEY_CHECKS=1;
