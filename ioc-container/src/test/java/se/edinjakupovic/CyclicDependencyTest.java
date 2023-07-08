package se.edinjakupovic;

import org.junit.jupiter.api.Test;
import se.edinjakupovic.cycles.CycleDetector;
import se.edinjakupovic.cycles.CyclicDependencyException;
import se.edinjakupovic.testconfigs.configs.CyclicDependencyConfig;
import se.edinjakupovic.testconfigs.configs.DeepCyclicDependencyConfig;
import se.edinjakupovic.testconfigs.configs.TestConfigurationClass;
import se.edinjakupovic.testconfigs.configs.configdependency.cyclicconfigs.CycleConfigA;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CyclicDependencyTest {

    @Test
    void shouldDetect1To1Cycles() {
        assertThatThrownBy(() -> CycleDetector.detectCycles(CyclicDependencyConfig.class))
                .isInstanceOf(CyclicDependencyException.class);
    }

    @Test
    void shouldDetectedLinkedCycles() {
        assertThatThrownBy(() -> CycleDetector.detectCycles(DeepCyclicDependencyConfig.class))
                .isInstanceOf(CyclicDependencyException.class);
    }

    @Test
    void shouldDetectClassConfigDependsOnCycles() {
        assertThatThrownBy(() -> CycleDetector.detectCycles(CycleConfigA.class))
                .isInstanceOf(CyclicDependencyException.class);
    }

    @Test
    void shouldNotThrowOnNoCycle() {
        assertThatCode(() -> CycleDetector.detectCycles(TestConfigurationClass.class))
                .doesNotThrowAnyException();
    }
}
