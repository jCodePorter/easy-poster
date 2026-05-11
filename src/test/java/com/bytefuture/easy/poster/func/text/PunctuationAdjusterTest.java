package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.v2.text.layout.CharCell;
import com.bytefuture.easy.poster.element.v2.text.layout.PunctuationAdjuster;
import com.bytefuture.easy.poster.element.v2.text.layout.TextColumn;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import com.bytefuture.easy.poster.model.PunctuationType;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PunctuationAdjusterTest {

    private final PunctuationAdjuster adjuster = new PunctuationAdjuster();

    private ResolvedTextStyle createStyle() {
        return new ResolvedTextStyle(new Font("Dialog", Font.PLAIN, 18), Color.BLACK, false, false, false, 0, null, 0, 0);
    }

    private CharCell makeChar(String ch, int offsetY, int height) {
        return new CharCell(ch, createStyle(), offsetY, 18, height);
    }

    @Test
    public void shouldSqueezeAvoidHeadPunctuationBackToPreviousColumn() {
        TextColumn col0 = new TextColumn("春眠不觉", 18, 80, 0,
                Arrays.asList(
                        makeChar("春", 0, 20),
                        makeChar("眠", 20, 20),
                        makeChar("不", 40, 20),
                        makeChar("觉", 60, 20)));

        TextColumn col1 = new TextColumn("。晓处处", 18, 80, 0,
                Arrays.asList(
                        makeChar("。", 0, 20),
                        makeChar("晓", 20, 20),
                        makeChar("处", 40, 20),
                        makeChar("处", 60, 20)));

        TextColumn col2 = new TextColumn("鸟", 18, 20, 0,
                Collections.singletonList(makeChar("鸟", 0, 20)));

        List<TextColumn> columns = new ArrayList<>(Arrays.asList(col0, col1, col2));
        List<TextColumn> adjusted = adjuster.adjust(columns);

        // 列0 应追加 squeezed "。"
        Assert.assertEquals(5, adjusted.get(0).getCharacters().size());
        CharCell squeezed = adjusted.get(0).getCharacters().get(4);
        Assert.assertEquals("。", squeezed.getCharacter());
        Assert.assertTrue(squeezed.isSqueezed());
        // offsetY 应为列0最后一非squeezed字符 "觉" 的 offsetY
        // rebuildAll 重算后 "觉" offsetY=60，squeezed "。" offsetY=60
        Assert.assertEquals(60, squeezed.getOffsetY());

        // 列1 的 "。" 已移除，剩余字符 offsetY 从0重算
        Assert.assertEquals(3, adjusted.get(1).getCharacters().size());
        Assert.assertEquals("晓处处", adjusted.get(1).getText());
        Assert.assertEquals(0, adjusted.get(1).getCharacters().get(0).getOffsetY());
        Assert.assertEquals(20, adjusted.get(1).getCharacters().get(1).getOffsetY());
        Assert.assertEquals(40, adjusted.get(1).getCharacters().get(2).getOffsetY());

        // 列0 高度不变（squeezed 不计入）
        Assert.assertEquals(80, adjusted.get(0).getHeight());
    }

    @Test
    public void shouldSqueezeAvoidTailPunctuationToNextColumn() {
        TextColumn col0 = new TextColumn("春眠不觉，", 18, 100, 0,
                Arrays.asList(
                        makeChar("春", 0, 20),
                        makeChar("眠", 20, 20),
                        makeChar("不", 40, 20),
                        makeChar("觉", 60, 20),
                        makeChar("，", 80, 20)));

        TextColumn col1 = new TextColumn("晓处处闻", 18, 80, 0,
                Arrays.asList(
                        makeChar("晓", 0, 20),
                        makeChar("处", 20, 20),
                        makeChar("处", 40, 20),
                        makeChar("闻", 60, 20)));

        List<TextColumn> columns = new ArrayList<>(Arrays.asList(col0, col1));
        List<TextColumn> adjusted = adjuster.adjust(columns);

        // 列0 的 "，" 已移除
        Assert.assertEquals(4, adjusted.get(0).getCharacters().size());
        Assert.assertEquals("春眠不觉", adjusted.get(0).getText());

        // 列1 应 prepend squeezed "，"
        Assert.assertEquals(5, adjusted.get(1).getCharacters().size());
        CharCell squeezed = adjusted.get(1).getCharacters().get(0);
        Assert.assertEquals("，", squeezed.getCharacter());
        Assert.assertTrue(squeezed.isSqueezed());
        Assert.assertEquals(0, squeezed.getOffsetY());
    }

    @Test
    public void shouldNotSqueezeAvoidHeadPunctuationInFirstColumn() {
        TextColumn col0 = new TextColumn("。春眠", 18, 60, 0,
                Arrays.asList(
                        makeChar("。", 0, 20),
                        makeChar("春", 20, 20),
                        makeChar("眠", 40, 20)));

        List<TextColumn> columns = new ArrayList<>(Collections.singletonList(col0));
        List<TextColumn> adjusted = adjuster.adjust(columns);

        Assert.assertEquals(3, adjusted.get(0).getCharacters().size());
        Assert.assertFalse(adjusted.get(0).getCharacters().get(0).isSqueezed());
    }

    @Test
    public void shouldNotSqueezeAvoidTailPunctuationInLastColumn() {
        TextColumn col0 = new TextColumn("春眠不觉，", 18, 100, 0,
                Arrays.asList(
                        makeChar("春", 0, 20),
                        makeChar("眠", 20, 20),
                        makeChar("不", 40, 20),
                        makeChar("觉", 60, 20),
                        makeChar("，", 80, 20)));

        List<TextColumn> columns = new ArrayList<>(Collections.singletonList(col0));
        List<TextColumn> adjusted = adjuster.adjust(columns);

        Assert.assertEquals(5, adjusted.get(0).getCharacters().size());
        Assert.assertFalse(adjusted.get(0).getCharacters().get(4).isSqueezed());
    }

    @Test
    public void shouldSqueezeCloseBracketBackToPreviousColumn() {
        TextColumn col0 = new TextColumn("觉", 18, 20, 0,
                Collections.singletonList(makeChar("觉", 0, 20)));

        TextColumn col1 = new TextColumn("）晓", 18, 40, 0,
                Arrays.asList(
                        makeChar("）", 0, 20),
                        makeChar("晓", 20, 20)));

        List<TextColumn> columns = new ArrayList<>(Arrays.asList(col0, col1));
        List<TextColumn> adjusted = adjuster.adjust(columns);

        // ）应挤回列0末尾
        Assert.assertEquals(2, adjusted.get(0).getCharacters().size());
        Assert.assertTrue(adjusted.get(0).getCharacters().get(1).isSqueezed());
        Assert.assertEquals(1, adjusted.get(1).getCharacters().size());
    }

    @Test
    public void shouldSqueezeOpenBracketToNextColumn() {
        TextColumn col0 = new TextColumn("觉（", 18, 40, 0,
                Arrays.asList(
                        makeChar("觉", 0, 20),
                        makeChar("（", 20, 20)));

        TextColumn col1 = new TextColumn("晓", 18, 20, 0,
                Collections.singletonList(makeChar("晓", 0, 20)));

        List<TextColumn> columns = new ArrayList<>(Arrays.asList(col0, col1));
        List<TextColumn> adjusted = adjuster.adjust(columns);

        // （应挤到列1开头
        Assert.assertEquals(1, adjusted.get(0).getCharacters().size());
        Assert.assertEquals(2, adjusted.get(1).getCharacters().size());
        Assert.assertTrue(adjusted.get(1).getCharacters().get(0).isSqueezed());
    }
}