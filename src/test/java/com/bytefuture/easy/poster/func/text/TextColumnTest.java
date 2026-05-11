package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.v2.text.layout.CharCell;
import com.bytefuture.easy.poster.element.v2.text.layout.TextColumn;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class TextColumnTest {

    private ResolvedTextStyle createStyle() {
        return new ResolvedTextStyle(new Font("Dialog", Font.PLAIN, 18), Color.BLACK, false, false, false, 0, null, 0, 0);
    }

    @Test
    public void shouldRebuildColumnWithNewCharacters() {
        CharCell cell1 = new CharCell("春", createStyle(), 0, 18, 20);
        CharCell cell2 = new CharCell("眠", createStyle(), 20, 18, 20);
        TextColumn original = new TextColumn("春眠", 18, 40, 0, Arrays.asList(cell1, cell2));

        CharCell cell3 = new CharCell("晓", createStyle(), 40, 18, 20);
        TextColumn rebuilt = original.rebuild(Arrays.asList(cell1, cell2, cell3));

        Assert.assertEquals("春眠晓", rebuilt.getText());
        Assert.assertEquals(60, rebuilt.getHeight());
        Assert.assertEquals(18, rebuilt.getWidth());
    }

    @Test
    public void shouldExcludeSqueezedCharFromHeightWhenRebuild() {
        CharCell cell1 = new CharCell("觉", createStyle(), 0, 18, 20);
        CharCell squeezedCell = new CharCell("。", createStyle(), 0, 9, 18, true, false, false);
        TextColumn original = new TextColumn("觉", 18, 20, 0, Collections.singletonList(cell1));

        TextColumn rebuilt = original.rebuild(Arrays.asList(cell1, squeezedCell));

        Assert.assertEquals("觉。", rebuilt.getText());
        Assert.assertEquals(20, rebuilt.getHeight());
    }

    @Test
    public void shouldRebuildEmptyColumn() {
        TextColumn original = new TextColumn("春", 18, 20, 0,
                Collections.singletonList(new CharCell("春", createStyle(), 0, 18, 20)));

        TextColumn rebuilt = original.rebuild(Collections.emptyList());

        Assert.assertEquals("", rebuilt.getText());
        Assert.assertEquals(0, rebuilt.getHeight());
    }
}