package com.example.engine

data class VisualizerStep(
    val arrayState: List<Int>,
    val activeIndices: List<Int>,
    val sortedIndices: List<Int>,
    val description: String
)

object AlgorithmVisualizer {

    fun generateBubbleSortSteps(initial: List<Int>): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        val arr = initial.toMutableList()
        val n = arr.size
        val sortedIndices = mutableListOf<Int>()

        steps.add(
            VisualizerStep(
                arrayState = arr.toList(),
                activeIndices = emptyList(),
                sortedIndices = emptyList(),
                description = "Kondisi awal array sebelum Bubble Sort dimulai."
            )
        )

        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                steps.add(
                    VisualizerStep(
                        arrayState = arr.toList(),
                        activeIndices = listOf(j, j + 1),
                        sortedIndices = sortedIndices.toList(),
                        description = "Membandingkan elemen ${arr[j]} dan ${arr[j + 1]}."
                    )
                )
                if (arr[j] > arr[j + 1]) {
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    steps.add(
                        VisualizerStep(
                            arrayState = arr.toList(),
                            activeIndices = listOf(j, j + 1),
                            sortedIndices = sortedIndices.toList(),
                            description = "Menukar posisi karena ${arr[j + 1]} > ${arr[j]}."
                        )
                    )
                }
            }
            sortedIndices.add(n - i - 1)
        }
        sortedIndices.add(0)

        steps.add(
            VisualizerStep(
                arrayState = arr.toList(),
                activeIndices = emptyList(),
                sortedIndices = (0 until n).toList(),
                description = "Sorting selesai! Seluruh elemen tersusun rapi dari kecil ke besar."
            )
        )

        return steps
    }

    fun generateBinarySearchSteps(sortedList: List<Int>, target: Int): List<VisualizerStep> {
        val steps = mutableListOf<VisualizerStep>()
        var left = 0
        var right = sortedList.size - 1

        steps.add(
            VisualizerStep(
                arrayState = sortedList,
                activeIndices = listOf(left, right),
                sortedIndices = emptyList(),
                description = "Mencari angka $target di antara index $left dan $right."
            )
        )

        var found = false
        while (left <= right) {
            val mid = (left + right) / 2
            steps.add(
                VisualizerStep(
                    arrayState = sortedList,
                    activeIndices = listOf(mid),
                    sortedIndices = emptyList(),
                    description = "Memeriksa titik tengah (mid): Index $mid bernilai ${sortedList[mid]}."
                )
            )

            if (sortedList[mid] == target) {
                steps.add(
                    VisualizerStep(
                        arrayState = sortedList,
                        activeIndices = listOf(mid),
                        sortedIndices = listOf(mid),
                        description = "Ditemukan! Nilai $target berada pada index $mid."
                    )
                )
                found = true
                break
            } else if (sortedList[mid] < target) {
                left = mid + 1
                steps.add(
                    VisualizerStep(
                        arrayState = sortedList,
                        activeIndices = if (left <= right) listOf(left, right) else emptyList(),
                        sortedIndices = emptyList(),
                        description = "${sortedList[mid]} < $target. Membuang separuh kiri, geser batas kiri ke index $left."
                    )
                )
            } else {
                right = mid - 1
                steps.add(
                    VisualizerStep(
                        arrayState = sortedList,
                        activeIndices = if (left <= right) listOf(left, right) else emptyList(),
                        sortedIndices = emptyList(),
                        description = "${sortedList[mid]} > $target. Membuang separuh kanan, geser batas kanan ke index $right."
                    )
                )
            }
        }

        if (!found) {
            steps.add(
                VisualizerStep(
                    arrayState = sortedList,
                    activeIndices = emptyList(),
                    sortedIndices = emptyList(),
                    description = "Target $target tidak ditemukan dalam array."
                )
            )
        }

        return steps
    }
}
