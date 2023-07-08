package se.edinjakupovic.testconfigs.configs.configdependency.cyclicconfigs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.C;

@Configuration
@DependsOn(dependencies = CycleConfigA.class)
public class CycleConfigC {

    @Provides
    C c() {
        return new C();
    }
}
