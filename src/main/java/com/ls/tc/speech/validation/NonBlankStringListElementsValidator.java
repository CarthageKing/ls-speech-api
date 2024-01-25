package com.ls.tc.speech.validation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NonBlankStringListElementsValidator implements ConstraintValidator<NonBlankStringListElements, List<String>> {

	private int min;
	private int max;

	public NonBlankStringListElementsValidator() {
		// noop
	}

	@Override
	public void initialize(NonBlankStringListElements constraintAnnotation) {
		min = constraintAnnotation.min();
		max = constraintAnnotation.max();
	}

	// https://stackoverflow.com/a/33544604
	@Override
	public boolean isValid(List<String> list, ConstraintValidatorContext context) {
		if (null == list || list.isEmpty()) {
			return true;
		}
		context.disableDefaultConstraintViolation();
		boolean overallValid = true;
		int index = -1;
		for (String v : list) {
			index++;
			if (StringUtils.isBlank(v)) {
				context.buildConstraintViolationWithTemplate("entry at index " + index + " must not be null or completely blank").addConstraintViolation();
				overallValid &= false;
				continue;
			}
			if (v.length() < min) {
				context.buildConstraintViolationWithTemplate("entry at index " + index + " must have minimum length of " + min).addConstraintViolation();
				overallValid &= false;
			}
			if (v.length() > max) {
				context.buildConstraintViolationWithTemplate("entry at index " + index + " must have maximum length of " + max).addConstraintViolation();
				overallValid &= false;
			}
		}
		return overallValid;
	}
}
