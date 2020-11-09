package app;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import reactor.core.publisher.Mono;
import support.BotSupport;
import support.ExtraBotSupport;
import support.VoiceSupport;

public class Main {

    public static void main(String[] args) {
        GatewayDiscordClient client = DiscordClient.create(System.getenv("token")).login().block();
        Mono.when(
                BotSupport.create(client).eventHandlers(),
                ExtraBotSupport.create(client).eventHandlers(),
                VoiceSupport.create(client).eventHandlers())
                .block();
    }
}