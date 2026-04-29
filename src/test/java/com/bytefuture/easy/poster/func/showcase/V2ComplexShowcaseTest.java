package com.bytefuture.easy.poster.func.showcase;

import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class V2ComplexShowcaseTest {

    @Test
    public void shouldRenderEventInviteShowcase() throws Exception {
        BufferedImage image = ComplexShowcaseFixtures.renderEventInviteShowcase();

        Assert.assertTrue(ComplexShowcaseFixtures.countColorLikePixels(image, ComplexShowcaseFixtures.EVENT_ACCENT, 16) > 4000);
        Assert.assertTrue(ComplexShowcaseFixtures.countColorLikePixels(image, ComplexShowcaseFixtures.EVENT_SECONDARY, 18) > 1200);
        Assert.assertTrue(ComplexShowcaseFixtures.countColorLikePixels(image, Color.BLACK, 12) > 800);
    }

    @Test
    public void shouldRenderDashboardShowcase() throws Exception {
        BufferedImage image = ComplexShowcaseFixtures.renderDashboardShowcase();

        Assert.assertTrue(ComplexShowcaseFixtures.countColorLikePixels(image, ComplexShowcaseFixtures.DASHBOARD_PRIMARY, 20) > 4000);
        Assert.assertTrue(ComplexShowcaseFixtures.countColorLikePixels(image, ComplexShowcaseFixtures.DASHBOARD_SECONDARY, 20) > 1400);
        Assert.assertTrue(ComplexShowcaseFixtures.countColorLikePixels(image, Color.WHITE, 6) > 90000);
    }
}
