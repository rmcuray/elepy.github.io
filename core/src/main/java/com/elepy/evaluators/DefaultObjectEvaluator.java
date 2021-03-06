package com.elepy.evaluators;

import com.elepy.exceptions.ElepyException;
import com.elepy.models.FieldType;
import com.elepy.models.Property;
import com.elepy.models.options.ArrayOptions;
import com.elepy.models.options.DateOptions;
import com.elepy.models.options.NumberOptions;
import com.elepy.utils.ModelUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DefaultObjectEvaluator<T> implements ObjectEvaluator<T> {

    @Override
    public void evaluate(Object o) throws Exception {
        Class c = o.getClass();
        evaluateObject(o, c);

    }

    private void evaluateObject(Object o, Class c) throws Exception {
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            var fieldDescriber = ModelUtils.describeAccessibleObject(field);

            if (fieldDescriber.getType().equals(FieldType.OBJECT)) {
                if (field.get(o) != null)
                    evaluateObject(field.get(o), field.getType());
            } else {
                checkProperty(field.get(o), fieldDescriber);
            }
        }
    }

    private void checkRequired(Object obj, Property property) {
        if (property.isRequired() && (obj == null || (obj instanceof Date && ((Date) obj).getTime() < 1000) || (obj instanceof String && ((String) obj).isEmpty()))) {
            throw new ElepyException(property.getLabel() + " is blank, please fill it in!");
        }
    }

    private void checkProperty(Object obj, Property property) throws Exception {
        checkRequired(obj, property);

        if (property.getType().equals(FieldType.NUMBER)) {

            checkNumberConfig(obj, property.getOptions(), property.getLabel());
        }
        if (property.getType().equals(FieldType.DATE)) {
            checkDateConfig(obj, property.getOptions(), property.getLabel());
        }

        if (property.getType().equals(FieldType.ARRAY)) {
            checkArray(obj, property);
        }

    }

    private void checkArray(Object obj, Property property) throws Exception {

        Collection collection = (Collection) obj;
        final Object[] objects = (collection == null ? List.of() : collection).toArray();

        final ArrayOptions options = property.getOptions();


        final int maximumArrayLength = options.getMaximumArrayLength();
        final int minimumArrayLength = options.getMinimumArrayLength();

        if (objects.length > maximumArrayLength || objects.length < minimumArrayLength) {
            throw new ElepyException(String.format("%s can only consist of between  %d and %d items, was %d", property.getLabel(), minimumArrayLength, maximumArrayLength, objects.length), 400);
        }
        for (Object arrayObject : objects) {
            switch (options.getArrayType()) {
                case DATE:
                    checkDateConfig(arrayObject, (DateOptions) options.getGenericOptions(), property.getLabel());
                    break;
                case NUMBER:
                    checkNumberConfig(arrayObject, (NumberOptions) options.getGenericOptions(), property.getLabel());
                    break;
                case OBJECT:
                    evaluateObject(arrayObject, arrayObject.getClass());
                    break;

            }
        }

    }

    private void checkNumberConfig(Object obj, NumberOptions numberAnnotation, String label) {
        if (obj == null) {
            obj = 0;
        }
        if (!(obj instanceof Number)) {
            throw new ElepyException(label + " must be a number");
        }
        Number number = (Number) obj;

        if (number.floatValue() > numberAnnotation.getMaximum() || number.floatValue() < numberAnnotation.getMinimum()) {
            throw new ElepyException(String.format("%s must be between %d and %d, was %d", label, (int) numberAnnotation.getMinimum(), (int) numberAnnotation.getMaximum(), number.intValue()));
        }
    }

    private void checkDateConfig(Object obj, DateOptions dateTimeAnnotation, String label) {
        Date date = obj == null ? new Date(0) : (Date) obj;

        Date min = dateTimeAnnotation.getMinimumDate();
        Date max = dateTimeAnnotation.getMaximumDate();
        if (date.before(min) || date.after(max)) {
            throw new ElepyException(String.format("%s must be between '%s' and '%s'", label, dateTimeAnnotation.getMinimumDate(), dateTimeAnnotation.getMaximumDate()));
        }
    }
}
