package com.github.mtesmct.rieau.api.domain.entities;

public interface ValueObject<T> {
    boolean hasSameValuesAs(T other);
}