package net.pubnative.domain

object ImpressionHits {

  def convert(imps: Iterable[Impression]): ImpressionHits = {
    if (Option(imps).isEmpty) throw new IllegalArgumentException("imps argument is null")

    if (imps.size == 0) throw new IllegalArgumentException("imps argument is an empty list")

    val imp = imps.head
    ImpressionHits(imp.id, imp.app_id, imp.advertiser_id, imp.country_code, impressions = imps.size)
  }

}

case class ImpressionHits(
                                val id: String,
                                val app_id: Int,
                                val advertiser_id: Int,
                                val country_code: Option[String],
                                var impressions: Long = 0,
                                var clicks: Long = 0,
                                var revenue: Double = 0
                              ) extends EventHits {

  def merge(other: EventHits): EventHits = {
    other match {
      case imp: ImpressionHits => {
        ImpressionHits(
          id, app_id, advertiser_id, country_code, impressions + imp.impressions, clicks, revenue
        )
      }
      case clk: ClickHits => {
        withClickHits(clk)
      }
      case _ => throw new IllegalArgumentException("Failed to merge other EventHits: Unknown EventHits type.")
    }

  }

  def withClickHits(ch: ClickHits): ImpressionHits = {
    ImpressionHits(
      id, app_id, advertiser_id, country_code, impressions, this.clicks + ch.clicks, this.revenue + ch.revenue
    )
  }

}
