package com.haidoan.android.stren.core.utils

object ListUtils {
    fun <T> List<T>.replaceWith(newValue: T, replacementCondition: (T) -> Boolean): List<T> {
        return map {
            if (replacementCondition(it)) newValue else it
        }
    }

    /**
     * Merge 2 lists by adding each list's unique elements to result. Then handle conflict
     * between equal elements of 2 lists, and add to result
     */
    fun <T> mergeLists(
        firstList: List<T>,
        secondList: List<T>,
        areEqual: (T, T) -> Boolean,
        handleConflict: (T, T) -> T
    ): List<T> {
        val mergedList = mutableListOf<T>()

        mergedList.addAll(
            firstList.filter { firstListItem ->
                !secondList.any { secondListItem -> areEqual(firstListItem, secondListItem) }
            })
        //Timber.d("mergedList - 1st addAll: ${mergedList}")

        mergedList.addAll(
            secondList.filter { secondListItem ->
                !firstList.any { firstListItem -> areEqual(firstListItem, secondListItem) }
            })

        //Timber.d("mergedList - 2nd addAll: ${mergedList}")

        firstList.filter { firstListItem ->
            secondList.any { secondListItem -> areEqual(firstListItem, secondListItem) }
        }.forEach { firstListItem ->
            mergedList.add(
                handleConflict(
                    firstListItem,
                    secondList.find { areEqual(firstListItem, it) } ?: firstListItem)
            )
        }

        //Timber.d("mergedList - 3rd addAll: ${mergedList}")
        return mergedList
    }
}