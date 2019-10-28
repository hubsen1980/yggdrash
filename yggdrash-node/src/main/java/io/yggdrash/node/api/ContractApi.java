package io.yggdrash.node.api;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import io.yggdrash.common.exception.FailedOperationException;
import io.yggdrash.core.exception.DecodeException;
import io.yggdrash.core.exception.NonExistObjectException;

import java.util.Map;

@JsonRpcService("/api/contract")
public interface ContractApi {

    /**
     * Handles all queries that are dispatched to the contract
     *
     * @param branchId branch id of contract
     * @param method query method
     * @param params query params
     * @return result of query
     */
    @JsonRpcErrors({
            @JsonRpcError(exception = NonExistObjectException.class, code = NonExistObjectException.CODE),
            @JsonRpcError(exception = FailedOperationException.class, code = FailedOperationException.CODE),
            @JsonRpcError(exception = DecodeException.class,
            code = DecodeException.CODE)})
    Object query(@JsonRpcParam(value = "branchId") String branchId,
                 @JsonRpcParam(value = "contractVersion") String contractVersion,
                 @JsonRpcParam(value = "method") String method,
                 @JsonRpcParam(value = "params") Map params);

}
