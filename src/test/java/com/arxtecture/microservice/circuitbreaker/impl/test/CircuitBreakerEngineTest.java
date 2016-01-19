package com.arxtecture.microservice.circuitbreaker.impl.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.arxtecture.microservices.circuitbreaker.engine.CircuitBreakerEngine;

public class CircuitBreakerEngineTest {
  private static final String circuitBreakerIdentifier1 = "methodName";
  private CircuitBreakerEngine cbEngine;

  @Test
  public void failedCallBelowFailureCountThreshold_ShouldAllowedToCallMethod_Test() {
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);

    Assert.assertTrue(this.cbEngine.isMethodCallAllowed(circuitBreakerIdentifier1));
  }

  @Test
  public void failedCallExceedFailureCountThreshold_ShouldNotAllowedToCallMethod_Test() {
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);

    Assert.assertFalse(this.cbEngine.isMethodCallAllowed(circuitBreakerIdentifier1));
  }

  @BeforeMethod
  public void init() {
    this.cbEngine = new CircuitBreakerEngine();
    this.cbEngine.putIfCircuitNotExist(circuitBreakerIdentifier1, 3, 1000);
  }

  @Test
  public void initialCall_ShouldAllowedToCallMethod_Test() {
    Assert.assertTrue(this.cbEngine.isMethodCallAllowed(circuitBreakerIdentifier1));
  }

  @Test
  public void successCallOnce_ShouldResetStateToClosed_Test() {
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);
    this.cbEngine.trackFailedCall(circuitBreakerIdentifier1);

    this.cbEngine.trackSuccessCall(circuitBreakerIdentifier1);

    Assert.assertTrue(this.cbEngine.isMethodCallAllowed(circuitBreakerIdentifier1));
  }
}
