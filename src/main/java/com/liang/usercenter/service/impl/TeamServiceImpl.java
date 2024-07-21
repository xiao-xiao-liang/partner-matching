package com.liang.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liang.usercenter.common.ErrorCode;
import com.liang.usercenter.constant.TeamStatusEnum;
import com.liang.usercenter.exception.BusinessException;
import com.liang.usercenter.mapper.UserMapper;
import com.liang.usercenter.mapper.UserTeamMapper;
import com.liang.usercenter.model.dto.TeamDTO;
import com.liang.usercenter.model.dto.TeamUpdateDTO;
import com.liang.usercenter.model.entity.Team;
import com.liang.usercenter.model.entity.User;
import com.liang.usercenter.model.entity.UserTeam;
import com.liang.usercenter.model.vo.TeamVO;
import com.liang.usercenter.model.vo.UserVO;
import com.liang.usercenter.service.TeamService;
import com.liang.usercenter.mapper.TeamMapper;
import com.liang.usercenter.service.UserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author 29018
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-07-19 18:51:24
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Resource
    private UserService userService;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    @Transactional
    public long addTeam(TeamDTO teamDTO, User loginUser) {
        // 1. 请求参数是否为空
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 3. 校验信息
        // 3.1 队伍人数 >1 且 <=20
        int maxNum = Optional.ofNullable(teamDTO.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        // 3.2 队伍标题 <= 20
        String name = teamDTO.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 3.3 描述 <= 512
        String description = teamDTO.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // 3.4 是否公开，不传默认公开
        Integer status = Optional.ofNullable(teamDTO.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 3.5 如果是加密状态，一定要有密码，且密码<=32
        String password = teamDTO.getPassword();
        if (statusEnum == TeamStatusEnum.SECRET) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        // 3.6 超时时间 > 当前时间
        Date expireTime = teamDTO.getExpireTime();
        if (expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间大于当前时间");
        }
        // 3.7 校验用户最多创建5个队伍
        // TODO 有bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginUser.getId());
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍");
        }
        //4. 插入队伍信息到队伍表
        Team team = Team.builder()
                .name(name)
                .description(description)
                .maxNum(maxNum)
                .userId(loginUser.getId())
                .status(status)
                .password(password)
                .deleted(0)
                .expireTime(expireTime)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        int res = teamMapper.insert(team);
        if (res <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        //5. 插入用户 =》 队伍关系到关系表
        UserTeam userTeam = UserTeam.builder()
                .userId(loginUser.getId())
                .teamId(team.getId())
                .deleted(0)
                .joinTime(new Date())
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        return userTeamMapper.insert(userTeam);
    }

    /**
     * 查询队伍列表
     */
    @Override
    @Transactional
    public List<TeamVO> listTeam(TeamDTO teamDTO, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 1. 构建查询条件
        if (teamDTO != null) {
            Long id = teamDTO.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String searchText = teamDTO.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamDTO.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamDTO.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamDTO.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("max_num", maxNum);
            }
            // 根据状态查询
            Integer status = teamDTO.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        queryWrapper.and(qw -> qw.gt("expire_time", new Date()).or().isNull("expire_time"));
        List<Team> teamList = teamMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return List.of();
        }
        List<TeamVO> teamVOList = new ArrayList<>();
        for (Team t : teamList) {
            Long userId = t.getUserId();
            User user = userMapper.selectById(userId);
            TeamVO teamVO = new TeamVO();
            BeanUtils.copyProperties(t, teamVO);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamVO.setUser(userVO);
            }
            teamVOList.add(teamVO);
        }
        return teamVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateDTO teamDTO, User loginUser) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamDTO.getId();
        if (teamDTO.getId() == null || teamDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = teamMapper.selectById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 只有管理员或者队伍创建者可以修改
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamDTO.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamDTO.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamDTO, team);
        int res = teamMapper.updateById(team);
        return res > 0;
    }

    @Override
    @Transactional
    public boolean joinTeam(TeamDTO teamDTO, User loginUser) {
        Long id = teamDTO.getId();
        Team team = teamMapper.selectById(id);
        if (team == null || team.getId() <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            // 密码不能为空
            if (StringUtils.isBlank(teamDTO.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
            }
            if (!team.getPassword().equals(teamDTO.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 用户已加入的队伍数量
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", loginUser.getId());
        List<UserTeam> userTeams = userTeamMapper.selectList(wrapper);
        if (userTeams.size() >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多加入5个队伍");
        }
        // 判断是否已加入队伍
        wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", id);
        wrapper.eq("user_id", loginUser.getId());
        List<UserTeam> userTeamList = userTeamMapper.selectList(wrapper);
        if (!userTeamList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已加入队伍");
        }
        // 判断队伍是否已满
        if (team.getCurrentNum() >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }
        // 修改队伍人数
        team.setCurrentNum(team.getCurrentNum() + 1);
        int res = teamMapper.updateById(team);
        if (res <= 0) {
            throw new BusinessException(ErrorCode.SYS_ERROR, "更新队伍人数失败");
        }
        // 插入用户队伍关系
        UserTeam userTeam = UserTeam.builder()
                .teamId(team.getId())
                .userId(loginUser.getId())
                .username(loginUser.getUsername())
                .joinTime(new Date())
                .build();
        res = userTeamMapper.insert(userTeam);
        return res > 0;
    }
}




