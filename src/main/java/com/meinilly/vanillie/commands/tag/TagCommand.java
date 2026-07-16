package com.meinilly.vanillie.commands.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import com.meinilly.vanillie.Vanillie;
import com.meinilly.vanillie.tagmenu.TagMenuUi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TagCommand implements CommandExecutor, TabCompleter {
    private final static MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("[Vanillie] Nur Spieler können diesen Befehl nutzen!");
            return true;
        }

        if (args.length == 0) {
            showTagUsage(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "new":
                newTag(player, args);
                break;
            case "add":
                addTag(player, args);
                break;
            case "remove":
                removeTag(player, args);
                break;
            case "list":
                listTag(player, args);
                break;
            case "removefully":
                fullyremoveTag(player, args);
                break;
            case "up":
                upTag(player, args);
                break;
            case "menu":
                TagMenuUi ui = new TagMenuUi();
                player.openInventory(ui.getInventory());
                break;
            default:
                showTagUsage(player);
        }

        return true;
    }

    // Tag Subcommands
    private void newTag(Player player, String[] args) {
        if (args.length < 2) {
            showTagUsage(player);
            return;
        }

        String tagText = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Component message = miniMessage
                .deserialize(Vanillie.getPluginTitle("Tag\n")
                        + Vanillie.getGradientText(TagManager.newTag(player.getUniqueId(), tagText)));

        player.sendMessage(message);
    }

    private void removeTag(Player player, String[] args) {
        if (args.length < 2) {
            showTagUsage(player);
            return;
        }

        Component message = miniMessage
                .deserialize(Vanillie.getPluginTitle("Tag\n")
                        + Vanillie.getGradientText(TagManager.removeTag(player.getUniqueId(), args[1])));

        player.sendMessage(message);
    }

    private void addTag(Player player, String[] args) {
        if (args.length < 2) {
            showTagUsage(player);
            return;
        }

        Component message = miniMessage
                .deserialize(Vanillie.getPluginTitle("Tag\n")
                        + Vanillie.getGradientText(TagManager.addTag(player.getUniqueId(), args[1])));

        player.sendMessage(message);
    }

    private void listTag(Player player, String[] args) {
        Component message = miniMessage
                .deserialize(Vanillie.getPluginTitle("Tag\n") + Vanillie.getGradientText(TagManager.getTagList()));

        player.sendMessage(message);
    }

    private void fullyremoveTag(Player player, String[] args) {
        if (args.length < 2) {
            showTagUsage(player);
            return;
        }

        Component message = miniMessage
                .deserialize(Vanillie.getPluginTitle("Tag\n")
                        + Vanillie.getGradientText(TagManager.deleteTagFromList(player.getUniqueId(), args[1])));

        player.sendMessage(message);
    }

    private void upTag(Player player, String[] args) {
        showUnimplemented(player);
    }

    // Messages
    public static void showUnimplemented(CommandSender sender) {
        Component message = miniMessage.deserialize(
                Vanillie.getPluginTitle("Fehler") +
                        Vanillie.getGradientText(" Dieser Befehl ist noch nicht fertig :("));
        sender.sendMessage(message);
    }

    public static void showTagUsage(CommandSender sender) {
        Component message = miniMessage.deserialize(
                Vanillie.getPluginTitle("Tag\n") +
                        Vanillie.getGradientText("/tag list\n - Zeige alle Tags mit Nummer\n\n") +
                        Vanillie.getGradientText("/tag new <Text>\n - Erstelle einen neuen Tag\n\n") +
                        Vanillie.getGradientText("/tag add <Nummer>\n - Aktiviert einen Tag\n\n") +
                        Vanillie.getGradientText("/tag remove <Nummer>\n - Deaktiviert einen Tag\n\n") +
                       // Vanillie.getGradientText("/tag up <Nummer>\n - Bewegt einen Tag vor die anderen\n\n") +
                        Vanillie.getGradientText(
                                "/tag removefully <Nummer>\n - Löscht einen Tag von der Liste\n\n")
                        +
                        Vanillie.getImportantText(
                                "Wenn du <b>/tag new</b> eingibst, werden dir Farbwerte und Farbverläuft angezeigt und wie du sie benutzt!"));
        sender.sendMessage(message);
    }

    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String @NotNull [] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("list", "new", "remove", "removefully", "add", "up"));
        } else if (args.length == 2) {
            String mainCommand = args[0].toLowerCase();

            switch (mainCommand) {
                case "new":
                    completions.addAll(Arrays.asList("<gradient:red:blue>Farbverlauf Beispie</gradient>",
                            "<b>Dicker Text</b>", "<u>Unterstichener Text</u>",
                            "<color:green>Grüner Text</color>",
                            "<color:#0000ff>Blauer Text mit Hex Code</color>",
                            "<rainbow>Regenbogen!</rainbow>"));
                    break;
                case "add":
                    completions.addAll(Arrays.asList("Nummer die du in /tag list findest"));
                    break;
                case "remove":
                    completions.addAll(Arrays.asList("Nummer die du in /tag list findest"));
                    break;
                case "removefully":
                    completions.addAll(Arrays.asList("(Löscht für alle!) Nummer die du in /tag list findest"));
                    break;
            }
        }

        String input = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
    }
}