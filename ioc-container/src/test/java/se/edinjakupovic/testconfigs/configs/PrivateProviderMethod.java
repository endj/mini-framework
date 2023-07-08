package se.edinjakupovic.testconfigs.configs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.A;

@Configuration
public class PrivateProviderMethod {

    @Provides
    private A a() {
        return new A();
    }
}
