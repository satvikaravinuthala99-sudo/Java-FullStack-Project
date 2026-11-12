
package com.echovault.servlet;

import com.echovault.dao.UserDAO;
import com.echovault.util.Flash;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {

            // Validate user details
            if (name == null || name.isBlank()
                    || email == null
                    || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
                    || password == null
                    || password.length() < 8
                    || !password.equals(confirmPassword)) {

                Flash.set(request, "error",
                        "Check your details. Use a valid email and password of at least 8 characters.");

                response.sendRedirect(request.getContextPath() + "/register.jsp");
                return;
            }

            UserDAO dao = new UserDAO();

            // Check whether email already exists
            if (dao.exists(email)) {

                Flash.set(request, "error",
                        "An account with this email already exists.");

                response.sendRedirect(request.getContextPath() + "/register.jsp");
                return;
            }

            // Create account
            dao.create(
                    name.trim(),
                    email.trim().toLowerCase(),
                    password
            );

            Flash.set(request, "success",
                    "Your EchoVault account is ready. Sign in to begin.");

            response.sendRedirect(request.getContextPath() + "/login.jsp");

        } catch (Exception exception) {

            getServletContext().log("Registration failed", exception);

            Flash.set(request, "error",
                    "We could not create your account. Check database setup and try again.");

            response.sendRedirect(request.getContextPath() + "/register.jsp");
        }
    }
}
