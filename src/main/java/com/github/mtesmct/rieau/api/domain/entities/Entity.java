package com.github.mtesmct.rieau.api.domain.entities;

public interface Entity<T,ID> {
    ID identity();
    boolean hasSameIdentityAs(T other);
}