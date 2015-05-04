package com.transgen.api;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.itextpdf.text.pdf.BarcodePDF417;
import com.transgen.api.enums.State;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This is the general state generator class which every script should inherit.
 */
public abstract class StateGenerator {
    private final String STATE_CODE;
    private final String FILE_TYPE;
    private final String IIN;
    private final int VERSION;
    private final int JURISDICTION;
    public LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
    private String CI = Character.toString((char) 64);
    private String LF = Character.toString((char) 10);
    private String RS = Character.toString((char) 30);
    private String CR = Character.toString((char) 13);
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> fields;

    public StateGenerator(String[] data, LinkedHashMap<String, LinkedHashMap<String, Integer>> fields, String code, String type, String iin, Integer ver, Integer juris) {
        for (String s : data) {
            String[] fv = s.split("::");
            this.data.put(fv[0], fv.length > 1 ? fv[1] : "");
        }
        this.fields = fields;
        this.STATE_CODE = code;
        this.FILE_TYPE = type;
        this.IIN = iin;
        this.VERSION = ver;
        this.JURISDICTION = juris;
    }

    public StateGenerator(String[] data, LinkedHashMap<String, LinkedHashMap<String, Integer>> fields, String code, String type, String iin, Integer ver, Integer juris, String rs) {
        this(data, fields, code, type, iin, ver, juris);
        this.RS = rs;
    }

    public StateGenerator(String[] data, LinkedHashMap<String, LinkedHashMap<String, Integer>> fields, String code, String type, String iin, Integer ver, Integer juris, String rs, String cr) {
        this(data, fields, code, type, iin, ver, juris, rs);
        this.CR = cr;
    }

    public StateGenerator(String[] data, LinkedHashMap<String, LinkedHashMap<String, Integer>> fields, State state, String type, Integer ver, Integer juris) {
        this(data, fields, state.getAbbreviation(), type, state.getIIN(), ver, juris);
    }

    public StateGenerator(String[] data, LinkedHashMap<String, LinkedHashMap<String, Integer>> fields, State state, String type, Integer ver, Integer juris, String rs) {
        this(data, fields, state.getAbbreviation(), type, state.getIIN(), ver, juris);
        this.RS = rs;
    }

    public StateGenerator(String[] data, LinkedHashMap<String, LinkedHashMap<String, Integer>> fields, State state, String type, Integer ver, Integer juris, String rs, String cr) {
        this(data, fields, state.getAbbreviation(), type, state.getIIN(), ver, juris, rs);
        this.CR = cr;
    }

    /**
     * Get a HashMap of fields and lengths for this state. Use 0 if length doesn't matter. Must be overridden in subclass.
     *
     * @return HashMap of fields and lengths for this state
     */
    protected static LinkedHashMap<String, LinkedHashMap<String, Integer>> getFields() {
        return null;
    }


    /**
     * Pad a string with spaces on the right
     *
     * @param s - the input string
     * @param n - minimum amount of padding
     * @return the padded string
     */
    public static String padRight(String s, int n) {
        if (n == 0) return s;
        return String.format("%1$-" + n + "s", s);
    }

    /**
     * Pad an integer with zeros on the left
     *
     * @param i - the input integer
     * @param n - minimum amount of padding
     * @return the padded string
     */
    public static String zeroLeft(Integer i, int n) {
        if (n == 0) return "" + i;
        return String.format("%0" + n + "d", i);
    }

    /**
     * Pad a string represented integer with zeros on the left
     *
     * @param s - the input string
     * @param n - minimum amount of padding
     * @return the padded string
     */
    public static String zeroLeft(String s, int n) {
        return zeroLeft(Integer.parseInt(s), n);
    }

    /**
     * Instantiate a state script represented as a class object
     *
     * @param clazz  - State script represented as a class
     * @param fields - The data fields to pass into the state script (same as the default StateGenerator constructor)
     * @return an instantiated StateGenerator object of the class
     */
    public static StateGenerator instantiateStateScript(Class clazz, String[] fields)throws Exception {
        Object aScript = null;

            aScript = clazz.getConstructor(new Class[]{String[].class}).newInstance(new Object[]{fields});

        return (StateGenerator) aScript;
    }

    /**
     * Get the 2 character state code (ex. CA, TX, FL, NY)
     *
     * @return a 2 character string representing the state code
     */
    public String getStateCode() {
        return STATE_CODE;
    }

    /**
     * Get the compliance indicator character. Should always be "@".
     *
     * @return CI character
     */
    public String getCI() {
        return CI;
    }

    /**
     * Get the line feed (data separator) character used in this barcode
     *
     * @return LF character
     */
    public String getLF() {
        return LF;
    }

    /**
     * Get the record separator character used in this barcode
     *
     * @return RS character
     */
    public String getRS() {
        return RS;
    }

    /**
     * Get the carriage return (segment terminator) character used in this barcode
     *
     * @return CR character
     */
    public String getCR() {
        return CR;
    }

    /**
     * Get the file type for this barcode. This is the designator that identifies the barcode
     * as an AAMVA compliant format.
     *
     * @return
     */
    public String getFileType() {
        return FILE_TYPE;
    }

    /**
     * Get the IIN number for this state
     *
     * @return the states 6 digit IIN number represented as a string
     */
    public String getIIN() {
        return IIN;
    }

    /**
     * Get the AAMVA version number for this state
     *
     * @return version number represented as a string
     */
    public String getVersion() {
        return zeroLeft(VERSION, 2);
    }

    /**
     * Get the jurisdiction version number. This is a decimal value between 00 and 99 that specifies
     * the jurisdiction version level of the PDF417 bar code format.
     *
     * @return jurisdiction version number represented as a string
     */
    public String getJurisdiction() {
        return zeroLeft(JURISDICTION, 2);
    }

    /**
     * Get the amount of entries/documents
     *
     * @return the amount of entries represented by a padded string
     */
    public String getEntries() {
        return zeroLeft(fields.keySet().size(), 2);
    }

    /**
     * Get all the documents for this state
     *
     * @return a list of strings representing the document keys
     */
    public ArrayList<String> getDocuments() {
        ArrayList<String> l = new ArrayList<String>();
        l.addAll(fields.keySet());
        return l;
    }

    /**
     * Get all the fields for a document
     *
     * @param doc - the document key to get fields from
     * @return a list of strings representing the fields for the specified document
     */
    public ArrayList<String> getFields(String doc) {
        ArrayList<String> l = new ArrayList<String>();
        l.addAll(fields.get(doc).keySet());
        return l;
    }

    /**
     * Get the desired length a field; 0 is any length
     *
     * @param doc   - the document key (ex. DL, ZN)
     * @param field - the field key (ex. DAQ, DAA)
     * @return the desired length for the specified field
     */
    public Integer getFieldLength(String doc, String field) {
        return fields.get(doc).get(field);
    }

    /**
     * Get the value of a special field. Override this method if
     * a field needs special operations done on it before being
     * added to the barcode.
     *
     * @param doc   - the document key of the field
     * @param field - the key of the desired field
     * @return the data as a string for the specified field
     */
    public String getSpecialField(String doc, String field) {
        return null;
    }

    /**
     * Generate the 2D barcode
     *
     * @param width  - the desired barcode width in pixels
     * @param height - the desired barcode height in pixels
     */
    public abstract void generate2D(int width, int height);

    /**
     * Generate the 1D barcode.
     *
     * @param width  - the desired barcode width in pixels
     * @param height - the desired barcode height in pixels
     */
    public abstract void generate1D(int width, int height);


    /**
     * Utility method to generate 1D and 2D barcodes
     *
     * @param width2d  - the desired width of the 2D barcode in pixels
     * @param height2d - the desired height of the 2D barcode in pixels
     * @param width1d  - the desired width of the 1D barcode in pixels
     * @param height1d - the desired height of the 1D barcode in pixels
     */
    public void generate(int width2d, int height2d, int width1d, int height1d) {
        this.generate2D(width2d, height2d);
        this.generate1D(width1d, height1d);
    }

    /**
     * The unique filename for this barcode. Should return the the customer name and
     * unique identifier separated by underscores. This may need to be overridden
     * as some states have different fields than those in this method.
     *
     * @return the unique filename as a string
     */
    public String getUniqueFilename() {
        return this.data.get("DAQ");
    }

    /**
     * Calculate data length for this state. This should be overridden if a
     * state uses a different method of calculating data length.
     *
     * @param s - the string to to calculate the length of
     * @return the length of the specified data
     */
    public int dataLength(String s) {
        return s.length();
    }

    /**
     * Get the header for this state
     *
     * @return the header for this state
     */
    public abstract String getHeader();

    /**
     * Get the common terminal which is appended after every field
     *
     * @return the common terminal
     */
    public abstract String getCommonTerminal();

    /**
     * Get the end terminal which is appended after every document
     *
     * @return the end terminal
     */
    public abstract String getEndTerminal();

    /**
     * Should we only add the final terminal at the end of the last document?
     *
     * @return whether or not we should only add the final terminal at the end of the last document
     */
    public abstract Boolean finalTerminalOnly();

    /**
     * Should we include the document headers?
     *
     * @return whether or not we should include the doucment headers
     */
    public abstract Boolean includeDocumentHeaders();

    /**
     * Should we remove the final terminal and only use the common terminal?
     *
     * @return whether or not we should remove the final terminal
     */
    public abstract Boolean removeFinalTerminal();

    /**
     * Should we add an additional terminal to the end of the last document?
     *
     * @return whether or not to add an additional final terminal
     */
    public String additionalFinalTerminal() {
        return "";
    }

    /**
     * Size offset to be added to the header size
     *
     * @return size offset as an int
     */
    public int headerSizeOffset() {
        return 0;
    }

    /**
     * Size offset to be added to each document
     *
     * @return size offset as an int
     */
    public int bodySizeOffset() {
        return 0;
    }

    /**
     * Get the data for the 1D barcode. Should be overridden for each state.
     *
     * @return the data for the 1D barcode as a string.
     */
    public String generate1DData() {
        return null;
    }

    /**
     * Get the data for the 2D barcode. Should only be overridden if the state
     * does not conform to the data generated by this method.
     *
     * @return the data for the 2D barcode as a string
     */
    public String generate2DData() {
        String body = "";
        Integer len = 0;

        String header = getHeader();
        Integer header_len = dataLength(header) + (10 * this.getDocuments().size());

        int i = 0;
        for (String doc : this.getDocuments()) {
            i++;
            Integer doc_len = includeDocumentHeaders() ? doc.length() : 0;
            if (includeDocumentHeaders()) body += doc;

            for (String field : this.getFields(doc)) {
                String val = getSpecialField(doc, field);
                if (val == null)
                    val = this.data.get(field);

                String term = getCommonTerminal();
                if (field.equals(this.getFields(doc).get(this.getFields(doc).size() - 1))) {
                    if (finalTerminalOnly()) {
                        if (i == this.getDocuments().size()) {
                            if (!removeFinalTerminal()) term = getEndTerminal();
                        }
                    } else {
                        if (i == this.getDocuments().size()) {
                            if (!removeFinalTerminal()) term = getEndTerminal();
                            else term = "";
                        } else {
                            term = getEndTerminal();
                        }
                    }

                    if (doc.equals(getDocuments().get(getDocuments().size() - 1))) {
                        term += additionalFinalTerminal();
                    }
                }

                String f = field.toUpperCase() + padRight(val.toUpperCase(), getFieldLength(doc, field)) + term;
                body += f;
                doc_len += dataLength(f);
            }

            Integer offset = header_len + len + headerSizeOffset();
            header += (doc + zeroLeft(offset.toString(), 4) + zeroLeft(Integer.toString(doc_len + bodySizeOffset()), 4));
            len += doc_len;
        }

        return header + body;
    }

    /**
     * Generate a Code 128 barcode
     *
     * @param data   - the data to store in the barcode
     * @param width  - the desired width of the barcode in pixels
     * @param height - the desired height of the barcode in pixels
     */
    public void generateCode128(String data, int width, int height) {
        try {
            Code128Writer c = new Code128Writer();
            BitMatrix matrix = c.encode(data, BarcodeFormat.CODE_128, width, height);
            if (Files.notExists(Paths.get(this.getStateCode()))) {
                Files.createDirectory(Paths.get(this.getStateCode()));
            }
            FileOutputStream fos = new FileOutputStream(new File(this.getStateCode() + File.separator + "CODE128_" + this.getUniqueFilename() + ".png"));
            MatrixToImageWriter.writeToStream(matrix, "png", fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate a PDF417 barcode
     *
     * @param data   - the data to store in the barcode
     * @param col    - the desired data columns in the barcode
     * @param row    - the desired data rows in the barcode (may not conform)
     * @param ecl    - the desired error correction level of the barcode
     * @param width  - the desired width of the barcode  in pixels
     * @param height - the desired barcode height of the barcode in pixels
     */
    public void generatePDF417(String data, int col, int row, int ecl, int width, int height) {
        BarcodePDF417 b = new BarcodePDF417();
        b.setText(data);
        b.setErrorLevel(ecl);
        b.setCodeRows(row);
        b.setCodeColumns(col);
        b.setOptions(BarcodePDF417.PDF417_FIXED_COLUMNS);

        Image i = b.createAwtImage(Color.BLACK, Color.WHITE);
        int type = BufferedImage.TYPE_INT_RGB;
        i = i.getScaledInstance(width, height, 0);
        BufferedImage dest = new BufferedImage(width, height, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(i, 0, 0, null);
        g2.dispose();

        if (Files.notExists(Paths.get(this.getStateCode()))) {
            try {
                Files.createDirectory(Paths.get(this.getStateCode()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File(System.getProperty("user.dir") + "\\" + this.getStateCode() + File.separator + "PDF417_" + this.getUniqueFilename() + ".png");

        try {
            ImageIO.write(dest, "png", file);
        }
         catch (IOException e) {
            System.out.println("Write error for " + file.getPath() +
                    ": " + e.getMessage());
        }
    }
}
