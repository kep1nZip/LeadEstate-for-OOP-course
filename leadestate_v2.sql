SET FOREIGN_KEY_CHECKS=0;
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 13, 2026 at 08:00 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `leadestate`
--

-- --------------------------------------------------------

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS roles;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `roles`
--

INSERT INTO `roles` (`id`, `role_name`) VALUES
(1, 'Admin'),
(2, 'Sales');

-- --------------------------------------------------------

--
-- Table structure for table `lead_status`
--

DROP TABLE IF EXISTS lead_status;
CREATE TABLE `lead_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `statusName` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `lead_status`
--

INSERT INTO `lead_status` (`id`, `statusName`) VALUES
(1, 'New Lead'),
(2, 'Contacted'),
(3, 'Follow Up'),
(4, 'Negotiation'),
(5, 'Closed Won'),
(6, 'Closed Lost');

-- --------------------------------------------------------

--
-- Table structure for table `properties`
--

DROP TABLE IF EXISTS properties;
CREATE TABLE `properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(150) NOT NULL,
  `location` varchar(200) DEFAULT NULL,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `properties`
--

INSERT INTO `properties` (`id`, `name`, `location`, `price`) VALUES
(1, 'Mansion AHA', 'Bandung', 5000000000),
(2, 'Penthouse Kai', 'Jakarta', 3000000000),
(3, 'Malfoy Manor', 'Bogor', 10000000000),
(4, 'The Burrow', 'Depok', 2000000000),
(5, 'Batcave', 'Gotham', 15000000000);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS users;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `roleId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `roleId` (`roleId`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `roles` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `roleId`) VALUES
(1, 'Kepin', 'kepin@gmail.com', '12345', 1),
(2, 'Fathan', 'fathan@gmail.com', '12345', 2),
(3, 'Firasy', 'firasy@gmail.com', '12345', 1),
(4, 'Rafi', 'rafi@gmail.com', '12345', 2),
(5, 'Nicole', 'nicole@gmail.com', '12345', 2);

-- --------------------------------------------------------

--
-- Table structure for table `leads`
--

DROP TABLE IF EXISTS leads;
CREATE TABLE `leads` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `propertyId` int(11) DEFAULT NULL,
  `salesId` int(11) DEFAULT NULL,
  `statusId` int(11) DEFAULT NULL,
  `source` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `propertyId` (`propertyId`),
  KEY `salesId` (`salesId`),
  KEY `statusId` (`statusId`),
  CONSTRAINT `leads_ibfk_1` FOREIGN KEY (`propertyId`) REFERENCES `properties` (`id`) ON DELETE SET NULL,
  CONSTRAINT `leads_ibfk_2` FOREIGN KEY (`salesId`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `leads_ibfk_3` FOREIGN KEY (`statusId`) REFERENCES `lead_status` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `leads`
--

INSERT INTO `leads` (`id`, `name`, `phone`, `email`, `propertyId`, `salesId`, `statusId`, `source`, `created_at`) VALUES
(1, 'Jane Doe', '0811111', 'jane@mail.com', 1, 2, 1, 'Instagram', '2026-04-06 14:08:37'),
(2, 'Neps', '0822222', 'neps@mail.com', 2, 2, 2, 'Website', '2026-04-06 14:08:37'),
(3, 'Mydeimos', '0833333', 'mydei@mail.com', 3, 4, 3, 'Whatsapp', '2026-04-06 14:08:37'),
(4, 'Cyrene', '0844444', 'cyrene@mail.com', 5, 4, 4, 'Facebook', '2026-04-06 14:08:37'),
(5, 'Growy', '0855555', 'growy@mail.com', 4, 2, 6, 'Referral', '2026-04-06 14:08:37'),
(6, 'Aditya Syachputra', '0822222234', 'AdityaShadowKilerz@gmail.com', 9, 6, 1, 'Facebook', '2026-04-06 14:42:04'),
(7, 'Leon', '081298765431', 'leonskenedy@mail.com', 7, 7, 1, 'Instagram', '2026-04-06 14:42:58'),
(8, 'Kael ', '083176543219', 'kael.vortigan@mail.com', 8, 8, 3, 'Website', '2026-04-06 14:42:58'),
(9, 'Nayra ', '084165432198', 'nayra.elowen@mail.com', 6, 9, 4, 'Facebook', '2026-04-06 14:42:58'),
(10, 'Damarion ', '085154321987', 'damarion.z@mail.com', 10, 10, 5, 'Referral', '2026-04-06 14:42:58');

-- --------------------------------------------------------

--
-- Table structure for table `followups`
--

DROP TABLE IF EXISTS followups;
CREATE TABLE `followups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `leadId` int(11) NOT NULL,
  `salesId` int(11) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `followupDate` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `leadId` (`leadId`),
  KEY `salesId` (`salesId`),
  CONSTRAINT `followups_ibfk_1` FOREIGN KEY (`leadId`) REFERENCES `leads` (`id`) ON DELETE CASCADE,
  CONSTRAINT `followups_ibfk_2` FOREIGN KEY (`salesId`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `followups`
--

INSERT INTO `followups` (`id`, `leadId`, `salesId`, `notes`, `followupDate`, `status`) VALUES
(1, 1, 2, 'Follow up pertama, tertarik lokasi', '2026-06-14', 'Pending'),
(2, 2, 2, 'Sudah direspon via WhatsApp', '2026-06-15', 'Done'),
(3, 3, 4, 'Tanya detail spesifikasi bangunan', '2026-06-16', 'Pending'),
(4, 4, 4, 'Sedang nego harga interior', '2026-06-17', 'Pending'),
(5, 5, 5, 'Lost contact, telepon tidak aktif', '2026-06-18', 'Cancelled');

-- --------------------------------------------------------

--
-- Table structure for table `reminders`
--

DROP TABLE IF EXISTS reminders;
CREATE TABLE `reminders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `followupId` int(11) NOT NULL,
  `reminderDate` datetime DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `followupId` (`followupId`),
  CONSTRAINT `reminders_ibfk_1` FOREIGN KEY (`followupId`) REFERENCES `followups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reminders`
--

INSERT INTO `reminders` (`id`, `followupId`, `reminderDate`, `status`) VALUES
(1, 1, '2026-06-14 09:00:00', 'Active'),
(2, 2, '2026-06-15 10:00:00', 'Done'),
(3, 3, '2026-06-16 14:00:00', 'Active'),
(4, 4, '2026-06-17 11:00:00', 'Active'),
(5, 5, '2026-06-18 16:30:00', 'Inactive');

-- --------------------------------------------------------

--
-- Table structure for table `notifikasi`
--

DROP TABLE IF EXISTS notifikasi;
CREATE TABLE `notifikasi` (
  `notifId` int(11) NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `sentAt` datetime DEFAULT NULL,
  `isRead` boolean NOT NULL DEFAULT 0,
  PRIMARY KEY (`notifId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifikasi`
--

INSERT INTO `notifikasi` (`notifId`, `message`, `sentAt`, `isRead`) VALUES
(1, 'FollowUp baru telah dijadwalkan untuk Lead Jane Doe', '2026-06-13 08:00:00', 0),
(2, 'Reminder: Jadwal follow up Neps 1 jam lagi', '2026-06-13 09:00:00', 1),
(3, 'Lead baru ditambahkan oleh Sales Fathan', '2026-06-13 10:15:00', 0),
(4, 'Status Lead Cyrene berubah menjadi Negotiation', '2026-06-13 11:30:00', 0),
(5, 'Reminder: Follow up Mydeimos tertunda', '2026-06-13 13:00:00', 1);

COMMIT;
SET FOREIGN_KEY_CHECKS=1;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;