package com.orion.visor.framework.common.handler.data;

import com.alibaba.fastjson.JSON;
import com.orion.spring.SpringHolder;
import com.orion.visor.framework.common.handler.data.model.GenericsDataModel;
import com.orion.visor.framework.common.handler.data.strategy.MapDataStrategy;

/**
 * 标准数据定义
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/12/21 0:07
 */
public interface GenericsDataDefinition {

    /**
     * 获取数据模型类型
     *
     * @return class
     */
    Class<? extends GenericsDataModel> getModel();

    /**
     * 获取数据处理策略
     *
     * @return class
     */
    Class<? extends MapDataStrategy<? extends GenericsDataModel>> getStrategy();

    /**
     * 获取数据模型策略处理器
     *
     * @param <Model>    Model
     * @param <Strategy> Strategy
     * @return StrategyBean
     */
    @SuppressWarnings("unchecked")
    default <Model extends GenericsDataModel, Strategy extends MapDataStrategy<Model>> Strategy getStrategyBean() {
        return (Strategy) SpringHolder.getBean(this.getStrategy());
    }

    /**
     * 反序列化对象
     *
     * @param json    json
     * @param <Model> Model
     * @return object
     */
    @SuppressWarnings("unchecked")
    default <Model extends GenericsDataModel> Model parse(String json) {
        return (Model) JSON.parseObject(json, this.getModel());
    }


}
