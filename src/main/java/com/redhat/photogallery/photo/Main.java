package com.redhat.photogallery.photo;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;

public class Main {

	public static void main(String[] args) {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new PhotoServer(8080, new PhotoService()));
	}
}

class PhotoServer extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(PhotoServer.class);
	private final int listenPort;
	private final Service[] services;

	public PhotoServer(int listenPort, Service... services) {
		this.listenPort = listenPort;
		this.services = services;
	}

	@Override
	public void start() {
		Router router = Router.router(vertx);
		EventBus eventBus = vertx.eventBus();
		for (Service service : services) {
			service.registerRoutes(router);
			service.injectEventBus(eventBus);
		}
		vertx.createHttpServer().requestHandler(router).rxListen(listenPort).subscribe(this::listenSuccess,
				this::listenError);
	}

	private void listenSuccess(HttpServer httpServer) {
		LOG.info("Server is listening on port {}", httpServer.actualPort());
	}

	private void listenError(Throwable t) {
		LOG.error("Server failed to listen on port {}", listenPort, t);
		vertx.close();
	}

}