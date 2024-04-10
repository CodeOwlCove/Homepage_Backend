package codeowlcove.codeowl_twitchplays_backend.Controller;

import codeowlcove.codeowl_twitchplays_backend.AnimalRacing.AnimalRaceBets;
import codeowlcove.codeowl_twitchplays_backend.Database.DatabaseHandler;
import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Points")
@CrossOrigin(origins = "https://codeowlcove.com")
public class PointsController {

    private DatabaseHandler databaseHandler;
    private AnimalRaceBets animalRaceBets;

    public PointsController(SQLDatabaseHandler databaseHandler, AnimalRaceBets animalRaceBets){
        this.databaseHandler = databaseHandler;
        this.animalRaceBets = animalRaceBets;
    }

    @GetMapping("/GetPointsHighscore")
    public ResponseEntity<String> GetPointsHighscore(){
        var topPoints = databaseHandler.getTopPoints(10);
        if(topPoints == null)
            return ResponseEntity.badRequest().body("Error while retrieving highscore.");
        return ResponseEntity.ok(topPoints);
    }

    @GetMapping("/GetAnimalRaceBets")
    public ResponseEntity<String> GetCurrentBets(){
        var currentBets = animalRaceBets.GetCurrentBets();
        if(currentBets == null)
            return ResponseEntity.badRequest().body("Error while retrieving current bets.");
        return ResponseEntity.ok(currentBets);
    }
}
