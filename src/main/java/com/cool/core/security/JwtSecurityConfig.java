package com.cool.core.security;

import com.cool.modules.base.security.JwtAuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.DigestUtils;

@EnableWebSecurity
@Configuration
@Slf4j
@RequiredArgsConstructor
public class JwtSecurityConfig {

	// 用户详情
	final private UserDetailsService userDetailsService;

	final private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
	// 401
	final private EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
	// 403
	final private RestAccessDeniedHandler restAccessDeniedHandler;
	// 忽略权限控制的地址
	final private IgnoredUrlsProperties ignoredUrlsProperties;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
			.authorizeHttpRequests(
				conf -> conf.requestMatchers(
						ignoredUrlsProperties.getUrls().toArray(String[]::new))
					.permitAll().anyRequest().authenticated())
			.headers(config -> config.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			// 允许网页iframe
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(jwtAuthenticationTokenFilter,
				UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(config -> {
				config.authenticationEntryPoint(entryPointUnauthorizedHandler);
				config.accessDeniedHandler(restAccessDeniedHandler);
			}).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(CharSequence rawPassword) {
				return DigestUtils.md5DigestAsHex(((String) rawPassword).getBytes());
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return encodedPassword.equals(
					DigestUtils.md5DigestAsHex(((String) rawPassword).getBytes()));
			}
		};
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
		throws Exception {
		return config.getAuthenticationManager();
	}
}
