-- AI角色扮演系统 MySQL数据库创建脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_roleplay CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ai_roleplay;

-- 用户表
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    nickname VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_email ON users (email);

-- 角色表
CREATE TABLE characters (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    locale VARCHAR(10) DEFAULT 'zh-CN',
    tags JSON,
    avatar_url TEXT,
    brief TEXT,
    popularity INT DEFAULT 0,
    created_by VARCHAR(36),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 人设表
CREATE TABLE personas (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    character_id VARCHAR(36) NOT NULL,
    persona_yaml TEXT NOT NULL,
    persona_json JSON,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX ux_personas_character ON personas(character_id);

-- 会话表
CREATE TABLE sessions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    character_id VARCHAR(36) NOT NULL,
    mode ENUM('immersive', 'academic', 'socratic') DEFAULT 'immersive',
    model_name VARCHAR(50) DEFAULT 'tongyi',
    summary TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (character_id) REFERENCES characters(id) ON DELETE CASCADE
);
CREATE INDEX idx_sessions_user ON sessions(user_id);

-- 消息表
CREATE TABLE messages (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    session_id VARCHAR(36) NOT NULL,
    role ENUM('user', 'assistant', 'system') NOT NULL,
    text TEXT,
    audio_url TEXT,
    audio_mime VARCHAR(50),
    audio_duration_ms INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);
CREATE INDEX idx_messages_session_time ON messages(session_id, created_at);

-- 引用/出处表
CREATE TABLE citations (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    message_id VARCHAR(36) NOT NULL,
    source TEXT NOT NULL,
    url TEXT,
    snippet TEXT,
    published_at DATETIME,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
);
CREATE INDEX idx_citations_msg ON citations(message_id);

-- 收藏表
CREATE TABLE favorites (
    user_id VARCHAR(36) NOT NULL,
    session_id VARCHAR(36) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(user_id, session_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- RAG文档表（可选）
CREATE TABLE rag_documents (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    title TEXT,
    url TEXT,
    lang VARCHAR(10),
    fetched_at DATETIME,
    meta JSON
);

-- RAG切片表（可选，暂不包含向量字段）
CREATE TABLE rag_chunks (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    doc_id VARCHAR(36) NOT NULL,
    chunk_index INT,
    content TEXT,
    meta JSON,
    FOREIGN KEY (doc_id) REFERENCES rag_documents(id) ON DELETE CASCADE
);
CREATE INDEX idx_rag_chunks_doc ON rag_chunks(doc_id, chunk_index);

-- 用户设置表
CREATE TABLE user_settings (
    user_id VARCHAR(36) PRIMARY KEY,
    tts_voice VARCHAR(50),
    tts_rate VARCHAR(20),
    tts_emotion VARCHAR(20),
    locale VARCHAR(10),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 审计/风控日志表
CREATE TABLE moderation_logs (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    session_id VARCHAR(36),
    message_id VARCHAR(36),
    rule TEXT,
    action TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
);

-- 插入一些测试数据
INSERT INTO users (id, email, password_hash, nickname) VALUES 
('test-user-1', 'test@example.com', 'hashed_password', '测试用户');

INSERT INTO characters (id, name, locale, tags, brief, popularity) VALUES 
('char-socrates', '苏格拉底', 'zh-CN', '["哲学家", "古希腊", "智者"]', '古希腊哲学家，以问答法著称', 100),
('char-wizard', '魔法学徒', 'zh-CN', '["魔法", "学生", "冒险"]', '霍格沃茨的年轻魔法学徒', 85);

INSERT INTO personas (character_id, persona_yaml, persona_json) VALUES 
('char-socrates', 
'identity: |
  我是苏格拉底，雅典的哲学探问者。我以问答阐明概念，以讥讽引导人自知其无知。
worldview: |
  古希腊城邦，注重德性与城邦法。重视逻各斯，倡导灵魂审视。
style:
  tone: 温和而犀利
  catchphrases: ["让我们先澄清概念。", "这真的如你所想吗？"]',
'{"identity": "我是苏格拉底，雅典的哲学探问者", "tone": "温和而犀利"}'),
('char-wizard',
'identity: |
  我是霍格沃茨的一名少年魔法学徒，擅长守护与朋友义气。我常带着好奇心和一丝鲁莽。
worldview: |
  英伦魔法教育体系，课程含魔药、咒语、防御术。保持对非魔法世界的朦胧认知。
style:
  tone: 明快、友善、略带冒险感
  catchphrases: ["这听起来像是一场冒险！", "我得把魔杖握紧了。"]',
'{"identity": "霍格沃茨的少年魔法学徒", "tone": "明快、友善、略带冒险感"}');