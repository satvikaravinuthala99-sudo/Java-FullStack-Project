
package com.echovault.servlet;

import com.echovault.dao.CapsuleDAO;
import com.echovault.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/create-capsule")
@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 12582912)
public class CreateCapsuleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String recipient = request.getParameter("recipientEmail");
        String visibility = request.getParameter("visibility");
        String date = request.getParameter("unlockAt");

        try {
            if (title == null || title.isBlank()
                    || date == null || date.isBlank()) {

                Flash.set(request, "error",
                        "Add a capsule title and unlock date.");

                response.sendRedirect("create-capsule.jsp");
                return;
            }

            LocalDateTime localDateTime = LocalDateTime.parse(date);
            Timestamp unlockTime = Timestamp.valueOf(localDateTime);

            if (!unlockTime.after(new Timestamp(System.currentTimeMillis()))) {
                Flash.set(request, "error",
                        "Choose a future unlock date and time.");

                response.sendRedirect("create-capsule.jsp");
                return;
            }

            if ("SHARED".equals(visibility)
                    && (recipient == null
                    || !recipient.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))) {

                Flash.set(request, "error",
                        "Enter a valid recipient email for a shared capsule.");

                response.sendRedirect("create-capsule.jsp");
                return;
            }

            int userId = (Integer) session.getAttribute("userId");

            int capsuleId = new CapsuleDAO().create(
                    userId,
                    title.trim(),
                    description,
                    recipient,
                    "SHARED".equals(visibility) ? "SHARED" : "PRIVATE",
                    unlockTime
            );

            Part part = request.getPart("memoryFile");

            if (part != null && part.getSize() > 0) {

                String submittedName = part.getSubmittedFileName();

                String originalName = Paths.get(submittedName)
                        .getFileName().toString();

                String extension = "";
                int dot = originalName.lastIndexOf('.');

                if (dot >= 0) {
                    extension = originalName.substring(dot)
                            .replaceAll("[^A-Za-z0-9.]", "");
                }

                String storedName = UUID.randomUUID() + extension;

                Path uploadDirectory = Paths.get(
                        System.getProperty("user.home"),
                        "echovault-uploads"
                );

                Files.createDirectories(uploadDirectory);

                part.write(uploadDirectory.resolve(storedName).toString());

                new CapsuleDAO().addMemory(
                        capsuleId,
                        originalName,
                        storedName,
                        part.getContentType()
                );
            }

            Flash.set(request, "success",
                    "Your time capsule has been sealed. Its contents stay locked until the chosen time.");

            response.sendRedirect("dashboard");

        } catch (Exception e) {

            getServletContext().log("Capsule creation failed", e);

            Flash.set(request, "error",
                    "Could not seal the capsule. Check the date and database, then try again.");

            response.sendRedirect("create-capsule.jsp");
        }
    }
}
