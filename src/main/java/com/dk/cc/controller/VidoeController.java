package com.dk.cc.controller;


import com.dk.cc.common.lang.Result;
import com.dk.cc.service.VidoeService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dk
 * @since 2020-11-28
 */
@RestController
@RequestMapping("/vidoe")
public class VidoeController {

    @Autowired
    VidoeService vidoeService;

    @RequiresRoles("admin")
    @GetMapping("/vidoeList")
    public Result vidoeList(){
        return Result.succ(vidoeService.list());
    }
}
