package fr.nautilus.architecture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Garantit que les regles ne sont pas vacantes : elles echouent sur des violations connues. */
class ArchitectureRulesDetectViolationsTest {

    private static JavaClasses fixtures;

    @BeforeAll
    static void importFixtures() {
        fixtures = new ClassFileImporter().importPackages("fr.nautilus")
                .that(new com.tngtech.archunit.base.DescribedPredicate<>("fixtures") {
                    @Override
                    public boolean test(com.tngtech.archunit.core.domain.JavaClass c) {
                        return c.getPackageName().contains(".fixture");
                    }
                });
    }

    @Test
    void domainDependingOnFrameworkIsDetected() {
        assertViolation(ArchRules.DOMAIN_IS_FRAMEWORK_FREE, "BadDomain");
    }

    @Test
    void domainDependingOnGeometryTypesIsDetected() {
        assertViolation(ArchRules.DOMAIN_IS_FRAMEWORK_FREE, "BadGeometryDomain");
    }

    @Test
    void domainDependingOnOuterLayerIsDetected() {
        assertViolation(ArchRules.DOMAIN_ISOLATED_FROM_OUTER_LAYERS, "BadDomainOuterLayer");
    }

    @Test
    void applicationDependingOnAdapterIsDetected() {
        assertViolation(ArchRules.APPLICATION_ISOLATED_FROM_ADAPTERS, "BadUseCase");
    }

    @Test
    void crossModuleCallToAdapterIsDetected() {
        assertViolation(ArchRules.CROSS_MODULE_ONLY_VIA_APPLICATION, "BadUseCase");
    }

    @Test
    void sharedDependingOnModuleIsDetected() {
        assertViolation(ArchRules.SHARED_INDEPENDENT_OF_MODULES, "BadShared");
    }

    @Test
    void contractUsedOutsideRestAdapterIsDetected() {
        assertViolation(ArchRules.CONTRACT_ONLY_IN_REST_ADAPTERS, "BadContractUser");
    }

    @Test
    void activeRecordIsDetected() {
        assertViolation(ArchRules.NO_ACTIVE_RECORD, "BadActiveRecord");
    }

    @Test
    void persistenceOutsidePersistenceAdapterIsDetected() {
        assertViolation(ArchRules.PERSISTENCE_CONFINED_TO_PERSISTENCE_ADAPTER, "BadActiveRecord");
    }

    /** La violation doit viser la classe fautive attendue, pas une violation sans rapport. */
    private static void assertViolation(ArchRule rule, String offendingClass) {
        AssertionError error = assertThrows(AssertionError.class, () -> rule.check(fixtures));
        assertTrue(error.getMessage().contains(offendingClass),
                "la violation devrait citer " + offendingClass + " : " + error.getMessage());
    }
}
