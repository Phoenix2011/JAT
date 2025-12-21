package com.massage.spa.security;

import com.massage.spa.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录授权过滤器
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Value("${jwt.header}")
    private String tokenHeader;
    
    @Value("${jwt.token-start-with}")
    private String tokenStartWith;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // 跳过登录接口
        if ("/api/auth/login".equals(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(this.tokenHeader);
        
        // 存在token
        if (authHeader != null && authHeader.startsWith(this.tokenStartWith)) {
            // 去除Bearer前缀
            String authToken = authHeader.substring(this.tokenStartWith.length()).trim();
            
            // 从token中获取用户名
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            
            // 用户名存在且未认证
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 加载用户信息
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // 验证token是否有效
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    // 将用户信息存入SecurityContextHolder
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        chain.doFilter(request, response);
    }
}
