-- 账号体系从邮箱切换为手机号：email 列改名 phone
-- 原 email 列若有唯一索引（如 uk_email），改列名后索引自动跟随列，无需重建
ALTER TABLE users CHANGE COLUMN email phone VARCHAR(20) NOT NULL;

-- 旧邮箱账号数据可整体废弃时执行：
-- TRUNCATE TABLE users;
