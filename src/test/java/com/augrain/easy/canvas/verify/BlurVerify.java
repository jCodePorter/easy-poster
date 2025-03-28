package com.augrain.easy.canvas.verify;

import com.augrain.easy.canvas.basic.image.ImageBasicTest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 高斯模糊
 *
 * @author biaoy
 * @since 2025/03/27
 */
public class BlurVerify {

    private static double[][] createGaussianKernel(int size, double sigma) {
        double[][] kernel = new double[size][size];
        double sum = 0.0;

        int halfSize = size / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                kernel[x + halfSize][y + halfSize] = (1 / (2 * Math.PI * sigma * sigma))
                        * Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                sum += kernel[x + halfSize][y + halfSize];
            }
        }

        // 归一化内核
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[i][j] /= sum;
            }
        }
        return kernel;
    }

    private static BufferedImage applyGaussianBlur(BufferedImage image, double[][] kernel) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage blurredImage = new BufferedImage(width, height, image.getType());

        int kernelSize = kernel.length;
        int halfSize = kernelSize / 2;

        for (int x = halfSize; x < width - halfSize; x++) {
            for (int y = halfSize; y < height - halfSize; y++) {
                double r = 0, g = 0, b = 0;

                for (int kx = -halfSize; kx <= halfSize; kx++) {
                    for (int ky = -halfSize; ky <= halfSize; ky++) {
                        int pixel = image.getRGB(x + kx, y + ky);
                        r += ((pixel >> 16) & 0xff) * kernel[kx + halfSize][ky + halfSize];
                        g += ((pixel >> 8) & 0xff) * kernel[kx + halfSize][ky + halfSize];
                        b += (pixel & 0xff) * kernel[kx + halfSize][ky + halfSize];
                    }
                }

                // 设置模糊后的 RGB 值
                int newPixel = ((int) r << 16) | ((int) g << 8) | (int) b;
                blurredImage.setRGB(x, y, newPixel);
            }
        }
        return blurredImage;
    }

    public static void saveImage(BufferedImage image, String path) throws IOException {
        // 将模糊后的图像保存到指定路径
        ImageIO.write(image, "png", new File(path));
    }

    public static void main(String[] args) {
        try {
            InputStream inputStream = ImageBasicTest.class.getClassLoader().getResourceAsStream("lotus.png");
            BufferedImage read = ImageIO.read(inputStream);
            double[][] kernel = createGaussianKernel(3, 50);
            BufferedImage blurredImage = applyGaussianBlur(read, kernel);
            saveImage(blurredImage, "output.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
