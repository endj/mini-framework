package se.edinjakupovic;

import org.junit.jupiter.api.Test;
import se.edinjakupovic.testconfigs.configs.PrivateProviderMethod;
import se.edinjakupovic.testconfigs.configs.StaticProviderMethod;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProviderMethodsTests {

    @Test
    void shouldThrowExceptionIfProviderMethodPrivate() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(PrivateProviderMethod.class))
                .isInstanceOf(ConfigurationException.class);
    }

    @Test
    void shouldThrowExceptionIfProviderMethodStatic() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(StaticProviderMethod.class))
                .isInstanceOf(ConfigurationException.class);
    }
}
