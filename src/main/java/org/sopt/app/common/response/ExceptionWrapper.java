package org.sopt.app.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionWrapper {

    private final String exceptionClassName;
    private final String exceptionMethodName;
    private final int exceptionLineNumber;
    private final String message;

    public static ExceptionWrapper extractExceptionWrapper(final Exception calledException) {
        StackTraceElement[] exceptionStackTrace = calledException.getStackTrace();
        String exceptionClassName = exceptionStackTrace[0].getClassName();
        String exceptionMethodName = exceptionStackTrace[0].getMethodName();
        int exceptionLineNumber = exceptionStackTrace[0].getLineNumber();
        String message = calledException.getMessage();
        return new ExceptionWrapper(exceptionClassName, exceptionMethodName, exceptionLineNumber, message);
    }
}