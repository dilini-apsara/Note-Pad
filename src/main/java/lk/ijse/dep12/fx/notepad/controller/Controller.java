package lk.ijse.dep12.fx.notepad.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.*;
import lk.ijse.dep12.fx.notepad.AppInitializer;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    public MenuItem mntAbout;

    public TextArea txtDisplay;
    public AnchorPane rootMain;
    public TextField txtFind;
    public Button btnFindClose;

    private boolean isalreadySaved;
    private File currentFile;
    private boolean isEdited;
    private boolean isOpened;
    private Stage newStage;
    private int selectIndex;

    public void initialize() {
        txtFind.setVisible(false);
        btnFindClose.setVisible(false);
        //set property listener to set the title
        txtDisplay.textProperty().addListener((observble, previous, current) -> {
            if (previous.isEmpty()) isEdited = false;
            else isEdited = true;

            if (isEdited) {
                if (!newStage.getTitle().contains("*")) newStage.setTitle("*" + newStage.getTitle());
            }
        });

        //Close window
        Platform.runLater(() -> {
            newStage = (Stage) rootMain.getScene().getWindow();
            newStage.setOnCloseRequest(windowEvent -> {
                windowEvent.consume();
                if (!newStage.getTitle().contains("*")) newStage.close();
                else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "There is unsaved data. Do you want to exit?", ButtonType.NO, ButtonType.YES);
                    alert.setHeaderText("Exit?");
                    alert.setTitle("Exit Window");
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get() == ButtonType.YES) newStage.close();
                }
            });

        });
    }

    public void mntExitOnAction(ActionEvent event) {
        if (isEdited) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "There is unsaved data. Do you want to exit ?", ButtonType.NO, ButtonType.YES);
            alert.setTitle("Exit Window");
            alert.setHeaderText("Exit ?");
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.YES) Platform.exit();
            else return;
        } else {
            Platform.exit();
        }

    }

    private void loadToTextArea(File currentFile) {
        try (FileInputStream fis = new FileInputStream(currentFile)) {
            byte[] bytes = new byte[(int) currentFile.length()];
            fis.read(bytes);
            txtDisplay.setText(new String(bytes));

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Can not Open the file").showAndWait();
        }
    }

    public void mntOpenOnAction(ActionEvent event) throws IOException {

        isOpened = true;
        fileChooser();
        if (currentFile == null) return;
        if (newStage.getTitle() != "Untitled Document - NOTE PAD") {
            Stage stage = new Stage();
            URL resource = getClass().getResource("/view/MainView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane container = fxmlLoader.load();
            Controller controller = fxmlLoader.getController();
            controller.newStage = stage;
            Scene scene = new Scene(container);
            stage.setScene(scene);
            stage.show();
            stage.setTitle(currentFile.getName()+" - NOTE PAD");
            controller.loadToTextArea(currentFile);
            controller.isalreadySaved = true;
            controller.isEdited = false;
            //System.out.println("curent file" + currentFile);
            controller.currentFile = currentFile;
            //  System.out.println("curent file" + currentFile);
        } else {
            loadToTextArea(currentFile);
            newStage.setTitle(currentFile.getName());
        }
        isEdited = false;
        isOpened = false;
        isalreadySaved = true;
    }

    public void mntSaveAsOnAction(ActionEvent event) {
        isalreadySaved = false;
        mntSaveOnAction(event);

    }


    private void saveToFile(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(txtDisplay.getText().getBytes());

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "failed to save the file.").showAndWait();
        }

    }

    private void fileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plain text file (*.txt)", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));
        fileChooser.setTitle("Folder Selection");
        fileChooser.setInitialDirectory(new File(System.getenv("HOME"), "Documents"));
        currentFile = isOpened ? fileChooser.showOpenDialog(txtDisplay.getScene().getWindow()) : fileChooser.showSaveDialog(txtDisplay.getScene().getWindow());

        if (currentFile == null) return;
        if (!currentFile.getName().contains("."))
            currentFile = new File(currentFile.getParentFile(), currentFile.getName() + ".txt - NOTE PAD");

    }

    public void mntSaveOnAction(ActionEvent event) {
        if (!isalreadySaved) {

            fileChooser();

        }
        if (currentFile == null) return;
        newStage.setTitle(currentFile.getName());
        saveToFile(currentFile);
        isalreadySaved = true;
        //to change the title
        isEdited = false;


    }

    public void mntAboutOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/HelpView.fxml")));
        stage.setScene(scene);
        stage.setTitle("About");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(rootMain.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
    }


    public void mntNewOnAction(ActionEvent event) throws IOException {

        Stage stage = new Stage();
        URL resource = getClass().getResource("/view/MainView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        AnchorPane container = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.newStage = stage;
        stage.setScene(new Scene(container));
        // stage.setTitle("Untitled Document");
        stage.setTitle("Untitled Document - NOTE PAD");
        stage.show();
        stage.centerOnScreen();
        isEdited = false;
        isalreadySaved = false;
    }

    public void mntFindOnAction(ActionEvent actionEvent) throws IOException {
        txtFind.setVisible(true);
        btnFindClose.setVisible(true);

    }


    public void btnFindCloseOnAction(ActionEvent actionEvent) {
        btnFindClose.setVisible(false);
        txtFind.setVisible(false);
    }


    public void OnKeyPressedFind(KeyEvent event) {
        txtFind.requestFocus();
        String findRegX = txtFind.getText();
        String content = txtDisplay.getText();
        Pattern pattern = Pattern.compile(findRegX);
        Matcher matcher = pattern.matcher(content);

        if (event.getCode() == KeyCode.ENTER) {
            if (matcher.find(selectIndex)) {
                selectIndex = matcher.end();
                txtDisplay.selectRange(matcher.start(), selectIndex);
            } else {
                selectIndex = 0;
            }
        }
    }


    public void mntSelectAllOnAction(ActionEvent actionEvent) {

        // can do using only selectAll()
        String regx = ".";

        String displayText = txtDisplay.getText();
        Pattern compiledPattern = Pattern.compile(regx);
        Matcher matcher = compiledPattern.matcher(displayText);
        if (matcher.find()) {
            txtDisplay.selectAll();
        }



    }
}
