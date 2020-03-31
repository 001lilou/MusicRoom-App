package io.kroom.app.view.activitymain.trackvoteevent.model

class Privacy (email: PrivacyEnum,
               location: PrivacyEnum,
               friends: PrivacyEnum,
               musicalPreferencesGenre: PrivacyEnum)
enum class PrivacyEnum() {
    PUBLIC,
    FRIENDS,
    PRIVATE
}