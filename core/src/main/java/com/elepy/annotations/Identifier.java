package com.elepy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that this is the field that is used to determine the identity of this object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Identifier {
    /**
     * If the field is auto generated or not. Use this in coalition with the {@link IdProvider}
     * The CMS won't let you edit the ID if this is true.
     */
    boolean generated() default true;
}
