package com.orion.visor.module.infra.dao;

import com.orion.visor.framework.mybatis.core.mapper.IMapper;
import com.orion.visor.module.infra.entity.domain.DictValueDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典配置值 Mapper 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-10-16 16:33
 */
@Mapper
public interface DictValueDAO extends IMapper<DictValueDO> {

}
