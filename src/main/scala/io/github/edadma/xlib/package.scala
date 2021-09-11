package io.github.edadma

import io.github.edadma.xlib.extern.{Xlib => x11}

package object xlib {

  implicit class Display(val display: x11.Display) extends AnyVal {

    def XPending: Int = x11.XPending(display)

  }

}
