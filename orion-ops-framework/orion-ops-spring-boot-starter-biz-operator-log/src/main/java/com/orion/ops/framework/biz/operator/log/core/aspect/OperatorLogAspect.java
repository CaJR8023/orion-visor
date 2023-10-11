package com.orion.ops.framework.biz.operator.log.core.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.orion.lang.define.thread.ExecutorBuilder;
import com.orion.lang.define.wrapper.Ref;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.json.matcher.ReplacementFormatters;
import com.orion.ops.framework.biz.operator.log.core.annotation.OperatorLog;
import com.orion.ops.framework.biz.operator.log.core.config.OperatorLogConfig;
import com.orion.ops.framework.biz.operator.log.core.enums.ReturnType;
import com.orion.ops.framework.biz.operator.log.core.holder.OperatorTypeHolder;
import com.orion.ops.framework.biz.operator.log.core.model.OperatorLogModel;
import com.orion.ops.framework.biz.operator.log.core.model.OperatorType;
import com.orion.ops.framework.biz.operator.log.core.service.OperatorLogFrameworkService;
import com.orion.ops.framework.biz.operator.log.core.uitls.OperatorLogs;
import com.orion.ops.framework.common.enums.BooleanBit;
import com.orion.ops.framework.common.meta.TraceIdHolder;
import com.orion.ops.framework.common.security.SecurityHolder;
import com.orion.ops.framework.common.utils.IpUtils;
import com.orion.web.servlet.web.Servlets;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * 操作日志切面
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/10/10 10:47
 */
@Aspect
@Slf4j
public class OperatorLogAspect {

    private static final ExecutorService LOG_SAVER = ExecutorBuilder.create()
            .corePoolSize(1)
            .maxPoolSize(1)
            .useLinkedBlockingQueue()
            .allowCoreThreadTimeout()
            .useLinkedBlockingQueue()
            .build();

    private final OperatorLogConfig operatorLogConfig;

    private final OperatorLogFrameworkService operatorLogFrameworkService;

    @Resource
    private ValueFilter desensitizeValueFilter;

    @Resource
    private SecurityHolder securityHolder;

    public OperatorLogAspect(OperatorLogConfig operatorLogConfig,
                             OperatorLogFrameworkService operatorLogFrameworkService) {
        this.operatorLogConfig = operatorLogConfig;
        this.operatorLogFrameworkService = operatorLogFrameworkService;
    }

    @Around("@annotation(o)")
    public Object around(ProceedingJoinPoint joinPoint, OperatorLog o) throws Throwable {
        long start = System.currentTimeMillis();
        // 先清空上下文
        OperatorLogs.clear();
        try {
            // 执行
            Object result = joinPoint.proceed();
            // 记录日志
            this.saveLog(start, o, result, null);
            return result;
        } catch (Throwable exception) {
            // 记录日志
            this.saveLog(start, o, null, exception);
            throw exception;
        } finally {
            // 清空上下文
            OperatorLogs.clear();
        }
    }

    /**
     * 保存日志
     *
     * @param start     start
     * @param exception exception
     */
    private void saveLog(long start, OperatorLog o, Object ret, Throwable exception) {
        try {
            // 请求信息
            Map<String, Object> extra = OperatorLogs.get();
            if (!OperatorLogs.isSave(extra)) {
                return;
            }
            OperatorLogModel model = new OperatorLogModel();
            // 填充使用时间
            this.fillUseTime(model, start);
            // 填充用户信息
            this.fillUserInfo(model);
            // 填充请求信息
            this.fillRequest(model);
            // 填充结果信息
            this.fillResult(model, o, ret, exception);
            // 填充拓展信息
            this.fillExtra(model, extra);
            // 填充日志
            this.fillLogInfo(model, extra, o);
            // 插入日志
            this.asyncSaveLog(model);
        } catch (Exception e) {
            log.error("操作日志保存失败", e);
        }
    }

    /**
     * 填充使用时间
     *
     * @param model model
     * @param start start
     */
    private void fillUseTime(OperatorLogModel model, long start) {
        long end = System.currentTimeMillis();
        model.setDuration((int) (end - start));
        model.setStartTime(new Date(start));
        model.setEndTime(new Date(end));
    }

    /**
     * 填充用户信息
     *
     * @param model model
     */
    private void fillUserInfo(OperatorLogModel model) {
        model.setUserId(securityHolder.getLoginUserId());
    }

    /**
     * 填充请求信息
     *
     * @param model model
     */
    private void fillRequest(OperatorLogModel model) {
        model.setTraceId(TraceIdHolder.get());
        Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(s -> (ServletRequestAttributes) s)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    String address = Servlets.getRemoteAddr(request);
                    model.setAddress(address);
                    model.setLocation(IpUtils.getLocation(address));
                    model.setUserAgent(Strings.retain(Servlets.getUserAgent(request), operatorLogConfig.getUserAgentLength()));
                });
    }

    /**
     * 填充结果
     *
     * @param model     model
     * @param exception exception
     */
    private void fillResult(OperatorLogModel model, OperatorLog o, Object ret, Throwable exception) {
        if (exception == null) {
            model.setResult(BooleanBit.TRUE.getValue());
            ReturnType retType = o.ret();
            if (ret != null) {
                if (ReturnType.JSON.equals(retType)) {
                    // 脱敏
                    model.setReturnValue(JSON.toJSONString(ret, desensitizeValueFilter));
                } else if (ReturnType.TO_STRING.equals(retType)) {
                    model.setReturnValue(JSON.toJSONString(Ref.of(Objects.toString(ret))));
                }
            }
        } else {
            model.setResult(BooleanBit.FALSE.getValue());
            // 错误信息
            String errorMessage = Strings.retain(exception.getMessage(), operatorLogConfig.getErrorMessageLength());
            model.setErrorMessage(errorMessage);
        }
    }

    /**
     * 填充拓展信息
     *
     * @param model model
     * @param extra extra
     */
    private void fillExtra(OperatorLogModel model, Map<String, Object> extra) {
        if (extra != null) {
            model.setExtra(JSON.toJSONString(extra));
        }
    }

    /**
     * 填充日志信息
     *
     * @param model model
     * @param extra extra
     * @param o     o
     */
    private void fillLogInfo(OperatorLogModel model, Map<String, Object> extra, OperatorLog o) {
        OperatorType type = OperatorTypeHolder.get(o.value());
        model.setModule(type.getModule());
        model.setType(type.getType());
        model.setLogInfo(ReplacementFormatters.format(type.getTemplate(), extra));
    }

    /**
     * 异步保存日志
     *
     * @param model model
     */
    private void asyncSaveLog(OperatorLogModel model) {
        LOG_SAVER.submit(() -> {
            operatorLogFrameworkService.insert(model);
        });
    }

}