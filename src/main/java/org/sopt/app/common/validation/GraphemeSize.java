package org.sopt.app.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.BreakIterator;
import java.util.Locale;

@Documented
@Constraint(validatedBy = GraphemeSize.Validator.class)
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.PARAMETER,
    ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphemeSize {

    String message() default "글자 수가 허용 범위를 벗어났습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    class Validator implements ConstraintValidator<GraphemeSize, String> {

        private int min;
        private int max;

        @Override
        public void initialize(GraphemeSize constraintAnnotation) {
            this.min = constraintAnnotation.min();
            this.max = constraintAnnotation.max();
            if (min < 0 || max < min) {
                throw new ConstraintDeclarationException("min은 0 이상이고 max 이하여야 합니다.");
            }
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            BreakIterator iterator = BreakIterator.getCharacterInstance(Locale.ROOT);
            iterator.setText(value);

            int graphemeCount = 0;
            while (iterator.next() != BreakIterator.DONE) {
                if (++graphemeCount > max) {
                    return false;
                }
            }
            return graphemeCount >= min;
        }
    }
}
