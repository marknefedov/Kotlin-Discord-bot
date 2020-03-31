import java.util.concurrent.TimeUnit

fun toHumanTime(time : Long) : String {
    return String.format(
        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
        TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1)
    )
}