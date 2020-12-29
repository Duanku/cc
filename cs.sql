/*
 Navicat Premium Data Transfer

 Source Server         : sb
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : cs

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 29/12/2020 09:31:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for file_inf
-- ----------------------------
DROP TABLE IF EXISTS `file_inf`;
CREATE TABLE `file_inf`  (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件编号',
  `cate_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型编号',
  `dir_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件夹编号',
  `user_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户编号',
  `file_name` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名',
  `file_path` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件存储位',
  `file_size` int(11) NOT NULL COMMENT '文件大小(单位kb)',
  `file_upload_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '上传时间',
  `file_status` int(2) NOT NULL COMMENT '文件状态',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_inf
-- ----------------------------
INSERT INTO `file_inf` VALUES (1, NULL, NULL, '1', '2020-12-22_京东超市.png', 'D:\\迅雷下载\\2020-12-22_京东超市.png', 46171168, '2020-12-22', 1);
INSERT INTO `file_inf` VALUES (2, NULL, NULL, '1', '2020-12-22_spring-tool-suite-4-4.3.2.RELEASE-e4.12.0-win32.win32.x86_64.7z', 'D:\\迅雷下载\\2020-12-22_spring-tool-suite-4-4.3.2.RELEASE-e4.12.0-win32.win32.x86_64.7z', 412494472, '2020-12-22', 1);
INSERT INTO `file_inf` VALUES (3, NULL, NULL, '1', '2020-12-22_268046904-1-192.mp4', 'D:\\迅雷下载\\2020-12-22_268046904-1-192.mp4', 57858599, '2020-12-22', 1);
INSERT INTO `file_inf` VALUES (4, NULL, NULL, '1', '2020-12-24_login_background.jpg', 'D:\\迅雷下载\\2020-12-24_login_background.jpg', 133623, '2020-12-24', 1);
INSERT INTO `file_inf` VALUES (5, NULL, NULL, '1', '2020-12-24_spring-tool-suite-4-4.3.2.RELEASE-e4.12.0-win32.win32.x86_64.7z', 'D:\\迅雷下载\\2020-12-24_spring-tool-suite-4-4.3.2.RELEASE-e4.12.0-win32.win32.x86_64.7z', 412494472, '2020-12-24', 1);

-- ----------------------------
-- Table structure for m_permission
-- ----------------------------
DROP TABLE IF EXISTS `m_permission`;
CREATE TABLE `m_permission`  (
  `id` bigint(20) NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_permission
-- ----------------------------

-- ----------------------------
-- Table structure for m_resource
-- ----------------------------
DROP TABLE IF EXISTS `m_resource`;
CREATE TABLE `m_resource`  (
  `id` bigint(20) NOT NULL COMMENT '资源id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_resource
-- ----------------------------

-- ----------------------------
-- Table structure for m_user
-- ----------------------------
DROP TABLE IF EXISTS `m_user`;
CREATE TABLE `m_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `roles` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色',
  `status` int(11) NULL DEFAULT NULL,
  `created` datetime(0) NULL DEFAULT NULL,
  `last_login` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `UK_USERNAME`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_user
-- ----------------------------
INSERT INTO `m_user` VALUES (1, 'DKu', 'http://m.imeitou.com/uploads/allimg/2020050712/zux1ab3v4zb.jpeg', NULL, '96e79218965eb72c92a549dd5a330112', 'admin', 0, '2020-04-20 10:44:01', NULL);

-- ----------------------------
-- Table structure for m_vidoe
-- ----------------------------
DROP TABLE IF EXISTS `m_vidoe`;
CREATE TABLE `m_vidoe`  (
  `v_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `v_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `v_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存储地址',
  `v_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `v_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '播放地址',
  `created` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`v_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_vidoe
-- ----------------------------
INSERT INTO `m_vidoe` VALUES (1, '2020-11-21_9.12.psd', 'D:\\迅雷下载\\2020-11-21_9.12.psd', 6454840, NULL, NULL);
INSERT INTO `m_vidoe` VALUES (2, '9.12.psd', 'D:\\迅雷下载\\2020-11-21_9.12.psd', 6454840, NULL, NULL);
INSERT INTO `m_vidoe` VALUES (3, '登录.psd', 'D:\\迅雷下载\\2020-11-21_登录.psd', 3182461, NULL, '2020-11-21 13:55:26');
INSERT INTO `m_vidoe` VALUES (5, '2.psd', 'D:\\迅雷下载\\2020-11-21_2.psd', 2865008, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
