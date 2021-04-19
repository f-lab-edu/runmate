package com.runmate.configure.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest= (HttpServletRequest) request;
        HttpServletResponse httpResponse=(HttpServletResponse)response;

        String requestPath=httpRequest.getServletPath();

        //permit
        if(requestPath.startsWith("/api/auth/")){
            chain.doFilter(request,response);
            return;
        }

        String header=httpRequest.getHeader("Authorization");
        if(header==null || !header.startsWith("Bearer")){
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //validate token
        header=header.replace("Bearer ","");
        try{
            JwtUtils.validate(header);
        }catch(JWTVerificationException exception){
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //Authenticaton completed.
        chain.doFilter(request,response);
        return;
    }
}
