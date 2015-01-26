package school

/**
 * Created by mitrus on 1/25/15.
 */
object Utilities {
  private val random = new scala.util.Random

  private def randomString(alphabet: String)(n: Int): String =
    Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

  def randomAlphanumericString(n: Int) =
    randomString("abcdef0123456789")(n)
}
