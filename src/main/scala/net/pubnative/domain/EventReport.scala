package net.pubnative.domain

case class EventReport(
                        app_id: Int,
                        country_code: Option[String],
                        impressions: Long,
                        clicks: Long,
                        revenue: Double
                      )
