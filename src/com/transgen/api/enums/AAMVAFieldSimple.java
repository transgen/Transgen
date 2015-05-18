package com.transgen.api.enums;

/**
 * An enum containing all standard AAMVA fields, their data ref and description
 */
public enum AAMVAFieldSimple {
    DCA("a", "Vehicle Class"),
    DCB("b", "Restriction Codes"),
    DCD("c", "Endorsement Codes"),
    DBA("d", "Expiration Date"),
    DCS("e", "Last Name"),
    DAC("f", "First Name"),
    DAD("g", "Middle Name(s)"),
    DBD("h", "Issue Date"),
    DBB("i", "Date of Birth"),
    DBC("j", "Sex"),
    DAY("k", "Eye Color"),
    DAU("l", "Height"),
    DAG("m", "Address Street 1"),
    DAI("n", "Address City"),
    DAJ("o", "Address Jurisdiction Code"),
    DAK("p", "Address Postal Code"),
    DAQ("q", "ID Number"),
    DCF("r", "Document Discriminator"),
    DCG("s", "Country"),
    DDE("t", "Family name truncation"),
    DDF("u", "First name truncation"),
    DDG("v", "Middle name truncation"),
    DAH("a", "Address Street 2"),
    DAZ("b", "Hair color"),
    DCI("c", "Place of birth"),
    DCJ("d", "Audit information"),
    DCK("e", "Inventory control number"),
    DBN("f", "Alias AKA Family Name"),
    DBG("g", "Alias AKA Given Name"),
    DBS("h", "Alias AKA Suffix Name"),
    DCU("i", "Name Suffix"),
    DCE("j", "Physical Description Weight Range"),
    DCL("k", "Race ethnicity"),
    DCM("l", "Standard vehicle classification"),
    DCN("m", "Standard endorsement code"),
    DCO("n", "Standard restriction code"),
    DCP("o", "Vehicle Classification"),
    DCQ("p", "Endorsement Code"),
    DCR("q", "Restriction Code"),
    DDA("r", "Compliance Type"),
    DDB("s", "Card Revision Date"),
    DDC("t", "HAZMAT Endorsement Expiration Date"),
    DDD("u", "Limited Duration Document Indicator"),
    DAW("v", "Weight (pounds)"),
    DAX("w", "Weight (kilograms)"),
    DDH("x", "Under 18 Until"),
    DDI("y", "Under 19 Until"),
    DDJ("z", "Under 21 Until"),
    DDK("aa", "Organ Donor Indicator"),
    DDL("ab", "Veteran Indicator");

    private final String dataRef;
    private final String elementDesc;

    /**
     * Create a new AAMVAField enum
     *
     * @param dataRef     - the data reference for this field
     * @param elementDesc - the text description of this field
     */
    private AAMVAFieldSimple(final String dataRef, final String elementDesc) {
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
