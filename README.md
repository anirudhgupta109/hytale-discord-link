# Hytale Discord Link

A module that brings a simple, lightweight, easy-to-use, and bloat-free bridge between Discord and Hytale, heavily inspired and based on [EssentialsX Discord](https://github.com/EssentialsX/Essentials) which was made for Minecraft.

Features you'd want from a Discord bridge such as:
* Hytale Chat -> Discord Channel
* Discord Channel -> Hytale Chat
* Basic Hytale -> Discord Event Monitoring (Join/Leave/Death/Mute)
* & more...

---

## Table of Contents
> * [Initial Setup](#initial-setup)
> * [Configuring authentication](#configuring-authentication)
> * [How to compile](#How-to-Compile?)

---

## Initial Setup

0. Before starting your server, there are a few steps you have to take. First, you must create a new
Discord bot at [discord.com/developers/applications](https://discord.com/developers/applications/).

1. Once on that page, click on "New Application" button on the top right, give your bot a name, and
then click "Create".
> ![Creating Application](https://i.imgur.com/4VfNpQc.gif)
> `New Application` -> Give Application a Name -> `Create`

2. Once you create the application, you'll be directed to its overview. From this screen, you'll
need to copy your "Application ID" and save it for a later step. To copy your 
Application ID, click the upper-left most blue "Copy" button. Make sure to save it for a later step.
> ![Copy Application ID](https://i.imgur.com/1QuUYKN.gif)
> `Copy` -> Paste into Notepad for later step

3. Optionally, you can set an icon for your application as it will be the icon for the bot too.
> ![Avatar](https://i.imgur.com/NuFS9kT.png)

4. The next step is actually creating a bot user for your application. From the overview screen,
this is done by going to the "Bot" tab on the left, then clicking the "Add Bot" on the right,
and finally then clicking "Yes, do it!".
> ![Create Bot](https://i.imgur.com/oW47yTu.gif)
> `Bot` -> `Add Bot` -> `Yes, do it!`

5. Once on this screen, you'll need to uncheck the "Public Bot" setting, enable all the "Privileged
Intents", and then click "Save Changes". This prevents other people from adding your bot and also
allows your bot to use more Discord features.
> ![Update Bot Settings](https://i.imgur.com/eIegfCC.gif)
> Uncheck `Public Bot` -> Check `Presence Intent` -> Check `Server Members Intent` -> Check `Message Content Intent` -> Save Changes`

6. Finally, you'll need to copy your bot's token and save it for a later step. To copy your bot's token,
click the blue "Reset Token" button right of your bot's icon, then click "Yes, do it!", and finally
click "Copy". Make sure to save it for a later step.
> ![Copy Token](https://i.imgur.com/C8Sk0z6.gif)
> `Reset Token` -> `Yes, do it!` -> `Copy` -> Paste into Notepad for later step
   
7. Next up is adding your bot to your Discord server.

8. Once on the Discord authorization website, select the server from the "Select a server" dropdown 
that you want to add the bot to. Then click the "Authorize" button. You may be prompted to confirm
you are not a bot, proceed with that like you would any other captcha.
> ![Authorize](https://i.imgur.com/KXkESqC.gif)
> Select Server -> `Authorize`

9. For the next few steps, you're going to need to do some stuff in Discord, so start up your
Discord desktop/web client. 

10. Once in your Discord client, you'll need to enable Developer Mode. Do this by going into the 
Settings, then go to the "Advanced" tab and check on the "Developer Mode" at the bottom of the
page. Once you've checked "Developer Mode" on, click the `X` at the top right to exit Settings.
> ![Developer Mode](https://i.imgur.com/f0Dmxcd.gif)
> `User Settings` -> `Advanced` -> Check `Developer Mode` -> Exit Settings

11. Next is copying a few IDs. First up, you'll want to copy the server (aka guild) id. Do this by
finding the server you added the bot to, right click its icon, and click "Copy ID". Once you copied
it, make sure to save it for a later step.
> ![Guild ID](https://i.imgur.com/0mg2yT3.gif)
> Right click server -> `Copy ID` -> Paste into Notepad for later step

12. The other ID you need to copy is the ID of the channel you wish to be your primary channel.
In other words, this will be the channel that, by default, receives messages for player chat/join/leave/death
messages as well as mute/kicks. To see how to further configure message types, see [Configuring Messages](#configuring-messages).
> ![Primary Channel ID](https://i.imgur.com/uMODfiQ.gif)
> Right-click your 'primary' channel -> `Copy ID` -> Paste into Notepad for later step

13. You've successfully copied all the necessary IDs needed for a basic setup. Next up is generating the
default config for the plugin, so you can start setting it up! Do this by putting the JAR (you can download
it [here](https://github.com/anirudhgupta109/hytale-discord-link/releases/latest) if you do not
already have one) in your mods folder, starting your server, and then stopping it as soon as it finishes
starting up.

14. Now you can start to configure the plugin with all the stuff you copied from earlier. Open the config
`mods/anirudhgupta109_HytaleDiscordLink/config.yml`. When you open the config, the
first thing to configure is your bot's token. Replace `YOUR_BOT_TOKEN` in the config with the token you
copied earlier from step 6.

15. Next is the guild ID. Replace the zeros for the guild value in the config with the guild ID you copied
from step 13.

16. Finally, you'll need to paste thechannel ID you copied from step 14 and paste it in the channels section
and once you've done that save the config file!

17. Congratulations, you've completed the initial setup guide! When you start up your server, you should
notice that chat and other messages start showing up in the channel you requested they be. Now that you
completed the initial, go back up to the [Table Of Contents](#table-of-contents) to see what other cool things you can do!


---

## Configuring authentication
The plugin also allows a server owner an optional feature where only members of the discord channel can join and play on the server
via the `auth-enabled` option in `config.yml`.

This can be further expanded to have `strict-auth` as an option, requiring a link code every session join
(useful for `offline` play or when you have players joining and leaving your Discord Server often).



## How to Compile?
Obtain the `HytaleServer.jar` file following the guide [HERE](https://support.hytale.com/hc/en-us/articles/45326769420827-Hytale-Server-Manual#server-setup) and place in libs/

Run the build using:

```./gradlew clean build shadowJar```

The output JAR will be found in build/libs/hytale-discord-link-<`VERSION`>-all.jar
PROFIT!


### WIP
Discord role -> Hytale group sync

Execution of console commands via discord

List commands and webhook integration