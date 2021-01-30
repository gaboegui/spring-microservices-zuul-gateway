package ec.pymeapps.micro.app.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


/**
 * @EnableEurekaClient para que se registre tambien con eureka
 * @EnableZuulProxy    para que se habilite
 * 
 * Al habilitar Zuul Proxy se crea un gateway unico para acceder a todos los servicios:
 * y las rutas, y timeouts se las configura en el aplication.properties 
 * 
 * Los filtros sirven para ejecutar ciertas pre o post condiciones
 * 
 * http://localhost:8090/api/items/ver/1/cantidad/3
 * http://localhost:8090/api/productos/ver/1
 * 
 * 
 * @author Gabriel Eguiguren
 *
 */

@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class UdeMicroServiceZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UdeMicroServiceZuulServerApplication.class, args);
	}

}
