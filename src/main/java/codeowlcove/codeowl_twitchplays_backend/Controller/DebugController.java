package codeowlcove.codeowl_twitchplays_backend.Controller;

import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Debug")
@CrossOrigin(origins = "https://codeowlcove.com")
public class DebugController {

    SQLDatabaseHandler sqlDatabaseHandler;

    public DebugController(SQLDatabaseHandler sqlDatabaseHandler){
        this.sqlDatabaseHandler = sqlDatabaseHandler;
    }

    @GetMapping("/debug")
    public void Debug(){
        System.out.println("Debugging...");
    }
}
