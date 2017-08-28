-- phpMyAdmin SQL Dump
-- version 4.6.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 07, 2016 at 12:59 PM
-- Server version: 5.6.31
-- PHP Version: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `is203`
--
CREATE DATABASE IF NOT EXISTS `is203` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `is203`;

-- --------------------------------------------------------

--
-- Table structure for table `bid`
--

CREATE TABLE `bid` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(255) NOT NULL,
  `Section_id` varchar(3) NOT NULL,
  `Round` int(100) NOT NULL,
  `Status` varchar(10) NOT NULL,
  `BidAmount` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bidding_cart`
--

CREATE TABLE `bidding_cart` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(255) NOT NULL,
  `Section_id` char(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `bidding_round`
--

CREATE TABLE `bidding_round` (
  `status` varchar(10) NOT NULL,
  `current_round` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `course`
--

CREATE TABLE `course` (
  `Course_id` varchar(255) NOT NULL,
  `School` varchar(255) NOT NULL,
  `Title` varchar(100) NOT NULL,
  `Description` varchar(1000) NOT NULL,
  `Exam_Date` date NOT NULL,
  `Exam_Start` time NOT NULL,
  `Exam_End` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `course_completed`
--

CREATE TABLE `course_completed` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `minimum_bid`
--

CREATE TABLE `minimum_bid` (
  `course_id` varchar(255) NOT NULL,
  `section_id` varchar(3) NOT NULL,
  `amount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `prerequisite`
--

CREATE TABLE `prerequisite` (
  `Course_id` varchar(255) NOT NULL,
  `Prerequisite_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `Size` int(100) NOT NULL,
  `Course_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `section_student`
--

CREATE TABLE `section_student` (
  `User_id` varchar(128) NOT NULL,
  `Course_id` varchar(255) NOT NULL,
  `Section_id` varchar(3) NOT NULL,
  `BidAmount` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `User_id` varchar(128) NOT NULL,
  `Password` varchar(128) NOT NULL,
  `Name` varchar(100) NOT NULL,
  `School` varchar(255) NOT NULL,
  `eDollar` double DEFAULT NULL,
  `Type` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `student_timetable`
--

CREATE TABLE `student_timetable` (
  `start_date` date NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
-- Indexes for table `bidding_round`
--
ALTER TABLE `bidding_round`
  ADD PRIMARY KEY (`current_round`,`status`);

--
-- Indexes for table `course`
--
ALTER TABLE `course`
  ADD PRIMARY KEY (`Course_id`),
  ADD KEY `Course_id` (`Course_id`) USING BTREE,
  ADD KEY `School` (`School`),
  ADD KEY `Title` (`Title`);

--
-- Indexes for table `course_completed`
--
ALTER TABLE `course_completed`
  ADD PRIMARY KEY (`User_id`,`Course_id`),
  ADD KEY `idCourse_idx` (`Course_id`);

--
-- Indexes for table `minimum_bid`
--
ALTER TABLE `minimum_bid`
  ADD PRIMARY KEY (`course_id`,`section_id`);

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
  ADD KEY `Course_id_idx` (`Course_id`),
  ADD KEY `Section_id` (`Section_id`);

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
  ADD PRIMARY KEY (`User_id`),
  ADD KEY `User_id` (`User_id`);

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
