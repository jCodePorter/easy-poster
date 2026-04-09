package com.bytefuture.easy.poster.model;

import com.bytefuture.easy.poster.EasyPoster;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * PosterContext
 *
 * @author biaoy
 * @since 2025/04/07
 */
@Getter
@Setter
public class PosterContext {

    private EasyPoster easyPoster;

    private Graphics2D graphics;

    private Config config;
}
