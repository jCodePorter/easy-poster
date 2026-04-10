package com.bytefuture.easy.poster.verify;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class BarChartGenerator {

    // 柱状图配置
    public static class BarChartConfig {
        private int width = 800;
        private int height = 600;
        private Color backgroundColor = Color.WHITE;
        private Color axisColor = Color.BLACK;
        private Color gridColor = new Color(200, 200, 200, 150);
        private Color titleColor = Color.BLACK;
        private Color labelColor = Color.BLACK;
        private String titleFont = "微软雅黑";
        private String labelFont = "微软雅黑";
        private int titleFontSize = 24;
        private int labelFontSize = 14;
        private int padding = 80;
        private boolean showGrid = true;
        private boolean showValues = true;
        private boolean showLegend = true;

        // 设置方法
        public BarChartConfig setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public BarChartConfig setBackgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }

        // ... 其他setter方法
    }

    // 数据条目
    public static class BarData {
        private String label;
        private double value;
        private Color color;

        public BarData(String label, double value) {
            this.label = label;
            this.value = value;
            this.color = null; // 使用默认颜色
        }

        public BarData(String label, double value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        // getter方法
        public String getLabel() {
            return label;
        }

        public double getValue() {
            return value;
        }

        public Color getColor() {
            return color;
        }
    }

    /**
     * 生成柱状图图片
     *
     * @param dataList 数据列表
     * @param title    图表标题
     * @param config   配置对象
     * @return BufferedImage 生成的图片
     */
    public static BufferedImage generateBarChart(List<BarData> dataList, String title, BarChartConfig config) {
        if (config == null) {
            config = new BarChartConfig();
        }

        // 创建图片
        BufferedImage image = new BufferedImage(
                config.width, config.height, BufferedImage.TYPE_INT_ARGB);

        // 获取Graphics2D对象
        Graphics2D g2d = image.createGraphics();

        try {
            // 启用抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 绘制背景
            g2d.setColor(config.backgroundColor);
            g2d.fillRect(0, 0, config.width, config.height);

            // 绘制标题
            drawTitle(g2d, title, config);

            // 绘制坐标轴和网格
            drawAxesAndGrid(g2d, dataList, config);

            // 绘制柱状图
            drawBars(g2d, dataList, config);

            // 绘制图例
            if (config.showLegend) {
                drawLegend(g2d, dataList, config);
            }

        } finally {
            g2d.dispose();
        }

        return image;
    }

    /**
     * 简化方法 - 生成柱状图
     */
    public static BufferedImage generateSimpleBarChart(Map<String, Double> data, String title) {
        List<BarData> dataList = new ArrayList<>();
        int index = 0;
        Color[] defaultColors = getDefaultColors();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            Color color = defaultColors[index % defaultColors.length];
            dataList.add(new BarData(entry.getKey(), entry.getValue(), color));
            index++;
        }

        BarChartConfig config = new BarChartConfig();
        return generateBarChart(dataList, title, config);
    }

    private static void drawTitle(Graphics2D g2d, String title, BarChartConfig config) {
        g2d.setFont(new Font(config.titleFont, Font.BOLD, config.titleFontSize));
        g2d.setColor(config.titleColor);
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = (config.width - titleWidth) / 2;
        g2d.drawString(title, x, 50);
    }

    private static void drawAxesAndGrid(Graphics2D g2d, List<BarData> dataList, BarChartConfig config) {
        int padding = config.padding;
        int chartWidth = config.width - padding * 2;
        int chartHeight = config.height - padding * 2 - 50; // 减去标题区域

        // 绘制坐标轴
        g2d.setColor(config.axisColor);
        g2d.setStroke(new BasicStroke(2));

        // Y轴
        g2d.drawLine(padding, padding, padding, padding + chartHeight);
        // X轴
        g2d.drawLine(padding, padding + chartHeight,
                padding + chartWidth, padding + chartHeight);

        if (config.showGrid) {
            drawGrid(g2d, dataList, config, padding, chartWidth, chartHeight);
        }
    }

    private static void drawGrid(Graphics2D g2d, List<BarData> dataList,
                                 BarChartConfig config, int padding, int chartWidth, int chartHeight) {
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(config.gridColor);

        double maxValue = getMaxValue(dataList);
        int yGridLines = 10;

        for (int i = 0; i <= yGridLines; i++) {
            int y = padding + chartHeight - (i * chartHeight / yGridLines);
            g2d.drawLine(padding, y, padding + chartWidth, y);

            // Y轴刻度标签
            double value = (i * maxValue / yGridLines);
            String label = formatValue(value);
            g2d.setColor(config.labelColor);
            g2d.setFont(new Font(config.labelFont, Font.PLAIN, config.labelFontSize - 2));
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, padding - labelWidth - 5, y + (fm.getHeight() / 3));
            g2d.setColor(config.gridColor);
        }
    }

    private static void drawBars(Graphics2D g2d, List<BarData> dataList, BarChartConfig config) {
        int padding = config.padding;
        int chartWidth = config.width - padding * 2;
        int chartHeight = config.height - padding * 2 - 50;
        int barCount = dataList.size();
        double maxValue = getMaxValue(dataList);

        // 计算每个柱子的宽度和间距
        int barWidth = Math.min((chartWidth - (barCount - 1) * 10) / barCount, 80);
        Color[] defaultColors = getDefaultColors();

        for (int i = 0; i < barCount; i++) {
            BarData data = dataList.get(i);

            // 计算柱子的位置和高度
            int x = padding + i * (barWidth + 10);
            int barHeight = (int) ((data.getValue() / maxValue) * chartHeight);
            int y = padding + chartHeight - barHeight;

            // 确定颜色
            Color barColor = data.getColor() != null ? data.getColor() : defaultColors[i % defaultColors.length];

            // 绘制柱子
            g2d.setColor(barColor);
            g2d.fillRect(x, y, barWidth, barHeight);

            // 绘制边框
            g2d.setColor(barColor.darker());
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(x, y, barWidth, barHeight);

            // 绘制数值标签
            if (config.showValues && barHeight > 20) {
                drawValueLabel(g2d, data.getValue(), x, y, barWidth, barColor, config);
            }

            // 绘制X轴标签
            drawXAxisLabel(g2d, data.getLabel(), x, y, barWidth, chartHeight, padding, config);
        }
    }

    private static void drawValueLabel(Graphics2D g2d, double value, int x, int y,
                                       int barWidth, Color barColor, BarChartConfig config) {
        g2d.setColor(getContrastColor(barColor));
        g2d.setFont(new Font(config.labelFont, Font.BOLD, config.labelFontSize));
        String valueLabel = formatValue(value);
        FontMetrics fm = g2d.getFontMetrics();
        int valueLabelWidth = fm.stringWidth(valueLabel);
        g2d.drawString(valueLabel,
                x + (barWidth - valueLabelWidth) / 2,
                y - 5);
    }

    private static void drawXAxisLabel(Graphics2D g2d, String label, int x, int y,
                                       int barWidth, int chartHeight, int padding, BarChartConfig config) {
        g2d.setColor(config.labelColor);
        g2d.setFont(new Font(config.labelFont, Font.PLAIN, config.labelFontSize));
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);

        // 处理长标签：截断或换行
        if (labelWidth > barWidth) {
            label = truncateLabel(label, fm, barWidth - 20);
            labelWidth = fm.stringWidth(label);
        }

        g2d.drawString(label,
                x + (barWidth - labelWidth) / 2,
                padding + chartHeight + 25);
    }

    private static void drawLegend(Graphics2D g2d, List<BarData> dataList, BarChartConfig config) {
        int legendX = config.width - 200;
        int legendY = 100;
        int boxSize = 15;

        g2d.setFont(new Font(config.labelFont, Font.BOLD, config.labelFontSize));
        g2d.setColor(config.labelColor);
        g2d.drawString("图例", legendX, legendY - 10);

        g2d.setFont(new Font(config.labelFont, Font.PLAIN, config.labelFontSize - 2));
        Color[] defaultColors = getDefaultColors();

        for (int i = 0; i < dataList.size(); i++) {
            BarData data = dataList.get(i);
            int y = legendY + i * 25;

            // 绘制颜色方块
            Color color = data.getColor() != null ? data.getColor() : defaultColors[i % defaultColors.length];
            g2d.setColor(color);
            g2d.fillRect(legendX, y, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, y, boxSize, boxSize);

            // 绘制标签
            g2d.setColor(config.labelColor);
            String displayLabel = data.getLabel();
            FontMetrics fm = g2d.getFontMetrics();
            if (fm.stringWidth(displayLabel) > 150) {
                displayLabel = truncateLabel(displayLabel, fm, 150);
            }
            g2d.drawString(displayLabel, legendX + boxSize + 10, y + boxSize - 3);
        }
    }

    private static double getMaxValue(List<BarData> dataList) {
        return dataList.stream()
                .mapToDouble(BarData::getValue)
                .max()
                .orElse(1.0);
    }

    private static Color[] getDefaultColors() {
        return new Color[]{
                new Color(70, 130, 180),    // 钢蓝色
                new Color(34, 139, 34),     // 森林绿
                new Color(220, 20, 60),     // 深红色
                new Color(255, 140, 0),     // 深橙色
                new Color(138, 43, 226),    // 蓝紫色
                new Color(255, 215, 0),     // 金色
                new Color(50, 205, 50)      // 酸橙色
        };
    }

    private static String formatValue(double value) {
        if (value % 1 == 0) {
            return String.format("%.0f", value);
        } else if (value < 10) {
            return String.format("%.2f", value);
        } else {
            return String.format("%.1f", value);
        }
    }

    private static String truncateLabel(String label, FontMetrics fm, int maxWidth) {
        if (fm.stringWidth(label) <= maxWidth) {
            return label;
        }

        String truncated = label + "...";
        while (fm.stringWidth(truncated) > maxWidth && truncated.length() > 3) {
            truncated = label.substring(0, truncated.length() - 4) + "...";
        }
        return truncated;
    }

    private static Color getContrastColor(Color color) {
        // 计算颜色亮度，选择对比度高的文字颜色
        double luminance = (0.299 * color.getRed() +
                0.587 * color.getGreen() +
                0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    /**
     * 保存图片到文件
     */
    public static void saveImage(BufferedImage image, String filePath, String format) throws IOException {
        File output = new File(filePath);
        ImageIO.write(image, format, output);
    }

    /**
     * 示例用法
     */
    public static void main(String[] args) throws IOException {
        // 方式1：使用Map数据
        Map<String, Double> dataMap = new LinkedHashMap<>();
        dataMap.put("一月", 120.5);
        dataMap.put("二月", 180.2);
        dataMap.put("三月", 210.8);
        dataMap.put("四月", 150.3);
        dataMap.put("五月", 280.7);
        dataMap.put("六月", 280.7);
        dataMap.put("七月", 280.7);
        dataMap.put("八月", 280.7);
        dataMap.put("九月", 280.7);
        dataMap.put("十月", 280.7);
        dataMap.put("十一月", 280.7);

        BufferedImage image1 = generateSimpleBarChart(dataMap, "2023年销售数据");
        saveImage(image1, "bar_chart_simple.png", "PNG");

        // 方式2：使用自定义配置
        List<BarData> dataList = Arrays.asList(
                new BarData("产品A", 320.5, new Color(65, 105, 225)),
                new BarData("产品B", 280.3, new Color(34, 139, 34)),
                new BarData("产品C", 410.2, new Color(220, 20, 60)),
                new BarData("产品D", 190.7, new Color(255, 140, 0))
        );

        BarChartConfig config = new BarChartConfig()
                .setSize(1000, 700)
                .setBackgroundColor(new Color(245, 245, 245));

        BufferedImage image2 = generateBarChart(dataList, "产品销量统计", config);
        saveImage(image2, "bar_chart_custom.png", "PNG");

        System.out.println("柱状图已生成保存到当前目录");
    }
}