package codeowlcove.codeowl_twitchplays_backend.Controller;

import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
