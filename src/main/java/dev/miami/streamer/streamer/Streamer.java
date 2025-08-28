package dev.miami.streamer.streamer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.HashMap;
import java.util.Map;

public class Streamer {
    private static final Map<String, String> replacements = new HashMap<>();
    private static boolean enabled = false;
    private static boolean hideOthers = false;
    private static String selfName = "Miami";
    private static String othersName = "Protected";

    public static int toggleEnabled() {
        enabled = !enabled;
        log("Streamer mode toggled " + (enabled ? "§aON" : "§cOFF"));
        return 1;
    }

    public static int toggleHideOthers() {
        hideOthers = !hideOthers;
        log("Hiding others is now " + (hideOthers ? "§aON" : "§cOFF"));
        return 1;
    }

    public static int changeYourName(String name) {
        selfName = name;
        log("Your name was set to §b" + selfName);
        return 1;
    }

    public static int changeOthersName(String name) {
        othersName = name;
        log("Others' name prefix was set to §b" + othersName);
        return 1;
    }


    public static int addReplacement(String realName, String fakeName) {
        replacements.put(realName, fakeName);
        log("Replaced §b" + realName + " §ewith §a" + fakeName);
        return 1;
    }

    public static int removeReplacement(String realName) {
        if (replacements.remove(realName) != null) {
            log("Removed replacement for §b" + realName);
        } else {
            log("No replacement found for §b" + realName);
        }
        return 1;
    }


    public static String replaceName(String input) {
        if (!enabled || input == null) return input;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getSession() == null) return input;

        String localName = mc.getSession().getUsername();
        String result = input;


        result = result.replace(localName, selfName);


        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }


        if (hideOthers && mc.getNetworkHandler() != null) {
            for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                String otherName = entry.getProfile().getName();
                if (!otherName.equals(localName) && !replacements.containsKey(otherName)) {
                    result = result.replace(otherName, othersName + randomDigits(otherName));
                }
            }
        }

        return result;
    }

    private static String randomDigits(String seed) {
        return String.valueOf(Math.abs(seed.hashCode()) % 1000 + 100);
    }


    public static void log(String msg) {
        MinecraftClient.getInstance().inGameHud.getChatHud()
                .addMessage(net.minecraft.text.Text.literal("§6[Streamer]§r " + msg));
    }

    public static boolean isInternalLog(String msg) {
        return msg != null && msg.startsWith("§6[Streamer]§r");
    }


    public static Iterable<String> getReplacedNames() {
        return replacements.keySet();
    }


    public static Iterable<String> getFakeNames() {
        return replacements.values();
    }

}
