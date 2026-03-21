package com.yeahicode.ucbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeahicode.ucbackend.model.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ryualvin
 * @description 针对表【user】的数据库操作Mapper
 * @createDate 2026-03-18 20:12:58
 * @Entity com.yeahicode.ucbackend.model.user
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user")
    List<User> selectAllIncludingDeleted();
}




