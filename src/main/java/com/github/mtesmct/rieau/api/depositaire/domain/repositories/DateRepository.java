package com.github.mtesmct.rieau.api.depositaire.domain.repositories;

import java.util.Date;

public interface DateRepository {
    public Date now();
    public String nowText();
}