package se.edinjakupovic.testconfigs.configs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.Provides;
import se.edinjakupovic.testconfigs.classes.ClassWithInitLifeCycle;
import se.edinjakupovic.testconfigs.classes.ClassWithInitLifeCycleAccessingContext;

@Configuration
public class ConfigWithLifeCycleClass {

    @Provides
    ClassWithInitLifeCycle classWithinitLifeCycle() {
        return new ClassWithInitLifeCycle();
    }

    @Provides
    ClassWithInitLifeCycleAccessingContext classWithInitLifeCycleAccessingContext() {
        return new ClassWithInitLifeCycleAccessingContext();
    }
}
