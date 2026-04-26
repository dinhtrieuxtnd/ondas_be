package com.example.ondas_be.application.service.port;

import com.example.ondas_be.application.dto.request.ChangePasswordRequest;
import com.example.ondas_be.application.dto.request.UpdateProfileRequest;
import com.example.ondas_be.application.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileServicePort {

    UserProfileResponse getMyProfile(String email);

    UserProfileResponse updateMyProfile(String email, UpdateProfileRequest request);

    UserProfileResponse updateAvatar(String email, MultipartFile avatarFile);

    void changePassword(String email, ChangePasswordRequest request);
}
