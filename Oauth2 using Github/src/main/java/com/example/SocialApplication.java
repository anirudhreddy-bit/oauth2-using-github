
package com.example;

import java.net.CookieHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SocialApplication extends WebSecurityConfigurerAdapter {

	@RequestMapping("/user")
	public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
		Map<String, Object> answer = new HashMap<>();
		answer.put("my_name", principal.getAttribute("name"));
		answer.put("my_company", principal.getAttribute("company"));
		return answer;
//		return Collections.singletonMap("name", principal.getAttribute("name"));
	}

	@RequestMapping("/user_details")
	public OAuth2User getUserDetails(@AuthenticationPrincipal OAuth2User principal){
		return principal;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeRequests(a -> a
				.antMatchers("/", "/error", "/webjars/**").permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(e -> e
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.PAYMENT_REQUIRED))
			)
			.csrf(c -> c
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			)
			.logout(l -> l
				.logoutSuccessUrl("/").permitAll()
			).oauth2Login();

		http.authorizeRequests().antMatchers("/book").hasRole("Book");
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialApplication.class, args);
	}

}
