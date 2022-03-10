package io.gaddings.backofficeservice;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "io.gaddings.backofficeservice", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

    @ArchTest
    void hexagonalArchitecture(JavaClasses classes) {
        layeredArchitecture()
            .layer("adapter").definedBy("io.gaddings.backofficeservice.adapter..")
            .layer("application").definedBy("io.gaddings.backofficeservice.application..")
            .layer("domain").definedBy("io.gaddings.backofficeservice.domain..")
            .whereLayer("adapter").mayNotBeAccessedByAnyLayer()
            .whereLayer("application").mayOnlyBeAccessedByLayers("adapter")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("adapter", "application")
            .check(classes);
    }

    @ArchTest
    static final ArchRule FREE_OF_CYCLES = slices().matching("io.gaddings.backofficeservice.(*)..")
        .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule ADAPTERS_DO_NOT_DEPEND_ON_ONE_ANOTHER = slices()
        .matching("io.gaddings.backofficeservice.adapter..").namingSlices("$1 '$2'")
        .should().notDependOnEachOther()
        .as("Adapters do not depend on one another")
        .because("Adapters should only depend on one external system; depending on other adapters is likely to imply pulling dependencies towards other external systems");

    @ArchTest
    static final ArchRule PORTS_SHOULD_ONLY_BE_INTERFACES = classes().that().resideInAPackage("..ports..").should().beInterfaces();

}
