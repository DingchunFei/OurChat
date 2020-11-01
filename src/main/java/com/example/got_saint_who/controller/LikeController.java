package com.example.got_saint_who.controller;

import com.example.got_saint_who.pojo.Like;
import com.example.got_saint_who.pojo.LikeDTO;
import com.example.got_saint_who.pojo.MomentIds;
import com.example.got_saint_who.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Kandoka
 * @createTime: 2020/10/31 10:40
 * @description:
 */

@RestController
@CrossOrigin
@RequestMapping("/like")
public class LikeController {

    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/findAllLikes",method= RequestMethod.POST)
    public LikeDTO findAllLikes(@RequestBody MomentIds momentIds){
        return likeService.findAllLikes(momentIds.getMomentIds());
    }

    @RequestMapping(value = "/addLike",method= RequestMethod.POST)
    public Like addLike(@RequestBody Like like){
        return likeService.addLike(like);
    }

    @RequestMapping(value = "/deleteLike",method= RequestMethod.POST)
    public Like deleteLike(@RequestBody Like like){
        return likeService.deleteLike(like);
    }
}
