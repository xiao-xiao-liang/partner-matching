package com.liang.usercenter.mapper;

import com.liang.usercenter.model.dto.TeamDTO;
import com.liang.usercenter.model.entity.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liang.usercenter.model.vo.TeamVO;

import java.util.List;

/**
* @author 29018
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2024-07-19 18:51:24
* @Entity com.liang.usercenter.model.entity.Team
*/
public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 获取队伍列表
     */
    List<TeamVO> listTeam(TeamDTO teamDTO);
}




