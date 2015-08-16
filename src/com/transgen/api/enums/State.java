package com.transgen.api.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum containing all 50 states + DC and their abbreviation and IIN.
 */
public enum State {
    ALABAMA("AL", "636033"),
    ALASKA("AK", "636059"),
    ARIZONA("AZ", "636026"),
    ARKANSAS("AR", "636021"),
    CALIFORNIA("CA", "636014"),
    COLORADO("CO", "636020"),
    CONNECTICUT("CT", "636006"),
    DISTRICT_OF_COLUMBIA("DC", "636043"),
    DELAWARE("DE", "636011"),
    FLORIDA("FL", "636010"),
    GEORGIA("GA", "636055"),
    HAWAII("HI", "636047"),
    IDAHO("ID", "636050"),
    ILLINOIS("IL", "636035"),
    INDIANA("IN", "636037"),
    IOWA("IA", "636018"),
    KANSAS("KS", "636022"),
    KENTUCKY("KY", "636046"),
    LOUISIANA("LA", "636007"),
    MAINE("ME", "636041"),
    MARYLAND("MD", "636003"),
    MASSACHUSETTS("MA", "636002"),
    MICHIGAN("MI", "636032"),
    MINNESOTA("MN", "636038"),
    MISSISSIPPI("MS", "636051"),
    MISSOURI("MO", "636030"),
    MONTANA("MT", "636008"),
    NEBRASKA("NE", "636054"),
    NEVADA("NV", "636049"),
    NEW_HAMPSHIRE("NH", "636039"),
    NEW_JERSEY("NJ", "636036"),
    NEW_MEXICO("NM", "636009"),
    NEW_YORK("NY", "636001"),
    NORTH_CAROLINA("NC", "636004"),
    NORTH_DAKOTA("ND", "636034"),
    OHIO("OH", "636023"),
    OKLAHOMA("OK", "636058"),
    OREGON("OR", "636029"),
    PENNSYLVANIA("PA", "636025"),
    RHODE_ISLAND("RI", "636052"),
    SOUTH_CAROLINA("SC", "636005"),
    SOUTH_DAKOTA("SD", "636042"),
    TENNESSEE("TN", "636053"),
    TEXAS("TX", "636015"),
    UTAH("UT", "636040"),
    VERMONT("VT", "636024"),
    VIRGINIA("VA", "636000"),
    WASHINGTON("WA", "636045"),
    WEST_VIRGINIA("WV", "636061"),
    WISCONSIN("WI", "636031"),
    WYOMING("WY", "636060");

    private static final Map<String, State> abbrevMap;

    static {
        abbrevMap = new HashMap<String, State>();
        for (State v : State.values()) {
            abbrevMap.put(v.getAbbreviation(), v);
        }
    }

    private final String iin;
    private final String abbrev;

    /**
     * Create a new state enum
     *
     * @param abbrev - the 2 character state abbreviation/state code as a string
     * @param iin    - the 6 digit IIN number represented as a string
     */
    private State(final String abbrev, final String iin) {
        this.iin = iin;
        this.abbrev = abbrev;
    }

    /**
     * Get the state enum by its abbreviation/state code.
     *
     * @param abbrev - the 2 character state abbreviation as a string
     * @return the state enum for the specified abbreviation
     */
    public static State getByAbbreviation(String abbrev) {
        if (abbrevMap.containsKey(abbrev)) return abbrevMap.get(abbrev.toUpperCase());
        return null;
    }

    /**
     * Get the IIN number for this state
     *
     * @return the 6 digit IIN number represented as a string
     */
    public String getIIN() {
        return this.iin;
    }

    /**
     * Get the state abbreviation/state code
     *
     * @return the 2 character state abbreviation
     */
    public String getAbbreviation() {
        return this.abbrev;
    }
}
