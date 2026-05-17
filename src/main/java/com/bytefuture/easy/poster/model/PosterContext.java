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

    /**
     * 当前poster对象
     */
    private EasyPoster easyPoster;

    /**
     * 图形对象
     */
    private Graphics2D graphics;

    /**
     * 全局配置
     */
    private Config config;
}
