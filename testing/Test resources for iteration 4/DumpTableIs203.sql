-- phpMyAdmin SQL Dump
-- version 4.5.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Oct 02, 2016 at 02:10 PM
-- Server version: 5.7.11
-- PHP Version: 5.6.16

SET FOREIGN_KEY_CHECKS=0;
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

--
-- Dumping data for table `course`
--

INSERT INTO `course` (`Course_id`, `School`, `Title`, `Description`, `Exam_Date`, `Exam_Start`, `Exam_End`) VALUES
('IS100', 'SIS', 'Calculus', 'The basic objective of Calculus is to relate small-scale (differential) quantities to large-scale (integrated) quantities. This is accomplished by means of the Fundamental Theorem of Calculus. Students should demonstrate an understanding of the integral as a cumulative sum, of the derivative as a rate of change, and of the inverse relationship between integration and differentiation', '20101119', '08:30:00', '11:45:00'),
('IS101', 'SIS', 'Advanced Calculus', 'This is a second course on calculus. It is more advanced definitely', '20101118', '12:00:00', '15:15:00'),


--
-- Dumping data for table `course_completed`
--

INSERT INTO `course_completed` (`User_id`, `Course_id`) VALUES
('bob.tan.2012', 'IS101'),
('charlie.chan.2012', 'IS100'),
('ada.goh.2012', 'IS100');

--
-- Dumping data for table `prerequisite`
--

INSERT INTO `prerequisite` (`Course_id`, `Prerequisite_id`) VALUES
('IS101', 'IS100');

--
-- Dumping data for table `section`
--

INSERT INTO `section` (`Section_id`, `Day`, `StartTime`, `EndTime`, `Instructor`, `Venue`, `Size`, `Course_id`) VALUES
('S1', 'Monday', '08:15:00', '11:45:00', 'Albert KHOO', 'Seminar Rm 2-1', 10, 'IS101'),
('S1', 'Monday', '08:15:00', '11:45:00', 'Albert KHOO', 'Seminar Rm 2-1', 10, 'IS100'),
('S2', 'Tuesday', '08:15:00', '11:45:00', 'Albert KHOO', 'Seminar Rm 2-1', 10, 'IS101'),


--
-- Dumping data for table `section_student`
--

INSERT INTO `section_student` (`User_id`, `Course_id`, `Section_id`, `BidAmount`) VALUES
('bob.tan.2012', 'IS101', 'S1', 10),
('charlie.chan.2012', 'IS100', 'S1', 10),
('ada.goh.2012', 'IS100', 'S1', 12);

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`User_id`, `Password`, `Name`, `School`, `eDollar`, `Type`) VALUES
('admin', 'password', 'Administrator', 'SMU', NULL, 'admin'),
('Ada.Goh.2012', 'qwerty128', 'Cristabel Lau', 'SIS', 200, 'student'),
('eugene.tan.2012', 'qwerty128', 'Eugene Tan', 'SIS', 200, 'student'),
('charlie.chan.2012', 'qwerty128', 'Charlie Chan', 'SIS', 200, 'student'),
('bob.tan.2012', 'qwerty128', 'Bob Tan', 'SIS', 200, 'student'),
('francis.tan.2012', 'Francis Tan', 'qwerty128', 'SIS', 200, 'student'),
('travis.tan.2012', 'qwerty128', 'travis tan', 'SIS', 200, 'student');
SET FOREIGN_KEY_CHECKS=1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
