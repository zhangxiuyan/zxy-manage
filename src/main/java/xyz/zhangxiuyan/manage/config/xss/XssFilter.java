package xyz.zhangxiuyan.manage.config.xss;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;
import java.io.IOException;
import java.util.*;

public class XssFilter implements Filter {

    private final AntPathMatcher matcher = new AntPathMatcher();

    /**
     * 需要跳过的路径白名单（支持 AntPath）
     * 如：/auth/**, /public/**, /static/*
     */
    private final List<String> excludePatterns = new ArrayList<>();

    public XssFilter(List<String> excludePatterns) {
        if (excludePatterns != null) {
            this.excludePatterns.addAll(excludePatterns);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        if (shouldNotFilter(req)) {
            chain.doFilter(request, response);
            return;
        }

        XssRequestWrapper wrapped = new XssRequestWrapper(req);
        chain.doFilter(wrapped, response);
    }

    @Override
    public void destroy() {}

    /**
     * 支持 AntPath 匹配
     */
    private boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : excludePatterns) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
