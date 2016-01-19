package com.arxtecture.microservices.circuitbreaker.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arxtecture.microservices.circuitbreaker.annotation.CircuitBreaker;
import com.arxtecture.microservices.circuitbreaker.engine.CircuitBreakerEngine;

public class CircuitBreakerInterceptor implements MethodInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerInterceptor.class);

  private CircuitBreakerEngine circuitBreakerEngine;

  public CircuitBreakerInterceptor() {
    this.circuitBreakerEngine = new CircuitBreakerEngine();
  }

  public Object invoke(MethodInvocation invocation) throws Throwable {
    CircuitBreaker circuitBreaker = invocation.getMethod().getAnnotation(CircuitBreaker.class);

    String circuitId = invocation.getMethod().getName();

    if (circuitBreaker.value() != null) {
      circuitId = circuitBreaker.value();
    }

    Class<? extends Exception>[] targetExceptionClasses = circuitBreaker.targetExceptions();

    LOGGER.debug("Execute method : {}", circuitId);
    Object responseObject = null;

    this.circuitBreakerEngine.putIfCircuitNotExist(circuitId,
        circuitBreaker.failureCountThreshold(), circuitBreaker.openTimeinMs());

    if (this.circuitBreakerEngine.isMethodCallAllowed(circuitId)) {
      try {
        responseObject = invocation.proceed();
      } catch (Exception e) {
        for (Class<? extends Exception> targetExceptionClass : targetExceptionClasses) {
          if (targetExceptionClass.equals(e.getClass())) {

            LOGGER.debug("Error Message : {}", e.getMessage());

            this.circuitBreakerEngine.trackFailedCall(circuitId);
          }
        }

        throw e;
      }

      this.circuitBreakerEngine.trackSuccessCall(circuitId);
    } else {
      this.circuitBreakerEngine.switchCircuitStateToHalfOpenWhenReady(circuitId);
    }

    return responseObject;
  }
}
