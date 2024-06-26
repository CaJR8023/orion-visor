package com.orion.visor.module.asset.handler.host.exec.command.handler;

import com.orion.lang.able.SafeCloseable;
import com.orion.visor.module.asset.enums.ExecHostStatusEnum;

/**
 * 命令执行器定义
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/3/12 11:31
 */
public interface IExecCommandHandler extends Runnable, SafeCloseable {

    /**
     * 写入
     *
     * @param msg msg
     */
    void write(String msg);

    /**
     * 中断执行
     */
    void interrupt();

    /**
     * 获取当前状态
     *
     * @return status
     */
    ExecHostStatusEnum getStatus();

    /**
     * 获取退出码
     *
     * @return exit code
     */
    Integer getExitCode();

    /**
     * 获取主机 id
     *
     * @return hostId
     */
    Long getHostId();

}
