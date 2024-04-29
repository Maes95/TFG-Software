package com.tfg.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tfg.app.security.jwt.JwtRequestFilter;




@Configuration
@Order(1)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	//Expose AuthenticationManager as a Bean to be used in other services
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.antMatcher("/api/**");
		
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "DOCTOR", "USER");
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/users/pass/{id}").permitAll();
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/users/{id}").hasAnyRole("USER", "ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/users/check-password/{id}").hasAnyRole("USER", "ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN", "DOCTOR");

		
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/interventions/**").hasAnyRole("ADMIN", "DOCTOR", "USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/interventions/**").hasAnyRole("ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/interventions/**").hasAnyRole("ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/interventions/**").hasAnyRole("ADMIN", "DOCTOR");


		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/appointments/**").hasAnyRole("USER", "ADMIN", "DOCTOR");	
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/appointments/**").hasAnyRole("USER", "ADMIN", "DOCTOR");	
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/appointments/**").hasAnyRole("USER", "ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAnyRole("ADMIN", "DOCTOR");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/documents/**").hasAnyRole("ADMIN", "DOCTOR", "USER");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/documents/**").hasAnyRole("ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.PUT, "/api/documents/**").hasAnyRole("ADMIN", "DOCTOR");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/documents/**").hasAnyRole("ADMIN", "DOCTOR");

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/util/exportInterventionsPDF/**").hasAnyRole("USER", "ADMIN", "DOCTOR");	
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/util/**").permitAll();



		
		// Other URLs can be accessed without authentication
		http.authorizeRequests().anyRequest().permitAll();

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf().disable();

		// Disable Http Basic Authentication
		http.httpBasic().disable();
		
		// Disable Form login Authentication
		http.formLogin().disable();

		// Avoid creating session 
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}
}

    
