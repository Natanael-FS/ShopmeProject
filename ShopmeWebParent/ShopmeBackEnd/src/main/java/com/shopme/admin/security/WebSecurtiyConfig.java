package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurtiyConfig {
		
	    @Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
//		@Bean
//		public UserDetailsService userDetailsService() {
//			return new ShopmeUserDetailsService();
//		};
//	    
//	    public DaoAuthenticationProvider authenticationProvider() {
//	    	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//	    	authenticationProvider.setUserDetailsService(userDetailsService());
//	    	authenticationProvider.setPasswordEncoder(passwordEncoder());
//	    	System.out.println(authenticationProvider.toString());
//	    	return authenticationProvider;
//	    }
//	 
//	    @Override
//		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//			auth.authenticationProvider(authenticationProvider());
//	    }
//
//		@Override
//	    protected void configure(HttpSecurity http) throws Exception {  
//	        http.authorizeRequests()
//	        .anyRequest()
//	        .authenticated()
//	        .and().formLogin()
//	        .loginPage("/login")
//	        .usernameParameter("email")
//	        .permitAll()
//			.and()
//			.logout().permitAll();;
//	    }
//		 
//	    @Override
//	    public void configure(WebSecurity  web) throws Exception {
//	        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
//	    }
	    
		  @Bean protected SecurityFilterChain filterChain(HttpSecurity http) throws
		  Exception { http.authorizeRequests().anyRequest().permitAll(); return
		  http.build(); }
		  
		  @Bean public WebSecurityCustomizer webSecurityCustomizer() {
		  return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
		  }
}
