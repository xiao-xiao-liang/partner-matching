package com.liang.usercenter.service;

import com.liang.usercenter.model.dto.TeamDTO;
import com.liang.usercenter.model.dto.TeamUpdateDTO;
import com.liang.usercenter.model.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liang.usercenter.model.entity.User;
import com.liang.usercenter.model.vo.TeamVO;

import java.util.List;

/**
* @author 29018
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-07-19 18:51:24
*/
public interface TeamService extends IService<Team> {
    /**
     * 添加队伍
     */
    long addTeam(TeamDTO teamDTO, User loginUser);

    /**
     * 获取队伍列表
     */
    List<TeamVO> listTeam(TeamDTO teamDTO, boolean isAdmin);

    /**
     * 更新队伍
     */
    boolean updateTeam(TeamUpdateDTO teamDTO, User loginUser);

    /**
     * 加入队伍
     */
    boolean joinTeam(TeamDTO teamDTO, User loginUser);
}
