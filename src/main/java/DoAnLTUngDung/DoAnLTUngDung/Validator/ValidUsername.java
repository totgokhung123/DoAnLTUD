package DoAnLTUngDung.DoAnLTUngDung.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD}) //Áp dụng cho các loại dữ Liệu (class) và các trường (field). 1usage
@Retention(RUNTIME) //Cho phép được sử dụng runtime để thực hiện các kiểm tra.
//Lop Validator cần kiểm tra
@Constraint(validatedBy = ValidUsernameValidator.class)
public @interface ValidUsername {
    String message() default "Tên đăng nhập không hợp lệ"; //Thông báo khi vi pham ràng buộc
    //Nhóm các ràng buộc Liên quan Lại với nhau.
    Class <? extends Payload>[] payload() default {};

    Class <? >[] groups() default {};//Cung cấp thêm chi tiết về lỗi

}