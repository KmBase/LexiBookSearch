-- 创建数据库
CREATE DATABASE IF NOT EXISTS LPMS DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE LPMS;

-- 用户表
CREATE TABLE IF NOT EXISTS lp_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    user_account VARCHAR(256) NOT NULL UNIQUE COMMENT '账号',
    user_password VARCHAR(512) NOT NULL COMMENT '密码',
    user_name VARCHAR(256) NOT NULL COMMENT '用户名',
    user_email VARCHAR(256) COMMENT '邮箱',
    user_login_num INT DEFAULT 0 COMMENT '登录次数',
    user_avatar_id VARCHAR(256) COMMENT '头像ID',
    user_role VARCHAR(256) DEFAULT 'user' COMMENT '用户角色：user/admin',
    user_state INT DEFAULT 0 COMMENT '用户状态：0-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_user_account(user_account)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT '用户表' COLLATE = utf8mb4_unicode_ci;

-- 图书表
CREATE TABLE IF NOT EXISTS lp_book (
  id bigint UNSIGNED NOT NULL COMMENT '书籍的唯一标识符',
  title varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  sub_title varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '副标题',
  author varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  publisher varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  publication_year varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版年份',
  publication_date varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版日期',
  isbn varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '国际标准书号',
  category varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书籍分类',
  key_word varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主题词/关键词',
  summary text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '书籍摘要',
  note text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `source` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '来源',
  file_name text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'minio文件储存名称，唯一',
  pic_url text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '封面链接',
  is_ocr int NULL DEFAULT NULL COMMENT '是否ocr',
  status int NULL DEFAULT NULL COMMENT '状态',
  page_size int NULL DEFAULT NULL COMMENT '页数',
  create_time datetime NULL DEFAULT NULL COMMENT '创建时间',
  modified_time datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  md5 varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'md5',
  cn varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '中图分类号',
  series varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '众编',
  score int NULL DEFAULT NULL COMMENT '评分；带不可消除的水印的0，低质量的1，高质量的5',
  is_extracted int NULL DEFAULT NULL COMMENT '是否已经抽取文本',
  is_indexed int NULL DEFAULT 0 COMMENT '是否进行elasticsearch索引',
  topic text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'opac主题',
  opac_series text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'opac丛编',
  is_opaced int NULL DEFAULT NULL COMMENT '是否已获取opac',
  isbn_format varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'isbn_format',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_title`(`title`) USING BTREE,
  INDEX `idx_isbn`(`isbn`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书管理表' ROW_FORMAT = Dynamic;

-- 图书封面img表
CREATE TABLE IF NOT EXISTS lp_img (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
    `img_data` longtext NOT NULL COMMENT '图片base64数据',
    `book_id` bigint NOT NULL COMMENT '图书ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书封面图片表';

-- 图书章节表
CREATE TABLE IF NOT EXISTS `lp_book_section` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `book_id` bigint NULL DEFAULT NULL COMMENT '书籍id',
  `section_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '分段的文本内容',
  `page_num` int NULL DEFAULT NULL COMMENT '分段所在的页码',
  `coordinates` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分段坐标',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书章节表' ROW_FORMAT = Dynamic;

-- 主题表
CREATE TABLE IF NOT EXISTS `lp_topic` (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '标签名称',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级标签ID',
  `level` int NULL DEFAULT NULL COMMENT '层级',
  `type` varchar(50) DEFAULT NULL COMMENT '标签类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modified_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` int NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_level`(`level`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- 书籍与主题关联表
CREATE TABLE IF NOT EXISTS lp_book_topic (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '书籍与主题关联表ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    topic_id BIGINT NOT NULL COMMENT '主题ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_book_id(book_id),
    INDEX idx_topic_id(topic_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍与主题关联表';