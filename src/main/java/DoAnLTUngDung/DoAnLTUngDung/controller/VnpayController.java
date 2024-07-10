package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.HoaDon;
import DoAnLTUngDung.DoAnLTUngDung.entity.Order;
import DoAnLTUngDung.DoAnLTUngDung.repository.IHoaDonRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Controller
public class VnpayController {
    @Autowired
    private IHoaDonRepository hoaDonRepository;
    @Autowired
    private VnpayService vnpayService;

    @GetMapping("/vnpay-return")
    public String vnpayReturn( @RequestParam Map<String, String> queryParams, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validate chữ ký từ VNPAY
        if (validateReturnUrl(queryParams)) {
//            // Xử lý thanh toán thành công
//            Order order = (Order) session.getAttribute("order");
//            double totalPrice = (double) session.getAttribute("totalPrice");
//
//            if (order != null) {
//                saveHoaDon(order, totalPrice);
//                session.removeAttribute("order");
//                session.removeAttribute("totalPrice");
                session.removeAttribute("selectedCartItems");
                redirectAttributes.addFlashAttribute("message", "Thanh toán VNPAY thành công! Đơn hàng của bạn đã được hoàn tất!");
                return "redirect:/";
          //  }
        }
        redirectAttributes.addFlashAttribute("message", "Thanh toán VNPAY thất bại!");
        return "redirect:/checkout";
    }
    // Hàm kiểm tra tính hợp lệ của URL trả về từ VNPAY
    private boolean validateReturnUrl(Map<String, String> queryParams) {
        try {
            // Chuỗi bí mật từ VNPAY
            String vnp_HashSecret = "JX8ALD9JQ29FRNLEV5NNDPMKWIUPN21I";

            // Sắp xếp các tham số theo thứ tự từ điển (alphabetical order)
            Map<String, String> sortedParams = new TreeMap<>(queryParams);

            // Lấy và loại bỏ vnp_SecureHash ra khỏi Map
            String secureHash = sortedParams.remove("vnp_SecureHash");

            // Tạo chuỗi dữ liệu can hash
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(entry.getValue());
                    sb.append("&");
                }
            }
            sb.setLength(sb.length() - 1); // Xóa dấu '&' cuối cùng

            // Thêm chuỗi bí mật vào sau chuỗi dữ liệu can hash
            sb.append("&");
            sb.append("vnp_HashSecret=");
            sb.append(vnp_HashSecret);

            // Mã hóa chuỗi dữ liệu can hash bằng HMAC-SHA-512
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            String generatedHash = bytesToHex(hash);

            // So sánh chữ ký nhận được từ VNPAY với chữ ký tính toán
            return secureHash.equalsIgnoreCase(generatedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chuyển byte array sang chuỗi hex
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
//    private void saveHoaDon(Order order, double totalPrice) {
//        HoaDon hoaDon = new HoaDon();
//        hoaDon.setOrder(order);
//        hoaDon.setNgayLap(new Date());
//        hoaDon.setTrangThai("hoan thanh");
//        hoaDon.setTotalPrice(totalPrice);
//        hoaDonRepository.save(hoaDon);
//    }
}
