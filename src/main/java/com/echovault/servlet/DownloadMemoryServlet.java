
package com.echovault.servlet;

import com.echovault.dao.CapsuleDAO;
import com.echovault.model.Capsule;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/memory")
public class DownloadMemoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int capsuleId = Integer.parseInt(
                    request.getParameter("capsuleId")
            );

            int userId = (Integer) session.getAttribute("userId");

            String userEmail = (String) session.getAttribute("userEmail");

            CapsuleDAO dao = new CapsuleDAO();

            Capsule capsule = dao.findVisible(
                    capsuleId,
                    userId,
                    userEmail
            );

            if (capsule == null || !capsule.isUnlocked()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            String storedName = request.getParameter("file");

            if (storedName == null || storedName.isBlank()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            boolean allowed = false;

            for (String[] memory : dao.memories(capsuleId)) {
                if (memory[1].equals(storedName)) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Path uploadDirectory = Paths.get(
                    System.getProperty("user.home"),
                    "echovault-uploads"
            ).toAbsolutePath().normalize();

            Path file = uploadDirectory.resolve(storedName).normalize();

            if (!file.startsWith(uploadDirectory) || !Files.exists(file)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String contentType = Files.probeContentType(file);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            response.setContentType(contentType);

            String safeName = storedName.replace("\"", "");

            response.setHeader(
                    "Content-Disposition",
                    "inline; filename=\"" + safeName + "\""
            );

            Files.copy(file, response.getOutputStream());

        } catch (NumberFormatException e) {

            response.sendError(HttpServletResponse.SC_BAD_REQUEST);

        } catch (Exception e) {

            getServletContext().log("Memory download failed", e);

            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
