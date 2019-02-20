package com.elepy.routes;

import com.elepy.concepts.AtomicIntegrityEvaluator;
import com.elepy.concepts.IntegrityEvaluatorImpl;
import com.elepy.concepts.ObjectEvaluator;
import com.elepy.dao.Crud;
import com.elepy.http.HttpContext;
import com.elepy.http.Response;
import com.elepy.models.ModelDescription;
import com.elepy.utils.ClassUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public class DefaultCreate<T> implements CreateHandler<T> {


    public T create(Response response, T product, Crud<T> dao, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
            objectEvaluator.evaluate(product, clazz);
        }
        new IntegrityEvaluatorImpl<T>().evaluate(product, dao);

        create(response, dao, product);
        return product;
    }


    private void create(Response response, Crud<T> dao, T item) {
        create(response, dao, Collections.singleton(item));
    }

    private void create(Response response, Crud<T> dao, Iterable<T> items) {
        dao.create(items);
        response.status(200);
        response.result("OK");
    }

    public void multipleCreate(Response response, List<T> items, Crud<T> dao, List<ObjectEvaluator<T>> objectEvaluators, Class<T> clazz) throws Exception {
        if (ClassUtils.hasIntegrityRules(dao.getType())) {
            new AtomicIntegrityEvaluator<T>().evaluate(Lists.newArrayList(Iterables.toArray(items, dao.getType())));
        }

        for (T item : items) {

            for (ObjectEvaluator<T> objectEvaluator : objectEvaluators) {
                objectEvaluator.evaluate(item, clazz);
            }
            new IntegrityEvaluatorImpl<T>().evaluate(item, dao);
        }

        create(response, dao, items);
    }

    @Override
    public void handleCreate(HttpContext context, Crud<T> dao, ModelDescription<T> modelDescription, ObjectMapper objectMapper) throws Exception {
        String body = context.request().body();

        try {

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, dao.getType());

            final List<T> ts = objectMapper.readValue(body, type);
            multipleCreate(context.response(), ts, dao, modelDescription.getObjectEvaluators(), modelDescription.getModelType());
        } catch (JsonMappingException e) {

            T item = objectMapper.readValue(body, dao.getType());
            create(context.response(), item, dao, modelDescription.getObjectEvaluators(), modelDescription.getModelType());
        }
    }
}
