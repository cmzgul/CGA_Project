package cga.exercise.game

import cga.exercise.components.geometry.Renderable

class CollisionDetection {
    companion object{
        fun checkCollision(obj1 : Renderable?, obj2 : Renderable?) : Int {
            var distance = obj1?.getPosition()?.sub(obj2?.getPosition())?.length()
            return distance!!.compareTo(35f)
        }
    }
}