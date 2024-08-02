-- ----------------------------
-- Table structure for `donation_invoices`
-- ----------------------------
DROP TABLE IF EXISTS `donation_invoices`;
CREATE TABLE `donation_invoices` (
  `id` varchar(30) COLLATE utf8_bin NOT NULL,
  `productid` varchar(10) COLLATE utf8_bin NOT NULL,
  `userid` int(10) DEFAULT NULL,
  `datecreate` int(10) NOT NULL,
  `datepaid` int(10) DEFAULT NULL,
  `currency` varchar(5) COLLATE utf8_bin DEFAULT NULL,
  `amount` decimal(7,2) DEFAULT NULL,
  `payway` tinyint(2) NOT NULL,
  `paymentid` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `payerid` varchar(15) COLLATE utf8_bin DEFAULT NULL,
  `token` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `status` tinyint(2) DEFAULT '0',
  `userstatus` tinyint(2) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `statusid` (`status`) USING BTREE,
  KEY `userid` (`userid`) USING BTREE,
  KEY `paymentid` (`paymentid`) USING BTREE,
  KEY `productid` (`productid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of donation_invoices
-- ----------------------------
