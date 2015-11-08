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
//       Administratie admin = new Administratie();
//       try{
//           conn = getConnection();
//       
//       ResultSet resultSet = statement.executeQuery("select * from PERSONEN4");
//       while (resultSet.next()) {
//            Geslacht g;
//            int id = resultSet.getInt("id");
//            String achternaam = resultSet.getString("achternaam");
//            String voornamen[] = resultSet.getString("voornamen").split(" ");
//            String tussenvoegsel = resultSet.getString("tussenvoegsel");
//            Calendar geboorteDatum = Calendar.getInstance();
//            geboorteDatum.setTimeInMillis(resultSet.getDate("geboortedatum").getTime());
//            String geboorteplaats = resultSet.getString("geboorteplaats");
//            String geslacht = resultSet.getString("geslacht");
//            if (geslacht.equalsIgnoreCase("MAN"))
//                g = Geslacht.MAN;
//            else
//                g = Geslacht.VROUW;
//            int ouder = resultSet.getInt("ouders");
//
//
//
//            admin.addPersoon(g, voornamen, achternaam, tussenvoegsel, geboorteDatum, geboorteplaats, null);
//        }
//       resultSet = statement.executeQuery("select * from GEZINNEN4");
//
//        while (resultSet.next()) {
//            int id, ouder1, ouder2;
//            Calendar huwelijksDatum = Calendar.getInstance();
//            Calendar scheidingsDatum = Calendar.getInstance();
//
//            id = resultSet.getInt("id");
//            ouder1 = resultSet.getInt("ouder1");
//            ouder2 = resultSet.getInt("ouder2");
//
//            java.sql.Date huwelijksdate = resultSet.getDate("huwelijksdatum");
//            java.sql.Date scheidingsDate = resultSet.getDate("scheidingsDatum");
//
//            if (huwelijksdate != null)
//                huwelijksDatum.setTime(huwelijksdate);
//            else
//                huwelijksDatum = null;
//
//            if (scheidingsDate != null)
//                scheidingsDatum.setTime(scheidingsDate);
//            else
//                scheidingsDatum = null;
//
//            Persoon pouder1 = admin.getPersoon(ouder1);
//            Persoon pouder2 = admin.getPersoon(ouder2);
//
//
//            Gezin g = admin.addOngehuwdGezin(pouder1, pouder2);
//
//
//            if (huwelijksDatum != null)
//                admin.setHuwelijk(g, huwelijksDatum);
//
//            if (scheidingsDatum != null)
//                admin.setScheiding(g, scheidingsDatum);
//        }
//
//        // Load children
//
//        resultSet = statement.executeQuery("select * from PERSONEN4");
//
//        while (resultSet.next()) {
//            int persoonNummer = resultSet.getInt("id");
//            int ouders = resultSet.getInt("ouders");
//            if(ouders != 0)
//                admin.setOuders(admin.getPersoon(persoonNummer), admin.getGezin(ouders));
//        }
//
//        return admin;
//       }
//       catch(Exception ex){
//           
//       }
//       finally{
//           closeConnection();
//       }
//       
//       return null;
//       
        Administratie admin = new Administratie();
        
        ArrayList<String> personenMetOuder = new ArrayList<>();
        
        try {
            conn = getConnection();
            statement = conn.createStatement();
            String SQLPersonen = "Select * from `Personen`";
            
            ResultSet executeQuery = statement.executeQuery(SQLPersonen);
            
            while (executeQuery.next()) {
                int PersoonNummer = executeQuery.getInt("persoonsNummer");
                
                String achternaam = executeQuery.getString("achternaam");
                
                String[] voornamen = executeQuery.getString("voornamen").split(" ");
                
                String tussenvoegsel = executeQuery.getString("tussenvoegsel");
                
                Date birthDate = new SimpleDateFormat("dd-MM-yyyy").parse(executeQuery.getString("geboortedatum"));
                
                String geboorteplaats = executeQuery.getString("geboorteplaats");
                Geslacht geslacht;
                String sex = executeQuery.getString("geslacht");
                if ("M".equals(sex.trim())) {
                    geslacht = Geslacht.MAN;
                }
                else{
                    geslacht = Geslacht.VROUW;
                }
                
                Gezin oudergezin = null;
                int OuderNummer = executeQuery.getInt("ouders");
                
                if (OuderNummer != 0) {
                    String persoonMetOuder =  PersoonNummer +","+ OuderNummer;
                    personenMetOuder.add(persoonMetOuder);
                }

                admin.addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, DateToCalendar(birthDate), geboorteplaats, oudergezin);
            }
            
        } catch (SQLException ex) {
            
        } catch (ParseException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            statement = conn.createStatement();

            String SQLGezinnen = "Select * from `Gezinnen`";
            ResultSet executeQuery = statement.executeQuery(SQLGezinnen);
            
            while (executeQuery.next()) {
                Gezin g;
                if(executeQuery.getInt(2) > 0){
                    
                    Persoon ouder2 = null;
                    
                    if (executeQuery.getInt(3) == -1) {
                        ouder2 = admin.getPersoon(executeQuery.getInt(3));
                    }

                    admin.addOngehuwdGezin(admin.getPersoon(executeQuery.getInt(2)),ouder2);
                    
                    g = admin.getGezin(admin.aantalGeregistreerdeGezinnen());
                    
                    
                    if (!executeQuery.getString(4).isEmpty()) {
                        Date huwlijksDate = new SimpleDateFormat("dd-MM-yyyy").parse(executeQuery.getString(4));
                        g.setHuwelijk(DateToCalendar(huwlijksDate));
                    }
                    
                    if (!executeQuery.getString(5).isEmpty()) {
                        Date scheidingDate = new SimpleDateFormat("dd-MM-yyyy").parse(executeQuery.getString(5));
                        g.setScheiding(DateToCalendar(scheidingDate));
                    }
                }
            }
        } 
        catch (Exception e) {
        }
        
        for (String peroon : personenMetOuder) {
            
           int persnumb = Integer.parseInt(peroon.split(",")[0]); //First is Personnummer, Second is ParentNummer
           int gezinnumb = Integer.parseInt(peroon.split(",")[1]); 
           
           Persoon persoon = admin.getPersoon(persnumb);
           
           admin.setOuders(persoon, admin.getGezin(gezinnumb)); 
        }
        
       return admin; //check if class is correct! 
       
    }
    
    
    private Connection getConnection() throws SQLException {
        //return DriverManager.getConnection(this.props.getProperty("url"), this.props.getProperty("user"), this.props.getProperty("password"));
        return DriverManager.getConnection("jdbc:oracle:thin:@//fhictora01.fhict.local:1521/fhictora", "dbi321816", "un9SqXabEB");
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
            
            SimpleDateFormat format = new SimpleDateFormat("DD-MM-YYYY");
            conn = getConnection();
            statement = conn.createStatement();
                statement.execute("TRUNCATE PERSONEN2;");
          statement.execute("TRUNCATE GEZINNEN2;");
//            for (Persoon persoon: admin.getPersonen()) 
//            {                               
//                
//                statement = conn.createStatement();
//                statement.execute("TRUNCATE PERSONEN2;");
//          statement.execute("TRUNCATE GEZINNEN2;");
//                int nr = persoon.getNr();
//                String Voornaam = persoon.getVoornamen();
//                String Achternaam = persoon.getAchternaam();
//                String Tussenvoegsel = persoon.getTussenvoegsel();
//                
//                if (Tussenvoegsel.isEmpty()) {
//                    Tussenvoegsel = "";
//                }
//                
//                String Datum = format.format(persoon.getGebDat().getTime());
//                String GebPlaats = persoon.getGebPlaats();
//                String Geslacht = persoon.getGeslacht().name().substring(0,1);
//           
//                String SQL = "INSERT INTO `personen` (`persoonsNummer`,`achternaam`,`voornamen`,`tussenvoegsel`,`geboortedatum`,`geboorteplaats`,`geslacht`,`ouders`) VALUES ('"+nr+"','"+Achternaam+"','"+Voornaam+"','"+Tussenvoegsel+"','"+Datum+"','"+GebPlaats+"','"+Geslacht+"',NULL);";
//                
//                System.out.println("Query Persoon\r\n");
//                System.out.println(SQL + "\r\n");
//                statement.executeUpdate(SQL);
//                
//                System.out.println("Query Insterted");
//            }
//            
//            for (Gezin gezin: admin.getGezinnen()) 
//            {
//                //prepStatement = conn.prepareStatement("INSERT INTO GEZINNEN VALUES (?,?,?,?,?)");
//                statement = conn.createStatement();
//                
//                int nr = gezin.getNr();
//                int ouder1nr;
//                Integer ouder2nr = null;
//                String huwelijk = "";
//                String scheiding = "";
//                
//                ouder1nr = gezin.getOuder1().getNr();
//                
//                if(gezin.getOuder2() == null){
//                    
//                }
//                else{
//                    ouder2nr = gezin.getOuder2().getNr();
//                }
//
//                if (gezin.getHuwelijksdatum() != null) 
//                {
//                    huwelijk = format.format(gezin.getHuwelijksdatum().getTime());
//                }
//                
//                if (gezin.getScheidingsdatum() != null) 
//                {
//                    scheiding = format.format(gezin.getScheidingsdatum().getTime());
//                }
//                if(ouder2nr != null){
//                String SQL = "INSERT INTO `Gezinnen`(`gezinsNummer`,`ouder1`,`ouder2`,`huwelijksdatum`,`scheidingsdatum`) VALUES ('"+nr+"','"+ouder1nr+"','"+ouder2nr+"','"+huwelijk+"','"+scheiding+"');";
//                System.out.println("Query Gezin\r\n");
//                System.out.println(SQL + "\r\n");
//                statement.executeUpdate(SQL);
//                System.out.println("Query Insterted");
//                }
//                else{
//                    String SQL = "INSERT INTO `Gezinnen`(`gezinsNummer`,`ouder1`,`huwelijksdatum`,`scheidingsdatum`) VALUES ('"+nr+"','"+ouder1nr+"','"+huwelijk+"','"+scheiding+"');";
//                    System.out.println("Query Gezin\r\n");
//                System.out.println(SQL + "\r\n");
//                statement.executeUpdate(SQL);
//                System.out.println("Query Insterted");
//                }
//                
//            }
//             
//            for (Persoon persoon: admin.getPersonen()) 
//            {
//                if (persoon.getOuderlijkGezin() != null) 
//                {
//                    if (persoon.getOuderlijkGezin() != null) 
//                    {
//                        statement = conn.createStatement();
//                        
//                        //prepStatement = conn.prepareStatement("UPDATE PERSONEN SET OUDERS = ? WHERE PERSOONSNUMMER = ?");
//                        
//                        int ouderlijkGezin = persoon.getOuderlijkGezin().getNr();
//                        int nr = persoon.getNr();
//                        
//                        String SQL = "UPDATE `Personen` SET `ouders`='"+ouderlijkGezin+"' WHERE `PersoonsNummer`= '"+nr+"' ;";
//                        
//                        System.out.println("Query Update Persoon\r\n");
//                        System.out.println(SQL + "\r\n");
//                        statement.executeUpdate(SQL);
////                        statement.executeQuery(SQL);
//                        System.out.println("Query Insterted");
//                    }
//                }
//            }
//            
//            JOptionPane.showConfirmDialog(null, "Saving completed");
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

//        String username = "root";
//        String password = "*Heinz538*";
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
