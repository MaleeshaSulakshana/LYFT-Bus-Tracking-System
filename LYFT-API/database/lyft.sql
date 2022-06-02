-- phpMyAdmin SQL Dump
-- version 5.1.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 02, 2022 at 12:08 PM
-- Server version: 10.4.19-MariaDB
-- PHP Version: 7.4.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `lyft`
--

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `id` int(255) NOT NULL,
  `user_id` int(255) NOT NULL,
  `available_booking_id` int(255) NOT NULL,
  `booked_date_time` datetime NOT NULL,
  `picked_location` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`id`, `user_id`, `available_booking_id`, `booked_date_time`, `picked_location`) VALUES
(1, 1, 1, '2022-06-01 09:39:28', 'Pitipana');

-- --------------------------------------------------------

--
-- Table structure for table `booking_available`
--

CREATE TABLE `booking_available` (
  `id` int(255) NOT NULL,
  `vehicle_id` int(255) NOT NULL,
  `available_date_time` datetime NOT NULL,
  `sheet_count` int(255) NOT NULL,
  `start_location` varchar(255) NOT NULL,
  `end_location` varchar(255) NOT NULL,
  `price` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `booking_available`
--

INSERT INTO `booking_available` (`id`, `vehicle_id`, `available_date_time`, `sheet_count`, `start_location`, `end_location`, `price`) VALUES
(1, 1, '2022-06-02 00:00:00', 40, 'Colombo', 'Badulla', '500'),
(2, 1, '2022-06-03 00:00:00', 40, 'Colombo', 'Badulla', '500');

-- --------------------------------------------------------

--
-- Table structure for table `tracking`
--

CREATE TABLE `tracking` (
  `id` int(255) NOT NULL,
  `gps_id` varchar(255) NOT NULL,
  `latitude` varchar(255) NOT NULL,
  `longitude` varchar(255) NOT NULL,
  `vehicle` int(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `started_date_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `tracking`
--

INSERT INTO `tracking` (`id`, `gps_id`, `latitude`, `longitude`, `vehicle`, `status`, `started_date_time`) VALUES
(13, '10979716', '6.8210414669522725', '80.04060730188732', 1, 'started', '2022-06-02 12:06:50'),
(14, '7420975', '6.837750788637951', '79.98469879884311', 2, 'started', '2022-06-02 12:06:50'),
(15, '9389834', '6.935811581268227', '79.98407888314912', 1, 'stopped', '2022-06-02 12:06:50'),
(16, '9134520', '6.944170544027098', '79.87843984521986', 1, 'stopped', '2022-06-02 12:22:30'),
(17, '3149022', '6.8382125', '80.0248154', 1, 'stopped', '2022-06-02 15:19:30');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `psw` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `mobile`, `psw`) VALUES
(1, 'Maleesha', 'maleesha@gmail.com', '0767950608', '12'),
(2, 'Anuja', 'anuja@gmail.com', '0764850698', '1234');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

CREATE TABLE `vehicles` (
  `id` int(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `mobile` varchar(255) NOT NULL,
  `bus_name` varchar(255) NOT NULL,
  `bus_reg_no` varchar(255) NOT NULL,
  `root_no` varchar(255) NOT NULL,
  `root_start` varchar(255) NOT NULL,
  `root_end` varchar(255) NOT NULL,
  `owner_name` varchar(255) NOT NULL,
  `owner_number` varchar(255) NOT NULL,
  `driver_name` varchar(255) NOT NULL,
  `conductor_name` varchar(255) NOT NULL,
  `psw` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`id`, `name`, `email`, `mobile`, `bus_name`, `bus_reg_no`, `root_no`, `root_start`, `root_end`, `owner_name`, `owner_number`, `driver_name`, `conductor_name`, `psw`, `image`) VALUES
(1, 'Anuja Travers Bus', 'anujad@gmail.com', '0754868957', 'Anuja Travels ', 'NB-1020', '129', 'Kottawa', 'Moragahahena', 'Anuja', '0748695807', 'Anuja Driver', 'Anuja Conductor ', '12', 'https://firebasestorage.googleapis.com/v0/b/lyft-84866.appspot.com/o/vehicles_Pictures%2F10163826?alt=media&token=cad05f0c-41a8-4884-8c2c-804b2c77b6f7'),
(2, 'Anuja Travers Bus', 'anujad@gmail.com', '0754868957', 'Anuja Travels 2', 'NB-6582', '128', 'Kottawa', 'Thalagala', 'Anuja', '0748695807', 'Anuja Driver', 'Anuja Conductor ', '12', 'https://firebasestorage.googleapis.com/v0/b/lyft-84866.appspot.com/o/vehicles_Pictures%2F10163826?alt=media&token=cad05f0c-41a8-4884-8c2c-804b2c77b6f7');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `booking_available`
--
ALTER TABLE `booking_available`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tracking`
--
ALTER TABLE `tracking`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `booking_available`
--
ALTER TABLE `booking_available`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `tracking`
--
ALTER TABLE `tracking`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
