-- ----------------------------
-- Table structure for character_daily_rewards
-- ----------------------------
DROP TABLE IF EXISTS `character_daily_rewards`;
CREATE TABLE `character_daily_rewards`  (
  `charId` int(10) UNSIGNED NOT NULL,
  `rewardId` int(3) UNSIGNED NOT NULL,
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1,
  `reset_time` tinyint(1) NULL DEFAULT NULL,
  `progress` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `lastCompleted` bigint(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`charId`, `rewardId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;
