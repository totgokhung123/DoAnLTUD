package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.repository.IOrderDetailRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public Integer getTotalQuantitySoldByProductId(Long productId) {
        // Implement your logic to fetch total quantity sold from OrderDetailRepository
        // Example:
        // return orderDetailRepository.getTotalQuantitySoldByProductId(productId);
        return null; // Replace with your actual implementation
    }
}
