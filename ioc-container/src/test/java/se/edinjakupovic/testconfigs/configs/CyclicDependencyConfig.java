package se.edinjakupovic.testconfigs.configs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.cyclic.CyclicA;
import se.edinjakupovic.testconfigs.classes.cyclic.CyclicB;

@Configuration
public class CyclicDependencyConfig {

    @Provides
    CyclicA a(CyclicB b) {
        return new CyclicA(b);
    }

    @Provides
    CyclicB b(CyclicA a) {
        return new CyclicB(a);
    }


}
