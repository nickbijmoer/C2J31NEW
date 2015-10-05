package stamboom.domain;


import java.util.*;

public class Administratie implements java.io.Serializable{

    //************************datavelden*************************************
    private int nextGezinsNr;
    private int nextPersNr;
    private final List<Persoon> personen;
    private final List<Gezin> gezinnen;
    private  String Achternaam;

    //***********************constructoren***********************************
    /**
     * er wordt een lege administratie aangemaakt.
     * personen en gezinnen die in de toekomst zullen worden gecreeerd, worden
     * (apart) opvolgend genummerd vanaf 1
     */
    public Administratie() {
        //todo opgave 1
        this.personen = new ArrayList<Persoon>();
        this.gezinnen = new ArrayList<Gezin>();
        this.nextGezinsNr = 1;
        this.nextPersNr = 1;
        
    }

    //**********************methoden****************************************
    /**
     * er wordt een persoon met de gegeven parameters aangemaakt; de persoon
     * krijgt een uniek nummer toegewezen, en de persoon is voortaan ook bij het
     * (eventuele) ouderlijk gezin bekend. Voor de voornamen, achternaam en
     * gebplaats geldt dat de eerste letter naar een hoofdletter en de resterende
     * letters naar kleine letters zijn geconverteerd; het tussenvoegsel is in
     * zijn geheel geconverteerd naar kleine letters; overbodige spaties zijn
     * verwijderd
     *
     * @param geslacht
     * @param vnamen vnamen.length>0; alle strings zijn niet leeg
     * @param anaam niet leeg
     * @param tvoegsel mag leeg zijn
     * @param gebdat
     * @param gebplaats niet leeg
     * @param ouderlijkGezin mag de waarde null (=onbekend) hebben
     *
     * @return de nieuwe persoon.
     * Als de persoon al bekend was (op basis van combinatie van getNaam(),
     * geboorteplaats en geboortedatum), wordt er null geretourneerd.
     */
    public Persoon addPersoon(Geslacht geslacht, String[] vnamen, String anaam,
            String tvoegsel, Calendar gebdat,
            String gebplaats, Gezin ouderlijkGezin) {

        List<String> voornamenVal = new ArrayList<String>();
        String[] voornamen;
        String tussenvoegsel = tvoegsel;
        
        for (int i = 0; i < vnamen.length; i++) {
            CapitalizeString(vnamen[i]);
            voornamenVal.add(CapitalizeString(vnamen[i]));
        }
        if (vnamen.length == 0) {
            throw new IllegalArgumentException("ten minste 1 voornaam");
        }
        for (String voornaam : vnamen) {
            if (voornaam.trim().isEmpty()) {
                throw new IllegalArgumentException("lege voornaam is niet toegestaan");
            }
            
        }
        
     
        if (anaam.trim().isEmpty()) {
            throw new IllegalArgumentException("lege achternaam is niet toegestaan");
        }

        if (gebplaats.trim().isEmpty()) {
            throw new IllegalArgumentException("lege geboorteplaats is niet toegestaan");
        }
        //todo opgave 1
        //String Voornamen = "";
        String initialen = "";
        for(String voornaam : vnamen)
        {
            voornaam = voornaam.trim();
            int lenght = voornaam.length();
            voornaam = voornaam.substring(0, 1).toUpperCase() + voornaam.substring(1, lenght).toLowerCase();
            if(initialen.length() > 0)
            {
                // = Voornamen + " " + voornaam.substring(0, 1).toUpperCase() + voornaam.substring(1, lenght).toLowerCase();
                initialen = initialen + " " + voornaam.substring(0, 1).toUpperCase();
            }
            else
            {
                //Voornamen = voornaam.substring(0, 1).toUpperCase() + voornaam.substring(1, lenght).toLowerCase();
                initialen = voornaam.substring(0, 1).toUpperCase();
            }
            
        }
                      
        anaam = anaam.trim();
        int temp = anaam.length();
        anaam = anaam.substring(0, 1).toUpperCase() + anaam.substring(1, temp).toLowerCase();
        
        if(tvoegsel != null)
        {
            tvoegsel = tvoegsel.toLowerCase();
        }
        
        gebplaats = gebplaats.trim();
        int temp2 = gebplaats.length();
        gebplaats = gebplaats.substring(0, 1).toUpperCase() + gebplaats.substring(1, temp2).toLowerCase();
        
        String naam = "";
        if(tvoegsel != null)
        {
         naam = initialen + " " + tvoegsel + " " + anaam;   
        }
        else
        {
            naam = initialen + " " + anaam;
        }
        if(personen!=null && !personen.isEmpty())
        {
            for(Persoon persoon : personen)
            {
                if(persoon.getInitialen().equalsIgnoreCase(setInitialen(vnamen))  && persoon.getGebDat().equals(gebdat)  && persoon.getGebPlaats().equalsIgnoreCase(gebplaats) && persoon.getTussenvoegsel().equalsIgnoreCase(tussenvoegsel))
                {
                    return null;
                }
            }
        }
            voornamen = voornamenVal.toArray(new String[0]);
            Persoon newPersoon = new Persoon(nextPersNr, voornamen, anaam, tvoegsel, gebdat, gebplaats, geslacht, ouderlijkGezin);
            nextPersNr++;
            personen.add(newPersoon);
            return newPersoon;
    }

    /**
     * er wordt, zo mogelijk (zie return) een (kinderloos) ongehuwd gezin met
     * ouder1 en ouder2 als ouders gecreeerd; de huwelijks- en scheidingsdatum
     * zijn onbekend (null); het gezin krijgt een uniek nummer toegewezen; dit
     * gezin wordt ook bij de afzonderlijke ouders geregistreerd;
     *
     * @param ouder1
     * @param ouder2 mag null zijn
     *
     * @return het nieuwe gezin. null als ouder1 = ouder2 of als een van de volgende
     * voorwaarden wordt overtreden:
     * 1) een van de ouders is op dit moment getrouwd
     * 2) het koppel vormt al een ander gezin
     */
    public Gezin addOngehuwdGezin(Persoon ouder1, Persoon ouder2) {
        if (ouder1 == ouder2) {
            return null;
        }

        if (ouder1.getGebDat().compareTo(Calendar.getInstance()) > 0) {
            return null;
        }
        if (ouder2 != null && ouder2.getGebDat().compareTo(Calendar.getInstance()) > 0) {
            return null;
        }
       
        Calendar nu = Calendar.getInstance();
        if (ouder1.isGetrouwdOp(nu) || (ouder2 != null
                && ouder2.isGetrouwdOp(nu))
                || ongehuwdGezinBestaat(ouder1, ouder2)) {
            return null;
        }

        Gezin gezin = new Gezin(nextGezinsNr, ouder1, ouder2);
        nextGezinsNr++;
        gezinnen.add(gezin);

        ouder1.wordtOuderIn(gezin);
        if (ouder2 != null) {
            ouder2.wordtOuderIn(gezin);
        }

        return gezin;
    }

    /**
     * Als het ouderlijk gezin van persoon nog onbekend is dan wordt
     * persoon een kind van ouderlijkGezin, en tevens wordt persoon als kind
     * in dat gezin geregistreerd. Als de ouders bij aanroep al bekend zijn,
     * verandert er niets
     *
     * @param persoon
     * @param ouderlijkGezin
     * @return of ouderlijk gezin kon worden toegevoegd.
     */
    public boolean setOuders(Persoon persoon, Gezin ouderlijkGezin) {
        return persoon.setOuders(ouderlijkGezin);
    }

    /**
     * als de ouders van dit gezin gehuwd zijn en nog niet gescheiden en datum
     * na de huwelijksdatum ligt, wordt dit de scheidingsdatum. Anders gebeurt
     * er niets.
     *
     * @param gezin
     * @param datum
     * @return true als scheiding geaccepteerd, anders false
     */
    public boolean setScheiding(Gezin gezin, Calendar datum) {
        return gezin.setScheiding(datum);
    }

    /**
     * registreert het huwelijk, mits gezin nog geen huwelijk is en beide
     * ouders op deze datum mogen trouwen (pas op: het is niet toegestaan dat een
     * ouder met een toekomstige (andere) trouwdatum trouwt.)
     *
     * @param gezin
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    public boolean setHuwelijk(Gezin gezin, Calendar datum) {
        return gezin.setHuwelijk(datum);
    }

    /**
     *
     * @param ouder1
     * @param ouder2
     * @return true als dit koppel (ouder1,ouder2) al een ongehuwd gezin vormt
     */
    boolean ongehuwdGezinBestaat(Persoon ouder1, Persoon ouder2) {
        return ouder1.heeftOngehuwdGezinMet(ouder2) != null;
    }

    /**
     * als er al een ongehuwd gezin voor dit koppel bestaat, wordt het huwelijk
     * voltrokken, anders wordt er zo mogelijk (zie return) een (kinderloos)
     * gehuwd gezin met ouder1 en ouder2 als ouders gecreeerd; de
     * scheidingsdatum is onbekend (null); het gezin krijgt een uniek nummer
     * toegewezen; dit gezin wordt ook bij de afzonderlijke ouders
     * geregistreerd;
     *
     * @param ouder1
     * @param ouder2
     * @param huwdatum
     * @return null als ouder1 = ouder2 of als een van de ouders getrouwd is
     * anders het gehuwde gezin
     */
    public Gezin addHuwelijk(Persoon ouder1, Persoon ouder2, Calendar huwdatum) {
        //todo opgave 1
        Gezin gezin = null;
        if (ouder1 != ouder2 && ouder1.kanTrouwenOp(huwdatum) && ouder2.kanTrouwenOp(huwdatum)) {
            for (Persoon p : personen) {
                if (p.equals(ouder1)) {
                    gezin = ouder1.heeftOngehuwdGezinMet(ouder2);
                    if (gezin != null) {
                        ouder1.heeftOngehuwdGezinMet(ouder2).setHuwelijk(huwdatum);
                    } else {
                        gezin = new Gezin(this.nextGezinsNr, ouder1, ouder2);
                        gezin.setHuwelijk(huwdatum);

                        ouder1.wordtOuderIn(gezin);
                        ouder2.wordtOuderIn(gezin);

                        this.gezinnen.add(gezin);
                        nextGezinsNr++;
                    }
                }
            }
        }
        return gezin;
    }

    /**
     *
     * @return het aantal geregistreerde personen
     */
    public int aantalGeregistreerdePersonen() {
        return nextPersNr - 1;
    }

    /**
     *
     * @return het aantal geregistreerde gezinnen
     */
    public int aantalGeregistreerdeGezinnen() {
        return nextGezinsNr - 1;
    }

    /**
     *
     * @param nr
     * @return de persoon met nummer nr, als die niet bekend is wordt er null
     * geretourneerd
     */
    public Persoon getPersoon(int nr) {
        //todo opgave 1
        //aanname: er worden geen personen verwijderd
        for(Persoon persoon : personen)
        {
            if(persoon.getNr() == nr)
            {
                return persoon;
            }
        }
        return null;
    }

    /**
     * @param achternaam
     * @return alle personen met een achternaam gelijk aan de meegegeven
     * achternaam (ongeacht hoofd- en kleine letters)
     */
    public ArrayList<Persoon> getPersonenMetAchternaam(String achternaam) {
        //todo opgave 1
        achternaam = achternaam.substring(0, 1).toUpperCase() + achternaam.substring(1, achternaam.length()).toLowerCase();
        ArrayList<Persoon> FoundPersons = new ArrayList<Persoon>();
        for(Persoon persoon : personen)
        {
            Achternaam = persoon.getAchternaam();
            if(Achternaam.equalsIgnoreCase(achternaam))
            {
                FoundPersons.add(persoon);
            }
        }
        return FoundPersons;
    }

    /**
     *
     * @return de geregistreerde personen
     */
    public List<Persoon> getPersonen() {
        // todo opgave 1
        List<Persoon> Personen = new ArrayList<>();
        Personen.addAll(this.personen);
        return Personen;
    }

    /**
     *
     * @param vnamen
     * @param anaam
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats
     * @return de persoon met dezelfde initialen, tussenvoegsel, achternaam,
     * geboortedatum en -plaats mits bekend (ongeacht hoofd- en kleine letters),
     * anders null
     */
    public Persoon getPersoon(String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats) {
        //todo opgave 1
        String Voornamen = "";
        for(String voornaam : vnamen)
        {
            if(Voornamen.length() > 0)
            {
                Voornamen = Voornamen + " " + voornaam;
            }
            else{
                Voornamen = voornaam;
            }
        }
            
        for(Persoon persoon : personen)
        {
            if(persoon.getInitialen().equalsIgnoreCase(setInitialen(vnamen))  && persoon.getAchternaam().equalsIgnoreCase(anaam) && persoon.getTussenvoegsel().equalsIgnoreCase(tvoegsel) && persoon.getGebDat().equals(gebdat)  && persoon.getGebPlaats().equalsIgnoreCase(gebplaats))
            {
                return persoon;
            }
        }
        return null;
    }

    /**
     *
     * @return de geregistreerde gezinnen
     */
    public List<Gezin> getGezinnen() {
        return null;
    }
    
    public String setInitialen(String[] voornamen) {
        //todo opgave 1
        String Initialen = "";

        for (int i = 0; i < voornamen.length; i++) {
            Initialen += voornamen[i].toUpperCase().charAt(0) + ".";
        }
        return Initialen;
    }

    /**
     *
     * @param gezinsNr
     * @return het gezin met nummer nr. Als dat niet bekend is wordt er null
     * geretourneerd
     */
    public Gezin getGezin(int gezinsNr) {
        // aanname: er worden geen gezinnen verwijderd
        if (gezinnen != null && 1 <= gezinsNr && 1 <= gezinnen.size()) {
            return gezinnen.get(gezinsNr - 1);
        }
        return null;
    }

    
    public String CapitalizeString(String str) {
        String tempVNaam = str.toLowerCase();
        tempVNaam = tempVNaam.substring(0, 1).toUpperCase() + tempVNaam.substring(1, tempVNaam.length());

        while (tempVNaam.contains("  ")) {
            tempVNaam = tempVNaam.replace("  ", " ");
        }

        if (tempVNaam.startsWith(" ")) {
            tempVNaam = tempVNaam.substring(1, tempVNaam.length());
        }

        if (tempVNaam.endsWith(" ")) {
            tempVNaam = tempVNaam.substring(0, tempVNaam.length() - 1);
        }
        return tempVNaam;
    }
}
