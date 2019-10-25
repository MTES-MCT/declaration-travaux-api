package com.github.mtesmct.rieau.api.infra.date;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DateConverter {
    String format(LocalDateTime date);

    Optional<LocalDateTime> parse(String date);
}
