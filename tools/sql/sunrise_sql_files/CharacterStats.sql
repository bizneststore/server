-- ----------------------------
-- Table structure for `character_stats`
-- ----------------------------
DROP TABLE IF EXISTS `character_stats`;
CREATE TABLE `character_stats` (
  `charId` int(10) unsigned NOT NULL DEFAULT '0',
  `variable` varchar(255) NOT NULL,
  `value` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`charId`,`variable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
