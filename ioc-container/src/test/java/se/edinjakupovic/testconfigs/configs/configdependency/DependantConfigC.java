package se.edinjakupovic.testconfigs.configs.configdependency;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepC;

@Configuration
public class DependantConfigC {

    @Provides
    DepC depC() {
        return new DepC();
    }
}
