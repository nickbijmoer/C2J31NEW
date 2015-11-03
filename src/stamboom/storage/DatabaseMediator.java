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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;
    private Statement statement;

    public DatabaseMediator(Properties props) {
        configure(props);
    }

    @Override
    public Administratie load() throws IOException {
        //todo opgave 4
       Administratie admin = new Administratie();
       try{
           conn = getConnection();
       
       ResultSet resultSet = statement.executeQuery("select * from PERSONEN");
       while (resultSet.next()) {
            Geslacht g;
            int id = resultSet.getInt("id");
            String achternaam = resultSet.getString("achternaam");
            String voornamen[] = resultSet.getString("voornamen").split(" ");
            String tussenvoegsel = resultSet.getString("tussenvoegsel");
            Calendar geboorteDatum = Calendar.getInstance();
            geboorteDatum.setTimeInMillis(resultSet.getDate("geboortedatum").getTime());
            String geboorteplaats = resultSet.getString("geboorteplaats");
            String geslacht = resultSet.getString("geslacht");
            if (geslacht.equalsIgnoreCase("MAN"))
                g = Geslacht.MAN;
            else
                g = Geslacht.VROUW;
            int ouder = resultSet.getInt("ouders");



            admin.addPersoon(g, voornamen, achternaam, tussenvoegsel, geboorteDatum, geboorteplaats, null);
        }
       resultSet = statement.executeQuery("select * from GEZINNEN");

        while (resultSet.next()) {
            int id, ouder1, ouder2;
            Calendar huwelijksDatum = Calendar.getInstance();
            Calendar scheidingsDatum = Calendar.getInstance();

            id = resultSet.getInt("id");
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

            Persoon pouder1 = admin.getPersoon(ouder1);
            Persoon pouder2 = admin.getPersoon(ouder2);


            Gezin g = admin.addOngehuwdGezin(pouder1, pouder2);


            if (huwelijksDatum != null)
                admin.setHuwelijk(g, huwelijksDatum);

            if (scheidingsDatum != null)
                admin.setScheiding(g, scheidingsDatum);
        }

        // Load children

        resultSet = statement.executeQuery("select * from PERSONEN");

        while (resultSet.next()) {
            int persoonNummer = resultSet.getInt("id");
            int ouders = resultSet.getInt("ouders");
            if(ouders != 0)
                admin.setOuders(admin.getPersoon(persoonNummer), admin.getGezin(ouders));
        }

        return admin;
       }
       catch(Exception ex){
           
       }
       finally{
           closeConnection();
       }
       
       return null;
       
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.props.getProperty("url"), "root", "*Heinz538*");
    }

    @Override
    public void save(Administratie admin) throws IOException {
        //todo opgave 4 
//        try{
//        Statement statement;
//
//        conn = getConnection();
//
//        statement = conn.createStatement();
//
//        // Clear database
//        statement.execute("TRUNCATE PERSONEN;");
//        statement.execute("TRUNCATE GEZINNEN");
//        
//        for(Persoon persoon: admin.getPersonen())
//        {
//            PreparedStatement preparedStatement = conn.prepareStatement(
//                    "INSERT INTO PERSONEN(" +
//                            "persoonsNummer," +
//                            "achternaam," +
//                            "voornamen," +
//                            "tussenvoegsel," +
//                            "geboortedatum," +
//                            "geboorteplaats," +
//                            "geslacht," +
//                            "ouders)" +
//                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
//
//            preparedStatement.setInt(1, persoon.getNr());
//            preparedStatement.setString(2, persoon.getAchternaam());
//            preparedStatement.setString(3, persoon.getVoornamen());
//            preparedStatement.setString(4, persoon.getTussenvoegsel());
//            preparedStatement.setDate(5, new java.sql.Date(persoon.getGebDat().getTimeInMillis()));
//            preparedStatement.setString(6, persoon.getGebPlaats());
//            preparedStatement.setString(7, persoon.getGeslacht().toString());
//
//            if(persoon.getOuderlijkGezin() != null)
//                preparedStatement.setInt(8, persoon.getOuderlijkGezin().getNr());
//            else
//                preparedStatement.setString(8, null);
//
//            preparedStatement.execute();
//        }
//        
//        for (Gezin g : admin.getGezinnen()) {
//            PreparedStatement preparedStatement = conn.prepareStatement(
//                    "INSERT INTO GEZINNEN(" +
//                            "id," +
//                            "ouder1," +
//                            "ouder2," +
//                            "huwelijksdatum," +
//                            "scheidingsDatum)" +
//                            "VALUES(?, ?, ?, ?, ?)");
//
//            preparedStatement.setInt(1, g.getNr());
//            preparedStatement.setInt(2, g.getOuder1().getNr());
//
//            if (g.getOuder2() == null)
//                preparedStatement.setString(3, null);
//            else
//                preparedStatement.setInt(3, g.getOuder2().getNr());
//
//            if (g.getHuwelijksdatum() == null)
//                preparedStatement.setString(4, null);
//            else
//                preparedStatement.setDate(4, new java.sql.Date(g.getHuwelijksdatum().getTimeInMillis()));
//
//            if (g.getScheidingsdatum() == null)
//                preparedStatement.setString(5, null);
//            else
//                preparedStatement.setDate(5, new java.sql.Date(g.getScheidingsdatum().getTimeInMillis()));
//
//            preparedStatement.execute();
//        }
          
        try
        {
            statement.execute("delete table  PERSONEN2;");
          statement.execute("delete table GEZINNEN2;");
            SimpleDateFormat format = new SimpleDateFormat("DD-MM-YYYY");
            conn = getConnection();
            for (Persoon huidig: admin.getPersonen()) 
            {                               
                
                statement = conn.createStatement();
                
                int nr = huidig.getNr();
                String Voornaam = huidig.getVoornamen();
                String Achternaam = huidig.getAchternaam();
                String Tussenvoegsel = huidig.getTussenvoegsel();
                
                if (Tussenvoegsel.isEmpty()) {
                    Tussenvoegsel = "";
                }
                
                String Datum = format.format(huidig.getGebDat().getTime());
                String GebPlaats = huidig.getGebPlaats();
                String Geslacht = huidig.getGeslacht().name().substring(0,1);
           
                String SQL = "INSERT INTO `personen2` (`persoonsNummer`,`achternaam`,`voornamen`,`tussenvoegsel`,`geboortedatum`,`geboorteplaats`,`geslacht`,`ouders`) VALUES ('"+nr+"','"+Achternaam+"','"+Voornaam+"','"+Tussenvoegsel+"','"+Datum+"','"+GebPlaats+"','"+Geslacht+"',NULL);";
                
                System.out.println("Query Persoon\r\n");
                System.out.println(SQL + "\r\n");
                statement.executeUpdate(SQL);
                
                System.out.println("Query Insterted");
            }
            
            for (Gezin huidig: admin.getGezinnen()) 
            {
                //prepStatement = conn.prepareStatement("INSERT INTO GEZINNEN VALUES (?,?,?,?,?)");
                statement = conn.createStatement();
                
                int nr = huidig.getNr();
                int ouder1nr;
                int ouder2nr;
                String huwelijk = "";
                String scheiding = "";
                
                ouder1nr = huidig.getOuder1().getNr();
                
                if(huidig.getOuder2() == null){
                    ouder2nr = -1;
                }
                else{
                    ouder2nr = huidig.getOuder2().getNr();
                }

                if (huidig.getHuwelijksdatum() != null) 
                {
                    huwelijk = format.format(huidig.getHuwelijksdatum().getTime());
                }
                
                if (huidig.getScheidingsdatum() != null) 
                {
                    scheiding = format.format(huidig.getScheidingsdatum().getTime());
                }
                
                String SQL = "INSERT INTO `Gezinnen2`(`gezinsNummer`,`ouder1`,`ouder2`,`huwelijksdatum`,`scheidingsdatum`) VALUES ('"+nr+"','"+ouder1nr+"','"+ouder2nr+"','"+huwelijk+"','"+scheiding+"');";
                
                System.out.println("Query Gezin\r\n");
                System.out.println(SQL + "\r\n");
                statement.executeUpdate(SQL);
                System.out.println("Query Insterted");
            }
             
            for (Persoon huidig: admin.getPersonen()) 
            {
                if (huidig.getOuderlijkGezin() != null) 
                {
                    if (huidig.getOuderlijkGezin() != null) 
                    {
                        statement = conn.createStatement();
                        
                        //prepStatement = conn.prepareStatement("UPDATE PERSONEN SET OUDERS = ? WHERE PERSOONSNUMMER = ?");
                        
                        int ouderlijkGezin = huidig.getOuderlijkGezin().getNr();
                        int nr = huidig.getNr();
                        
                        String SQL = "UPDATE `Personen2` SET `ouders`='"+ouderlijkGezin+"' WHERE `PersoonsNummer`= '"+nr+"' ;";
                        
                        System.out.println("Query Update Persoon\r\n");
                        System.out.println(SQL + "\r\n");
                        //statement.executeUpdate(SQL);
//                        statement.executeQuery(SQL);
                        System.out.println("Query Insterted");
                    }
                }
            }
            
            JOptionPane.showConfirmDialog(null, "Saving completed");
        }
        catch (SQLException ex)
        {
            System.out.print(ex + "\r\n");
            JOptionPane.showConfirmDialog(null, "Saving failed.");
        }
//        }
//        catch(Exception ex){
//            
//        }
        finally{
           closeConnection();
       }
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en controleert
     * of deze in de correcte vorm is, en er verbinding gemaakt kan worden met
     * de database.
     * @param props
     * @return
     */
    @Override
    public final boolean configure(Properties props) {
        this.props = props;
        if (!isCorrectlyConfigured()) {
            System.err.println("props mist een of meer keys");
            return false;
        }

        try {
            initConnection();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            //closeConnection();
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
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        //opgave 4
        String url = props.getProperty("url");
//        String username = props.getProperty("username");
//        String password = props.getProperty("password");

        String username = "root";
        String password = "*Heinz538*";
        try (Connection connection = getConnection()) {
            System.out.println("Database connected!");
            this.conn = connection;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
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
