package com.echovault.util;
import jakarta.servlet.http.*;
public final class Flash { private Flash(){} public static void set(HttpServletRequest r,String type,String message){r.getSession().setAttribute("flashType",type);r.getSession().setAttribute("flashMessage",message);} }