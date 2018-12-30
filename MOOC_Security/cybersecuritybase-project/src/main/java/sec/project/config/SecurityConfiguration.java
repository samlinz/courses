package sec.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // No real security at the moment.
        http.authorizeRequests()
                .anyRequest().permitAll();

        // Explicitly disable csrf protection.
        http.csrf().disable();

        // Sessions are handled by custom code, disable Spring's own stuff.
        http.sessionManagement().disable();
    }
}
