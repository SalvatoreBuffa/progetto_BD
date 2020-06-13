import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class getDataFromMongoDB {
    public getDataFromMongoDB(){

    }

    public static void main(String[] args) {

        Block<Document> printBlock = new Block<Document>(){
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        };
        MongoClient mc = new MongoClient("localhost", 27017);
        MongoDatabase database = mc.getDatabase("Progetto_BD");
        MongoCollection<Document> collection = database.getCollection("Persone");
        collection.find().forEach(printBlock);
    }
}
