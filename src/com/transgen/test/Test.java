package com.transgen.test;

import com.transgen.TransGen;
import com.transgen.Utils;
import com.transgen.api.StateGenerator;
import com.transgen.api.enums.State;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    private final State s;
    private final Class clazz;
    private boolean failing = false;

    public Test(Class clazz, State s) {
        this.s = s;
        this.clazz = clazz;
    }

    public Test(Class clazz) {
        this.s = State.getByAbbreviation(clazz.getSimpleName());
        this.clazz = clazz;
    }

    public void runTest() {
        String out = null;
        try {
            out = getStateData(s);
        } catch (IOException e) {
            System.out.println("[Warning] No test file for " + s.getAbbreviation() + ". Not testing.");
            return;
        }

        if (TransGen.debug) {
            System.out.println("DEBUG DATA:");
            System.out.println(Utils.repr(out));
            System.out.println(generateFieldHashMap(s, out));
        }

        ArrayList<String> data = parseBarcodeData(s, out);
        try {
            StateGenerator sg = StateGenerator.instantiateStateScript(clazz, data.toArray(new String[data.size()]));
            printStateReport(sg, out);
        }
        catch(Exception E){
            E.printStackTrace();
        }

        //sg.generate2D(1000, 1000);
    }

    public String generateFieldHashMap(State s, String real) {
        String out = "";
        for (String l : parseBarcodeData(s, real)) {
            out += ("dl.put(\"" + l.split("::")[0] + "\", 0);") + "\n";
        }
        return out;
    }

    public String generateExampleHash(State s, String real) {
        String out = "";
        out += "public LinkedHashMap<String, String> getExamples() { \n";
        out += "        LinkedHashMap<String, String> dl = new LinkedHashMap<String, String>();\n";
        for (String l : parseBarcodeData(s, real)) {
            if (l.split("::").length > 1) {
                if (l.split("::")[0].equals("DCS"))
                    out += ("dl.put(\"" + l.split("::")[0] + "\",\"" + "SMITH") + "\");" + "\n";
                else if (l.split("::")[0].equals("DAC"))
                    out += ("dl.put(\"" + l.split("::")[0] + "\",\"" + "JOHN") + "\");" + "\n";
                else if (l.split("::")[0].equals("DAD"))
                    out += ("dl.put(\"" + l.split("::")[0] + "\",\"" + "JOE") + "\");" + "\n";
                else if (l.split("::")[0].equals("DAG"))
                    out += ("dl.put(\"" + l.split("::")[0] + "\",\"" + "111 1ST STREET") + "\");" + "\n";
                else if (l.split("::")[0].equals("DAI"))
                    out += ("dl.put(\"" + l.split("::")[0] + "\",\"" + "SPRINGFIELD") + "\");" + "\n";
                else
                    out += ("dl.put(\"" + l.split("::")[0] + "\",\"" + l.split("::")[1]) + "\");" + "\n";
            }

            else
                out += ("dl.put(\"" + l.split("::")[0] + "\",\"") + "\");" + "\n";
        }
        out += "return dl;\n";
        out += "}";
        return out;
    }

    public ArrayList<String> parseBarcodeData(State s, String out) {
        String curr = "";
        String[] split = out.split("DL[0-9]{8}?");
        if (split.length > 1) curr = split[1];
        else curr = split[0];
        split = curr.split("Z[A-Z][0-9]{8}?");
        if (split.length > 1) curr = split[1];
        else curr = split[0];

        String[] lines = curr.split("\\P{Print}+");
        ArrayList<String> data = new ArrayList<String>();
        Boolean f = false;

        for (String l : lines) {
            if ((l.startsWith("D") || l.startsWith("Z")) && l.length() > 2) {
                if (l.startsWith("DL")) {
                    l = l.replaceFirst("DL", "");
                }
                if (l.startsWith("Z" + s.getAbbreviation().charAt(0)) && !f) {
                    l = l.replaceFirst("Z" + s.getAbbreviation().charAt(0), "");
                    f = true;
                }

                data.add(l.substring(0, 3) + "::" + l.substring(3, l.length()));
            }
        }

        return data;
    }

    public String getStateData(State s) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("testing/" + s.getAbbreviation() + ".b64"));
        String out = "";
        String line;
        while ((line = reader.readLine()) != null) out += line;
        reader.close();
        return new String(DatatypeConverter.parseBase64Binary(out));
    }

    public boolean isFailing() {
        return failing;
    }

    public void printStateReport(StateGenerator sg, String real) {
        String gen = sg.generate2DData();
        this.failing = !gen.equalsIgnoreCase(real);
        System.out.println("======================================================================");
        System.out.println("REPORT FOR: " + sg.getStateCode());
        System.out.println("======================================================================");
        System.out.println("GENERATED:");
        System.out.println(Utils.repr(gen));
        System.out.println("");
        System.out.println("TEST DATA:");
        System.out.println(Utils.repr(real));
        System.out.println("");
        System.out.println("PASS (CASE SENSITIVE)? " + (gen.equals(real)));
        System.out.println("PASS (CASE INSENSITIVE)? " + (gen.equalsIgnoreCase(real)));

        String[] realSplit = real.split(sg.getFields("DL").get(0));
        String[] genSplit = gen.split(sg.getFields("DL").get(0));

        StringBuilder builder = new StringBuilder();
        for (String s : Arrays.copyOfRange(realSplit, 1, realSplit.length)) builder.append(s);
        String realSplitData = sg.getFields("DL").get(0) + builder.toString();

        builder = new StringBuilder();
        for (String s : Arrays.copyOfRange(genSplit, 1, genSplit.length)) builder.append(s);
        String genSplitData = sg.getFields("DL").get(0) + builder.toString();

        System.out.println("PASS (HEADER)? " + (realSplit[0]).equalsIgnoreCase(genSplit[0]));
        System.out.println("PASS (NO HEADER)? " + (realSplitData).equalsIgnoreCase(genSplitData));
        try {
            sg.generate2D(300, 100);
            if (Files.exists(Paths.get(sg.getStateCode()))) System.out.println("GENERATION PASSED!");
            else System.out.println("GENERATION FAILED!");
        } catch (Exception e) {
            if (Files.notExists(Paths.get(sg.getStateCode()))) System.out.println("GENERATION FAILED!");
        }
        Utils.delete(new File(sg.getStateCode()));
        System.out.println("======================================================================");
    }
}