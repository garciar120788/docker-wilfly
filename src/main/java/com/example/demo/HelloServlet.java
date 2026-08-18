package com.example.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><title>Demo WildFly</title></head>");
            out.println("<body style='font-family: sans-serif; text-align: center; margin-top: 50px;'>");
            out.println("<h1>¡Hola desde WildFly!</h1>");
            out.println("<p>Esta página fue servida por un servlet Java dentro de un contenedor Docker.</p>");
            out.println("<p>Fecha y hora del servidor: " + LocalDateTime.now() + "</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
