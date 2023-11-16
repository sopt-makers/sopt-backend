package org.sopt.app.interfaces.external;

import feign.HeaderMap;
import feign.RequestLine;
import org.sopt.app.application.operation.OperationInfo;

import java.util.Map;

public interface OperationClient {

    @RequestLine("GET /api/v1/app/score")
    OperationInfo.ScoreResponse getScore(@HeaderMap final Map<String, String> headers);

}
