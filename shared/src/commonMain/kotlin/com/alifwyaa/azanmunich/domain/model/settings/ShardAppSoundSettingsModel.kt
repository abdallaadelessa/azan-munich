package com.alifwyaa.azanmunich.domain.model.settings

/**
 * @author Created by Abdullah Essa on 26.06.21.
 */
data class ShardAppSoundSettingsModel(
    override val displayName: String,
    val appSound: SharedAppSound
) : SharedAppBaseSettingsModel

/**
 * Shared app sound
 */
enum class SharedAppSound {
    DEFAULT, SOUND1
}
