-- ----------------------------
-- Table structure for `donation_converts`
-- ----------------------------
DROP TABLE IF EXISTS `donation_converts`;
CREATE TABLE `donation_converts` (
  `login` varchar(150) NOT NULL DEFAULT '',
  `charId` int(15) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '',
  `action` varchar(100) NOT NULL DEFAULT '',
  `date` varchar(50) NOT NULL DEFAULT '',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `item_ObjId` int(11) NOT NULL DEFAULT '0',
  `item_name` varchar(150) NOT NULL DEFAULT '',
  `count` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`action`,`date`,`item_id`,`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
