package fr.nautilus.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.List;

/** Regles partagees entre le test sur le code reel et le test de detection sur des violations connues. */
final class ArchRules {

    private static final String[] MODULES = {
        "account", "needsprofile", "driver", "vehicle", "search", "booking", "notification", "audit" };

    static final ArchRule DOMAIN_IS_FRAMEWORK_FREE = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "io.quarkus..", "jakarta..", "javax..", "org.hibernate..", "io.smallrye..",
                    "org.eclipse.microprofile..", "com.fasterxml.jackson..", "org.jboss..", "io.vertx..")
            .allowEmptyShould(true)
            .because("le domaine est du Java pur");

    static final ArchRule DOMAIN_ISOLATED_FROM_OUTER_LAYERS = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..adapter..")
            .allowEmptyShould(true);

    static final ArchRule APPLICATION_ISOLATED_FROM_ADAPTERS = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")
            .allowEmptyShould(true);

    /** Un module n'appelle un autre module que via ses ports application. */
    static final ArchRule CROSS_MODULE_ONLY_VIA_APPLICATION = classes()
            .that().resideInAnyPackage(prefix(MODULES))
            .should(onlyUseOtherModulesThroughTheirApplication())
            .allowEmptyShould(true);

    static final ArchRule SHARED_INDEPENDENT_OF_MODULES = noClasses()
            .that().resideInAPackage("fr.nautilus.shared..")
            .should().dependOnClassesThat().resideInAnyPackage(prefix(MODULES))
            .allowEmptyShould(true);

    private static ArchCondition<JavaClass> onlyUseOtherModulesThroughTheirApplication() {
        return new ArchCondition<>("n'appeler un autre module que via son package application") {
            @Override
            public void check(JavaClass origin, ConditionEvents events) {
                String originModule = moduleOf(origin.getPackageName());
                for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                    JavaClass target = dependency.getTargetClass();
                    String targetModule = moduleOf(target.getPackageName());
                    boolean foreign = !targetModule.isEmpty() && !targetModule.equals(originModule)
                            && List.of(MODULES).contains(targetModule);
                    boolean viaApplication = target.getPackageName().startsWith(
                            "fr.nautilus." + targetModule + ".application");
                    if (foreign && !viaApplication) {
                        events.add(SimpleConditionEvent.violated(dependency, dependency.getDescription()));
                    }
                }
            }
        };
    }

    static String moduleOf(String packageName) {
        String[] parts = packageName.split("[.]");
        return parts.length > 2 && parts[0].equals("fr") && parts[1].equals("nautilus") ? parts[2] : "";
    }

    private static String[] prefix(String[] modules) {
        String[] result = new String[modules.length];
        for (int i = 0; i < modules.length; i++) {
            result[i] = "fr.nautilus." + modules[i] + "..";
        }
        return result;
    }

    private ArchRules() {
    }
}
