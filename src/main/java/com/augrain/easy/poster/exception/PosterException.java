package com.augrain.easy.poster.exception;

/**
 * PosterException
 *
 * @author biaoy
 * @since 2025/03/20
 */
public class PosterException extends RuntimeException {

    public PosterException(String message) {
        super(message);
    }

    public PosterException(Throwable cause) {
        super(cause);
    }
}
