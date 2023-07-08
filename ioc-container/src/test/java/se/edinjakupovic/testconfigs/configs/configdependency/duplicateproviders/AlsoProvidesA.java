package se.edinjakupovic.testconfigs.configs.configdependency.duplicateproviders;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.A;

@Configuration
public class AlsoProvidesA {
    @Provides
    A a() {
        return new A();
    }
}
