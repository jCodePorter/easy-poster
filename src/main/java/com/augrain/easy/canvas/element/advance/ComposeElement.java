package com.augrain.easy.canvas.element.advance;

import com.augrain.easy.canvas.element.AbstractElement;
import com.augrain.easy.canvas.element.AbstractRepeatableElement;
import com.augrain.easy.canvas.element.IElement;
import com.augrain.easy.canvas.geometry.CoordinatePoint;
import com.augrain.easy.canvas.geometry.Dimension;
import com.augrain.easy.canvas.geometry.Position;
import com.augrain.easy.canvas.geometry.RelativePosition;
import com.augrain.easy.canvas.model.CanvasContext;
import com.augrain.easy.canvas.model.RelativeDirection;
import com.augrain.easy.canvas.utils.PointUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static ComposeElement of(AbstractElement basicElement) {
        return new ComposeElement(basicElement);
    }

    /**
     * 在基准元素下方添加元素
     *
     * @param element 下方待添加的元素
     */
    public ComposeElement bottom(AbstractElement element) {
        next(element, RelativeDirection.BOTTOM);
        return this;
    }

    public ComposeElement top(AbstractElement element) {
        next(element, RelativeDirection.TOP);
        return this;
    }

    public ComposeElement left(AbstractElement element) {
        next(element, RelativeDirection.LEFT);
        return this;
    }

    public ComposeElement right(AbstractElement element) {
        next(element, RelativeDirection.RIGHT);
        return this;
    }

    public ComposeElement in(AbstractElement element) {
        next(element, RelativeDirection.IN);
        return this;
    }

    public ComposeElement next(AbstractElement element, RelativeDirection direction) {
        next(element, direction, true);
        return this;
    }

    public ComposeElement next(AbstractElement element, RelativeDirection direction, boolean strict) {
        elementWrapper.add(new ElementWrapper(element, direction, strict, false));
        return this;
    }

    public ComposeElement follow(AbstractElement element, RelativeDirection direction, boolean strict) {
        elementWrapper.add(new ElementWrapper(element, direction, strict, true));
        return this;
    }

    private static void doReset(ElementWrapper elementWrapper, int x, int y, AbstractElement element, Dimension dimension) {
        if (elementWrapper.getElement() instanceof ComposeElement) {
            ((ComposeElement) elementWrapper.getElement()).resetCoordinatePointOffset(x, y);
        } else {
            Position position = element.getPosition();
            if (position instanceof RelativePosition) {
                CoordinatePoint point = dimension.getPoint();
                point.setX(point.getX() + x);
                point.setY(point.getY() + y);
            }
        }
    }

    @Override
    public Dimension calculateDimension(CanvasContext context, int canvasWidth, int canvasHeight) {
        // 基准元素的尺寸
        Dimension basicDimension = basicElement.calculateDimension(context, canvasWidth, canvasHeight);
        dimensionMap.put(basicElement, basicDimension);

        Map<RelativeDirection, List<ElementWrapper>> elementGroup = elementWrapper.stream().collect(Collectors.groupingBy(ElementWrapper::getDirection));
        elementGroup.forEach((direction, elementWrapperList) -> {
            if (direction == RelativeDirection.BOTTOM) {
                doBottom(context, canvasWidth, canvasHeight, elementWrapperList, basicDimension);
            } else if (direction == RelativeDirection.TOP) {
                doTop(context, canvasWidth, elementWrapperList, basicDimension);
            } else if (direction == RelativeDirection.LEFT) {
                doLeft(context, canvasHeight, elementWrapperList, basicDimension);
            } else if (direction == RelativeDirection.RIGHT) {
                doRight(context, canvasWidth, canvasHeight, elementWrapperList, basicDimension);
            } else if (direction == RelativeDirection.IN) {
                doIn(context, elementWrapperList, basicDimension);
            }
        });

        // 根据各元素尺寸大小和位置信息，计算外接矩形，即待渲染组合元素的大小，基准点
        return calculateBoundingBox(dimensionMap);
    }

    private void doIn(CanvasContext context, List<ElementWrapper> elementWrapperList, Dimension basicDimension) {
        for (ElementWrapper wrapper : elementWrapperList) {
            wrapper.getElement().beforeRender(context);
            Dimension dimension = doCalRelativeIn(context, wrapper, basicDimension);
            dimensionMap.put(wrapper.getElement(), dimension);
        }
    }

    private void doRight(CanvasContext context, int canvasWidth, int canvasHeight, List<ElementWrapper> elementWrapperList, Dimension basicDimension) {
        Dimension last = null;
        for (ElementWrapper wrapper : elementWrapperList) {
            wrapper.getElement().beforeRender(context);
            Dimension dimension = doCalRelativeRight(context, canvasWidth, canvasHeight, wrapper, basicDimension);
            if (last != null && wrapper.isFollow()) {
                if (wrapper.getElement() instanceof ComposeElement) {
                    ((ComposeElement) wrapper.getElement()).resetCoordinatePointOffset(last.getWidth(), 0);
                } else {
                    dimension.getPoint().setX(dimension.getPoint().getX() + last.getWidth());
                }
            }
            last = dimension;
            dimensionMap.put(wrapper.getElement(), dimension);
        }
    }

    private void doLeft(CanvasContext context, int canvasHeight, List<ElementWrapper> elementWrapperList, Dimension basicDimension) {
        Dimension last = null;
        for (ElementWrapper wrapper : elementWrapperList) {
            wrapper.getElement().beforeRender(context);
            Dimension dimension = doCalRelativeLeft(context, canvasHeight, wrapper, basicDimension);
            if (last != null && wrapper.isFollow()) {
                dimension.getPoint().setX(dimension.getPoint().getX() - last.getWidth());
            }
            last = dimension;
            dimensionMap.put(wrapper.getElement(), dimension);
        }
    }

    private void doTop(CanvasContext context, int canvasWidth, List<ElementWrapper> elementWrapperList, Dimension basicDimension) {
        Dimension last = null;
        for (ElementWrapper wrapper : elementWrapperList) {
            wrapper.getElement().beforeRender(context);
            Dimension dimension = doCalRelativeTop(context, canvasWidth, wrapper, basicDimension);
            if (last != null && wrapper.isFollow()) {
                dimension.getPoint().setY(dimension.getPoint().getY() - last.getHeight());
            }
            last = dimension;
            dimensionMap.put(wrapper.getElement(), dimension);
        }
    }

    private void doBottom(CanvasContext context, int canvasWidth, int canvasHeight, List<ElementWrapper> elementWrapperList, Dimension basicDimension) {
        Dimension last = null;
        for (ElementWrapper wrapper : elementWrapperList) {
            wrapper.getElement().beforeRender(context);
            Dimension dimension = doCalRelativeBottom(context, canvasWidth, canvasHeight, wrapper, basicDimension);
            if (last != null && wrapper.isFollow()) {
                if (wrapper.getElement() instanceof ComposeElement) {
                    ((ComposeElement) wrapper.getElement()).resetCoordinatePointOffset(0, last.getHeight());
                } else {
                    dimension.getPoint().setY(dimension.getPoint().getY() + last.getHeight());
                }
            }
            last = dimension;
            dimensionMap.put(wrapper.getElement(), dimension);
        }
    }

    public void resetCoordinatePointOffset(int xOffset, int yOffset) {
        dimensionMap.forEach((element, dimension) -> {
            dimension.getPoint().setX(dimension.getPoint().getX() + xOffset);
            dimension.getPoint().setY(dimension.getPoint().getY() + yOffset);
        });
    }

    private Dimension doCalRelativeIn(CanvasContext context, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        Dimension dimension = element.calculateDimension(context, basicDimension.getWidth(), basicDimension.getHeight());

        // 根据相对的基准元素进行坐标修正
        Position position = element.getPosition();
        if (position instanceof RelativePosition) {
            CoordinatePoint point = dimension.getPoint();
            point.setX(point.getX() + basicDimension.getPoint().getX());
            point.setY(point.getY() + basicDimension.getPoint().getY());
        }
        return dimension;
    }

    private Dimension doCalRelativeLeft(CanvasContext context, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeHeight = elementWrapper.isStrict() ? basicDimension.getHeight() : canvasHeight;
        Dimension dimension = element.calculateDimension(context, basicDimension.getPoint().getX(), relativeHeight);

        // 根据相对的基准元素进行坐标修正
        int y = 0;
        if (elementWrapper.isStrict()) {
            y = basicDimension.getPoint().getY();
        }
        doReset(elementWrapper, 0, y, element, dimension);
        return dimension;
    }

    private Dimension doCalRelativeRight(CanvasContext context, int canvasWidth, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeHeight = elementWrapper.isStrict() ? basicDimension.getHeight() : canvasHeight;
        int relativeWidth = canvasWidth - basicDimension.getWidth() - basicDimension.getPoint().getX();
        Dimension dimension = element.calculateDimension(context, relativeWidth, relativeHeight);

        // 根据相对的基准元素进行坐标修正
        int x;
        int y = 0;
        if (elementWrapper.isStrict()) {
            x = basicDimension.getPoint().getX() + basicDimension.getWidth();
            y = basicDimension.getPoint().getY();
        } else {
            x = basicDimension.getPoint().getX() + basicDimension.getWidth();
        }
        doReset(elementWrapper, x, y, element, dimension);
        return dimension;
    }

    private Dimension doCalRelativeTop(CanvasContext context, int canvasWidth, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeWidth = elementWrapper.isStrict() ? basicDimension.getWidth() : canvasWidth;
        Dimension dimension = element.calculateDimension(context, relativeWidth, basicDimension.getPoint().getY());

        int x = 0;
        if (elementWrapper.isStrict()) {
            x = basicDimension.getPoint().getX();
        }
        doReset(elementWrapper, x, 0, element, dimension);
        return dimension;
    }

    private Dimension doCalRelativeBottom(CanvasContext context, int canvasWidth, int canvasHeight, ElementWrapper elementWrapper, Dimension basicDimension) {
        AbstractElement element = elementWrapper.getElement();

        int relativeWidth = elementWrapper.isStrict() ? basicDimension.getWidth() : canvasWidth;
        int relativeHeight = canvasHeight - basicDimension.getHeight() - basicDimension.getPoint().getY();
        Dimension dimension = element.calculateDimension(context, relativeWidth, relativeHeight);

        // 根据相对的基准元素进行坐标修正
        int x = 0;
        int y;
        if (elementWrapper.isStrict()) {
            x = basicDimension.getPoint().getX();
            y = basicDimension.getPoint().getY() + basicDimension.getHeight();
        } else {
            y = basicDimension.getPoint().getY() + basicDimension.getHeight();
        }
        doReset(elementWrapper, x, y, element, dimension);
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

        // 计算组合元素外接矩形
        Dimension dimension = PointUtils.boundingBox(points);
        // 外接矩形左上角坐标点，组合元素中也称之为基准坐标点
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
    public CoordinatePoint doRender(CanvasContext context, Dimension dimension, int canvasWidth, int canvasHeight) {
        Dimension basicDimension = dimensionMap.get(basicElement);

        if (getPosition() != null) {
            // 如果组合元素整体设置位置参数，则基于整体宽高重新计算
            CoordinatePoint markPoint = getPosition().calculate(canvasWidth, canvasHeight, dimension.getWidth(), dimension.getHeight());

            // 组合元素设置位置属性，重新调整坐标点
            CoordinatePoint point = basicDimension.getPoint();
            point.setX(markPoint.getX() + pointOffsetMap.get(basicElement).xOffset);
            point.setY(markPoint.getY() + pointOffsetMap.get(basicElement).yOffset);

            basicElement.beforeRender(context);
            basicElement.doRender(context, basicDimension, basicDimension.getWidth(), basicDimension.getHeight());

            for (ElementWrapper elementWrapper : elementWrapper) {
                AbstractElement element = elementWrapper.getElement();
                Dimension elementDimension = dimensionMap.get(element);
                CoordinatePoint elementPoint = elementDimension.getPoint();
                elementPoint.setX(markPoint.getX() + pointOffsetMap.get(element).xOffset);
                elementPoint.setY(markPoint.getY() + pointOffsetMap.get(element).yOffset);

                element.beforeRender(context);
                element.doRender(context, elementDimension, basicDimension.getWidth(), basicDimension.getHeight());
                element.afterRender(context);
            }
            return null;
        } else {
            basicElement.beforeRender(context);
            CoordinatePoint basicPoint = basicElement.doRender(context, basicDimension, canvasWidth, canvasHeight);

            for (ElementWrapper elementWrapper : elementWrapper) {
                AbstractElement element = elementWrapper.getElement();
                element.beforeRender(context);
                element.doRender(context, dimensionMap.get(element), basicDimension.getWidth(), basicDimension.getHeight());
                element.afterRender(context);
            }
            return basicPoint;
        }
    }

    @Override
    public void beforeRender(CanvasContext context) {
        super.beforeRender(context);
        basicElement.beforeRender(context);
    }

    @Override
    public void afterRender(CanvasContext context) {
        super.afterRender(context);
        basicElement.afterRender(context);
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

        /**
         * 是否跟随，设置为true，当添加多个同一个方向的元素时，会再次参考前一个元素的位置
         */
        private boolean follow;

        public ElementWrapper(AbstractElement element) {
            this.element = element;
        }

        public ElementWrapper(AbstractElement element, RelativeDirection direction, boolean strict, boolean follow) {
            this.element = element;
            this.direction = direction;
            this.strict = strict;
            this.follow = follow;
        }
    }
}
