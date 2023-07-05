package com.example.bacourse.config;

import com.example.bacourse.security.CustomUserDetailsService;
import com.example.bacourse.security.JwtAuthenticationEntryPoint;
import com.example.bacourse.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // primary spring security annotation
@EnableGlobalMethodSecurity(//enable method level security based on annotations
        securedEnabled = true,//@secured for protecting controller/service methods
        jsr250Enabled = true,//@RolesAllowed
        prePostEnabled = true //@PreAuthorize or PostAuthorize
)

//Default security config & overrides of existing methods
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    // service which implements UserDetailsService interface wich loads the user details by username
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    //return 401 to clients that try to access a protected resource without proper authentication
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    //reads JWT authentication token from the Authorization header of all the requests
    //validates the token
    //loads the user details associated with that token.
    //sets the user details in Spring Security’s SecurityContext. Spring Security uses the user details to perform authorization checks. We can also access the user details stored in the SecurityContext in our controllers to perform our business logic
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(name=BeanIds.AUTHENTICATION_MANAGER)
    @Override
    //authenticate a user in login API
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

  

    @Override
    //permitting access to static resources and few other public APIs to everyone and restricting access to other APIs to authenticated users only
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers("/",
                        "/favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js")
                        .permitAll()
                    .antMatchers("/api/auth/**")
                        .permitAll()
                    .antMatchers("/api/user/checkEmailAvailability")
                        .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/polls/**", "/api/users/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated();

        // Add our custom JWT security filter
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
