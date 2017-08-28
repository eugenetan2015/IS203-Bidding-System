-- phpMyAdmin SQL Dump
-- version 4.5.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Sep 23, 2016 at 05:27 PM
-- Server version: 5.7.11
-- PHP Version: 5.6.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `is203`
--
CREATE DATABASE IF NOT EXISTS `is203` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `is203`;

-- --------------------------------------------------------

--
-- Table structure for table `bid`
--

CREATE TABLE `bid` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  `Section_id` varchar(3) NOT NULL,
  `Round` double NOT NULL,
  `Status` varchar(10) NOT NULL,
  `BidAmount` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `bidding_cart`
--

CREATE TABLE `bidding_cart` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  `Section_id` char(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `Course_id` varchar(25) NOT NULL,
  `School` char(4) NOT NULL,
  `Title` varchar(100) NOT NULL,
  `Description` varchar(1000) NOT NULL,
  `Exam_Date` date NOT NULL,
  `Exam_Start` time NOT NULL,
  `Exam_End` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`Course_id`, `School`, `Title`, `Description`, `Exam_Date`, `Exam_Start`, `Exam_End`) VALUES
('IS101', 'SIS', 'Seminar on Information Systems Management', 'Seminar on Information Systems Management', '2017-03-08', '10:00:00', '12:00:00'),
('IS200', 'SIS', 'Fundamentals of Java', 'Fundamentals of Java Programming', '2017-03-09', '10:00:00', '12:00:00'),
('MATH001', 'SOB', 'Calculus', 'Calculus', '2016-11-24', '12:00:00', '15:15:00'),
('STATS101', 'SOE', 'Introduction to Statistics', 'Introduction to Fundmentals of Statistics', '2017-03-23', '10:00:00', '12:00:00'),
('STATS151', 'SOE', 'Advanced Statistics', 'Advanced Statistics', '2017-03-16', '10:00:00', '12:00:00'),
('TEST001', 'SIS', 'Test Class', 'This class should not have vacancy', '2016-09-14', '00:00:00', '01:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `course_completed`
--

CREATE TABLE `course_completed` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `course_completed`
--

INSERT INTO `course_completed` (`User_id`, `Course_id`) VALUES
('cristabel.lau', 'IS101'),
('jett.quek', 'STATS101');

-- --------------------------------------------------------

--
-- Table structure for table `prerequisite`
--

CREATE TABLE `prerequisite` (
  `Course_id` varchar(25) NOT NULL,
  `Prerequisite_id` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `prerequisite`
--

INSERT INTO `prerequisite` (`Course_id`, `Prerequisite_id`) VALUES
('STATS151', 'STATS101');

-- --------------------------------------------------------

--
-- Table structure for table `section`
--

CREATE TABLE `section` (
  `Section_id` varchar(3) NOT NULL,
  `Day` varchar(10) NOT NULL,
  `StartTime` time DEFAULT NULL,
  `EndTime` time DEFAULT NULL,
  `Instructor` varchar(100) NOT NULL,
  `Venue` varchar(100) NOT NULL,
  `Size` int(11) NOT NULL,
  `Course_id` varchar(25) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `section`
--

INSERT INTO `section` (`Section_id`, `Day`, `StartTime`, `EndTime`, `Instructor`, `Venue`, `Size`, `Course_id`) VALUES
('G1', 'Monday', '08:15:00', '11:30:00', 'Lin Mei', 'SIS SR 2-1', 40, 'IS101'),
('G1', 'Tuesday', '12:00:00', '15:15:00', 'Lee Yeow Leong', 'SIS SR 3-2', 40, 'IS200'),
('G1', 'Tuesday', '08:15:00', '11:30:00', 'Zhao Yibao', 'LKCSB Seminar Rm 3.8', 49, 'MATH001'),
('G1', 'Monday', '10:00:00', '11:00:00', 'Instructor', 'SIS SR 22-1', 2, 'TEST001'),
('G2', 'Tuesday', '15:30:00', '18:45:00', 'Lee Yeow Leong', 'SIS SR 3-3', 40, 'IS200'),
('G2', 'Tuesday', '15:30:00', '18:45:00', 'Zhao Yibao', 'LKCSB Seminar Rm 3.8', 49, 'MATH001'),
('G3', 'Wedneday', '08:15:00', '11:30:00', 'Zhao Yibao', 'LKCSB Seminar Rm 3.7', 49, 'MATH001'),
('G4', 'Tuesday', '15:30:00', '18:45:00', 'Zhao Yibao', 'LKCSB Seminar Rm 3.8', 49, 'MATH001'),
('G5', 'Monday', '08:15:00', '11:30:00', 'Liew Sing Loon', 'LKCSB Seminar Rm 3.7', 49, 'MATH001'),
('G6', 'Monday', '15:30:00', '18:45:00', 'Liew Sing Loon', 'LKCSB Seminar Rm 3.7', 49, 'MATH001'),
('G7', 'Friday', '08:15:00', '11:30:00', 'Liew Sing Loon', 'LKCSB Seminar Rm 3.7', 49, 'MATH001'),
('G8', 'Friday', '12:00:00', '15:15:00', 'Liew Sing Loon', 'LKCSB Seminar Rm 3.7', 49, 'MATH001');

-- --------------------------------------------------------

--
-- Table structure for table `section_student`
--

CREATE TABLE `section_student` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(25) NOT NULL,
  `Section_id` varchar(3) NOT NULL,
  `BidAmount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `section_student`
--

INSERT INTO `section_student` (`User_id`, `Course_id`, `Section_id`, `BidAmount`) VALUES
('mktan', 'IS101', 'G1', 10),
('mktan', 'TEST001', 'G1', 10),
('testacc', 'MATH001', 'G1', 15),
('wilson.ho', 'IS101', 'G1', 10),
('wilson.ho', 'TEST001', 'G1', 15);

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `User_id` varchar(128) NOT NULL,
  `Password` varchar(128) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `School` char(4) NOT NULL,
  `eDollar` double DEFAULT NULL,
  `Type` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`User_id`, `Password`, `Name`, `School`, `eDollar`, `Type`) VALUES
('admin', 'password', 'Administrator', 'SMU', NULL, 'admin'),
('cristabel.lau', 'cristabel', 'Cristabel Lau', 'SIS', 100, 'student'),
('eugene.tan', 'eugene', 'Eugene Tan', 'SIS', 100, 'student'),
('jett.quek', 'jett', 'Jett Quek', 'SIS', 100, 'student'),
('mktan', 'mktan', 'Tan Ming Kwang', 'SIS', 100, 'student'),
('testacc', '123', 'Tester', 'SIS', 100, 'student'),
('wilson.ho', 'wilson', 'Wilson Ho', 'SIS', 100, 'student');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bid`
--
ALTER TABLE `bid`
  ADD PRIMARY KEY (`User_id`,`Course_id`,`Section_id`,`Round`),
  ADD KEY `ID_Course_idx` (`Course_id`,`Section_id`);

--
-- Indexes for table `bidding_cart`
--
ALTER TABLE `bidding_cart`
  ADD PRIMARY KEY (`User_id`,`Course_id`,`Section_id`),
  ADD KEY `SectionID_idx` (`Course_id`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`Course_id`);

--
-- Indexes for table `course_completed`
--
ALTER TABLE `course_completed`
  ADD PRIMARY KEY (`User_id`,`Course_id`),
  ADD KEY `idCourse_idx` (`Course_id`);

--
-- Indexes for table `prerequisite`
--
ALTER TABLE `prerequisite`
  ADD PRIMARY KEY (`Course_id`,`Prerequisite_id`),
  ADD KEY `Prerequisite_id_idx` (`Prerequisite_id`);

--
-- Indexes for table `section`
--
ALTER TABLE `section`
  ADD PRIMARY KEY (`Section_id`,`Course_id`),
  ADD KEY `Course_id_idx` (`Course_id`);

--
-- Indexes for table `section_student`
--
ALTER TABLE `section_student`
  ADD PRIMARY KEY (`User_id`,`Course_id`,`Section_id`),
  ADD KEY `Course_id_idx` (`Course_id`),
  ADD KEY `Section_id_idx` (`Section_id`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`User_id`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bid`
--
ALTER TABLE `bid`
  ADD CONSTRAINT `ID_Section` FOREIGN KEY (`Course_id`,`Section_id`) REFERENCES `section` (`Course_id`, `Section_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
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
