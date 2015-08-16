package com.transgen.forms;

import com.transgen.TransGen;
import com.transgen.Utils;
import com.transgen.api.StateGenerator;
import com.transgen.api.enums.AAMVAField;
import com.transgen.api.enums.AAMVAFieldSimple;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;

public class SingleForm {
    private JPanel SingleForm;
    private JButton generateBarcodeButton;
    private JPanel scrollPane;
    private JComboBox chooseAState;
    private JFormattedTextField twoDH;
    private JFormattedTextField twoDW;
    private JFormattedTextField oneDW;
    private JFormattedTextField oneDH;
    private JCheckBox autoPopulateCheckBox;
    private JFormattedTextField UI_fileName;


    private HashMap<String, JTextField> fields;

    public SingleForm(Boolean simple) {
        autoPopulateCheckBox.doClick();
        Object[] sortedArray = TransGen.getInstance().getStateGenerators().keySet().toArray();
        Arrays.sort(sortedArray);
        chooseAState.setModel(new DefaultComboBoxModel(sortedArray));

        //Populate data fields


        autoPopulateCheckBox.addActionListener(e -> populateDataFields(chooseAState, simple));
        chooseAState.addActionListener(e -> populateDataFields(chooseAState, simple));
        populateDataFields(chooseAState, simple);


        //Generate the barcode data
        generateBarcodeButton.addActionListener(e -> generateData());

        //Validate width/height
        KeyAdapter listener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JFormattedTextField jft = (JFormattedTextField) e.getComponent();
                try {
                    int x = Integer.parseInt(jft.getText());
                } catch (NumberFormatException nfe) {
                    try {
                        jft.setText(jft.getText(0, jft.getText().length() - 1));
                    } catch (BadLocationException e1) {
                        jft.setText("");
                    }
                }
            }
        };

        twoDH.addKeyListener(listener);
        twoDW.addKeyListener(listener);
        oneDH.addKeyListener(listener);
        oneDW.addKeyListener(listener);
    }

    public void generateData() {
        if (chooseAState.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Error: You must choose a state.");
        }

        ArrayList<String> data = new ArrayList<String>();
        for (String s : fields.keySet()) data.add(s + "::" + fields.get(s).getText());
        try {
            StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(chooseAState.getSelectedItem()), data.toArray(new String[data.size()]));
            sg.generate(Utils.tryParseInt(twoDW.getText(), 718), Utils.tryParseInt(twoDH.getText(), 200), Utils.tryParseInt(oneDW.getText(), 500), Utils.tryParseInt(oneDH.getText(), 200), UI_fileName.getText());

            JOptionPane.showMessageDialog(null, "Success: Barcodes generated in program root -> " + new File("").getAbsolutePath());
        }
        catch (Exception E){
            JOptionPane.showMessageDialog(null, E.getMessage());
        }
    }

    private void populateDataFields(JComboBox jcb, Boolean simple) {
        String s = (String) jcb.getSelectedItem();
        if (s != null) {
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(s), new String[]{});
                Set<String> aamvaFields = new HashSet<String>();
                if (simple) {
                    for (AAMVAFieldSimple f : AAMVAFieldSimple.values()) {
                        aamvaFields.add(f.name());
                    }
                }
                else{
                    for (AAMVAField f : AAMVAField.values()) {
                        aamvaFields.add(f.name());
                    }
                }

                int amt = 0;
                for (String d : sg.getDocuments()) for (String f : sg.getFields(d)) amt++;

                fields = new HashMap<String, JTextField>();
                scrollPane.removeAll();
                scrollPane.revalidate();
                scrollPane.repaint();
                scrollPane.setLayout(new BorderLayout());
                JPanel labelPanel = new JPanel(new GridLayout(amt, 1));
                JPanel fieldPanel = new JPanel(new GridLayout(amt, 1));
                labelPanel.setMaximumSize(new Dimension(700, 400));
                fieldPanel.setMaximumSize(new Dimension(700, 400));
                scrollPane.add(labelPanel, BorderLayout.WEST);
                scrollPane.add(fieldPanel, BorderLayout.CENTER);

                for (String d : sg.getDocuments()) {
                    for (String f : sg.getFields(d)) {
                        if (simple) {
                            JLabel l = new JLabel((aamvaFields.contains(f) ? (AAMVAFieldSimple.valueOf(f).getElementDesc() + " ") : f));
                            JTextField t = new JTextField();
                            if (autoPopulateCheckBox.isSelected() && sg.getExamples().containsKey(f)){
                                t.setText(sg.getExamples().get(f));
                            }
                            labelPanel.add(l);
                            fieldPanel.add(t);
                            fields.put(f, t);
                        }
                        else{
                            JLabel l = new JLabel((aamvaFields.contains(f) ? (AAMVAField.valueOf(f).getElementDesc() +  " " + "(" + f + ")") : f ));
                            JTextField t = new JTextField();
                            if (autoPopulateCheckBox.isSelected()){
                                t.setText(sg.getExamples().get(f));
                            }
                            labelPanel.add(l);
                            fieldPanel.add(t);
                            fields.put(f, t);
                        }
                    }
                }
            } catch (Exception E) {
                JOptionPane.showMessageDialog(null, E.getMessage());
            }
        }
    }

    public void main(Boolean simple) {
        JFrame frame = new JFrame("SingleForm");
        frame.setTitle("TransGen™ -  Single Barcode");
        frame.setContentPane(this.SingleForm);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageIcon img = new ImageIcon(getClass().getResource("Transgen.jpg"));
        frame.setIconImage(img.getImage());
        frame.pack();
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
