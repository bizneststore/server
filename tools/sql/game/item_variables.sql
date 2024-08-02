-- ----------------------------
-- Table structure for item_variables
-- ----------------------------
DROP TABLE IF EXISTS `item_variables`;
CREATE TABLE `item_variables`  (
  `itemId` int(10) UNSIGNED NOT NULL,
  `var` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `val` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`itemId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
