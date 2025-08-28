package dev.miami.streamer;

import com.mojang.brigadier.CommandDispatcher;
import dev.miami.streamer.command.StreamerCommand;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;


public class StreamerModeInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(StreamerModeInitializer::registerCommands);
	}

	public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		StreamerCommand.register(dispatcher);
	}
}