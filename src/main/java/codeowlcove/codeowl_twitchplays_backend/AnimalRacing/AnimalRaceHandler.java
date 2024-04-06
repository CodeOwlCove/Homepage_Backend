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

    public AnimalRaceHandler(AnimalRaceBets animalRaceBets){
        this.animalRaceBets = animalRaceBets;

        CreateRaceAnimals(ANIMALS_AMOUNT);
        placementList = new ArrayList<>();

        timeUntilNextGameState = betTime;
        executorService.scheduleAtFixedRate(handleGameloop, 0, (long) gameLoopTickTime, TimeUnit.MILLISECONDS);
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
        if(timeUntilNextGameState <= 0){
            animalRaceBets.PayoutBets(placementList.get(0));
            ResetRace();
            currentGameState = AnimalRaceGameState.BETTING;
            timeUntilNextGameState = betTime;
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
    }

    public void ResetRace(){
        placementList = new ArrayList<>();

        for(Animal animal : animals){
            animal.setProgress(0);
            animal.setState(AnimalState.IDLE);
        }
    }

    public void StartRace(){
        for(Animal animal : animals)
            animal.setState(AnimalState.RUNNING);
    }

    public AnimalRaceGameState getCurrentGameState() {
        return currentGameState;
    }

}
