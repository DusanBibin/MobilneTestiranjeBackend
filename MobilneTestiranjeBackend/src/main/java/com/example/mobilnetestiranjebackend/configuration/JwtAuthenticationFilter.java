package com.example.mobilnetestiranjebackend.configuration;

import com.example.mobilnetestiranjebackend.model.VerificationEmailChange;
import com.example.mobilnetestiranjebackend.repositories.VerificationEmailChangeRepository;
import com.example.mobilnetestiranjebackend.services.JwtService;
import com.example.mobilnetestiranjebackend.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver exceptionResolver;
    private final VerificationEmailChangeRepository verificationEmailChangeRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
                                   VerificationEmailChangeRepository verificationEmailChangeRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.exceptionResolver = exceptionResolver;
        this.verificationEmailChangeRepository = verificationEmailChangeRepository;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);

            return;
        }

        try{
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);
            Optional<VerificationEmailChange> verWrapper = verificationEmailChangeRepository.findByOldEmail(userEmail);
            if(verWrapper.isPresent()) userEmail = verWrapper.get().getNewEmail();
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if(jwtService.isTokenValid(jwt, userDetails)){ //TODO NASTAVI OVDEDEEEE
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            exceptionResolver.resolveException(request, response, null, e);
        }

    }
}
