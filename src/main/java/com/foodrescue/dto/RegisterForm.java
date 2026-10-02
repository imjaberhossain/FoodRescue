package com.foodrescue.dto;

import com.foodrescue.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
<<<<<<< HEAD

=======
/**
 * Holds and validates user registration input data.
 */
>>>>>>> b3947b32b8c1031c4bab383c7ff83f179a94df59
public class RegisterForm {

    @NotNull(message = "Please choose an account type")
    private User.Role role = User.Role.PROVIDER;

    @NotBlank(message = "Please enter your full name")
    @Size(max = 100, message = "Name is too long")
    private String fullName;

    @NotBlank(message = "Please enter your email")
    @Email(message = "Please enter a valid email address")
    @Size(max = 150, message = "Email is too long")
    private String email;

    @NotBlank(message = "Please enter a password")
    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
    private String password;

    @Size(max = 20, message = "Phone number is too long")
    private String phone;

    /** Business name (Provider) or organization name (NGO). Not used for Volunteer. */
    @Size(max = 150, message = "Name is too long")
    private String organizationName;

    @NotBlank(message = "Please enter your address")
    @Size(max = 255, message = "Address is too long")
    private String address;

    @NotBlank(message = "Please enter your city")
    @Size(max = 100, message = "City name is too long")
    private String city;

    // ---------- NGO / Volunteer verification (checked manually in AuthService,
    //            since they are required only for those two roles) ----------

    private String idDocumentType;
    private String idDocumentNumber;
    private MultipartFile idDocumentFile;

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getIdDocumentType() { return idDocumentType; }
    public void setIdDocumentType(String idDocumentType) { this.idDocumentType = idDocumentType; }
    public String getIdDocumentNumber() { return idDocumentNumber; }
    public void setIdDocumentNumber(String idDocumentNumber) { this.idDocumentNumber = idDocumentNumber; }
    public MultipartFile getIdDocumentFile() { return idDocumentFile; }
    public void setIdDocumentFile(MultipartFile idDocumentFile) { this.idDocumentFile = idDocumentFile; }
}
