package net.pubnative.domain

object AppCountryReport {

  def fromAggregates(aggregates: Iterable[EventsAggregate]): Iterable[AppCountryReport] = {
    if(Option(aggregates).isEmpty) return Nil
    aggregates.
      groupBy(agg => (agg.app_id, agg.country_code)).
      map {
        case ((appId, countryCode), chunks) => collapse(appId, countryCode, chunks)
      }
  }

  private def collapse(appId: Int, countryCode: Option[String], chunks: Iterable[EventsAggregate]): AppCountryReport = {
    var clicks = 0L
    var imps = 0L
    var revenue = 0D
    chunks.foreach(r => {
      clicks += r.clicks
      imps += r.impressions
      revenue += r.revenue
    })
    AppCountryReport(appId, countryCode, imps, clicks, revenue)
  }
}

case class AppCountryReport(
                             app_id: Int,
                             country_code: Option[String],
                             impressions: Long,
                             clicks: Long,
                             revenue: Double
                           )
