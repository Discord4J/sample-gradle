import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class Main {

    public static void main(String[] args) {
        /*        DiscordClient discord = DiscordClient.create(System.getenv("token"));

         *//*        GatewayDiscordClient client = discord.login().block();*/

        DiscordClient client = new DiscordClientBuilder(System.getenv("token"))
                .build();

        // option 1
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(event ->
                        Mono.fromRunnable(() -> {
                            // call listener.onMessage(event)
                        }).onErrorResume(t -> {
                            // log
                            return Mono.empty();
                        }))
                .subscribe();

        // option 2
//        client.getEventDispatcher().on(MessageCreateEvent.class)
//                .flatMap(event ->
//                        listener.onMessage(event) // make it return Mono.empty() for now
//                        .onErrorResume(t -> {
//                            // log
//                            return Mono.empty();
//                        }))
//                .subscribe();

        client.getEventDispatcher().on(ReadyEvent.class)
                .flatMap(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(),
                            self.getDiscriminator()));
                    return Mono.empty();
                })
                .subscribe();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().orElse("").equalsIgnoreCase("!ping"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Pong!"))
                .subscribe();

        client.login().block();
    }
}
