package org.hhu.chatgpt.domain.model;

import java.util.List;

public class Matching {
    private List<String> kurse;
    private List<String> interessen;

    public Matching(List<String> kurse, List<String> interessen) {
        this.kurse = kurse;
        this.interessen = interessen;
    }

    public List<String> getKurse() {
        return kurse;
    }

    public List<String> getInteressen() {
        return interessen;
    }
}
