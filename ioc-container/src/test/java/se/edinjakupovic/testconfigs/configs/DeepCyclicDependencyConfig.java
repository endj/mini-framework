package se.edinjakupovic.testconfigs.configs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.cyclic.CyclicX;
import se.edinjakupovic.testconfigs.classes.cyclic.CyclicY;
import se.edinjakupovic.testconfigs.classes.cyclic.CyclicZ;

@Configuration
public class DeepCyclicDependencyConfig {


    @Provides
    CyclicX cyclicX(CyclicZ cyclicZ) {
        return new CyclicX(cyclicZ);
    }

    @Provides
    CyclicY cyclicY(CyclicX cyclicX) {
        return new CyclicY(cyclicX);
    }

    @Provides
    CyclicZ cyclicZ(CyclicY cyclicY) {
        return new CyclicZ(cyclicY);
    }
}
