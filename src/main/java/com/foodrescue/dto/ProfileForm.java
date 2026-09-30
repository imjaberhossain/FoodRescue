package com.foodrescue.dto;

import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/** Editable part of a user's profile - kept small on purpose (name/role/city stay fixed). */
public class ProfileForm {

    @Size(max = 20, message = "Phone number is too long")
    private String phone;

    @Size(max = 500, message = "Please keep the bio under 500 characters")
    private String bio;

    private MultipartFile profilePhoto;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public MultipartFile getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(MultipartFile profilePhoto) { this.profilePhoto = profilePhoto; }
}
