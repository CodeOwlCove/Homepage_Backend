package codeowlcove.codeowl_twitchplays_backend.AnimalRacing;

import codeowlcove.codeowl_twitchplays_backend.Database.DatabaseHandler;
import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnimalRaceBets {

    public class Bet {
    private String animalName;
    private int animalId;
    private int betAmount;

    public Bet(String animalName, int animalId, int betAmount) {
        this.animalName = animalName;
        this.animalId = animalId;
        this.betAmount = betAmount;
    }

    public String getAnimalName() {
        return animalName;
    }

    public int getAnimalId() {
        return animalId;
    }

    public int getBetAmount() {
        return betAmount;
    }

    @Override
    public String toString() {
        return "{'animalName': " + this.animalName + " }, 'animalId' : " + this.animalId + "}, 'betAmount' : " + this.betAmount + "'}";
    }
}

    private static final Logger logger = LoggerFactory.getLogger(AnimalRaceHandler.class);

    private HashMap<String, Bet> bets = new HashMap<>();
    private DatabaseHandler databaseHandler;

    public AnimalRaceBets(SQLDatabaseHandler databaseHandler){
        this.databaseHandler = databaseHandler;
    }

    public String GetCurrentBets(){
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(bets);
        }catch (Exception e){
            logger.error("Error while converting bets to JSON: " + e.getMessage());
            return null;
        }
    }

    public boolean CheckIfUserHasPoints(String user, int points){
        return databaseHandler.GetPoints(user) >= points;
    }

    public void addBet(String user, String animalName, int animalId, int betAmount){
        if(bets.containsKey(user)){
            removeBet(user);
        }
        bets.put(user, new Bet(animalName, animalId, betAmount));
        databaseHandler.RemovePoints(user, betAmount);
    }

    public void removeBet(String user){
        if(bets.containsKey(user)){
            bets.remove(user);
        }
    }

    public void clearBets(){
        bets.clear();
    }

    public void PayoutBets(Integer winner){
        logger.info(" --- Payout for winner " + winner);
        for(Map.Entry<String, Bet> entry : bets.entrySet()){
            if(entry.getValue().animalId == winner){
                logger.info(" --- Payout for user " + entry.getKey() + " with bet on " + entry.getValue().animalName + " for " + entry.getValue().betAmount * 2 + " points");
                databaseHandler.AddPoints(entry.getKey(), entry.getValue().betAmount * 2);
            }
        }
        clearBets();
    }



}
