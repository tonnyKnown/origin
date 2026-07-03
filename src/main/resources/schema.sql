CREATE TABLE IF NOT EXISTS interview_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    position_type VARCHAR(100) NOT NULL,
    current_index INT NOT NULL DEFAULT 1,
    total_score INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    overall_comment TEXT NULL,
    improvement_advice TEXT NULL,
    finished_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS interview_direction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    level INT NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(500) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_interview_direction_parent_id (parent_id),
    INDEX idx_interview_direction_enabled (enabled),
    INDEX idx_interview_direction_sort (level, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS interview_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    question_index INT NOT NULL,
    question_type VARCHAR(50) NULL,
    question_content TEXT NOT NULL,
    reference_answer TEXT NOT NULL,
    scoring_rule TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_interview_question_interview_id (interview_id),
    UNIQUE KEY uk_interview_question_index (interview_id, question_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS interview_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_answer TEXT NOT NULL,
    score INT NOT NULL,
    answer_summary TEXT NULL,
    ai_comment TEXT NOT NULL,
    suggestion TEXT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NULL,
    revision_count INT NOT NULL DEFAULT 0,
    INDEX idx_interview_answer_interview_id (interview_id),
    UNIQUE KEY uk_interview_answer_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS manual_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_content TEXT NOT NULL,
    understanding_level VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    ai_answer TEXT NULL,
    answered_at DATETIME NULL,
    manual_remark TEXT NULL,
    remark_updated_at DATETIME NULL,
    self_test_answer TEXT NULL,
    self_test_score INT NULL,
    self_test_comment TEXT NULL,
    self_test_suggestion TEXT NULL,
    self_test_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_manual_question_created_at (created_at),
    INDEX idx_manual_question_level (understanding_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE interview_session ADD COLUMN overall_comment TEXT NULL;
ALTER TABLE interview_session ADD COLUMN improvement_advice TEXT NULL;
ALTER TABLE interview_session ADD COLUMN finished_at DATETIME NULL;
ALTER TABLE interview_question ADD COLUMN question_type VARCHAR(50) NULL;
ALTER TABLE interview_answer ADD COLUMN answer_summary TEXT NULL;
ALTER TABLE interview_answer ADD COLUMN updated_at DATETIME NULL;
ALTER TABLE interview_answer ADD COLUMN revision_count INT NOT NULL DEFAULT 0;
ALTER TABLE manual_question ADD COLUMN manual_remark TEXT NULL;
ALTER TABLE manual_question ADD COLUMN remark_updated_at DATETIME NULL;
ALTER TABLE manual_question ADD COLUMN self_test_answer TEXT NULL;
ALTER TABLE manual_question ADD COLUMN self_test_score INT NULL;
ALTER TABLE manual_question ADD COLUMN self_test_comment TEXT NULL;
ALTER TABLE manual_question ADD COLUMN self_test_suggestion TEXT NULL;
ALTER TABLE manual_question ADD COLUMN self_test_at DATETIME NULL;
ALTER TABLE manual_question ADD COLUMN understanding_level VARCHAR(20) NOT NULL DEFAULT 'HIGH';

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT NULL, '软件开发方向', 1, 10, TRUE, '软件研发相关岗位', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM interview_direction WHERE parent_id IS NULL AND name = '软件开发方向');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT NULL, '运维方向', 1, 20, TRUE, '系统运维、DevOps、云原生相关岗位', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM interview_direction WHERE parent_id IS NULL AND name = '运维方向');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT NULL, '硬件工程师方向', 1, 30, TRUE, '硬件设计、测试、嵌入式硬件相关岗位', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM interview_direction WHERE parent_id IS NULL AND name = '硬件工程师方向');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT NULL, '机器人方向', 1, 40, TRUE, '机器人软件、控制、算法相关岗位', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM interview_direction WHERE parent_id IS NULL AND name = '机器人方向');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Java', 2, 10, TRUE, 'Java 技术方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '软件开发方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Java');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Python', 2, 20, TRUE, 'Python 技术方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '软件开发方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Python');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'C++', 2, 30, TRUE, 'C++ 技术方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '软件开发方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'C++');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'AI', 2, 40, TRUE, 'AI 应用与大模型方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '软件开发方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'AI');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Linux 运维', 2, 10, TRUE, 'Linux 系统运维方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '运维方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Linux 运维');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'DevOps', 2, 20, TRUE, 'DevOps 工程方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '运维方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'DevOps');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '云原生', 2, 30, TRUE, '容器与 Kubernetes 方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '运维方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '云原生');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '嵌入式硬件', 2, 10, TRUE, '嵌入式硬件方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '硬件工程师方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '嵌入式硬件');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '电路设计', 2, 20, TRUE, '电路与 PCB 方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '硬件工程师方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '电路设计');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '机器人软件', 2, 10, TRUE, '机器人软件工程方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '机器人方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '机器人软件');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '机器人算法', 2, 20, TRUE, '机器人算法方向', NOW(), NOW()
FROM interview_direction p
WHERE p.parent_id IS NULL AND p.name = '机器人方向'
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '机器人算法');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Java 后端开发', 3, 10, TRUE, 'Java 后端开发岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'Java' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Java 后端开发');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Java 架构师', 3, 20, TRUE, 'Java 架构师岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'Java' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Java 架构师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Python 后端开发', 3, 10, TRUE, 'Python 后端开发岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'Python' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Python 后端开发');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'C++ 服务端开发', 3, 10, TRUE, 'C++ 服务端开发岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'C++' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'C++ 服务端开发');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'AI 应用开发', 3, 10, TRUE, 'AI 应用开发岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'AI' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'AI 应用开发');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '大模型应用开发', 3, 20, TRUE, 'LLM 应用开发岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'AI' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '大模型应用开发');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '系统运维工程师', 3, 10, TRUE, 'Linux 系统运维岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'Linux 运维' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '系统运维工程师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'CI/CD 工程师', 3, 10, TRUE, '持续集成与持续交付岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = 'DevOps' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'CI/CD 工程师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'Kubernetes 运维工程师', 3, 10, TRUE, 'Kubernetes 运维岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = '云原生' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'Kubernetes 运维工程师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '硬件测试工程师', 3, 10, TRUE, '硬件测试岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = '嵌入式硬件' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '硬件测试工程师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'PCB 工程师', 3, 10, TRUE, 'PCB 设计岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = '电路设计' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'PCB 工程师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, '运动控制工程师', 3, 10, TRUE, '机器人运动控制岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = '机器人软件' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = '运动控制工程师');

INSERT INTO interview_direction (parent_id, name, level, sort_order, enabled, description, created_at, updated_at)
SELECT p.id, 'SLAM 算法工程师', 3, 10, TRUE, '机器人 SLAM 算法岗位', NOW(), NOW()
FROM interview_direction p
WHERE p.name = '机器人算法' AND p.level = 2
  AND NOT EXISTS (SELECT 1 FROM interview_direction c WHERE c.parent_id = p.id AND c.name = 'SLAM 算法工程师');
