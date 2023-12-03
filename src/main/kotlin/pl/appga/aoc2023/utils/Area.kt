package pl.appga.aoc2023.utils

data class Area(val topLeft: Point, val bottomRight: Point) {
    constructor(x0: Int, y0: Int, x1: Int, y1: Int) : this(Point(x0, y0), Point(x1, y1))
}
