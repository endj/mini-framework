package se.edinjakupovic.testconfigs.configs.configdependency;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepA;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepB;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepC;

@Configuration
@DependsOn(dependencies = {DependantConfigB.class, DependantConfigC.class})
public class DependantConfigA {

    @Provides
    DepA depA(DepB depB, DepC depC) {
        return new DepA(depB, depC);
    }
}
