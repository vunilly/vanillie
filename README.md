# Vanillie - For a pleasent minecraft server experience

Vanillie is a personal project for a minecraft server.

Plugin version: v.1.1

# Features

All features are integrated in a nice, clean and simple to use ui. The plugin is designed to be very user friendly.

## /tag - custom prefixes
You can create custom tags/prefixes, with a list of colors or your own colors.
- They can have gradients and symbols.
- Custom color support using Hex (`#ff0000` = red) Codes
- Each player can make 5 tags and use a maximum of 3
- A tag shows above the name and chat (For tablist, use the Placeholder Apis player team name)
- Players can change the order how they are shown

## /vote - weather and time voting
Players can vote for either day, night, clear weather, rain or thunderstorm.
- Shows voting in chat with clickable buttons in chat
- Shows time remaining in bossbar

## /pvp - toggle pvp
Players who just want to chill can turn off pvp/turn on pacifist mode
- Has 60 second cooldown before you can turn it off
- In pacifist mode, you cant hit players or get hit
- Mobs still hit you
- Cannot be changed when player size is too small

## /size - change player model size
Players can change their ingame size
- Fun for hide and seek
- Maximum size is 3.0(~5 blocks) and minimum is 0.1(10cm)
- You are able to decrease your size in cm
- Cannot be changed when player has pvp cooldown

## Other stuff
- /vanillieconifg - `/vconf <clearconfig|reloadconfig|saveconfig|resetbook>` (resetbook shows a book on first join, resetbook allows you to show it again if you change it, which requirese a recompilation)
- /vanillie - Shows a book with info on the plugin
- Join messages will be changed if you have tag active

# Language
The plugin (by default) comes with a German language pack and no english translation. However, after starting the plugin, in the plugin folder there is a file called `lang.json` which you can edit to your liking. Then use `/vconf reloadconfig` (FIRST USE `/vconf saveconfig` TO SAVE ALL OTHER DATA!)