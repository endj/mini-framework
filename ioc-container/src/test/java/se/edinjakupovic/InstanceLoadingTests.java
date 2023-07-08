package se.edinjakupovic;

import org.junit.jupiter.api.Test;
import se.edinjakupovic.testconfigs.classes.A;
import se.edinjakupovic.testconfigs.classes.DependsOnAandB;
import se.edinjakupovic.testconfigs.classes.DependsOnB;
import se.edinjakupovic.testconfigs.classes.TestConstants;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepA;
import se.edinjakupovic.testconfigs.classes.dependantconfig.DepC;
import se.edinjakupovic.testconfigs.configs.ConfigClassDependingOnNonConfigClass;
import se.edinjakupovic.testconfigs.configs.TestConfigurationClass;
import se.edinjakupovic.testconfigs.configs.configdependency.DependantConfigA;
import se.edinjakupovic.testconfigs.configs.configdependency.MissingDependencyConfig;
import se.edinjakupovic.testconfigs.configs.configdependency.duplicateproviders.ProvidesA;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InstanceLoadingTests {

    @Test
    void shouldInstantiateSimpleInstanceNoDependencies() {
        ApplicationContext context = ApplicationContext.fromConfiguration(TestConfigurationClass.class);
        A aInstance = context.getInstance(A.class);
        assertThat(aInstance).isNotNull();
        assertThat(aInstance.getNumber()).isEqualTo(TestConstants.A_EXPECTED_METHOD_RETURN_VALUE);
    }

    @Test
    void shouldInstantiateClassWithOneDependency() {
        ApplicationContext context = ApplicationContext.fromConfiguration(TestConfigurationClass.class);
        DependsOnB instance = context.getInstance(DependsOnB.class);
        assertThat(instance).isNotNull();
        assertThat(instance.b()).isNotNull();
        assertThat(instance.b().getNumber()).isEqualTo(TestConstants.B_EXPECTED_METHOD_RETURN_VALUE);
    }

    @Test
    void shouldInstantiateClassWithMultipleDependencies() {
        ApplicationContext context = ApplicationContext.fromConfiguration(TestConfigurationClass.class);
        DependsOnAandB instance = context.getInstance(DependsOnAandB.class);
        assertThat(instance).isNotNull();
        assertThat(instance.b()).isNotNull();
        assertThat(instance.b().getNumber()).isEqualTo(TestConstants.B_EXPECTED_METHOD_RETURN_VALUE);
        assertThat(instance.a()).isNotNull();
        assertThat(instance.a().getNumber()).isEqualTo(TestConstants.A_EXPECTED_METHOD_RETURN_VALUE);

    }

    @Test
    void shouldThrowExceptionIfProvidedClassIsNotConfig() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(Object.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionIfDependentConfigClassNotConfig() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(ConfigClassDependingOnNonConfigClass.class))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void shouldInstantiateClassWithDependenciesInOtherConfigClasses() {
        ApplicationContext context = ApplicationContext.fromConfiguration(DependantConfigA.class);
        DepA a = context.getInstance(DepA.class);

        assertThat(a).isNotNull();
        assertThat(a.depB()).isNotNull();
        assertThat(a.depC()).isNotNull();

        DepC AC = a.depC();
        DepC BC = a.depB().depC();

        assertThat(AC).isNotNull();
        assertThat(BC).isNotNull();

        assertThat(AC).isSameAs(BC);
    }

    @Test
    void shouldFailIfConfigIsMissingForDependantClasses() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(MissingDependencyConfig.class))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldFailIfMultipleProvidersForSameType() {
        assertThatThrownBy(() -> ApplicationContext.fromConfiguration(ProvidesA.class))
                .isInstanceOf(ConfigurationException.class);
    }

}