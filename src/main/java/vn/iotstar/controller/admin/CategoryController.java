package vn.iotstar.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

@MultipartConfig
@WebServlet(urlPatterns = {
        "/admin/categories",
        "/admin/category/add",
        "/admin/category/insert",
        "/admin/category/edit",
        "/admin/category/update",
        "/admin/category/delete"
})
public class CategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ICategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        switch (request.getServletPath()) {
            case "/admin/categories" -> showList(request, response);
            case "/admin/category/add" -> showAddForm(request, response);
            case "/admin/category/edit" -> showEditForm(request, response);
            case "/admin/category/delete" -> deleteCategory(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        switch (request.getServletPath()) {
            case "/admin/category/insert" -> insertCategory(request, response);
            case "/admin/category/update" -> updateCategory(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = normalize(request.getParameter("keyword"));
        List<Category> categories = keyword.isEmpty()
                ? categoryService.findAll()
                : categoryService.searchByName(keyword);
        request.setAttribute("listcate", categories);
        request.setAttribute("count", categories.size());
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/views/admin/category-list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/admin/category-add.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = parseId(request.getParameter("id"));
        Category category = categoryService.findById(id);
        if (category == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy Category");
            return;
        }
        request.setAttribute("cate", category);
        request.getRequestDispatcher("/views/admin/category-edit.jsp").forward(request, response);
    }

    private void insertCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryname = normalize(request.getParameter("categoryname"));
        if (categoryname.isEmpty()) {
            request.setAttribute("alert", "Tên Category không được để trống");
            showAddForm(request, response);
            return;
        }

        Category category = new Category();
        category.setCategoryname(categoryname);
        category.setStatus(parseStatus(request.getParameter("status")));
        category.setImages(resolveImage(request, null));

        try {
            categoryService.insert(category);
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("alert", exception.getMessage());
            showAddForm(request, response);
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int categoryid = parseId(request.getParameter("categoryid"));
        Category category = categoryService.findById(categoryid);
        if (category == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy Category");
            return;
        }

        String categoryname = normalize(request.getParameter("categoryname"));
        if (categoryname.isEmpty()) {
            request.setAttribute("alert", "Tên Category không được để trống");
            request.setAttribute("cate", category);
            request.getRequestDispatcher("/views/admin/category-edit.jsp").forward(request, response);
            return;
        }

        String oldImage = category.getImages();
        category.setCategoryname(categoryname);
        category.setStatus(parseStatus(request.getParameter("status")));
        category.setImages(resolveImage(request, oldImage));

        try {
            categoryService.update(category);
            if (!sameValue(oldImage, category.getImages())) {
                deleteLocalImage(oldImage);
            }
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        } catch (IllegalArgumentException exception) {
            request.setAttribute("alert", exception.getMessage());
            request.setAttribute("cate", category);
            request.getRequestDispatcher("/views/admin/category-edit.jsp").forward(request, response);
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = parseId(request.getParameter("id"));
        Category category = categoryService.findById(id);
        if (category == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy Category");
            return;
        }

        try {
            categoryService.delete(id);
            deleteLocalImage(category.getImages());
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Không thể xóa Category này");
        }
    }

    private String resolveImage(HttpServletRequest request, String oldImage)
            throws IOException, ServletException {
        Part part = request.getPart("images1");
        if (part != null && part.getSize() > 0 && !normalize(part.getSubmittedFileName()).isEmpty()) {
            return saveUploadedImage(part);
        }

        String imageLink = normalize(request.getParameter("images"));
        if (!imageLink.isEmpty()) {
            return imageLink;
        }
        if (oldImage != null && !oldImage.isBlank()) {
            return oldImage;
        }
        return "avatar.png";
    }

    private String saveUploadedImage(Part part) throws IOException {
        Path uploadDirectory = Constant.uploadDirectory();
        Files.createDirectories(uploadDirectory);

        String submittedName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
        int extensionIndex = submittedName.lastIndexOf('.');
        String extension = extensionIndex >= 0 ? submittedName.substring(extensionIndex) : "";
        String fileName = System.currentTimeMillis() + extension;
        Path target = uploadDirectory.resolve(fileName).normalize();

        try (var inputStream = part.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    private void deleteLocalImage(String imageName) throws IOException {
        if (imageName == null || imageName.isBlank() || imageName.equals("avatar.png") || isRemoteImage(imageName)) {
            return;
        }
        Path uploadDirectory = Constant.uploadDirectory();
        Path imagePath = uploadDirectory.resolve(imageName).normalize();
        if (imagePath.startsWith(uploadDirectory)) {
            Files.deleteIfExists(imagePath);
        }
    }

    private boolean isRemoteImage(String imageName) {
        return imageName.startsWith("http://") || imageName.startsWith("https://");
    }

    private boolean sameValue(String first, String second) {
        return first == null ? second == null : first.equals(second);
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private int parseStatus(String value) {
        return "1".equals(value) ? 1 : 0;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
