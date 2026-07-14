CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    student_id VARCHAR(20) UNIQUE,
    real_name VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    status INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_auth (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    auth_status VARCHAR(20) DEFAULT 'not_verified',
    student_id VARCHAR(20),
    real_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_address (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    campus VARCHAR(100),
    building VARCHAR(100),
    room VARCHAR(50),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);