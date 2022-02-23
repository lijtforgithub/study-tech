package com.ljt.study.tools.itext;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

/**
 * @author LiJingTang
 * @date 2022-01-21 16:09
 */
class PdfTest {

    @SneakyThrows
    @Test
    void imageToPdf() {
        final String image = "D:\\李敬堂\\test.png";
        final String pdf = "D:\\李敬堂\\ImageToPdf.pdf";
        imageToPdf(new FileInputStream(image), new FileOutputStream(pdf));
    }

    void imageToPdf(InputStream imageInput, OutputStream pdfOutput) throws IOException {
        Objects.requireNonNull(imageInput, "imageInput");
        Objects.requireNonNull(pdfOutput, "pdfOutput");

        byte[] bytes = IOUtils.toByteArray(imageInput);
        ImageData imageData = ImageDataFactory.create(bytes);
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(pdfOutput));

        Image image = new Image(imageData);
        image.setWidth(pdfDocument.getDefaultPageSize().getWidth() - 50);
        image.setAutoScaleHeight(true);

        try (Document document = new Document(pdfDocument)) {
            document.add(image);
            pdfDocument.close();
        }
    }

}
