USE petvet;

INSERT INTO clinics (name, document, phone, address, status) VALUES
('PetCare Vet', '12.345.678/0001-90', '(11) 3000-0001', 'Rua das Flores, 123 - São Paulo/SP', 'ACTIVE'),
('PetLife Clinica', '98.765.432/0001-21', '(11) 3000-0002', 'Av. Principal, 456 - São Paulo/SP', 'ACTIVE');

INSERT INTO users (clinic_id, name, email, password_hash, phone, role, status) VALUES
(1, 'Maria Admin', 'maria@petcare.com', '$2a$10$QqA0B8nYw4aYqKJxKx5EOeC7Yi6mLqQ6M0jZ9m1z2m7B9x7e8u9u6', '(11) 99999-0001', 'ADMIN', 'ACTIVE'),
(1, 'João Funcionário', 'joao@petcare.com', '$2a$10$QqA0B8nYw4aYqKJxKx5EOeC7Yi6mLqQ6M0jZ9m1z2m7B9x7e8u9u6', '(11) 99999-0002', 'EMPLOYEE', 'ACTIVE'),
(2, 'Ana Suporte', 'ana@petlife.com', '$2a$10$QqA0B8nYw4aYqKJxKx5EOeC7Yi6mLqQ6M0jZ9m1z2m7B9x7e8u9u6', '(11) 99999-0003', 'SUPPORT', 'ACTIVE');

INSERT INTO tutors (clinic_id, name, email, phone, cpf, address) VALUES
(1, 'Carlos Pereira', 'carlos@email.com', '(11) 98888-0001', '123.456.789-01', 'Rua B, 10 - São Paulo/SP'),
(1, 'Fernanda Costa', 'fernanda@email.com', '(11) 98888-0002', '987.654.321-09', 'Av. C, 20 - São Paulo/SP');

INSERT INTO pets (clinic_id, tutor_id, name, species, breed, sex, birth_date, weight_kg, chip_code, notes, status) VALUES
(1, 1, 'Thor', 'DOG', 'Labrador', 'MALE', '2020-04-10', 28.00, 'PET-001', 'Castrado e vacinado', 'ACTIVE'),
(1, 2, 'Luna', 'CAT', 'Siamês', 'FEMALE', '2021-08-23', 4.20, 'PET-002', 'Sensível a mudanças', 'ACTIVE');

INSERT INTO pet_health_records (pet_id, record_type, title, description, performed_at, next_due_date, created_by_user_id) VALUES
(1, 'VACCINE', 'Vacina antirrábica', 'Vacina anual de antirrábica aplicada', '2024-01-15 10:00:00', '2025-01-15', 2),
(2, 'WORMER', 'Vermífugo', 'Aplicado vermífugo mensal', '2024-02-05 09:30:00', '2025-02-05', 2);

INSERT INTO reminders (pet_id, record_id, reminder_type, scheduled_at, status, channel, message) VALUES
(1, 1, 'VACCINE', '2025-01-10 09:00:00', 'PENDING', 'WHATSAPP', 'Olá, Thor está perto da vacina anual. Agende a consulta!'),
(2, 2, 'WORMER', '2025-02-01 09:00:00', 'PENDING', 'WHATSAPP', 'Luna está perto do próximo vermífugo. Não deixe passar!');

INSERT INTO subscription_plans (clinic_id, plan_name, price, billing_cycle, status) VALUES
(1, 'Starter', 149.90, 'MONTHLY', 'ACTIVE'),
(2, 'Growth', 249.90, 'MONTHLY', 'ACTIVE');

INSERT INTO billings (clinic_id, subscription_id, amount, due_date, status) VALUES
(1, 1, 149.90, '2025-09-05', 'PENDING'),
(2, 2, 249.90, '2025-09-05', 'PENDING');
