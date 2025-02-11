package codeowlcove.codeowl_twitchplays_backend.Controller;

import codeowlcove.codeowl_twitchplays_backend.AnimalRacing.AnimalRaceHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/AnimalRace")
@CrossOrigin(origins = "https://codeowlcove.com")
public class AnimaRacingController {

    private AnimalRaceHandler animalRaceHandler;

    public AnimaRacingController(AnimalRaceHandler animalRaceHandler){
        this.animalRaceHandler = animalRaceHandler;
    }

    @GetMapping("/animalRaceUpdateProgress")
    public ResponseEntity<String> horseRaceUpdateProgress() {
        String allHorseInformation = animalRaceHandler.GetAllHorseInformationAsJSON();
        if(allHorseInformation == null)
            return ResponseEntity.badRequest().body("Error");
        return ResponseEntity.ok(animalRaceHandler.GetAllHorseInformationAsJSON());
    }

    @GetMapping("/animalRaceState")
    public ResponseEntity<String[]> horseRaceState() {
        return ResponseEntity.ok(animalRaceHandler.getCurrentGameState());
    }

    @GetMapping("/getLastWinners")
    public ResponseEntity<String[]> getLastWinners() {
        return ResponseEntity.ok(animalRaceHandler.GetLastWinners());
    }

    @GetMapping("/getAnimalRaceInformation")
    public SseEmitter getAnimalRaceInformation() {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try{
                while(true){
                    emitter.send(animalRaceHandler.GetAnimalRaceInformation());
                    Thread.sleep((int) animalRaceHandler.getTickTime());
                }
            } catch (Exception e){
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

}
