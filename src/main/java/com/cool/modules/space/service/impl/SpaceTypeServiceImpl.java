package com.cool.modules.space.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.space.entity.SpaceTypeEntity;
import com.cool.modules.space.mapper.SpaceTypeMapper;
import com.cool.modules.space.service.SpaceTypeService;
import org.springframework.stereotype.Service;

/**
 * 文件空间信息
 */
@Service
public class SpaceTypeServiceImpl extends BaseServiceImpl<SpaceTypeMapper, SpaceTypeEntity>
        implements SpaceTypeService {
}