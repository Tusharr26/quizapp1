package com.quizapp.util;

import com.quizapp.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class sessionUtil {
    
    /**
     * Checks if a user is logged in
     * 
     * @param request The HTTP request
     * @return True if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }
    
    /**
     * Gets the current user from the session
     * 
     * @param request The HTTP request
     * @return The current user, or null if not logged in
     */
    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
    /**
     * Redirects to login page if not logged in
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return True if redirected, false if user is logged in
     * @throws IOException If an I/O error occurs
     */
    public static boolean redirectIfNotLoggedIn(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return true;
        }
        return false;
    }
    
    /**
     * Redirects to dashboard if already logged in
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @return True if redirected, false if user is not logged in
     * @throws IOException If an I/O error occurs
     */
    public static boolean redirectIfLoggedIn(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        if (isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return true;
        }
        return false;
    }
    
    /**
     * Creates a new session and stores user information
     * 
     * @param request The HTTP request
     * @param user The user to store in session
     */
    public static void createUserSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
    }
    
    /**
     * Invalidates the current session
     * 
     * @param request The HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * Sets a flash message to be displayed on the next page
     * 
     * @param request The HTTP request
     * @param type The message type (success, error, info, warning)
     * @param message The message text
     */
    public static void setFlashMessage(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }
    
    /**
     * Gets and clears the flash message
     * 
     * @param request The HTTP request
     * @return A String[] with {type, message} or null if no message
     */
    public static String[] getAndClearFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String type = (String) session.getAttribute("flashType");
            String message = (String) session.getAttribute("flashMessage");
            
            if (type != null && message != null) {
                session.removeAttribute("flashType");
                session.removeAttribute("flashMessage");
                return new String[] {type, message};
            }
        }
        return null;
    }
}