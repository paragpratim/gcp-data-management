package com.fusadora.model.validator.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * com.fusadora.model.validator.context.ValidationCollector
 * This class is responsible for collecting validation messages that are generated during the evaluation of a data contract. It provides methods to add violations, retrieve the list of violations, and check if there are any violations collected.
 *
 * @author Parag Ghosh
 * @since 21/03/2026
 */
public class ValidationCollector {

    private final List<String> violations = new ArrayList<>();

    public void addViolation(String violation) {
        if (violation != null && !violation.isBlank()) {
            violations.add(violation);
        }
    }

    public List<String> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public boolean isEmpty() {
        return violations.isEmpty();
    }
}


