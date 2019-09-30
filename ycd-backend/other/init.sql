-- MySQL dump 10.13  Distrib 5.7.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: chat_im
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nick` varchar(100) DEFAULT NULL,
  `status` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_role_menu`
--

DROP TABLE IF EXISTS `t_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_role_menu` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `role_id` varchar(50) NOT NULL,
  `menu_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_document_mapping`
--

DROP TABLE IF EXISTS `t_document_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_document_mapping` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `document_id` bigint(20) NOT NULL,
  `business_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_menu`
--

DROP TABLE IF EXISTS `t_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_menu` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `perms` varchar(50) DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL,
  `type` varchar(2) NOT NULL,
  `order_num` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_role`
--

DROP TABLE IF EXISTS `t_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_role` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `name` varchar(50) NOT NULL,
  `remark` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_dept`
--

DROP TABLE IF EXISTS `t_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dept` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `dept_name` varchar(100) NOT NULL,
  `order_num` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_user_role`
--

DROP TABLE IF EXISTS `t_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user_role` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `role_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_dic`
--

DROP TABLE IF EXISTS `t_dic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dic` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT '0',
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `name` varchar(20) NOT NULL,
  `dic_key` varchar(30) NOT NULL,
  `dic_value` varchar(30) NOT NULL,
  `dic_type` varchar(2) NOT NULL,
  `parent_id` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_document`
--

DROP TABLE IF EXISTS `t_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_document` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `create_time` varchar(30) NOT NULL,
  `update_time` varchar(30) NOT NULL,
  `create_user_id` varchar(30) NOT NULL,
  `update_user_id` varchar(30) NOT NULL,
  `name` varchar(500) NOT NULL,
  `path` varchar(550) NOT NULL,
  `size` bigint(20) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `status` varchar(2) DEFAULT NULL,
  `file_suffix` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-29 10:25:13
