-- ----------------------------
-- Table structure for npcbuffer_scheme_contents
-- ----------------------------
DROP TABLE IF EXISTS `npcbuffer_scheme_contents`;
CREATE TABLE `npcbuffer_scheme_contents`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scheme_id` int(11) NULL DEFAULT NULL,
  `skill_id` int(8) NULL DEFAULT NULL,
  `buff_class` int(2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of npcbuffer_scheme_contents
-- ----------------------------
INSERT INTO `npcbuffer_scheme_contents` VALUES (30, 7, 1036, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (31, 7, 1040, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (32, 7, 1043, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (33, 7, 1044, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (34, 7, 1045, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (35, 7, 1047, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (36, 7, 1048, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (37, 7, 1059, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (38, 7, 1068, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (39, 7, 1077, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (40, 7, 1085, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (41, 7, 1086, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (42, 7, 1087, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (43, 7, 1204, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (44, 7, 1240, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (45, 7, 1242, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (46, 7, 1243, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (47, 7, 1257, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (48, 7, 1268, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (49, 7, 1303, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (50, 7, 264, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (51, 7, 265, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (52, 7, 266, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (53, 7, 267, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (54, 7, 268, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (55, 7, 269, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (56, 7, 270, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (57, 7, 304, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (58, 7, 305, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (59, 7, 306, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (60, 7, 308, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (61, 7, 349, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (62, 9, 1036, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (63, 9, 1043, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (64, 9, 1045, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (65, 9, 1077, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (66, 9, 1303, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (67, 9, 1460, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (68, 10, 1036, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (69, 10, 1043, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (70, 10, 1040, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (77, 12, 1043, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (78, 13, 1040, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (79, 13, 1059, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (80, 13, 1204, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (81, 13, 264, 1);
INSERT INTO `npcbuffer_scheme_contents` VALUES (82, 13, 1389, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (83, 13, 311, 2);
INSERT INTO `npcbuffer_scheme_contents` VALUES (84, 13, 365, 2);
INSERT INTO `npcbuffer_scheme_contents` VALUES (85, 13, 310, 2);
INSERT INTO `npcbuffer_scheme_contents` VALUES (86, 14, 1036, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (87, 14, 1040, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (88, 14, 1045, 0);
INSERT INTO `npcbuffer_scheme_contents` VALUES (89, 14, 1363, 7);

SET FOREIGN_KEY_CHECKS = 1;
