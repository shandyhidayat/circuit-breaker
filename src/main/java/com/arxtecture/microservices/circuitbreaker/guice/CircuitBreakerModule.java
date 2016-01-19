package com.arxtecture.microservices.circuitbreaker.guice;

import com.arxtecture.microservices.circuitbreaker.annotation.CircuitBreaker;
import com.arxtecture.microservices.circuitbreaker.aop.interceptor.CircuitBreakerInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class CircuitBreakerModule extends AbstractModule {

  @Override
  protected void configure() {
    this.bindInterceptor(Matchers.any(), Matchers.annotatedWith(CircuitBreaker.class),
        new CircuitBreakerInterceptor());
  }
}
