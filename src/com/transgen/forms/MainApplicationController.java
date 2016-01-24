package com.transgen.forms;

import com.sun.javafx.application.HostServicesDelegate;
import com.transgen.TransGen;
import com.transgen.Utils;
import com.transgen.api.StateGenerator;
import com.transgen.api.enums.AAMVAField;
import com.transgen.api.enums.AAMVAFieldSimple;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainApplicationController implements Initializable {
    // Main Controls
    @FXML  private TextArea console;

    @FXML private CheckMenuItem show_simple;
    @FXML private CheckMenuItem populate_example_info;
    @FXML private CheckMenuItem populate_dates;
    @FXML private CheckMenuItem populate_id_num;
    @FXML private CheckMenuItem generate_mags;

    @FXML private MenuItem generate;
    @FXML private MenuItem exit;

    @FXML private MenuItem github;
    @FXML private MenuItem documentation;

    @FXML private TabPane main_tab_pane;

    // Single Barcode Tab
    @FXML private ChoiceBox<String> single_barcode_state;
    @FXML private TextField td_width;
    @FXML private TextField td_height;
    @FXML private TextField od_width;
    @FXML private TextField od_height;
    @FXML private TableView data_table;

    // Multiple Barcode Tab
    @FXML private ChoiceBox<String> multi_barcode_state;
    @FXML private Button select_csv;
    @FXML private TextField csv_filename;
    @FXML private TextArea csv_example;

    // Other stuff
    private Stage stage;
    private HostServicesDelegate hostServices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initConsole();

        initNumericFields();

        initCSVSelection();

        initDataTableRows();

        initStateChoiceBoxes();

        initMenuBar();
    }

    public void setStage(Stage s) {
        this.stage = s;
    }

    public void setHostServices(HostServicesDelegate hsd) {
        this.hostServices = hsd;
    }

    private void initConsole() {
        MainApplicationConsole mac = new MainApplicationConsole(console);
        mac.setAsSystemOut();
    }

    private void initDataTableRows() {
        TableColumn fieldColumn = (TableColumn) data_table.getColumns().get(0);
        TableColumn valueColumn = (TableColumn) data_table.getColumns().get(1);

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AAMVAFieldValuePair, String>>() {
            @Override public void handle(TableColumn.CellEditEvent<AAMVAFieldValuePair, String> t) {
                if(t.getTableView().getItems().get(t.getTablePosition().getRow()).getField() == "DAQ" && Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_id_num"))) {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue("**AUTOMATICALLY POPULATED ON GENERATE**");
                } else t.getTableView().getItems().get(t.getTablePosition().getRow()).setValue(t.getNewValue());
            }
        });

        valueColumn.setCellValueFactory(new PropertyValueFactory<AAMVAFieldValuePair, String>("value"));
        fieldColumn.setCellValueFactory(new PropertyValueFactory<AAMVAFieldValuePair, String>("desc"));
    }

    private void initStateChoiceBoxes() {
        String[] sortedArray = TransGen.getInstance().getStateGenerators().keySet().toArray(new String[0]);
        Arrays.sort(sortedArray);
        final ObservableList<String> states = FXCollections.observableArrayList(sortedArray);

        multi_barcode_state.setItems(states);
        single_barcode_state.setItems(states);

        // Fill CSV example when new multi state is selected
        multi_barcode_state.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String s = states.get((Integer) newValue);
            boolean simple = show_simple.isSelected();

            if(TransGen.debug) System.out.println("[DEBUG] Selected '" + s + "' for multiple barcodes");

            populateCSVExample(csv_example, s, show_simple.isSelected());
        });

        // Fill data table when new single state is selected
        single_barcode_state.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String s = states.get((Integer) newValue);
            boolean simple = show_simple.isSelected();

            if(TransGen.debug) System.out.println("[DEBUG] Selected '" + s + "' for single barcode");

            populateDataTable(data_table, s, show_simple.isSelected());
        });

        multi_barcode_state.getSelectionModel().selectFirst();
        single_barcode_state.getSelectionModel().selectFirst();
    }

    private void initMenuBar() {
        // Load initial menu bar states from settings
        final Properties props = TransGen.getInstance().getProperties();
        show_simple.setSelected(Boolean.valueOf(props.getProperty("show_simple")));
        populate_example_info.setSelected(Boolean.valueOf(props.getProperty("populate_example_info")));
        populate_dates.setSelected(Boolean.valueOf(props.getProperty("populate_dates")));
        populate_id_num.setSelected(Boolean.valueOf(props.getProperty("populate_id_num")));
        generate_mags.setSelected(Boolean.valueOf(props.getProperty("generate_mags")));

        refreshData();

        // Refresh data and save to properties file everytime a setting is changed
        ChangeListener<Boolean> settingChanged = (observable, oldValue, newValue) -> {
            props.setProperty("show_simple", String.valueOf(show_simple.isSelected()));
            props.setProperty("populate_example_info", String.valueOf(populate_example_info.isSelected()));
            props.setProperty("populate_dates", String.valueOf(populate_dates.isSelected()));
            props.setProperty("populate_id_num", String.valueOf(populate_id_num.isSelected()));
            props.setProperty("generate_mags", String.valueOf(generate_mags.isSelected()));

            try {
                TransGen.getInstance().saveProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }

            refreshData();
        };

        show_simple.selectedProperty().addListener(settingChanged);
        populate_example_info.selectedProperty().addListener(settingChanged);
        populate_dates.selectedProperty().addListener(settingChanged);
        populate_id_num.selectedProperty().addListener(settingChanged);
        generate_mags.selectedProperty().addListener(settingChanged);

        documentation.setOnAction(event -> {
            hostServices.showDocument("https://infotomb.com/8ez4n.pdf");
        });

        github.setOnAction(event -> {
            hostServices.showDocument("https://github.com/transgen/Transgen");
        });

        generate.setOnAction(event -> {
            if(main_tab_pane.getSelectionModel().isSelected(0)) { // Single generation selected
                System.out.println("[DEBUG] Generating single barcode");
                generateSingleBarcode();
            } else if(main_tab_pane.getSelectionModel().isSelected(1)) { // multiple generation selected
                System.out.println("[DEBUG] Generating multiple barcodes");
                generateMultipleBarcodes();
            }
        });

        exit.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void initCSVSelection() {
        select_csv.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
            fileChooser.setTitle("Choose the CSV file to generate from");
            File f  = fileChooser.showOpenDialog(stage);

            csv_filename.setText(f.getAbsolutePath());
        });
    }

    private void validateNumericField(String newValue, String oldValue, TextField text) {
        if (!newValue.matches("\\d*")) {
            text.setText(oldValue);
        }
    }

    private void initNumericFields() {
        td_width.textProperty().addListener((observable, oldValue, newValue) -> validateNumericField(newValue, oldValue, td_width));
        td_height.textProperty().addListener((observable, oldValue, newValue) -> validateNumericField(newValue, oldValue, td_height));
        od_width.textProperty().addListener((observable, oldValue, newValue) -> validateNumericField(newValue, oldValue, od_width));
        od_height.textProperty().addListener((observable, oldValue, newValue) -> validateNumericField(newValue, oldValue, od_height));
    }

    private void generateSingleBarcode() {
        Properties p = TransGen.getInstance().getProperties();

        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose directory to save barcodes");
        dc.setInitialDirectory(new File("./"));
        File dir = dc.showDialog(stage);

        if (single_barcode_state.getValue() == null) {
            System.out.println("[ERROR] You must choose a state.");
        }

        ArrayList<String> data = new ArrayList<String>();
        for (Object o : data_table.getItems()) {
            AAMVAFieldValuePair afp = (AAMVAFieldValuePair) o;

            String field = afp.getField();
            String value = afp.getValue();

            data.add(field + "::" + value);
            System.out.println(afp.getField() + "::" + afp.getValue());
        }
        try {
            StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(single_barcode_state.getValue()), data.toArray(new String[data.size()]));

            // Auto-gen client ID
            if(Boolean.valueOf(p.getProperty("populate_id_num"))) {
                try {
                    sg.data.replace("DAQ", sg.getClientId());
                } catch(Exception e) {
                    System.out.println("[ERROR] Problem generating ID number, assure dates are not malformed and names exist.");
                    return;
                }
            }

            sg.generate(Utils.tryParseInt(td_width.getText(), 718), Utils.tryParseInt(td_height.getText(), 200), Utils.tryParseInt(od_width.getText(), 500), Utils.tryParseInt(od_height.getText(), 200), dir.getAbsolutePath(), generate_mags.isSelected());
            System.out.println("[SUCCESS] Barcodes generated in -> " + dir.getAbsolutePath());
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("[ERROR] Unknown error occurred: " + e.getMessage() + " with " + dir.getAbsolutePath());
        }
    }

    private void generateMultipleBarcodes() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose directory to save barcodes");
        dc.setInitialDirectory(new File("./"));
        File dir = dc.showDialog(stage);

        File csv = new File(csv_filename.getText());
        if (!csv.exists()) {
            System.out.println("[ERROR] the selected file does not exist or no file selected.");
            return;
        }

        Boolean created = false;
        ArrayList<String> lines = new ArrayList<>();

        try {
            lines.addAll(Files.readAllLines(Paths.get(csv.getAbsolutePath()), Charset.defaultCharset()).stream().collect(Collectors.toList()));
        } catch (IOException e) {
            System.out.println("[ERROR] couldn't read CSV file.");
            return;
        }

        String delim = ",";
        String regex = "(?<!\\\\)" + Pattern.quote(delim);
        ArrayList<String> data = new ArrayList<>();
        String[] header = lines.get(0).split(",");

        if (multi_barcode_state.getValue() == null)  System.out.println("[ERROR] You must choose a state.");
        if (multi_barcode_state.getValue() != null && !header[0].equalsIgnoreCase(multi_barcode_state.getValue())) System.out.println("[WARNING] CSV state code does not match script state code.");

        for (int i = 1; i < lines.size(); i++) {
            String[] l = lines.get(i).split(regex, -1);
            for (int j = 0; j < l.length; j++) {
                l[j] = l[j].replaceAll("\\\\","");
                data.add(header[j] + "::" + l[j]);
            }
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(multi_barcode_state.getValue()), data.toArray(new String[data.size()]));
                sg.generate(Utils.tryParseInt(l[2], 718), Utils.tryParseInt(l[3], 200), Utils.tryParseInt(l[4], 500), Utils.tryParseInt(l[5], 200), dir.getAbsolutePath(), generate_mags.isSelected());
                created = true;
            }
            catch(Exception e) {
                System.out.println("[ERROR] Incorrect CSV format: " + e.toString());
            }

        }

        if (created) System.out.println("[SUCCESS] Barcodes generated in -> " + dir.getAbsolutePath());
    }

    private void populateDataTable(TableView table, String state, boolean simple) {
        if (state != null) {
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(state), new String[]{});
                Set<String> aamvaFields = new HashSet<>();

                if (simple) for (AAMVAFieldSimple f : AAMVAFieldSimple.values())  aamvaFields.add(f.name());
                else for (AAMVAField f : AAMVAField.values()) aamvaFields.add(f.name());

                ObservableList<AAMVAFieldValuePair> data = FXCollections.observableArrayList();
                for (String d : sg.getDocuments()) {
                    for (String f : sg.getFields(d)) {
                        if (simple) {
                            if(!Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_example_info"))) data.add(new AAMVAFieldValuePair(
                                    f,
                                    (aamvaFields.contains(f) ? (AAMVAFieldSimple.valueOf(f).getElementDesc()) : (f.startsWith("Z") ? "State Specific " + f.substring(2, 3) : f)),
                                    (f == "DAQ" && Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_id_num"))) ? "**AUTO POPULATED ON GENERATE**" : ""
                            ));
                            else data.add(new AAMVAFieldValuePair(
                                    f,
                                    (aamvaFields.contains(f) ? (AAMVAFieldSimple.valueOf(f).getElementDesc()) : (f.startsWith("Z") ? "State Specific " + f.substring(2, 3) : f)),
                                    (f == "DAQ" && Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_id_num"))) ? "**AUTO POPULATED ON GENERATE**" : sg.getExamples().get(f)
                            ));
                        }
                        else{
                            if(!Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_example_info"))) data.add(new AAMVAFieldValuePair(
                                    f,
                                    (aamvaFields.contains(f) ? (AAMVAField.valueOf(f).getElementDesc() +  " " + "(" + f + ")") : (f.startsWith("Z") ? "State Specific " + f.substring(2, 3) : f)),
                                    (f == "DAQ" && Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_id_num"))) ? "**AUTOMATICALLY POPULATED ON GENERATE**" : ""
                            ));
                            else data.add(new AAMVAFieldValuePair(
                                    f,
                                    (aamvaFields.contains(f) ? (AAMVAField.valueOf(f).getElementDesc() +  " " + "(" + f + ")") : (f.startsWith("Z") ? "State Specific " + f.substring(2, 3) : f)),
                                    (f == "DAQ" && Boolean.valueOf(TransGen.getInstance().getProperties().getProperty("populate_id_num"))) ? "**AUTOMATICALLY POPULATED ON GENERATE**" : sg.getExamples().get(f)
                            ));
                        }
                    }
                }
                table.setItems(data);
            } catch (Exception e) {
                System.out.println("[Error] " + e.getMessage());
            }
        }
    }

    private void populateCSVExample(TextArea text, String state, boolean simple) {
        if (state != null) {
            try {
                StateGenerator sg = StateGenerator.instantiateStateScript(TransGen.getInstance().getStateGenerators().get(state), new String[]{});
                String ex = sg.getStateCode() + ",CUSTOM_FILENAME_(OPTIONAL),2D_WIDTH,2D_HEIGHT,1D_WIDTH,1D_HEIGHT,";
                for (String d : sg.getDocuments()) {
                    for (String f : sg.getFields(d)) {
                        ex += f + ",";
                    }
                }
                ex = ex.replaceAll(";$", "");
                ex = ex.substring(0, ex.length() - 1);
                ex += "\n";

                Set<String> aamvaFields = new HashSet<String>();
                for (AAMVAField f : AAMVAField.values()) {
                    aamvaFields.add(f.name());
                }
                ex += "<STATE>,<CUSTOM_FILENAME_(OPTIONAL)>,<2D_WIDTH>,<2D_HEIGHT>,<1D_WIDTH>,<1D_HEIGHT>,";
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
                ex = ex.substring(0, ex.length() - 1);
                ex += "\n";

                text.setWrapText(true);
                text.setText(ex);
            }
            catch(Exception E){
                E.printStackTrace();
            }

        }
    }

    private void refreshData() {
        populateDataTable(data_table, single_barcode_state.getValue(), show_simple.isSelected());
        populateCSVExample(csv_example, multi_barcode_state.getValue(), show_simple.isSelected());
    }

    private void showDateDialog() {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        FXMLLoader f = new FXMLLoader((getClass().getResource("/date-dialog.fxml")));
        Parent fxmlRoot = null;
        try {
            fxmlRoot = f.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(fxmlRoot);
        dialog.setScene(scene);
        dialog.show();
    }
}
