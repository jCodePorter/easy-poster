package com.bytefuture.easy.poster.element.advance;

import com.bytefuture.easy.poster.element.AbstractElement;
import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.geometry.AbsolutePosition;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.ContainerAlign;
import com.bytefuture.easy.poster.model.ContainerLayoutMode;
import com.bytefuture.easy.poster.model.PosterContext;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 容器元素
 *
 * @author Codex
 * @since 2026/05/01
 */
public class ContainerElement extends AbstractRepeatableElement<ContainerElement> implements IElement {

    /**
     * 子元素集合
     */
    private final List<AbstractElement> children = new ArrayList<>();

    /**
     * 子元素尺寸缓存
     */
    private final Map<AbstractElement, Dimension> childDimensionMap = new LinkedHashMap<>();

    /**
     * 子元素外边距缓存
     */
    private final Map<AbstractElement, Margin> childMarginMap = new LinkedHashMap<>();

    /**
     * 容器宽度
     */
    protected int width;

    /**
     * 容器高度
     */
    protected int height;

    /**
     * 容器内边距
     */
    private Margin padding = Margin.of(0);

    /**
     * 容器背景色
     */
    private Color backgroundColor;

    /**
     * 边框颜色
     */
    private Color borderColor;

    /**
     * 边框宽度
     */
    private int borderSize = 0;

    /**
     * 是否裁剪内容到内容区
     */
    private boolean clipContent = false;

    /**
     * 圆角宽度
     */
    private int arcWidth = 0;

    /**
     * 圆角高度
     */
    private int arcHeight = 0;

    /**
     * 容器布局模式
     */
    private ContainerLayoutMode layoutMode = ContainerLayoutMode.FREE;

    /**
     * 子元素间距
     */
    private int gap = 0;

    /**
     * 主轴对齐方式
     */
    private ContainerAlign justifyContent = ContainerAlign.START;

    /**
     * 交叉轴对齐方式
     */
    private ContainerAlign alignItems = ContainerAlign.START;

    public ContainerElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 创建容器元素
     *
     * @param width 容器宽度
     * @param height 容器高度
     * @return 容器元素
     */
    public static ContainerElement of(int width, int height) {
        return new ContainerElement(width, height);
    }

    /**
     * 添加子元素
     *
     * @param element 子元素
     * @return 当前容器
     */
    public ContainerElement addChild(AbstractElement element) {
        this.children.add(element);
        this.childMarginMap.putIfAbsent(element, Margin.of(0));
        return this;
    }

    /**
     * 添加带外边距的子元素
     *
     * @param element 子元素
     * @param margin 子元素外边距
     * @return 当前容器
     */
    public ContainerElement addChild(AbstractElement element, Margin margin) {
        this.children.add(element);
        this.childMarginMap.put(element, margin);
        return this;
    }

    /**
     * 批量添加子元素
     *
     * @param elements 子元素集合
     * @return 当前容器
     */
    public ContainerElement addChildren(List<? extends AbstractElement> elements) {
        for (AbstractElement element : elements) {
            addChild(element);
        }
        return this;
    }

    /**
     * 设置子元素外边距
     *
     * @param element 子元素
     * @param margin 子元素外边距
     * @return 当前容器
     */
    public ContainerElement setChildMargin(AbstractElement element, Margin margin) {
        this.childMarginMap.put(element, margin);
        return this;
    }

    /**
     * 设置容器内边距
     *
     * @param padding 内边距
     * @return 当前容器
     */
    public ContainerElement setPadding(Margin padding) {
        this.padding = padding;
        return this;
    }

    /**
     * 设置容器背景色
     *
     * @param backgroundColor 背景色
     * @return 当前容器
     */
    public ContainerElement setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    /**
     * 设置边框颜色
     *
     * @param borderColor 边框颜色
     * @return 当前容器
     */
    public ContainerElement setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    /**
     * 设置边框宽度
     *
     * @param borderSize 边框宽度
     * @return 当前容器
     */
    public ContainerElement setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        return this;
    }

    /**
     * 设置容器圆角
     *
     * @param arc 圆角大小
     * @return 当前容器
     */
    public ContainerElement setArc(int arc) {
        this.arcWidth = arc;
        this.arcHeight = arc;
        return this;
    }

    /**
     * 设置容器圆角
     *
     * @param arcWidth 圆角宽度
     * @param arcHeight 圆角高度
     * @return 当前容器
     */
    public ContainerElement setArc(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        return this;
    }

    /**
     * 设置是否裁剪内容
     *
     * @param clipContent 是否裁剪到内容区
     * @return 当前容器
     */
    public ContainerElement setClipContent(boolean clipContent) {
        this.clipContent = clipContent;
        return this;
    }

    /**
     * 设置布局模式
     *
     * @param layoutMode 布局模式
     * @return 当前容器
     */
    public ContainerElement setLayoutMode(ContainerLayoutMode layoutMode) {
        this.layoutMode = layoutMode;
        return this;
    }

    /**
     * 设置子元素间距
     *
     * @param gap 子元素间距
     * @return 当前容器
     */
    public ContainerElement setGap(int gap) {
        this.gap = gap;
        return this;
    }

    /**
     * 设置主轴对齐方式
     *
     * @param justifyContent 主轴对齐方式
     * @return 当前容器
     */
    public ContainerElement setJustifyContent(ContainerAlign justifyContent) {
        this.justifyContent = justifyContent;
        return this;
    }

    /**
     * 设置交叉轴对齐方式
     *
     * @param alignItems 交叉轴对齐方式
     * @return 当前容器
     */
    public ContainerElement setAlignItems(ContainerAlign alignItems) {
        this.alignItems = alignItems;
        return this;
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        childDimensionMap.clear();
        if (layoutMode == ContainerLayoutMode.FREE) {
            return calculateFreeDimension(context, posterWidth, posterHeight);
        }
        return calculateFlowDimension(context, posterWidth, posterHeight);
    }

    @Override
    public Point doRender(PosterContext context, Dimension dimension, int posterWidth, int posterHeight) {
        Graphics2D graphics = context.getGraphics();
        Point point = dimension.getPoint();
        int contentWidth = getContentWidth(dimension.getWidth());
        int contentHeight = getContentHeight(dimension.getHeight());

        paintBackground(graphics, point, dimension.getWidth(), dimension.getHeight());
        paintBorder(graphics, point, dimension.getWidth(), dimension.getHeight());

        Shape oldClip = graphics.getClip();
        if (clipContent) {
            graphics.clip(createContentShape(point, dimension.getWidth(), dimension.getHeight()));
        }

        for (AbstractElement child : children) {
            Dimension childDimension = childDimensionMap.get(child);
            child.beforeRender(context);
            child.doRender(context, childDimension, contentWidth, contentHeight);
            child.afterRender(context);
        }

        if (clipContent) {
            graphics.setClip(oldClip);
        }
        return point;
    }

    /**
     * 计算自由布局尺寸
     *
     * @param context 上下文
     * @param posterWidth 可用宽度
     * @param posterHeight 可用高度
     * @return 容器尺寸
     */
    private Dimension calculateFreeDimension(PosterContext context, int posterWidth, int posterHeight) {
        int realWidth = width == 0 ? posterWidth : width;
        int realHeight = height == 0 ? posterHeight : height;
        Point point = position == null ? Point.ORIGIN_COORDINATE
                : position.calculate(posterWidth, posterHeight, realWidth, realHeight);
        Dimension dimension = Dimension.builder()
                .width(realWidth)
                .height(realHeight)
                .point(Point.of(point.getX(), point.getY()))
                .build();

        int contentWidth = getContentWidth(realWidth);
        int contentHeight = getContentHeight(realHeight);
        int contentStartX = point.getX() + padding.getMarginLeft();
        int contentStartY = point.getY() + padding.getMarginTop();
        for (AbstractElement child : children) {
            Dimension childDimension = child.calculateDimension(context, contentWidth, contentHeight);
            normalizeChildPoint(child, childDimension, contentStartX, contentStartY);
            childDimensionMap.put(child, childDimension);
        }
        return dimension;
    }

    /**
     * 计算流式布局尺寸
     *
     * @param context 上下文
     * @param posterWidth 可用宽度
     * @param posterHeight 可用高度
     * @return 容器尺寸
     */
    private Dimension calculateFlowDimension(PosterContext context, int posterWidth, int posterHeight) {
        boolean vertical = layoutMode == ContainerLayoutMode.VERTICAL;
        int tentativeWidth = resolveTentativeWidth(vertical, posterWidth);
        int tentativeHeight = resolveTentativeHeight(vertical, posterHeight);
        int contentWidth = getContentWidth(tentativeWidth);
        int contentHeight = getContentHeight(tentativeHeight);

        List<Dimension> measuredChildren = new ArrayList<>();
        int totalMainAxisSize = 0;
        for (AbstractElement child : children) {
            Dimension childDimension = child.calculateDimension(context, contentWidth, contentHeight);
            childDimension.setPoint(Point.of(0, 0));
            measuredChildren.add(childDimension);
            Margin childMargin = getChildMargin(child);
            int childMainAxisSize = vertical
                    ? childMargin.getMarginTop() + childDimension.getHeight() + childMargin.getMarginBottom()
                    : childMargin.getMarginLeft() + childDimension.getWidth() + childMargin.getMarginRight();
            int childCrossAxisSize = vertical
                    ? childMargin.getMarginLeft() + childDimension.getWidth() + childMargin.getMarginRight()
                    : childMargin.getMarginTop() + childDimension.getHeight() + childMargin.getMarginBottom();
            totalMainAxisSize += childMainAxisSize;
        }
        if (!children.isEmpty()) {
            totalMainAxisSize += gap * (children.size() - 1);
        }

        int realWidth = width;
        int realHeight = height;
        if (vertical) {
            if (realWidth == 0) {
                realWidth = posterWidth;
            }
            if (realHeight == 0) {
                realHeight = padding.getMarginTop() + padding.getMarginBottom() + totalMainAxisSize;
            }
        } else {
            if (realWidth == 0) {
                realWidth = padding.getMarginLeft() + padding.getMarginRight() + totalMainAxisSize;
            }
            if (realHeight == 0) {
                realHeight = posterHeight;
            }
        }

        Point point = position == null ? Point.ORIGIN_COORDINATE
                : position.calculate(posterWidth, posterHeight, realWidth, realHeight);
        Dimension dimension = Dimension.builder()
                .width(realWidth)
                .height(realHeight)
                .point(Point.of(point.getX(), point.getY()))
                .build();

        layoutMeasuredChildren(measuredChildren, dimension, totalMainAxisSize, vertical);
        for (int i = 0; i < children.size(); i++) {
            childDimensionMap.put(children.get(i), measuredChildren.get(i));
        }
        return dimension;
    }

    /**
     * 布局测量后的子元素
     *
     * @param measuredChildren 子元素尺寸集合
     * @param dimension 容器尺寸
     * @param totalMainAxisSize 主轴总尺寸
     * @param vertical 是否垂直布局
     */
    private void layoutMeasuredChildren(List<Dimension> measuredChildren, Dimension dimension, int totalMainAxisSize,
                                        boolean vertical) {
        int contentWidth = getContentWidth(dimension.getWidth());
        int contentHeight = getContentHeight(dimension.getHeight());
        int contentStartX = dimension.getPoint().getX() + padding.getMarginLeft();
        int contentStartY = dimension.getPoint().getY() + padding.getMarginTop();
        int availableMainAxisSize = vertical ? contentHeight : contentWidth;
        int mainAxisOffset = resolveMainAxisOffset(availableMainAxisSize, totalMainAxisSize);
        int currentOffset = mainAxisOffset;

        for (int i = 0; i < measuredChildren.size(); i++) {
            Dimension childDimension = measuredChildren.get(i);
            Margin childMargin = getChildMargin(children.get(i));
            int childWidth = childDimension.getWidth();
            int childHeight = childDimension.getHeight();
            int x = contentStartX;
            int y = contentStartY;
            if (vertical) {
                x += childMargin.getMarginLeft() + resolveCrossAxisOffset(
                        contentWidth - childMargin.getMarginLeft() - childMargin.getMarginRight(), childWidth);
                y += currentOffset + childMargin.getMarginTop();
                currentOffset += childMargin.getMarginTop() + childHeight + childMargin.getMarginBottom() + gap;
            } else {
                x += currentOffset + childMargin.getMarginLeft();
                y += childMargin.getMarginTop() + resolveCrossAxisOffset(
                        contentHeight - childMargin.getMarginTop() - childMargin.getMarginBottom(), childHeight);
                currentOffset += childMargin.getMarginLeft() + childWidth + childMargin.getMarginRight() + gap;
            }
            childDimension.setPoint(Point.of(x, y));
        }
    }

    /**
     * 获取子元素外边距
     *
     * @param child 子元素
     * @return 子元素外边距
     */
    private Margin getChildMargin(AbstractElement child) {
        return childMarginMap.getOrDefault(child, Margin.of(0));
    }

    /**
     * 解析垂直布局时的临时宽度
     *
     * @param vertical 是否垂直布局
     * @param posterWidth 可用宽度
     * @return 临时宽度
     */
    private int resolveTentativeWidth(boolean vertical, int posterWidth) {
        if (vertical) {
            return width == 0 ? posterWidth : width;
        }
        return width == 0 ? posterWidth : width;
    }

    /**
     * 解析水平布局时的临时高度
     *
     * @param vertical 是否垂直布局
     * @param posterHeight 可用高度
     * @return 临时高度
     */
    private int resolveTentativeHeight(boolean vertical, int posterHeight) {
        if (vertical) {
            return height == 0 ? posterHeight : height;
        }
        return height == 0 ? posterHeight : height;
    }

    /**
     * 计算主轴偏移量
     *
     * @param availableMainAxisSize 主轴可用尺寸
     * @param totalMainAxisSize 子元素主轴总尺寸
     * @return 主轴偏移量
     */
    private int resolveMainAxisOffset(int availableMainAxisSize, int totalMainAxisSize) {
        if (justifyContent == ContainerAlign.CENTER) {
            return Math.max(0, (availableMainAxisSize - totalMainAxisSize) / 2);
        }
        if (justifyContent == ContainerAlign.END) {
            return Math.max(0, availableMainAxisSize - totalMainAxisSize);
        }
        return 0;
    }

    /**
     * 计算交叉轴偏移量
     *
     * @param availableCrossAxisSize 交叉轴可用尺寸
     * @param childCrossAxisSize 子元素交叉轴尺寸
     * @return 交叉轴偏移量
     */
    private int resolveCrossAxisOffset(int availableCrossAxisSize, int childCrossAxisSize) {
        if (alignItems == ContainerAlign.CENTER) {
            return Math.max(0, (availableCrossAxisSize - childCrossAxisSize) / 2);
        }
        if (alignItems == ContainerAlign.END) {
            return Math.max(0, availableCrossAxisSize - childCrossAxisSize);
        }
        return 0;
    }

    /**
     * 计算内容区宽度
     *
     * @param outerWidth 容器总宽度
     * @return 内容区宽度
     */
    private int getContentWidth(int outerWidth) {
        return Math.max(0, outerWidth - padding.getMarginLeft() - padding.getMarginRight());
    }

    /**
     * 计算内容区高度
     *
     * @param outerHeight 容器总高度
     * @return 内容区高度
     */
    private int getContentHeight(int outerHeight) {
        return Math.max(0, outerHeight - padding.getMarginTop() - padding.getMarginBottom());
    }

    /**
     * 标准化子元素坐标到全局坐标系
     *
     * @param child 子元素
     * @param childDimension 子元素尺寸
     * @param contentStartX 内容区起始 X
     * @param contentStartY 内容区起始 Y
     */
    private void normalizeChildPoint(AbstractElement child, Dimension childDimension, int contentStartX, int contentStartY) {
        if (child.getPosition() instanceof AbsolutePosition) {
            return;
        }
        Point point = childDimension.getPoint();
        point.setX(point.getX() + contentStartX);
        point.setY(point.getY() + contentStartY);
    }

    /**
     * 绘制容器背景
     *
     * @param graphics 画笔
     * @param point 容器左上角坐标
     * @param outerWidth 容器总宽度
     * @param outerHeight 容器总高度
     */
    private void paintBackground(Graphics2D graphics, Point point, int outerWidth, int outerHeight) {
        if (backgroundColor == null) {
            return;
        }
        Color oldColor = graphics.getColor();
        graphics.setColor(backgroundColor);
        graphics.fill(createOuterShape(point, outerWidth, outerHeight));
        graphics.setColor(oldColor);
    }

    /**
     * 绘制容器边框
     *
     * @param graphics 画笔
     * @param point 容器左上角坐标
     * @param outerWidth 容器总宽度
     * @param outerHeight 容器总高度
     */
    private void paintBorder(Graphics2D graphics, Point point, int outerWidth, int outerHeight) {
        if (borderColor == null || borderSize <= 0) {
            return;
        }
        Color oldColor = graphics.getColor();
        graphics.setColor(borderColor);
        if (arcWidth > 0 || arcHeight > 0) {
            Shape outerShape = createOuterShape(point, outerWidth, outerHeight);
            Shape innerShape = createInnerBorderShape(point, outerWidth, outerHeight);
            Area borderArea = new Area(outerShape);
            if (innerShape != null) {
                borderArea.subtract(new Area(innerShape));
            }
            graphics.fill(borderArea);
        } else {
            for (int i = 0; i < borderSize; i++) {
                int borderWidth = outerWidth - 1 - i * 2;
                int borderHeight = outerHeight - 1 - i * 2;
                if (borderWidth < 0 || borderHeight < 0) {
                    break;
                }
                graphics.drawRect(point.getX() + i, point.getY() + i, borderWidth, borderHeight);
            }
        }
        graphics.setColor(oldColor);
    }

    /**
     * 创建容器外部形状
     *
     * @param point 容器左上角坐标
     * @param outerWidth 容器总宽度
     * @param outerHeight 容器总高度
     * @return 容器外部形状
     */
    private Shape createOuterShape(Point point, int outerWidth, int outerHeight) {
        if (arcWidth <= 0 && arcHeight <= 0) {
            return new Rectangle(point.getX(), point.getY(), outerWidth, outerHeight);
        }
        return new RoundRectangle2D.Double(point.getX(), point.getY(), outerWidth, outerHeight, arcWidth, arcHeight);
    }

    /**
     * 创建边框内部镂空形状
     *
     * @param point 容器左上角坐标
     * @param outerWidth 容器总宽度
     * @param outerHeight 容器总高度
     * @return 边框内部形状
     */
    private Shape createInnerBorderShape(Point point, int outerWidth, int outerHeight) {
        int innerWidth = outerWidth - borderSize * 2;
        int innerHeight = outerHeight - borderSize * 2;
        if (innerWidth <= 0 || innerHeight <= 0) {
            return null;
        }
        int innerArcWidth = Math.max(0, arcWidth - borderSize * 2);
        int innerArcHeight = Math.max(0, arcHeight - borderSize * 2);
        if (innerArcWidth <= 0 && innerArcHeight <= 0) {
            return new Rectangle(point.getX() + borderSize, point.getY() + borderSize, innerWidth, innerHeight);
        }
        return new RoundRectangle2D.Double(point.getX() + borderSize, point.getY() + borderSize,
                innerWidth, innerHeight, innerArcWidth, innerArcHeight);
    }

    /**
     * 创建内容区裁剪形状
     *
     * @param point 容器左上角坐标
     * @param outerWidth 容器总宽度
     * @param outerHeight 容器总高度
     * @return 内容区裁剪形状
     */
    private Shape createContentShape(Point point, int outerWidth, int outerHeight) {
        int contentX = point.getX() + padding.getMarginLeft();
        int contentY = point.getY() + padding.getMarginTop();
        int contentWidth = getContentWidth(outerWidth);
        int contentHeight = getContentHeight(outerHeight);
        int contentArcWidth = Math.max(0, arcWidth - padding.getMarginLeft() - padding.getMarginRight());
        int contentArcHeight = Math.max(0, arcHeight - padding.getMarginTop() - padding.getMarginBottom());
        if (contentArcWidth <= 0 && contentArcHeight <= 0) {
            return new Rectangle(contentX, contentY, contentWidth, contentHeight);
        }
        return new RoundRectangle2D.Double(contentX, contentY, contentWidth, contentHeight,
                contentArcWidth, contentArcHeight);
    }
}
