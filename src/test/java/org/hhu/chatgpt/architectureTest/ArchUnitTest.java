package org.hhu.chatgpt.architectureTest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.hhu.chatgpt.ChatGptApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packagesOf = ChatGptApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)  //JUnit 5 Variante //zu testende Klassen

//ArchUnit Test erzwingt eine Onion-Architektur
public class ArchUnitTest {


        @ArchTest //Testet, dass Abhängigkeiten nur von außen nach innen zeigen // äußere Schichten dürfen innere verwenden // innere aber nicht äußere
        ArchRule onion = onionArchitecture().withOptionalLayers(true) // we don't have domain services. So we need withOptionalLayers(true)
                .domainModels("..domain.model..")
                .applicationServices("..application.applicationservice..")
                .adapter("web","..web..")                  //testet, dass web und persistance keine Abhängigkeit zueinander haben //Adpater der Infrastruktur dürfen keine Querreferenz haben
                .adapter("persistence","..persistence..") //packages gehören zur selben Layer package
                .adapter("config", "..config..");

        // onion ist die ArchRule Instanz, welche die Regle definiert // muss in JUnit5 mit @ArchTest annotiert werden
        // in JUnit 5 muss die Instanz/Regel nicht manuell auf die zu testenden Klassen aufgerufen werden
        // kein onion.check(klassen);

    }





