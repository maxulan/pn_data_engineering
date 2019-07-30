package net.pubnative.domain

case class Click (
                  impression_id: String,
                  revenue: Double
                ) extends Event
