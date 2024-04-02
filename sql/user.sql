CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户昵称',
  `user_account` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户账号',
  `avatar_url` varchar(1024) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户头像',
  `gender` int DEFAULT NULL COMMENT '性别',
  `passwd` varchar(512) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电话',
  `email` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT (now()) COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT (now()) COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `roles` int NOT NULL DEFAULT '0' COMMENT '用户身份，0普通用户，1管理员',
  `planet_code` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '星球编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci

