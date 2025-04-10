package org.sopt.app.common.exception;

import org.sopt.app.common.response.ErrorCode;

public class ForbiddenException extends BaseException {
	public ForbiddenException() { super(ErrorCode.FORBIDDEN);}

	public ForbiddenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
