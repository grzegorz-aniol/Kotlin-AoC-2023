package pl.appga.aoc2023.utils

operator fun Area.contains(p: Point) =
    p.x in topLeft.x..bottomRight.x && p.y in topLeft.y .. bottomRight.y


