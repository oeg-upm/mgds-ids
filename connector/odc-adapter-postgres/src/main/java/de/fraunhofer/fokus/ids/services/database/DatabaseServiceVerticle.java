package de.fraunhofer.fokus.ids.services.database;

import de.fraunhofer.fokus.ids.services.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import io.vertx.serviceproxy.ServiceBinder;
/**
 * @author Vincent Bohlen, vincent.bohlen@fokus.fraunhofer.de
 */
public class DatabaseServiceVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {

        DatabaseService.create(vertx, ready -> {
            if (ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                        .setAddress(Constants.DATABASE_SERVICE)
                        .register(DatabaseService.class, ready.result());
                startFuture.complete();
            } else {
                startFuture.fail(ready.cause());
            }
        });
    }

}
