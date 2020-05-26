# Voice record Discord bot in Kotlin [![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Y8Y61K34E)

[![Github](https://github.com/markusgod/Simple-Discord-bot/workflows/masterci/badge.svg)](https://github.com/markusgod/Simple-Discord-bot)

A simple bot for recording voice chats.

For simple recording:
```text
::record | ::record 10
::stop
```
After recording bot will send an audio file to the chat it was started from.

![screenshot-1](https://i.imgur.com/GiSbNbu.png)

To send recording in pm use
`::record @Someone` or `::record 300 @Someone`, bot will send record to @Someone's private messages.

"Instant replay" functions much like Nvidia's ShadowPlay instant replays (that is where name come from). Bot is constantly recording audio from a channel to a buffer and will send current buffer content in a file when you request it.

Use `::irecord` to start recording and `::ireplay` to replay buffer, and `::istop` to kick it.

Configuration using environment variables:

|Variable|Description|
|:---:|---|
|BOT_TOKEN|Authentication token from discord developer portal|
|CLIENT_ID|Client ID from discord developer portal or bot user id|
|RECORD_LENGTH|Maximum record length in milliseconds. Keep this low on servers with little RAM. Default 5 minutes|
|TOPGG_TOKEN|top.gg bot token for statistics. Optional|

[![Discord Bots](https://top.gg/api/widget/685883762642387005.svg)](https://top.gg/bot/685883762642387005)