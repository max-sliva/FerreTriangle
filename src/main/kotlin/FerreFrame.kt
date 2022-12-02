import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import java.net.URL
import java.util.*

//исходная картинка взята с https://direct.farm/post/opredeleniye-mekhanicheskogo-sostava-pochvy-metodom-ferre-5708
class FerreFrame: Initializable {
    lateinit var anchorPane: AnchorPane
//    lateinit var boundTriangle: Polygon
    lateinit var movingDot: Circle
    private var anchorX = 0.0
    private var anchorY = 0.0
    private var mouseOffsetFromNodeZeroX = 0.0
    private var mouseOffsetFromNodeZeroY = 0.0
    lateinit var polygon : Polygon
    var curX = 0.0
    var curY = 0.0
    var xAfterStop = 0.0
    var yAfterStop = 0.0
    lateinit var myTriangle: MyPolyline
    fun mousePressedDot(mouseEvent: MouseEvent) {
        anchorX = mouseEvent.sceneX
        anchorY = mouseEvent.sceneY
        movingDot.cursor = Cursor.MOVE
        println("triangle points = ${ polygon.points}")
    }
    fun moveDot(mouseEvent: MouseEvent) {
        // уравнение левой стороны треугольника: y = -1.73x + 574
        curX = movingDot.layoutX+mouseEvent.sceneX - anchorX
        curY = movingDot.layoutY+mouseEvent.sceneY - anchorY
//        if (curY >= (-1.73*curX+574)){ // если двигаем точку внутри треугольника
        if (myTriangle.isPointInside(MyPoint(curX, curY))) {
            movingDot.translateX = mouseEvent.sceneX - anchorX
            movingDot.translateY = mouseEvent.sceneY - anchorY
            println("moving dot x = ${mouseEvent.sceneX}")
            xAfterStop = curX
        }
        else if ((curX == ((curY-574) / (-1.73)).toInt().toDouble())){  //если точка на границе треугольника
            xAfterStop = mouseEvent.sceneX
        } else {  //если точка за пределами треугольника
            movingDot.layoutX = (curY-574) / (-1.73)
            xAfterStop = movingDot.layoutX
            movingDot.translateX = 0.0
            movingDot.translateY = mouseEvent.sceneY - anchorY
            println("xAfterStop = $xAfterStop")
        }
    }
    fun mouseReleasedDot(mouseEvent: MouseEvent) {
        if (curY <(-1.73*curX+574)) { //если остановились за пределами треугольника
            movingDot.layoutX = xAfterStop
        }
        else movingDot.layoutX = mouseEvent.sceneX - mouseOffsetFromNodeZeroX
        movingDot.layoutY = mouseEvent.sceneY - mouseOffsetFromNodeZeroY
        //clear changes from TranslateX and TranslateY
        movingDot.translateX = 0.0
        movingDot.translateY = 0.0
        movingDot.cursor = Cursor.HAND
    }

    fun mouseEnteredDot(mouseEvent: MouseEvent) {
        movingDot.cursor = Cursor.HAND
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("pane width = ${anchorPane.maxWidth}  pane height = ${anchorPane.prefHeight}")
        polygon = Polygon()
        polygon.points.addAll(
            *arrayOf(
                313.0, 34.0,
                132.0, 346.0,
                493.0, 346.0
            )
        )
        polygon.fill = Color.TRANSPARENT
        polygon.stroke = Color.BLUE
        polygon.strokeWidth = 4.0
        anchorPane.children.add(polygon)
        var myPoints = ArrayList<MyPoint>()
        var j = 0
        var myPoint: MyPoint
        for (i in 0..4 step 2){
            myPoints.add(MyPoint(polygon.points[i], polygon.points[i+1]))
            println("added to myPoints ${polygon.points[i]}, ${polygon.points[i+1]}")
        }
        myTriangle = MyPolyline(myPoints.toTypedArray())
        println("polygon points = ${ polygon.points}")
        println("myTriangle points = $myTriangle")
        movingDot.toFront()
        val isInside = myTriangle.isPointInside(MyPoint(313.0, 30.0))
        println("myPoint inside is $isInside")
    }
}