package landoopex

import io.estatico.newtype.macros.newtype

package object types {
  @newtype case class Currency(value: String)
}
