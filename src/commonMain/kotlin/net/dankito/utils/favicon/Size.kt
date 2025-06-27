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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is Size) return false

    if (width != other.width) return false
    if (height != other.height) return false

    return true
  }

  override fun hashCode(): Int {
    var result = width
    result = 31 * result + height
    return result
  }

}