package com.eduardo.studymind.infra.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.util.RateLimiter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket criarBucket(String tipo) {
        Bandwidth limite = switch (tipo) {
            case "auth" -> Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
            case "ia" -> Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
            default -> Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));

        };
        return Bucket.builder().addLimit(limite).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr();
        String uri = httpRequest.getRequestURI();

        String tipo = uri.startsWith("/auth") ? "auth"
                : uri.startsWith("/onboarding") ? "ia"
                : uri.startsWith("/aula") ? "ia"
                : "default";

        String chave = tipo + ":" + ip;
        Bucket bucket = buckets.computeIfAbsent(chave, k -> criarBucket(tipo));


        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);

        } else {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"erro\": \"Muitas requisições. Tente novamente em instantes.\"}"
            );
        }
    }
}
