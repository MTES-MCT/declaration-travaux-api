package com.github.mtesmct.rieau.api.application;

import java.lang.annotation.*;
/**
 * <p>
 * A DDD application service is a standalone operation within the context of your application. 
 * A Service Object collects one or more services into an object. 
 * Typically you will have only one instance of each service object type within your execution context
 * </p>
 *
 * @see <a href= "https://martinfowler.com/bliki/EvansClassification.html">
 *  Application service in Domain Driven Design
 * </a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ApplicationService {

}