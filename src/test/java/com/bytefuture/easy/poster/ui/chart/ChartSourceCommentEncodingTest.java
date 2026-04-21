package com.bytefuture.easy.poster.ui.chart;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 校验 chart 包源码注释中不再包含典型乱码片段。
 */
public class ChartSourceCommentEncodingTest {

    private static final List<String> CHART_FILES = Arrays.asList(
            "src/main/java/com/bytefuture/easy/poster/element/chart/BarChartElement.java",
            "src/main/java/com/bytefuture/easy/poster/element/chart/BarChartSeries.java",
            "src/main/java/com/bytefuture/easy/poster/element/chart/FunnelChartElement.java",
            "src/main/java/com/bytefuture/easy/poster/element/chart/LineChartSeries.java",
            "src/main/java/com/bytefuture/easy/poster/element/chart/PieChartElement.java"
    );

    private static final List<String> MOJIBAKE_MARKERS = Arrays.asList(
            "闂", "閸", "闁", "缂", "椤", "婵", "閺", "閹", "鏆"
    );

    @Test
    public void testChartSourceCommentsShouldNotContainKnownMojibakeMarkers() throws IOException {
        for (String path : CHART_FILES) {
            String source = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            for (String marker : MOJIBAKE_MARKERS) {
                Assert.assertFalse("Unexpected mojibake marker '" + marker + "' in " + path, source.contains(marker));
            }
        }
    }
}
