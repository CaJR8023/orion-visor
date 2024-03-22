package com.orion.ops.module.asset.handler.host.exec.log.manager;

import com.orion.lang.utils.Threads;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.module.asset.handler.host.exec.log.tracker.IExecLogTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 执行日志管理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/3/18 23:36
 */
@Slf4j
@Component
public class ExecLogManager {

    private final ConcurrentHashMap<String, IExecLogTracker> execTrackers = new ConcurrentHashMap<>();

    /**
     * 添加执行日志追踪器
     *
     * @param tracker tracker
     */
    public void addTracker(IExecLogTracker tracker) {
        execTrackers.put(tracker.getTrackerId(), tracker);
    }

    /**
     * 获取日志追踪器
     *
     * @param trackerId trackerId
     * @return tracker
     */
    public IExecLogTracker getTracker(String trackerId) {
        return execTrackers.get(trackerId);
    }

    /**
     * 移除日志追踪器
     *
     * @param trackerId trackerId
     */
    public void removeTracker(String trackerId) {
        IExecLogTracker tracker = execTrackers.remove(trackerId);
        if (tracker != null) {
            tracker.close();
        }
    }

    /**
     * 异步关闭进行中的追踪器
     *
     * @param path path
     */
    public void asyncCloseTailFile(String path) {
        Threads.start(() -> {
            try {
                // 获取当前路径的全部追踪器
                List<IExecLogTracker> trackers = execTrackers.values()
                        .stream()
                        .filter(s -> s.getPath().equals(path))
                        .collect(Collectors.toList());
                Threads.sleep(Const.MS_S_1);
                trackers.forEach(IExecLogTracker::setLastModify);
                Threads.sleep(Const.MS_S_5);
                trackers.forEach(IExecLogTracker::close);
            } catch (Exception e) {
                log.error("ExecLogManager.asyncCloseTailFile error path: {}", path, e);
            }
        });
    }

}
