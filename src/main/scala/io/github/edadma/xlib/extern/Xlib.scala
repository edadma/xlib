package io.github.edadma.xlib.extern

import scala.scalanative.unsafe._

@link("X11")
@extern
object Xlib {

  type Display = Ptr[CStruct0]
  type XEvent  = Ptr[CInt]

  def XPending(display: Display): CInt = extern //2891

}
