package ec.pymeapps.micro.app.zuul.security.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 *	Con @EnableResourceServer configuramos que vamos a conectarnos a un server de autorizacion
 *
 * 	@RefreshScope nos sirve para que podamos invocar el Actuator para actualizar 
 *   los valores del config server sin reiniciar el servicio
 * 
 * 
 * @author Gabriel Eguiguren
 *
 */

@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceAuthorizationServerConfig extends ResourceServerConfigurerAdapter  {

	// añado para obtener datos del configserver y eliminar de la clase los valores quemados
	// Valor se encuentra en archivo aplication.properties en el repositorio GIT
	@Value("${config.security.oauth.jwt.key}")
	private String keySecret;
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/api/security/oauth/**").permitAll() // se permite que cualquier usuario pueda enviar un form para obtener el token JWT y los otros methodos de oauth 
		.antMatchers(HttpMethod.GET, 
				"/api/productos/listar", 
				"/api/items/listar", 
				"/api/user/usuarios" ).permitAll() // cualquiera puede obtener los listados solo accion 
		.antMatchers(HttpMethod.GET, 
				"/api/productos/ver/{id}", 
				"/api/user/usuarios/{id}").hasAnyRole("ADMIN", "USER") 	// usuarios autenticados
		.antMatchers("/api/productos/**", "/api/items/**", 
				"/api/user/usuarios/**").hasRole("ADMIN") 	// POST, PUT, DELETE solo para administradores
		.anyRequest().authenticated()    //cualquier otra ruta no especificada aqui requiere autenticacion
		.and().cors().configurationSource(corsConfigurationSource()); // CORS para que otros servicios en otro domain pueda comunicarse              						
	}
	
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}


	/**
	 * Aqui defino el JWT y la clave privada para generar el TOKEN
	 * 
	 * @return
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		// esta frase es la misma que esta en el AuthorizationServerConfig
		// tokenConverter.setSigningKey("frase_clave_secreta_para_validar");    Local 
		tokenConverter.setSigningKey(keySecret);    // con config server
		
		return tokenConverter;
	}

	/**
	 * Configura que dominios externos podran acceder a nuestros endpoints,
	 * por ejemplos clientes de Angular, React, etc
	 * 
	 * @return
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration corsConfig = new CorsConfiguration();
		
		corsConfig.setAllowedOrigins(Arrays.asList("*")); // por el momento se ponen todos, pero deberia ir un dominio: http://google/api
		// metodos autorizados, donde "OPTIONS" es importante por que lo usa oauth2
		corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "OPTIONS"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		source.registerCorsConfiguration("/**", corsConfig); // Para que se aplique a todos nuestros ENDPOINTS: "/**"
		return source;
	}
	
	/**
	 * Registra la configuracion de arriba en un Filter Bean
	 * 
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		FilterRegistrationBean<CorsFilter> bean 
		= new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
		
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
				
		return bean;
	}
	
	
}
