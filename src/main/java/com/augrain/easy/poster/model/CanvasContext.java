package com.augrain.easy.poster.model;

import com.augrain.easy.poster.EasyPoster;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * CanvasContext
 *
 * @author biaoy
 * @since 2025/04/07
 */
@Getter
@Setter
public class CanvasContext {

    private EasyPoster easyCanvas;

    private Graphics2D graphics;

    private Config config;
}
