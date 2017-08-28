-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Oct 13, 2016 at 11:24 AM
-- Server version: 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `is203`
--
CREATE DATABASE IF NOT EXISTS is203 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE is203;

-- --------------------------------------------------------

--
-- Table structure for table `bid`
--

CREATE TABLE IF NOT EXISTS `bid` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  `Section_id` varchar(3) NOT NULL,
  `Round` int(11) NOT NULL,
  `Status` varchar(10) NOT NULL,
  `BidAmount` double DEFAULT NULL,
  PRIMARY KEY (`User_id`,`Course_id`,`Section_id`,`Round`),
  KEY `ID_Course_idx` (`Course_id`,`Section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `bid`
--

INSERT INTO `bid` (`User_id`, `Course_id`, `Section_id`, `Round`, `Status`, `BidAmount`) VALUES
('ben.ng.2009', 'IS100', 'S1', 2, 'Pending', 11),
('calvin.ng.2009', 'IS100', 'S1', 2, 'Pending', 12),
('dawn.ng.2009', 'IS100', 'S1', 2, 'Pending', 13),
('eddy.ng.2009', 'IS100', 'S1', 2, 'Pending', 14),
('fred.ng.2009', 'IS100', 'S1', 2, 'Pending', 15),
('harry.ng.2009', 'IS100', 'S1', 2, 'Pending', 17),
('ian.ng.2009', 'IS100', 'S1', 2, 'Pending', 18),
('larry.ng.2009', 'IS100', 'S1', 2, 'Pending', 19),
('maggie.ng.2009', 'IS100', 'S1', 2, 'Pending', 20),
('neilson.ng.2009', 'IS100', 'S1', 2, 'Pending', 21),
('olivia.ng.2009', 'IS100', 'S1', 2, 'Pending', 22),
('parker.ng.2009', 'IS100', 'S1', 2, 'Pending', 24),
('quiten.ng.2009', 'IS100', 'S1', 2, 'Pending', 24),
('ricky.ng.2009', 'IS100', 'S1', 2, 'Pending', 24),
('steven.ng.2009', 'IS100', 'S1', 2, 'Pending', 26),
('timothy.ng.2009', 'IS100', 'S1', 2, 'Pending', 27),
('ursala.ng.2009', 'IS100', 'S1', 2, 'Pending', 28),
('valarie.ng.2009', 'IS100', 'S1', 2, 'Pending', 29),
('winston.ng.2009', 'IS100', 'S1', 2, 'Pending', 30),
('xavier.ng.2009', 'IS100', 'S1', 2, 'Pending', 31),
('yasir.ng.2009', 'IS100', 'S1', 2, 'Pending', 32),
('zac.ng.2009', 'IS100', 'S1', 2, 'Pending', 33);

-- --------------------------------------------------------

--
-- Table structure for table `bidding_cart`
--

CREATE TABLE IF NOT EXISTS `bidding_cart` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  `Section_id` char(3) NOT NULL,
  PRIMARY KEY (`User_id`,`Course_id`,`Section_id`),
  KEY `SectionID_idx` (`Course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bidding_round`
--

CREATE TABLE IF NOT EXISTS `bidding_round` (
  `status` varchar(10) NOT NULL,
  `current_round` int(11) NOT NULL,
  PRIMARY KEY (`current_round`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `bidding_round`
--

INSERT INTO `bidding_round` (`status`, `current_round`) VALUES
('started', 2);

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE IF NOT EXISTS `course` (
  `Course_id` varchar(25) NOT NULL,
  `School` char(4) NOT NULL,
  `Title` varchar(100) NOT NULL,
  `Description` varchar(1000) NOT NULL,
  `Exam_Date` date NOT NULL,
  `Exam_Start` time NOT NULL,
  `Exam_End` time NOT NULL,
  PRIMARY KEY (`Course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`Course_id`, `School`, `Title`, `Description`, `Exam_Date`, `Exam_Start`, `Exam_End`) VALUES
('ECON001', 'SOE', 'Microeconomics', 'Microeconomics is about economics in smaller scale (e.g. firm-scale)', '2013-11-01', '15:30:00', '18:45:00'),
('ECON002', 'SOE', 'Macroeconomics', 'You don''t learn about excel macros here.', '2013-11-01', '08:30:00', '11:45:00'),
('IS100', 'SIS', 'Calculus ', 'The basic objective of Calculus is to relate small-scale (differential) quantities to large-scale (integrated) quantities. This is accomplished by means of the Fundamental Theorem of Calculus. Students should demonstrate an understanding of the integral as a cumulative sum, of the derivative as a rate of change, and of the inverse relationship between integration and differentiation.', '2013-11-19', '08:30:00', '11:45:00'),
('IS101', 'SIS', 'Advanced Calculus', 'This is a second course on calculus. It is more advanced definitely.', '2013-11-18', '12:00:00', '15:15:00'),
('IS102', 'SIS', 'Java programming', 'This course teaches you on Java programming. I love Java definitely.', '2013-11-17', '15:30:00', '18:45:00'),
('IS103', 'SIS', 'Web Programming', 'JSP, Servlets using Tomcat', '2013-11-16', '08:30:00', '11:45:00'),
('IS104', 'SIS', 'Advanced Programming', 'How to write code that nobody can understand', '2013-11-15', '12:00:00', '15:15:00'),
('IS105', 'SIS', 'Data Structures', 'Data structure is a particular way of storing and organizing data in a computer so that it can be used efficiently. Arrays, Lists, Stacks and Trees will be covered.', '2013-11-14', '15:30:00', '18:45:00'),
('IS106', 'SIS', 'Database Modeling & Design', 'Data modeling in software engineering is the process of creating a data model by applying formal data model descriptions using data modeling techniques. ', '2013-11-13', '08:30:00', '11:45:00'),
('IS107', 'SIS', 'IT Outsourcing', 'This course teaches you on how to outsource your programming projects to others.', '2013-11-12', '12:00:00', '15:15:00'),
('IS108', 'SIS', 'Organization Behaviour', 'Organizational Behavior (OB) is the study and application of knowledge about how people, individuals, and groups act in organizations. ', '2013-11-11', '15:30:00', '18:45:00'),
('IS109', 'SIS', 'Cloud Computing', 'Cloud computing is Internet-based computing, whereby shared resources, software and information are provided to computers and other devices on-demand, like the electricity grid.', '2013-11-10', '08:30:00', '11:45:00'),
('IS200', 'SIS', 'Final Touch', 'Learn how eat, dress and talk.', '2013-11-09', '12:00:00', '15:15:00'),
('IS201', 'SIS', 'Fun with Shell Programming', 'Shell scripts are a fundamental part of the UNIX and Linux programming environment.', '2013-11-08', '15:30:00', '18:45:00'),
('IS202', 'SIS', 'Enterprise integration', 'Enterprise integration is a technical field of Enterprise Architecture, which focused on the study of things like system interconnection, electronic data interchange, product data exchange and distributed computing environments, and it''s possible other solutions.[1', '2013-11-07', '08:30:00', '11:45:00'),
('IS203', 'SIS', 'Software Engineering', 'The Sleepless Era.', '2013-11-06', '12:00:00', '15:15:00'),
('IS204', 'SIS', 'Database System Administration', 'Database administration is a complex, often thankless chore.', '2013-11-05', '15:30:00', '18:45:00'),
('IS205', 'SIS', 'All Talk, No Action', 'The easiest course of all. We will sit around and talk.', '2013-11-04', '08:30:00', '11:45:00'),
('IS206', 'SIS', 'Operation Research', 'Operations research, also known as operational research, is an interdisciplinary branch of applied mathematics and formal science that uses advanced analytical methods such as mathematical modeling, statistical analysis, and mathematical optimization to arrive at optimal or near-optimal solutions to complex decision-making problems.', '2013-11-03', '12:00:00', '15:15:00'),
('IS207', 'SIS', 'GUI Bloopers', 'Common User Interface Design Don''ts and Dos', '2013-11-03', '15:30:00', '18:45:00'),
('IS208', 'SIS', 'Artifical Intelligence', 'The science and engineering of making intelligent machine', '2013-11-03', '08:30:00', '11:45:00'),
('IS209', 'SIS', 'Information Storage and Management', 'Information storage and management (ISM) - once a relatively straightforward operation -has developed into a highly sophisticated pillar of information technology, requiring proven technical expertise.', '2013-11-02', '12:00:00', '15:15:00'),
('MGMT001', 'SOB', 'Business,Government, and Society', 'learn the interrelation amongst the three', '2013-11-02', '08:30:00', '11:45:00'),
('MGMT002', 'SOB', 'Technology and World Change', 'As technology changes, so does the world', '2013-11-01', '12:00:00', '15:15:00');

-- --------------------------------------------------------

--
-- Table structure for table `course_completed`
--

CREATE TABLE IF NOT EXISTS `course_completed` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  PRIMARY KEY (`User_id`,`Course_id`),
  KEY `idCourse_idx` (`Course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `course_completed`
--

INSERT INTO `course_completed` (`User_id`, `Course_id`) VALUES
('amy.ng.2009', 'IS100'),
('gary.ng.2009', 'IS100'),
('ben.ng.2009', 'IS102'),
('ben.ng.2009', 'IS103');

-- --------------------------------------------------------

--
-- Table structure for table `prerequisite`
--

CREATE TABLE IF NOT EXISTS `prerequisite` (
  `Course_id` varchar(25) NOT NULL,
  `Prerequisite_id` varchar(25) NOT NULL,
  PRIMARY KEY (`Course_id`,`Prerequisite_id`),
  KEY `Prerequisite_id_idx` (`Prerequisite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `prerequisite`
--

INSERT INTO `prerequisite` (`Course_id`, `Prerequisite_id`) VALUES
('IS101', 'IS100'),
('IS103', 'IS102'),
('IS109', 'IS102'),
('IS104', 'IS103'),
('IS203', 'IS103'),
('IS203', 'IS106'),
('IS204', 'IS106'),
('IS209', 'IS106');

-- --------------------------------------------------------

--
-- Table structure for table `section`
--

CREATE TABLE IF NOT EXISTS `section` (
  `Section_id` varchar(3) NOT NULL,
  `Day` varchar(10) NOT NULL,
  `StartTime` time DEFAULT NULL,
  `EndTime` time DEFAULT NULL,
  `Instructor` varchar(100) NOT NULL,
  `Venue` varchar(100) NOT NULL,
  `Size` int(11) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  PRIMARY KEY (`Section_id`,`Course_id`),
  KEY `Course_id_idx` (`Course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `section`
--

INSERT INTO `section` (`Section_id`, `Day`, `StartTime`, `EndTime`, `Instructor`, `Venue`, `Size`, `Course_id`) VALUES
('S1', 'Thursday', '08:30:00', '11:45:00', 'John KHOO', 'Seminar Rm 2-34', 10, 'ECON001'),
('S1', 'Friday', '15:30:00', '18:45:00', 'Andy KHOO', 'Seminar Rm 2-35', 10, 'ECON002'),
('S1', 'Monday', '08:30:00', '11:45:00', 'Albert KHOO', 'Seminar Rm 2-1', 5, 'IS100'),
('S1', 'Wednesday', '15:30:00', '18:45:00', 'Cheri KHOO', 'Seminar Rm 2-3', 10, 'IS101'),
('S1', 'Monday', '15:30:00', '18:45:00', 'Felicia KHOO', 'Seminar Rm 2-6', 10, 'IS102'),
('S1', 'Thursday', '15:30:00', '18:45:00', 'Ivy KHOO', 'Seminar Rm 2-9', 10, 'IS103'),
('S1', 'Tuesday', '15:30:00', '18:45:00', 'Linn KHOO', 'Seminar Rm 2-12', 10, 'IS104'),
('S1', 'Thursday', '12:00:00', '15:15:00', 'Nathaniel KHOO', 'Seminar Rm 2-14', 10, 'IS105'),
('S1', 'Monday', '08:30:00', '11:45:00', 'Peter KHOO', 'Seminar Rm 2-16', 10, 'IS106'),
('S1', 'Wednesday', '15:30:00', '18:45:00', 'Ray KHOO', 'Seminar Rm 2-18', 10, 'IS107'),
('S1', 'Friday', '12:00:00', '15:15:00', 'Tim KHOO', 'Seminar Rm 2-20', 10, 'IS108'),
('S1', 'Tuesday', '08:30:00', '11:45:00', 'Vincent KHOO', 'Seminar Rm 2-22', 10, 'IS109'),
('S1', 'Thursday', '15:30:00', '18:45:00', 'Xtra KHOO', 'Seminar Rm 2-24', 10, 'IS200'),
('S1', 'Friday', '08:30:00', '11:45:00', 'Yale KHOO', 'Seminar Rm 2-25', 10, 'IS201'),
('S1', 'Monday', '12:00:00', '15:15:00', 'Zen KHOO', 'Seminar Rm 2-26', 10, 'IS202'),
('S1', 'Tuesday', '15:30:00', '18:45:00', 'Anderson KHOO', 'Seminar Rm 2-27', 10, 'IS203'),
('S1', 'Wednesday', '08:30:00', '11:45:00', 'Bing KHOO', 'Seminar Rm 2-28', 10, 'IS204'),
('S1', 'Thursday', '12:00:00', '15:15:00', 'Carlo KHOO', 'Seminar Rm 2-29', 10, 'IS205'),
('S1', 'Friday', '15:30:00', '18:45:00', 'Dickson KHOO', 'Seminar Rm 2-30', 10, 'IS206'),
('S1', 'Monday', '08:30:00', '11:45:00', 'Edmund KHOO', 'Seminar Rm 2-31', 10, 'IS207'),
('S1', 'Tuesday', '12:00:00', '15:15:00', 'Febrice KHOO', 'Seminar Rm 2-32', 10, 'IS208'),
('S1', 'Wednesday', '08:30:00', '11:45:00', 'Gavin KHOO', 'Seminar Rm 2-33', 10, 'MGMT001'),
('S1', 'Wednesday', '15:30:00', '18:45:00', 'Bob KHOO', 'Seminar Rm 2-37', 10, 'MGMT002'),
('S2', 'Tuesday', '12:00:00', '15:15:00', 'Billy KHOO', 'Seminar Rm 2-2', 10, 'IS100'),
('S2', 'Thursday', '08:30:00', '11:45:00', 'Daniel KHOO', 'Seminar Rm 2-4', 10, 'IS101'),
('S2', 'Tuesday', '08:30:00', '11:45:00', 'Gerald KHOO', 'Seminar Rm 2-7', 10, 'IS102'),
('S2', 'Friday', '08:30:00', '11:45:00', 'Jason KHOO', 'Seminar Rm 2-10', 10, 'IS103'),
('S2', 'Wednesday', '08:30:00', '11:45:00', 'Michael KHOO', 'Seminar Rm 2-13', 10, 'IS104'),
('S2', 'Friday', '15:30:00', '18:45:00', 'Oreilly KHOO', 'Seminar Rm 2-15', 10, 'IS105'),
('S2', 'Tuesday', '12:00:00', '15:15:00', 'Queen KHOO', 'Seminar Rm 2-17', 10, 'IS106'),
('S2', 'Thursday', '08:30:00', '11:45:00', 'Simon KHOO', 'Seminar Rm 2-19', 10, 'IS107'),
('S2', 'Wednesday', '12:00:00', '15:15:00', 'Winnie KHOO', 'Seminar Rm 2-23', 10, 'IS109'),
('S3', 'Friday', '12:00:00', '15:15:00', 'Ernest KHOO', 'Seminar Rm 2-5', 10, 'IS101'),
('S3', 'Wednesday', '12:00:00', '15:15:00', 'Henry KHOO', 'Seminar Rm 2-8', 10, 'IS102'),
('S3', 'Monday', '12:00:00', '15:15:00', 'Kat KHOO', 'Seminar Rm 2-11', 10, 'IS103');

-- --------------------------------------------------------

--
-- Table structure for table `section_student`
--

CREATE TABLE IF NOT EXISTS `section_student` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  `Section_id` varchar(3) NOT NULL,
  `BidAmount` double NOT NULL,
  PRIMARY KEY (`User_id`,`Course_id`,`Section_id`),
  KEY `Course_id_idx` (`Course_id`),
  KEY `Section_id_idx` (`Section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE IF NOT EXISTS `student` (
  `User_id` varchar(128) NOT NULL,
  `Password` varchar(128) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `School` char(4) NOT NULL,
  `eDollar` double DEFAULT NULL,
  `Type` varchar(10) NOT NULL,
  PRIMARY KEY (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`User_id`, `Password`, `Name`, `School`, `eDollar`, `Type`) VALUES
('admin', 'password', 'Administrator', 'SMU', NULL, 'admin'),
('amy.ng.2009', 'qwerty128', 'Amy NG', 'SIS', 200, 'student'),
('ben.ng.2009', 'qwerty129', 'Ben NG', 'SIS', 189, 'student'),
('calvin.ng.2009', 'qwerty130', 'Calvin NG', 'SIS', 188, 'student'),
('cristabel', 'cristabel', 'Cristabel Lau', 'SIS', 1000, 'student'),
('dawn.ng.2009', 'qwerty131', 'Dawn NG', 'SIS', 187, 'student'),
('eddy.ng.2009', 'qwerty132', 'Eddy NG', 'SIS', 186, 'student'),
('eugene', 'eugene', 'Eugene Tan', 'SIS', 1000, 'student'),
('fred.ng.2009', 'qwerty133', 'Fred NG', 'SIS', 185, 'student'),
('gary.ng.2009', 'qwerty134', 'Gary NG', 'SIS', 200, 'student'),
('harry.ng.2009', 'qwerty135', 'Harry NG', 'SIS', 183, 'student'),
('ian.ng.2009', 'qwerty136', 'Ian NG', 'SIS', 182, 'student'),
('jerry.ng.2009', 'qwerty137', 'Jerry NG', 'SIS', 200, 'student'),
('jett', 'jett', 'Jett Quek', 'SIS', 1000, 'student'),
('kelly.ng.2009', 'qwerty138', 'Kelly NG', 'SIS', 200, 'student'),
('larry.ng.2009', 'qwerty139', 'Larry NG', 'SIS', 181, 'student'),
('maggie.ng.2009', 'qwerty140', 'Maggie NG', 'SIS', 180, 'student'),
('mktan', 'mktan', 'Tan Ming Kwang', 'SIS', 1000, 'student'),
('neilson.ng.2009', 'qwerty141', 'Neilson NG', 'SIS', 179, 'student'),
('olivia.ng.2009', 'qwerty142', 'Olivia NG', 'SIS', 178, 'student'),
('parker.ng.2009', 'qwerty143', 'Parker NG', 'SOE', 176, 'student'),
('quiten.ng.2009', 'qwerty144', 'Quiten NG', 'SOE', 176, 'student'),
('ricky.ng.2009', 'qwerty145', 'Ricky NG', 'SOE', 176, 'student'),
('steven.ng.2009', 'qwerty146', 'Steven NG', 'SOE', 174, 'student'),
('timothy.ng.2009', 'qwerty147', 'Timothy NG', 'SOE', 173, 'student'),
('ursala.ng.2009', 'qwerty148', 'Ursala NG', 'SOE', 172, 'student'),
('valarie.ng.2009', 'qwerty149', 'Valarie NG', 'SOB', 171, 'student'),
('wilson', 'wilson', 'Wilson He', 'SIS', 1000, 'student'),
('winston.ng.2009', 'qwerty150', 'Winston NG', 'SOB', 170, 'student'),
('xavier.ng.2009', 'qwerty151', 'Xavier NG', 'SOB', 169, 'student'),
('yasir.ng.2009', 'qwerty152', 'Yasir NG', 'SOB', 168, 'student'),
('zac.ng.2009', 'qwerty153', 'Zac NG', 'SOB', 167, 'student');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bid`
--
ALTER TABLE `bid`
  ADD CONSTRAINT `ID_Section` FOREIGN KEY (`Course_id`, `Section_id`) REFERENCES `section` (`Course_id`, `Section_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `ID_User` FOREIGN KEY (`User_id`) REFERENCES `student` (`User_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `bidding_cart`
--
ALTER TABLE `bidding_cart`
  ADD CONSTRAINT `SectionID` FOREIGN KEY (`Course_id`) REFERENCES `section` (`Course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `UserID` FOREIGN KEY (`User_id`) REFERENCES `student` (`User_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `course_completed`
--
ALTER TABLE `course_completed`
  ADD CONSTRAINT `idCourse` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `idUser` FOREIGN KEY (`User_id`) REFERENCES `student` (`User_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `prerequisite`
--
ALTER TABLE `prerequisite`
  ADD CONSTRAINT `PrerequisiteID` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `Prerequisite_id` FOREIGN KEY (`Prerequisite_id`) REFERENCES `course` (`Course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `section`
--
ALTER TABLE `section`
  ADD CONSTRAINT `Course_id` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Constraints for table `section_student`
--
ALTER TABLE `section_student`
  ADD CONSTRAINT `IDSection` FOREIGN KEY (`Section_id`) REFERENCES `section` (`Section_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `IDStudent` FOREIGN KEY (`User_id`) REFERENCES `student` (`User_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `ID_Course` FOREIGN KEY (`Course_id`) REFERENCES `course` (`Course_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
