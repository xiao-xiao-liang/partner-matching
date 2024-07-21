package com.liang.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liang.usercenter.model.entity.UserTeam;
import com.liang.usercenter.service.UserTeamService;
import com.liang.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 29018
* @description 针对表【user_team(用户-队伍关系)】的数据库操作Service实现
* @createDate 2024-07-19 19:16:33
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




