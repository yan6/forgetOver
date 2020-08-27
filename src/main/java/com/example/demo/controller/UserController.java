package com.example.demo.controller;

import com.example.demo.core.project.ChapterListInfo;
import com.example.demo.domain.bean.User;
import com.example.demo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getUser")
    public String getUser(HttpServletRequest request,
                          @RequestParam(value = "id", required = false, defaultValue = "1") int id) {
        User user = userService.get(id);
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        return user.getName();
    }

    @RequestMapping(value = "/insertNewUser", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public boolean insertNewUser(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                 @RequestParam(value = "age", required = false, defaultValue = "-1") int age) {
        if (StringUtils.isBlank(name) || age == -1) {
            return false;
        }
        userService.insert(name, age);
        return true;
    }

    @RequestMapping(value = "/monitor")
    public String monitor(@RequestParam(value = "udid", required = false, defaultValue = "1") String udid) {
        return "success";
    }

    @RequestMapping(value = "/test")
    public String test(@RequestParam(value = "id", required = false, defaultValue = "1") int name) {
        User user = userService.getUserById(name);
        return user.getName();
    }

    @RequestMapping(value = "/getChapterListInfo", produces = "application/json; charset=utf-8")
    @ResponseBody
    public ChapterListInfo getChapterListInfo() {
        return userService.getChapterListInfo();
    }

    @PostMapping(value = "/testSign")
    @ResponseBody
    public boolean testSign() {
        return true;
    }
}
