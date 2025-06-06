package dev.miami.streamer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.miami.streamer.streamer.Streamer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

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
                                .then(ClientCommandManager.argument("prefix", StringArgumentType.greedyString())
                                        .executes(ctx -> Streamer.changeOthersName(StringArgumentType.getString(ctx, "prefix")))
                                )
                        )
                        .then(ClientCommandManager.literal("self")
                                .then(ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> Streamer.changeYourName(StringArgumentType.getString(ctx, "name")))
                                )
                        )
                )
        );
    }
}