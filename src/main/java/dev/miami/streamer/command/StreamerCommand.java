package dev.miami.streamer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import dev.miami.streamer.streamer.Streamer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.concurrent.CompletableFuture;

public class StreamerCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(ClientCommandManager.literal("streamer")

                .then(ClientCommandManager.literal("toggle")
                        .executes(ctx -> Streamer.toggleEnabled())
                        .then(ClientCommandManager.literal("hideothers")
                                .executes(ctx -> Streamer.toggleHideOthers())
                        )
                )

                .then(ClientCommandManager.literal("change")
                        .then(ClientCommandManager.literal("others")
                                .then(ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> Streamer.changeOthersName(
                                                StringArgumentType.getString(ctx, "name")
                                        ))
                                )
                        )
                        .then(ClientCommandManager.literal("self")
                                .then(ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> Streamer.changeYourName(
                                                StringArgumentType.getString(ctx, "name")
                                        ))
                                )
                        )
                )

                .then(ClientCommandManager.literal("override")
                        .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestOnlinePlayers(builder))
                                .then(ClientCommandManager.argument("fake-name", StringArgumentType.word())
                                        .suggests((ctx, builder) -> suggestFakeNames(builder))
                                        .executes(ctx -> Streamer.addReplacement(
                                                StringArgumentType.getString(ctx, "player"),
                                                StringArgumentType.getString(ctx, "fake-name")
                                        ))
                                )
                        )
                )

                .then(ClientCommandManager.literal("clear")
                        .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggestReplacedPlayers(builder))
                                .executes(ctx -> {
                                    String realName = StringArgumentType.getString(ctx, "player");
                                    return Streamer.removeReplacement(realName);
                                })
                        )
                )

        );
    }


    private static CompletableFuture<Suggestions> suggestOnlinePlayers(SuggestionsBuilder builder) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getNetworkHandler() == null) return builder.buildFuture();

        String remaining = builder.getRemaining().toLowerCase();
        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            String name = entry.getProfile().getName();
            if (name.toLowerCase().startsWith(remaining)) builder.suggest(name);
        }

        return builder.buildFuture();
    }


    private static CompletableFuture<Suggestions> suggestFakeNames(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (String fake : Streamer.getFakeNames()) {
            if (fake.toLowerCase().startsWith(remaining)) builder.suggest(fake);
        }
        return builder.buildFuture();
    }


    private static CompletableFuture<Suggestions> suggestReplacedPlayers(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (String realName : Streamer.getReplacedNames()) {
            if (realName.toLowerCase().startsWith(remaining)) builder.suggest(realName);
        }
        return builder.buildFuture();
    }

}
