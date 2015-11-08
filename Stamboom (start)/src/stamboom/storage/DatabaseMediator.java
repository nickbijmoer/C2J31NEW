/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import stamboom.domain.*;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;
    private Statement statement;

    public DatabaseMediator(Properties props)
    {
        configure(props);
    }
    
    @Override
    public Administratie load() throws IOException {
        Administratie administratie = new Administratie();
        try{           

        Statement statement = conn.createStatement();
        //Haal personen op
        ResultSet resultSet = statement.executeQuery("select * from PERSONEN");

        while (resultSet.next()) {
            Geslacht g;
            int id = resultSet.getInt("persoonsnummer");
            String voornamen[] = resultSet.getString("voornamen").split(" ");
            String achternaam = resultSet.getString("achternaam");
            String tussenvoegsel = resultSet.getString("tussenvoegsel");
            String geboorteplaats = resultSet.getString("geboorteplaats");
            String geslacht = resultSet.getString("geslacht");
            if (geslacht.equalsIgnoreCase("MAN"))
                g = Geslacht.MAN;
            else
                g = Geslacht.VROUW;
            int ouder = resultSet.getInt("ouders");
            Calendar geboorteDatum = Calendar.getInstance();

            geboorteDatum.setTimeInMillis(resultSet.getDate("geboortedatum").getTime());


            administratie.addPersoon(g, voornamen, achternaam, tussenvoegsel, geboorteDatum, geboorteplaats, null);
        }
        
        //Haal gezinnen op
        resultSet = statement.executeQuery("select * from GEZINNEN");

        while (resultSet.next()) {
            int id, ouder1, ouder2;
            Calendar huwelijksDatum = Calendar.getInstance();
            Calendar scheidingsDatum = Calendar.getInstance();

            id = resultSet.getInt("gezinsnummer");
            ouder1 = resultSet.getInt("ouder1");
            ouder2 = resultSet.getInt("ouder2");

            java.sql.Date huwelijksdate = resultSet.getDate("huwelijksdatum");
            java.sql.Date scheidingsDate = resultSet.getDate("scheidingsDatum");

            if (huwelijksdate != null)
                huwelijksDatum.setTime(huwelijksdate);
            else
                huwelijksDatum = null;

            if (scheidingsDate != null)
                scheidingsDatum.setTime(scheidingsDate);
            else
                scheidingsDatum = null;

            Persoon pouder1 = administratie.getPersoon(ouder1);
            Persoon pouder2 = administratie.getPersoon(ouder2);


            Gezin g = administratie.addOngehuwdGezin(pouder1, pouder2);


            if (huwelijksDatum != null)
                administratie.setHuwelijk(g, huwelijksDatum);

            if (scheidingsDatum != null)
                administratie.setScheiding(g, scheidingsDatum);
        }

        // Load children

        resultSet = statement.executeQuery("select * from PERSONEN");

        while (resultSet.next()) {
            int persoonNummer = resultSet.getInt("persoonsnummer");
            int ouders = resultSet.getInt("ouders");
            if(ouders != 0)
                administratie.setOuders(administratie.getPersoon(persoonNummer), administratie.getGezin(ouders + 1));
        }

        return administratie;
        }
        catch(SQLException ex)
        {
           System.out.println(ex.getMessage());
        }
        return administratie;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        try{
        statement = conn.createStatement();
        statement.execute("ALTER TABLE PERSONEN NOCHECK CONSTRAINT ALL");
        statement.execute("TRUNCATE TABLE PERSONEN");
        statement.execute("ALTER TABLE PERSONEN WITH CHECK CHECK CONTSTAINT ALL");
        
        statement.execute("ALTER TABLE GEZINNEN NOCHECK CONSTRAINT ALL");
        statement.execute("TRUNCATE TABLE GEZINNEN");
        statement.execute("ALTER TABLE GEZINNEN WITH CHECK CHECK CONTSTAINT ALL");
        
        for(Persoon persoon : admin.getPersonen())
        {            
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO PERSONEN(" +
                            "persoonsnummer," +
                            "achternaam," +
                            "voornamen," +
                            "tussenvoegsel," +
                            "geboortedatum," +
                            "geboorteplaats," +
                            "geslacht," +
                            "ouders)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setInt(1, persoon.getNr());
            preparedStatement.setString(2, persoon.getAchternaam());
            preparedStatement.setString(3, persoon.getVoornamen());
            preparedStatement.setString(4, persoon.getTussenvoegsel());
            preparedStatement.setDate(5, new java.sql.Date(persoon.getGebDat().getTimeInMillis()));
            preparedStatement.setString(6, persoon.getGebPlaats());
            preparedStatement.setString(7, persoon.getGeslacht().toString());
            
//             if(persoon.getOuderlijkGezin() != null)
//                preparedStatement.setInt(8, persoon.getOuderlijkGezin().getNr());
//            else
                
            preparedStatement.setString(8, null);

            preparedStatement.execute();
        }
        
        saveGezinnen(admin);
        
        for (Persoon persoon: admin.getPersonen()) 
            {
                if (persoon.getOuderlijkGezin() != null) 
                {
                    if (persoon.getOuderlijkGezin() != null) 
                    {                        
                        String SQL = "UPDATE Personen SET ouders =? WHERE PersoonsNummer = ?";
                        PreparedStatement pst = conn.prepareStatement(SQL);
                        
                        pst.setInt(1, persoon.getOuderlijkGezin().getNr());
                        pst.setInt(2, persoon.getNr());
                        pst.executeUpdate();
                    }
                }
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
        
    }
    
    public void saveGezinnen(Administratie admin) throws SQLException{
        
        //statement.execute("TRUNCATE TABLE GEZINNEN");
        try{
            
            for (Gezin gezin : admin.getGezinnen()) {
            PreparedStatement preparedStatement2 = conn.prepareStatement(
                    "INSERT INTO GEZINNEN(" +
                            "gezinsnummer," +
                            "ouder1," +
                            "ouder2," +
                            "huwelijksdatum," +
                            "scheidingsDatum)" +
                            "VALUES(?, ?, ?, ?, ?)");

            preparedStatement2.setInt(1, gezin.getNr());
            preparedStatement2.setInt(2, gezin.getOuder1().getNr());

            if (gezin.getOuder2() == null)
                preparedStatement2.setString(3, null);
            else
                preparedStatement2.setInt(3, gezin.getOuder2().getNr());

            if (gezin.getHuwelijksdatum() == null)
                preparedStatement2.setString(4, null);
            else
                preparedStatement2.setDate(4, new java.sql.Date(gezin.getHuwelijksdatum().getTimeInMillis()));

            if (gezin.getScheidingsdatum() == null)
                preparedStatement2.setString(5, null);
            else
                preparedStatement2.setDate(5, new java.sql.Date(gezin.getScheidingsdatum().getTimeInMillis()));

            preparedStatement2.execute();
        }
        }
        catch(SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public final boolean configure(Properties props) {
        this.props = props;

        try {
            initConnection();
            return isCorrectlyConfigured();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } 
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("user")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        
        String driver = props.getProperty("driver");
        String url = props.getProperty("url");
        
        try
        {
            //Class.forName(driver);
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch(ClassNotFoundException ex)
        {
            System.out.println("JDBC Driver not found!");
        }
        
        try
        {
            conn = DriverManager.getConnection(this.props.getProperty("url"), this.props.getProperty("username"), this.props.getProperty("password"));
        }
        catch (SQLException ex)
        {
            System.out.println("Connection failed.");
        }        
        
        if (conn == null) {
            System.out.println("Connection could not be made.");
        }
        
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public static Calendar DateToCalendar(Date date){ 
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}