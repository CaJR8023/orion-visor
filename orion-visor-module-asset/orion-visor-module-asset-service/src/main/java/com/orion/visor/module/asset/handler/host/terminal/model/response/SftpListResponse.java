package com.orion.visor.module.asset.handler.host.terminal.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * sftp 列表响应 实体对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/2/6 16:20
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SftpListResponse", description = "sftp 列表响应 实体对象")
public class SftpListResponse extends SftpBaseResponse {

    @Schema(description = "path")
    private String path;

    @Schema(description = "body")
    private String body;

}
