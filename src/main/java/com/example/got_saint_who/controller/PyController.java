package com.example.got_saint_who.controller;

import com.example.got_saint_who.pojo.Py;
import com.example.got_saint_who.pojo.User;
import com.example.got_saint_who.service.PyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 19:56
 * @description:
 */

@RestController
@CrossOrigin
@RequestMapping("/py")
public class PyController {
    @Autowired
    private PyService pyService;

    @RequestMapping(value = "/findFriends", method = RequestMethod.POST)
    public List<Py> findFriends(@RequestBody User user){
        return pyService.findAllFriendById(user.getUserId());
    }
}
