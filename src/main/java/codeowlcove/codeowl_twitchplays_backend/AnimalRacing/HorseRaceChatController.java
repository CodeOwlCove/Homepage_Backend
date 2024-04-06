package codeowlcove.codeowl_twitchplays_backend.AnimalRacing;

import codeowlcove.codeowl_twitchplays_backend.Database.DatabaseHandler;
import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import codeowlcove.codeowl_twitchplays_backend.Services.TwitchChat;
import codeowlcove.codeowl_twitchplays_backend.Services.TwitchConnector;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseRaceChatController {
    private static final Logger logger = LoggerFactory.getLogger(TwitchConnector.class);

    private DatabaseHandler databaseHandler;
    private TwitchClient twitchClient;
    private TwitchChat twitchChat;
    private AnimalRaceHandler animalRaceHandler;
    private AnimalRaceBets animalRaceBets;

    public HorseRaceChatController(TwitchConnector twitchConnector, SQLDatabaseHandler databaseHandler, TwitchChat twitchChat,
                                   AnimalRaceHandler animalRaceHandler, AnimalRaceBets animalRaceBets) {
        this.databaseHandler = databaseHandler;
        this.twitchClient = twitchConnector.getTwitchClient();
        this.twitchChat = twitchChat;
        this.animalRaceHandler = animalRaceHandler;
        this.animalRaceBets = animalRaceBets;

        twitchConnector.getEventHandler().onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
    }

    /**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    public void onChannelMessage(ChannelMessageEvent event) {
        if(event.getMessage().charAt(0) != '!')
            return;

        if(event.getMessage().toLowerCase().startsWith("!register")){
            if(databaseHandler.RegisterUser(event.getUser().getName())) {
                logger.info(" --- User [" + event.getUser().getName() + "] registered");
                twitchChat.SendMessage("codeowlcove", "@" + event.getUser().getName() + " | You have been registered!");
            }else{
                logger.info(" --- User [" + event.getUser().getName() + "] already registered");
                twitchChat.SendMessage("codeowlcove", "@" + event.getUser().getName() + " | You are already registered with " + databaseHandler.GetPoints(event.getUser().getName()) + " points!");
            }
        }

        if(event.getMessage().toLowerCase().startsWith("!enter")){

            /*
            if(animalRaceHandler.getCurrentGameState() != AnimalRaceGameState.BETTING) {
                twitchChat.SendMessage("codeowlcove", "Race is currently in progress. No betting allowed!");
                return;
            }
            */


            var splitMessage = getBetInformationOfMessage(event.getMessage().toLowerCase());
            if(splitMessage[0].isEmpty() || splitMessage[1].isEmpty() || splitMessage[2].isEmpty())
                return;
            var horseName = splitMessage[0];
            var horseId =  Integer.parseInt(splitMessage[1]);
            var betAmount = Integer.parseInt(splitMessage[2]);


            if(animalRaceBets.CheckIfUserHasPoints(event.getUser().getName(), betAmount)){
                logger.info(" --- User [" + event.getUser().getName() + "] entered the race with horse [" + horseName + "] and bet [" + betAmount + "] points");
                animalRaceBets.addBet(event.getUser().getName(), horseName, horseId, betAmount);
            }else{
                logger.info(" --- User [" + event.getUser().getName() + "] does not have enough points to bet [" + betAmount + "] points");
                twitchChat.SendMessage("codeowlcove", "@"+ event.getUser().getName() +" | You do not have enough points to bet " + betAmount + " points!");
                return;
            }

        }

        if(event.getMessage().toLowerCase().startsWith("!getpoints")){
            logger.info(" --- Getting points for user " + event.getUser().getName());
            try {
                twitchChat.SendMessage("codeowlcove", "@"+ event.getUser().getName() +" | You have " + databaseHandler.GetPoints(event.getUser().getName()) + " points!");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private String[] getBetInformationOfMessage(String message){
        var splitMessage = message.toLowerCase().split(" ");
        String horseID;
        String horseName;
        String betAmount;
        if(splitMessage[1].matches("\\d+")) {
            horseID = splitMessage[1];
            horseName = GetHorseNameFromID(horseID);
        }else{
            horseName = splitMessage[1];
            horseID = horseName;
        }


        try {
            betAmount = splitMessage[2];
        }catch(Exception e){
            logger.error(" --- Invalid bet amount");
            return new String[]{"", "", ""};
        }
        return new String[]{horseName, horseID, betAmount};
    }

    private int GetHorseIDFromName(String horseName){
        for(Animal animal : animalRaceHandler.animals){
            if(animal.getName().equals(horseName))
                return animal.getId();
        }
        return -1;
    }

    private String GetHorseNameFromID(int horseID){
        for(Animal animal : animalRaceHandler.animals){
            if(animal.getId() == horseID)
                return animal.getName();
        }
        return "";
    }

    private String GetHorseNameFromID(String horseID){
        for(Animal animal : animalRaceHandler.animals){
            if(animal.getId() == Integer.parseInt(horseID))
                return animal.getName();
        }
        return "";
    }

}
