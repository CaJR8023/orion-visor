package com.orion.visor.module.asset.service;

import com.orion.visor.framework.common.handler.data.model.GenericsDataModel;
import com.orion.visor.module.asset.entity.request.host.HostConfigUpdateRequest;
import com.orion.visor.module.asset.entity.request.host.HostConfigUpdateStatusRequest;
import com.orion.visor.module.asset.entity.vo.HostConfigVO;
import com.orion.visor.module.asset.enums.HostConfigTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 主机配置 服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-9-11 14:16
 */
public interface HostConfigService {

    /**
     * 获取配置
     *
     * @param hostId hostId
     * @param type   type
     * @return config
     */
    HostConfigVO getHostConfig(Long hostId, String type);

    /**
     * 获取配置 配置未启用会报错
     *
     * @param hostId hostId
     * @param type   type
     * @param <T>    T
     * @return config
     */
    <T extends GenericsDataModel> T getHostConfig(Long hostId, HostConfigTypeEnum type);

    /**
     * 获取配置
     *
     * @param hostId hostId
     * @return config
     */
    List<HostConfigVO> getHostConfigList(Long hostId);

    /**
     * 获取配置
     *
     * @param hostIdList hostIdList
     * @param type       type
     * @return config
     */
    List<HostConfigVO> getHostConfigList(List<Long> hostIdList, String type);

    /**
     * 获取配置
     *
     * @param hostIdList hostIdList
     * @param type       type
     * @param <T>        T
     * @return config
     */
    <T extends GenericsDataModel> Map<Long, T> getHostConfigMap(List<Long> hostIdList, HostConfigTypeEnum type);

    /**
     * 更新配置
     *
     * @param request request
     * @return version
     */
    Integer updateHostConfig(HostConfigUpdateRequest request);

    /**
     * 更新配置状态
     *
     * @param request request
     * @return version
     */
    Integer updateHostConfigStatus(HostConfigUpdateStatusRequest request);

    /**
     * 初始化主机配置
     *
     * @param hostId hostId
     */
    void initHostConfig(Long hostId);

    /**
     * 获取启用配置的 hostId
     *
     * @param type       type
     * @param hostIdList hostIdList
     * @return hostId
     */
    List<Long> getEnabledConfigHostId(String type, List<Long> hostIdList);

}
