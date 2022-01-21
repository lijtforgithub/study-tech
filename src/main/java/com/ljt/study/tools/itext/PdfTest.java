package com.ljt.study.tools.itext;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author LiJingTang
 * @date 2022-01-21 16:09
 */
class PdfTest {

    @SneakyThrows
    @Test
    void imageToPDF() {
        final String ORIG = "D:\\李敬堂\\八卦图.jpg";
        final String OUTPUT_FOLDER = "D:\\李敬堂\\";

        ImageData imageData = ImageDataFactory.create(ORIG);

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(OUTPUT_FOLDER + "ImageToPdf.pdf"));
        Document document = new Document(pdfDocument);

        Image image = new Image(imageData);
        image.setWidth(pdfDocument.getDefaultPageSize().getWidth() - 50);
        image.setAutoScaleHeight(true);

        document.add(image);
        pdfDocument.close();
    }

}
