package com.orion.visor.framework.mybatis.core.generator.template;

/**
 * 前端代码模板
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/9/26 1:17
 */
public class VueTemplate extends Template {

    public VueTemplate(Table table) {
        super(table);
        table.enableVue = true;
    }

    public VueTemplate(Table table, String module, String feature) {
        super(table);
        table.enableVue = true;
        table.module = module;
        table.feature = feature;
    }

    /**
     * 设置模块 用于文件名称生成
     *
     * @param module module
     * @return this
     */
    public VueTemplate module(String module) {
        table.module = module;
        return this;
    }

    /**
     * 设置功能 用于文件名称生成
     *
     * @param feature feature
     * @return this
     */
    public VueTemplate feature(String feature) {
        table.feature = feature;
        return this;
    }

    /**
     * 使用抽屉表单
     *
     * @return this
     */
    public VueTemplate enableDrawerForm() {
        table.enableDrawerForm = true;
        return this;
    }

    /**
     * 列表可多选
     *
     * @return this
     */
    public VueTemplate enableRowSelection() {
        table.enableRowSelection = true;
        return this;
    }

    /**
     * 启用卡片列表
     *
     * @return this
     */
    public VueTemplate enableCardView() {
        table.enableCardView = true;
        return this;
    }

}
