package com.ls.tc.speech.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = NonBlankStringListElementsValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NonBlankStringListElements {

	String message() default "list must contain non-null, non-blank entries following length restrictions";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	// minimum length of any string in the list
	int min() default 0;

	// max length of any string in the list
	int max() default Integer.MAX_VALUE;
}
