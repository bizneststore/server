ALTER TABLE `l2srgs`.`npcbuffer_buff_list` 
ADD COLUMN `buffLevelVip` int(5) NOT NULL DEFAULT 0 AFTER `buffLevel`;

ALTER TABLE `l2srgs`.`npcbuffer_scheme_contents` 
DROP COLUMN `skill_level`;