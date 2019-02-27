package landoopex

import types._

case class CacheEntry(from: Currency, to: Currency)

case class Cache(vals: Map[CacheEntry, Double])

object Cache {
  def empty: Cache                          = Cache(vals = Map.empty)
  def init(kv: (CacheEntry, Double)): Cache = Cache(vals = Map(kv))
}
