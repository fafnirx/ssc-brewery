package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public abstract class CustomRestAuthFilter extends AbstractAuthenticationProcessingFilter {
    public CustomRestAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter ((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            Authentication authenticationResult = attemptAuthentication(request, response);
            if (authenticationResult != null) {
                // return immediately as subclass has indicated that it hasn't completed
                successfulAuthentication(request, response, chain, authenticationResult);
            } else {
                chain.doFilter(request,response);
            }
        } catch (AuthenticationException e) {
            unsuccessfulAuthentication(request,response,e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("Set SecurityContextHolder to {}", authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);

    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {

        SecurityContextHolder.clearContext();

        if (log.isDebugEnabled()) {
            log.debug("Authentication request failed: " + failed.toString(), failed);
            log.debug("Updated SecurityContextHolder to contain null Authentication");
        }

        response.sendError(HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
                                                HttpServletResponse httpServletResponse)
            throws AuthenticationException, IOException, ServletException {
        String name = getUserName(httpServletRequest);
        Optional<String> userName = Optional.ofNullable(name);
        String passwordS = getPassword(httpServletRequest);
        Optional<String> password = Optional.ofNullable(passwordS);

        log.debug("auth user {} and password {}", userName, password);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName.orElse(""),
                        password.orElse(""));
        if (userName.isPresent()) {
            return this.getAuthenticationManager().authenticate(authenticationToken);
        }
        return null;
    }

    protected abstract String getPassword(HttpServletRequest httpServletRequest);

    protected abstract String getUserName(HttpServletRequest httpServletRequest);
}
