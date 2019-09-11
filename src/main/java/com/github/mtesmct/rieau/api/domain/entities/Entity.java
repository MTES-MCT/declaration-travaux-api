package com.github.mtesmct.rieau.api.domain.entities;
/**
 * <p>
 * A DDD entity is an object that have a distinct identity that runs through time and different representations.
 * </p>
 *
 * @see <a href= "https://martinfowler.com/bliki/EvansClassification.html">
 *  Entity in Domain Driven Design
 * </a>
 */
public interface Entity<T,ID> {
    ID identity();
    boolean hasSameIdentityAs(T other);
}