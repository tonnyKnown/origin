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
