package se.edinjakupovic.testconfigs.configs.configdependency;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepA;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepB;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepC;

@Configuration
public class MissingDependencyConfig {

    @Provides
    DepA depA(DepB b, DepC c) {
        return new DepA(b, c);
    }
}
