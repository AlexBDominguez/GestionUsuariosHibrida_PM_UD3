package com.example.pm_ud3.data

import com.example.pm_ud3.data.local.User

object TestUsers {

    val users = listOf(
        User(
            id = "",
            firstName = "Ana",
            lastName = "García",
            email = "ana.garcia@test.com",
            age = 28,
            userName = "anagarcia",
            positionTitle = "Developer",
            imagen = "https://randomuser.me/api/portraits/women/1.jpg"
        ),
        User(
            id = "",
            firstName = "Carlos",
            lastName = "López",
            email = "carlos.lopez@test.com",
            age = 35,
            userName = "clopez",
            positionTitle = "Project Manager",
            imagen = "https://randomuser.me/api/portraits/men/2.jpg"
        ),
        User(
            id = "",
            firstName = "Lucía",
            lastName = "Martínez",
            email = "lucia.martinez@test.com",
            age = 42,
            userName = "luciam",
            positionTitle = "UX Designer",
            imagen = "https://randomuser.me/api/portraits/women/3.jpg"
        ),
        User(
            id = "",
            firstName = "Miguel",
            lastName = "Rodríguez",
            email = "miguel.rodriguez@test.com",
            age = 31,
            userName = "mrodriguez",
            positionTitle = "Backend Developer",
            imagen = "https://randomuser.me/api/portraits/men/4.jpg"
        ),
        User(
            id = "",
            firstName = "Elena",
            lastName = "Fernández",
            email = "elena.fernandez@test.com",
            age = 29,
            userName = "elenaf",
            positionTitle = "Frontend Developer",
            imagen = "https://randomuser.me/api/portraits/women/5.jpg"
        ),
        User(
            id = "",
            firstName = "David",
            lastName = "Sánchez",
            email = "david.sanchez@test.com",
            age = 38,
            userName = "dsanchez",
            positionTitle = "DevOps Engineer",
            imagen = "https://randomuser.me/api/portraits/men/6.jpg"
        )
    )
}