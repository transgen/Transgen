package com.transgen;

import com.transgen.api.StateGenerator;
import com.transgen.forms.MainForm;
import com.transgen.test.Test;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class TransGen {

    public static boolean debug = true;
    private static TransGen instance;

    private HashMap<String, Class> stateGenerators = new HashMap<String, Class>();

    public TransGen() {

    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("true")) {
            debug = true;
        }

        TransGen bg = new TransGen();
        instance = bg;
        bg.init();

        new MainForm().main();
    }

    public static TransGen getInstance() {
        return instance;
    }

    public void init() {
        System.out.println("[INFO] Loading scripts from disk...");
        stateGenerators = loadStateScripts();
        System.out.println("[INFO] Loaded " + stateGenerators.size() + " scripts.");
        System.out.println("[INFO] Testing loaded scripts...");
        testStateScripts(stateGenerators);
        System.out.println("[INFO] All scripts tested.");
    }

    private void testStateScripts(HashMap<String, Class> classes) {
        Integer failing = 0;
        String check = "";
        Integer total = classes.size();
        Iterator it = classes.entrySet().iterator();
        for (Class c : classes.values()) {
            Test t = new Test((Class) c);
            t.runTest();
            if (t.isFailing()) {
                check += c.getSimpleName() + " ";
                failing++;
            }
        }
        System.out.println("[INFO] " + failing + "/" + classes.size() + " scripts are failing.");
        if (check.length() > 0) System.out.println("[INFO] CHECK: " + check);
        else System.out.println("[INFO] All checked scripts are passing!");
    }

    private HashMap<String, Class> loadStateScripts() {
        HashMap<String, Class> out = new HashMap<>();

        //Load all files with the .groovy extension
        File dir = new File("scripts");
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".groovy"));

        if (files == null) return out;

        for (File script : files) {
            //Load the script
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz;
            try {
                clazz = gcl.parseClass(script);
            } catch (IOException e) {
                System.out.println("[Error] Couldn't load script at \"" + script.getAbsolutePath() + "\" from disk.");
                continue;
            }

            //Check that it extends StateGenerator
            if (clazz != null && !StateGenerator.class.isAssignableFrom(clazz)) {
                System.out.println("[Warning] Script at \"" + script.getAbsolutePath() + "\" is invalid. You must extend StateGenerator.");
                continue;
            }

            //Add class to class map
            out.put(clazz.getSimpleName(), clazz);
        }

        return out;
    }

    public HashMap<String, Class> getStateGenerators() {
        return stateGenerators;
    }
}
