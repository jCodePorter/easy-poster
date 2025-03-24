package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.exception.CanvasException;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.geometry.Position;
import com.augrain.easy.canvas.model.Gradient;
import lombok.Getter;

import java.awt.*;

/**
 * 视图抽象类，存储共有属性
 *
 * @author biaoy
 * @since 2025/02/20
 */
@Getter
public abstract class AbstractElement<T extends AbstractElement> implements IElement {
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

    /**
     * 渐变设置
     */
    protected Gradient gradient;

    public T setAlpha(float alpha) {
        if (alpha < 0 || alpha > 1) {
            throw new CanvasException("alpha must be between 0 and 1");
        }
        this.alpha = alpha;
        return (T) this;
    }

    public T setRotate(int rotate) {
        this.rotate = rotate;
        return (T) this;
    }

    public T setPosition(Position position) {
        this.position = position;
        return (T) this;
    }

    public T setGradient(Gradient gradient) {
        this.gradient = gradient;
        return (T) this;
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
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(alphaComposite);
    }

    public void afterRender(Graphics2D g) {

    }

    public void gradient(Graphics2D g, Dimension dimension) {
        if (gradient != null) {
            g.setPaint(this.gradient.toGradient(dimension));
        }
    }
}
