package com.transgen.api.enums;

/**
 * An enum containing all standard AAMVA fields, their data ref and description
 */
public enum AAMVAExamples{
    DDE("DDE", "U"),
    DDF("DDF", "U"),
    DDG("DDG", "U"),
    DCA("DCA", "NONE"),
    DCD("DCD", "NONE"),
    DCG("DCG", "USA");

    private final String dataRef;
    private final String elementDesc;

    /**
     * Create a new AAMVAField enum
     *
     * @param dataRef     - the data reference for this field
     * @param elementDesc - the text description of this field
     */
    private AAMVAExamples(final String dataRef, final String elementDesc) {
        this.dataRef = dataRef;
        this.elementDesc = elementDesc;
    }

    /**
     * Get the data reference
     *
     * @return - the data reference for this field
     */
    public String getDataRef() {
        return dataRef;
    }

    /**
     * Get the fields description
     *
     * @return - the text description of this field
     */
    public String getElementDesc() {
        return elementDesc;
    }
}
