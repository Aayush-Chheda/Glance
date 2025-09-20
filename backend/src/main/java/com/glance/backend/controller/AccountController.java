package com.glance.backend.controller;

import com.glance.backend.model.AppUser;
import com.glance.backend.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "API for managing user accounts, profiles, and authentication.")
public class AccountController {

    private Long userImageId;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    IAccountService iAccountService;

    @Operation(summary = "Get a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
                    content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "200", description = "No users found")
    })
    @GetMapping("/list")
    public ResponseEntity<?> getUsersList() {
        List<AppUser> users = iAccountService.userList();
        if (users.isEmpty()) {
            return new ResponseEntity<>("No Users Found.", HttpStatus.OK);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get user information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserInfo(@Parameter(description = "Username to search for") @PathVariable String username) {
        AppUser user = iAccountService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("No Users Found.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(summary = "Find users by a partial or full username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
                    content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "200", description = "No users found matching the criteria")
    })
    @GetMapping("/findByUsername/{username}")
    public ResponseEntity<?> getUsersListByUsername(@Parameter(description = "Part of a username to search for") @PathVariable String username) {
        List<AppUser> users = iAccountService.getUsersListByUsername(username);
        if (users.isEmpty()) {
            return new ResponseEntity<>("No Users Found.", HttpStatus.OK);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Register a new user account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration details including username, email, and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "409", description = "Username or email already exists"),
            @ApiResponse(responseCode = "400", description = "An error occurred during registration")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody HashMap<String, String> request) {
        String username = request.get("username");
        if (iAccountService.findByUsername(username) != null) {
            return new ResponseEntity<>("usernameExist", HttpStatus.CONFLICT);
        }
        String email = request.get("email");
        if (iAccountService.findByEmail(email) != null) {
            return new ResponseEntity<>("emailExist", HttpStatus.CONFLICT);
        }
        String name = request.get("name");
        try {
            AppUser user = iAccountService.saveUser(name, username, email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occured", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update a user's profile details")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User profile details to update, including user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "An error occurred during the update")
    })
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody HashMap<String, String> request) {
        String id = request.get("id");
        AppUser user = iAccountService.findUserById(Long.parseLong(id));
        if (user == null) {
            return new ResponseEntity<>("userNotFound", HttpStatus.NOT_FOUND);
        }
        try {
            iAccountService.updateUser(user, request);
            userImageId = user.getId();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occured", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Upload a user's profile picture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User picture saved successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to save user picture")
    })
    @PostMapping("/photo/upload")
    public ResponseEntity<String> fileUpload(@Parameter(description = "The image file to upload") @RequestParam("image") MultipartFile multipartFile) {
        try {
            iAccountService.saveUserImage(multipartFile, userImageId);
            return new ResponseEntity<>("User Picture Saved!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User Picture Not Saved", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Change a user's password")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details for changing the password, including username, current password, and new password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user, incorrect current password, or passwords don't match")
    })
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody HashMap<String, String> request) {
        String username = request.get("username");
        AppUser appUser = iAccountService.findByUsername(username);
        if (appUser == null) {
            return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
        }
        String currentPassword = request.get("currentpassword");
        String newPassword = request.get("newpassword");
        String confirmpassword = request.get("confirmpassword");
        if (!newPassword.equals(confirmpassword)) {
            return new ResponseEntity<>("PasswordNotMatched", HttpStatus.BAD_REQUEST);
        }
        String userPassword = appUser.getPassword();
        try {
            if (newPassword != null && !newPassword.isEmpty() && !StringUtils.isEmpty(newPassword)) {
                if (bCryptPasswordEncoder.matches(currentPassword, userPassword)) {
                    iAccountService.updateUserPassword(appUser, newPassword);
                }
            } else {
                return new ResponseEntity<>("IncorrectCurrentPassword", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Password Changed Successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error Occured: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Request a password reset via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Email not found")
    })
    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@Parameter(description = "Email of the user to reset the password for") @PathVariable("email") String email) {
        AppUser user = iAccountService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<String>("emailNotFound", HttpStatus.BAD_REQUEST);
        }
        iAccountService.resetPassword(user);
        return new ResponseEntity<String>("EmailSent!", HttpStatus.OK);
    }

    @Operation(summary = "Delete a user account")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Username of the user to delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully")
    })
    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody HashMap<String, String> mapper) {
        String username = mapper.get("username");
        AppUser user = iAccountService.findByUsername(username);
        iAccountService.deleteUser(user);
        return new ResponseEntity<String>("User Deleted Successfully!", HttpStatus.OK);
    }
}