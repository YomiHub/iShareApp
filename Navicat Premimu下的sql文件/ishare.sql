/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50724
Source Host           : localhost:3306
Source Database       : ishare

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-05-22 14:22:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `comment_id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_user` varchar(255) DEFAULT NULL,
  `comment_info` int(11) DEFAULT NULL,
  `comment_detail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES ('8', 'hym', '30', '皮');
INSERT INTO `comment` VALUES ('9', 'hym', '30', '皮');
INSERT INTO `comment` VALUES ('10', 'hym', '12', '皮');
INSERT INTO `comment` VALUES ('11', 'hym', '13', '美美的');
INSERT INTO `comment` VALUES ('12', 'hym', '12', '超级好听');
INSERT INTO `comment` VALUES ('13', 'hym', '10', '挺喜欢的照片');
INSERT INTO `comment` VALUES ('14', 'hym', '5', '公主殿下');
INSERT INTO `comment` VALUES ('15', 'hym', '3', '每一天都要好好的');
INSERT INTO `comment` VALUES ('16', 'wyx', '13', '今天也要加油');
INSERT INTO `comment` VALUES ('17', 'wyx', '12', '好看好看');
INSERT INTO `comment` VALUES ('18', 'wyx', '10', '必须收藏');
INSERT INTO `comment` VALUES ('19', 'wyx', '4', '赞');
INSERT INTO `comment` VALUES ('20', 'wyx', '2', '哈哈哈哈。超美，超好听');
INSERT INTO `comment` VALUES ('21', 'wyx', '5', '我的我的');
INSERT INTO `comment` VALUES ('22', 'fjx', '12', '喜欢喜欢');
INSERT INTO `comment` VALUES ('23', 'hym', '5', '好听好听');
INSERT INTO `comment` VALUES ('25', 'hym', '31', 'hym');

-- ----------------------------
-- Table structure for focus
-- ----------------------------
DROP TABLE IF EXISTS `focus`;
CREATE TABLE `focus` (
  `focus_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `info_id` int(11) NOT NULL,
  PRIMARY KEY (`focus_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of focus
-- ----------------------------
INSERT INTO `focus` VALUES ('18', 'hym', '12');
INSERT INTO `focus` VALUES ('19', 'hym', '10');
INSERT INTO `focus` VALUES ('20', 'hym', '3');
INSERT INTO `focus` VALUES ('21', 'wyx', '12');
INSERT INTO `focus` VALUES ('22', 'wyx', '2');
INSERT INTO `focus` VALUES ('23', 'wyx', '5');

-- ----------------------------
-- Table structure for info
-- ----------------------------
DROP TABLE IF EXISTS `info`;
CREATE TABLE `info` (
  `info_id` int(11) NOT NULL AUTO_INCREMENT,
  `info_title` varchar(255) NOT NULL,
  `info_describe` varchar(255) NOT NULL,
  `info_detail` varchar(255) DEFAULT NULL,
  `info_type` int(11) NOT NULL,
  `info_support` int(11) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of info
-- ----------------------------
INSERT INTO `info` VALUES ('1', '无意间捕获', 'test1.jpeg', '分享图片的详情1，<br>分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情,<br><br>分享图片的详情, 分享图片的详情, 分享图片的详情, <br><br>分享图片的详情, <br>分享图片的详情, <br><br>分享图片的详情,', '1', '345', 'hym');
INSERT INTO `info` VALUES ('2', '慢冷', '梁静茹《慢冷》', 'https://www.bilibili.com/video/av52621182?spm_id_from=333.6.b_686967685f656e65726779.1', '2', '10', 'hym');
INSERT INTO `info` VALUES ('3', '分享日记1', '这是分享日记1的简述，这是分享日记的简述', '分享日记的详情1，<br>分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，<br><br><br>分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，分享日记的详情，<br>分享日记的详情，<br><br分享日记的详情，分享日记的详情，><br><br>分享日记的详情，<br><br>', '3', '65', 'hym');
INSERT INTO `info` VALUES ('4', '分享今日之照骗', 'test2.jpeg', '分享图片的详情2，<br>分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情,<br><br>分享图片的详情, 分享图片的详情, 分享图片的详情, <br><br>分享图片的详情, <br>分享图片的详情, <br><br>分享图片的详情,', '1', '344', 'wyx');
INSERT INTO `info` VALUES ('5', '大笨钟', '周杰伦《大笨钟》', 'https://www.bilibili.com/video/av49810797/?spm_id_from=333.788.videocard.3', '2', '432', 'wyx');
INSERT INTO `info` VALUES ('6', '好看，好看的照片', 'test1.jpeg', '分享图片的详情3，<br>分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情,<br><br>分享图片的详情, 分享图片的详情, 分享图片的详情, <br><br>分享图片的详情, <br>分享图片的详情, <br><br>分享图片的详情,', '1', '347', 'fjx');
INSERT INTO `info` VALUES ('7', '这是分享趣事1', '分享趣事的简述文案，分享趣事的简述文案', '分享趣事的详情，<br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情,<br><br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, <br><br>分享趣事的详情, <br>分享趣事的详情, <br><br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, <br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情,', '0', '66', 'fjx');
INSERT INTO `info` VALUES ('9', '百看不厌', 'test1.jpeg', '分享图片的详情4，<br>分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情,<br><br>分享图片的详情, 分享图片的详情, 分享图片的详情, <br><br>分享图片的详情, <br>分享图片的详情, <br><br>分享图片的详情,', '1', '168', 'fjx');
INSERT INTO `info` VALUES ('10', '今日照片', 'test3.jpeg', '分享图片的详情5，<br>分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情,<br><br>分享图片的详情, 分享图片的详情, 分享图片的详情, <br><br>分享图片的详情, <br>分享图片的详情, <br><br>分享图片的详情,', '1', '80', 'wyx');
INSERT INTO `info` VALUES ('11', '这是分享趣事2', '分享趣事的简述文案，分享趣事的简述文案2', '分享趣事的详情2，<br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情,<br><br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, <br><br>分享趣事的详情, <br>分享趣事的详情, <br><br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, <br>分享趣事的详情, 分享趣事的详情, 分享趣事的详情, 分享趣事的详情,', '0', '58', 'hym');
INSERT INTO `info` VALUES ('12', '《最近的永远》', '光良《最近的永远》', 'https://www.bilibili.com/video/av52554433', '2', '44', 'hym');
INSERT INTO `info` VALUES ('13', '今天也美美的', 'test1.jpeg', '分享图片的详情6，<br>分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情, 分享图片的详情,<br><br>分享图片的详情, 分享图片的详情, 分享图片的详情, <br><br>分享图片的详情, <br>分享图片的详情, <br><br>分享图片的详情,', '1', '130', 'wyx');
INSERT INTO `info` VALUES ('31', '今天天气还是很热', '看到了有趣的人儿', '这是很热很热很热很热的一天～～～～', '0', '0', 'fjx');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `signature` varchar(255) DEFAULT NULL,
  `userlogimage` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('fjx', 'fjx', 'fjx', 'userImg.png');
INSERT INTO `user` VALUES ('hym', 'hym', '想要喝奶茶！！！', 'userImg1.png');
INSERT INTO `user` VALUES ('pjj', 'pjj', 'pjj', 'userImg1.png');
INSERT INTO `user` VALUES ('wyx', 'wyx', 'wyx', 'userImg.png');
INSERT INTO `user` VALUES ('zbl', 'zbl', 'zbl', 'userImg1.png');
