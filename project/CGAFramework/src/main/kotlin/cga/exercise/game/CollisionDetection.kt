package cga.exercise.game

import cga.exercise.components.geometry.Renderable
import kotlin.math.roundToInt

class CollisionDetection {
    companion object{
        fun checkCollision(obj1 : Renderable?, obj2 : Renderable?) : Int {
            var distance = obj1?.getPosition()?.sub(obj2?.getPosition())?.length()
            return distance!!.compareTo(35f)
        }

        fun randtreffer(obj1: Renderable?, obj2: Renderable?) : Boolean{
            return obj1!!.getPosition().z.roundToInt() == obj2!!.getPosition().z.roundToInt() &&
                    Math.sqrt(Math.pow((obj1.getPosition().x - obj2.getPosition().x).toDouble(), 2.0)) > 40 || Math.sqrt(Math.pow((obj1.getPosition().x - obj2.getPosition().x).toDouble(), 2.0)) < -40 &&
                    Math.sqrt(Math.pow((obj1.getPosition().y - obj2.getPosition().y).toDouble(), 2.0)) > 40 || Math.sqrt(Math.pow((obj1.getPosition().y - obj2.getPosition().y).toDouble(), 2.0)) < -40
        }
    }
}