package com.runmate.configure.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter implements Filter {
    private JwtProvider jwtProvider;
    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider=jwtProvider;
    }

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
            jwtProvider.validate(header);
        }catch(JWTVerificationException exception){
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //Authenticaton completed.
        ModifiableHttpServletRequest modifiablerequest=new ModifiableHttpServletRequest(httpRequest);
        modifiablerequest.setParameter("email",jwtProvider.getClaim(header));
        request=(HttpServletRequest)modifiablerequest;

        chain.doFilter(request,response);
        return;
    }
    static class ModifiableHttpServletRequest extends HttpServletRequestWrapper {

        private HashMap<String, Object> params;

        @SuppressWarnings("unchecked")
        public ModifiableHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.params = new HashMap<String, Object>(request.getParameterMap());
        }

        public String getParameter(String name) {
            String returnValue = null;
            String[] paramArray = getParameterValues(name);
            if (paramArray != null && paramArray.length > 0) {
                returnValue = paramArray[0];
            }
            return returnValue;
        }

        @SuppressWarnings("unchecked")
        public Map getParameterMap() {
            return Collections.unmodifiableMap(params);
        }

        @SuppressWarnings("unchecked")
        public Enumeration getParameterNames() {
            return Collections.enumeration(params.keySet());
        }

        public String[] getParameterValues(String name) {
            String[] result = null;
            String[] temp = (String[]) params.get(name);
            if (temp != null) {
                result = new String[temp.length];
                System.arraycopy(temp, 0, result, 0, temp.length);
            }
            return result;
        }

        public void setParameter(String name, String value) {
            String[] oneParam = { value };
            setParameter(name, oneParam);
        }

        public void setParameter(String name, String[] value) {
            params.put(name, value);
        }
    }
}
