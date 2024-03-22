package com.orion.ops.module.asset.dao;

import com.orion.ops.framework.mybatis.core.mapper.IMapper;
import com.orion.ops.module.asset.entity.domain.ExecTemplateDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 执行模板 Mapper 接口
 *
 * @author Jiahang Li
 * @version 1.0.1
 * @since 2024-3-7 18:08
 */
@Mapper
public interface ExecTemplateDAO extends IMapper<ExecTemplateDO> {

}
