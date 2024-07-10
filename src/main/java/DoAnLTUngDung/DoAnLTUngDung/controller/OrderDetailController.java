package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.entity.OrderDetail;
import DoAnLTUngDung.DoAnLTUngDung.services.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

//    @GetMapping("/orderhistory")
//    public String showOrderHistory(Model model) {
//        List<OrderDetail> orderDetails = orderDetailService.();
//        model.addAttribute("orderDetails", orderDetails);
//        return "USER/OrderHistory";
//    }
}