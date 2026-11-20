package fr.nautilus.architecture;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertViolation(ArchRules.DOMAIN_IS_FRAMEWORK_FREE);
    }

    @Test
    void crossModuleCallToAdapterIsDetected() {
        assertViolation(ArchRules.CROSS_MODULE_ONLY_VIA_APPLICATION);
    }

    private static void assertViolation(ArchRule rule) {
        assertThrows(AssertionError.class, () -> rule.check(fixtures));
    }
}
