
package com.aleksrd.pi4test.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class JwtTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtProvider				jwtProvider;
	@Autowired
	private UserDetailsService	userDetailsService;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = getToken(request);
			if (token != null && jwtProvider.validateToken(token)) {
				String userName = jwtProvider.getUserNameFromToken(token);
				UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				filterChain.doFilter(request, response);
			}
		} catch (Exception e) {

			log.warn("doFilterInternal() - " + e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized");
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String requestURI = request.getRequestURI();
		String[] uris = {
			"/api/auth/login", "/api/auth/new", "/api/auth/refresh", "/api/admin/message/get",
		};
		List<String> list = Arrays.asList(uris);
		return list.contains(requestURI);
	}

	private String getToken(HttpServletRequest request) throws AuthenticationException {
		String header = request.getHeader("authorization");
		if (header != null) {
			return header.replace("Bearer ", "");
		}
		throw new BadCredentialsException("Invalid token");
	}

}
