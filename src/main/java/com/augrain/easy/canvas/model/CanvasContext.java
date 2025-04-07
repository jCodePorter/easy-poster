package com.augrain.easy.canvas.model;

import com.augrain.easy.canvas.EasyCanvas;
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

    private EasyCanvas easyCanvas;

    private Graphics2D graphics;

    private Config config;
}
