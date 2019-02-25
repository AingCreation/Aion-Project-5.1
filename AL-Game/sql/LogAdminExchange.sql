SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for log_command_add
-- ----------------------------
DROP TABLE IF EXISTS `log_exchange_admin`;
CREATE TABLE `log_exchange_admin` (
  `admin_id` int(11) NOT NULL DEFAULT '0',
  `admin_name` varchar(255) DEFAULT '',
  `player_id` int(11) DEFAULT '0',
  `player_name` varchar(255) DEFAULT '',
  `item_id` int(11) DEFAULT '0',
  `item_name` varchar(255) DEFAULT '',
  `item_count` varchar(255) DEFAULT '0',
  `description` varchar(255) DEFAULT '',
  `date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;