package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.geometry.Position;
import lombok.Getter;

import java.awt.*;

/**
 * 视图抽象类，存储共有属性
 *
 * @author biaoy
 * @since 2025/02/20
 */
@Getter
public abstract class AbstractElement implements IElement {
    /**
     * 透明度
     */
    protected float alpha = 1F;

    /**
     * 旋转角度
     */
    protected int rotate = 0;

    /**
     * 位置
     */
    protected Position position;

    public AbstractElement setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    public AbstractElement setRotate(int rotate) {
        this.rotate = rotate;
        return this;
    }

    public AbstractElement setPosition(Position position) {
        this.position = position;
        return this;
    }

    @Override
    public CoordinatePoint render(Graphics2D g, int canvasWidth, int canvasHeight) throws Exception {
        beforeRender(g);
        Dimension elementInfo = calculateDimension(g, canvasWidth, canvasHeight);
        CoordinatePoint coordinatePoint = doRender(g, elementInfo, canvasWidth, canvasHeight);
        afterRender(g);
        return coordinatePoint;
    }

    /**
     * 计算元素尺寸
     */
    public abstract Dimension calculateDimension(Graphics2D g, int canvasWidth, int canvasHeight);

    /**
     * 执行渲染
     */
    public abstract CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight);

    /**
     * 渲染之前，做一些默认配置
     */
    public void beforeRender(Graphics2D g) {

    }

    public void afterRender(Graphics2D g) {

    }
}
