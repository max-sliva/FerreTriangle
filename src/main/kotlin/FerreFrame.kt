import javafx.fxml.Initializable
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import java.net.URL
import java.util.*


class FerreFrame: Initializable {
    lateinit var anchorPane: AnchorPane
    lateinit var boundTriangle: Polygon
    lateinit var movingDot: Circle
    private var anchorX = 0.0
    private var anchorY = 0.0
    private var mouseOffsetFromNodeZeroX = 0.0
    private var mouseOffsetFromNodeZeroY = 0.0
    lateinit var polygon : Polygon
    fun mousePressedDot(mouseEvent: MouseEvent) {
        anchorX = mouseEvent.sceneX
        anchorY = mouseEvent.sceneY
        movingDot.cursor = Cursor.MOVE
        println("triangle points = ${ boundTriangle.points}")
//        println("triangle bounds = ${ boundTriangle.}")
    }
    fun moveDot(mouseEvent: MouseEvent) {
//        if (!movingDot.intersects(polygon.boundsInParent))
        movingDot.translateX = mouseEvent.sceneX - anchorX
        movingDot.translateY = mouseEvent.sceneY - anchorY
        val curX = movingDot.layoutX+movingDot.translateX
        val curY = movingDot.layoutY+movingDot.translateY
        // уравнение левой стороны треугольника: y = -1.73x + 574
//        if (polygon.intersects(curX, curY, 1.0, 1.0))
//            println("Intersection!!")
        println("dot x = ${curX} dot y = ${curY}")
//        if (curX in boundTriangle.points)
//            println("In triangle")
    }
    fun mouseReleasedDot(mouseEvent: MouseEvent) {
        movingDot.layoutX = mouseEvent.sceneX - mouseOffsetFromNodeZeroX
        movingDot.layoutY = mouseEvent.sceneY - mouseOffsetFromNodeZeroY
        //clear changes from TranslateX and TranslateY
        movingDot.translateX = 0.0;
        movingDot.translateY = 0.0;
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
                314.0, 34.0,
                133.0, 347.0,
                492.0, 347.0
            )
        )
        anchorPane.children.add(polygon)
        println("triangle points = ${ polygon.points}")
        movingDot.toFront()

    }
}