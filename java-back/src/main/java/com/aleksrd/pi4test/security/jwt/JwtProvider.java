
package com.aleksrd.pi4test.security.jwt;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.aleksrd.pi4test.security.dto.JwtDto;
import com.aleksrd.pi4test.security.dto.UserLoginDto;
import com.aleksrd.pi4test.security.entities.UserAccount;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
public class JwtProvider {

	@Value("${jwt.secret}")
	private String					secret;
	@Value("${jwt.expiration}")
	private int						expiration;
	@Autowired
	private AuthenticationManager authenticationManager;
	

	public String generateToken(Authentication authentication) {
		UserAccount principal = (UserAccount) authentication.getPrincipal();
		List<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		boolean isSuperAdmin = principal.isSuperAdmin();
		return Jwts.builder().setSubject(principal.getUsername()).claim("roles", roles).claim("superAdmin", isSuperAdmin).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(SignatureAlgorithm.HS512, secret.getBytes()).compact();
	}

	public String getUserNameFromToken(String token) {
		return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody().getSubject();
	}

	public JwtDto generateUserLoginToken(UserLoginDto login) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.nick(), login.password()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = generateToken(authentication);
		return new JwtDto(jwt);
	}

	public boolean validateToken(String token) throws BadCredentialsException {
		try {
			Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException ex) {
			log.warn("validateToken() - Malformed token");
			throw new BadCredentialsException("Invalid credentials");
		} catch (UnsupportedJwtException ex) {
			log.warn("validateToken() - Unsupported token");
			throw new BadCredentialsException("The token is invalid");
		} catch (ExpiredJwtException ex) {
			log.warn("validateToken() - Expired token");
			throw new BadCredentialsException("The token is expired");
		} catch (IllegalArgumentException ex) {
			log.warn("validateToken() - Empty token");
			throw new BadCredentialsException("The token is invalid");
		} catch (SignatureException ex) {
			log.warn("validateToken() - Invalid signature");
			throw new BadCredentialsException("Invalid signature");
		}
	}

	@SuppressWarnings("unchecked")
	public JwtDto refreshToken(String token) throws ParseException {
		log.debug("Refreshing token...");
		try {
			Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			JWT jwt = JWTParser.parse(token);
			JWTClaimsSet claims = jwt.getJWTClaimsSet();
			String userName = claims.getSubject();
			List<String> roles = (List<String>) claims.getClaim("roles");

			String refreshedToken = Jwts.builder().setSubject(userName).claim("roles", roles).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(SignatureAlgorithm.HS512, secret.getBytes()).compact();
			JwtDto newJwt = new JwtDto(refreshedToken);
			return newJwt;
		}
		return null;
	}
}
