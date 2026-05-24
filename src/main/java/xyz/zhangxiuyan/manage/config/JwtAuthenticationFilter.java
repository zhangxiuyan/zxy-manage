package xyz.zhangxiuyan.manage.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.zhangxiuyan.manage.entity.LoginUser;
import xyz.zhangxiuyan.manage.service.JwtService;
import xyz.zhangxiuyan.manage.service.SysUserService;
import xyz.zhangxiuyan.manage.service.TokenStoreService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zxy
 * @version 1.0 - 2025/10/23
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SysUserService sysUserService;
    private final TokenStoreService tokenStoreService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, SysUserService sysUserService, TokenStoreService tokenStoreService) {
        this.jwtService = jwtService;
        this.sysUserService = sysUserService;
        this.tokenStoreService = tokenStoreService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        try {
            // Check if access token is blacklisted
            String jti = jwtService.getJti(token);
            if (jti != null && tokenStoreService.isAccessTokenBlacklisted(jti)) {
                sendErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "token_revoked");
                return;
            }

            Jws<Claims> jws = jwtService.parseAndValidate(token);
            String username = jws.getBody().getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails ud = sysUserService.loadUserByUsername(username);

                // Verify token's userId matches the loaded user
                String tokenUserId = jwtService.getUserId(token);
                if (!matchesUser(ud, tokenUserId)) {
                    sendErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "invalid_token");
                    return;
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(req, res);
        } catch (ExpiredJwtException eje) {
            sendErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "token_expired");
        } catch (Exception ex) {
            sendErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED, "invalid_token");
        }
    }

    private boolean matchesUser(UserDetails ud, String tokenUserId) {
        if (ud instanceof LoginUser loginUser) {
            return loginUser.getId().toString().equals(tokenUserId);
        }
        return true; // Fallback for other UserDetails implementations
    }

    private void sendErrorResponse(HttpServletResponse res, int status, String error) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"" + error + "\"}");
    }
}
