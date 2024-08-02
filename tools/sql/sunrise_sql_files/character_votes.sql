DROP TABLE IF EXISTS `character_votes`;
CREATE TABLE `character_votes`  (
  `value` char(100) NOT NULL DEFAULT '1',
  `value_type` tinyint(4) NOT NULL DEFAULT 1,
  `votetype` char(50) NOT NULL,
  `date_voted_website` bigint(10) NULL DEFAULT 1,
  `date_take_reward_in_game` bigint(10) NULL DEFAULT 1,
  `vote_count` int(3) NULL DEFAULT 1,
  PRIMARY KEY (`value`, `value_type`, `votetype`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
