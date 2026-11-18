package com.example.lab1.domain

class Warrior(name: String) : Person(name, 110) {
    var countSpec = 0

    override fun attack(target: Person) = applyDamage(target, 20)

    override fun spec(target: Person) {
        if (countSpec < 2) {
            countSpec += 1
            isInvulnerable = true
            turnsRemaining = 2
        }
    }

    override fun getHeroType() = "Warrior"
}