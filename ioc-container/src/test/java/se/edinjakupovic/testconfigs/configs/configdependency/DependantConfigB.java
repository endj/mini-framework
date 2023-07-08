package se.edinjakupovic.testconfigs.configs.configdependency;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepB;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepC;

@Configuration
@DependsOn(dependencies = {DependantConfigC.class})
public class DependantConfigB {

    @Provides
    DepB depB(DepC depC) {
        return new DepB(depC);
    }
}
