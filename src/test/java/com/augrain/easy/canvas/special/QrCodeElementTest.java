package com.augrain.easy.canvas.special;

import com.augrain.easy.canvas.EasyCanvas;
import com.augrain.easy.canvas.element.advance.ComposeElement;
import com.augrain.easy.canvas.element.basic.TextElement;
import com.augrain.easy.canvas.element.special.QrCodeElement;
import com.augrain.easy.canvas.geometry.Margin;
import com.augrain.easy.canvas.geometry.PositionDirection;
import com.augrain.easy.canvas.geometry.RelativePosition;
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
        EasyCanvas canvas = new EasyCanvas(500, 500);

        QrCodeElement qrcodeElement = new QrCodeElement("这是一段二维码测试文本", 200, 200);
        qrcodeElement.setPosition(RelativePosition.of(PositionDirection.CENTER));
        canvas.addElement(qrcodeElement);

        canvas.asFile("png", "qrcode.png");
    }

    @Test
    public void testCompose() {
        EasyCanvas canvas = new EasyCanvas(300, 300);

        ComposeElement composeElement = ComposeElement.of(
                        new QrCodeElement("联系作者jcodeporter@gmail.com", 250, 250)
                                .setPosition(RelativePosition.of(PositionDirection.CENTER)))
                .bottom(new TextElement("测试门店").setFontSize(35).setFontName("仿宋")
                        .setPosition(RelativePosition.of(PositionDirection.TOP_CENTER, Margin.of(5))))
                .setPosition(RelativePosition.of(PositionDirection.CENTER));

        canvas.addElement(composeElement);
        canvas.asFile("png", "qrcode_compose.png");
    }
}
