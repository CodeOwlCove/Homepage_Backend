package codeowlcove.codeowl_twitchplays_backend.Services;

import codeowlcove.codeowl_twitchplays_backend.Database.DatabaseHandler;
import codeowlcove.codeowl_twitchplays_backend.Database.SQLDatabaseHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import codeowlcove.codeowl_twitchplays_backend.Configuration;

@Service
public class TwitchConnector {

    private static final Logger logger = LoggerFactory.getLogger(TwitchConnector.class);

    //Holds the config of the bot
    private Configuration configuration;
    private TwitchClient twitchClient;
    private DatabaseHandler databaseHandler;

    public TwitchConnector(SQLDatabaseHandler databaseHandler){
        this.databaseHandler = databaseHandler;
        InitializeBot();
    }

    public void InitializeBot(){
        loadConfiguration();

        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();

        OAuth2Credential credential = new OAuth2Credential(
                "twitch",
                configuration.getCredentials().get("irc")
        );

        credential.getAccessToken();
        configuration.getApi().get("twitch_client_id");
        configuration.getApi().get("twitch_client_secret");

        twitchClient = clientBuilder
                .withClientId(configuration.getApi().get("twitch_client_id"))
                .withClientSecret(configuration.getApi().get("twitch_client_secret"))
                .withEnableHelix(true)
                /*
                 * Chat Module
                 * Joins irc and triggers all chat based events (viewer join/leave/sub/bits/gifted subs/...)
                 */
                .withChatAccount(credential)
                .withEnableChat(true)
                /*
                 * Build the TwitchClient Instance
                 */
                .build();

        Start();
    }

    public SimpleEventHandler getEventHandler() {
        return twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class);
    }

    /**
     * Method to register all features
     */
    public void registerFeatures() {

    }

    /**
     * Load the Configuration
     */
    private void loadConfiguration() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("chatBotConfig.yaml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, Configuration.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to load Configuration ... Exiting.");
            System.exit(1);
        }
    }

    public void Start() {
        // Connect to all channels
        for (String channel : configuration.getChannels()) {
            twitchClient.getChat().joinChannel(channel);
        }

        // Enable client helper for Stream GoLive / GoOffline / GameChange / TitleChange Events
        twitchClient.getClientHelper().enableStreamEventListener(configuration.getChannels());
        // Enable client helper for Follow Event
        twitchClient.getClientHelper().enableFollowEventListener(configuration.getChannels());

        twitchClient.getChat().sendMessage(configuration.getChannels().get(0), "Bot is now live!");
    }

    public TwitchClient getTwitchClient() {
        return twitchClient;
    }

}
