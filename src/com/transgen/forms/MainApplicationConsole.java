package com.transgen.forms;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class MainApplicationConsole extends OutputStream {
    private TextArea console;
    private OutputStream normalOut;
    private boolean isDefault = false;

    public MainApplicationConsole(TextArea console) {
        this.console = console;
        normalOut = System.out;
        console.setText("");
    }

    public void setAsSystemOut() {
        isDefault = true;
        System.setOut(new PrintStream(this, true));
    }

    @Override
    public void write(int b) throws IOException {
        Platform.runLater(() -> {
            console.appendText(String.valueOf((char) b));
        });

        if(isDefault) normalOut.write(b);
    }
}
