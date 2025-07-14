package com.augrain.easy.poster.special;

import com.augrain.easy.poster.EasyPoster;
import com.augrain.easy.poster.element.advance.ComposeElement;
import com.augrain.easy.poster.element.basic.RectangleElement;
import com.augrain.easy.poster.element.basic.TextElement;
import com.augrain.easy.poster.element.special.QrCodeElement;
import com.augrain.easy.poster.geometry.Margin;
import com.augrain.easy.poster.geometry.Direction;
import com.augrain.easy.poster.geometry.RelativePosition;
import com.augrain.easy.poster.model.BaseLine;
import org.junit.Test;

/**
 * 二维码测试
 *
 * @author biaoy
 * @since 2025/05/23
 */
public class QrCodeElementTest {

    @Test
    public void testBasic() {
        EasyPoster poster = new EasyPoster(500, 500);

        QrCodeElement qrcodeElement = new QrCodeElement("这是一段二维码测试文本", 200, 200);
        qrcodeElement.setPosition(RelativePosition.of(Direction.CENTER));
        poster.addElement(qrcodeElement);

        poster.asFile("png", "qrcode.png");
    }

    @Test
    public void testCompose() {
        EasyPoster poster = new EasyPoster(350, 350);

        ComposeElement composeElement = ComposeElement.of(
                        new QrCodeElement("联系作者jcodeporter@gmail.com", 250, 250)
                                .setPosition(RelativePosition.of(Direction.CENTER)))
                .bottom(new TextElement("测试门店").setFontSize(35).setFontName("仿宋")
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of(5))))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.addElement(composeElement);
        poster.asFile("png", "qrcode_compose.png");
    }

    @Test
    public void testCompose2() {
        EasyPoster poster = new EasyPoster(350, 350);

        ComposeElement composeElement = ComposeElement.of(
                        new QrCodeElement("联系作者jcodeporter@gmail.com", 250, 250)
                                .setPosition(RelativePosition.of(Direction.CENTER)))
                .bottom(new RectangleElement(100, 20)
                        .setPosition(RelativePosition.of(Direction.TOP_CENTER, Margin.of(5))))
                .setPosition(RelativePosition.of(Direction.CENTER));

        poster.addElement(composeElement);
        poster.asFile("png", "qrcode_compose.png");
    }
}
