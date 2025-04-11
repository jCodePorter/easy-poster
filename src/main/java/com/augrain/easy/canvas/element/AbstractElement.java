package com.augrain.easy.canvas.element;

import com.augrain.easy.canvas.element.advance.ComposeElement;
import com.augrain.easy.canvas.exception.CanvasException;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.geometry.Position;
import com.augrain.easy.canvas.model.CanvasContext;
import com.augrain.easy.canvas.model.Gradient;
import com.augrain.easy.canvas.model.RelativeDirection;
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

    public ComposeElement in(AbstractElement element) {
        return follow(element, RelativeDirection.IN, true);
    }

    public ComposeElement left(AbstractElement element) {
        return follow(element, RelativeDirection.LEFT, true);
    }

    public ComposeElement right(AbstractElement element) {
        return follow(element, RelativeDirection.RIGHT, true);
    }

    public ComposeElement top(AbstractElement element) {
        return follow(element, RelativeDirection.TOP, true);
    }

    public ComposeElement bottom(AbstractElement element) {
        return follow(element, RelativeDirection.BOTTOM, true);
    }

    public ComposeElement next(AbstractElement element, RelativeDirection direction, boolean strict) {
        ComposeElement composeElement;
        if (element instanceof ComposeElement) {
            composeElement = (ComposeElement) element;
            composeElement.next(element, direction, strict);
        } else {
            composeElement = ComposeElement.of(this);
            composeElement.next(element, direction, strict);
        }
        return composeElement;
    }

    public ComposeElement follow(AbstractElement element, RelativeDirection direction, boolean strict) {
        ComposeElement composeElement;
        if (element instanceof ComposeElement) {
            composeElement = (ComposeElement) element;
            composeElement.follow(element, direction, strict);
        } else {
            composeElement = ComposeElement.of(this);
            composeElement.follow(element, direction, strict);
        }
        return composeElement;
    }

    @Override
    public CoordinatePoint render(CanvasContext context, int canvasWidth, int canvasHeight) {
        beforeRender(context);
        Dimension dimension = calculateDimension(context, canvasWidth, canvasHeight);
        debug(context, dimension);
        CoordinatePoint coordinatePoint = doRender(context, dimension, canvasWidth, canvasHeight);
        afterRender(context);
        return coordinatePoint;
    }

    /**
     * 计算元素尺寸
     */
    public abstract Dimension calculateDimension(CanvasContext context, int canvasWidth, int canvasHeight);

    /**
     * 执行渲染
     */
    public abstract CoordinatePoint doRender(CanvasContext context, Dimension dimension, int canvasWidth, int canvasHeight);

    /**
     * 渲染之前，做一些默认配置
     */
    public void beforeRender(CanvasContext context) {
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        context.getGraphics().setComposite(alphaComposite);
    }

    public void afterRender(CanvasContext context) {

    }

    public void gradient(CanvasContext context, Dimension dimension) {
        if (gradient != null) {
            context.getGraphics().setPaint(this.gradient.toGradient(dimension));
        }
    }

    public void debug(CanvasContext context, Dimension dimension) {
        if (context.getConfig().isDebug()) {
            Graphics2D graphics = context.getGraphics();
            Color oldColor = graphics.getColor();
            graphics.setColor(Color.BLACK);
            graphics.drawRect(dimension.getPoint().getX(), dimension.getPoint().getY(),
                    dimension.getWidth(), dimension.getHeight());
            graphics.setColor(oldColor);
        }
    }
}
