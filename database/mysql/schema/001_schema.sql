CREATE DATABASE IF NOT EXISTS petvet;
USE petvet;

CREATE TABLE clinics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    document VARCHAR(30) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clinic_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(40) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (role IN ('ADMIN', 'EMPLOYEE', 'SUPPORT')),
    CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_users_clinic_id ON users (clinic_id);
CREATE INDEX idx_users_email ON users (email);

CREATE TABLE tutors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clinic_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20) NOT NULL,
    cpf VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tutors_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uq_tutor_clinic_phone (clinic_id, phone)
);

CREATE INDEX idx_tutors_clinic_id ON tutors (clinic_id);
CREATE INDEX idx_tutors_phone ON tutors (phone);

CREATE TABLE pets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clinic_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    sex VARCHAR(20),
    birth_date DATE,
    weight_kg DECIMAL(5,2),
    chip_code VARCHAR(50),
    notes TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_pets_tutor FOREIGN KEY (tutor_id) REFERENCES tutors(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (species IN ('DOG', 'CAT', 'BIRD', 'OTHER')),
    CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_pets_clinic_id ON pets (clinic_id);
CREATE INDEX idx_pets_tutor_id ON pets (tutor_id);
CREATE INDEX idx_pets_status ON pets (status);

CREATE TABLE pet_health_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    record_type VARCHAR(40) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    performed_at TIMESTAMP NOT NULL,
    next_due_date DATE,
    created_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_health_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_health_user FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (record_type IN ('VACCINE', 'WORMER', 'CONSULTATION', 'PROCEDURE'))
);

CREATE INDEX idx_health_pet_id ON pet_health_records (pet_id);
CREATE INDEX idx_health_next_due_date ON pet_health_records (next_due_date);

CREATE TABLE reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    record_id BIGINT NOT NULL,
    reminder_type VARCHAR(40) NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    channel VARCHAR(30) NOT NULL DEFAULT 'WHATSAPP',
    message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reminders_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_reminders_record FOREIGN KEY (record_id) REFERENCES pet_health_records(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (reminder_type IN ('VACCINE', 'WORMER', 'RETURN')),
    CHECK (status IN ('PENDING', 'SENT', 'CONFIRMED', 'FAILED', 'CANCELLED')),
    CHECK (channel IN ('WHATSAPP', 'SMS', 'EMAIL'))
);

CREATE INDEX idx_reminders_pet_id ON reminders (pet_id);
CREATE INDEX idx_reminders_status ON reminders (status);
CREATE INDEX idx_reminders_scheduled_at ON reminders (scheduled_at);

CREATE TABLE subscription_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clinic_id BIGINT NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_plan_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (billing_cycle IN ('MONTHLY', 'QUARTERLY', 'YEARLY')),
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'CANCELLED'))
);

CREATE TABLE billings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    clinic_id BIGINT NOT NULL,
    subscription_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_billing_clinic FOREIGN KEY (clinic_id) REFERENCES clinics(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_billing_subscription FOREIGN KEY (subscription_id) REFERENCES subscription_plans(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CHECK (status IN ('PENDING', 'PAID', 'OVERDUE', 'CANCELLED'))
);

CREATE INDEX idx_billings_clinic_id ON billings (clinic_id);
CREATE INDEX idx_billings_status ON billings (status);

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    entity_name VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    field_name VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'LOGIN'))
);

CREATE INDEX idx_audit_entity ON audit_logs (entity_name, entity_id);
CREATE INDEX idx_audit_user_id ON audit_logs (user_id);
