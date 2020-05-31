package main.server.security;

import main.server.security.jwt.JwtSecurityConfigure;
import main.server.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public AuthenticationManager authenticationManageBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/signin").permitAll()
                .antMatchers("/api/auth/signup").permitAll()
                .antMatchers("/api/auth/checkUsername/{username}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/groups").hasRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/api/subjects").hasRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/api/person/{id}").hasRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/api/student/{id}/marks").hasRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/api/group/{id}").hasRole("STUDENT")
                .antMatchers(HttpMethod.POST, "/api/addMark").hasRole("PROFESSOR")
                .antMatchers(HttpMethod.POST, "/api/addGroup").hasRole("MANAGER")
                .antMatchers(HttpMethod.POST, "/api/addPerson").hasRole("MANAGER")
                .antMatchers(HttpMethod.POST, "/api/addSubject").hasRole("MANAGER")
                .antMatchers(HttpMethod.GET, "/api/checkSubjectName/{subjectname}").hasRole("MANAGER")
                .antMatchers(HttpMethod.GET, "/api/checkGroupName/{groupname}").hasRole("MANAGER")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfigure(jwtTokenProvider));
    }
}
