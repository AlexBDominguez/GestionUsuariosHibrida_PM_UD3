package com.example.pm_ud3.data

import com.example.pm_ud3.data.local.User
import com.example.pm_ud3.data.remote.RemoteUser

fun User.toRemoteUser(): RemoteUser {
    return RemoteUser(
        id = if(id.startsWith("local_")) "" else id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        userName = userName,
        positionTitle = positionTitle,
        imagen = imagen
    )
}

fun RemoteUser.toLocalUser(): User{
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        age = age,
        userName = userName,
        positionTitle = positionTitle,
        imagen = imagen,
        pendingSync = false,
        pendingDelete = false
    )
}