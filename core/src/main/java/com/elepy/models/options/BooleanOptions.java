package com.elepy.models.options;

import com.elepy.annotations.TrueFalse;

import java.lang.reflect.AnnotatedElement;

public class BooleanOptions implements Options {

    private String trueValue;
    private String falseValue;

    private BooleanOptions(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }


    public static BooleanOptions of(AnnotatedElement field) {
        final TrueFalse annotation = com.elepy.utils.Annotations.get(field,TrueFalse.class);
        return new BooleanOptions(
                annotation == null ? "true" : annotation.trueValue(),
                annotation == null ? "false" : annotation.falseValue()
        );
    }

    public String getFalseValue() {
        return falseValue;
    }

    public String getTrueValue() {
        return trueValue;
    }
}
