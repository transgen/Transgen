package com.transgen.forms;

import com.transgen.TransGen;
import com.transgen.api.StateGenerator;
import com.transgen.api.enums.AAMVAField;
import com.transgen.api.enums.AAMVAFieldSimple;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BatchForm {
    private JPanel BatchForm;
    private JButton browseButton;
    private JTextField pathField;
    private JButton generateButton;
    private JComboBox chooseAState;
    private JTextArea csvExample;

    public BatchForm(Boolean simple) {
        Object[] sortedArray = TransGen.getInstance().getStateGenerators().keySet().toArray();
        Arrays.sort(sortedArray);
        chooseAState.setModel(new DefaultComboBoxModel(sortedArray));
        populateCSVExample(chooseAState, simple);

        //Populate CSV Example on dropdown selection
        chooseAState.addActionListener(e -> populateCSVExample(chooseAState, simple));

        //Browse for a csv file
        browseButton.addActionListener(e -> browseForCSV());

        //Generate the barcode data
        generateButton.addActionListener(e -> generateData());
    }

    public void generateData() {
        File csv = new File(pathField.getText());
        if (!csv.exists()) {
            JOptionPane.showMessageDialog(null, "Error: the selected file does not exist or no file selected.");
            return;
        }
        Boolean created = false;
        ArrayList<String> lines = new ArrayList<String>();

        try {
            lines.addAll(Files.readAllLines(Paths.get(csv.getAbsolutePath()), Charset.defaultCharset()).stream().collect(Collectors.toList()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: couldn't read CSV file.");
            return;
        }
        String delim = ",";
        String regex = "(?<!\\\\)" + Pattern.quote(delim);
        ArrayList<String> data = new ArrayList<String>();
        String[] header = lines.get(0).split(",");
        String[] fields = lines.get(1).split(regex);


        if (chooseAState.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Error: You must choose a state.");
        }

        if (chooseAState.getSelectedItem() != null && !header[0].equalsIgnoreCase((String) chooseAState.getSelectedItem())) {
            JOptionPane.showMessageDialog(null, "Warning: CSV state code does not match script state code.");
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] l = lines.get(i).split(regex, -1);
            for (int j = 0; j < l.length; j++) {
                l[j] = l[j].replaceAll("\\\\","");
                data.add(header[j] + "::" + l[j]);
            }
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(chooseAState.getSelectedItem()), data.toArray(new String[data.size()]));
                sg.generate(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
                created = true;
            }
            catch(Exception E){
                JOptionPane.showMessageDialog(null, "Error: Incorrect CSV format. \n" + E.toString());
            }

        }
        if (created)
            JOptionPane.showMessageDialog(null, "Success: Barcodes generated in program root -> " + new File("").getAbsolutePath());


    }

    public void browseForCSV() {
        JFileChooser jf = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV FILES", "csv");
        jf.setFileFilter(filter);
        int returnVal = jf.showOpenDialog(BatchForm);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            pathField.setText(jf.getSelectedFile().getAbsolutePath());
        }
    }

    public void populateCSVExample(JComboBox jcb, boolean simple) {
        String s = (String) jcb.getSelectedItem();
        if (s != null) {
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(s), new String[]{});
                String ex = sg.getStateCode() + ",2D_WIDTH,2D_HEIGHT,1D_WIDTH,1D_HEIGHT,";
                for (String d : sg.getDocuments()) {
                    for (String f : sg.getFields(d)) {
                        ex += f + ",";
                    }
                }
                ex = ex.replaceAll(";$", "");
                ex += "\n";

                Set<String> aamvaFields = new HashSet<String>();
                for (AAMVAField f : AAMVAField.values()) {
                    aamvaFields.add(f.name());
                }
                ex += "<STATE>,<2D_WIDTH>,<2D_HEIGHT>,<1D_WIDTH>,<1D_HEIGHT>,";
                for (String d : sg.getDocuments()) {
                    for (String f : sg.getFields(d)) {
                        if (!aamvaFields.contains(f)) {
                            ex += "<UNKNOWN DATA>,";
                        } else {
                            if (simple) {
                                AAMVAFieldSimple e = AAMVAFieldSimple.valueOf(f);
                                ex += "<" + e.getElementDesc().toUpperCase() + ">,";
                            }
                            else{
                                AAMVAField e = AAMVAField.valueOf(f);
                                ex += "<" + e.getElementDesc().toUpperCase() + ">,";
                            }
                        }

                    }
                }
                ex = ex.replaceAll(";$", "");
                ex += "\n";
                csvExample.setLineWrap(true);
                csvExample.setText(ex);
            }
            catch(Exception E){
                E.printStackTrace();
            }

        }
    }

    public void main(Boolean simple) {
        JFrame frame = new JFrame("BatchForm");
        frame.setTitle("TransGen™ - Multiple Barcodes");
        frame.setContentPane(new BatchForm(simple).BatchForm);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon img = new ImageIcon(getClass().getResource("Transgen.jpg"));
        frame.setIconImage(img.getImage());
        frame.pack();
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
