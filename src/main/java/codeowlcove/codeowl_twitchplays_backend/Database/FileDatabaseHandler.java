package codeowlcove.codeowl_twitchplays_backend.Database;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map;

@Service
public class FileDatabaseHandler implements DatabaseHandler{
    private static final Logger logger = LoggerFactory.getLogger(FileDatabaseHandler.class);

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final Path pointsJsonPath;

    public FileDatabaseHandler(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        pointsJsonPath = Paths.get("user_points.json");
    }

    @Override
    public void AddPoints(String username, int pointsToAdd){
        try {
            InputStream inputStream = Files.newInputStream(pointsJsonPath);

            Map<String, Integer> points = objectMapper.readValue(inputStream, Map.class);

            if(!points.containsKey(username)) {
                logger.info(" --- User[" + username + "] does not exist... creating them now.");
                points.put(username, pointsToAdd);
            }else{
                points.put(username, GetPoints(username) + pointsToAdd);
            }

            String json = objectMapper.writeValueAsString(points);

            WriteToFileAndClose(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RemovePoints(String username, int pointsToSubtract){
        try {
            InputStream inputStream = Files.newInputStream(pointsJsonPath);

            // Convert the JSON content to a Map
            Map<String, Integer> points = objectMapper.readValue(inputStream, Map.class);

            if(!points.containsKey(username)){
                inputStream.close();
                logger.error(" --- User[" + username + "] does not exist");
                return;
            }

            // Manipulate the map
            points.put(username, GetPoints(username) - pointsToSubtract); // Add a new user

            // Convert the map back to JSON
            String json = objectMapper.writeValueAsString(points);

            WriteToFileAndClose(json);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public int GetPoints(String username){
        try {
            InputStream inputStream = Files.newInputStream(pointsJsonPath);

            // Convert the JSON content to a Map
            Map<String, Integer> points = objectMapper.readValue(inputStream, Map.class);

            inputStream.close();

            if(!points.containsKey(username)){
                inputStream.close();
                logger.error(" --- User[" + username + "] does not exist");
                return -1;
            }

            // Manipulate the map
            return points.get(username); // Add a new user

        }catch (IOException e){
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void SetPoints(String username, int newPoints){
        try {
            InputStream inputStream = Files.newInputStream(pointsJsonPath);

            // Convert the JSON content to a Map
            Map<String, Integer> points = objectMapper.readValue(inputStream, Map.class);

            // Create the user if they don't exist
            if(!points.containsKey(username)) {
                logger.info(" --- User[" + username + "] does not exist... creating them now.");
                points.put(username, 0);
            }

            // Manipulate the map
            points.put(username, newPoints); // Add a new user

            // Convert the map back to JSON
            String json = objectMapper.writeValueAsString(points);

            WriteToFileAndClose(json);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean RegisterUser(String username){
        try {
            InputStream inputStream = Files.newInputStream(pointsJsonPath);

            // Convert the JSON content to a Map
            Map<String, Integer> points = objectMapper.readValue(inputStream, Map.class);

            // Create the user if they don't exist
            if(!points.containsKey(username)) {
                logger.info(" --- User[" + username + "] does not exist... creating them now.");
                points.put(username, 100);
                // Convert the map back to JSON
                String json = objectMapper.writeValueAsString(points);

                WriteToFileAndClose(json);
                return true;
            }else{
                logger.error(" --- User[" + username + "] already exists");
                inputStream.close();
                return false;
            }
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getTopPoints(int amount) {
        try {
            InputStream inputStream = Files.newInputStream(pointsJsonPath);
            Map<String, Integer> points = objectMapper.readValue(inputStream, Map.class);

            PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                    (a, b) -> a.getValue().equals(b.getValue()) ? b.getKey().compareTo(a.getKey()) : b.getValue() - a.getValue()
            );

            pq.addAll(points.entrySet());

            Map<String, Integer> result = new LinkedHashMap<>();
            for (int i = 0; i < 10 && !pq.isEmpty(); i++) {
                Map.Entry<String, Integer> entry = pq.poll();
                result.put(entry.getKey(), entry.getValue());
            }

            return objectMapper.writeValueAsString(result);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void WriteToFileAndClose(String json) throws IOException {
        // Save the JSON back to the file
        // Note: This will overwrite the existing file. Make sure this is what you want.
        logger.info(" --- Writing to file: " + pointsJsonPath.toString());
        OutputStream outputStream = Files.newOutputStream(Paths.get(pointsJsonPath.toString()));
        outputStream.write(json.getBytes());
        outputStream.close();
    }
}
