-- ----------------------------
-- Table structure for krateis_cube
-- ----------------------------
DROP TABLE IF EXISTS `krateis_cube`;
CREATE TABLE `krateis_cube`  (
  `charId` int(10) UNSIGNED NOT NULL,
  `played_matchs` int(10) NOT NULL,
  `total_kills` int(10) NOT NULL,
  `total_coins` double(10, 0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`) USING BTREE,
  CONSTRAINT `krateis_cube_ibfk_1` FOREIGN KEY (`charId`) REFERENCES `characters` (`charId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
