# Simple Discord bot in Kotlin
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