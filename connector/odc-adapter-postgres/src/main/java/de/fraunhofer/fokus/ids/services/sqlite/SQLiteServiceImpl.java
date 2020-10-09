package de.fraunhofer.fokus.ids.services.sqlite;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Vincent Bohlen, vincent.bohlen@fokus.fraunhofer.de
 */
public class SQLiteServiceImpl implements SQLiteService {
    private Logger LOGGER = LoggerFactory.getLogger(de.fraunhofer.fokus.ids.services.database.DatabaseServiceImpl.class.getName());
    private SQLClient jdbc;

    public enum ConnectionType{
        QUERY,
        UPDATE
    }
    public SQLiteServiceImpl(SQLClient dbClient, Handler<AsyncResult<SQLiteService>> readyHandler){
        this.jdbc = dbClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    @Override
    public SQLiteService query(String query, JsonArray params, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        createResult(query, params, ConnectionType.QUERY, resultHandler);
        return this;
    }

    @Override
    public SQLiteService update(String query, JsonArray params, Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        createResult(query, params, ConnectionType.UPDATE, resultHandler);
        return this;
    }

    /**
     * processing pipeline to create the intended result
     * @param queryString SQL Query to perform
     * @param params Query parameters for the SQL query
     * @param connectionType UPDATE or QUERY depending on the type of database manipulation to be performed
     */
    private void createResult(String queryString, JsonArray params, ConnectionType connectionType, Handler<AsyncResult<List<JsonObject>>> resultHandler){

        createConnection(connection -> handleConnection(connection,
                connectionType,
                queryString,
                params,
                result -> handleResult(result,
                        resultHandler
                ),
                resultHandler));
    }

    /**
     * Method to retrieve the connection from the (postgre) SQL client
     * @param next Handler to perform the query (handleQuery or handleQueryWithParams)
     */
    private void createConnection(Handler<AsyncResult<SQLConnection>> next){

        jdbc.getConnection(res -> {
            if (res.succeeded()) {
                next.handle(Future.succeededFuture(res.result()));
            }
            else{
                LOGGER.error("Connection could not be established.\n\n" + res.cause().getMessage());
                next.handle(Future.failedFuture(res.cause().toString()));
            }
        });
    }

    /**
     * Method to call the correct method specified by the connectionType enum.
     * @param result Connection future produced by createConnection
     * @param connectionType UPDATE or QUERY
     * @param queryString SQL String to query
     * @param params params for the SQL query
     * @param next final step of pipeline: handleResult function
     */
    private void handleConnection(AsyncResult<SQLConnection> result,
                                  ConnectionType connectionType,
                                  String queryString,
                                  JsonArray params,
                                  Handler<AsyncResult<List<JsonObject>>> next,
                                  Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        switch (connectionType) {
            case QUERY:
                handleQuery(result, queryString, params,next,resultHandler);
                break;
            case UPDATE:
                handleUpdate(result, queryString, params,resultHandler);
                break;
            default:
                resultHandler.handle(Future.failedFuture("Unknowne Connection type specified."));
        }
    }

    /**
     * Method to perform the SQL query on the connection retrieved via createConnection
     * @param result Connection future produced by createConnection
     * @param queryString SQL String to query
     * @param params params for the SQL query
     * @param next final step of pipeline: handleResult function
     */
    private void handleQuery(AsyncResult<SQLConnection> result,
                             String queryString,
                             JsonArray params,
                             Handler<AsyncResult<List<JsonObject>>> next,
                             Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        if(result.failed()){
            LOGGER.error("Connection Future failed.", result.cause());
            resultHandler.handle(Future.failedFuture(result.cause().toString()));
        }
        else {
            SQLConnection connection = result.result();
            connection.queryWithParams(queryString, params, query -> {
                if (query.succeeded()) {
                    ResultSet rs = query.result();
                    next.handle(Future.succeededFuture(rs.getRows()));
                    connection.close();
                } else {
                    LOGGER.error("Query failed.", query.cause().getMessage());
                    resultHandler.handle(Future.failedFuture(query.cause().toString()));
                    connection.close();
                }
            });
        }
    }

    /**
     * Method to perform the SQL update on the connection retrieved via createConnection
     * @param result Connection future produced by createConnection
     * @param queryString SQL String to query
     * @param params params for the SQL query
     */
    private void handleUpdate(AsyncResult<SQLConnection> result,
                              String queryString,
                              JsonArray params,
                              Handler<AsyncResult<List<JsonObject>>> resultHandler) {

        if(result.failed()){
            LOGGER.error("Connection Future failed.", result.cause());
            resultHandler.handle(Future.failedFuture(result.cause().toString()));
        }
        else {
            SQLConnection connection = result.result();
            connection.updateWithParams(queryString, params, query -> {
                if (query.succeeded()) {
                    LOGGER.info("No. of rows updated: " + query.result().getUpdated());
                    resultHandler.handle(Future.succeededFuture(new ArrayList<>()));
                    connection.close();
                } else {
                    LOGGER.error("Update failed.", query.cause().getMessage());
                    resultHandler.handle(Future.failedFuture(query.cause().toString()));
                    connection.close();
                }
            });
        }
    }

    /**
     * Process the SQL ResultSet (as List<JSONObject>) and reply the results via receivedMessage
     * @param result SQL ResultSet as List<JsonObject>
     */
    private void handleResult(AsyncResult<List<JsonObject>> result, Handler<AsyncResult<List<JsonObject>>> resultHandler){

        if(result.failed()){
            LOGGER.error("List<JsonObject> Future failed.", result.cause());
            resultHandler.handle(Future.failedFuture(result.cause().toString()));
        }
        else {
            resultHandler.handle(Future.succeededFuture(result.result()));
        }
    }



}
