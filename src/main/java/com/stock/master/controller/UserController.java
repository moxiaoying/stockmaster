package com.stock.master.controller;

import com.stock.master.exception.FieldInputException;
import com.stock.master.model.po.User;
import com.stock.master.model.vo.CommonResponse;
import com.stock.master.model.vo.UserVo;
import com.stock.master.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public UserVo login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            FieldInputException e = new FieldInputException();
            e.addError("user", "username and password could not be null");
            throw e;
        }
        User user = userService.login(username, password);
        if (user == null) {
            FieldInputException e = new FieldInputException();
            e.addError("user", "username or password is error");
            throw e;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        userService.putToSession(user, token);

        UserVo userVo = new UserVo();
        userVo.setToken(token);
        userVo.setEmail(user.getEmail());
        userVo.setMobile(user.getMobile());
        userVo.setName(user.getName());
        userVo.setUsername(user.getUsername());
        userVo.setToken(token);
        return userVo;
    }
    @RequestMapping("updatePassword")
    public CommonResponse updatePassword(String oldPassword, String password, String password2) {
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(password) || StringUtils.isEmpty(password2)) {
            FieldInputException e = new FieldInputException();
            e.addError("user", "old password and password could not be null");
            throw e;
        }
        if (!password.equals(password2)) {
            FieldInputException e = new FieldInputException();
            e.addError("user", "confirmed password and new password do not match");
            throw e;
        }
        int userId = getUserId();
        User user = userService.getById(userId);
        if (!user.getPassword().equals(DigestUtils.md5Hex(oldPassword))) {
            FieldInputException e = new FieldInputException();
            e.addError("user", "old password is error");
            throw e;
        }
        user.setPassword(DigestUtils.md5Hex(password));
        userService.update(user);
        return CommonResponse.buildResponse("success");
    }

}

