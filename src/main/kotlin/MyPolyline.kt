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

        inside = a >= 0 && b >= 0 && c >= 0 || a <= 0 && b <= 0 && c <= 0
        return inside
    }
}
