package codeowlcove.codeowl_twitchplays_backend.Services;

import com.github.twitch4j.TwitchClient;
import org.springframework.stereotype.Service;

@Service
public class TwitchChat {

    TwitchConnector twitchConnector;
    TwitchClient twitchClient;

    public TwitchChat(TwitchConnector twitchConnector){
        this.twitchConnector = twitchConnector;
        this.twitchClient = twitchConnector.getTwitchClient();
    }

    public void SendMessage(String channel, String message){
        twitchConnector.getTwitchClient().getChat().sendMessage(channel, message);
    }
}
