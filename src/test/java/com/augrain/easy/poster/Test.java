package com.augrain.easy.poster;

import com.augrain.easy.poster.element.advance.ComposeElement;
import com.augrain.easy.poster.element.basic.TextElement;
import com.augrain.easy.poster.element.special.QrCodeElement;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.Margin;
import com.augrain.easy.poster.geometry.RelativePosition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author biaoy
 * @since 2025/07/13
 */
public class Test {

    @org.junit.Test
    public void t1() throws IOException {
        InputStream inputStream = Test.class.getClassLoader().getResourceAsStream("invite_code_bg.jpg");
        BufferedImage bgImg = ImageIO.read(inputStream);

        EasyPoster poster = new EasyPoster(bgImg);

        String qrCodeMsg = "https://cs.juhuoxc.com/company/index?companyId=1932261242312810498&bizType=invite&companyName=河南三易途信息技术有限公司";
        String textMsg = "河南三易途信息技术有限公司技术有限公司";

        ComposeElement composeElement = ComposeElement.of(
                        new QrCodeElement(qrCodeMsg, 500, 500)
                                .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of(0, 635))))
                .bottom(new TextElement(textMsg).setFontSize(36).setFontName("微软雅黑")
                        .setAutoWrapText(600)
                        .setFontColor(new Color(Integer.parseInt("56", 16), Integer.parseInt("6C", 16), Integer.parseInt("91", 16)))
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of(35))));

        poster.addElement(composeElement);
        poster.asFile("png", "out_juhuo.png");
    }
}
