/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import stamboom.controller.StamboomController;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;
import static stamboom.util.StringUtilities.datumString;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

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
    @FXML Button btStamboom;
    @FXML TextArea taStamboom;
    
    //INVOER PERSOON
    @FXML TextField tfVoornaamIN;
    @FXML TextField tfTussenIN;
    @FXML TextField tfAchternaamIn;
    @FXML TextField tfGeslachtIn;
    @FXML TextField tfGebDatIn;
    @FXML TextField tfGebPlaatsIn;
    @FXML ComboBox cbOuderlijkIn;
    @FXML ListView lvBetrokkenIn;
    @FXML Button btnOKPersoon;
    @FXML Button btnCancelPersoon;
    
    //GEZIN
    @FXML ComboBox cbGezin;
    @FXML  Label lbOuder1;
    @FXML Label lbOuder2;
    @FXML TextField tfHuwelijk;
    @FXML TextField tfScheiding;
    //@FXML ListView lvKinderen;
    @FXML Button btnScheiding;
    @FXML Button btnHuwelijk;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = false;
    }

    private void initComboboxes() {
        //todo opgave 3           
        cbPersonen.setItems(getAdministratie().getObservablePersonen());
        cbOuder1Invoer.setItems(getAdministratie().getObservablePersonen());        
        cbOuder2Invoer.setItems(getAdministratie().getObservablePersonen());
        cbGezin.setItems(getAdministratie().getObservableGezinnen());
        cbOuderlijkIn.setItems(getAdministratie().getObservableGezinnen());
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(Integer.toString(persoon.getNr()));
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
            lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
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
        Gezin gezin = (Gezin) cbGezin.getSelectionModel().getSelectedItem();
        showGezin(gezin);
    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3
        if(gezin == null)
        {
            clearTabGezin();
        }
        else
        {
            lbOuder1.setText(gezin.getOuder1().getNaam());
            if(gezin.getOuder2() != null)
        {
            this.lbOuder2.setText(gezin.getOuder2().getNaam());
        }
        if(gezin.getHuwelijksdatum() != null)
        {
            this.tfHuwelijk.setText(datumString(gezin.getHuwelijksdatum()));
            
            if(gezin.getScheidingsdatum() != null)
            {
                this.tfScheiding.setText(datumString(gezin.getScheidingsdatum()));
            }
        }       
        
            //lvKinderen.setItems((ObservableList) gezin.getKinderen());
        }
    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3
        Calendar huwelijk;
       try
       {
           huwelijk = StringUtilities.datum(tfHuwelijk.getText());
       } catch(IllegalArgumentException exc){
           showDialog("Warning", "Geboortedatum :" + exc.getMessage());
           return;
       }
        
        getAdministratie().setHuwelijk((Gezin) cbGezin.getSelectionModel().getSelectedItem(), huwelijk);
        clearTabGezin();
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3
        Calendar Scheiding;
       try
       {
           Scheiding = StringUtilities.datum(tfScheiding.getText());
       } catch(IllegalArgumentException exc){
           showDialog("Warning", "Geboortedatum :" + exc.getMessage());
           return;
       }
        getAdministratie().setScheiding((Gezin) cbGezin.getSelectionModel().getSelectedItem(), Scheiding);getAdministratie().setHuwelijk((Gezin) cbGezin.getSelectionModel().getSelectedItem(), Scheiding);
        clearTabGezin();
    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        // todo opgave 3
       String geslacht;
       if(tfGeslachtIn.getText().equalsIgnoreCase("MAN"))
       {
           geslacht = "MAN";
       }
       else if(tfGeslachtIn.getText().equalsIgnoreCase("VROUW"))
       {
           geslacht = "VROUW";
       }
       else
       {
           throw new IllegalArgumentException("Geslacht onbekend."); 
       }
       
       String[] vnamen = null;
       if(!tfVoornaamIN.getText().equalsIgnoreCase(null))
       {
           vnamen = tfVoornaamIN.getText().split(" ");
       }
       
//       String temp = null;
//       int names = 0;
//       for(int i = 0; i < tfVoornaamIN.getText().length(); i++)
//       {
//           if (tfVoornaamIN.getText().substring(i, 2).equalsIgnoreCase(" "))
//           {
//               vnamen[names] = temp;
//               names++;
//           }
//           else
//           {
//               temp = temp + tfVoornaamIN.getText().substring(i, 2);
//           }
//       }
       
       Calendar GebDatum;
       try
       {
           GebDatum = StringUtilities.datum(tfGebDatIn.getText());
       } catch(IllegalArgumentException exc){
           showDialog("Warning", "Geboortedatum :" + exc.getMessage());
           return;
       }
       
       Gezin Found = null;
        for (Gezin gezin: getAdministratie().getGezinnen()) {
            if (gezin == (Gezin)cbOuderlijkIn.getSelectionModel().getSelectedItem()) {
                Found = gezin;
            }
        }
        
       getAdministratie().addPersoon(Geslacht.valueOf(geslacht), vnamen, tfAchternaamIn.getText(), tfTussenIN.getText(), GebDatum, tfGebPlaatsIn.getText(), Found);
       clearTabPersoonInvoer();
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
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
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
            try{
               g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
                } 
            }
            catch(Exception ex)
            {
                JOptionPane.showConfirmDialog(null, "Error: " + ex.getMessage());
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
        try
        {
            Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
            taStamboom.setText(persoon.stamboomAlsString());            
        }
        catch(Exception ex)
        {
            JOptionPane.showConfirmDialog(null, "Select a person first");
        }
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        // todo opgave 3
//            String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        path = path.substring(0, path.length() - 38) + "/administratieFiles";
//        
//        JFileChooser fileChooser = new JFileChooser(path);
//        FileNameExtensionFilter filter = new FileNameExtensionFilter("Administraties", "administratie");
//        fileChooser.setFileFilter(filter);
//        //JOptionPane.showConfirmDialog(null, path);
//        
//        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
//        {
//            File file = fileChooser.getSelectedFile();
//            JOptionPane.showConfirmDialog(null, file.getName());
//            try
//            {
//                deserialize(file);
//                Administratie check = getAdministratie();
//                clearTabs();
//                initComboboxes();
//            }
//            catch (IOException ex)
//            {
//                JOptionPane.showConfirmDialog(null, "Bestand kon niet geladen worden: " + ex.getMessage());
//            }
//        }
        if(withDatabase)
        {
            try
            {
                loadFromDatabase();
            }
            catch (IOException ex)
            {
                System.out.println("Database could not be loaded.");
            }
        }
        else{
            try{
            JFileChooser jf = new JFileChooser();
            jf.showOpenDialog(jf);
            File bestand = new File(jf.getSelectedFile().toString());
            this.deserialize(bestand);
            System.out.println(this.getAdministratie().getPersonen().size());
            initComboboxes();
            showDialog("Geslaagd", "Stamboom is succesvol opgehaald uit het bestand: "+ jf.getSelectedFile().toString());
        }
        catch(Exception ex){
           showDialog("Exception", ex.toString());
        }
        }
        
        
            
    }

    
    public void saveStamboom(Event evt) {
        // todo opgave 3
//        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        path = path.substring(0, path.length() - 38) + "/administratieFiles";
//        
//        JFileChooser fileChooser = new JFileChooser(path);
//        
//        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//          File file = fileChooser.getSelectedFile();
//                    
//            try
//            {
//                serialize(file);
//            }
//            catch (IOException ex)
//            {
//                JOptionPane.showConfirmDialog(null, "Bestand kon niet opgeslagen worden: " + ex.getMessage());
//            }
//        }
        if(withDatabase)
        {
            try
        {
            saveToDatabase();
        }
        catch (IOException ex)
        {
            System.out.println("Database could not be saved.");
        }
        }
        else{
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
        
        
        
        //try{
//           
//           
//           
//           
////           File f = new File("stamboom.dat");
////           super.serialize(f);
//           
////           FileOutputStream fout = new FileOutputStream("c:\\address.ser");
////		ObjectOutputStream oos = new ObjectOutputStream(fout);   
////		oos.writeObject(address);
////		oos.close();
////		System.out.println("Done");
//       }
//       catch(Exception ex){
//           showDialog("Exception", ex.toString());
//       }
                
    }

    
    public void closeApplication(Event evt) {
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
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        //todo opgave 3
        tfVoornaamIN.clear();
        tfTussenIN.clear();
        tfAchternaamIn.clear();
        tfGeslachtIn.clear();
        tfGebDatIn.clear();
        tfGebPlaatsIn.clear();
        cbOuderlijkIn.getSelectionModel().clearSelection();
        lvBetrokkenIn.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezinInvoer() {
        //todo opgave 3
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
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
        cbGezin.getSelectionModel().clearSelection();
        lbOuder1.setText("");
        lbOuder2.setText("");
        tfHuwelijk.clear();
        tfScheiding.clear();
        //lvKinderen.setItems(FXCollections.emptyObservableList());
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
