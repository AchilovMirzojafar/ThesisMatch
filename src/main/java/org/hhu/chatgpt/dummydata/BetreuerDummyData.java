package org.hhu.chatgpt.dummydata;

import org.hhu.chatgpt.domain.model.betreuer.Betreuer;
import org.hhu.chatgpt.domain.model.files.File;
import org.hhu.chatgpt.domain.model.files.FileType;

import java.time.LocalDateTime;
import java.util.List;

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
                "Mirzojafar",
                "Achilov",
                "jafarachilov@gmail.com",
                "+49 17671984372",
                "AchilovMirzojafar",
                List.of("Compilerbau", "Formale Methoden", "Theoretische Informatik"),
                List.of(leitfadenMarkdown)
                );

        Betreuer betreuer2 = new Betreuer(
                "Marwan",
                "Alkhoury",
                "marwanakov@hhu.de",
                "+49 211 654321",
                "Marwanakov",
                List.of("Webentwicklung", "Spring", "Datenbanken"),
                List.of(exposeVorlagePdf)
                );

        Betreuer betreuer3 = new Betreuer(
                "Muhammad",
                "Challou",
                "muhammad.challou@hhu.de",
                "+49 211 987654",
                "MohamedChallou",
                List.of("KI", "Machine Learning", "Data Science"),
                List.of(materialZip)
                );

        return List.of(betreuer1, betreuer2, betreuer3);
    }
}
