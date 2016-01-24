package com.transgen.api.enums;

/**
 * An enum containing all standard AAMVA fields, their data ref and description.
 */
public enum AAMVAField {
    DCA("a", "Jurisdiction-specific vehicle class"),
    DCB("b", "Jurisdiction-specific restriction codes"),
    DCD("c", "Jurisdiction-specific endorsement codes"),
    DBA("d", "Document Expiration Date"),
    DCS("e", "Customer Family Name"),
    DAC("f", "Customer First Name"),
    DAD("g", "Customer Middle Name(s)"),
    DBD("h", "Document Issue Date"),
    DBB("i", "Date of Birth"),
    DBC("j", "Physical Description Sex"),
    DAY("k", "Physical Description Eye Color"),
    DAU("l", "Physical Description Height"),
    DAG("m", "Address Street 1"),
    DAI("n", "Address City"),
    DAJ("o", "Address Jurisdiction Code"),
    DAK("p", "Address Postal Code"),
    DAQ("q", "Customer ID Number"),
    DCF("r", "Document Discriminator"),
    DCG("s", "Country Identification"),
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
    DCP("o", "Jurisdiction- specific vehicle classification description"),
    DCQ("p", "Jurisdiction- specific endorsement code description"),
    DCR("q", "Jurisdiction- specific restriction code description"),
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
    DDL("ab", "Veteran Indicator"),

    // These are outdated but some states still use them.
    DAP("41", "Driver Residence Postal Code"),
    DAA("1", "Driver License Name"),
    DAR("8", "Driver License Classification Code"),
    DAS("9", "Driver License Restriction Code "),
    DAT("10", "Driver License Endorsements Code"),
    DAL("37", "DDriver Residence Street Address 1"),
    DAN("39", "Driver Residence City"),
    DAO("40", "Driver Residence Jurisdiction Code"),
    DBH("47", "Organ Donor"),
    DCH("u", "Federal Commercial Vehicle Codes"),
    DCT("f", "Customer Given Names");

    private final String dataRef;
    private final String elementDesc;

    /**
     * Create a new AAMVAField enum
     *
     * @param dataRef     - the data reference for this field
     * @param elementDesc - the text description of this field
     */
    private AAMVAField(final String dataRef, final String elementDesc) {
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
