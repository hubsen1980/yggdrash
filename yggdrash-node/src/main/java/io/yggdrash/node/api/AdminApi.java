package io.yggdrash.node.api;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import io.yggdrash.core.exception.FailedOperationException;
import io.yggdrash.node.api.dto.AdminDto;

@JsonRpcService("/api/admin")
public interface AdminApi {

    /**
     * Client send a nodeHello message, node return a clientHello message.
     *
     * @param command The command data
     */
    @JsonRpcErrors({
            @JsonRpcError(exception = FailedOperationException.class,
                    code = FailedOperationException.code)})
    String nodeHello(@JsonRpcParam(value = "command") AdminDto command);

    /**
     * Client send a requestCommand message, node return a responseCommand message.
     *
     * @param command The command data
     */
    @JsonRpcErrors({
            @JsonRpcError(exception = FailedOperationException.class,
                    code = FailedOperationException.code)})
    String requestCommand(@JsonRpcParam(value = "command") AdminDto command);

}
