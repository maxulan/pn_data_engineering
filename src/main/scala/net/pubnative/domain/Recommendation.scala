package net.pubnative.domain

object Recommendation {

  def fromAggregates(aggregates: Iterable[EventsAggregate]): Iterable[Recommendation] = {
    aggregates.
      groupBy(agg => (agg.app_id, agg.country_code)).
      map {
        case ((appId, countryCode), chunks) => Recommendation(appId, countryCode, top5Advertisiers(chunks))
      }
  }

  private def top5Advertisiers(chunks: Iterable[EventsAggregate]): List[Int] = {
    chunks.groupBy(_.advertiser_id).mapValues(cpi).toList.sortWith(_._2 > _._2).take(5).map(_._1)
  }

  /**
    * Cost Per Impression
    * @param chunks
    * @return
    */
  private def cpi(chunks: Iterable[EventsAggregate]): Double = {
    var imps = 0L
    var revenue = 0D
    chunks.foreach(r => {
      imps += r.impressions
      revenue += r.revenue
    })
    revenue / imps
  }
}

case class Recommendation(
                           app_id: Int,
                           country_code: Option[String],
                           recommended_advertiser_ids: List[Int]
                         )
