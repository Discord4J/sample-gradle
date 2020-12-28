package app;

import command.CommandListener;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationInfoData;
import reactor.core.publisher.Mono;
import support.AddRandomReaction;
import support.Commands;
import support.VoiceSupport;

import static support.Commands.isAuthor;

public class Main {

    public static void main(String[] args) {
        GatewayDiscordClient client = DiscordClient.create(System.getenv("token"))
                .login()
                .block();

        Mono<Long> ownerId = client.rest().getApplicationInfo()
                .map(ApplicationInfoData::owner)
                .map(user -> Snowflake.asLong(user.id()))
                .cache();

        CommandListener listener = CommandListener.createWithPrefix("!!")
                .filter(req -> isAuthor(ownerId, req))
                .on("echo", Commands::echo)
                .on("exit", (req, res) -> req.getClient().logout())
                .on("status", Commands::status)
                .on("requestMembers", Commands::requestMembers)
                .on("getMembers", Commands::getMembers)
                .on("addRole", Commands::addRole)
                .on("changeAvatar", Commands::changeAvatar)
                .on("changeLogLevel", Commands::logLevelChange)
                .on("react", new AddRandomReaction())
                .on("userinfo", Commands::userInfo)
                .on("reactionRemove", Commands::reactionRemove)
                .on("leaveGuild", Commands::leaveGuild);

        Mono.when(client.on(listener), VoiceSupport.create(client).eventHandlers()).block();
    }
}