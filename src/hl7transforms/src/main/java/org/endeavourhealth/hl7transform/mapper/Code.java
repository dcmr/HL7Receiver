package org.endeavourhealth.hl7transform.mapper;

import org.endeavourhealth.hl7parser.datatypes.Ce;

public class Code {
    private String identifier;
    private String codingSystem;
    private String originalTerm;
    private String displayTerm;

    public String getIdentifier() {
        return identifier;
    }

    public Code setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public String getCodingSystem() {
        return codingSystem;
    }

    public Code setCodingSystem(String codingSystem) {
        this.codingSystem = codingSystem;
        return this;
    }

    public String getOriginalTerm() {
        return originalTerm;
    }

    public Code setOriginalTerm(String originalTerm) {
        this.originalTerm = originalTerm;
        return this;
    }

    public String getDisplayTerm() {
        return displayTerm;
    }

    public Code setDisplayTerm(String displayTerm) {
        this.displayTerm = displayTerm;
        return this;
    }

    public static Code fromCe(Ce ce) {
        return new Code()
                .setIdentifier(ce.getIdentifier())
                .setCodingSystem(ce.getCodingSystem())
                .setDisplayTerm(ce.getText());
    }

    public static Code fromCeAlternate(Ce ce) {
        return new Code()
                .setIdentifier(ce.getAlternateIdentifier())
                .setCodingSystem(ce.getAlternativeCodingSystem())
                .setDisplayTerm(ce.getAlternateText());
    }
}
