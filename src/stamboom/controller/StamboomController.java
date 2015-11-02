/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.storage.DatabaseMediator;
import stamboom.storage.IStorageMediator;
import stamboom.storage.SerializationMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */
    public void serialize(File bestand) throws IOException {
        //todo opgave 2
        SerializationMediator serializationMediator = new SerializationMediator();

        Properties props = new Properties();

        props.setProperty("file", String.valueOf(bestand.getAbsoluteFile()));

        if(serializationMediator.configure(props) == false)
            throw new IOException("Properties could not be loaded");

        serializationMediator.save(this.admin);
    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException {
        //todo opgave 2
        SerializationMediator serializationMediator = new SerializationMediator();

        Properties props = new Properties();

        props.setProperty("file", String.valueOf(bestand.getAbsoluteFile()));

        serializationMediator.configure(props);

        this.admin = serializationMediator.load();
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {
        if (!(storageMediator instanceof DatabaseMediator)) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("database.properties")) {
                props.load(in);
            }
            //storageMediator = new DatabaseMediator(props);
            storageMediator = new DatabaseMediator(props);
        }
    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        //todo opgave 4
        //DatabaseMediator databaseMediator = new DatabaseMediator();
        Properties p = new Properties();

        FileInputStream fin = new FileInputStream("database.properties");
        p.load(fin);

        DatabaseMediator databaseMediator = new DatabaseMediator(p);

        this.admin = databaseMediator.load();
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        //todo opgave 4
         if(this.storageMediator == null)
        {
            Properties p = new Properties();

            FileInputStream fin = new FileInputStream("database.properties");

            p.load(fin);

            DatabaseMediator databaseMediator = new DatabaseMediator(p);

            databaseMediator.save(admin);
        }
    }

}
