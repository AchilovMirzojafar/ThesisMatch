package org.hhu.thesismatch.architectureTest;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.hhu.thesismatch.ThesisMatchApplication;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Erzwingt die Onion-Architektur: Abhängigkeiten zeigen nur von außen nach innen,
 * und Adapter (web, persistence, config) kennen sich nicht gegenseitig.
 */
@AnalyzeClasses(packagesOf = ThesisMatchApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
class ArchUnitTest {

    @ArchTest
    static final ArchRule onion = onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application..")
            .adapter("web", "..web..")
            .adapter("persistence", "..persistence..")
            .adapter("config", "..config..");

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..")
            .because("das Domänenmodell soll reines Java sein");

    @ArchTest
    static final ArchRule controllersDoNotUseRepositories = noClasses()
            .that().resideInAPackage("..web..")
            .should().dependOnClassesThat().resideInAPackage("..application.applicationservice.repository..")
            .because("Controller sollen nur über Application Services arbeiten");
}
