package codeowlcove.codeowl_twitchplays_backend.Features;

import codeowlcove.codeowl_twitchplays_backend.Services.TwitchConnector;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteChannelChatToConsole {

    private static final Logger logger = LoggerFactory.getLogger(TwitchConnector.class);

    public WriteChannelChatToConsole(SimpleEventHandler eventHandler) {
        logger.info("Registering ChannelMessage Event");
        eventHandler.onEvent(ChannelMessageEvent.class, event -> onChannelMessage(event));
    }

    /**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    public void onChannelMessage(ChannelMessageEvent event) {
        logger.info(" --- Channel ["+event.getChannel().getName()+"] - User["+event.getUser().getName()+"] - Message ["+event.getMessage()+"]");
    }
}
