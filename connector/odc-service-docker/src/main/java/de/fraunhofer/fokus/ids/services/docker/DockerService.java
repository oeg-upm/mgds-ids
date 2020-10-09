package de.fraunhofer.fokus.ids.services.docker;

import com.github.dockerjava.api.DockerClient;
import de.fraunhofer.fokus.ids.services.database.*;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Vincent Bohlen, vincent.bohlen@fokus.fraunhofer.de
 */
@ProxyGen
@VertxGen
public interface DockerService {

    @Fluent
    DockerService findImages(Handler<AsyncResult<JsonArray>> resultHandler);

    @Fluent
    DockerService startContainer(String imageId, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    DockerService stopContainer(JsonArray containerIds, Handler<AsyncResult<JsonArray>> resultHandler);

    @GenIgnore
    static DockerService create(DatabaseService databaseService, DockerClient dockerClient, Handler<AsyncResult<DockerService>> readyHandler) {
        return new DockerServiceImpl(databaseService, dockerClient, readyHandler);
    }

    @GenIgnore
    static DockerService createProxy(Vertx vertx, String address) {
        return new DockerServiceVertxEBProxy(vertx, address);
    }

}
