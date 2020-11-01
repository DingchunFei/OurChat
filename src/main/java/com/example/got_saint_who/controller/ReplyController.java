package com.example.got_saint_who.controller;

import com.example.got_saint_who.pojo.*;
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

    @RequestMapping(value = "/findAllReplies", method= RequestMethod.POST)
    public ReplyDTO findAllReplies(@RequestBody MomentIds momentIds){
        return replyServive.findAllReplies(momentIds);
    }

    @RequestMapping(value = "/addReply", method= RequestMethod.POST)
    public Reply addReply(@RequestBody Reply reply){
        return replyServive.save(reply);
    }

    @RequestMapping(value = "/deleteReply", method= RequestMethod.POST)
    public Reply deleteReply(@RequestBody Reply reply){
        replyServive.delete(reply);
        return reply;
    }
}
