/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import com.sun.xml.internal.ws.wsdl.writer.document.OpenAtts;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import stamboom.domain.Administratie;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class SerializationMediator implements IStorageMediator{

    /**
     * bevat de bestandslocatie. Properties is een subclasse van HashTable, een
     * alternatief voor een List. Het verschil is dat een List een volgorde heeft,
     * en een HashTable een key/value index die wordt opgevraagd niet op basis van
     * positie, maar op key.
     */
    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        
        // todo opgave 2
        Administratie admin = null;
        try {
            FileInputStream fis = new FileInputStream(props.getProperty("file"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            admin = (Administratie) ois.readObject();
            ois.close();
            fis.close();  
        } 
        catch(IOException ex){
           System.out.printf("Cannot perform output.", ex.toString());
           
        } 
        catch (ClassNotFoundException ex) {
            Logger.getLogger(SerializationMediator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.printf("Class not found");
        }
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        if (!isCorrectlyConfigured()) {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }

        // todo opgave 2
        try {
            FileOutputStream fos = new FileOutputStream(props.getProperty("file"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(admin);
            oos.close();
            fos.close();   
        } catch(IOException ex){
           System.out.printf("Cannot perform output.", ex.toString());
        }
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en controleert
     * of deze in de juiste vorm is.
     * @param props
     * @return
     */
    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     * Controleert of er een geldig Key/Value paar bestaat in de Properties.
     * De bedoeling is dat er een Key "file" is, en de Value van die Key 
     * een String representatie van een FilePath is (eg. C:\\Users\Username\test.txt).
     * 
     * @return true if config() contains at least a key "file" and the
     * corresponding value is formatted like a file path
     */
    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        return props.containsKey("file") 
                && props.getProperty("file").contains(File.separator);
    }
}
