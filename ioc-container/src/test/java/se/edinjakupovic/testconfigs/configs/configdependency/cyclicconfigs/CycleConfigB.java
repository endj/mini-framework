package se.edinjakupovic.testconfigs.configs.configdependency.cyclicconfigs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.B;

@Configuration
@DependsOn(dependencies = CycleConfigC.class)
public class CycleConfigB {

    @Provides
    B b() {
        return new B();
    }
}
