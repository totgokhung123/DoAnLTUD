package DoAnLTUngDung.DoAnLTUngDung.controller;

import DoAnLTUngDung.DoAnLTUngDung.dto.OrderForm;
import DoAnLTUngDung.DoAnLTUngDung.entity.*;
import DoAnLTUngDung.DoAnLTUngDung.repository.IHoaDonRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IOrderRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IProductRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.OrderDetailRepository;
import DoAnLTUngDung.DoAnLTUngDung.services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IProductRepository p;
    @Autowired
    private IHoaDonRepository hoaDonRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;

    @Autowired
    private VnpayService vnpayService;

    @Autowired
    private UserServices userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductServices productService;
    @Autowired
    private PdfService pdfService;
    private CategoryServices categoryServices;

    @PostMapping("/checkout")
    public String checkout(@RequestParam("selectedProducts") List<Long> selectedProductIds,Model model, HttpSession session,Authentication authentication) {
        // Log để kiểm tra giá trị của selectedProducts
        if (selectedProductIds == null) {
            System.out.println("selectedProducts is null");
        } else {
            System.out.println("selectedProducts size: " + selectedProductIds.size());
            System.out.println("selectedProducts content: " + selectedProductIds);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        // Lấy danh sách CartItem được chọn dựa trên user hiện tại
        List<CartItem> selectedCartItems = cartService.findCartItemsByIds(selectedProductIds, user);
        session.setAttribute("selectedCartItems", selectedCartItems);
        // Đưa selectedCartItems vào model để sử dụng trong template
        model.addAttribute("selectedCartItemss", selectedCartItems);
        Double s = 0.0;
        if (selectedCartItems == null) {
            System.out.println("selectedCartItems is null");
        } else {
            System.out.println("selectedCartItems size: " + selectedCartItems.size());
            System.out.println("selectedCartItems content: " + selectedCartItems);

            for(CartItem cart : selectedCartItems){
                System.out.println("cart id: " + cart.getId());
                System.out.println("cart product name: " + cart.getProduct().getTitle());
                System.out.println("cart product price: " + cart.getProduct().getPrice());
                System.out.println("cart user: " + cart.getUser().getId());
                s += cart.getProduct().getPrice() * cart.getQuantity();
                System.out.println("totalPrice trong USER/Order: " + s);
            }

        }
        List<Category> categories = categoryServices.getAllCategories()
                .stream()
                .filter(Category::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        model.addAttribute("sum",s);
        return "USER/Order";
    }

    @PostMapping("/complete")
    public String submitOrder(
        @RequestParam("recipientName") String recipientName,
        @RequestParam("recipientPhone") String recipientPhone,
        @RequestParam("recipientAddress") String recipientAddress,
        @RequestParam("email") String email,
        @RequestParam("note") String note,
        @RequestParam("paymentMethod") String paymentMethod,
        Model model,
        HttpSession session,
        Authentication authentication,RedirectAttributes redirectAttributes) {
        List<CartItem> selectedCartItems = (List<CartItem>) session.getAttribute("selectedCartItems");
        if (selectedCartItems == null || selectedCartItems.isEmpty()) {
            return "redirect:/cart";
        }
        List<Category> categories = categoryServices.getAllCategories()
                .stream()
                .filter(Category::getStatus)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);
        // Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setTenNguoiNhan(recipientName);
        order.setSdt(recipientPhone);
        order.setDiachi(recipientAddress);
        order.setEmail(email);
        order.setNote(note);
        order.setPttt(paymentMethod);
        orderRepository.save(order);
        double totalPrice = 0;
        // Tạo OrderDetail từ CartItem được chọn

        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();

            if (product != null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProduct(product);
                orderDetail.setOrder(order);
                orderDetail.setQuantity(cartItem.getQuantity());
                orderDetail.setPrice(product.getPrice() * cartItem.getQuantity());
                order.getOrderDetails().add(orderDetail);
                orderDetailRepository.save(orderDetail);
                totalPrice += orderDetail.getPrice();
                double remainingQuantity = product.getQuantity() - cartItem.getQuantity();
                product.setQuantity(remainingQuantity);
                p.save(product);
            }
        }
        if ("VNPAY".equals(paymentMethod)) {
            double amount = totalPrice;
            String paymentUrl = vnpayService.createPaymentUrl(order.getId().toString(), amount);
            saveHoaDon(order, totalPrice);
            //session.setAttribute("order", order);
           // session.setAttribute("totalPrice", totalPrice);
            return "redirect:" + paymentUrl;
        } else {
            HoaDon hoaDon = saveHoaDon(order, totalPrice);
            double subTotal = totalPrice;
            double tax = subTotal * 0.1;
            double total = subTotal + tax;
            System.out.println("od : " + order.getOrderDetails());
            for(OrderDetail i :order.getOrderDetails()){
                System.out.println("od : " + i.getProduct().getId());
                System.out.println("od : " + i.getProduct().getTitle());
            }
            //OrderDetail i = order.getOrderDetails();
            model.addAttribute("user", user);
            model.addAttribute("order", order);
            model.addAttribute("orderDetails",order.getOrderDetails());
            model.addAttribute("subTotal", subTotal);
            model.addAttribute("tax", tax);
            model.addAttribute("total", total);
            model.addAttribute("hoaDon", hoaDon);
            session.removeAttribute("selectedCartItems");
            redirectAttributes.addFlashAttribute("message", "Đơn hàng của bạn đã được hoàn tất!");
            return "HOADON/invoice-4";
        }
    }
    private HoaDon saveHoaDon(Order order, double totalPrice) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setOrder(order);
        hoaDon.setNgayLap(new Date());
        hoaDon.setTrangThai("Success"); // Hoặc trạng thái phù hợp khác
        hoaDon.setTotalPrice(totalPrice);
        hoaDonRepository.save(hoaDon);
        return hoaDon;
    }
//    @Autowired
//    private ThymeleafTemplateService templateService;


//    @GetMapping("/order/download-invoice/{id}")
//    public void downloadInvoice(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
//        // Tạo model cho Thymeleaf template
//        Map<String, Object> model = new HashMap<>();
//        model.put("hoaDon", getHoaDonById(id));
//        model.put("user", getUserById(id));
//        model.put("order", getOrderById(id));
//        model.put("orderDetails", getOrderDetailsById(id));
//        model.put("subTotal", getSubTotal(id));
//        model.put("tax", getTax(id));
//        model.put("total", getTotal(id));
//
//        // Render Thymeleaf template thành HTML string
//        String htmlContent = templateService.processTemplate("invoice_template", model);
//
//        // Tạo tên file PDF
//        String pdfFileName = "invoice_" + id + ".pdf";
//
//        // Thiết lập thông tin HTTP response
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
//
//        // Chuyển đổi HTML sang PDF và ghi vào response
//        pdfService.generatePdfFromHtml(htmlContent, response.getOutputStream());
//    }
//
//    // Các phương thức để lấy dữ liệu từ CSDL (hoặc dịch vụ khác)
//    private Object getHoaDonById(Long id) {
//        // Implement this method to fetch hoaDon by id
//        return new Object(); // Dummy object, replace with actual implementation
//    }
//
//    private Object getUserById(Long id) {
//        // Implement this method to fetch user by id
//        return new Object(); // Dummy object, replace with actual implementation
//    }
//
//    private Object getOrderById(Long id) {
//        // Implement this method to fetch order by id
//        return new Object(); // Dummy object, replace with actual implementation
//    }
//
//    private Object getOrderDetailsById(Long id) {
//        // Implement this method to fetch order details by id
//        return new Object(); // Dummy object, replace with actual implementation
//    }
//
//    private Object getSubTotal(Long id) {
//        // Implement this method to fetch subtotal
//        return new Object(); // Dummy object, replace with actual implementation
//    }
//
//    private Object getTax(Long id) {
//        // Implement this method to fetch tax
//        return new Object(); // Dummy object, replace with actual implementation
//    }
//
//    private Object getTotal(Long id) {
//        // Implement this method to fetch total
//        return new Object(); // Dummy object, replace with actual implementation
//    }
}