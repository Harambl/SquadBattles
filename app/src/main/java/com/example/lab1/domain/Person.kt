package com.example.lab1.domain

abstract class Person(val name: String, val maxHealth: Int) {
    var health: Int = maxHealth
    var isDefended: Boolean = false
    var isInvulnerable: Boolean = false
    var turnsRemaining: Int = 0
    val isAlive: Boolean get() = health > 0

    abstract fun attack(target: Person)
    abstract fun spec(target: Person)
    abstract fun getHeroType(): String

    open fun spec(targets: List<Person>) {
        val firstAlive = targets.firstOrNull { it.isAlive }
        if (firstAlive != null) {
            spec(firstAlive)
        }
    }

    fun tick() {
        if (turnsRemaining > 0) {
            turnsRemaining--
            if (turnsRemaining == 0) {
                isInvulnerable = false
            }
        }
    }

    fun defend() {
        isDefended = true
    }

    protected fun applyDamage(target: Person, damage: Int) {
        if (target.isInvulnerable) {

        } else if (target.isDefended) {
            target.health = (target.health - damage / 2).coerceAtLeast(0)
        } else {
            target.health = (target.health - damage).coerceAtLeast(0)
        }
    }
}