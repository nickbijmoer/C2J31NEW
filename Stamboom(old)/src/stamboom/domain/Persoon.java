package stamboom.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stamboom.util.StringUtilities;

public class Persoon implements Serializable{

    // ********datavelden**************************************
    private final int nr;
    private final String[] voornamen;
    private final String achternaam;
    private final String tussenvoegsel;
    private final Calendar gebDat;
    private final String gebPlaats;
    private Gezin ouderlijkGezin;
    private final List<Gezin> alsOuderBetrokkenIn;
    private transient ObservableList<Gezin> ObservableAlsOuderBetrokkenIn;
    private final Geslacht geslacht;
    
    private List<PersoonMetGeneratie> pmg = new ArrayList<PersoonMetGeneratie>();


    // ********constructoren***********************************    
    /**
     * er wordt een persoon gecreeerd met persoonsnummer persNr en met als
     * voornamen vnamen, achternaam anaam, tussenvoegsel tvoegsel, geboortedatum
     * gebdat, gebplaats, geslacht g en een gegeven ouderlijk gezin (mag null
     * (=onbekend) zijn); NB. de eerste letter van een voornaam, achternaam en
     * gebplaats wordt naar een hoofdletter omgezet, alle andere letters zijn
     * kleine letters; het tussenvoegsel is zo nodig in zijn geheel
     * geconverteerd naar kleine letters.
     *
     */
    Persoon(int persNr, String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats, Geslacht g, Gezin ouderlijkGezin) {
        //todo opgave 1
    
        
         for (int i = 0; i < vnamen.length; i++)
        {
         vnamen[i] = StringUtilities.withFirstCapital(vnamen[i]);
        }
        
        anaam = StringUtilities.withFirstCapital(anaam);
        gebplaats = StringUtilities.withFirstCapital(gebplaats);
        tvoegsel = tvoegsel.trim().toLowerCase();
        
        
        this.nr = persNr;
        this.voornamen = vnamen;
        this.achternaam = anaam;
        this.tussenvoegsel = tvoegsel;
        this.gebDat = gebdat;
        this.gebPlaats = gebplaats;
        this.geslacht = g;
        this.alsOuderBetrokkenIn = new ArrayList<Gezin>();
        SetObservableList();
        if(ouderlijkGezin != null)
        {
            setOuders(ouderlijkGezin);
        }
        
        
    }

    // ********methoden****************************************
    public void SetObservableList(){
    ObservableAlsOuderBetrokkenIn = FXCollections.observableList(alsOuderBetrokkenIn);

    }
    public void SetItems(ObservableList<Gezin> Gezinnen)
    {
        this.ObservableAlsOuderBetrokkenIn = Gezinnen;
    }
    
    public ObservableList<Gezin> GetItems()
    {
        return (ObservableList<Gezin>)
                FXCollections.unmodifiableObservableList(ObservableAlsOuderBetrokkenIn);
    }
    
    /**
     * @return de achternaam van deze persoon
     */
    public String getAchternaam() {
        return achternaam;
    }

    /**
     * @return de geboortedatum van deze persoon
     */
    public Calendar getGebDat() {
        return gebDat;
    }

    /**
     *
     * @return de geboorteplaats van deze persoon
     */
    public String getGebPlaats() {
        return gebPlaats;
    }

    /**
     *
     * @return het geslacht van deze persoon
     */
    public Geslacht getGeslacht() {
        return geslacht;
    }

    /**
     *
     * @return de voorletters van de voornamen; elke voorletter wordt gevolgd
     * door een punt
     */
    public String getInitialen() 
    {
        //todo opgave 1
        String s = "";
        for(String letter : voornamen)
        {
            
        s += letter.substring(0, 1) + ".";
        
        }
        return s;
    }

    /**
     *
     * @return de initialen gevolgd door een eventueel tussenvoegsel en
     * afgesloten door de achternaam; initialen, voorzetsel en achternaam zijn
     * gescheiden door een spatie
     */
    public String getNaam() {
        //todo opgave 1
        String naam;
        if(tussenvoegsel.equalsIgnoreCase(""))
        {
        naam = getInitialen()  + " " + achternaam;
        }
        else
        {
        naam = getInitialen() + " " + tussenvoegsel + " " + achternaam;
        }
        
        return naam;
    }

    /**
     * @return het nummer van deze persoon
     */
    public int getNr() {
        return nr;
    }

    /**
     * @return het ouderlijk gezin van deze persoon, indien bekend, anders null
     */
    public Gezin getOuderlijkGezin() {
        return ouderlijkGezin;
    }

    /**
     * @return het tussenvoegsel van de naam van deze persoon (kan een lege
     * string zijn)
     */
    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    /**
     * @return alle voornamen onderling gescheiden door een spatie
     */
    public String getVoornamen() {
        StringBuilder init = new StringBuilder();
        for (String s : voornamen) {
            init.append(s).append(' ');
        }
        return init.toString().trim();
    }

    /**
     * @return de standaardgegevens van deze mens: naam (geslacht) geboortedatum
     */
    public String standaardgegevens() {
        return getNaam() + " (" + getGeslacht() + ") " + StringUtilities.datumString(gebDat);
    }

    @Override
    public String toString() {
        return standaardgegevens();
    }

    /**
     * @return de gezinnen waar deze persoon bij betrokken is
     */
//    public List<Gezin> getAlsOuderBetrokkenIn() {
//        return (List<Gezin>) Collections.unmodifiableList(alsOuderBetrokkenIn);
//    }
    public ObservableList<Gezin> getAlsOuderBetrokkenIn() {
        return (ObservableList<Gezin>)FXCollections.unmodifiableObservableList(ObservableAlsOuderBetrokkenIn);
    }

    /**
     * Als het ouderlijk gezin van deze persoon nog onbekend is dan wordt deze
     * persoon een kind van ouderlijkGezin en tevens wordt deze persoon als kind
     * in dat gezin geregistreerd Als de ouders bij aanroep al bekend zijn,
     * verandert er niets
     *
     * @param ouderlijkGezin
     * @return of ouderlijk gezin kon worden toegevoegd
     */
    boolean setOuders(Gezin ouderlijkGezin) {
        //todo opgave 1
        boolean gelukt = false;
        if(this.ouderlijkGezin == null)
        {
          this.ouderlijkGezin = ouderlijkGezin;
            
            ouderlijkGezin.breidUitMet(this);
          gelukt = true;
        }
        
        return gelukt;
    }

    /**
     * @return voornamen, eventueel tussenvoegsel en achternaam, geslacht,
     * geboortedatum, namen van de ouders, mits bekend, en nummers van de
     * gezinnen waarbij deze persoon betrokken is (geweest)
     */
    public String beschrijving() {
        StringBuilder sb = new StringBuilder();

        sb.append(standaardgegevens());

        if (ouderlijkGezin != null) {
            sb.append("; 1e ouder: ").append(ouderlijkGezin.getOuder1().getNaam());
            if (ouderlijkGezin.getOuder2() != null) {
                sb.append("; 2e ouder: ").append(ouderlijkGezin.getOuder2().getNaam());
            }
        }
        if (!ObservableAlsOuderBetrokkenIn.isEmpty()) {
            sb.append("; is ouder in gezin ");

            for (Gezin g : ObservableAlsOuderBetrokkenIn) {
                sb.append(g.getNr()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * als g nog niet bij deze persoon staat geregistreerd wordt g bij deze
     * persoon geregistreerd en anders verandert er niets
     *
     * @param g een nieuw gezin waarin deze persoon een ouder is
     *
     */
    void wordtOuderIn(Gezin g) {
        if(ObservableAlsOuderBetrokkenIn != null)
        {
            if (!ObservableAlsOuderBetrokkenIn.contains(g)) {
            ObservableAlsOuderBetrokkenIn.add(g);
        }
        }
        else{
            ObservableAlsOuderBetrokkenIn = FXCollections.observableList(alsOuderBetrokkenIn);
            ObservableAlsOuderBetrokkenIn.add(g);
        }
        
    }

    /**
     *
     *
     * @param andereOuder mag null zijn
     * @return het ongehuwde gezin met de andere ouder ; mits bestaand anders
     * null
     */
    public Gezin heeftOngehuwdGezinMet(Persoon andereOuder) {
        //todo opgave 1
       
//       for(Gezin gezin : alsOuderBetrokkenIn)
//       {
//       
//        return gezin;
//       
//       }
//        
//       
//        return null;
        if(ObservableAlsOuderBetrokkenIn == null)
        {
            return null;
        }
        if (andereOuder != null) {
            for (Gezin g : ObservableAlsOuderBetrokkenIn) {
                if ((g.getOuder1().equals(andereOuder) || (g.getOuder2() != null && g.getOuder2().equals(andereOuder))) && g.isOngehuwd()) {
                    return g;
                }
            }
        } else {
            for (Gezin g : ObservableAlsOuderBetrokkenIn) {
                Persoon p = null;
                if (g.getOuder1().equals(this)) {
                    p = g.getOuder2();
                } else if (g.getOuder2().equals(this)) {
                    p = g.getOuder1();
                }
                if (p != null) {
                    return p.heeftOngehuwdGezinMet(this);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum getrouwd is, anders false
     */
    public boolean isGetrouwdOp(Calendar datum) {
        if(ObservableAlsOuderBetrokkenIn != null)
        {
        for (Gezin gezin : ObservableAlsOuderBetrokkenIn) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return true;
            }
        }
        }
        return false;
    }

    /**
     *
     * @param datum
     * @return true als de persoon kan trouwen op datum, hierbij wordt rekening
     * gehouden met huwelijken in het verleden en in de toekomst
     * Alleen meerderjarige (18+) personen kunnen trouwen.
     */
    public boolean kanTrouwenOp(Calendar datum) {
        Calendar meerderjarigDatum = ((GregorianCalendar)this.gebDat.clone());
        meerderjarigDatum.add(Calendar.YEAR, 18);
        if(datum.compareTo(meerderjarigDatum) < 1){
            return false;
        }

        for (Gezin gezin : ObservableAlsOuderBetrokkenIn) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return false;
            } else {
                Calendar huwdatum = gezin.getHuwelijksdatum();
                if (huwdatum != null && huwdatum.after(datum)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum gescheiden is, anders false
     */
    public boolean isGescheidenOp(Calendar datum) {
        //todo opgave 1
        for(Gezin gezin : ObservableAlsOuderBetrokkenIn)
        {
            if(gezin.getScheidingsdatum() == datum)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * ********* de rest wordt in opgave 2 verwerkt ****************
     */
    /**
     *
     * @return het aantal personen in de stamboom van deze persoon (ouders,
     * grootouders etc); de persoon zelf telt ook mee
     */
    public int afmetingStamboom() {
           //todo opgave 2
        int aantalLeden = 1;
        if (this.ouderlijkGezin != null) {
            aantalLeden += recursiefStamboom(this.ouderlijkGezin);
        }
        return aantalLeden;
    }

    public int recursiefStamboom(Gezin ouderlijkGezin) {
        int aantalLeden = 0;

        if (ouderlijkGezin.getOuder1() != null) {
            if (ouderlijkGezin.getOuder1().getOuderlijkGezin() != null) {
                aantalLeden += recursiefStamboom(ouderlijkGezin.getOuder1().getOuderlijkGezin()) + 1;
            } else {
                aantalLeden += 1;
            }
        }

        if (ouderlijkGezin.getOuder2() != null) {
            if (ouderlijkGezin.getOuder2().getOuderlijkGezin() != null) {
                aantalLeden += recursiefStamboom(ouderlijkGezin.getOuder2().getOuderlijkGezin()) + 1;
            } else {
                aantalLeden += 1;
            }
        }

        return aantalLeden;
    
    }

    /**
     * de lijst met de items uit de stamboom van deze persoon wordt toegevoegd
     * aan lijst, dat wil zeggen dat begint met de toevoeging van de
     * standaardgegevens van deze persoon behorende bij generatie g gevolgd door
     * de items uit de lijst met de stamboom van de eerste ouder (mits bekend)
     * en gevolgd door de items uit de lijst met de stamboom van de tweede ouder
     * (mits bekend) (het generatienummer van de ouders is steeds 1 hoger)
     *
     * @param lijst
     * @param g >=0, het nummer van de generatie waaraan deze persoon is
     * toegewezen;
     */
    void voegJouwStamboomToe(ArrayList<PersoonMetGeneratie> lijst, int g) {
         //todo opgave 2
        lijst = new ArrayList<PersoonMetGeneratie>();
        g = 0;
        lijst.add(new PersoonMetGeneratie(this.standaardgegevens(), g));

        if (this.ouderlijkGezin != null) {
            lijst.addAll(b(this.ouderlijkGezin, g));
        }

        this.pmg = lijst;
    }

    /**
     *
     * @return de stamboomgegevens van deze persoon in de vorm van een String:
     * op de eerste regel de standaardgegevens van deze persoon, gevolgd door de
     * stamboomgegevens van de eerste ouder (mits bekend) en gevolgd door de
     * stamboomgegevens van de tweede ouder (mits bekend); formattering: iedere
     * persoon staat op een aparte regel en afhankelijk van het
     * generatieverschil worden er per persoon 2*generatieverschil spaties
     * ingesprongen;
     *
     * bijv voor M.G. Pieterse met ouders, grootouders en overgrootouders,
     * inspringen is in dit geval met underscore gemarkeerd: <br>
     *
     * M.G. Pieterse (VROUW) 5-5-2004<br>
     * __L. van Maestricht (MAN) 27-6-1953<br>
     * ____A.G. von Bisterfeld (VROUW) 13-4-1911<br>
     * ______I.T.M.A. Goldsmid (VROUW) 22-12-1876<br>
     * ______F.A.I. von Bisterfeld (MAN) 27-6-1874<br>
     * ____H.C. van Maestricht (MAN) 17-2-1909<br>
     * __J.A. Pieterse (MAN) 23-6-1964<br>
     * ____M.A.C. Hagel (VROUW) 12-0-1943<br>
     * ____J.A. Pieterse (MAN) 4-8-1923<br>
     */
        public String stamboomAlsString() {
        StringBuilder builder = new StringBuilder();
        //todo opgave 2

       voegJouwStamboomToe(null, 0);
        
        String totalS = "";
        
        for(PersoonMetGeneratie pmg : this.pmg)
        {
            String s = pmg.getPersoonsgegevens();
            for(int i = 0; i < pmg.getGeneratie(); i++)
            {
                s = "  " + s; 
            }
            totalS += s + System.getProperty("line.separator");
        }
        builder.append(totalS);
        
        return builder.toString();
    }
    public ArrayList<PersoonMetGeneratie> b(Gezin ouderlijkGezin, int generatie)
    {
        int count = 0;
        generatie += 1;
        ArrayList<PersoonMetGeneratie> localpmg = new ArrayList<PersoonMetGeneratie>();
        
        if(ouderlijkGezin.getOuder1() != null)
            if(ouderlijkGezin.getOuder1().getOuderlijkGezin() != null)
            {
                localpmg.add(new PersoonMetGeneratie(ouderlijkGezin.getOuder1().standaardgegevens(), generatie));
                localpmg.addAll(b( ouderlijkGezin.getOuder1().getOuderlijkGezin(), generatie));
            }
            else 
            {
                localpmg.add(new PersoonMetGeneratie(ouderlijkGezin.getOuder1().standaardgegevens(), generatie));
            }
        
        if(ouderlijkGezin.getOuder2() != null)
            if(ouderlijkGezin.getOuder2().getOuderlijkGezin() != null)
            {
                localpmg.add(new PersoonMetGeneratie(ouderlijkGezin.getOuder2().standaardgegevens(), generatie));
                localpmg.addAll(b( ouderlijkGezin.getOuder2().getOuderlijkGezin(), generatie));
            }
            else 
            {
                localpmg.add(new PersoonMetGeneratie(ouderlijkGezin.getOuder2().standaardgegevens(), generatie));
            }
        
        return localpmg;
    }
}
