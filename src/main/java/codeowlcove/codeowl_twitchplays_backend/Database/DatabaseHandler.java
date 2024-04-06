package codeowlcove.codeowl_twitchplays_backend.Database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

public interface DatabaseHandler {

    public void AddPoints(String username, int pointsToAdd);

    public void RemovePoints(String username, int pointsToSubtract);

    public int GetPoints(String username);

    public void SetPoints(String username, int newPoints);

    public boolean RegisterUser(String username);

    public String getTopPoints(int amount);
}
