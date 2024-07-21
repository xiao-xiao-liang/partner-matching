package com.liang.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liang.usercenter.common.BaseResponse;
import com.liang.usercenter.common.ErrorCode;
import com.liang.usercenter.common.ResultUtils;
import com.liang.usercenter.common.result.PageResult;
import com.liang.usercenter.exception.BusinessException;
import com.liang.usercenter.model.dto.TeamDTO;
import com.liang.usercenter.model.dto.TeamPageQueryDTO;
import com.liang.usercenter.model.dto.TeamUpdateDTO;
import com.liang.usercenter.model.entity.Team;
import com.liang.usercenter.model.entity.User;
import com.liang.usercenter.model.vo.TeamVO;
import com.liang.usercenter.service.TeamService;
import com.liang.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 队伍接口
 */
@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long res = teamService.addTeam(teamDTO, loginUser);
        return ResultUtils.success(res);
    }

    @DeleteMapping
    public BaseResponse<Boolean> deleteTeam(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(id);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYS_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 更新队伍
     */
    @PutMapping
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateDTO teamDTO, HttpServletRequest request) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamDTO, loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYS_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 用户加入队伍
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean join = teamService.joinTeam(teamDTO, loginUser);
        if (!join) {
            throw new BusinessException(ErrorCode.SYS_ERROR, "加入失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据id查询
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.SYS_ERROR, "查询失败");
        }
        return ResultUtils.success(team);
    }

    /**
     * 查询队伍列表
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamVO>> listTeam(TeamDTO teamDTO, HttpServletRequest request) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean admin = userService.isAdmin(loginUser);
        List<TeamVO> teamList = teamService.listTeam(teamDTO, admin);
        return ResultUtils.success(teamList);
    }

    /**
     * 分页查询
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamByPage(TeamPageQueryDTO teamPageQueryDTO) {
        if (teamPageQueryDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamPageQueryDTO, team);
        Page<Team> page = new Page<>(teamPageQueryDTO.getPage(), teamPageQueryDTO.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamList = teamService.page(page, queryWrapper);
        return ResultUtils.success(teamList);
    }
}