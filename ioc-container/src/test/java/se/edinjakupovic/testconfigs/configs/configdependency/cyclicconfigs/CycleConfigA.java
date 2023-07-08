package se.edinjakupovic.testconfigs.configs.configdependency.cyclicconfigs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.A;

@Configuration
@DependsOn(dependencies = CycleConfigB.class)
public class CycleConfigA {
    @Provides
    A a() {
        return new A();
    }
}
