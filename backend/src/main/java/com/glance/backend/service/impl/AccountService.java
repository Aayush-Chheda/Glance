package com.glance.backend.service.impl;

import com.glance.backend.model.AppUser;
import com.glance.backend.model.Role;
import com.glance.backend.model.UserRole;
import com.glance.backend.repository.IAppUserRepository;
import com.glance.backend.repository.IRoleRepository;
import com.glance.backend.service.IAccountService;
import com.glance.backend.utility.Constants;
import com.glance.backend.utility.EmailConstructor;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountService implements IAccountService {

    @Autowired
    AccountService accountService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private IAppUserRepository appUserRepo;

    @Autowired
    private IRoleRepository roleRepo;

    @Autowired
    private EmailConstructor emailConstructor;

    @Autowired
    private JavaMailSender mailSender;
    @Override
    @Transactional
    public AppUser saveUser(String name, String username, String email) {
        String password = RandomStringUtils.randomAlphanumeric(10);
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        AppUser appUser = new AppUser();
        appUser.setPassword(encryptedPassword);
        appUser.setName(name);
        appUser.setUsername(username);
        appUser.setEmail(email);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(appUser, accountService.findUserRoleByName("USER")));
        appUser.setUserRoles(userRoles);
        appUserRepo.save(appUser);
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Constants.TEMP_USER.toPath());
            String fileName = appUser.getId() + ".png";
            Path path = Paths.get(Constants.USER_FOLDER + fileName);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));
        return appUser;
    }

    @Override
    public AppUser findByUsername(String username) {
        return appUserRepo.findByUsername(username);
    }

    @Override
    public AppUser findByEmail(String userEmail) {
        return appUserRepo.findByEmail(userEmail);
    }

    @Override
    public List<AppUser> userList() {
        return appUserRepo.findAll();
    }

    @Override
    public Role findUserRoleByName(String name) {
        return roleRepo.findRoleByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public void updateUserPassword(AppUser appUser, String newpassword) {
        String encryptedPassword = bCryptPasswordEncoder.encode(newpassword);
        appUser.setPassword(encryptedPassword);
        appUserRepo.save(appUser);
        //mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, newpassword));
    }

    @Override
    public AppUser updateUser(AppUser user, HashMap<String, String> request) {
        String name = request.get("name");
        // String username = request.get("username");
        String email = request.get("email");
        String bio = request.get("bio");
        user.setName(name);
        // appUser.setUsername(username);
        user.setEmail(email);
        user.setBio(bio);
        appUserRepo.save(user);
        //mailSender.send(emailConstructor.constructUpdateUserProfileEmail(user));
        return user;

    }

    public AppUser simpleSaveUser(AppUser user) {
        appUserRepo.save(user);
        //mailSender.send(emailConstructor.constructUpdateUserProfileEmail(user));
        return user;
    }

    @Override
    public AppUser findUserById(Long id) {
        return appUserRepo.findUserById(id);
    }

    @Override
    public void deleteUser(AppUser appUser) {
        appUserRepo.delete(appUser);

    }

    @Override
    public void resetPassword(AppUser user) {
        String password = RandomStringUtils.randomAlphanumeric(10);
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        appUserRepo.save(user);
       // mailSender.send(emailConstructor.constructResetPasswordEmail(user, password));

    }

    @Override
    public List<AppUser> getUsersListByUsername(String username) {
        return appUserRepo.findByUsernameContaining(username);
    }

    @Override
    public String saveUserImage(MultipartFile multipartFile, Long userImageId) {
        /*
         * MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
         * request; Iterator<String> it = multipartRequest.getFileNames(); MultipartFile
         * multipartFile = multipartRequest.getFile(it.next());
         */
        byte[] bytes;
        try {
            Files.deleteIfExists(Paths.get(Constants.USER_FOLDER + "/" + userImageId + ".png"));
            bytes = multipartFile.getBytes();
            Path path = Paths.get(Constants.USER_FOLDER + userImageId + ".png");
            Files.write(path, bytes);
            return "User picture saved to server";
        } catch (IOException e) {
            return "User picture Saved";
        }
    }
}
