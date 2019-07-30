package net.pubnative.domain

object ClickHits {

  def convert(clicks: Iterable[Click]): ClickHits = {
    if (Option(clicks).isEmpty) throw new IllegalArgumentException("clicks argument is null")

    if (clicks.size == 0) throw new IllegalArgumentException("clicks argument is an empty list")

    val click = clicks.head
    val revenue: Double = clicks.map(_.revenue).reduce((a, b) => a + b)
    ClickHits(click.impression_id, revenue, clicks.size)
  }
}

case class ClickHits(
                      impression_id: String,
                      revenue: Double,
                      clicks: Int
                    ) extends EventHits {

  def merge(other: EventHits): EventHits = {
    other match {
      case imp: ImpressionHits => {
        imp.withClickHits(this)
      }
      case clk: ClickHits => {
        ClickHits(impression_id, revenue + clk.revenue, clicks + clk.clicks)
      }
      case _ => throw new IllegalArgumentException("Failed to merge other EventHits: Unknown EventHits type.")
    }

  }
}
