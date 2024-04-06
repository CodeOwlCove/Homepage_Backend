package codeowlcove.codeowl_twitchplays_backend.Database;

import codeowlcove.codeowl_twitchplays_backend.Entities.UserDBEntity;
import codeowlcove.codeowl_twitchplays_backend.Entities.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SQLDatabaseHandler implements DatabaseHandler{

    private final UserRepository userRepository;

    public SQLDatabaseHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void AddPoints(String username, int pointsToAdd) {
        UserDBEntity user = userRepository.findByUsername(username);
        user.setPoints(user.getPoints() + pointsToAdd);
        userRepository.save(user);
    }

    @Override
    public void RemovePoints(String username, int pointsToSubtract) {
        UserDBEntity user = userRepository.findByUsername(username);
        user.setPoints(user.getPoints() - pointsToSubtract);
        userRepository.save(user);
    }

    @Override
    public int GetPoints(String username) {
        return userRepository.findByUsername(username).getPoints();
    }

    @Override
    public void SetPoints(String username, int newPoints) {
        UserDBEntity user = userRepository.findByUsername(username);
        user.setPoints(newPoints);
        userRepository.save(user);
    }

    @Override
    public boolean RegisterUser(String username) {
        if(userRepository.findByUsername(username) == null){
            userRepository.save(new UserDBEntity(username, 100, "Twitch"));
            return true;
        }
        return false;
    }

    @Override
    public String getTopPoints(int amount) {
        try {
            // Get top 'amount' users sorted by points in descending order
            List<UserDBEntity> topUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "points")).stream()
                    .limit(amount)
                    .collect(Collectors.toList());

            // Convert list of users to JSON string
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(topUsers);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
