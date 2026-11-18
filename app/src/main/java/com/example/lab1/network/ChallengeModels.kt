package com.example.lab1.network

import kotlinx.serialization.Serializable

@Serializable
data class ChallengesResponse(
    val challenges: List<Challenge>
)

@Serializable
data class Challenge(
    val id: String,
    val name: String,
    val type: String, // "regular" или "boss"
    val description: String,
    val members: List<ChallengeMember>
)

@Serializable
data class ChallengeMember(
    val heroClass: String,
    val name: String? = null
)