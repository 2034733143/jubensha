-- 剧本杀预约与场次管理系统 数据库脚本
-- MySQL 8.0 / utf8mb4

DROP DATABASE IF EXISTS jubensha;
CREATE DATABASE jubensha DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE jubensha;

CREATE TABLE users (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL UNIQUE,
  password    VARCHAR(64)  NOT NULL COMMENT 'SHA-256',
  real_name   VARCHAR(50),
  phone       VARCHAR(20),
  role        VARCHAR(10)  NOT NULL DEFAULT 'USER' COMMENT 'USER/ADMIN',
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

CREATE TABLE scripts (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  title        VARCHAR(100) NOT NULL,
  author       VARCHAR(50),
  category     VARCHAR(20)  NOT NULL COMMENT '硬核推理/情感沉浸/机制阵营/恐怖惊悚',
  difficulty   VARCHAR(10)  NOT NULL COMMENT '新手/进阶/硬核',
  min_players  INT          NOT NULL,
  max_players  INT          NOT NULL,
  duration_min INT          NOT NULL,
  price        DECIMAL(10,2) NOT NULL,
  cover_color  VARCHAR(20)  NOT NULL DEFAULT '#6b4bd6',
  summary      VARCHAR(200),
  description  TEXT,
  status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='剧本';

CREATE TABLE game_sessions (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  script_id   INT          NOT NULL,
  room        VARCHAR(50),
  dm_name     VARCHAR(50),
  start_time  DATETIME     NOT NULL,
  end_time    DATETIME     NOT NULL,
  capacity    INT          NOT NULL,
  booked      INT          NOT NULL DEFAULT 0,
  price       DECIMAL(10,2) NOT NULL,
  status      VARCHAR(12)  NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/FULL/STARTED/FINISHED/CANCELED',
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_start (start_time),
  CONSTRAINT fk_sess_script FOREIGN KEY (script_id) REFERENCES scripts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场次';

CREATE TABLE bookings (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  code          VARCHAR(20)  NOT NULL UNIQUE,
  session_id    INT          NOT NULL,
  user_id       INT          NOT NULL,
  player_count  INT          NOT NULL,
  amount        DECIMAL(10,2) NOT NULL,
  contact_name  VARCHAR(50),
  contact_phone VARCHAR(20),
  status        VARCHAR(12)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/CONFIRMED/FINISHED/CANCELED',
  remark        VARCHAR(200),
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_user (user_id),
  KEY idx_session (session_id),
  CONSTRAINT fk_book_session FOREIGN KEY (session_id) REFERENCES game_sessions(id),
  CONSTRAINT fk_book_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约';

CREATE TABLE reviews (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  booking_id INT          NOT NULL UNIQUE,
  script_id  INT          NOT NULL,
  user_id    INT          NOT NULL,
  rating     INT          NOT NULL COMMENT '1-5',
  content    VARCHAR(500),
  visible    TINYINT      NOT NULL DEFAULT 1,
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_script (script_id),
  CONSTRAINT fk_rev_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
  CONSTRAINT fk_rev_script FOREIGN KEY (script_id) REFERENCES scripts(id),
  CONSTRAINT fk_rev_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价';

-- ---------- 种子数据 ----------
INSERT INTO users (username, password, real_name, phone, role) VALUES
('admin',    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '店长', '13800000001', 'ADMIN'),
('xiaoming', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '张小明', '13900000002', 'USER'),
('tingting', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '李婷婷', '13900000003', 'USER');

INSERT INTO scripts (title, author, category, difficulty, min_players, max_players, duration_min, price, cover_color, summary, description, status) VALUES
('雾隐山庄', '墨白', '硬核推理', '硬核', 6, 6, 300, 168.00, '#3e4a7d', '暴雪封山的庄园里，六位继承人在等一场迟到的遗嘱宣读。', '民国年间，雾隐山庄主人离奇坠楼。六位继承人在暴雪之夜被困山庄，管家递上遗嘱的那一刻，客厅的灯灭了。三十分钟后，灯亮起，新的尸体出现在书房。每个人都在说谎，而凶手就在你们中间。线索环环相扣，适合喜欢盘逻辑的硬核玩家。', 1),
('长夜将尽', '青禾', '情感沉浸', '进阶', 4, 7, 240, 138.00, '#7d3e63', '如果重逢是最后一次告别，你还会走进那家旧书店吗？', '故事发生在一座即将拆迁的老城。五封没有寄出的信、一间二十四小时营业的旧书店、一场没能说出口的告白。玩家将在四小时里经历三段人生，完成那些来不及完成的事。情感本，节奏偏慢，需要玩家有一定沉浸意愿。', 1),
('第七个访客', '南屿', '恐怖惊悚', '进阶', 5, 6, 270, 158.00, '#2c5a4f', '旅馆只有六间房，可登记簿上出现了第七个名字。', '山间旅馆每逢十三年便有一场"接待"。第六位访客入住的那个雨夜，前台发现登记簿上多了一行笔迹。恐怖惊悚机制本，含惊吓桥段与追逐环节，心脏不适者慎入。', 1),
('山海谣', '白鹭', '机制阵营', '进阶', 6, 8, 300, 148.00, '#6b5733', '上古山海异兽苏醒，八方部族在血色月下重新结盟。', '以山海经为背景的阵营机制本。玩家分为四族，通过探索、谈判、交换资源来争夺山海图。机制环节共三轮，每轮都有隐藏任务，适合喜欢博弈与表演的玩家。', 1),
('玫瑰谋杀案', '沈砚', '硬核推理', '硬核', 5, 5, 240, 128.00, '#7a3535', '剧院散场后，后台的玫瑰花瓣上留下了第三枚指纹。', '知名女演员在首演之夜死于化妆间，现场只有一束玫瑰与一支断掉的口红。五位嫌疑人各有不在场证明，而每个人的证明都恰好缺了十分钟。经典密室+时间线诡计。', 1),
('遗忘之城', '鹿鸣', '情感沉浸', '新手', 4, 6, 210, 108.00, '#3f5473', '在这座城市，遗忘是一种需要付费的服务。', '新手友好的情感本。玩家扮演六位在"记忆诊所"登记的客人，通过交换记忆片段还原一段共同经历。没有复杂推理，重体验与台词，适合团建与新手上车。', 1),
('雪落无声', '孤舟', '恐怖惊悚', '硬核', 5, 7, 280, 168.00, '#37424e', '雪原哨站的无线电，每晚都会收到同一个求救信号。', '极寒雪原上的第七哨站，八年前全员失踪。新一批调查员抵达后，无线电在午夜准时响起，坐标指向哨站地下的冰层。惊悚+硬核双属性，含大量细节线索。', 1),
('机械之心', '霍尔', '机制阵营', '新手', 6, 8, 320, 158.00, '#4c3f6e', '当仿生人开始梦见机械之心，谁才是真正的造物主？', '科幻背景欢乐机制本。玩家扮演六个觉醒的仿生人，需要通过"情感测试"骗取人类身份。机制轻松、笑点多，适合新手与朋友局。', 1);

INSERT INTO game_sessions (script_id, room, dm_name, start_time, end_time, capacity, booked, price, status) VALUES
(1, 'A 雾隐厅', '阿岚', TIMESTAMP(CURDATE(), '19:00:00'), DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL 300 MINUTE), 6, 2, 168.00, 'OPEN'),
(2, 'B 长夜厅', '小满', TIMESTAMP(CURDATE(), '19:30:00'), DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL 240 MINUTE), 7, 2, 138.00, 'OPEN'),
(3, 'C 惊悚厅', '老K', TIMESTAMP(CURDATE(), '20:00:00'), DATE_ADD(TIMESTAMP(CURDATE(), '20:00:00'), INTERVAL 270 MINUTE), 6, 0, 158.00, 'OPEN'),
(4, 'D 山海厅', '阿泽', DATE_ADD(TIMESTAMP(CURDATE(), '14:00:00'), INTERVAL 1 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '14:00:00'), INTERVAL 1 DAY) + INTERVAL 300 MINUTE, 8, 0, 148.00, 'OPEN'),
(5, 'A 雾隐厅', '阿岚', DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL 1 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL 1 DAY) + INTERVAL 240 MINUTE, 5, 0, 128.00, 'OPEN'),
(6, 'E 遗忘厅', '小满', DATE_ADD(TIMESTAMP(CURDATE(), '15:00:00'), INTERVAL 1 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '15:00:00'), INTERVAL 1 DAY) + INTERVAL 210 MINUTE, 6, 2, 108.00, 'OPEN'),
(7, 'C 惊悚厅', '老K', DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL 1 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL 1 DAY) + INTERVAL 280 MINUTE, 7, 0, 168.00, 'OPEN'),
(8, 'D 山海厅', '阿泽', DATE_ADD(TIMESTAMP(CURDATE(), '13:30:00'), INTERVAL 2 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '13:30:00'), INTERVAL 2 DAY) + INTERVAL 320 MINUTE, 8, 0, 158.00, 'OPEN'),
(1, 'A 雾隐厅', '阿岚', DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL -1 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL -1 DAY) + INTERVAL 300 MINUTE, 6, 2, 168.00, 'FINISHED'),
(2, 'B 长夜厅', '小满', DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL -1 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL -1 DAY) + INTERVAL 240 MINUTE, 7, 3, 138.00, 'FINISHED'),
(5, 'A 雾隐厅', '阿岚', DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL -2 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '19:00:00'), INTERVAL -2 DAY) + INTERVAL 240 MINUTE, 5, 2, 128.00, 'FINISHED'),
(3, 'C 惊悚厅', '老K', DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL -3 DAY), DATE_ADD(TIMESTAMP(CURDATE(), '19:30:00'), INTERVAL -3 DAY) + INTERVAL 270 MINUTE, 6, 2, 158.00, 'FINISHED');

INSERT INTO bookings (code, session_id, user_id, player_count, amount, contact_name, contact_phone, status, remark) VALUES
('JB0A1B2C3D', 9, 2, 2, 336.00, '张小明', '13900000002', 'FINISHED', '希望坐靠窗的位置'),
('JB4E5F6G7H', 10, 3, 3, 414.00, '李婷婷', '13900000003', 'FINISHED', ''),
('JB8I9J0K1L', 11, 2, 2, 256.00, '张小明', '13900000002', 'FINISHED', ''),
('JB2M3N4O5P', 12, 3, 2, 316.00, '李婷婷', '13900000003', 'FINISHED', ''),
('JB6Q7R8S9T', 1, 2, 2, 336.00, '张小明', '13900000002', 'PENDING', '第一次玩硬核本'),
('JBCU1D2E3F', 2, 3, 2, 276.00, '李婷婷', '13900000003', 'CONFIRMED', ''),
('JBG4H5I6J7', 6, 2, 2, 216.00, '张小明', '13900000002', 'CONFIRMED', '');

INSERT INTO reviews (booking_id, script_id, user_id, rating, content, visible) VALUES
(1, 1, 2, 5, 'DM 阿岚节奏把控得非常好，第二轮的线索复盘让人起鸡皮疙瘩。剧本细节很多，值得二刷。', 1),
(2, 2, 3, 4, '情感线写得很细腻，中途确实红了眼眶。时长稍长，后半段略有拖沓。', 1),
(3, 5, 2, 5, '密室诡计设计得很精巧，我们车队盘了三个小时才理清时间线，最后全员起立鼓掌。', 1),
(4, 3, 3, 3, '氛围营造到位，但机制部分略显单薄，恐怖桥段对心脏不太友好。', 1);
