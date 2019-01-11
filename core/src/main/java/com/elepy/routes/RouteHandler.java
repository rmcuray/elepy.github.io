package com.elepy.routes;

import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.di.ElepyContext;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * The base of all Elepy Routes. It can be used to override the functionality of Elepy.
 *
 * @param <T> The model you're handling
 * @see com.elepy.annotations.Create
 * @see com.elepy.annotations.Find
 * @see com.elepy.annotations.Update
 * @see com.elepy.annotations.Delete
 * @see DefaultCreate
 * @see DefaultFind
 * @see DefaultUpdate
 * @see DefaultDelete
 */
public interface RouteHandler<T> {

    /**
     * This handles the functionality of a specific elepy route.
     *
     * @param request          The spark request
     * @param response         The spark response
     * @param crud             The crud implementation
     * @param elepy            The elepy context
     * @param objectEvaluators The list of evaluators
     * @param clazz            The class type
     * @throws Exception you can throw any exception and Elepy handles them nicely.
     * @see com.elepy.exceptions.ElepyException
     * @see com.elepy.exceptions.ElepyErrorMessage
     */
    void handle(Request request, Response response, Crud<T> crud, ElepyContext elepy, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception;
}
