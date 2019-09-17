package nl.terra10.api.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import nl.terra10.api.model.Todo;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TodoService {

    @Inject
    MongoClient mongoClient;

    public List<Todo> list() {
        List<Todo> list = new ArrayList<>();
        MongoCursor<Document> cursor = getCollection().find().iterator();

        try {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Todo todo = new Todo();
                todo.setId(document.getInteger("id"));
                todo.setOption(document.getString("option"));
                list.add(todo);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public void add(Todo todo) {
        Document document = new Document()
                .append("id", todo.getId())
                .append("option", todo.getOption());
        getCollection().insertOne(document);
    }

    public void remove(int id) {
        Document document = new Document()
                .append("id", id);
        getCollection().findOneAndDelete(document);
    }

    public void removeAll() {
        getCollection().deleteMany(new Document());
    }

    private MongoCollection getCollection() {
        return mongoClient.getDatabase("todos").getCollection("todos");
    }

}
