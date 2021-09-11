package io.github.edadma

import io.github.edadma.xlib.extern.{Xlib => x11}

package object xlib {

  type Drawable = x11.Drawable

  implicit class Display(val display: x11.Display) extends AnyVal {

    def XPending: Int = x11.XPending(display)

  }

  implicit class Visual(val visual: x11.Visual) extends AnyVal {}

}
