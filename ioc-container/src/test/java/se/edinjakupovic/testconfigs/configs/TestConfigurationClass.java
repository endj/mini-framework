package se.edinjakupovic.testconfigs.configs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.A;
import se.edinjakupovic.testconfigs.classes.B;
import se.edinjakupovic.testconfigs.classes.DependsOnAandB;
import se.edinjakupovic.testconfigs.classes.DependsOnB;

@Configuration
public class TestConfigurationClass {
    @Provides
    A a() {
        return new A();
    }

    @Provides
    B b() {
        return new B();
    }

    @Provides
    DependsOnB dependsOnB(B b) {
        return new DependsOnB(b);
    }

    @Provides
    DependsOnAandB dependsOnAandB(A a, B b) {
        return new DependsOnAandB(a, b);
    }

}
