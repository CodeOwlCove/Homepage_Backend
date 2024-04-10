package codeowlcove.codeowl_twitchplays_backend.AnimalRacing;

import codeowlcove.codeowl_twitchplays_backend.Database.DatabaseHandler;
import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public class Payout {
        public String animalName;
        public String betPoints;
        public int payedOutPoints;
        public float payedOutPointsMultiplier;

        public Payout(String animalName, String betPoints, float payedOutPointsMultiplier) {
            this.animalName = animalName;
            this.betPoints = betPoints;
            this.payedOutPoints = (int) (Float.parseFloat(betPoints) * payedOutPointsMultiplier);
            this.payedOutPointsMultiplier = payedOutPointsMultiplier;
        }

        @Override
        public String toString() {
            return "{'animalName': " + this.animalName + " }, 'betPoints' : " + this.betPoints + "}, 'payedOutPoints' : " + this.payedOutPoints + " , 'payedOutPointsMultiplier' : " + this.payedOutPointsMultiplier + "'}";
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(AnimalRaceHandler.class);
    private HashMap<String, Bet> bets = new HashMap<>();
    private HashMap<String, Payout> lastSuccessfullyPlacedBets = new HashMap<>();
    private HashMap<String, Payout> lastFailedPlacedBets = new HashMap<>();
    private final float[] payoutMultipliers = {2, 1.5f, 1};
    private DatabaseHandler databaseHandler;

    public AnimalRaceBets(SQLDatabaseHandler databaseHandler){
        this.databaseHandler = databaseHandler;
    }

    public HashMap<String, Bet> getBets() {
        return bets;
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

    public void PayoutBets(ArrayList<Integer> winnerList){
        logger.info(" --- Payout " + payoutMultipliers.toString() + " for winners " + winnerList);

        lastSuccessfullyPlacedBets.clear();
        lastFailedPlacedBets.clear();

        for(Map.Entry<String, Bet> entry : bets.entrySet()){
            var userWon = false;
            for(int i = 0; i < payoutMultipliers.length; i++){
                if(i >= winnerList.size())
                    break;
                if(entry.getValue().animalId == winnerList.get(i)){
                    databaseHandler.AddPoints(entry.getKey(), (int) (entry.getValue().betAmount * payoutMultipliers[i]));
                    lastSuccessfullyPlacedBets.put(entry.getKey(), new Payout(entry.getValue().animalName, String.valueOf(entry.getValue().betAmount), payoutMultipliers[i]));
                    userWon = true;
                    break;
                }
            }
            if(!userWon){
                lastFailedPlacedBets.put(entry.getKey(), new Payout(entry.getValue().animalName, String.valueOf(entry.getValue().betAmount), 0));
            }
        }
        clearBets();
        System.out.println(" --- Payouts: " + lastSuccessfullyPlacedBets.toString());
        System.out.println(" --- Failed Payouts: " + lastFailedPlacedBets.toString());
    }



}
