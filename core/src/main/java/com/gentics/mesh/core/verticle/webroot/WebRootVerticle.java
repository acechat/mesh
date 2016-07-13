package com.gentics.mesh.core.verticle.webroot;

import static io.vertx.core.http.HttpMethod.GET;

import org.jacpfx.vertx.spring.SpringVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gentics.mesh.core.AbstractProjectRestVerticle;
import com.gentics.mesh.rest.Endpoint;

@Component
@Scope("singleton")
@SpringVerticle
public class WebRootVerticle extends AbstractProjectRestVerticle {

	@Autowired
	private WebRootHandler handler;

	public WebRootVerticle() {
		super("webroot");
	}

	@Override
	public void registerEndPoints() throws Exception {
		if (springConfiguration != null) {
			Endpoint endpoint = createEndpoint();
			endpoint.path("/*").handler(springConfiguration.authHandler());
		}
		addErrorHandlers();
		addPathHandler();
	}

	private void addPathHandler() {
		Endpoint endpoint = createEndpoint();
		endpoint.pathRegex("\\/(.*)");
		endpoint.method(GET);
		endpoint.description("Load the node which is located using the provided path.");
		endpoint.handler(rc -> {
			handler.handleGetPath(rc);
		});
	}

	private void addErrorHandlers() {
		Endpoint endpoint = createEndpoint();
		endpoint.path("/error/404");
		endpoint.description("Fallback endpoint for unresolvable links which returns 404.");
		endpoint.handler(rc -> {
			rc.data().put("statuscode", "404");
			rc.next();
		});
	}
}
