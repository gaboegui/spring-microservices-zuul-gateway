package ec.pymeapps.micro.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PostTiempoTranscurridoFilter extends ZuulFilter {
	
	private static Logger log = LoggerFactory.getLogger(PostTiempoTranscurridoFilter.class);

	@Override
	public boolean shouldFilter() {
		// aqui se puede poner una condicion para que se ejecute o no, 
		// en nuestro ejemplo se ejecutara siempre
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		log.info("Entrando a FilterPost...");
		
		Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
		Long tiempoFin = System.currentTimeMillis();
		Long TiempoTotalEjecucion = tiempoFin - tiempoInicio;
		
		log.info("Tiempo de Ejecucion del Request en milisegundos: " + TiempoTotalEjecucion.toString() );
		
		
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 2;
	}

}
