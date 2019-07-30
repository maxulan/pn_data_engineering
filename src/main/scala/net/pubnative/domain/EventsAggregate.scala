package net.pubnative.domain

case class EventsAggregate(
                                 val app_id: Int,
                                 val advertiser_id: Int,
                                 val country_code: Option[String],
                                 var impressions: Long = 0,
                                 var clicks: Long = 0,
                                 var revenue: Double = 0
                               )