package net.pubnative.domain

case class Impression(
                       id: String,
                       app_id: Int,
                       advertiser_id: Int,
                       country_code: Option[String]
                     ) extends Event