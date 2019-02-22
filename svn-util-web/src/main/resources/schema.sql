CREATE TABLE `config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(255) DEFAULT NULL,
  `project_desc` varchar(255) DEFAULT NULL,
  `svn_url` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `last_version` varchar(255) DEFAULT NULL,
  `start_version` varchar(255) DEFAULT NULL,
  `target_path` varchar(255) DEFAULT NULL,
  `project_version` varchar(255) DEFAULT NULL,
  `war_list` varchar(255) DEFAULT NULL,
  `self_lib_list` varchar(255) DEFAULT NULL,
  `out_lib_list` varchar(255) DEFAULT NULL,
  `save_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pack_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_name` varchar(255) DEFAULT NULL,
  `war_path` varchar(255) DEFAULT NULL,
  `record` varchar(2000) DEFAULT NULL,
  `create_time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

