package com.arxtecture.microservices.circuitbreaker.engine;

import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arxtecture.microservices.circuitbreaker.model.constant.CircuitStates;
import com.arxtecture.microservices.circuitbreaker.model.dto.Circuit;

public class CircuitBreakerEngine {

  // TODO code formatting convention

  private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerEngine.class);

  private ConcurrentHashMap<String, Circuit> circuits = new ConcurrentHashMap<String, Circuit>();

  private Circuit getCircuit(final String circuitId) {
    return this.circuits.get(circuitId);
  }

  private boolean isCircuitInOpenState(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    return circuit.getState() == CircuitStates.OPEN ? true : false;
  }

  public boolean isMethodCallAllowed(final String circuitId) {
    return !this.isCircuitInOpenState(circuitId);
  }

  public void putIfCircuitNotExist(final String circuitId, final int failureCountThreshold,
      final long openTimeInMs) {
    if (!this.circuits.containsKey(circuitId)) {
      Circuit circuit = Circuit.create(circuitId, failureCountThreshold, openTimeInMs);
      this.circuits.put(circuitId, circuit);
    }
  }

  private void switchCircuitStateToClose(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    if (circuit != null) {
      circuit.setState(CircuitStates.CLOSED);
    }

    LOGGER.debug("Circuit {}, Switch State to {}", circuitId, CircuitStates.CLOSED);
  }

  private void switchCircuitStateToHalfOpen(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    circuit.setState(CircuitStates.HALF_OPEN);

    LOGGER.debug("Circuit {}, Switch State to {}", circuitId, CircuitStates.HALF_OPEN);
  }

  public void switchCircuitStateToHalfOpenWhenReady(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    if (CircuitStates.OPEN.equals(circuit.getState())) {
      Duration elapsedTime = new Duration(circuit.getLastOpenTime(), new DateTime());
      long elapsedTimeInMs = elapsedTime.getMillis();

      LOGGER.debug("Circuit {}, Elapsed Time Since Last Circuit Open : {} Milliseconds", circuitId,
          elapsedTimeInMs);

      if (elapsedTimeInMs > circuit.getOpenTimeInMs()) {
        this.switchCircuitStateToHalfOpen(circuitId);
      }
    }
  }

  private void switchCircuitStateToOpen(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    circuit.setState(CircuitStates.OPEN);
    circuit.setLastOpenTime(new DateTime());

    LOGGER.debug("Circuit {}, Switch State to {}", circuitId, CircuitStates.OPEN);
  }

  public void trackFailedCall(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    long currentFailureCount = circuit.incrementAndGetFailureCount();

    LOGGER.debug("Circuit {}, Failure Count : {}", circuitId, currentFailureCount);

    if (currentFailureCount >= circuit.getFailureCountThreshold()) {
      this.switchCircuitStateToOpen(circuitId);
    }

    LOGGER.debug("Circuit {}, Failed Call", circuitId);
  }

  public void trackSuccessCall(final String circuitId) {
    Circuit circuit = this.getCircuit(circuitId);

    circuit.resetFailureCount();
    this.switchCircuitStateToClose(circuitId);

    LOGGER.debug("Circuit {}, Success Call", circuitId);
  }
}
