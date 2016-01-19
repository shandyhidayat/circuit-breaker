package com.arxtecture.microservices.circuitbreaker.model.dto;

import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;

import com.arxtecture.microservices.circuitbreaker.model.constant.CircuitStates;

public class Circuit {
  private static final long INITIAL_FAILURE_COUNT = 0;
  private static final CircuitStates INITIAL_CIRCUIT_STATE = CircuitStates.CLOSED;

  public static Circuit create(String id, int failureCountThreshold, long openTimeInMs) {
    return new Circuit(id, INITIAL_CIRCUIT_STATE, failureCountThreshold, openTimeInMs,
        new AtomicLong(INITIAL_FAILURE_COUNT));
  }
  private String id;
  private CircuitStates state;
  private DateTime lastOpenTime;
  private int failureCountThreshold;
  private long openTimeInMs;

  private AtomicLong failureCount;

  private Circuit(String id, CircuitStates state, int failureCountThreshold, long openTimeInMs,
      AtomicLong failureCount) {
    super();
    this.id = id;
    this.state = state;
    this.failureCountThreshold = failureCountThreshold;
    this.openTimeInMs = openTimeInMs;
    this.failureCount = failureCount;
  }

  public long getFailureCount() {
    return this.failureCount.get();
  }

  public int getFailureCountThreshold() {
    return this.failureCountThreshold;
  }

  public String getId() {
    return this.id;
  }

  public DateTime getLastOpenTime() {
    return this.lastOpenTime;
  }

  public long getOpenTimeInMs() {
    return this.openTimeInMs;
  }

  public CircuitStates getState() {
    return this.state;
  }

  public long incrementAndGetFailureCount() {
    return this.failureCount.incrementAndGet();
  }

  public void resetFailureCount() {
    this.failureCount.set(INITIAL_FAILURE_COUNT);
  }

  public void setFailureCountThreshold(int failureCountThreshold) {
    this.failureCountThreshold = failureCountThreshold;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLastOpenTime(DateTime lastOpenTime) {
    this.lastOpenTime = lastOpenTime;
  }

  public void setOpenTimeInMs(long openTimeInMs) {
    this.openTimeInMs = openTimeInMs;
  }

  public void setState(CircuitStates state) {
    this.state = state;
  }
}
