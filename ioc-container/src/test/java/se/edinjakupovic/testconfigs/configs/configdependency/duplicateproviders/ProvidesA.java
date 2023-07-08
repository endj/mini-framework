package se.edinjakupovic.testconfigs.configs.configdependency.duplicateproviders;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.A;

@Configuration
@DependsOn(dependencies = {AlsoProvidesA.class})
public class ProvidesA {
    @Provides
    A a() {
        return new A();
    }
}
