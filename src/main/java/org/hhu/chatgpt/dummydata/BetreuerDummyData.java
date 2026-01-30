package org.hhu.chatgpt.dummydata;

import org.hhu.chatgpt.domain.model.Betreuer;
import org.hhu.chatgpt.domain.model.files.File;
import org.hhu.chatgpt.domain.model.files.FileType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BetreuerDummyData {

    public static List<Betreuer> getBetreuerListe() {

        File leitfadenMarkdown = new File(
                "Betreuungsleitfaden",
                "Leitfaden für die Betreuung von Bachelorarbeiten",
                "karla.turing",
                LocalDateTime.now().minusDays(3),
                "leitfaden_karla_turing.md",
                "Betreuungsleitfaden.md",
                FileType.MARKDOWN
        );

        File exposeVorlagePdf = new File(
                "Exposé Vorlage",
                "PDF-Vorlage für ein Exposé",
                "max.mustermann",
                LocalDateTime.now().minusDays(7),
                "expose_vorlage.pdf",
                "Expose_Vorlage.pdf",
                FileType.PDF
        );

        File materialZip = new File(
                "Beispielmaterial",
                "Zusatzmaterial für Studierende",
                "anna.schmidt",
                LocalDateTime.now().minusDays(1),
                "material_anna_schmidt.zip",
                "Material.zip",
                FileType.ZIP
        );



        Betreuer betreuer1 = new Betreuer(
                116162742,
                "Prof.",
                "Mirzojafar",
                "Achilov",
                "jafarachilov@gmail.com",
                "+49 17671984372",
                Set.of("Compilerbau", "Formale Methoden", "Theoretische Informatik"),
                new HashSet<>()
                );

        Betreuer betreuer2 = new Betreuer(
                115821857,
                "Prof.",
                "Marwan",
                "Alkhoury",
                "marwanakov@hhu.de",
                "+49 211 654321",
                Set.of("Webentwicklung", "Spring", "Datenbanken"),
                new HashSet<>()
                );

        Betreuer betreuer3 = new Betreuer(
                147902436,
                "Prof.",
                "Muhammad",
                "Challou",
                "muhammad.challou@hhu.de",
                "+49 211 987654",
                Set.of("KI", "Machine Learning", "Data Science"),
                new HashSet<>()
                );

        return List.of(betreuer1, betreuer2, betreuer3);
    }
}
