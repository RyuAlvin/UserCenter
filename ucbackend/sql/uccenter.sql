/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : uccenter

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 02/05/2026 09:34:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户的登录账号（用来登录系统的唯一标识，一旦注册，通常不可修改或修改流程非常严格）',
  `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别（0：未设置、1：男性、2：女性）',
  `userPassword` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `userStatus` tinyint NULL DEFAULT 1 COMMENT '用户状态（0：禁用、1：可用）',
  `userRole` tinyint NULL DEFAULT 1 COMMENT '用户角色（1：普通用户、2：管理者）',
  `isDelete` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `createTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ryuuu', 'ryuuu', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 2, '123', '11111111111', '111@gmail.com', 1, 1, 0, '2026-03-18 20:21:26', '2026-03-18 20:21:26');
INSERT INTO `user` VALUES (5, 'Test001', 'Test001', '', 1, 'f3e53385f5a8d666ffb9c541fe1b5967', '33333333333', '333@gmail.com', 1, 1, 0, '2026-03-21 17:07:51', '2026-03-21 17:07:51');
INSERT INTO `user` VALUES (6, 'Test002', 'Test002', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 1, 'f3e53385f5a8d666ffb9c541fe1b5967', '44444444444', '444@gmail.com', 0, 1, 0, '2026-03-21 17:07:51', '2026-03-21 17:07:51');
INSERT INTO `user` VALUES (7, 'TestDeleted', 'TestDeleted', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 2, 'f3e53385f5a8d666ffb9c541fe1b5967', '55555555555', '555@gmail.com', 1, 1, 1, '2026-03-21 17:07:51', '2026-03-21 17:07:51');
INSERT INTO `user` VALUES (8, 'admin', 'admin', 'https://prod-eurasian-res.popmart.com/default/20260120_114905_241448____1_____1200x1200.jpg', 1, 'f3e53385f5a8d666ffb9c541fe1b5967', '66666666666', '666@gmail.com', 1, 2, 0, '2026-03-21 17:07:51', '2026-03-21 17:07:51');
INSERT INTO `user` VALUES (9, 'Test003', 'Test003', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 2, 'f3e53385f5a8d666ffb9c541fe1b5967', '77777777777', '777@gmail.com', 1, 1, 0, '2026-03-25 23:37:02', '2026-03-25 23:37:02');
INSERT INTO `user` VALUES (10, 'Test004', 'Test004', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 1, 'f3e53385f5a8d666ffb9c541fe1b5967', '88888888888', '888@gmail.com', 1, 1, 0, '2026-03-26 10:14:45', '2026-03-26 10:14:45');
INSERT INTO `user` VALUES (11, 'Test005', 'Test005', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 2, 'f3e53385f5a8d666ffb9c541fe1b5967', '99999999999', '999@gmail.com', 1, 1, 0, '2026-03-26 10:40:22', '2026-03-26 10:40:22');
INSERT INTO `user` VALUES (12, 'Test006', 'Test006', 'https://prod-eurasian-res.popmart.com/default/20260224_160615_103509____1_____1200x1200.jpg', 0, 'f3e53385f5a8d666ffb9c541fe1b5967', '00000000000', '000@gmail.com', 1, 1, 0, '2026-03-26 10:50:09', '2026-03-26 10:50:09');

SET FOREIGN_KEY_CHECKS = 1;
