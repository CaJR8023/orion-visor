package com.orion.visor.module.asset.enums;

/**
 * 批量执行来源
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/3/11 16:00
 */
public enum ExecSourceEnum {

    /**
     * 批量执行
     */
    BATCH,

    /**
     * 计划任务
     */
    JOB,

    ;

    public static ExecSourceEnum of(String source) {
        if (source == null) {
            return null;
        }
        for (ExecSourceEnum value : values()) {
            if (value.name().equals(source)) {
                return value;
            }
        }
        return null;
    }

}
