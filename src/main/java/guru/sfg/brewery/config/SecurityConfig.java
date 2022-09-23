package guru.sfg.brewery.config;

import guru.sfg.brewery.security.RestHeaderAuthFilter;
import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
            .csrf().disable();

        ((HttpSecurity)((ExpressionUrlAuthorizationConfigurer.AuthorizedUrl)http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/h2-console/**").permitAll() // do not use in production
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .antMatchers(HttpMethod.DELETE, "/api/v1/beer/**").hasRole("ADMIN")
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll()
                            .mvcMatchers(HttpMethod.GET,"/brewery/breweries")
                                .hasAnyRole("ADMIN","CUSTOMER")
                            .mvcMatchers(HttpMethod.GET,"/brewery/api/v1/breweries")
                                .hasAnyRole("ADMIN","CUSTOMER");
                })
                .authorizeRequests()
                .anyRequest()).authenticated()
                .and()).formLogin()
                .and().httpBasic();

                // h2 console config
                http.headers().frameOptions().sameOrigin();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//        return new LdapShaPasswordEncoder();
//        return new StandardPasswordEncoder();
//        return new BCryptPasswordEncoder();
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{bcrypt}$2a$10$qWAYeQsZmd1aF2r.wjCJ.u/9A2kh3rDinaCSIgoyjXz8k2nIj9JmG")
//                .password("guru")
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}0e342c1c6c9b7aa476df0946fa1dfd64f02bec13c846624b06c28d584ffc15cfd4829f8e1deec4db")
//                .password("$2a$10$.9GwRr9luSZKDCY/bQG2.OGELZRnvWnZXK9Pitp1Kmjiu3Rax.Fxu")
//                .password("caea0368f084ad303c14352a1ab3c81f362eb6aff15516124309a03e658cb4bcab3ce86f6ab47231")
//                .password("{SSHA}BakcEiQm6Gw2KgMDlB9qSubr6BaHrfEJZ1kCnQ==")
//                .password("password")
                .roles("USER");

        auth.inMemoryAuthentication()
                .withUser("scott")
                .password("{ldap}{SSHA}484hWH9aHb7LKxDMSheVHTQ8577GpszTJx1X0A==")
//                .password("tiger")
                .roles("CUSTOMER");
    }*/

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("spring")
//                .password("guru")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        UserDetails scott = User.withDefaultPasswordEncoder()
//                .username("scott")
//                .password("tiger")
//                .roles("COSTUMER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin, user, scott);
//    }

}
