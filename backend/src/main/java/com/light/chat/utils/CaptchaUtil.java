package com.light.chat.utils;

import org.apache.commons.lang3.RandomStringUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

public class CaptchaUtil {

    public static String generateNumberCode(int length) {
        return RandomStringUtils.random(length, false, true);
    }
    
    public static void generateImage(String code, OutputStream output) throws Exception {
        int width = 160, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(code, 20, 25);
        
        // 添加干扰线
        g.setColor(Color.GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = (int) (Math.random() * width);
            int y1 = (int) (Math.random() * height);
            int x2 = (int) (Math.random() * width);
            int y2 = (int) (Math.random() * height);
            g.drawLine(x1, y1, x2, y2);
        }
        
        ImageIO.write(image, "JPEG", output);
    }
}