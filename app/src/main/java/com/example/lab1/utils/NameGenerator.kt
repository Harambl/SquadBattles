package com.example.lab1.utils

object NameGenerator {
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

    fun generateTeam(size: Int = 3): List<Pair<String, String>> {
        val team = mutableListOf<Pair<String, String>>()
        repeat(size) { index ->
            when (index % 3) {
                0 -> team.add("Archer" to generateArcherName())
                1 -> team.add("Warrior" to generateWarriorName())
                2 -> team.add("Mage" to generateMageName())
            }
        }
        return team
    }
}