import java.util.ArrayList

//class MyPolyline(var lines: Array<MyLine>){
class MyPolyline(private var points: Array<MyPoint>){
    override fun toString(): String {
        var str = ""
        var i = 0
        for (point in points){
//            println("x$i = ${point.x} x$i = ${point.x}")
            str+="x$i = ${point.x} y$i = ${point.y}\n"
            i++
        }
        return str
    }
    fun isPointInside(point: MyPoint): Boolean{
        var inside = false
        val a = (points[0].x - point.x) * (points[1].y - points[0].y) - (points[1].x - points[0].x) * (points[0].y - point.y)
        val b = (points[1].x - point.x) * (points[2].y - points[1].y) - (points[2].x - points[1].x) * (points[1].y - point.y)
        val c = (points[2].x - point.x) * (points[0].y - points[2].y) - (points[0].x - points[2].x) * (points[2].y - point.y)
        println("a = $a b = $b c = $c")
        inside = a >= 0 && b >= 0 && c >= 0 || a <= 0 && b <= 0 && c <= 0
//        inside = a > 0 && b > 0 && c > 0 || a < 0 && b < 0 && c < 0
        return inside
    }

    fun sideWithPoint(point: MyPoint): ArrayList<MyPoint> {
        var pointsOfSide = ArrayList<MyPoint>()
        val a = getPointLineCrossing(arrayOf(points[0], points[1]), point)
        val b = getPointLineCrossing(arrayOf(points[1], points[2]), point)
        val c = getPointLineCrossing(arrayOf(points[2], points[0]), point)

        if (a == 0.0) {
            pointsOfSide.add(points[0])
            pointsOfSide.add(points[1])
        }
        else if (b == 0.0) {
            pointsOfSide.add(points[1])
            pointsOfSide.add(points[2])
        }
        else if (c == 0.0) {
            pointsOfSide.add(points[2])
            pointsOfSide.add(points[0])
        }

        return pointsOfSide
    }

    private fun getPointLineCrossing(points: Array<MyPoint>, point: MyPoint) = (points[0].x - point.x) * (points[1].y - points[0].y) - (points[1].x - points[0].x) * (points[0].y - point.y)
    fun sideOutOfPoint(point: MyPoint): ArrayList<MyPoint> {
        var pointsOfSide = ArrayList<MyPoint>()
        val a = getPointLineCrossing(arrayOf(points[0], points[1]), point)
        val b = getPointLineCrossing(arrayOf(points[1], points[2]), point)
        val c = getPointLineCrossing(arrayOf(points[2], points[0]), point)

        if (a > 0.0) {
            pointsOfSide.add(points[0])
            pointsOfSide.add(points[1])
        }
        else if (b > 0.0) {
            pointsOfSide.add(points[1])
            pointsOfSide.add(points[2])
        }
        else if (c > 0.0) {
            pointsOfSide.add(points[2])
            pointsOfSide.add(points[0])
        }

        return pointsOfSide
    }
}
