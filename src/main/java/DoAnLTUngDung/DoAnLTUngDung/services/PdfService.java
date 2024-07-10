package DoAnLTUngDung.DoAnLTUngDung.services;

//import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PdfService {

//    public void generatePdfFromHtml(String htmlContent, OutputStream outputStream) throws IOException {
//        Document document = Jsoup.parse(htmlContent);
//        String xHtml = document.html();
//
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            PdfRendererBuilder builder = new PdfRendererBuilder();
//            builder.withHtmlContent(xHtml, null);
//            builder.toStream(baos);
//            builder.run();
//            baos.writeTo(outputStream);
//        } catch (Exception e) {
//            throw new IOException("Error generating PDF", e);
//        }
//    }
}