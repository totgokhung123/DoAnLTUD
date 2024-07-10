package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.HoaDon;
import DoAnLTUngDung.DoAnLTUngDung.entity.OrderDetail;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfService {

    public void generateInvoicePdf(HoaDon hoaDon, HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

        // Tiêu đề hóa đơn
        Paragraph title = new Paragraph("HÓA ĐƠN", boldFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Thông tin hóa đơn
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        document.add(new Paragraph("Mã hóa đơn: " + hoaDon.getId(), normalFont));
        document.add(new Paragraph("Ngày lập: " + dateFormat.format(hoaDon.getNgayLap()), normalFont));
        document.add(new Paragraph("Trạng thái: " + hoaDon.getTrangThai(), normalFont));
        document.add(new Paragraph("Tổng tiền: " + hoaDon.getTotalPrice(), normalFont));
        document.add(Paragraph.getInstance(" "));

        // Thông tin chi tiết đơn hàng
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 1, 1, 1});

        addTableHeader(table);
        addRows(table, hoaDon.getOrder().getOrderDetails());

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Sản phẩm", "Giá", "Số lượng", "Tổng")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<OrderDetail> orderDetails) {
        for (OrderDetail orderDetail : orderDetails) {
            table.addCell(orderDetail.getProduct().getTitle());
            table.addCell(String.valueOf(orderDetail.getPrice()));
            table.addCell(String.valueOf(orderDetail.getQuantity()));
            table.addCell(String.valueOf(orderDetail.getPrice() * orderDetail.getQuantity()));
        }
    }
}
