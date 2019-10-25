package com.github.mtesmct.rieau.api.domain.services;

import java.time.LocalDateTime;

public interface DateService {
    public LocalDateTime now();
    public String year();
    public String nowText();
    public LocalDateTime parse(String texte);
    public Integer daysUntilNow(LocalDateTime start);
}