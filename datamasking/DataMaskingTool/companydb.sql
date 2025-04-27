-- MySQL dump 10.13  Distrib 8.0.41, for Linux (x86_64)
--
-- Host: localhost    Database: companydb
-- ------------------------------------------------------
-- Server version	8.0.41-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `dname` varchar(30) DEFAULT NULL,
  `dnumber` smallint NOT NULL,
  `mgr_ssn` char(9) DEFAULT NULL,
  `mgr_start_date` date DEFAULT NULL,
  PRIMARY KEY (`dnumber`),
  KEY `dep_fk` (`mgr_ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES ('Headquarters',1,'888665555','1981-05-22'),('Administration',4,'987654321','1995-05-22'),('Research',5,'333445555','1988-05-22');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dependent`
--

DROP TABLE IF EXISTS `dependent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dependent` (
  `essn` char(9) NOT NULL,
  `dependent_name` varchar(30) NOT NULL,
  `sex` char(1) DEFAULT NULL,
  `bdate` date DEFAULT NULL,
  `relationship` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`essn`,`dependent_name`),
  CONSTRAINT `fk_dep_essn` FOREIGN KEY (`essn`) REFERENCES `employee` (`ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dependent`
--

LOCK TABLES `dependent` WRITE;
/*!40000 ALTER TABLE `dependent` DISABLE KEYS */;
INSERT INTO `dependent` VALUES ('123456789','Alice','M','1988-04-05','Daughter'),('123456789','Elizabeth','M','1967-04-05','Spouse'),('123456789','Michael','M','1988-04-05','Son'),('333445555','Alice','F','1986-04-05','Daughter'),('333445555','Joy','F','1958-04-05','Spouse'),('333445555','Theodore','M','1983-04-05','Son'),('987654321','Abner','M','1942-04-05','Spouse');
/*!40000 ALTER TABLE `dependent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dept_locations`
--

DROP TABLE IF EXISTS `dept_locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dept_locations` (
  `dnumber` smallint NOT NULL,
  `dlocation` varchar(20) NOT NULL,
  PRIMARY KEY (`dnumber`,`dlocation`),
  CONSTRAINT `fk_dnumber` FOREIGN KEY (`dnumber`) REFERENCES `department` (`dnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dept_locations`
--

LOCK TABLES `dept_locations` WRITE;
/*!40000 ALTER TABLE `dept_locations` DISABLE KEYS */;
INSERT INTO `dept_locations` VALUES (1,'Houston'),(4,'Stafford'),(5,'Bellaire'),(5,'Houston'),(5,'Sugarland');
/*!40000 ALTER TABLE `dept_locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `fname` varchar(30) DEFAULT NULL,
  `minit` char(1) DEFAULT NULL,
  `lname` varchar(30) DEFAULT NULL,
  `ssn` char(9) NOT NULL,
  `bdate` date DEFAULT NULL,
  `address` varchar(30) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `salary` decimal(10,2) DEFAULT NULL,
  `super_ssn` char(9) DEFAULT NULL,
  `dno` smallint DEFAULT NULL,
  PRIMARY KEY (`ssn`),
  KEY `fk_super_ssn` (`super_ssn`),
  KEY `fk_dno` (`dno`),
  CONSTRAINT `fk_dno` FOREIGN KEY (`dno`) REFERENCES `department` (`dnumber`),
  CONSTRAINT `fk_super_ssn` FOREIGN KEY (`super_ssn`) REFERENCES `employee` (`ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES ('John','B','Smith','123456789','1965-01-09','731 Fondren, Houston, TX','M',30000.00,'333445555',5),('Franklin','T','Wong','333445555','1955-01-09','638 Fondren, Houston, TX','M',40000.00,'888665555',5),('Joyce','A','English','453453453','1972-01-09','5631 Fondren, Houston, TX','F',25000.00,'333445555',5),('Ramesh','K','Narayan','666884444','1962-01-09','975 Fondren, Houston, TX','M',38000.00,'333445555',5),('James','E','Borg','888665555','1937-01-09','450 Fondren, Houston, TX','M',55000.00,NULL,1),('Jennifer','S','Wallace','987654321','1941-01-09','21 Fondren, Houston, TX','F',43000.00,'888665555',4),('Ahmad','V','Jabbar','987987987','1969-01-09','980 Fondren, Houston, TX','M',25000.00,'987654321',4),('Alicia','J','Zelaya','999887777','1968-01-09','3321 Fondren, Houston, TX','F',25000.00,'987654321',4);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `project` (
  `pname` varchar(30) DEFAULT NULL,
  `pnumber` smallint NOT NULL,
  `plocation` varchar(30) DEFAULT NULL,
  `dnum` smallint DEFAULT NULL,
  PRIMARY KEY (`pnumber`),
  KEY `fk_dnum` (`dnum`),
  CONSTRAINT `fk_dnum` FOREIGN KEY (`dnum`) REFERENCES `department` (`dnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES ('ProductX',1,'Bellaire',5),('ProductY',2,'Sugarland',5),('ProductZ',3,'Houston',5),('Computerization',10,'Stafford',4),('Reorganization',20,'Houston',1),('Newbenefits',30,'Stafford',4);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `works_on`
--

DROP TABLE IF EXISTS `works_on`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `works_on` (
  `essn` char(9) NOT NULL,
  `pno` smallint NOT NULL,
  `hours` decimal(4,2) DEFAULT NULL,
  PRIMARY KEY (`essn`,`pno`),
  KEY `fk_pno` (`pno`),
  CONSTRAINT `fk_essn` FOREIGN KEY (`essn`) REFERENCES `employee` (`ssn`),
  CONSTRAINT `fk_pno` FOREIGN KEY (`pno`) REFERENCES `project` (`pnumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `works_on`
--

LOCK TABLES `works_on` WRITE;
/*!40000 ALTER TABLE `works_on` DISABLE KEYS */;
INSERT INTO `works_on` VALUES ('123456789',1,32.50),('123456789',2,7.50),('123456789',3,45.00),('333445555',2,10.00),('333445555',3,10.00),('333445555',10,10.00),('333445555',20,10.00),('453453453',1,20.00),('453453453',2,20.00),('666884444',1,30.00),('666884444',3,40.00),('987654321',20,15.00),('987654321',30,20.00),('987987987',10,35.00),('987987987',30,5.00),('999887777',10,10.00),('999887777',30,30.00);
/*!40000 ALTER TABLE `works_on` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-27 16:43:33
