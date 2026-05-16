package com.bytefuture.easy.poster.element.advance;

import com.bytefuture.easy.poster.element.AbstractElement;
import com.bytefuture.easy.poster.element.AbstractRepeatableElement;
import com.bytefuture.easy.poster.element.IElement;
import com.bytefuture.easy.poster.element.v2.TextElement;
import com.bytefuture.easy.poster.geometry.Dimension;
import com.bytefuture.easy.poster.geometry.Margin;
import com.bytefuture.easy.poster.geometry.Point;
import com.bytefuture.easy.poster.model.ClearType;
import com.bytefuture.easy.poster.model.FloatType;
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
     * 子元素可视区宽度缓存
     */
    private final Map<AbstractElement, Integer> childVisibleWidthMap = new LinkedHashMap<>();

    /**
     * 子元素可视区高度缓存
     */
    private final Map<AbstractElement, Integer> childVisibleHeightMap = new LinkedHashMap<>();

    /**
     * 子元素原始位置缓存（calculateDimension返回时的绝对坐标，用于计算嵌套容器偏移）
     */
    private final Map<AbstractElement, Point> childOriginalPointMap = new LinkedHashMap<>();

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
     * @param width  容器宽度
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
     * @param margin  子元素外边距
     * @return 当前容器
     */
    public ContainerElement addChild(AbstractElement element, Margin margin) {
        this.children.add(element);
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
     * 递归调整所有子元素坐标位置
     * 用于嵌套容器场景：父容器改变子容器的绝对坐标后，
     * 需要同步调整子容器内部所有元素的坐标偏移量
     *
     * @param deltaX X方向偏移量
     * @param deltaY Y方向偏移量
     */
    public void adjustChildPositions(int deltaX, int deltaY) {
        for (AbstractElement child : children) {
            Dimension childDim = childDimensionMap.get(child);
            if (childDim != null && childDim.getPoint() != null) {
                Point oldPoint = childDim.getPoint();
                childDim.setPoint(Point.of(oldPoint.getX() + deltaX, oldPoint.getY() + deltaY));
            }
            if (child instanceof ContainerElement) {
                ((ContainerElement) child).adjustChildPositions(deltaX, deltaY);
            }
        }
    }

    /**
     * 设置容器圆角
     *
     * @param arcWidth  圆角宽度
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
        childVisibleWidthMap.clear();
        childVisibleHeightMap.clear();
        childOriginalPointMap.clear();
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
            int childVisibleWidth = childVisibleWidthMap.getOrDefault(child, contentWidth);
            int childVisibleHeight = childVisibleHeightMap.getOrDefault(child, contentHeight);
            child.beforeRender(context);
            child.doRender(context, childDimension, childVisibleWidth, childVisibleHeight);
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
     * @param context      上下文
     * @param posterWidth  可用宽度
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

        int contentStartX = padding.getMarginLeft();
        int contentStartY = padding.getMarginTop();

        layoutFloatChildren(context, contentWidth, contentHeight, contentStartX, contentStartY, dimension);

        // 布局阶段使用相对坐标（相对于容器左上角），现在转换为绝对坐标
        Point containerPoint = dimension.getPoint();
        for (AbstractElement child : children) {
            Dimension childDim = childDimensionMap.get(child);
            Point relativePoint = childDim.getPoint();
            int absX = containerPoint.getX() + relativePoint.getX();
            int absY = containerPoint.getY() + relativePoint.getY();
            childDim.setPoint(Point.of(absX, absY));

            // 对嵌套容器子元素，递归调整其内部元素的坐标
            if (child instanceof ContainerElement) {
                ContainerElement childContainer = (ContainerElement) child;
                Point childOriginalPoint = childOriginalPointMap.get(child);
                int deltaX = absX - childOriginalPoint.getX();
                int deltaY = absY - childOriginalPoint.getY();
                if (deltaX != 0 || deltaY != 0) {
                    childContainer.adjustChildPositions(deltaX, deltaY);
                }
            }
        }

        return dimension;
    }

    /**
     * 布局浮动子元素
     *
     * @param context       上下文
     * @param contentWidth  内容区宽度
     * @param contentHeight 内容区高度
     * @param contentStartX 内容区起始 X
     * @param contentStartY 内容区起始 Y
     * @param dimension     容器尺寸（用于更新高度）
     */
    private void layoutFloatChildren(PosterContext context, int contentWidth, int contentHeight,
                                     int contentStartX, int contentStartY, Dimension dimension) {
        List<FloatRow> rows = new ArrayList<>();
        rows.add(new FloatRow(contentStartY, contentWidth));

        int currentY = contentStartY;
        int maxBottomY = 0;

        for (AbstractElement child : children) {
            FloatType floatType = child.getFloatType();
            ClearType clearType = child.getClearType();
            Margin childMargin = getChildMargin(child);

            // 先确定行，再计算可视区
            FloatRow currentRow = findSuitableRow(rows, contentWidth, floatType, clearType, currentY);

            // 计算子元素在当前行的可视区宽度
            int visibleWidth = contentWidth - currentRow.leftAvailable - (contentWidth - currentRow.rightAvailable);
            int visibleHeight = contentHeight;

            // 对 TextElement 子元素，如果未显式设置 maxTextWidth，临时用可视区宽度约束
            int originalMaxTextWidth = applyVisibleWidthConstraint(child, visibleWidth);
            Dimension childDimension = child.calculateDimension(context, visibleWidth, visibleHeight);
            restoreMaxTextWidth(child, originalMaxTextWidth);
            int childWidth = childDimension.getWidth() + childMargin.getMarginLeft() + childMargin.getMarginRight();
            int childHeight = childDimension.getHeight() + childMargin.getMarginTop() + childMargin.getMarginBottom();

            // 记录子元素原始位置（用于嵌套容器的坐标调整）
            childOriginalPointMap.put(child, Point.of(childDimension.getPoint().getX(), childDimension.getPoint().getY()));

            // 检查元素是否需要换到新行
            boolean needNewRow = false;
            if (floatType == FloatType.NONE) {
                // NONE/块级元素：当前行有浮动占位时，必须移到新行（块级元素不与浮动同行）
                if (currentRow.leftAvailable > 0 || currentRow.rightAvailable < contentWidth) {
                    needNewRow = true;
                }
            } else if (floatType == FloatType.LEFT) {
                // LEFT 浮动：左浮动区加上子元素宽度超过右边界时换行
                if (currentRow.leftAvailable + childWidth > currentRow.rightAvailable) {
                    needNewRow = true;
                }
            } else if (floatType == FloatType.RIGHT) {
                // RIGHT 浮动：右浮动区减去子元素宽度小于左边界时换行
                if (currentRow.rightAvailable - childWidth < currentRow.leftAvailable) {
                    needNewRow = true;
                }
            }

            if (needNewRow) {
                currentY = Math.max(currentY, currentRow.y + currentRow.height + gap);
                currentRow = new FloatRow(currentY, contentWidth);
                rows.add(currentRow);
                // 重新计算可视区
                visibleWidth = contentWidth;
                originalMaxTextWidth = applyVisibleWidthConstraint(child, visibleWidth);
                childDimension = child.calculateDimension(context, visibleWidth, visibleHeight);
                restoreMaxTextWidth(child, originalMaxTextWidth);
                childWidth = childDimension.getWidth() + childMargin.getMarginLeft() + childMargin.getMarginRight();
                childHeight = childDimension.getHeight() + childMargin.getMarginTop() + childMargin.getMarginBottom();
                childOriginalPointMap.put(child, Point.of(childDimension.getPoint().getX(), childDimension.getPoint().getY()));
            }

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

            // 子元素坐标暂存为相对坐标（相对于容器左上角）
            childDimension.setPoint(Point.of(x, y));
            childDimensionMap.put(child, childDimension);

            // 存储可视区宽高
            childVisibleWidthMap.put(child, visibleWidth);
            childVisibleHeightMap.put(child, visibleHeight);

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
     * 查找适合当前元素的行（不考虑子元素宽度，仅根据浮动类型和清除浮动类型选行）
     * 子元素宽度超出当前行的判断在 layoutFloatChildren 中处理
     *
     * @param rows         行列表
     * @param contentWidth 内容区宽度
     * @param floatType    浮动类型
     * @param clearType    清除浮动类型
     * @param currentY     当前 Y 坐标
     * @return 合适的行
     */
    private FloatRow findSuitableRow(List<FloatRow> rows, int contentWidth,
                                     FloatType floatType, ClearType clearType,
                                     int currentY) {
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

        // NONE/块级元素直接返回当前行（后续在 layoutFloatChildren 中判断是否需要换行）
        if (floatType == FloatType.NONE) {
            return lastRow;
        }

        // LEFT/RIGHT 浮动元素也返回当前行（后续在 layoutFloatChildren 中判断是否需要换行）
        return lastRow;
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
     * 对 TextElement 子元素应用可视区宽度约束
     * 如果子元素未显式设置 maxTextWidth，临时用可视区宽度约束文本自动换行
     *
     * @param child       子元素
     * @param visibleWidth 可视区宽度
     * @return 原始 maxTextWidth 值，用于恢复
     */
    private int applyVisibleWidthConstraint(AbstractElement child, int visibleWidth) {
        if (child instanceof TextElement) {
            TextElement textElement = (TextElement) child;
            int original = textElement.getBlockStyle().getMaxTextWidth();
            if (original <= 0 && visibleWidth > 0) {
                textElement.getBlockStyle().maxTextWidth(visibleWidth);
            }
            return original;
        }
        return 0;
    }

    /**
     * 恢复 TextElement 子元素的原始 maxTextWidth 值
     *
     * @param child              子元素
     * @param originalMaxTextWidth 原始 maxTextWidth 值
     */
    private void restoreMaxTextWidth(AbstractElement child, int originalMaxTextWidth) {
        if (child instanceof TextElement && originalMaxTextWidth <= 0) {
            ((TextElement) child).getBlockStyle().resetMaxTextWidth();
        }
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
     * @param graphics    画笔
     * @param point       容器左上角坐标
     * @param outerWidth  容器总宽度
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
     * @param graphics    画笔
     * @param point       容器左上角坐标
     * @param outerWidth  容器总宽度
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
     * @param point       容器左上角坐标
     * @param outerWidth  容器总宽度
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
     * @param point       容器左上角坐标
     * @param outerWidth  容器总宽度
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
     * @param point       容器左上角坐标
     * @param outerWidth  容器总宽度
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
