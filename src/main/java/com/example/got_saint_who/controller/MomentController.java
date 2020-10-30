package com.example.got_saint_who.controller;

import com.example.got_saint_who.pojo.Friends;
import com.example.got_saint_who.pojo.Moment;
import com.example.got_saint_who.pojo.User;
import com.example.got_saint_who.service.MomentService;
import com.example.got_saint_who.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/moment")
public class MomentController {

    @Autowired
    private MomentService momentService;
    @Value("${system.config.imageSavePath}")
    private static final String filePath = "/usr/local/androidImages/";     //默认图片存放位置
    //    private static final String filePath = "C:\\Users\\conan\\Desktop\\CCE\\";

    /**
     * 根据好友id查询相关的moment
     */
    @RequestMapping(method= RequestMethod.POST)
    public List<Moment> queryByIds(@RequestBody Friends friends){
        List<Moment> moments = momentService.queryByIds(friends.getFriendsIdList());
        return moments;
    }

    @RequestMapping(value = "/create",method= RequestMethod.POST)
    public void add(@RequestBody Moment moment){
        momentService.save(moment);
    }

    @RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
    public String uploadImg(@RequestParam("moment_image") MultipartFile file, HttpServletRequest request){

        Integer userId = Integer.valueOf(request.getParameter("user_id"));
        String username = request.getParameter("moment_username");
        String content = request.getParameter("moment_content");

        //获取文件名
        String fileName = file.getOriginalFilename();

        //获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        System.out.println("上传的后缀名为： "+ suffixName);

        //moment图片是随机名称
        fileName = UUID.randomUUID().toString() + suffixName;
        File dest = new File(filePath + fileName);

        try {
            file.transferTo(dest);
            //保存moment
            Moment moment = new Moment(userId, username, fileName, content);
            momentService.save(moment);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Failing to upload image";
    }

}
