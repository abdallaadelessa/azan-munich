
#=============> Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.alifwyaa.azanmunich.**$$serializer { *; }

-keepclassmembers class com.alifwyaa.azanmunich.** {
    *** Companion;
}
-keepclasseswithmembers class com.alifwyaa.azanmunich.** {
    kotlinx.serialization.KSerializer serializer(...);
}

#=============> Crashlytics
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
