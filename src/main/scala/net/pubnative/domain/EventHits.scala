package net.pubnative.domain

trait EventHits extends Event {
  def merge(other: EventHits): EventHits
}
