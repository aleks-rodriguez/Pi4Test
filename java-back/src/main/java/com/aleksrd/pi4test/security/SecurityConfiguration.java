
package com.aleksrd.pi4test.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aleksrd.pi4test.security.jwt.JwtEntryPoint;
import com.aleksrd.pi4test.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

	@Autowired
	private JwtEntryPoint jwtEntryPoint;


	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}

	@Bean
	public PasswordEncoder passwordEnconder() {
		return new BCryptPasswordEncoder(8);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and()
		.csrf().disable()
		.authorizeRequests((auth) -> { auth
			//AuthController
			.mvcMatchers("/api/*").fullyAuthenticated()
			.mvcMatchers(HttpMethod.POST, "/api/auth/changePass").fullyAuthenticated()
			.mvcMatchers(HttpMethod.POST, "/api/auth/newAdmin").hasRole("ADMIN")
			//ProjectController
			.mvcMatchers(HttpMethod.GET, "/api/project/list").hasRole("TESTER")
			.mvcMatchers(HttpMethod.POST, "/api/project/upload").hasRole("TESTER")
			.mvcMatchers(HttpMethod.DELETE, "/api/project/reset/{uid}").hasRole("TESTER")
			//TestController
			.mvcMatchers(HttpMethod.POST, "/api/test/runUnit", "/api/test/jmeter").hasRole("TESTER")
			.mvcMatchers(HttpMethod.GET, "/api/test/download/{uid}", "/api/test/getJMeterPort").hasRole("TESTER")
			//SecurityMessageController
			.mvcMatchers(HttpMethod.POST, "/api/admin/message/save").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.DELETE, "/api/admin/message/delete/{uid}").hasRole("ADMIN")
			//UserController
			.mvcMatchers(HttpMethod.GET, "/api/users/info").fullyAuthenticated()
			.mvcMatchers(HttpMethod.DELETE, "/api/users/delete").fullyAuthenticated()
			.mvcMatchers(HttpMethod.GET, "/api/users/statistics", "/api/users/userManagement", "/api/users/adminManagement", "/api/users/convertSuperAdmin").hasRole("ADMIN")
			.mvcMatchers(HttpMethod.POST, "/api/users/ban","/api/users/enable").hasRole("ADMIN");
		
		})
		.exceptionHandling().authenticationEntryPoint(jwtEntryPoint).and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
