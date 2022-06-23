package com.haallo.api.profile.model

sealed class EditProfilePhotoState {
    object OpenCamera : EditProfilePhotoState()
    object OpenGallery : EditProfilePhotoState()
    object DeletePhoto : EditProfilePhotoState()
}