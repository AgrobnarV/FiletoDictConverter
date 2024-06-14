package org.example;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentReader {
    private static final Logger LOGGER = Logger.getLogger(DocumentReader.class.getName());

    public static Map<String, String> readDocument(File file) throws IOException, TesseractException {
        Map<String, String> extractedData = new HashMap<>();
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            extractedData.putAll(readPDF(file));
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            extractedData.putAll(readImage(file));
        }

        return extractedData;
    }

    private static Map<String, String> readPDF(File file) {
        Map<String, String> data = new HashMap<>();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            data.put("content", text);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading PDF file", e);
        }
        return data;
    }

    private static Map<String, String> readImage(File file) {
        Map<String, String> data = new HashMap<>();
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // path to tessdata directory
        try {
            BufferedImage img = ImageIO.read(file);
            String text = tesseract.doOCR(img);
            data.put("content", text);
        } catch (TesseractException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading image file", e);
        }
        return data;
    }

    public static void main(String[] args) {
        try {
            System.setProperty("TESSDATA_PREFIX", "/path");
            File file = new File("/path");
            Map<String, String> result = readDocument(file);
            result.forEach((key, value) -> System.out.println(key + ": " + value));
        } catch (IOException | TesseractException e) {
            LOGGER.log(Level.SEVERE, "Error processing document", e);
        }
    }
}

