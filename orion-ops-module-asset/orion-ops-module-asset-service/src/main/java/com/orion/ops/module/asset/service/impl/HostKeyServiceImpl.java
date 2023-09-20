package com.orion.ops.module.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.framework.common.constant.ErrorMessage;
import com.orion.ops.framework.common.utils.CryptoUtils;
import com.orion.ops.framework.common.utils.Valid;
import com.orion.ops.framework.redis.core.utils.RedisLists;
import com.orion.ops.module.asset.convert.HostKeyConvert;
import com.orion.ops.module.asset.dao.HostKeyDAO;
import com.orion.ops.module.asset.define.HostCacheKeyDefine;
import com.orion.ops.module.asset.entity.domain.HostKeyDO;
import com.orion.ops.module.asset.entity.dto.HostKeyCacheDTO;
import com.orion.ops.module.asset.entity.request.host.HostKeyCreateRequest;
import com.orion.ops.module.asset.entity.request.host.HostKeyQueryRequest;
import com.orion.ops.module.asset.entity.request.host.HostKeyUpdateRequest;
import com.orion.ops.module.asset.entity.vo.HostKeyVO;
import com.orion.ops.module.asset.service.HostKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主机秘钥 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-20 11:55
 */
@Slf4j
@Service
public class HostKeyServiceImpl implements HostKeyService {

    @Resource
    private HostKeyDAO hostKeyDAO;

    @Override
    public Long createHostKey(HostKeyCreateRequest request) {
        log.info("HostKeyService-createHostKey request: {}", JSON.toJSONString(request));
        // 转换
        HostKeyDO record = HostKeyConvert.MAPPER.to(request);
        // 查询数据是否冲突
        this.checkHostKeyPresent(record);
        String password = record.getPassword();
        if (!Strings.isBlank(password)) {
            record.setPassword(CryptoUtils.encryptAsString(password));
        }
        // 插入
        int effect = hostKeyDAO.insert(record);
        log.info("HostKeyService-createHostKey effect: {}", effect);
        Long id = record.getId();
        // 设置缓存
        RedisLists.pushJson(HostCacheKeyDefine.HOST_KEY.getKey(), HostKeyConvert.MAPPER.toCache(record));
        RedisLists.setExpire(HostCacheKeyDefine.HOST_KEY);
        return id;
    }

    @Override
    public Integer updateHostKeyById(HostKeyUpdateRequest request) {
        log.info("HostKeyService-updateHostKeyById request: {}", JSON.toJSONString(request));
        // 查询
        Long id = Valid.notNull(request.getId(), ErrorMessage.ID_MISSING);
        HostKeyDO record = hostKeyDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 转换
        HostKeyDO updateRecord = HostKeyConvert.MAPPER.to(request);
        // 查询数据是否冲突
        this.checkHostKeyPresent(updateRecord);
        String password = updateRecord.getPassword();
        if (!Strings.isBlank(password)) {
            updateRecord.setPassword(CryptoUtils.encryptAsString(password));
        }
        // 更新
        int effect = hostKeyDAO.updateById(updateRecord);
        log.info("HostKeyService-updateHostKeyById effect: {}", effect);
        // 设置缓存
        if (!record.getName().equals(updateRecord.getName())) {
            RedisLists.removeJson(HostCacheKeyDefine.HOST_KEY.getKey(), HostKeyConvert.MAPPER.toCache(record));
            RedisLists.pushJson(HostCacheKeyDefine.HOST_KEY.getKey(), HostKeyConvert.MAPPER.toCache(updateRecord));
        }
        return effect;
    }

    @Override
    public HostKeyVO getHostKeyById(Long id) {
        // 查询
        HostKeyDO record = hostKeyDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        // 转换
        return HostKeyConvert.MAPPER.to(record);
    }

    @Override
    public HostKeyDO getHostKey(Long id) {
        HostKeyDO record = hostKeyDAO.selectById(id);
        Valid.notNull(record, ErrorMessage.DATA_ABSENT);
        String password = record.getPassword();
        if (password != null) {
            record.setPassword(CryptoUtils.decryptAsString(password));
        }
        return record;
    }

    @Override
    public List<HostKeyVO> getHostKeyList() {
        // 查询缓存
        List<HostKeyCacheDTO> list = RedisLists.rangeJson(HostCacheKeyDefine.HOST_KEY);
        if (list.isEmpty()) {
            // 查询数据库
            list = hostKeyDAO.of().list(HostKeyConvert.MAPPER::toCache);
            // 添加默认值 防止穿透
            if (list.isEmpty()) {
                list.add(HostKeyCacheDTO.builder()
                        .id(Const.NONE_ID)
                        .build());
            }
            // 设置缓存
            RedisLists.pushAllJson(HostCacheKeyDefine.HOST_KEY.getKey(), list);
        }
        // 删除默认值
        return list.stream()
                .filter(s -> !s.getId().equals(Const.NONE_ID))
                .map(HostKeyConvert.MAPPER::to)
                .collect(Collectors.toList());
    }

    @Override
    public DataGrid<HostKeyVO> getHostKeyPage(HostKeyQueryRequest request) {
        // 条件
        LambdaQueryWrapper<HostKeyDO> wrapper = this.buildQueryWrapper(request);
        // 查询
        DataGrid<HostKeyVO> dataGrid = hostKeyDAO.of(wrapper)
                .page(request)
                .dataGrid(HostKeyConvert.MAPPER::to);
        dataGrid.forEach(this::toSafe);
        return dataGrid;
    }

    @Override
    public Integer deleteHostKeyById(Long id) {
        log.info("HostKeyService-deleteHostKeyById id: {}", id);
        int effect = hostKeyDAO.deleteById(id);
        log.info("HostKeyService-deleteHostKeyById effect: {}", effect);
        return effect;
    }

    /**
     * 检测对象是否存在
     *
     * @param domain domain
     */
    private void checkHostKeyPresent(HostKeyDO domain) {
        // 构造条件
        LambdaQueryWrapper<HostKeyDO> wrapper = hostKeyDAO.wrapper()
                // 更新时忽略当前记录
                .ne(HostKeyDO::getId, domain.getId())
                // 用其他字段做重复校验
                .eq(HostKeyDO::getName, domain.getName());
        // 检查是否存在
        boolean present = hostKeyDAO.of(wrapper).present();
        Valid.isFalse(present, ErrorMessage.DATA_PRESENT);
    }

    /**
     * 构建查询 wrapper
     *
     * @param request request
     * @return wrapper
     */
    private LambdaQueryWrapper<HostKeyDO> buildQueryWrapper(HostKeyQueryRequest request) {
        return hostKeyDAO.wrapper()
                .eq(HostKeyDO::getId, request.getId())
                .like(HostKeyDO::getName, request.getName());
    }

    /**
     * 删除不安全字段
     *
     * @param vo vo
     */
    public void toSafe(HostKeyVO vo) {
        vo.setPublicKey(null);
        vo.setPrivateKey(null);
    }

}
