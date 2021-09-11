package io.github.edadma.xlib.extern

import scala.scalanative.unsafe._

@link("X11")
@extern
object Xlib {

  type XID      = CUnsignedLong
  type Window   = XID
  type Drawable = XID
  type Font     = XID
  type Pixmap   = XID
  type Cursor   = XID
  type Colormap = XID
  type GContext = XID
  type KeySym   = XID
  type KeyCode  = CUnsignedChar
  type Display  = Ptr[CStruct0]
  type XEvent   = Ptr[CStruct0] //todo: xlib.h 973
  type Visual_s = CStruct0 //todo: xlib.h 227
  type Visual   = Ptr[Visual_s]

  def XNextEvent(display: Display, event: XEvent): CInt = extern //2851
  def XPending(display: Display): CInt                  = extern //2891

}
