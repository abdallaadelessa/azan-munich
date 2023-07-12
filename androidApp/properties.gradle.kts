//======================================================>
// android_signing.properties

object Signing {
    const val KEYSTORE_FILE_NAME = "keystore/android_signing_keystore.jks"
    const val KEYSTORE_PASSWORD = "android_signing_keystore_password"
    const val KEYSTORE_KEY_ALIAS = "android_signing_key_alias"
    const val KEYSTORE_KEY_PASSWORD = "android_signing_key_password"
}

val signingProperties = java.util.Properties().apply {
    val localSigningPropFile = file("./keystore/android_signing.properties")
    if (localSigningPropFile.exists()) {
        // Locally
        load(localSigningPropFile.reader())
    } else {
        // CI
        setProperty(Signing.KEYSTORE_KEY_ALIAS, System.getenv(Signing.KEYSTORE_KEY_ALIAS))
        setProperty(Signing.KEYSTORE_KEY_PASSWORD, System.getenv(Signing.KEYSTORE_KEY_PASSWORD))
        setProperty(Signing.KEYSTORE_PASSWORD, System.getenv(Signing.KEYSTORE_PASSWORD))
    }
}
extra["keyStoreFileName"] = Signing.KEYSTORE_FILE_NAME
extra["keystorePassword"] = signingProperties[Signing.KEYSTORE_PASSWORD] as String
extra["keystoreKeyAlias"] = signingProperties[Signing.KEYSTORE_KEY_ALIAS] as String
extra["keystoreKeyPassword"] = signingProperties[Signing.KEYSTORE_KEY_PASSWORD] as String

//======================================================>
// android_metadata.properties

object AppVersion {
    const val KEY_VERSION_CODE = "version_code"
    const val KEY_VERSION_NAME = "version_name"
}

private val metadataProperties = java.util.Properties()
    .apply { load(file("app_version.properties").reader()) }
extra["appVersionCode"] = metadataProperties[AppVersion.KEY_VERSION_CODE]
extra["appVersionName"] = metadataProperties[AppVersion.KEY_VERSION_NAME]

//======================================================>
