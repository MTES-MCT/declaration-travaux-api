package com.github.mtesmct.rieau.api.domain.services;

import java.lang.annotation.*;

/**
 * <p>
 * A DDD domain service is a standalone operation within the context of your domain. 
 * A Service Object collects one or more services into an object. 
 * Typically you will have only one instance of each service object type within your execution context
 * </p>
 *
 * @see <a href= "https://martinfowler.com/bliki/EvansClassification.html">
 *  Domain service in Domain Driven Design
 * </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DomainService {

}