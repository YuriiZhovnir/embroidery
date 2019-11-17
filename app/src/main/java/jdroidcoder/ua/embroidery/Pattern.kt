package jdroidcoder.ua.embroidery

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Pattern(var width: Int? = 0,
                   var height: Int? = 0,
                   var cells: RealmList<Cell> = RealmList(),
                   var preview: String? = null,
                   @PrimaryKey var id: Int = Random().nextInt()) : RealmObject()

open class Cell(@PrimaryKey var tag: String? = null,
                var position: RealmList<Int> = RealmList(2),
                var color: CellColor? = null) : RealmObject()

open class CellColor(var colorName: String? = null,
                     var colorCode: Int? = 0,
                     @PrimaryKey var id: Int = Random().nextInt()) : RealmObject()