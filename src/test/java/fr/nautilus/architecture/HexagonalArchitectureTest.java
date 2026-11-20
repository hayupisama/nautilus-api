package fr.nautilus.architecture;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** Regles d'architecture hexagonale par module (CLAUDE.md, section Architecture). */
@AnalyzeClasses(packages = "fr.nautilus", importOptions = {
        ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domainIsFrameworkFree = ArchRules.DOMAIN_IS_FRAMEWORK_FREE;

    @ArchTest
    static final ArchRule domainDoesNotDependOnOuterLayers = ArchRules.DOMAIN_ISOLATED_FROM_OUTER_LAYERS;

    @ArchTest
    static final ArchRule applicationDoesNotDependOnAdapters = ArchRules.APPLICATION_ISOLATED_FROM_ADAPTERS;

    @ArchTest
    static final ArchRule modulesOnlyUseOtherModulesThroughApplication = ArchRules.CROSS_MODULE_ONLY_VIA_APPLICATION;

    @ArchTest
    static final ArchRule sharedDoesNotDependOnModules = ArchRules.SHARED_INDEPENDENT_OF_MODULES;

    @ArchTest
    static final ArchRule generatedContractIsOnlyUsedByRestAdapters = ArchRules.CONTRACT_ONLY_IN_REST_ADAPTERS;

    @ArchTest
    static final ArchRule noActiveRecord = ArchRules.NO_ACTIVE_RECORD;

    @ArchTest
    static final ArchRule persistenceConfinedToPersistenceAdapter =
            ArchRules.PERSISTENCE_CONFINED_TO_PERSISTENCE_ADAPTER;

    @ArchTest
    static final ArchRule noCyclesBetweenModules = slices().matching("fr.nautilus.(*)..")
            .should().beFreeOfCycles().allowEmptyShould(true);
}
