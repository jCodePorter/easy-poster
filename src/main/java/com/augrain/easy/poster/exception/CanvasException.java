package com.augrain.easy.poster.exception;

/**
 * CanvasException
 *
 * @author biaoy
 * @since 2025/03/20
 */
public class CanvasException extends RuntimeException {

    public CanvasException(String message) {
        super(message);
    }

    public CanvasException(Throwable cause) {
        super(cause);
    }
}
