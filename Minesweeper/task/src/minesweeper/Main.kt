@file:Suppress("SpellCheckingInspection")

package minesweeper

enum class CellState { UNEXPLORED, FREE, NUMBER, MINEHIDDEN, MINE, MARKED }
data class Cell(var state: CellState, var minesAround: Int)

const val ROW = 9
const val COLUMNS = 9
var firstMove = true

fun main() {
    println("How many mines do you want on the field?")
    val minesNumber = readln().toInt()
    val minefield = Array(ROW) { Array(COLUMNS) { Cell(CellState.UNEXPLORED, 0) } }
    val mines = mutableListOf<Pair<Int, Int>>()
    minefield.print()
    sortMines(minefield, mines)
    while (true) {
        println("Set/unset mine marks or claim a cell as free:")
        val (rowS, columnS, mark) = readln().split(' ')
        val row = columnS.toInt() - 1
        val column = rowS.toInt() - 1
        when (mark) {
            "mine" -> {
                if (firstMove) {
                    putMines(mines, minesNumber)
                    firstMove = false
                }
                if (minefield[row][column].state == CellState.UNEXPLORED || minefield[row][column].state == CellState.MINEHIDDEN) {
                    minefield[row][column].state = CellState.MARKED
                } else if (minefield[row][column].state == CellState.MARKED) {
                    minefield[row][column].state = CellState.UNEXPLORED
                }
            }

            "free" -> {
                if (firstMove) {
                    putMines(mines, minesNumber, Pair(row, column))
                    firstMove = false
                }
                if (mines.contains(Pair(row, column))) {
                    minefield[row][column].state = CellState.MINE
                    minefield.print()
                    println("You stepped on a mine and failed!")
                    break
                } else {
                    explore(minefield, mines, row, column)
                }
            }
        }
        minefield.print()
        if (checkWin(minefield, mines)) {
            println("Congratulations! You won!")
            break
        }
    }
}

fun explore(minefield: Array<Array<Cell>>, mines: List<Pair<Int, Int>>, row: Int, column: Int) {
    if (row !in 0 until ROW || column !in 0 until COLUMNS) {
        return
    }

    if (minefield[row][column].state != CellState.UNEXPLORED && minefield[row][column].state != CellState.MARKED) {
        return
    }

    if (minefield[row][column].state == CellState.MARKED && mines.contains(Pair(row, column))) {
        return
    }

    var minesAround = 0
    if (row > 0 && column > 0) {
        for (i in row - 1..row + 1) {
            for (j in column - 1..column + 1) {
                if (i in minefield.indices && j in 0 until minefield[0].size && mines.contains(Pair(i, j))) {
                    minesAround++
                }
            }
        }
    } else if (row == 0 && column > 0) {
        for (i in 0..1) {
            for (j in column - 1..column + 1) {
                if (j in 0 until minefield[0].size && mines.contains(Pair(i, j))) {
                    minesAround++
                }
            }
        }
    } else if (row > 0) {
        for (i in row - 1..row + 1) {
            for (j in 0..1) {
                if (i in minefield.indices && j in 0 until minefield[0].size && mines.contains(Pair(i, j))) {
                    minesAround++
                }
            }
        }
    } else {
        for (i in 0..1) {
            for (j in 0..1) {
                if (mines.contains(Pair(i, j))) {
                    minesAround++
                }
            }
        }
    }

    minefield[row][column].state = CellState.FREE
    minefield[row][column].minesAround = minesAround
    if (minesAround == 0) {
        for (i in row - 1..row + 1) {
            for (j in column - 1..column + 1) {
                explore(minefield, mines, i, j)
            }
        }
    } else {
        minefield[row][column].state = CellState.NUMBER
    }
}

fun checkWin(minefield: Array<Array<Cell>>, mines: List<Pair<Int, Int>>): Boolean {
    for (i in minefield.indices) {
        for (j in minefield[0].indices) {
            if (minefield[i][j].state == CellState.UNEXPLORED ||
                (minefield[i][j].state == CellState.MINE && !mines.contains(Pair(i, j))) ||
                (minefield[i][j].state == CellState.MARKED && mines.contains(Pair(i, j)))
            ) {
                return false
            }
        }
    }
    return true
}

fun Array<Array<Cell>>.print() {
    println(" │123456789│")
    println("—│—————————│")
    for (i in this.indices) {
        print("${i + 1}│")
        for (j in this[0].indices) {
            when (this[i][j].state) {
                CellState.UNEXPLORED -> print(".")
                CellState.MINEHIDDEN -> print(".")
                CellState.FREE -> print(if (this[i][j].minesAround == 0) "/" else this[i][j].minesAround)
                CellState.MINE -> print("X")
                CellState.MARKED -> print("*")
                CellState.NUMBER -> print(this[i][j].minesAround)
            }
        }
        println("│")
    }
    println("—│—————————│")
}

fun sortMines(minefield: Array<Array<Cell>>, mines: List<Pair<Int, Int>>) {
    for (i in minefield.indices) {
        for (j in minefield[0].indices) {
            if (mines.contains(Pair(i, j))) {
                minefield[i][j].state = CellState.MINEHIDDEN
            }
        }
    }
}

fun putMines(mines: MutableList<Pair<Int, Int>>, minesAround: Int, first: Pair<Int, Int> = Pair(10, 10)) {
    if (minesAround == 0) {
        return
    }
    val row = (Math.random() * ROW).toInt()
    val col = (Math.random() * COLUMNS).toInt()
    if (!mines.contains(Pair(row, col)) && Pair(row, col) != first) {
        mines.add(Pair(row, col))
        putMines(mines, minesAround - 1, first)

    } else {
        putMines(mines, minesAround, first)
    }

}