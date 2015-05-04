package com.transgen.forms;

import com.transgen.TransGen;
import com.transgen.api.StateGenerator;
import com.transgen.api.enums.AAMVAField;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SingleForm {
    private JPanel SingleForm;
    private JButton generateBarcodeButton;
    private JPanel scrollPane;
    private JComboBox chooseAState;
    private JFormattedTextField twoDH;
    private JFormattedTextField twoDW;
    private JFormattedTextField oneDW;
    private JFormattedTextField oneDH;

    private HashMap<String, JTextField> fields;

    public SingleForm() {
        chooseAState.setModel(new DefaultComboBoxModel(TransGen.getInstance().getStateGenerators().keySet().toArray()));

        //Populate data fields
        chooseAState.addActionListener(e -> populateDataFields(chooseAState));
        populateDataFields(chooseAState);

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
            sg.generate(Integer.parseInt(twoDW.getText()), Integer.parseInt(twoDH.getText()), Integer.parseInt(oneDW.getText()), Integer.parseInt(oneDH.getText()));

            JOptionPane.showMessageDialog(null, "Success: Barcodes generated in program root -> " + new File("").getAbsolutePath());
        }
        catch (Exception E){
            JOptionPane.showMessageDialog(null, E.getMessage());
        }

    }

    private void populateDataFields(JComboBox jcb) {
        String s = (String) jcb.getSelectedItem();
        if (s != null) {
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(s), new String[]{});
                Set<String> aamvaFields = new HashSet<String>();
                for (AAMVAField f : AAMVAField.values()) {
                    aamvaFields.add(f.name());
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
                labelPanel.setMaximumSize(new Dimension(1000, 400));
                fieldPanel.setMaximumSize(new Dimension(1000, 400));
                scrollPane.add(labelPanel, BorderLayout.WEST);
                scrollPane.add(fieldPanel, BorderLayout.CENTER);

                for (String d : sg.getDocuments()) {
                    for (String f : sg.getFields(d)) {
                        JLabel l = new JLabel((aamvaFields.contains(f) ? (AAMVAField.valueOf(f).getElementDesc() + " ") : "") + "(" + f + ")");
                        JTextField t = new JTextField();
                        labelPanel.add(l);
                        fieldPanel.add(t);
                        fields.put(f, t);
                    }
                }

            } catch (Exception E) {
                JOptionPane.showMessageDialog(null, E.getMessage());
            }
        }
    }

    public void main() {
        JFrame frame = new JFrame("SingleForm");
        frame.setTitle("Generate A Single Barcode");
        frame.setContentPane(new SingleForm().SingleForm);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(500, 700);
        frame.setVisible(true);
    }
}
