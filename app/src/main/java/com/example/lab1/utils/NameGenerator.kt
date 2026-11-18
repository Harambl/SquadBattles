package com.example.lab1.utils

import com.example.lab1.domain.Warrior

object NameGenerator {

    private val classNames = listOf("Archer", "Warrior", "Mage")
    private val archerNames = listOf(
        "Леголас", "Грин", "Стрелок", "Ксавье", "Орлиный Глаз",
        "Ветер", "Точный", "Лесной Бегун", "Сокол", "Ястреб"
    )

    private val warriorNames = listOf(
        "Гимли", "Торин", "Боромир", "Арагорн", "Гром",
        "Стальной", "Молот", "Щит", "Воин", "Защитник"
    )

    private val mageNames = listOf(
        "Гэндальф", "Саруман", "Мерлин", "Дамблдор", "Маг",
        "Мудрый", "Огненный", "Теневой", "Звездочет", "Чародей"
    )

    fun generateArcherName(): String = archerNames.random()
    fun generateWarriorName(): String = warriorNames.random()
    fun generateMageName(): String = mageNames.random()

    fun generateTeam(size: Int): List<Pair<String, String>> {
        val team = mutableListOf<Pair<String, String>>()
        var className: String
        for (index in 1..size){
            className = classNames.random()
            when (className) {
                "Archer" -> team.add(className to generateArcherName())
                "Warrior" -> team.add(className to generateWarriorName())
                "Mage" -> team.add(className to generateMageName())
            }
        }
        return team
    }
}