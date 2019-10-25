package com.github.mtesmct.rieau.api.domain.entities;

import java.lang.annotation.*;

/**
 * <p>
 * A DDD factory is an object that encapsulates complex aggregates creations.
 * </p>
 *
 * @see <a href= "https://martinfowler.com/bliki/EvansClassification.html">
 *      Factories in Domain Driven Design </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Factory {

}