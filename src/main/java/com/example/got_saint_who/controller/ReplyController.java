package com.example.got_saint_who.controller;

import com.example.got_saint_who.pojo.Friends;
import com.example.got_saint_who.pojo.ReplyDTO;
import com.example.got_saint_who.pojo.User;
import com.example.got_saint_who.service.ReplyServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 17:10
 * @description:
 */

@RestController
@CrossOrigin
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    ReplyServive replyServive;

    @RequestMapping(value = "/FindAllReplies", method= RequestMethod.POST)
    public ReplyDTO findAllReplies(@RequestBody User user){
        return replyServive.findAllReplies(user);
    }
}
