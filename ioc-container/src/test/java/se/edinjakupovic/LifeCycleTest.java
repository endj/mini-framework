package se.edinjakupovic;

import org.junit.jupiter.api.Test;
import se.edinjakupovic.testconfigs.LifeCycleException;
import se.edinjakupovic.testconfigs.configs.ConfigWithLifeCycleClass;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class LifeCycleTest {

    @Test
    void shouldCallOnInit() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(ConfigWithLifeCycleClass.class))
                .hasRootCauseInstanceOf(LifeCycleException.class);
    }

    @Test
    void shouldBeAbleToAccessContextAfterInit() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(ConfigWithLifeCycleClass.class))
                .hasRootCauseInstanceOf(LifeCycleException.class);
    }
}
