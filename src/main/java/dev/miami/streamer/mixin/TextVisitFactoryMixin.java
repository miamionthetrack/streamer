package dev.miami.streamer.mixin;

import dev.miami.streamer.streamer.Streamer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextVisitFactory.class)
public class TextVisitFactoryMixin {

	@ModifyArg(
			method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"
			),
			index = 0
	)
	private static String injectNameHiding(String original) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc == null || mc.getSession() == null || original == null) {
			return original;
		}

		if (Streamer.isInternalLog(original)) {
			return original;
		}

		return Streamer.replaceName(original);
	}
}
