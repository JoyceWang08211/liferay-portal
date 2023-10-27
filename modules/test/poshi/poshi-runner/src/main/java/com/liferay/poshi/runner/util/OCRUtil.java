package com.liferay.poshi.runner.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public class OCRUtil {

    public static String extractTextFromImage(String base64Data) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

            BufferedImage image = ImageIO.read(inputStream);

            ITesseract tesseract = new Tesseract();

            tesseract.setDatapath("/tessdata");

            String extractedText = tesseract.doOCR(image);

            return extractedText;

        } catch (IOException | TesseractException e) {

            e.printStackTrace();

            return null;
        }
    }
}