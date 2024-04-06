package codeowlcove.codeowl_twitchplays_backend.Controller;

import codeowlcove.codeowl_twitchplays_backend.AnimalRacing.AnimalRaceHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HorseRacingController {

    private AnimalRaceHandler animalRaceHandler;

    public HorseRacingController(AnimalRaceHandler animalRaceHandler){
        this.animalRaceHandler = animalRaceHandler;
    }

    @GetMapping("/horseRaceUpdateProgress")
    public ResponseEntity<String> horseRaceUpdateProgress() {
        String allHorseInformation = animalRaceHandler.GetAllHorseInformationAsJSON();
        if(allHorseInformation == null)
            return ResponseEntity.badRequest().body("Error");
        return ResponseEntity.ok(animalRaceHandler.GetAllHorseInformationAsJSON());
    }

}
