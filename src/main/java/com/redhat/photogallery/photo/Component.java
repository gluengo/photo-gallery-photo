package com.redhat.photogallery.photo;

import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;

public interface Component {

	void registerRoutes(Router router);

	void injectEventBus(EventBus eventBus);
}