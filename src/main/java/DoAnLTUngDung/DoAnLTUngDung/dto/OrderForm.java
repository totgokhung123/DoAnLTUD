package DoAnLTUngDung.DoAnLTUngDung.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderForm {
    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(min = 10, max = 32, message = "Tên người nhận ít nhất 10 ký tự và lớn nhất 32 ký tự")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(min = 10, max = 15, message = "Số điện thoại ít nhất 10 số lớn nhất 14 số")
    private String recipientPhone;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 10, message = "Địa chỉ ít nhất 10 ký tự")
    private String recipientAddress;

    @Email(message = "Email phải hợp lệ")
    @NotNull(message = "Email không được để trống")
    private String email;

    private String note;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
}