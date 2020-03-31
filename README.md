# Voice record Discord bot in Kotlin [![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Y8Y61K34E)
![](https://github.com/markusgod/Simple-Discord-bot/workflows/masterci/badge.svg) 

A simple bot for recording voice chats.

For simple recording:
```
::record time
::stop
```
After recording bot will send an audio file to the chat it was started from.

![screenshot-1](https://i.imgur.com/GiSbNbu.png)

"Instant replay" functions much like Nvidia's ShadowPlay instant replays (that is where name come from). Bot is constantly recording audio from a channel to a buffer and will send current buffer content in a file when you request it.
```
::irecord
::ireplay
```
Configuration using environment variables:

|Variable|Description|
|:---:|---|
|BOT_TOKEN|Authentication token from discord developer portal|
|CLIENT_ID|Client ID from discord developer portal or bot user id|
|RECORD_LENGTH|Maximum record length in milliseconds. Keep this low on servers with little RAM|