package com.zemtsov.cookbook

/**
 * Алгоритмическая задача
 *
 * Есть односвязный список
 * 0 -> 1 -> 2 -> 3 -> 4 -> 5 - индексы
 * A -> B -> C -> D -> E -> F - список
 *
 * Нужно вывести N-ый с конца элемент
 * 2 = D
 */

// scratch.kts
//println(findItem(generateDataStructure(), 2))

data class Item(
    val value: String,
    var next: Item? = null
)

fun findItem(startItem: Item, itemIndexFromTail: Int): Item? {
    var currentIndex = 0
    var foundItem: Item? = null

    var nextItem: Item? = startItem
    while (true) {
        if (currentIndex - itemIndexFromTail > 0) {
            if (foundItem == null) {
                foundItem = startItem
            }
            foundItem = foundItem.next
        }

        nextItem = nextItem?.next

        if (nextItem == null) return foundItem

        currentIndex++
    }
}

fun generateDataStructure(): Item {
    val itemA = Item("A")
    val itemB = Item("B")
    val itemC = Item("C")
    val itemD = Item("D")
    val itemE = Item("E")
    val itemF = Item("F")

    itemA.next = itemB
    itemB.next = itemC
    itemC.next = itemD
    itemD.next = itemE
    itemE.next = itemF

    return itemA
}
