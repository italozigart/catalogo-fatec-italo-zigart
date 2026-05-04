package br.com.fatec.catalogo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/categorias/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios/**").hasRole("ADMIN") // Somente Admin cadastra novos usuários
                        .requestMatchers("/produtos").permitAll() // Público
                        .requestMatchers("/produtos/novo", "/produtos/editar/**", "/produtos/excluir/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Define a rota da página customizada
                        .defaultSuccessUrl("/produtos", true)
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/produtos"));
                

        return http.build();
    }

//@Bean
//public InMemoryUserDetailsManager userDetailsService() {
//    // Administrador (Escrita)
//    UserDetails admin = User.builder()
//            .username("admin")
//            .password("{noop}12345") // {noop} indica senha sem criptografia para fins didáticos
//            .roles("ADMIN")
//            .build();
//
//    // Usuário Comum (Leitura)
//    UserDetails user = User.builder()
//            .username("aluno")
//            .password("{noop}12345")
//            .roles("USER")
//            .build();
//
//    return new InMemoryUserDetailsManager(user, admin);
//}
}