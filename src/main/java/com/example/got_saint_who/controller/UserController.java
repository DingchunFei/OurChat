package com.example.got_saint_who.controller;

import com.example.got_saint_who.pojo.User;
import com.example.got_saint_who.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 增加新用户
     */
    @RequestMapping(method= RequestMethod.POST)
    public User add(@RequestBody User user){
        if(user.getUsername()=="" || user.getUsername()==null){
            //直接返回一个没有id的user,当服务端收到后根据判断有没有id来确定是否插入成功！
            return user;
        }
        return userService.add(user);
    }

    /**
     * 用户登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestBody User user){
        user =userService.login(user.getUsername(),user.getPassword());
        return user;
    }

    /**
     * 根据用户名查询用户
     */
    @RequestMapping(value = "/findUserByUsername", method = RequestMethod.POST)
    public List<User> findByUsername(@RequestBody User user){
        System.out.println("===========> "+ user.getUsername());
        return userService.findByUsernameLike(user.getUsername());
    }

    @RequestMapping(value = "/findUserById", method = RequestMethod.POST)
    public User findUserById(@RequestBody User user){
        return userService.findUserById(user.getUserId());
    }

    //private static final String filePath = "/usr/local/androidImages/";
    private static final String filePath = "C:\\Users\\conan\\Desktop\\CCE\\";

    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    public String uploadImg(@RequestParam("img") MultipartFile file, HttpServletRequest request){

        String userId = request.getParameter("user_id");
        System.out.println("user id是： " + userId);

        //获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的后缀名为： "+ fileName);

        //获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("上传的后缀名为： "+ suffixName);

        //文件上传后的路径
        fileName = userId + suffixName;
        File dest = new File(filePath + fileName);

        try {
            file.transferTo(dest);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failing to upload image";
    }

}
