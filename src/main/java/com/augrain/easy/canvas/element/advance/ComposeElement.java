package com.augrain.easy.canvas.element.advance;

import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.AbstractRepeatableElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.geometry.Position;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.model.RelativeDirection;
import com.augrain.easy.canvas.utils.PointUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组合元素，可以以一个基础元素作为基准，在他的上下左右添加附加元素
 * 实验性质，当前基本可用
 *
 * @author biaoy
 * @since 2025/03/06
 */
public class ComposeElement extends AbstractRepeatableElement<ComposeElement> implements IElement {

    private final AbstractElement basicElement;

    private final List<ElementWrapper> elementWrapper = new ArrayList<>();

    private final Map<AbstractElement, Dimension> dimensionMap = new HashMap<>();

    private final Map<AbstractElement, PointOffset> pointOffsetMap = new HashMap<>();

    public ComposeElement(AbstractElement basicElement) {
        this.basicElement = basicElement;
    }

    /**
     * 在基准元素下方添加元素
     *
     * @param element 下方待添加的元素
     */
    public ComposeElement bottom(AbstractElement element) {
        add(element, RelativeDirection.BOTTOM);
        return this;
    }

    public ComposeElement top(AbstractElement element) {
        add(element, RelativeDirection.TOP);
        return this;
    }

    public ComposeElement left(AbstractElement element) {
        add(element, RelativeDirection.LEFT);
        return this;
    }

    public ComposeElement right(AbstractElement element) {
        add(element, RelativeDirection.RIGHT);
        return this;
    }

    public ComposeElement in(AbstractElement element) {
        add(element, RelativeDirection.IN);
        return this;
    }

    public ComposeElement add(AbstractElement element, RelativeDirection direction) {
        add(element, direction, true);
        return this;
    }

    public ComposeElement add(AbstractElement element, RelativeDirection direction, boolean strict) {
        elementWrapper.add(new ElementWrapper(element, direction, strict));
        return this;
    }

    @Override
    public Dimension calculateDimension(Graphics2D g, int canvasWidth, int canvasHeight) {
        // 基准元素的尺寸
        Dimension basicDimension = basicElement.calculateDimension(g, canvasWidth, canvasHeight);
        dimensionMap.put(basicElement, basicDimension);

        for (ElementWrapper wrapper : elementWrapper) {
            Dimension dimension = calRelativeElementDimension(g, canvasWidth, canvasHeight, wrapper, basicDimension);
            dimensionMap.put(wrapper.getElement(), dimension);
        }

        // 根据各元素尺寸大小和位置信息，计算外接矩形，即待渲染组合元素的大小，基准点
        return calculateBoundingBox(dimensionMap);
    }

    private Dimension calRelativeElementDimension(Graphics2D g, int canvasWidth, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        if (elementWrapper.getDirection() == RelativeDirection.BOTTOM) {
            return doCalRelativeBottom(g, canvasWidth, canvasHeight, elementWrapper, basicDimension);
        } else if (elementWrapper.getDirection() == RelativeDirection.TOP) {
            return doCalRelativeTop(g, canvasWidth, elementWrapper, basicDimension);
        } else if (elementWrapper.getDirection() == RelativeDirection.LEFT) {
            return doCalRelativeLeft(g, canvasHeight, elementWrapper, basicDimension);
        } else if (elementWrapper.getDirection() == RelativeDirection.RIGHT) {
            return doCalRelativeRight(g, canvasWidth, canvasHeight, elementWrapper, basicDimension);
        } else if (elementWrapper.getDirection() == RelativeDirection.IN) {
            return doCalRelativeIn(g, elementWrapper, basicDimension);
        }
        throw new UnsupportedOperationException("未知的相对位置属性");
    }

    private Dimension doCalRelativeIn(Graphics2D g, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        Dimension dimension = element.calculateDimension(g, basicDimension.getWidth(), basicDimension.getHeight());

        // 根据相对的基准元素进行坐标修正
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            CoordinatePoint point = dimension.getPoint();
            point.setX(point.getX() + basicDimension.getPoint().getX());
            point.setY(point.getY() + basicDimension.getPoint().getY());
        }
        return dimension;
    }

    private Dimension doCalRelativeRight(Graphics2D g, int canvasWidth, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeHeight = elementWrapper.isStrict() ? basicDimension.getHeight() : canvasHeight;
        int relativeWidth = canvasWidth - basicDimension.getWidth() - basicDimension.getPoint().getX();
        Dimension dimension = element.calculateDimension(g, relativeWidth, relativeHeight);

        // 根据相对的基准元素进行坐标修正
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            CoordinatePoint point = dimension.getPoint();
            if (elementWrapper.isStrict()) {
                point.setX(point.getX() + basicDimension.getPoint().getX() + basicDimension.getWidth());
                point.setY(point.getY() + basicDimension.getPoint().getY());
            } else {
                point.setX(point.getX() + basicDimension.getPoint().getX() + basicDimension.getWidth());
            }
        }
        return dimension;
    }

    private Dimension doCalRelativeLeft(Graphics2D g, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeHeight = elementWrapper.isStrict() ? basicDimension.getHeight() : canvasHeight;
        Dimension dimension = element.calculateDimension(g, basicDimension.getPoint().getX(), relativeHeight);

        // 根据相对的基准元素进行坐标修正
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            CoordinatePoint point = dimension.getPoint();
            if (elementWrapper.isStrict()) {
                point.setY(point.getY() + basicDimension.getPoint().getY());
            }
        }
        return dimension;
    }

    private Dimension doCalRelativeTop(Graphics2D g, int canvasWidth, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeWidth = elementWrapper.isStrict() ? basicDimension.getWidth() : canvasWidth;
        Dimension dimension = element.calculateDimension(g, relativeWidth, basicDimension.getPoint().getY());

        // 根据相对的基准元素进行坐标修正
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            CoordinatePoint point = dimension.getPoint();
            if (elementWrapper.isStrict()) {
                point.setX(point.getX() + basicDimension.getPoint().getX());
            }
        }
        return dimension;
    }

    private Dimension doCalRelativeBottom(Graphics2D g, int canvasWidth, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeWidth = elementWrapper.isStrict() ? basicDimension.getWidth() : canvasWidth;
        int relativeHeight = canvasHeight - basicDimension.getHeight() - basicDimension.getPoint().getY();
        Dimension dimension = element.calculateDimension(g, relativeWidth, relativeHeight);

        // 根据相对的基准元素进行坐标修正
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            CoordinatePoint point = dimension.getPoint();
            if (elementWrapper.isStrict()) {
                point.setX(point.getX() + basicDimension.getPoint().getX());
                point.setY(point.getY() + basicDimension.getPoint().getY() + basicDimension.getHeight());
            } else {
                point.setY(point.getY() + basicDimension.getPoint().getY() + basicDimension.getHeight());
            }
        }
        return dimension;
    }

    private Dimension calculateBoundingBox(Map<AbstractElement, Dimension> dimensionMap) {
        List<CoordinatePoint> points = new ArrayList<>();

        // 根据渲染元素宽高与左上角坐标点，计算四角坐标
        dimensionMap.forEach((k, v) -> {
            CoordinatePoint point = v.getPoint();
            int startX = point.getX();
            int startY = point.getY();

            points.add(CoordinatePoint.of(startX, startY));
            points.add(CoordinatePoint.of(startX + v.getWidth(), startY));
            points.add(CoordinatePoint.of(startX, startY + v.getHeight()));
            points.add(CoordinatePoint.of(startX + v.getWidth(), startY + v.getHeight()));
        });
        Dimension dimension = PointUtils.boundingBox(points);
        CoordinatePoint markPoint = dimension.getPoint();

        // 计算差值
        dimensionMap.forEach((k, v) -> {
            CoordinatePoint point = v.getPoint();
            int xOffset = point.getX() - markPoint.getX();
            int yOffset = point.getY() - markPoint.getY();
            PointOffset pointOffset = new PointOffset(xOffset, yOffset);
            pointOffsetMap.put(k, pointOffset);
        });
        return dimension;
    }

    @Override
    public CoordinatePoint doRender(Graphics2D g, Dimension dimension, int canvasWidth, int canvasHeight) {
        Dimension basicDimension = dimensionMap.get(basicElement);

        if (getPosition() != null) {
            // 如果组合元素整体设置位置参数，则基于整体宽高重新计算
            CoordinatePoint markPoint = getPosition().calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());

            // 组合元素设置位置属性，重新调整坐标点
            CoordinatePoint point = basicDimension.getPoint();
            point.setX(markPoint.getX() + pointOffsetMap.get(basicElement).xOffset);
            point.setY(markPoint.getY() + pointOffsetMap.get(basicElement).yOffset);
            basicElement.doRender(g, basicDimension, basicDimension.getWidth(), basicDimension.getHeight());

            for (ElementWrapper elementWrapper : elementWrapper) {
                AbstractElement element = elementWrapper.getElement();
                Dimension elementDimension = dimensionMap.get(element);
                CoordinatePoint elementPoint = elementDimension.getPoint();
                elementPoint.setX(markPoint.getX() + pointOffsetMap.get(element).xOffset);
                elementPoint.setY(markPoint.getY() + pointOffsetMap.get(element).yOffset);
                element.doRender(g, elementDimension, basicDimension.getWidth(), basicDimension.getHeight());
            }
            return null;
        } else {
            CoordinatePoint basicPoint = basicElement.doRender(g, basicDimension, canvasWidth, canvasHeight);

            for (ElementWrapper elementWrapper : elementWrapper) {
                AbstractElement element = elementWrapper.getElement();
                element.doRender(g, dimensionMap.get(element), basicDimension.getWidth(), basicDimension.getHeight());
            }
            return basicPoint;
        }
    }

    @Override
    public void beforeRender(Graphics2D g) {
        basicElement.beforeRender(g);

        elementWrapper.forEach(b -> b.getElement().beforeRender(g));
    }

    @Getter
    @Setter
    private static class ElementWrapper {
        /**
         * 包装的基础元素
         */
        private AbstractElement element;

        /**
         * 相对基准元素的方向
         */
        private RelativeDirection direction;

        /**
         * 是否严格模式，比如 direction = RelativeDirection.BOTTOM时，
         * 如果strict = true，则表示相对基准元素在其垂直正下方，否则表示相对于基准元素的下方
         */
        private boolean strict;

        public ElementWrapper(AbstractElement element) {
            this.element = element;
        }

        public ElementWrapper(AbstractElement element, RelativeDirection direction, boolean strict) {
            this.element = element;
            this.direction = direction;
            this.strict = strict;
        }
    }

    /**
     * 组合元素坐标点与基准坐标的偏移量
     */
    private static class PointOffset {
        private final int xOffset;

        private final int yOffset;

        public PointOffset(int x, int y) {
            this.xOffset = x;
            this.yOffset = y;
        }
    }
}
