package com.zcunsoft.clklog.framework.filter;

import com.zcunsoft.clklog.api.services.ReportServiceImpl;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求结束后清理 ReportServiceImpl 中的 ThreadLocal。
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class ReportThreadLocalCleanupFilter extends OncePerRequestFilter {

    @Resource
    private ReportServiceImpl reportService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            reportService.clearThreadLocals();
        }
    }
}
