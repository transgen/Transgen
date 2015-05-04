package com.transgen.forms;

import javax.swing.*;

public class MainForm {
    private JButton createSingle;
    private JButton createBatch;
    private JPanel MainPanel;

    public MainForm() {
        createSingle.addActionListener(e -> new SingleForm().main());
        createBatch.addActionListener(e -> new BatchForm().main());
    }

    public void main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("MainForm");
        frame.setTitle("Barcode Generator");
        frame.setContentPane(new MainForm().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
