-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 伺服器版本:                        10.8.3-MariaDB - mariadb.org binary distribution
-- 伺服器作業系統:                      Win64
-- HeidiSQL 版本:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 傾印 test 的資料庫結構
CREATE DATABASE IF NOT EXISTS `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `test`;

-- 傾印  資料表 test.hcmio 結構
CREATE TABLE IF NOT EXISTS `hcmio` (
  `TradeDate` char(8) NOT NULL,
  `BranchNo` char(4) NOT NULL,
  `CustSeq` char(7) NOT NULL,
  `DocSeq` char(5) NOT NULL,
  `Stock` char(6) DEFAULT NULL,
  `BsType` char(1) DEFAULT NULL,
  `Price` decimal(10,4) DEFAULT NULL,
  `Qty` decimal(9,0) DEFAULT NULL,
  `Amt` decimal(16,2) DEFAULT NULL,
  `Fee` decimal(8,0) DEFAULT NULL,
  `Tax` decimal(8,0) DEFAULT NULL,
  `StinTax` decimal(8,0) DEFAULT NULL,
  `NetAmt` decimal(16,2) DEFAULT NULL,
  `ModDate` char(8) DEFAULT NULL,
  `ModTime` char(6) DEFAULT NULL,
  `ModUser` char(10) DEFAULT NULL,
  PRIMARY KEY (`TradeDate`,`BranchNo`,`CustSeq`,`DocSeq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 取消選取資料匯出。

-- 傾印  資料表 test.holiday 結構
CREATE TABLE IF NOT EXISTS `holiday` (
  `Holiday` char(8) NOT NULL,
  PRIMARY KEY (`Holiday`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 取消選取資料匯出。

-- 傾印  資料表 test.mstmb 結構
CREATE TABLE IF NOT EXISTS `mstmb` (
  `Stock` char(6) COLLATE utf8mb3_bin NOT NULL,
  `StockName` varchar(20) COLLATE utf8mb3_bin NOT NULL DEFAULT '',
  `MarketType` char(1) COLLATE utf8mb3_bin DEFAULT NULL,
  `CurPrice` decimal(10,4) DEFAULT 0.0000,
  `RefPrice` decimal(10,4) DEFAULT 0.0000,
  `Currency` char(3) COLLATE utf8mb3_bin DEFAULT 'NTD',
  `ModDate` char(8) COLLATE utf8mb3_bin DEFAULT NULL,
  `ModTime` char(6) COLLATE utf8mb3_bin DEFAULT NULL,
  `ModUser` char(10) COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`Stock`),
  KEY `IDX_MSTMB` (`Stock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;

-- 取消選取資料匯出。

-- 傾印  資料表 test.tcnud 結構
CREATE TABLE IF NOT EXISTS `tcnud` (
  `TradeDate` char(8) NOT NULL,
  `BranchNo` char(4) NOT NULL,
  `CustSeq` char(7) NOT NULL,
  `DocSeq` char(5) NOT NULL,
  `Stock` char(6) DEFAULT NULL,
  `Price` decimal(10,4) DEFAULT NULL,
  `Qty` decimal(9,0) DEFAULT NULL,
  `RemainQty` decimal(9,0) DEFAULT NULL,
  `Fee` decimal(8,0) DEFAULT NULL,
  `Cost` decimal(16,2) DEFAULT NULL,
  `ModDate` char(8) DEFAULT NULL,
  `ModTime` char(6) DEFAULT NULL,
  `ModUser` char(10) DEFAULT NULL,
  PRIMARY KEY (`TradeDate`,`BranchNo`,`CustSeq`,`DocSeq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 取消選取資料匯出。

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
