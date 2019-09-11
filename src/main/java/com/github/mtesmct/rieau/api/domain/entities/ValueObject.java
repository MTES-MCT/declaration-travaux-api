package com.github.mtesmct.rieau.api.domain.entities;
/**
 * <p>
 * A DDD value object is an object that matters only as the combination of its attributes. 
 * Two value objects with the same values for all their attributes are considered equal.
 * </p>
 *
 * @see <a href= "https://martinfowler.com/bliki/EvansClassification.html">
 *  Value Object in Domain Driven Design
 * </a>
 */
public interface ValueObject<T> {
    boolean hasSameValuesAs(T other);
}