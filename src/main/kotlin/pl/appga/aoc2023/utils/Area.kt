package pl.appga.aoc2023.utils

data class Area(val topLeft: Point, val bottomRight: Point) {
    constructor(x0: Int, y0: Int, x1: Int, y1: Int) : this(Point(x0, y0), Point(x1, y1))
}

data class BoundingBox(val min: Point, val max: Point) {
    constructor(minX: Int, minY: Int, maxX: Int, maxY: Int) : this(Point(minX, minY), Point(maxX, maxY))
    constructor(sizeX: Int, sizeY: Int) : this(Point(0, 0), Point(sizeX - 1, sizeY - 1))

    val rangeX = min.x..max.x
    val rangeY = min.y..max.y

    fun inRange(p: Point) = p.x in rangeX && p.y in rangeY
    fun neighbours(p: Point) =
        listOf(Point(p.x + 1, p.y), Point(p.x, p.y + 1), Point(p.x - 1, p.y), Point(p.x, p.y - 1)).filter(::inRange)
}