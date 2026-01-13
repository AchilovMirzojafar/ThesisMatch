package org.hhu.chatgpt.dummydata;

import java.util.List;

import org.hhu.chatgpt.domain.model.Betreuende;

public class BetreuendeDummyData {

    private BetreuendeDummyData(){

    }

    public static List<Betreuende> getAlleBetreuende(){
        return List.of(
            new Betreuende("Jens", "compilerbau"),
            new Betreuende("Marwan", "formale-methoden"),
            new Betreuende("Mirzo", "organisation"),
            new Betreuende("Jafar", "rust"),
            new Betreuende("Mohammed", "programmierung")
        );
    }
    
}
