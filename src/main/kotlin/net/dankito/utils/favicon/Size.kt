package net.dankito.utils.favicon


open class Size(open val width: Int, open val height: Int) : Comparable<Size> {

  open fun isSquare(): Boolean {
    return width == height
  }

  open fun getDisplayText(): String {
    return "$width x $height"
  }


  override fun compareTo(other: Size): Int {
    if(width == other.width) {
      return height.compareTo(other.height)
    }

    return width.compareTo(other.width)
  }


  override fun toString(): String {
    return getDisplayText()
  }

}