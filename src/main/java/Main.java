import discord4j.core.DiscordClientBuilder;

public class Main {

    public static void main(String[] args) {
        new DiscordClientBuilder(System.getenv("token")).build().login().block();
    }
}
