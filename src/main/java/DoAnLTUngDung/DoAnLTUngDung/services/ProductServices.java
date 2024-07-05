package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.entity.Role;
import DoAnLTUngDung.DoAnLTUngDung.entity.User;
import DoAnLTUngDung.DoAnLTUngDung.repository.ICategoryRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ProductServices {
    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }





    public ByteArrayInputStream exportProductsToExcel() throws IOException {
        List<Product> products = getAllProducts();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"Title", "Price", "Quantity", "Description", "Manufacturing Year", "Image Path", "Category"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Tạo CellStyle cho ngày tháng
            CellStyle dateCellStyle = workbook.createCellStyle();
            DataFormat dateFormat = workbook.createDataFormat();
            dateCellStyle.setDataFormat(dateFormat.getFormat("dd/MM/yyyy"));

            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(product.getTitle());
                row.createCell(1).setCellValue(product.getPrice());
                row.createCell(2).setCellValue(product.getSl());
                row.createCell(3).setCellValue(product.getDescription());

                // Định dạng ngày tháng năm
                Cell dateCell = row.createCell(4);
                if (product.getNamSX() != null) {
                    dateCell.setCellValue(product.getNamSX());
                    dateCell.setCellStyle(dateCellStyle);
                } else {
                    dateCell.setCellValue("");
                }

                row.createCell(5).setCellValue(product.getMuTiImagePath());
                row.createCell(6).setCellValue(product.getCategory().getName());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }



    public List<Product> readProductsFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

        List<Product> products = new ArrayList<>();

        // Duyệt qua từng dòng của sheet, bắt đầu từ dòng thứ 1 (dòng tiêu đề đã có trong mẫu)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow != null) {
                Product product = new Product();
                product.setTitle(currentRow.getCell(0).getStringCellValue());
                product.setPrice(currentRow.getCell(1).getNumericCellValue());
                product.setSl((int) currentRow.getCell(2).getNumericCellValue());
                product.setDescription(currentRow.getCell(3).getStringCellValue());
                double excelDate = currentRow.getCell(4).getNumericCellValue();
                Date manufacturingDate = DateUtil.getJavaDate(excelDate);
                product.setNamSX(manufacturingDate);


                product.setMuTiImagePath(currentRow.getCell(5).getStringCellValue());

                // Xử lý category từ file Excel
                String categoryName = currentRow.getCell(6).getStringCellValue();
                Category category = categoryRepository.findByName(categoryName);
                if (category != null) {
                    product.setCategory(category);
                } else {
                    // Nếu không tìm thấy category, có thể xử lý tùy ý (ví dụ: bỏ qua, ghi log,...)
                    // Ví dụ: product.setCategory(null);
                    // Hoặc có thể bỏ qua sản phẩm này
                    continue; // Bỏ qua sản phẩm nếu không tìm thấy category
                }

                products.add(product);
            }
        }

        workbook.close();
        return products;
    }
    public List<Product> getTopTwoHighestPricedProducts() {
        List<Product> allProducts = productRepository.findAll();

        // Sắp xếp danh sách sản phẩm theo giá giảm dần
        List<Product> sortedProducts = allProducts.stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .collect(Collectors.toList());

        // Lấy hai sản phẩm đầu tiên (cao nhất)
        List<Product> topTwoProducts = sortedProducts.stream()
                .limit(2)
                .collect(Collectors.toList());

        return topTwoProducts;
    }


    public void updateProductDetails(Product product) {
        // Lấy thông tin category từ repository và cập nhật vào sản phẩm
        Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
        if (category != null) {
            product.getCategory().setName(category.getName()); // Cập nhật tên của category trong sản phẩm
        }
        // Các bước tương tự cho các thuộc tính khác như mô tả, năm sản xuất, hình ảnh, v.v.
    }
//    //nhapnha
//    public List<User> readUsersFromExcel(InputStream inputStream) throws IOException {
//        Workbook workbook = new XSSFWorkbook(inputStream);
//        Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
//
//        List<User> users = new ArrayList<>();
//
//        // Duyệt qua từng dòng của sheet, bắt đầu từ dòng thứ 1 (dòng tiêu đề đã có trong mẫu)
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//            Row currentRow = sheet.getRow(i);
//            if (currentRow != null) {
//                User user = new User();
//                user.setName(currentRow.getCell(0).getStringCellValue());
//                user.setUsername(currentRow.getCell(1).getStringCellValue());
//                user.setPassword(new BCryptPasswordEncoder().encode(currentRow.getCell(2).getStringCellValue()));
//                user.setEmail(currentRow.getCell(3).getStringCellValue());
//
//                // Xử lý vai trò từ file Excel
//                String[] roleNames = currentRow.getCell(4).getStringCellValue().split(",");
//                Set<Role> roles = new HashSet<>();
//                for (String roleName : roleNames) {
//                    Role role = rolePepository.findByName(roleName.trim());
//                    if (role != null) {
//                        roles.add(role);
//                    }
//                }
//                user.setRoles(new ArrayList<>(roles));
//
//                users.add(user);
//            }
//        }
//
//        workbook.close();
//        return users;
//    }
}
