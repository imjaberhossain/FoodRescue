package com.foodrescue.service;

import com.foodrescue.dto.RegisterForm;
import com.foodrescue.model.FoodProvider;
import com.foodrescue.model.Organization;
import com.foodrescue.model.User;
import com.foodrescue.model.VerificationStatus;
import com.foodrescue.model.Volunteer;
import com.foodrescue.repository.FoodProviderRepository;
import com.foodrescue.repository.OrganizationRepository;
import com.foodrescue.repository.UserRepository;
import com.foodrescue.repository.VolunteerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
<<<<<<< HEAD

=======
/**
 * Handles user registration and authentication-related business logic.
 */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
@Service
public class AuthService {

    private final UserRepository userRepo;
    private final FoodProviderRepository providerRepo;
    private final OrganizationRepository orgRepo;
    private final VolunteerRepository volunteerRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorage;

    public AuthService(UserRepository userRepo,
                       FoodProviderRepository providerRepo,
                       OrganizationRepository orgRepo,
                       VolunteerRepository volunteerRepo,
                       PasswordEncoder passwordEncoder,
                       FileStorageService fileStorage) {
        this.userRepo = userRepo;
        this.providerRepo = providerRepo;
        this.orgRepo = orgRepo;
        this.volunteerRepo = volunteerRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileStorage = fileStorage;
    }

    /** Creates a user and the matching Provider / Organization(NGO) / Volunteer row. */
    @Transactional
    public User register(RegisterForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("This email is already registered. Try logging in.");
        }

        // NGO and Volunteer must supply an NID / document to verify. Providers do not.
        boolean needsVerification = form.getRole() == User.Role.NGO || form.getRole() == User.Role.VOLUNTEER;
        if (needsVerification) {
            if (isBlank(form.getIdDocumentType()) || isBlank(form.getIdDocumentNumber())) {
                throw new IllegalArgumentException("Please provide your document type and number for verification.");
            }
            if (form.getIdDocumentFile() == null || form.getIdDocumentFile().isEmpty()) {
                throw new IllegalArgumentException("Please upload a photo of your NID or document for verification.");
            }
        }
        if (form.getRole() != User.Role.VOLUNTEER && isBlank(form.getOrganizationName())) {
            throw new IllegalArgumentException("Please enter a business or organization name.");
        }

        User user = new User();
        user.setFullName(form.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setPhone(blankToNull(form.getPhone()));
        user.setRole(form.getRole());
        userRepo.save(user);

        switch (form.getRole()) {
            case PROVIDER -> {
                FoodProvider provider = new FoodProvider();
                provider.setUser(user);
                provider.setBusinessName(form.getOrganizationName().trim());
                provider.setAddress(form.getAddress().trim());
                provider.setCity(form.getCity().trim());
                providerRepo.save(provider);
            }
            case NGO -> {
                String docPath = fileStorage.store(form.getIdDocumentFile(), "documents");
                Organization org = new Organization();
                org.setUser(user);
                org.setOrgName(form.getOrganizationName().trim());
                org.setAddress(form.getAddress().trim());
                org.setCity(form.getCity().trim());
                org.setIdDocumentType(form.getIdDocumentType().trim());
                org.setIdDocumentNumber(form.getIdDocumentNumber().trim());
                org.setIdDocumentPath(docPath);
                org.setVerificationStatus(VerificationStatus.PENDING);
                orgRepo.save(org);
            }
            case VOLUNTEER -> {
                String docPath = fileStorage.store(form.getIdDocumentFile(), "documents");
                Volunteer volunteer = new Volunteer();
                volunteer.setUser(user);
                volunteer.setAddress(form.getAddress().trim());
                volunteer.setCity(form.getCity().trim());
                volunteer.setIdDocumentType(form.getIdDocumentType().trim());
                volunteer.setIdDocumentNumber(form.getIdDocumentNumber().trim());
                volunteer.setIdDocumentPath(docPath);
                volunteer.setVerificationStatus(VerificationStatus.PENDING);
                volunteerRepo.save(volunteer);
            }
        }
        return user;
    }

    /** Returns the user if the email and password are correct. */
    @Transactional(readOnly = true)
    public Optional<User> authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null || email.isBlank()) {
            return Optional.empty();
        }
        return userRepo.findByEmailIgnoreCase(email.trim())
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
