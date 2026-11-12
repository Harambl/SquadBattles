package com.example.lab1.domain

class Mage(name: String) : Person(name, 80) {
    var countSpec = 0

    override fun attack(target: Person) = applyDamage(target, 27)

    override fun spec(target: Person) {

    }

    override fun spec(targets: List<Person>) {
        if (countSpec < 2) {
            countSpec += 1
            targets.forEach { target ->
                if (target.isAlive && !target.isInvulnerable) {
                    target.health = (target.health - 25).coerceAtLeast(0)
                }
            }
        }
    }

    override fun getHeroType() = "Mage"
}