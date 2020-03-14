package com.elepy.models.options;

import com.elepy.annotations.Input;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class InputOptions implements Options {
    private final String type;

    public InputOptions(String type) {
        this.type = type;
    }

    public static InputOptions of(AnnotatedElement accessibleObject) {
        final var type = Optional.ofNullable(accessibleObject.getAnnotation(Input.class))
                .map(Input::type)
                .orElse("text");
        return new InputOptions(
                type
        );
    }

    public String getType() {
        return type;
    }
}
