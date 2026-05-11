package com.bytefuture.easy.poster.func.text;

import com.bytefuture.easy.poster.element.v2.text.layout.CharCell;
import com.bytefuture.easy.poster.element.v2.text.style.ResolvedTextStyle;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class CharCellTest {

    private ResolvedTextStyle createStyle() {
        return new ResolvedTextStyle(new Font("Dialog", Font.PLAIN, 18), Color.BLACK, false, false, false, 0, null, 0, 0);
    }

    @Test
    public void shouldCreateCharCellWithDefaultPunctuationFlags() {
        CharCell cell = new CharCell("春", createStyle(), 0, 18, 20);
        Assert.assertFalse(cell.isSqueezed());
        Assert.assertFalse(cell.isCompressed());
        Assert.assertFalse(cell.isEllipsisUnit());
    }

    @Test
    public void shouldCreateCharCellWithSqueezedFlag() {
        CharCell cell = new CharCell("。", createStyle(), 10, 9, 18, true, false, false);
        Assert.assertTrue(cell.isSqueezed());
        Assert.assertFalse(cell.isCompressed());
        Assert.assertFalse(cell.isEllipsisUnit());
    }

    @Test
    public void shouldCreateCharCellWithEllipsisUnitFlag() {
        CharCell cell = new CharCell("……", createStyle(), 0, 18, 36, false, false, true);
        Assert.assertTrue(cell.isEllipsisUnit());
        Assert.assertEquals(36, cell.getHeight());
    }

    @Test
    public void shouldCreateWithSqueezedViaFactory() {
        CharCell original = new CharCell("。", createStyle(), 10, 9, 18);
        CharCell squeezed = original.withSqueezed(true);
        Assert.assertTrue(squeezed.isSqueezed());
        Assert.assertEquals(original.getCharacter(), squeezed.getCharacter());
        Assert.assertEquals(original.getOffsetY(), squeezed.getOffsetY());
    }

    @Test
    public void shouldCreateWithCompressedViaFactory() {
        CharCell original = new CharCell("……", createStyle(), 0, 18, 36, false, false, true);
        CharCell compressed = original.withCompressed(true);
        Assert.assertTrue(compressed.isCompressed());
        Assert.assertTrue(compressed.isEllipsisUnit());
        Assert.assertEquals(18, compressed.getHeight());
    }
}