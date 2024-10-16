package com.cool.core.security;

import com.cool.core.annotation.TokenIgnore;
import com.cool.core.enums.UserTypeEnum;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

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

    final private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 动态获取忽略的URL
        configureIgnoredUrls();

        return httpSecurity
                .authorizeHttpRequests(
                        conf -> {
                            conf.requestMatchers(
                                            ignoredUrlsProperties.getAdminAuthUrls().toArray(String[]::new))
                                    .permitAll();
                            conf.requestMatchers("/admin/**").authenticated();
                            conf.requestMatchers("/app/**").hasRole(UserTypeEnum.APP.name());
                        })
                .headers(config -> config.frameOptions(FrameOptionsConfig::disable))
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

    private void configureIgnoredUrls() {
        Map<RequestMappingInfo, HandlerMethod> mappings = requestMappingHandlerMapping.getHandlerMethods();
        List<String> handlerCtr = new ArrayList<>();
        mappings.forEach((requestMappingInfo, handlerMethod) -> {
            Method method = handlerMethod.getMethod();
            TokenIgnore tokenIgnore = AnnotatedElementUtils.findMergedAnnotation(method, TokenIgnore.class);
            TokenIgnore tokenIgnoreCtr = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), TokenIgnore.class);
            if (!handlerCtr.contains(handlerMethod.getBeanType().getName()) && tokenIgnoreCtr != null) {
                requestMappingInfo.getPathPatternsCondition().getPatterns().forEach(pathPattern -> {
                    String[] prefixs = pathPattern.getPatternString().split("/");
                    // 去除最后一个路径
                    List<String> urls = new ArrayList<>();
                    for (int i = 0; i < prefixs.length - 1; i++) {
                        urls.add(prefixs[i]);
                    }
                    // 遍历 tokenIgnoreCtr.value()
                    for (String path : tokenIgnoreCtr.value()) {
                        ignoredUrlsProperties.getAdminAuthUrls().add(String.join("/", urls) + "/" + path);
                    }
                    if (tokenIgnoreCtr.value().length == 0) {
                        // 通配
                        ignoredUrlsProperties.getAdminAuthUrls().add(String.join("/", urls)+ "/**");
                    }
                    handlerCtr.add(handlerMethod.getBeanType().getName());
                });
            }
            if (tokenIgnore != null) {
                StringBuilder url = new StringBuilder();
                RequestMapping classRequestMapping = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequestMapping.class);
                if (classRequestMapping != null) {
                    for (String path : classRequestMapping.value()) {
                        url.append(path);
                    }
                }
                if (requestMappingInfo.getPathPatternsCondition() == null) {
                    return;
                }
                for (PathPattern path : requestMappingInfo.getPathPatternsCondition().getPatterns()) {
                    url.append(path);
                }
                ignoredUrlsProperties.getAdminAuthUrls().add(url.toString());
            }
        });
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
