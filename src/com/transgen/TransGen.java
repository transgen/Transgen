package com.transgen;

import com.transgen.api.StateGenerator;
import com.transgen.forms.MainApplication;
import com.transgen.test.Test;
import groovy.lang.GroovyClassLoader;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

public class TransGen {
    private final static String VERSION = "2.0";
    private final static int REQUIRED_MODULE_VERSION = 1;

    public static boolean debug = false;
    private static TransGen instance;

    private HashMap<String, Class> stateGenerators = new HashMap<>();

    private File configFile = new File("config.properties");
    private Properties properties;

    public TransGen() throws IOException {
        loadProperties();
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("true")) {
            debug = true;
            System.out.println("[INFO] Debugging mode is enabled.");
        }

        TransGen bg = new TransGen();
        instance = bg;
        bg.init();

        MainApplication.main(args);
    }

    public static String getVersion() {
        return VERSION;
    }

    public static TransGen getInstance() {
        return instance;
    }

    public void init() {
        System.out.println("[INFO] Loading modules from disk...");
        stateGenerators = loadStateScripts();
        System.out.println("[INFO] Loaded " + stateGenerators.size() + " modules.");
        System.out.println("[INFO] Testing loaded modules...");
        if(debug) testStateScripts(stateGenerators);
        System.out.println("[INFO] All modules tested.");
    }

    private void loadProperties() throws IOException {
        // load default properties
        Properties defaultProps = new Properties();
        InputStream in = getClass().getResourceAsStream("/default_config.properties");
        defaultProps.load(in);
        in.close();

        // Create application properties from defaults
        properties = new Properties(defaultProps);

        if(!configFile.exists()) {
            FileOutputStream configOut = new FileOutputStream(configFile);
            properties.store(configOut, "Version: " + getVersion());
            configOut.close();
        } else {
            FileInputStream configIn = new FileInputStream(configFile);
            properties.load(configIn);
            in.close();
        }
    }

    private void testStateScripts(HashMap<String, Class> classes) {
        Integer failing = 0;
        String check = "";
        for (Class c : classes.values()) {
            Test t = new Test(c);
            t.runTest();
            if (t.isFailing()) {
                check += c.getSimpleName() + " ";
                failing++;
            }
        }
        System.out.println("[INFO] " + failing + "/" + classes.size() + " modules are failing.");
        if (check.length() > 0) System.out.println("[INFO] CHECK: " + check);
        else System.out.println("[INFO] All checked modules are passing!");
    }

    private HashMap<String, Class> loadStateScripts() {
        HashMap<String, Class> out = new HashMap<>();

        //Load all files with the .groovy extension
        File dir = new File("Modules");
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".groovy"));

        if (files == null) return out;

        for (File script : files) {
            //Load the script
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz;
            try {
                clazz = gcl.parseClass(script);
            } catch (IOException e) {
                System.out.println("[Error] Couldn't load modules at \"" + script.getAbsolutePath() + "\" from disk.");
                continue;
            }

            //Check that it extends StateGenerator
            if (clazz != null && !StateGenerator.class.isAssignableFrom(clazz)) {
                System.out.println("[Warning] Module at \"" + script.getAbsolutePath() + "\" is invalid. You must extend StateGenerator.");
                continue;
            }

            // Check module has expected version
            boolean correctVer = false;
            try {
                Method method = clazz.getMethod("getExpectedVersion");
                int ver = (Integer) method.invoke(null);
                correctVer = ver == REQUIRED_MODULE_VERSION;
            } catch (Exception e){
                e.printStackTrace();
            }

            if(!correctVer) {
                System.out.println("[Warning] Module at \"" + script.getAbsolutePath() + "\" is invalid. This module is not compatible with this version.");
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

    public void saveProperties() throws IOException {
        FileOutputStream configOut = new FileOutputStream(configFile);
        properties.store(configOut, "Version: " + getVersion());
        configOut.close();
    }

    public Properties getProperties() {
        return properties;
    }
}
