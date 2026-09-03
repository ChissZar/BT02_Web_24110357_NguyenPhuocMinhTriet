package vn.iotstar.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/image")
public class DownloadImageController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fileName = request.getParameter("fname");
        if (fileName == null || fileName.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/assets/category-placeholder.svg");
            return;
        }

        Path uploadDirectory = Constant.uploadDirectory();
        Path file = uploadDirectory.resolve(fileName).normalize();
        if (!file.startsWith(uploadDirectory)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!Files.isRegularFile(file)) {
            response.sendRedirect(request.getContextPath() + "/assets/category-placeholder.svg");
            return;
        }

        String contentType = Files.probeContentType(file);
        response.setContentType(contentType == null ? "application/octet-stream" : contentType);
        response.setContentLengthLong(Files.size(file));
        Files.copy(file, response.getOutputStream());
    }
}
