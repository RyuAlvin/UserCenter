package com.yeahicode.ucbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeahicode.ucbackend.model.Tag;
import com.yeahicode.ucbackend.service.TagService;
import com.yeahicode.ucbackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author ryualvin
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2026-05-16 15:29:12
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




