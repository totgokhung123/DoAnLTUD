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


    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }


    public ByteArrayInputStream exportProductsToExcel() throws IOException {
        List<Product> products = getAllProducts();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Products");
            Row headerRow = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"Title", "Price", "Quantity", "Description", "Manufacturing Year", "Anh dai dien", "Category" ,"Image Path"};
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

                row.createCell(5).setCellValue(product.getAnhdaidien());
                row.createCell(6).setCellValue(product.getCategory().getName());
                row.createCell(7).setCellValue(product.getMuTiImagePath());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public void deleteall(List<Long> productIDs) {
        for (Long id : productIDs) {
            productRepository.deleteById(id);
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

                Cell imageCell1 = currentRow.getCell(5);
                String imagePath1 = (imageCell1 != null && imageCell1.getCellType() == CellType.STRING) ?
                        imageCell1.getStringCellValue() : "null";
                product.setAnhdaidien(imagePath1);


                // Xử lý category từ file Excel
                String categoryName = currentRow.getCell(6).getStringCellValue();

                Cell imageCell = currentRow.getCell(7);
                String imagePath = (imageCell != null && imageCell.getCellType() == CellType.STRING) ?
                        imageCell.getStringCellValue() : "null";
                product.setMuTiImagePath(imagePath);

                Category category = categoryRepository.findByName(categoryName);
                if (category != null) {
                    product.setCategory(category);
                } else {
                    continue; // Bỏ qua sản phẩm nếu không tìm thấy category
                }

                products.add(product);
            }
        }

        workbook.close();
        return products;
    }



    public void updateProductDetails(Product product) {
        // Lấy thông tin category từ repository và cập nhật vào sản phẩm
        Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
        if (category != null) {
            product.getCategory().setName(category.getName()); // Cập nhật tên của category trong sản phẩm
        }
        // Các bước tương tự cho các thuộc tính khác như mô tả, năm sản xuất, hình ảnh, v.v.
    }

    public List<Product> getProductsByCategoryIdAndHasImage(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .filter(product -> product.getAnhdaidien() != null && product.getSl() > 1)
                .collect(Collectors.toList());
    }


}
