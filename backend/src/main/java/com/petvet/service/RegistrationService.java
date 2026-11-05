package com.petvet.service;

import com.petvet.model.Clinic;
import com.petvet.model.User;
import com.petvet.repository.ClinicRepository;
import com.petvet.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Cadastro público: cria a clínica e o primeiro usuário dela na mesma transação.
 *
 * <p>O schema exige {@code users.clinic_id} e só aceita os papéis ADMIN, EMPLOYEE e SUPPORT.
 * O usuário criado aqui é EMPLOYEE de propósito: no {@code AuthzService}, ROLE_ADMIN acessa
 * qualquer clínica, então esse papel fica reservado à equipe da plataforma e nunca sai de um
 * cadastro público.
 */
@Service
public class RegistrationService {

    static final String SELF_SIGNUP_ROLE = "EMPLOYEE";

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository,
                               ClinicRepository clinicRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String name, String email, String password, String clinicName, String clinicDocument) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedDocument = normalizeDocument(clinicDocument);

        if (!isStrongPassword(password)) {
            throw new RegistrationException(
                "Password must contain at least 12 characters, including uppercase, lowercase, number and symbol.");
        }
        if (normalizedDocument.length() != 11 && normalizedDocument.length() != 14) {
            throw new RegistrationException("Clinic document must be a CPF (11 digits) or a CNPJ (14 digits).");
        }
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new RegistrationException("Email already registered.");
        }
        if (clinicRepository.existsByDocument(normalizedDocument)) {
            throw new RegistrationException("Clinic document already registered.");
        }

        LocalDateTime now = LocalDateTime.now();

        Clinic clinic = new Clinic();
        clinic.setName(clinicName.trim());
        clinic.setDocument(normalizedDocument);
        clinic.setStatus("ACTIVE");
        clinic.setCreatedAt(now);
        Clinic savedClinic = clinicRepository.save(clinic);

        User user = new User();
        user.setClinic(savedClinic);
        user.setName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(SELF_SIGNUP_ROLE);
        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        return userRepository.save(user);
    }

    static String normalizeDocument(String document) {
        return document == null ? "" : document.replaceAll("\\D", "");
    }

    static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 12) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSymbol = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUpper && hasLower && hasDigit && hasSymbol;
    }

    /** Erro de regra do cadastro; a mensagem pode ser devolvida ao cliente. */
    public static class RegistrationException extends RuntimeException {
        public RegistrationException(String message) {
            super(message);
        }
    }
}
