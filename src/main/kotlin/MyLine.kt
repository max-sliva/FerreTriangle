import kotlin.math.pow
import kotlin.math.sqrt

data class MyLine(val point1: MyPoint, val point2: MyPoint){
        fun length(): Double = sqrt((point1.x-point2.x).pow(2)+(point1.y-point2.y).pow(2))
    companion object {
    }
}
