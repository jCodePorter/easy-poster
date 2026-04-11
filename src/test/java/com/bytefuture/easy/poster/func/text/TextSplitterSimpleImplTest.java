package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.text.split.SplitTextInfo;
import com.bytefuture.easy.poster.text.split.TextSplitRequest;
import com.bytefuture.easy.poster.text.split.TextSplitResult;
import com.bytefuture.easy.poster.text.split.TextSplitterSimpleImpl;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public class TextSplitterSimpleImplTest {

    @Test
    public void shouldKeepEnglishWordsUnbrokenInMixedText() {
        FontMetrics fontMetrics = createFontMetrics();
        TextSplitterSimpleImpl splitter = new TextSplitterSimpleImpl();
        String text = "中文 mixed text 换行 example";
        int maxWidth = fontMetrics.stringWidth("中文 mixed");

        TextSplitResult result = splitter.split(TextSplitRequest.of(text, maxWidth, fontMetrics));
        List<String> lines = result.getLines().stream().map(SplitTextInfo::getText).collect(Collectors.toList());

        Assert.assertEquals("中文 mixed", lines.get(0));
        Assert.assertEquals("text 换行", lines.get(1));
        Assert.assertEquals("example", lines.get(2));
    }

    @Test
    public void shouldPreserveExplicitNewLine() {
        FontMetrics fontMetrics = createFontMetrics();
        TextSplitterSimpleImpl splitter = new TextSplitterSimpleImpl();
        String text = "hello\n\nworld";

        TextSplitResult result = splitter.split(TextSplitRequest.of(text, 200, fontMetrics));
        List<String> lines = result.getLines().stream().map(SplitTextInfo::getText).collect(Collectors.toList());

        Assert.assertEquals(3, lines.size());
        Assert.assertEquals("hello", lines.get(0));
        Assert.assertEquals("", lines.get(1));
        Assert.assertEquals("world", lines.get(2));
    }

    @Test
    public void shouldFallbackToCharacterSplitForOversizedWordLikeToken() {
        FontMetrics fontMetrics = createFontMetrics();
        TextSplitterSimpleImpl splitter = new TextSplitterSimpleImpl();
        String text = "Visit https://example.com/path/to/resource?id=12345 now";
        int maxWidth = fontMetrics.stringWidth("https://example");

        TextSplitResult result = splitter.split(TextSplitRequest.of(text, maxWidth, fontMetrics));
        List<String> lines = result.getLines().stream().map(SplitTextInfo::getText).collect(Collectors.toList());

        Assert.assertTrue(lines.size() >= 3);
        Assert.assertEquals("Visit", lines.get(0));
        Assert.assertTrue(lines.get(1).startsWith("https://"));
        Assert.assertEquals("now", lines.get(lines.size() - 1));
    }

    private FontMetrics createFontMetrics() {
        BufferedImage image = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(new Font("Dialog", Font.PLAIN, 18));
        return graphics.getFontMetrics();
    }
}
