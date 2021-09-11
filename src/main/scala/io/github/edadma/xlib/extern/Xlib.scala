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
  type GC       = Ptr[CStruct0]

  def XOpenDisplay(display_name: CString): Display      = extern //1483
  def XNextEvent(display: Display, event: XEvent): CInt = extern //2851
  def XPending(display: Display): CInt                  = extern //2891

  // macros

  @name("xlib_DefaultScreen")
  def DefaultScreen(display: Display): CInt = extern //93
  @name("xlib_DefaultRootWindow")
  def DefaultRootWindow(display: Display): CInt = extern //94

}
