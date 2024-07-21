package com.liang.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liang.usercenter.model.entity.User;
import com.liang.usercenter.model.vo.UserVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 29018
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-03-20 19:09:39
* @Entity com.liang.usercenter.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where id = #{id}")
    List<UserVO> listUserById(Long id);
}




