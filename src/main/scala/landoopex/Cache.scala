package landoopex

import types._

case class CacheEntry(from: Currency, to: Currency)

case class Cache(vals: Map[CacheEntry, Rate])

object Cache {
  def empty: Cache                        = Cache(vals = Map.empty)
  def init(kv: (CacheEntry, Rate)): Cache = Cache(vals = Map(kv))
}
