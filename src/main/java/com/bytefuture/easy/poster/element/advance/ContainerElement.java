package com.bytefuture.easy.poster.element.advance;

import com.bytefuture.easy.poster.element.AbstractElement;
import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.PosterContext;
import com.bytefuture.easy.poster.model.FloatType;
import com.bytefuture.easy.poster.model.ClearType;

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
 * @author biaoy
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
     * 子元素间距
     */
    private int gap = 0;

    /**
     * 浮动布局的行信息
     */
    private static class FloatRow {
        int y;
        int height;
        int leftAvailable;
        int rightAvailable;

        FloatRow(int y, int width) {
            this.y = y;
            this.height = 0;
            this.leftAvailable = 0;
            this.rightAvailable = width;
        }
    }

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
     * 设置子元素间距
     *
     * @param gap 子元素间距
     * @return 当前容器
     */
    public ContainerElement setGap(int gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public Dimension calculateDimension(PosterContext context, int posterWidth, int posterHeight) {
        childDimensionMap.clear();
        return calculateFloatDimension(context, posterWidth, posterHeight);
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
     * 计算浮动布局尺寸
     *
     * @param context 上下文
     * @param posterWidth 可用宽度
     * @param posterHeight 可用高度
     * @return 容器尺寸
     */
    private Dimension calculateFloatDimension(PosterContext context, int posterWidth, int posterHeight) {
        int realWidth = width == 0 ? posterWidth : width;
        int realHeight = height == 0 ? posterHeight : height;
        int contentWidth = getContentWidth(realWidth);
        int contentHeight = getContentHeight(realHeight);

        Point point = position == null ? Point.ORIGIN_COORDINATE
                : position.calculate(posterWidth, posterHeight, realWidth, realHeight);
        Dimension dimension = Dimension.builder()
                .width(realWidth)
                .height(realHeight)
                .point(Point.of(point.getX(), point.getY()))
                .build();

        int contentStartX = point.getX() + padding.getMarginLeft();
        int contentStartY = point.getY() + padding.getMarginTop();

        layoutFloatChildren(context, contentWidth, contentHeight, contentStartX, contentStartY, dimension);
        return dimension;
    }

    /**
     * 布局浮动子元素
     *
     * @param context 上下文
     * @param contentWidth 内容区宽度
     * @param contentHeight 内容区高度
     * @param contentStartX 内容区起始 X
     * @param contentStartY 内容区起始 Y
     * @param dimension 容器尺寸（用于更新高度）
     */
    private void layoutFloatChildren(PosterContext context, int contentWidth, int contentHeight,
                                     int contentStartX, int contentStartY, Dimension dimension) {
        List<FloatRow> rows = new ArrayList<>();
        rows.add(new FloatRow(contentStartY, contentWidth));

        int currentY = contentStartY;
        int maxBottomY = 0;

        for (AbstractElement child : children) {
            Dimension childDimension = child.calculateDimension(context, contentWidth, contentHeight);
            Margin childMargin = getChildMargin(child);
            int childWidth = childDimension.getWidth() + childMargin.getMarginLeft() + childMargin.getMarginRight();
            int childHeight = childDimension.getHeight() + childMargin.getMarginTop() + childMargin.getMarginBottom();

            FloatType floatType = child.getFloatType();
            ClearType clearType = child.getClearType();

            FloatRow currentRow = findSuitableRow(rows, contentWidth, floatType, clearType, childWidth, currentY);

            int x, y;
            if (floatType == FloatType.LEFT) {
                x = contentStartX + childMargin.getMarginLeft() + currentRow.leftAvailable;
                y = currentRow.y + childMargin.getMarginTop();
                currentRow.leftAvailable += childWidth + gap;
            } else if (floatType == FloatType.RIGHT) {
                currentRow.rightAvailable -= childWidth + gap;
                x = contentStartX + currentRow.rightAvailable + childMargin.getMarginLeft();
                y = currentRow.y + childMargin.getMarginTop();
            } else {
                x = contentStartX + currentRow.leftAvailable + childMargin.getMarginLeft();
                y = currentRow.y + childMargin.getMarginTop();
            }

            childDimension.setPoint(Point.of(x, y));
            childDimensionMap.put(child, childDimension);

            int bottomY = y + childHeight;
            if (bottomY > maxBottomY) {
                maxBottomY = bottomY;
            }

            currentRow.height = Math.max(currentRow.height, childHeight);

            boolean rowFull = currentRow.rightAvailable - currentRow.leftAvailable <= gap;
            boolean isBlockElement = floatType == FloatType.NONE;
            if (rowFull || isBlockElement) {
                currentY = Math.max(currentY, currentRow.y + currentRow.height + gap);
                if (currentY < bottomY) {
                    currentY = bottomY;
                }
                rows.add(new FloatRow(currentY, contentWidth));
            }
        }

        if (height == 0) {
            dimension.setHeight(maxBottomY - contentStartY + padding.getMarginBottom());
        }
    }

    /**
     * 查找适合当前元素的行
     *
     * @param rows 行列表
     * @param contentWidth 内容区宽度
     * @param floatType 浮动类型
     * @param clearType 清除浮动类型
     * @param childWidth 子元素宽度
     * @param currentY 当前 Y 坐标
     * @return 合适的行
     */
    private FloatRow findSuitableRow(List<FloatRow> rows, int contentWidth,
                                     FloatType floatType, ClearType clearType,
                                     int childWidth, int currentY) {
        FloatRow lastRow = rows.get(rows.size() - 1);

        if (clearType == ClearType.LEFT) {
            currentY = Math.max(currentY, lastRow.y + lastRow.height + gap);
            FloatRow newRow = new FloatRow(currentY, contentWidth);
            newRow.leftAvailable = 0;
            newRow.rightAvailable = contentWidth;
            rows.add(newRow);
            return newRow;
        }

        if (clearType == ClearType.RIGHT) {
            currentY = Math.max(currentY, lastRow.y + lastRow.height + gap);
            FloatRow newRow = new FloatRow(currentY, contentWidth);
            newRow.leftAvailable = lastRow.leftAvailable;
            newRow.rightAvailable = contentWidth;
            rows.add(newRow);
            return newRow;
        }

        if (clearType == ClearType.BOTH) {
            currentY = Math.max(currentY, lastRow.y + lastRow.height + gap);
            FloatRow newRow = new FloatRow(currentY, contentWidth);
            rows.add(newRow);
            return newRow;
        }

        if (floatType == FloatType.NONE) {
            return lastRow;
        }

        if (floatType == FloatType.LEFT) {
            if (lastRow.leftAvailable + childWidth <= lastRow.rightAvailable) {
                return lastRow;
            }
        } else if (floatType == FloatType.RIGHT) {
            if (lastRow.rightAvailable - childWidth >= lastRow.leftAvailable) {
                return lastRow;
            }
        }

        currentY = Math.max(currentY, lastRow.y + lastRow.height + gap);
        FloatRow newRow = new FloatRow(currentY, contentWidth);
        rows.add(newRow);
        return newRow;
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
