package com.transgen.forms;

import com.alee.laf.WebLookAndFeel;

import javax.swing.*;

public class MainForm {
    private JButton createSingle;
    private JButton createBatch;
    private JPanel MainPanel;
    private JCheckBox simpleDesciptionsCheckBox;

    public MainForm() {
        createSingle.addActionListener(e -> new SingleForm(simpleDesciptionsCheckBox.isSelected()).main(simpleDesciptionsCheckBox.isSelected()));
        createBatch.addActionListener(e -> new BatchForm(simpleDesciptionsCheckBox.isSelected()).main(simpleDesciptionsCheckBox.isSelected()));
        simpleDesciptionsCheckBox.doClick();
    }

    public void main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        WebLookAndFeel.install();
        JFrame frame = new JFrame("MainForm");
        frame.setTitle("TransGen™ 2D Barcode Generator");
        frame.setContentPane(new MainForm().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon(getClass().getResource("Transgen.jpg"));
        frame.setIconImage(img.getImage());

        frame.pack();
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);


    }
}
