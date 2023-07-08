package se.edinjakupovic.testconfigs.configs;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.DependsOn;

@Configuration
@DependsOn(dependencies = {NonConfigClass.class})
public class ConfigClassDependingOnNonConfigClass {
}
