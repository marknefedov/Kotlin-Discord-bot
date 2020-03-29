# Voice record Discord bot in Kotlin [![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/Y8Y61K34E)
![](https://github.com/markusgod/Simple-Discord-bot/workflows/masterci/badge.svg) 

A simple bot for recording voice chats.

For simple recording:
```
::record time
::stop
```
"Instant replay" functions much like Nvidia's ShadowPlay instant replays (that is where name come from). Bot is constantly recording audio from a channel to a buffer and will send current buffer content in a file when you request it.
```
:!record 
:!replay
```

After recording bot will send an audio file to the chat it was started from.

![screenshot](https://i.imgur.com/GiSbNbu.png)