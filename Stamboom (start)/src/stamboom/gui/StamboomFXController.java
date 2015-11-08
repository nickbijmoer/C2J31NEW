/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

import javax.swing.*;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    private ObservableList<Persoon> personen;
    private ObservableList<Gezin> gezinnen;
    private ObservableList<Persoon> kinderen;
    private ObservableList<Gezin> alsOuderBetrokkenIn;

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML TextArea taStamboom;
    @FXML Button btStamboom;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;

    //GEZIN
    @FXML ComboBox cbGezin;
    @FXML TextField tfGezinNr;
    @FXML TextField tfGezinOuder1;
    @FXML TextField tfGezinOuder2;
    @FXML TextField tfGezinHuwelijk;
    @FXML TextField tfGezinScheiding;
    @FXML TextArea taGezinKinderen;


    //NIEUWE PERSOON
    @FXML ComboBox cbNieuwPersoonGeslacht;
    @FXML ComboBox cbNieuwPersoonOuderlijkGezinNieuw;
    @FXML TextField tfNieuwPersoonVoornamen;
    @FXML TextField tfNieuwPersoonTussenVoegsel;
    @FXML TextField tfNieuwPersoonAchternaam;
    @FXML TextField tfNieuwPersoonGeboortePlaats;
    @FXML DatePicker dpNieuwPersonGeboorteDatum;




    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addTestData();
        this.personen = this.getAdministratie().getPersonen();
        this.gezinnen = this.getAdministratie().getGezinnen();
        initComboboxes();
        withDatabase = false;

    }

    private void addTestData() {
        //Persoon
        String[] voornaam = {"Barry"};
        GregorianCalendar gebDat = new GregorianCalendar();
        gebDat.set(1996, 06, 22);
        //Persoon2
        String[] voornaam2= {"Minoes"};
        //Persoon3
        GregorianCalendar gebDat2 = new GregorianCalendar();
        gebDat2.set(2012, 06, 10);
        String[] voornaam3 = {"Peter"};

        this.getAdministratie().addPersoon(Geslacht.MAN, voornaam, "Fish", "", gebDat, "Boston", null);
        this.getAdministratie().addPersoon(Geslacht.VROUW, voornaam2, "Bisschop", "", gebDat, "Helmond", null);
        this.getAdministratie().addOngehuwdGezin(this.getAdministratie().getPersoon(1), this.getAdministratie().getPersoon(2));
        this.getAdministratie().addPersoon(Geslacht.MAN, voornaam3, "Bier", "", gebDat2, "Amstel", getAdministratie().getGezin(1));
    }

    private void initComboboxes() {
        //todo opgave 3
        ObservableList<Persoon> personen = FXCollections.observableArrayList(this.getAdministratie().getPersonen());
        ObservableList<Gezin> gezinnen = FXCollections.observableArrayList(this.getAdministratie().getGezinnen());

        this.cbPersonen.setItems(personen);
        this.cbOuder1Invoer.setItems(personen);
        this.cbOuder2Invoer.setItems(personen);
        this.cbGezin.setItems(gezinnen);
        ObservableList<Geslacht> geslachten = FXCollections.observableList(new ArrayList<Geslacht>());
        geslachten.add(Geslacht.MAN);
        geslachten.add(Geslacht.VROUW);
        cbNieuwPersoonGeslacht.setItems(geslachten);
        cbNieuwPersoonOuderlijkGezinNieuw.setItems(gezinnen);
        cbOuderlijkGezin.setItems(gezinnen);



    }

    public void setHuwdatum(Event evt) {
        Gezin g = (Gezin)cbGezin.getSelectionModel().getSelectedItem();
        Calendar c = StringUtilities.datum(tfGezinHuwelijk.getText());
        this.getAdministratie().setHuwelijk(g, c);
    }

    public void setScheidingsdatum(Event evt) {
        Gezin g = (Gezin)cbGezin.getSelectionModel().getSelectedItem();
        Calendar c = StringUtilities.datum(tfGezinScheiding.getText());
        this.getAdministratie().setScheiding(g, c);
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems(persoon.getPersoonGezin());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        if(getAdministratie().setOuders(p, ouderlijkGezin)){
            showDialog("Success", ouderlijkGezin.toString()
                    + " is nu het ouderlijk gezin van " + p.getNaam());
        }
        
    }

    public void selectGezin(Event evt) {
        // todo opgave 3

        showGezin((Gezin)cbGezin.getSelectionModel().getSelectedItem());

    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3
        //clearTabGezin();

        if (gezin != null)
        {
            tfGezinNr.setText("" + gezin.getNr());
            tfGezinOuder1.setText(gezin.getOuder1().getNaam());
            tfGezinOuder2.setText(gezin.getOuder2().getNaam());

            // Datums goed omzetten
            if (gezin.getHuwelijksdatum() != null) {
                Calendar c = gezin.getHuwelijksdatum();
                tfGezinHuwelijk.setText(calendarToString(c));
            }

            if (gezin.getScheidingsdatum() != null) {
                Calendar c = gezin.getScheidingsdatum();
                tfGezinScheiding.setText(calendarToString(c));
            }

            this.kinderen = FXCollections.observableArrayList(gezin.getKinderen());

            for (Persoon p : this.getKinderen()) {
                taGezinKinderen.setText(taGezinKinderen.getText() + p.getNaam() + "; " );
            }

        }

    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        clearTabPersoon();
    }

    public void okPersoonInvoer(Event evt) {
        // todo opgave 3
        Geslacht geslacht = (Geslacht)cbNieuwPersoonGeslacht.getSelectionModel().getSelectedItem();

        String[] voornamen = tfNieuwPersoonVoornamen.getText().split(" ");

        Gezin ouderlijkGezin = null;

        if (this.cbNieuwPersoonOuderlijkGezinNieuw.getSelectionModel().getSelectedItem() != null) {
            ouderlijkGezin = (Gezin) this.cbNieuwPersoonOuderlijkGezinNieuw.getSelectionModel().getSelectedItem();
        }

        showDialog("Gelukt", getAdministratie().addPersoon(geslacht, voornamen, tfNieuwPersoonAchternaam.getText(), tfNieuwPersoonTussenVoegsel.getText(),
                StringUtilities.datum(dpNieuwPersonGeboorteDatum.getEditor().getText()), tfNieuwPersoonGeboortePlaats.getText(), ouderlijkGezin).getNaam() + " is toegevoegd!");

    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk niet geldig");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if(scheidingsdatum != null){
                        getAdministratie().setScheiding(g, scheidingsdatum);
                    }
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin niet geldig");
            }
        }

        clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        // todo opgave 3
        taStamboom.clear();
        if (!this.tfPersoonNr.getText().trim().isEmpty()) {
            //showDialog("Stamboom", getAdministratie().getPersoon(Integer.parseInt(this.tfPersoonNr.getText())).stamboomAlsString());
            taStamboom.setText(getAdministratie().getPersoon(Integer.parseInt(this.tfPersoonNr.getText())).stamboomAlsString());
        }
//        taStamboom.clear();
//        try
//        {
//            Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
//            taStamboom.setText(persoon.stamboomAlsString());            
//        }
//        catch(Exception ex)
//        {
//            JOptionPane.showConfirmDialog(null, "Select a person first");
//        }
        
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) throws IOException, SQLException, ClassNotFoundException {
        // todo opgave 3
        if (!withDatabase) {

            JFileChooser jf = new JFileChooser();
            jf.showOpenDialog(jf);
            File bestand = new File(jf.getSelectedFile().toString());
            this.deserialize(bestand);
            System.out.println(this.getAdministratie().getPersonen().size());
            initComboboxes();
            showDialog("Geslaagd", "Stamboom is succesvol opgehaald uit het bestand: "+ jf.getSelectedFile().toString());
        }

        else {
            this.loadFromDatabase();
            initComboboxes();
            showDialog("Geslaagd", "Stamboom is succesvol opgehaald uit de database");
        }


    }

    
    public void saveStamboom(Event evt) throws SQLException, IOException, ClassNotFoundException {
        // todo opgave 3
        if (!withDatabase) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sla Stamboom op");
            File f = fileChooser.showSaveDialog(new Stage());
            try {

                this.serialize(f);
                showDialog("Geslaagd", "Stamboom is succesvol opgeslagen in het bestand");

            } catch (Exception exc) {
                exc.fillInStackTrace();
            }
        }
        else
        {
            // TODO: fill in opgave 4
            this.saveToDatabase();
            showDialog("Geslaagd", "Alles is succesvol opgeslagen in de database");
        }
    }



    
    public void closeApplication(Event evt) throws SQLException, IOException, ClassNotFoundException {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
        // Wat is hier fout aan?
       initComboboxes();

    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        //todo opgave 3
        tfNieuwPersoonAchternaam.clear();
        tfNieuwPersoonGeboortePlaats.clear();
        tfNieuwPersoonTussenVoegsel.clear();
        tfNieuwPersoonVoornamen.clear();
        //clear dpNieuwPersonGeboorteDatum
        dpNieuwPersonGeboorteDatum.getEditor().clear();
        cbNieuwPersoonGeslacht.getSelectionModel().clearSelection();
        cbNieuwPersoonOuderlijkGezinNieuw.getSelectionModel().clearSelection();
        
    }

    
    private void clearTabGezinInvoer() {
        //todo opgave 3
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
    
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        cbPersonen.setItems(personen);
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezin() {
        // todo opgave 3
        tfGezinHuwelijk.clear();
        tfGezinScheiding.clear();
        tfGezinNr.clear();
        tfGezinOuder1.clear();
        tfGezinOuder2.clear();
        cbGezin.getSelectionModel().clearSelection();
        taGezinKinderen.clear();


       
    }



    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

    // http://stackoverflow.com/questions/10829942/using-gregoriancalendar-with-simpledateformat
    private String calendarToString(Calendar calendar){
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());
        return dateFormatted;
    }


    public ObservableList<Persoon> getKinderen() {
        return (ObservableList<Persoon>) FXCollections.unmodifiableObservableList(kinderen);
    }

}