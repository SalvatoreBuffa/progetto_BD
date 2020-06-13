import com.github.javafaker.Address;
import com.github.javafaker.Faker;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.Random;

public class connectMongoDB {

    private String host;
    private int port;
    private MongoClient mc;

    public connectMongoDB(String host, int port){
        this.host = host;
        this.port = port;
        mc = new MongoClient(host, port);
    }

    public static void main(String[] args) {

       MongoClient mc = new MongoClient("localhost", 27017);
       MongoDatabase database = mc.getDatabase("Progetto_BD");
       MongoCollection<Document> collection = database.getCollection("Persone");

       for(int i = 0; i < 100; i++){
           int eta = (int) ((Math.random() * 80) + 10);
           Document anagrafica = getPersona(eta);
           Document indirizzo = getIndirizzo();
           anagrafica.put("Indirizzo", indirizzo);

           Document abitudini = getAbitudini();
           anagrafica.put("Abitudini", abitudini);

           anagrafica.put("patologia attuale", getPatologia());

           Document patologie_precedenti = getPatologiePassate(eta);

           anagrafica.put("patologie precedenti",patologie_precedenti);
           collection.insertOne(anagrafica);
           System.out.println(i);
       }
    }

    public static Document getPersona(int eta){
        Faker f = new Faker(new Locale("it"));
        Random rd = new Random();
        Boolean genere = rd.nextBoolean();
        return new Document("nome", getNome(genere))
                .append("cognome", f.name().lastName())
                .append("genere", genere ? "M" : "F")
                .append("Eta", eta)
                .append("etnia", "italiana");
    }

    public static Document getIndirizzo(){
        Faker f = new Faker(new Locale("it"));
        Address a = f.address();
        return new Document("via", a.streetName())
                    .append("CAP", a.zipCode())
                    .append("Città", a.cityName());
    }

    public static Document getAbitudini(){
        return new Document("Sport", getSport())
                .append("Alimentazione", getAlimentazione())
                .append("Lavoro", getLavoro());
    }

    public static Document getPatologiePassate(int eta){
        Document patologie_precedenti = new Document();
        int n_patologie = (int) (Math.random() * 7);
        for(int i = 0; i < n_patologie; i++){
            patologie_precedenti.put(getPatologia(), new Document("Anno", (int) (Math.random() * (eta)) + (2020-eta)));
        }
        return patologie_precedenti;
    }

    public static String getNome(Boolean genere){
        RandomAccessFile rf;
        try {
        if(genere){
            rf = new RandomAccessFile("nomi_maschili", "r");
        }else{
            rf = new RandomAccessFile("nomi_femminili.txt", "r");
        }
            long len = rf.length();
            int pos = (int) (Math.random() * len)-1;
            rf.seek(pos);
            rf.readLine();
            return rf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Nome non definito";
    }
    public static String getLavoro(){
        String[] lavoro = {"ingegnere", "architetto", "operaio", "impiegato", "disoccupato", "segretario","avvocato", "notaio", "programmatore","meccanico"};
        return lavoro[(int) (Math.random() * lavoro.length)];
    }

    public static String getSport(){
        String[] sport = {"calcio", "nuoto", "pallavolo", "basket", "tennis", "vela", "karate", "scherma", "corsa", "atletica"};
        return sport[(int) (Math.random() * sport.length)];
    }
    public static String getAlimentazione(){
        String[] alimentazione = {"vegetariano", "vegano", "ipocalorica","ipercalorica", "onnivora"};
        return alimentazione[(int) (Math.random() * alimentazione.length)];
    }
    public static String getPatologia(){
        String[] patologia = {"Acalasia", "Acondroplasia", "Acufeni", "Alluce valgo", "Anemia", "Asma", "Artrosi", "Ansia", "Anoressia",
                                "Botulismo", "Bradicardia", "Bronchite",
                                "Calcolosi biliare", "Celiachia", "Cistite",
                                "Depressione", "Dermatite", "Deviazione setto nasale", "Dismenorrea",
                                "Epatite", "Epistassi"};
        return patologia[(int) (Math.random() * patologia.length)];
    }
}

/*
*
* Sport: Calcio, nuoto, pallavolo, basket, tennis, vela, karate, scherma, corsa, atletica,
* Alimentazione: vegetariano, vegano, ipocalorica, ipercalorica, onnivora
* Lavoro: Ingegnere, architetto, operaio, impiegato, disoccupato, segretario, avvocato, notaio, programmatore, meccanico
* Patologie:
* */