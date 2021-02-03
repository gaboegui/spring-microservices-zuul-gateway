package ec.pymeapps.micro.app.zuul.security.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

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
		.anyRequest().authenticated();             //cualquier otra ruta no especificada aqui requiere autenticacion 						
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

	
	
}
