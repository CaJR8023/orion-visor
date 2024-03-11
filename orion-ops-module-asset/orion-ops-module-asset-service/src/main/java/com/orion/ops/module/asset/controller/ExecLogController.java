package com.orion.ops.module.asset.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.framework.biz.operator.log.core.annotation.OperatorLog;
import com.orion.ops.framework.common.validator.group.Page;
import com.orion.ops.framework.log.core.annotation.IgnoreLog;
import com.orion.ops.framework.log.core.enums.IgnoreLogMode;
import com.orion.ops.framework.web.core.annotation.RestWrapper;
import com.orion.ops.module.asset.define.operator.ExecLogOperatorType;
import com.orion.ops.module.asset.entity.request.exec.ExecLogQueryRequest;
import com.orion.ops.module.asset.entity.vo.ExecLogVO;
import com.orion.ops.module.asset.service.ExecLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 执行日志 api
 *
 * @author Jiahang Li
 * @version 1.0.1
 * @since 2024-3-11 11:31
 */
@Tag(name = "asset - 执行日志服务")
@Slf4j
@Validated
@RestWrapper
@RestController
@RequestMapping("/asset/exec-log")
@SuppressWarnings({"ELValidationInJSP", "SpringElInspection"})
public class ExecLogController {

    @Resource
    private ExecLogService execLogService;

    @IgnoreLog(IgnoreLogMode.RET)
    @GetMapping("/get")
    @Operation(summary = "查询执行日志")
    @Parameter(name = "id", description = "id", required = true)
    @PreAuthorize("@ss.hasPermission('asset:exec-log:query')")
    public ExecLogVO getExecLog(@RequestParam("id") Long id) {
        return execLogService.getExecLogById(id);
    }

    @IgnoreLog(IgnoreLogMode.RET)
    @PostMapping("/query")
    @Operation(summary = "分页查询执行日志")
    @PreAuthorize("@ss.hasPermission('asset:exec-log:query')")
    public DataGrid<ExecLogVO> getExecLogPage(@Validated(Page.class) @RequestBody ExecLogQueryRequest request) {
        return execLogService.getExecLogPage(request);
    }

    @OperatorLog(ExecLogOperatorType.DELETE)
    @DeleteMapping("/delete")
    @Operation(summary = "删除执行日志")
    @Parameter(name = "id", description = "id", required = true)
    @PreAuthorize("@ss.hasPermission('asset:exec-log:delete')")
    public Integer deleteExecLog(@RequestParam("id") Long id) {
        return execLogService.deleteExecLogById(id);
    }

    @OperatorLog(ExecLogOperatorType.DELETE)
    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除执行日志")
    @Parameter(name = "idList", description = "idList", required = true)
    @PreAuthorize("@ss.hasPermission('asset:exec-log:delete')")
    public Integer batchDeleteExecLog(@RequestParam("idList") List<Long> idList) {
        return execLogService.deleteExecLogByIdList(idList);
    }

}

