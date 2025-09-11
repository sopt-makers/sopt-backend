package org.sopt.app.common.external.feign;

import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;

import feign.Response;
import feign.codec.ErrorDecoder;

public class Feign401ErrorDecoder implements ErrorDecoder {
	private final ErrorDecoder defaultDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		int status = response.status();
		if (status == 401) {
			return new UnauthorizedException(ErrorCode.UNAUTHORIZED);
		}
		if (status == 403) {
			return new UnauthorizedException(ErrorCode.FORBIDDEN);
		}
		return defaultDecoder.decode(methodKey, response);
	}
}
