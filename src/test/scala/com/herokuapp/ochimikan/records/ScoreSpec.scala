package com.herokuapp.ochimikan.records

import java.util.Date
import org.specs2.Specification

/** Specification of [[Score]]. */
class ScoreSpec extends Specification { def is = s2"""
  Score(10000, 5, Player, 2014-11-27T19:43:54-0500) should
  have score=10000                    ${ score1.value  must_== 10000 }
  have level=5                        ${ score1.level  must_== 5 }
  have player=Player                  ${ score1.player must_== "Player" }
  have date=2014-11-27T19:43:54-0500  ${ score1.date   must_== new Date(1417135434000L)}

  Score(215000, 23, プレイヤー, 2014-11-27T19:51:26-0500) should
  have score=215000                   ${ score2.value  must_== 215000 }
  have level=23                       ${ score2.level  must_== 23 }
  have player=プレイヤー              ${ score2.player must_== "プレイヤー" }
  have date=2014-11-27T19:15:26-0500  ${ score2.date   must_== new Date(1417135886000L)}

  Score(0, 0, "", 1970-01-01T00:00:00+0000) should
  have score=0                        ${ score3.value  must_== 0 }
  have level=0                        ${ score3.level  must_== 0 }
  have player=""                      ${ score3.player must_== "" }
  have date=1970-01-01T00:00:00+0000  ${ score3.date   must_== new Date(0)}

  Score(-1, 5, Player, 2014-11-27T19:43:54-0500) should
  throw an IllegalArgumentException  ${
    Score(-1, 5, "Player", new Date(1417135434000L)) must
      throwA[IllegalArgumentException]
  }

  Score(10000, -1, Player, 2014-11-27T19:43:54-0500) should
  throw an IllegalArgumentException  ${
    Score(10000, -1, "Player", new Date(1417135434000L)) must
      throwA[IllegalArgumentException]
  }
"""

  val score1 = Score(10000, 5, "Player", new Date(1417135434000L))

  val score2 = Score(215000, 23, "プレイヤー", new Date(1417135886000L))

  val score3 = Score(0, 0, "", new Date(0))
}
