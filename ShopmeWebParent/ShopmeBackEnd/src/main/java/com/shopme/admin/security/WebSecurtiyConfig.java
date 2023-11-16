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
public class WebSecurtiyConfig extends WebSecurityConfigurerAdapter{

		@Bean
		public UserDetailsService userDetailsService() {
			return new ShopmeUserDetailsService();
		};
		
	    @Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
	    public DaoAuthenticationProvider authenticationProvider() {
	    	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
	    	authenticationProvider.setUserDetailsService(userDetailsService());
	    	authenticationProvider.setPasswordEncoder(passwordEncoder());
//	    	System.out.println("authprov :: "+authenticationProvider.toString());
	    	
	    	return authenticationProvider;
	    }
	 
	    @Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(authenticationProvider());
	    }

		@Override
	    protected void configure(HttpSecurity http) throws Exception {  
	        http.authorizeRequests()
	        .antMatchers("/users/**, /settings/**, /countries/**, /states/**").hasAuthority("Admin")
	        .antMatchers("/categories/**","/brands/**","/menus/**").hasAnyAuthority("Admin", "Editor")
	        
			.antMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
			.antMatchers("/products/save", "/products/check_unique","/products/edit/**").hasAnyAuthority("Admin", "Editor","Salesperson")
			.antMatchers("/products","/products/", "/products/detail/**", "/products/page/**").hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
			.antMatchers("/products/**").hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
			
			.antMatchers("/customers/**","/shipping/**","/articles/**").hasAnyAuthority("Admin", "Salesperson")
			.antMatchers("/orders/**","/report/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
	        .anyRequest()
	        .authenticated()
	        .and().formLogin()
	        .loginPage("/login")
	        .usernameParameter("email")
	        .permitAll()
			.and()
			.logout().permitAll()
			.and()
			.rememberMe().key("AbcDefgKLDSLmvop_0123456789")
			.tokenValiditySeconds(7 * 24 * 60 * 60)  // 7 days 24 hours 60 minutes 60 seconds -> 7days ;
			;
	    }
		 
	    @Override
	    public void configure(WebSecurity  web) throws Exception {
	        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	    }
	    
		/*
		  @Bean protected SecurityFilterChain filterChain(HttpSecurity http) throws
		  Exception { http.authorizeRequests().anyRequest().permitAll(); return
		  http.build(); }
		  
		  @Bean public WebSecurityCustomizer webSecurityCustomizer() {
		  return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
		  }
		 */
}
