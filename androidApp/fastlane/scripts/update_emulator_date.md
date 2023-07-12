# month/day/hour/minute/year.second
# Reset to start of the day 07/03/2022 01:59:59
adb shell "su 0 toybox date 030701592022.59"

# Start App
adb shell am start -n com.alifwyaa.azanmunich.android/.MainActivity

# Fajr 05:00
adb shell "su 0 toybox date 030704592022.59"

# Fajr 06:39
adb shell "su 0 toybox date 030706382022.59"

# Duhur 12:30
adb shell "su 0 toybox date 030712292022.59"

# Asr 15:31
adb shell "su 0 toybox date 030715302022.59"

# Ma3'reeb 18:11
adb shell "su 0 toybox date 030718102022.59"

# 3sha2 19:43
adb shell "su 0 toybox date 030719422022.59"
