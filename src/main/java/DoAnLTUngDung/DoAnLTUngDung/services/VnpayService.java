package DoAnLTUngDung.DoAnLTUngDung.services;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.apache.commons.codec.binary.Base64;
import DoAnLTUngDung.DoAnLTUngDung.entity.CartItem;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VnpayService {

    @Value("${vnp_TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnp_HashSecret}")
    private String vnp_HashSecret;


    @Value("${vnp_Url}")
    private String vnp_Url;

    private SecureRandom random = new SecureRandom();
    String bankCode = "NCB";
    public String createPaymentUrl(String orderId, double amount) {
        try{
            String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // Lấy từ @Value hoặc constant

            System.out.println("gia trong payment: "  +String.valueOf((int) (amount * 100)));
            // Chuẩn bị các tham số cần gửi đi
            Map<String, String> params = new HashMap<>();
            params.put("vnp_TmnCode", "OFJJATMS"); // Terminal ID của bạn
            params.put("vnp_Amount", String.valueOf((int) (amount * 100))); // Số tiền thanh toán
            params.put("vnp_OrderInfo", "thanhtoandonhang"); // Thông tin đơn hàng
            params.put("vnp_ReturnUrl", "https://09a2-2405-4802-9015-1770-9d78-7631-2e19-41cd.ngrok-free.app/vnpay-return"); // URL trả về sau khi thanh toán
            params.put("vnp_Command", "pay"); // Lệnh thanh toán
            params.put("vnp_BankCode", "NCB");
            params.put("vnp_CurrCode", "VND"); // Đơn vị tiền tệ
            params.put("vnp_Locale", "vn"); // Ngôn ngữ
            params.put("vnp_TxnRef", generateRandomTxnRef()); // Mã đơn hàng
            params.put("vnp_OrderType", "other"); // Loại đơn hàng
            params.put("vnp_IpAddr", "127.0.0.1"); // Địa chỉ IP của khách hàng
            Calendar cld=Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            params.put("vnp_CreateDate", vnp_CreateDate);
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            params.put("vnp_ExpireDate", vnp_ExpireDate);


        // Tạo chữ ký HMAC-SHA-512
        String vnp_SecureHash = generateVnpayChecksum(params);

        // Thêm chữ ký vào danh sách tham số
        params.put("vnp_SecureHashType", "SHA512");
        params.put("vnp_SecureHash", vnp_SecureHash);

        // Xây dựng URL thanh toán
        StringBuilder urlBuilder = new StringBuilder("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        urlBuilder.setLength(urlBuilder.length() - 1); // Xóa dấu '&' cuối cùng

        return urlBuilder.toString();
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }

//            // Thêm các tham số bảo mật và tạo chữ ký
//            params.put("vnp_HashSecret", "JX8ALD9JQ29FRNLEV5NNDPMKWIUPN21I"); // Chuỗi bí mật của bạn
//            String vnp_SecureHash = generateVnpayChecksum(params); // Tạo chữ ký
//            // Thêm chữ ký vào tham số
//            params.put("vnp_SecureHashType", "MD5"); // Loại chữ ký
//            params.put("vnp_SecureHash", vnp_SecureHash); // Chữ ký

            // Xây dựng URL thanh toán
//            String paymentUrl = buildPaymentUrl(params);
//            return paymentUrl;
//            // Tạo URL thanh toán
//            StringBuilder urlBuilder = new StringBuilder(vnpUrl);
//            urlBuilder.append("?");
//
//            // Append các tham số vào URL
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//            }
//            // Xóa ký tự '&' cuối cùng
//            String paymentUrl = urlBuilder.substring(0, urlBuilder.length() - 1);
//            return paymentUrl;
    }
    // Hàm tạo chữ ký HMAC-SHA-512 cho VNPAY
    public String generateVnpayChecksum(Map<String, String> params) {
        try {
            // Chuỗi bí mật từ VNPAY
            String vnp_HashSecret = "JX8ALD9JQ29FRNLEV5NNDPMKWIUPN21I";

            // Sắp xếp các tham số theo thứ tự từ điển (alphabetical order)
            Map<String, String> sortedParams = new TreeMap<>(params);

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
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(vnp_HashSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(secretKey);

            // Tính toán và trả về chữ ký HMAC-SHA-512
            byte[] hmacBytes = sha512Hmac.doFinal(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder checksum = new StringBuilder();
            for (byte b : hmacBytes) {
                checksum.append(String.format("%02x", b));
            }
            return checksum.toString().toUpperCase(); // Chuyển sang chữ hoa
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // Hàm kiểm tra tính hợp lệ của URL trả về từ VNPAY
//    private boolean validateReturnUrl(Map<String, String> queryParams) {
//        try {
//            // Chuỗi bí mật từ VNPAY
//            String vnp_HashSecret = "JX8ALD9JQ29FRNLEV5NNDPMKWIUPN21I";
//
//            // Sắp xếp các tham số theo thứ tự từ điển (alphabetical order)
//            Map<String, String> sortedParams = new TreeMap<>(queryParams);
//
//            // Lấy và loại bỏ vnp_SecureHash ra khỏi Map
//            String secureHash = sortedParams.remove("vnp_SecureHash");
//
//            // Tạo chuỗi dữ liệu can hash
//            StringBuilder sb = new StringBuilder();
//            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
//                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
//                    sb.append(entry.getKey());
//                    sb.append("=");
//                    sb.append(entry.getValue());
//                    sb.append("&");
//                }
//            }
//            sb.setLength(sb.length() - 1); // Xóa dấu '&' cuối cùng
//
//            // Thêm chuỗi bí mật vào sau chuỗi dữ liệu can hash
//            sb.append("&");
//            sb.append("vnp_HashSecret=");
//            sb.append(vnp_HashSecret);
//
//            // Mã hóa chuỗi dữ liệu can hash bằng HMAC-SHA-512
//            MessageDigest digest = MessageDigest.getInstance("SHA-512");
//            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
//            String generatedHash = bytesToHex(hash);
//
//            // So sánh chữ ký nhận được từ VNPAY với chữ ký tính toán
//            return secureHash.equalsIgnoreCase(generatedHash);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    // Chuyển byte array sang chuỗi hex
//    private String bytesToHex(byte[] bytes) {
//        StringBuilder hexString = new StringBuilder(2 * bytes.length);
//        for (byte b : bytes) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString().toUpperCase();
//    }
    // Hàm sinh mã giao dịch ngẫu nhiên
    private String generateRandomTxnRef() {
        // Độ dài mã giao dịch
        int length = 8;
        // Ký tự cho phép
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
    public String buildPaymentUrl(Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?");
        boolean firstParam = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                value = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                // Handle encoding exception as needed
                e.printStackTrace();
            }
            if (!firstParam) {
                urlBuilder.append("&");
            }
            urlBuilder.append(key).append("=").append(value);
            firstParam = false;
        }
        return urlBuilder.toString();
    }
//    private String generateVnpayChecksum(Map<String, String> params) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(vnp_HashSecret);
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
//                sb.append(entry.getValue());
//            }
//        }
//        return md5(sb.toString());
//    }
//    private String md5(String input) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            byte[] messageDigest = md.digest(input.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for (byte b : messageDigest) {
//                sb.append(String.format("%02x", b));
//            }
//            return sb.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("MD5 hashing algorithm not found", e);
//        }
//    }
// Hàm validate URL trả về từ VNPAY
//public boolean validateReturnUrl(Map<String, String> queryParams) {
//    if (queryParams == null || queryParams.isEmpty()) {
//        return false;
//    }
//    // Lấy chữ ký trả về từ VNPAY
//    String vnp_SecureHash = queryParams.get("vnp_SecureHash");
//    // Xóa chữ ký khỏi các tham số để tính toán chữ ký
//    queryParams.remove("vnp_SecureHash");
//
//    // Tạo chuỗi dữ liệu can hash
//    String checkData = createCheckData(queryParams);
//
//    // Thêm chuỗi bí mật vào sau chuỗi dữ liệu can hash
//    String hashData = checkData + "&vnp_HashSecret=" + vnp_HashSecret;
//    // Mã hóa chuỗi dữ liệu can hash bằng SHA256
//    String checkSum = DigestUtils.sha256Hex(hashData);
//    // So sánh chữ ký từ VNPAY và chữ ký tính toán
//    return vnp_SecureHash.equalsIgnoreCase(checkSum);
//}
//    // Hàm tạo chuỗi dữ liệu can hash
//    private String createCheckData(Map<String, String> params) {
//        // Sắp xếp các tham số theo thứ tự từ điển (alphabetical order)
//        Map<String, String> sortedParams = new TreeMap<>(params);
//
//        // Tạo chuỗi dữ liệu can hash
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
//            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
//                sb.append(entry.getKey());
//                sb.append("=");
//                sb.append(entry.getValue());
//                sb.append("&");
//            }
//        }
//        sb.setLength(sb.length() - 1); // Xóa dấu '&' cuối cùng
//
//        return sb.toString();
//    }
//    private String generateTxnRef() {
//        return String.valueOf(System.currentTimeMillis());
//    }
//
//    private double calculateTotalAmount(List<CartItem> selectedCartItems) {
//        return selectedCartItems.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
//    }
}