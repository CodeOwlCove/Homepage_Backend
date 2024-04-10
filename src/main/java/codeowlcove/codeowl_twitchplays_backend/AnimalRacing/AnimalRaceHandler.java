package codeowlcove.codeowl_twitchplays_backend.AnimalRacing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class AnimalRaceHandler {

    /**
     * Class Object to repsresent the current state of a race (for transport purposes)
     */
    private static class AnimalRaceInformation{
        public List<Animal> animals = new ArrayList<>();
        public ArrayList<Integer> lastPlacementList;
        public String gameState;
        public float timeUntilNextGameState;
        public HashMap<String, codeowlcove.codeowl_twitchplays_backend.AnimalRacing.AnimalRaceBets.Bet> placedBets;

        public AnimalRaceInformation(List<Animal> animals, ArrayList<Integer> lastPlacementList, String gameState,
                                     float timeUntilNextGameState, AnimalRaceBets animalRaceBets){
            this.animals = animals;
            this.lastPlacementList = lastPlacementList;
            this.gameState = gameState;
            this.timeUntilNextGameState = timeUntilNextGameState;
            this.placedBets = animalRaceBets.getBets();
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(AnimalRaceHandler.class);

    private AnimalRaceBets animalRaceBets;

    List<Animal> animals = new ArrayList<>();

    private final int ANIMALS_AMOUNT = 5;

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private AnimalRaceGameState currentGameState = AnimalRaceGameState.BETTING;
    private float resetTime = 10;
    private float betTime = 25;
    private float timeUntilNextGameState = 0;
    private final float gameLoopTickTime = 100;
    private int animalSpeed = 200;

    private ArrayList<Integer> placementList = new ArrayList<>();
    private ArrayList<Integer> lastPlacementList = new ArrayList<>();

    private boolean isGamePayedOut = false;

    Runnable handleGameloop = new Runnable() {
        @Override
        public void run() {
            switch(currentGameState){
                case BETTING:
                    HandleBettingTick();
                    break;
                case RACING:
                    HandleRaceTick();
                    break;
                case FINISHED:
                    HandleFinishedTick();
                    break;
            }
            timeUntilNextGameState -= gameLoopTickTime / 1000;
        }
    };

    // -- Constructor --

    public AnimalRaceHandler(AnimalRaceBets animalRaceBets){
        this.animalRaceBets = animalRaceBets;

        CreateRaceAnimals(ANIMALS_AMOUNT);

        timeUntilNextGameState = betTime;
        executorService.scheduleAtFixedRate(handleGameloop, 0, (long) gameLoopTickTime, TimeUnit.MILLISECONDS);
    }

    // -- Getter and Setters --

    public float getTickTime(){
        return gameLoopTickTime;
    }

    private void HandleRaceTick(){
        Random rnd = new Random();
        for(Animal animal : animals){
            if(animal.getState() == AnimalState.FINISHED)
                continue;

            animal.setProgress(Math.min(animal.getProgress() + (rnd.nextInt(animalSpeed)), 10000));
            if(animal.getProgress() >= 10000) {
                placementList.add(animal.getId());
                animal.setState(AnimalState.FINISHED);
            }
        }
        CheckForRaceFinished();
    }

    private void HandleFinishedTick(){
        if(!isGamePayedOut && timeUntilNextGameState <= betTime/2) {
            isGamePayedOut = true;
            animalRaceBets.PayoutBets(lastPlacementList);
        }

        if(timeUntilNextGameState <= 0){
            ResetRace();
            currentGameState = AnimalRaceGameState.BETTING;
            timeUntilNextGameState = betTime;
            isGamePayedOut = false;
        }
    }

    private void HandleBettingTick(){
        if(timeUntilNextGameState <= 0){
            currentGameState = AnimalRaceGameState.RACING;
            StartRace();
            timeUntilNextGameState = resetTime;
        }
    }

    public String GetAllHorseInformationAsJSON(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(animals);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

    public void CreateRaceAnimals(int amount){
        for(int i = 0; i < amount; i++){
            animals.add(new Animal(i));
        }
    }

    public void CheckForRaceFinished(){
        int counter = 0;
        for(Animal animal : animals){
            if(animal.getState() == AnimalState.FINISHED)
                counter++;
        }
        if(counter == animals.size())
            EndRace();
    }

    public void EndRace(){
        currentGameState = AnimalRaceGameState.FINISHED;
        timeUntilNextGameState = resetTime;

        logger.info("Placement: " + placementList.toString());
        ResetPlacementList();
    }

    public void ResetPlacementList(){
        lastPlacementList = new ArrayList<>();
        lastPlacementList.addAll(placementList);

        placementList = new ArrayList<>();
    }

    public void ResetRace(){
        for(Animal animal : animals){
            animal.setProgress(0);
            animal.setState(AnimalState.IDLE);
        }
    }

    public void StartRace(){
        for(Animal animal : animals)
            animal.setState(AnimalState.RUNNING);
    }


    /**
     * Get the current game state and the time until the next game state
     * @return String[] {GameState, TimeUntilNextGameState}
     */
    public String[] getCurrentGameState() {
        String[] result = new String[2];
        result[0] = currentGameState.toString();
        result[1] = String.valueOf(GetRemainingStateTime());
        return result;
    }

    public int GetRemainingStateTime(){
        return switch (currentGameState) {
            case BETTING, FINISHED -> (int) timeUntilNextGameState;
            case RACING -> -1;
        };
    }

    public String[] GetLastWinners(){
        String[] winners = new String[lastPlacementList.size()];
        for(int i = 0; i < lastPlacementList.size(); i++){
            winners[i] = animals.get(lastPlacementList.get(i)).getName();
        }
        return winners;
    }

    public String GetAnimalRaceInformation(){
        var raceInformation = new AnimalRaceInformation(animals, lastPlacementList, currentGameState.toString(), timeUntilNextGameState, animalRaceBets);
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(raceInformation);
        }catch (Exception e){
            logger.error("Error while converting  to JSON: " + e.getMessage());
            return null;
        }
    }

}
