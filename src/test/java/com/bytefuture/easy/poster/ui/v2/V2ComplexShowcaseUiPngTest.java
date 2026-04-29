package com.bytefuture.easy.poster.ui.v2;

import com.bytefuture.easy.poster.func.showcase.ComplexShowcaseFixtures;
import org.junit.Test;

public class V2ComplexShowcaseUiPngTest {

    @Test
    public void shouldOutputComplexEventInviteShowcasePng() throws Exception {
        ComplexShowcaseFixtures.writeEventInviteShowcase("out_v2_complex_event_invite_showcase.png");
    }

    @Test
    public void shouldOutputComplexDashboardShowcasePng() throws Exception {
        ComplexShowcaseFixtures.writeDashboardShowcase("out_v2_complex_dashboard_showcase.png");
    }
}
