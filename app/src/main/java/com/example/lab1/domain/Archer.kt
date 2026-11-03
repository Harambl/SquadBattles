package com.example.lab1.domain

class Archer(name: String) : Person(name, 70) {
    var countSpec = 0

    override fun attack(target: Person) = applyDamage(target, 35)

    override fun spec(target: Person) {
        if (countSpec < 2) {
            countSpec += 1
            if (!target.isInvulnerable) {
                target.health = (target.health - 20).coerceAtLeast(0)
            }
        }
    }

    override fun getHeroType() = "Archer"
}