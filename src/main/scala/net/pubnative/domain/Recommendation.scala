package net.pubnative.domain

case class Recommendation(
                           app_id: Int,
                           country_code: String,
                           recommended_advertiser_ids: List[Int]
                         )
