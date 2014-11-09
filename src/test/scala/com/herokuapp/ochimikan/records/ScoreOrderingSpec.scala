package com.herokuapp.ochimikan.records

import com.herokuapp.ochimikan.specs2.BeEquivalentToMatcher
import java.text.SimpleDateFormat
import java.util.{
  Date,
  Locale
}
import org.specs2.Specification
import org.specs2.matcher.{
  Expectable,
  Matcher
}

/** Specification for [[Score]]. */
class ScoreOrderingSpec extends Specification with BeEquivalentToMatcher {
  import ScoreOrderingSpec._

  def is = s2"""

  When the following Scores are given,
    A: ${showScore(scoreA)}
    B: ${showScore(scoreB)}
    C: ${showScore(scoreC)}
    D: ${showScore(scoreD)}
    E: ${showScore(scoreE)}
  
  Score A should be better than   Score B: ${scoreA must beGreaterThan(scoreB)}
  Score B should be worse  than   Score A: ${scoreB must beLessThan(scoreA)}
  Score B should be better than   Score C: ${scoreB must beGreaterThan(scoreC)}
  Score C should be worse  than   Score B: ${scoreC must beLessThan(scoreB)}
  Score C should be better than   Score D: ${scoreC must beGreaterThan(scoreD)}
  Score D should be worse  than   Score C: ${scoreD must beLessThan(scoreC)}
  Score D should be equivalent to Score E: ${scoreD must beEquivalentTo(scoreE)}
"""

  val scoreA = Score(100, 1, "A", formatDate("2014-11-08 19:20:00"))
  val scoreB = Score( 90, 1, "A", formatDate("2014-11-08 19:20:00"))
  val scoreC = Score( 90, 2, "A", formatDate("2014-11-08 19:20:00"))
  val scoreD = Score( 90, 2, "A", formatDate("2014-11-08 19:20:01"))
  val scoreE = Score( 90, 2, "B", formatDate("2014-11-08 19:20:01"))
}

/** Companion object of [[ScoreOrderingSpec]]. */
object ScoreOrderingSpec {
  // the date format
  private val dateFormat =
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

  /** Shows a score. */
  def showScore(score: Score): String =
    f"value=${score.value}%3d level=${score.level}%2d player=${score.player}%-5s date=${dateFormat.format(score.date)}"

  /** Formats a specified date string. */
  def formatDate(date: String): Date = dateFormat.parse(date)
}
