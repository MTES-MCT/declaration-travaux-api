package com.github.mtesmct.rieau.api.infra.date;

import java.time.temporal.Temporal;
import java.util.Optional;

public interface DateConverter<T extends Temporal> {
    String formatDate(T date);
    String formatDateTime(T date);
    String formatYear(T date);
    Optional<T> parse(String date);
}
