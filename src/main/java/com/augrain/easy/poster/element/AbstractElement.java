package com.augrain.easy.poster.element;

import cn.augrain.easy.tool.support.ColorUtils;
import com.augrain.easy.poster.element.advance.ComposeElement;
import com.augrain.easy.poster.exception.PosterException;
import com.augrain.easy.poster.geometry.Dimension;
import com.augrain.easy.poster.geometry.Point;
import com.augrain.easy.poster.geometry.Position;
import com.augrain.easy.poster.model.Gradient;
import com.augrain.easy.poster.model.PosterContext;
import com.augrain.easy.poster.model.RelativeDirection;
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

    /**
     * 颜色
     */
    protected Color color = Color.BLACK;

    public T setColor(final Color color) {
        this.color = color;
        return (T) this;
    }

    /**
     * 设置文本颜色，十六进制模式
     *
     * @param color 颜色，比如 #FF0000
     */
    public T setColor(String color) {
        this.color = ColorUtils.hexToColor(color);
        return (T) this;
    }

    public T setAlpha(float alpha) {
        if (alpha < 0 || alpha > 1) {
            throw new PosterException("alpha must be between 0 and 1");
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
    public Point render(PosterContext context, int posterWidth, int posterHeight) {
        beforeRender(context);
        Dimension dimension = calculateDimension(context, posterWidth, posterHeight);
        debug(context, dimension);
        Point coordinatePoint = doRender(context, dimension, posterWidth, posterHeight);
        afterRender(context);
        return coordinatePoint;
    }

    /**
     * 计算元素尺寸
     */
    public abstract Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight);

    /**
     * 执行渲染
     */
    public abstract Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight);

    /**
     * 渲染之前，做一些默认配置
     */
    public void beforeRender(PosterContext context) {
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        context.getGraphics().setComposite(alphaComposite);
    }

    public void afterRender(PosterContext context) {

    }

    public void gradient(PosterContext context, Dimension dimension) {
        if (gradient != null) {
            context.getGraphics().setPaint(this.gradient.toGradient(dimension));
        }
    }

    public void debug(PosterContext context, Dimension dimension) {
        if (context.getConfig().isDebug()) {
            // Some elements do not have obvious sizes, for example LineElement
            if (dimension != null) {
                Graphics2D graphics = context.getGraphics();
                Color oldColor = graphics.getColor();
                graphics.setColor(Color.BLACK);
                graphics.drawRect(dimension.getPoint().getX(), dimension.getPoint().getY(),
                        dimension.getWidth(), dimension.getHeight());
                graphics.setColor(oldColor);
            }
        }
    }
}
