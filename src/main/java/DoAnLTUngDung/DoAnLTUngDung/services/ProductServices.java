package DoAnLTUngDung.DoAnLTUngDung.services;

import DoAnLTUngDung.DoAnLTUngDung.entity.Category;
import DoAnLTUngDung.DoAnLTUngDung.entity.Product;
import DoAnLTUngDung.DoAnLTUngDung.repository.ICategoryRepository;
import DoAnLTUngDung.DoAnLTUngDung.repository.IProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(product.getTitle());
                row.createCell(1).setCellValue(product.getPrice());
                row.createCell(2).setCellValue(product.getSl());
                row.createCell(3).setCellValue(product.getDescription());
                row.createCell(4).setCellValue(product.getNamSX().toString());
                row.createCell(5).setCellValue(product.getMuTiImagePath());
                row.createCell(6).setCellValue(product.getCategory().getName());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // Nhập danh sách sản phẩm từ Excel
    public List<Product> readProductsFromExcel(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        List<Product> products = new ArrayList<>();

        // Duyệt qua từng dòng của sheet, bắt đầu từ dòng thứ 1 (dòng tiêu đề đã có trong mẫu)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            if (currentRow != null) {
                Product product = new Product();
                product.setTitle(currentRow.getCell(0).getStringCellValue());
                product.setPrice(currentRow.getCell(1).getNumericCellValue());
                product.setSl(currentRow.getCell(2).getNumericCellValue());
                product.setDescription(currentRow.getCell(3).getStringCellValue());
                product.setNamSX(currentRow.getCell(4).getDateCellValue());
                product.setMuTiImagePath(currentRow.getCell(5).getStringCellValue());

                // Xử lý danh sách hình ảnh (nếu có)
                // Giả sử cột 6 trở đi là danh sách các đường dẫn hình ảnh
                List<String> imagePaths = new ArrayList<>();
                for (int j = 6; j < currentRow.getLastCellNum(); j++) {
                    String imagePath = currentRow.getCell(j).getStringCellValue();
                    imagePaths.add(imagePath);
                }
                product.setImagePaths(imagePaths);

                // Xử lý danh mục sản phẩm từ Excel
                String categoryName = currentRow.getCell(6).getStringCellValue();
                Category category = categoryRepository.findByName(categoryName);
                if (category == null) {
                    // Xử lý nếu danh mục không tồn tại
                    throw new RuntimeException("Danh mục '" + categoryName + "' không tồn tại.");
                }
                product.setCategory(category);

                products.add(product);
            }
        }

        workbook.close();
        return products;
    }
}
