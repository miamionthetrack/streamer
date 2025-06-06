package dev.miami.streamer.streamer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

public class Streamer {
    private static boolean enabled = false;
    private static boolean hideOthers = false;
    private static String selfName = "Miami";
    private static String othersName = "Protected";

    public static int toggleEnabled() {
        enabled = !enabled;
        send("Streamer mode toggled " + (enabled ? "§aON" : "§cOFF"));
        return 1;
    }

    public static int toggleHideOthers() {
        hideOthers = !hideOthers;
        send("Hiding others is now " + (hideOthers ? "§aON" : "§cOFF"));
        return 1;
    }

    public static int changeYourName(String name) {
        selfName = name;
        send("Your name was set to §b" + selfName);
        return 1;
    }

    public static int changeOthersName(String name) {
        othersName = name;
        send("Others' name prefix was set to §b" + othersName);
        return 1;
    }

    public static String replaceName(String input) {
        if (!enabled || input == null) return input;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSession() == null) return input;
        String localName = mc.getSession().getUsername();
        String result = input.replace(localName, selfName);
        if (hideOthers && mc.getNetworkHandler() != null) {
            for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                String otherName = entry.getProfile().getName();
                if (!otherName.equals(localName)) {
                    result = result.replace(otherName, othersName + randomDigits(otherName));
                }
            }
        }
        return result;
    }

    private static String randomDigits(String seed) {
        return String.valueOf(Math.abs(seed.hashCode()) % 1000 + 100);
    }

    private static void send(String msg) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("§6[Streamer]§r " + msg));
    }

}
