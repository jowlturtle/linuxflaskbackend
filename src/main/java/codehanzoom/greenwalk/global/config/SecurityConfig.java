package codehanzoom.greenwalk.global.config;

import codehanzoom.greenwalk.login.handler.LoginFailureHandler;
import codehanzoom.greenwalk.login.handler.LoginSuccessHandler;
import codehanzoom.greenwalk.login.jwt.JwtAuthenticationProcessingFilter;
import codehanzoom.greenwalk.login.jwt.JwtService;
import codehanzoom.greenwalk.login.customlogin.CustomJsonUsernamePasswordAuthenticationFilter;
import codehanzoom.greenwalk.login.customlogin.CustomUserDetailService;
import codehanzoom.greenwalk.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */

//@configuration : 설정 파일
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoderConfig bCryptPasswordEncoderConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    .formLogin(form -> form.disable())// FormLogin 사용 X
                .httpBasic(httpbasic -> httpbasic.disable()) // httpBasic 사용 X
                .csrf(r -> r.disable())// csrf 보안 사용 X
                .sessionManagement(r -> r.sessionCreationPolicy(SessionCreationPolicy.STATELESS));// 세션 사용하지 않으므로 STATELESS로 설정

        //== URL별 권한 관리 옵션 ==//
        http
                .authorizeHttpRequests
                        (authorizeRequests -> authorizeRequests
                                .requestMatchers("/").permitAll() // "/" 경로에 대한 모든 접근 허용
                                .requestMatchers("/auth/**").permitAll()// 로그인,회원가입 경로
                                //.requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // ADMIN 접속 URL => ADMIN 사용 시 주석 해제
                                .anyRequest().authenticated()); // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능

        // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 CustomUserDetailsService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoderConfig.bCryptPasswordEncoder());
        provider.setUserDetailsService(customUserDetailService);
        return new ProviderManager(provider);
    }


    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    //CustomJsonUsernamePasswordAuthenticationFilter의 역할 = UsernamePasswordAuthenticationFilter
    //AbstractAuthenticationProcessingFilter의 구현체
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter =
                new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);

        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter =
                new JwtAuthenticationProcessingFilter(jwtService, userRepository);

        return jwtAuthenticationFilter;
    }

}
