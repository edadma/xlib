package io.github.edadma

import io.github.edadma.xlib.extern.{Xlib => lib}

import scala.scalanative.unsafe._
import scala.scalanative.libc.stdlib._
import scala.scalanative.unsigned._

package object xlib {

  type Drawable = lib.Drawable
  type Window   = lib.Window
  type Screen   = lib.Screen
  type Pixmap   = lib.Pixmap
  type Time     = Long
  type KeySym   = Long

  private def bool(a: CInt): Boolean = if (a == 0) false else true

  private def bool(a: CUnsignedInt): Boolean = if (a == 0.toUInt) false else true

  private def bool2int(a: Boolean): CInt = if (a) 1 else 0

  implicit class Display(val ptr: lib.Display) extends AnyVal {

    def isNull: Boolean = ptr eq null

    def createSimpleWindow(parent: Window,
                           x: Int,
                           y: Int,
                           width: Int,
                           height: Int,
                           border_width: Int,
                           border: Long,
                           background: Long): Window =
      lib.XCreateSimpleWindow(ptr,
                              parent,
                              x,
                              y,
                              width.toUInt,
                              height.toUInt,
                              border_width.toUInt,
                              border.toULong,
                              background.toULong)

    def defaultRootWindow: Window = lib.XDefaultRootWindow(ptr)

    def defaultVisual(screen_number: Int): Visual = lib.XDefaultVisual(ptr, screen_number)

    def closeDisplay: Int = lib.XCloseDisplay(ptr)

    def defaultScreen: Int = lib.XDefaultScreen(ptr)

    def nextEvent(ev: XEvent): Int = lib.XNextEvent(ptr, ev.ptr)

    def pending: Int = lib.XPending(ptr)

    def mapWindow(w: Window): CInt = lib.XMapWindow(ptr, w)

    def selectInput(w: Window, event_mask: Long): Int = lib.XSelectInput(ptr, w, event_mask)

  }

  implicit class Visual(val visual: lib.Visual) extends AnyVal {}

  class XEvent(val ptr: lib.XEvent = malloc(sizeof[CLong] * 24.toULong).asInstanceOf[lib.XEvent]) extends AnyVal {
    def getType: Int = !ptr

    def xkey: XKeyEvent = XKeyEvent(ptr.asInstanceOf[lib.XKeyEvent])

    def destroy(): Unit = free(ptr.asInstanceOf[Ptr[Byte]])
  }

  implicit class XKeyEvent(val ptr: lib.XKeyEvent) extends AnyVal {
    def lookupKeysym(index: Int): KeySym = lib.XLookupKeysym(ptr, index).toLong

    def lookupString: (String, KeySym) = {
      val buffer_return = stackalloc[CChar](50)
      val keysym_return = stackalloc[lib.KeySym]

      lib.XLookupString(ptr, buffer_return, 50, keysym_return, null)
      (fromCString(buffer_return), (!keysym_return).toLong)
    }

    def getType: Int        = ptr._1
    def serial: Long        = ptr._2.toLong
    def sendEvent: Boolean  = bool(ptr._3)
    def display: Display    = ptr._4
    def window: Window      = ptr._5
    def root: Window        = ptr._6
    def subwindow: Window   = ptr._7
    def time: Time          = ptr._8.toLong
    def x: Int              = ptr._9
    def y: Int              = ptr._10
    def xRoot: Int          = ptr._11
    def yRoot: Int          = ptr._12
    def state: Int          = ptr._13.toInt
    def keycode: Int        = ptr._14.toInt
    def sameScreen: Boolean = bool(ptr._15)

    def type_=(v: Int): Unit           = ptr._1 = v
    def serial_=(v: Long): Unit        = ptr._2 = v.toULong
    def sendEvent_=(v: Boolean): Unit  = ptr._3 = bool2int(v)
    def display_=(v: Display): Unit    = ptr._4 = v.ptr
    def window_=(v: Window): Unit      = ptr._5 = v
    def root_=(v: Window): Unit        = ptr._6 = v
    def subwindow_=(v: Window): Unit   = ptr._7 = v
    def time_=(v: Time): Unit          = ptr._8 = v.toULong
    def x_=(v: Int): Unit              = ptr._9 = v
    def y_=(v: Int): Unit              = ptr._10 = v
    def xRoot_=(v: Int): Unit          = ptr._11 = v
    def yRoot_=(v: Int): Unit          = ptr._12 = v
    def state_=(v: Int): Unit          = ptr._13 = v.toUInt
    def keycode_=(v: Int): Unit        = ptr._14 = v.toUInt
    def sameScreen_=(v: Boolean): Unit = ptr._15 = bool2int(v)
  }

  def openDisplay(display_name: String): Display =
    Zone(implicit z => lib.XOpenDisplay(if (display_name eq null) null else toCString(display_name)))

  def keysymToString(keysym: KeySym): String = fromCString(lib.XKeysymToString(keysym.toULong))

  // X11/X.h

  /***********************************************************

Copyright 1987, 1998  The Open Group

Permission to use, copy, modify, distribute, and sell this software and its
documentation for any purpose is hereby granted without fee, provided that
the above copyright notice appear in all copies and that both that
copyright notice and this permission notice appear in supporting
documentation.

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
OPEN GROUP BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of The Open Group shall not be
used in advertising or otherwise to promote the sale, use or other dealings
in this Software without prior written authorization from The Open Group.


Copyright 1987 by Digital Equipment Corporation, Maynard, Massachusetts.

                        All Rights Reserved

Permission to use, copy, modify, and distribute this software and its
documentation for any purpose and without fee is hereby granted,
provided that the above copyright notice appear in all copies and that
both that copyright notice and this permission notice appear in
supporting documentation, and that the name of Digital not be
used in advertising or publicity pertaining to distribution of the
software without specific, written prior permission.

DIGITAL DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO EVENT SHALL
DIGITAL BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR
ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,
ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
SOFTWARE.

   ******************************************************************/
  /*****************************************************************
    * RESERVED RESOURCE AND CONSTANT DEFINITIONS
   *****************************************************************/
  lazy val None = 0L /* universal null resource or null atom */

  lazy val ParentRelative = 1L /* background pixmap in CreateWindow
				    and ChangeWindowAttributes */

  lazy val CopyFromParent = 0L /* border pixmap in CreateWindow
				       and ChangeWindowAttributes
				   special VisualID and special window
				       class passed to CreateWindow */

  lazy val PointerWindow = 0L /* destination window in SendEvent */
  lazy val InputFocus    = 1L /* destination window in SendEvent */

  lazy val PointerRoot = 1L /* focus window in SetInputFocus */

  lazy val AnyPropertyType = 0L /* special Atom, passed to GetProperty */

  lazy val AnyKey = 0L /* special Key Code, passed to GrabKey */

  lazy val AnyButton = 0L /* special Button Code, passed to GrabButton */

  lazy val AllTemporary = 0L /* special Resource ID passed to KillClient */

  lazy val CurrentTime = 0L /* special Time */

  lazy val NoSymbol = 0L /* special KeySym */

  /*****************************************************************
    * EVENT DEFINITIONS
   *****************************************************************/
  /* Input Event Masks. Used as event-mask window attribute and as arguments
     to Grab requests.  Not to be confused with event names.  */

  lazy val NoEventMask: Long              = 0L
  lazy val KeyPressMask: Long             = 1L << 0
  lazy val KeyReleaseMask: Long           = 1L << 1
  lazy val ButtonPressMask: Long          = 1L << 2
  lazy val ButtonReleaseMask: Long        = 1L << 3
  lazy val EnterWindowMask: Long          = 1L << 4
  lazy val LeaveWindowMask: Long          = 1L << 5
  lazy val PointerMotionMask: Long        = 1L << 6
  lazy val PointerMotionHintMask: Long    = 1L << 7
  lazy val Button1MotionMask: Long        = 1L << 8
  lazy val Button2MotionMask: Long        = 1L << 9
  lazy val Button3MotionMask: Long        = 1L << 10
  lazy val Button4MotionMask: Long        = 1L << 11
  lazy val Button5MotionMask: Long        = 1L << 12
  lazy val ButtonMotionMask: Long         = 1L << 13
  lazy val KeymapStateMask: Long          = 1L << 14
  lazy val ExposureMask: Long             = 1L << 15
  lazy val VisibilityChangeMask: Long     = 1L << 16
  lazy val StructureNotifyMask: Long      = 1L << 17
  lazy val ResizeRedirectMask: Long       = 1L << 18
  lazy val SubstructureNotifyMask: Long   = 1L << 19
  lazy val SubstructureRedirectMask: Long = 1L << 20
  lazy val FocusChangeMask: Long          = 1L << 21
  lazy val PropertyChangeMask: Long       = 1L << 22
  lazy val ColormapChangeMask: Long       = 1L << 23
  lazy val OwnerGrabButtonMask: Long      = 1L << 24

  /* Event names.  Used in "type" field in XEvent structures.  Not to be
  confused with event masks above.  They start from 2 because 0 and 1
  are reserved in the protocol for errors and replies. */

  lazy val KeyPress         = 2
  lazy val KeyRelease       = 3
  lazy val ButtonPress      = 4
  lazy val ButtonRelease    = 5
  lazy val MotionNotify     = 6
  lazy val EnterNotify      = 7
  lazy val LeaveNotify      = 8
  lazy val FocusIn          = 9
  lazy val FocusOut         = 10
  lazy val KeymapNotify     = 11
  lazy val Expose           = 12
  lazy val GraphicsExpose   = 13
  lazy val NoExpose         = 14
  lazy val VisibilityNotify = 15
  lazy val CreateNotify     = 16
  lazy val DestroyNotify    = 17
  lazy val UnmapNotify      = 18
  lazy val MapNotify        = 19
  lazy val MapRequest       = 20
  lazy val ReparentNotify   = 21
  lazy val ConfigureNotify  = 22
  lazy val ConfigureRequest = 23
  lazy val GravityNotify    = 24
  lazy val ResizeRequest    = 25
  lazy val CirculateNotify  = 26
  lazy val CirculateRequest = 27
  lazy val PropertyNotify   = 28
  lazy val SelectionClear   = 29
  lazy val SelectionRequest = 30
  lazy val SelectionNotify  = 31
  lazy val ColormapNotify   = 32
  lazy val ClientMessage    = 33
  lazy val MappingNotify    = 34
  lazy val GenericEvent     = 35
  lazy val LASTEvent        = 36 /* must be bigger than any event # */

  /* Key masks. Used as modifiers to GrabButton and GrabKey, results of QueryPointer,
     state in various key-, mouse-, and button-related events. */

  lazy val ShiftMask: Int   = 1 << 0
  lazy val LockMask: Int    = 1 << 1
  lazy val ControlMask: Int = 1 << 2
  lazy val Mod1Mask: Int    = 1 << 3
  lazy val Mod2Mask: Int    = 1 << 4
  lazy val Mod3Mask: Int    = 1 << 5
  lazy val Mod4Mask: Int    = 1 << 6
  lazy val Mod5Mask: Int    = 1 << 7

  /* modifier names.  Used to build a SetModifierMapping request or
     to read a GetModifierMapping request.  These correspond to the
     masks defined above. */
  lazy val ShiftMapIndex: Int   = 0
  lazy val LockMapIndex: Int    = 1
  lazy val ControlMapIndex: Int = 2
  lazy val Mod1MapIndex: Int    = 3
  lazy val Mod2MapIndex: Int    = 4
  lazy val Mod3MapIndex: Int    = 5
  lazy val Mod4MapIndex: Int    = 6
  lazy val Mod5MapIndex: Int    = 7

  /* button masks.  Used in same manner as Key masks above. Not to be confused
     with button names below. */

  lazy val Button1Mask: Int = 1 << 8
  lazy val Button2Mask: Int = 1 << 9
  lazy val Button3Mask: Int = 1 << 10
  lazy val Button4Mask: Int = 1 << 11
  lazy val Button5Mask: Int = 1 << 12

  lazy val AnyModifier: Int = 1 << 15 /* used in GrabButton, GrabKey */

  /* button names. Used as arguments to GrabButton and as detail in ButtonPress
     and ButtonRelease events.  Not to be confused with button masks above.
     Note that 0 is already defined above as "AnyButton".  */

  lazy val Button1 = 1
  lazy val Button2 = 2
  lazy val Button3 = 3
  lazy val Button4 = 4
  lazy val Button5 = 5

  /* Notify modes */

  lazy val NotifyNormal       = 0
  lazy val NotifyGrab         = 1
  lazy val NotifyUngrab       = 2
  lazy val NotifyWhileGrabbed = 3

  lazy val NotifyHint = 1 /* for MotionNotify events */

  /* Notify detail */

  lazy val NotifyAncestor         = 0
  lazy val NotifyVirtual          = 1
  lazy val NotifyInferior         = 2
  lazy val NotifyNonlinear        = 3
  lazy val NotifyNonlinearVirtual = 4
  lazy val NotifyPointer          = 5
  lazy val NotifyPointerRoot      = 6
  lazy val NotifyDetailNone       = 7

  /* Visibility notify */

  lazy val VisibilityUnobscured        = 0
  lazy val VisibilityPartiallyObscured = 1
  lazy val VisibilityFullyObscured     = 2

  /* Circulation request */

  lazy val PlaceOnTop    = 0
  lazy val PlaceOnBottom = 1

  /* protocol families */

  lazy val FamilyInternet  = 0 /* IPv4 */
  lazy val FamilyDECnet    = 1
  lazy val FamilyChaos     = 2
  lazy val FamilyInternet6 = 6 /* IPv6 */

  /* authentication families not tied to a specific protocol */
  lazy val FamilyServerInterpreted = 5

  /* Property notification */

  lazy val PropertyNewValue = 0
  lazy val PropertyDelete   = 1

  /* Color Map notification */

  lazy val ColormapUninstalled = 0
  lazy val ColormapInstalled   = 1

  /* GrabPointer, GrabButton, GrabKeyboard, GrabKey Modes */

  lazy val GrabModeSync  = 0
  lazy val GrabModeAsync = 1

  /* GrabPointer, GrabKeyboard reply status */

  lazy val GrabSuccess     = 0
  lazy val AlreadyGrabbed  = 1
  lazy val GrabInvalidTime = 2
  lazy val GrabNotViewable = 3
  lazy val GrabFrozen      = 4

  /* AllowEvents modes */

  lazy val AsyncPointer   = 0
  lazy val SyncPointer    = 1
  lazy val ReplayPointer  = 2
  lazy val AsyncKeyboard  = 3
  lazy val SyncKeyboard   = 4
  lazy val ReplayKeyboard = 5
  lazy val AsyncBoth      = 6
  lazy val SyncBoth       = 7

  /* Used in SetInputFocus, GetInputFocus */

  lazy val RevertToNone: Int        = None.toInt
  lazy val RevertToPointerRoot: Int = PointerRoot.toInt
  lazy val RevertToParent           = 2

  /*****************************************************************
    * ERROR CODES
   *****************************************************************/
  lazy val Success           = 0 /* everything's okay */
  lazy val BadRequest        = 1 /* bad request code */
  lazy val BadValue          = 2 /* int parameter out of range */
  lazy val BadWindow         = 3 /* parameter not a Window */
  lazy val BadPixmap         = 4 /* parameter not a Pixmap */
  lazy val BadAtom           = 5 /* parameter not an Atom */
  lazy val BadCursor         = 6 /* parameter not a Cursor */
  lazy val BadFont           = 7 /* parameter not a Font */
  lazy val BadMatch          = 8 /* parameter mismatch */
  lazy val BadDrawable       = 9 /* parameter not a Pixmap or Window */
  lazy val BadAccess         = 10 /* depending on context:
				 - key/button already grabbed
				 - attempt to free an illegal
				   cmap entry
				- attempt to store into a read-only
				   color map entry.
 				- attempt to modify the access control
				   list from other than the local host.
				*/
  lazy val BadAlloc          = 11 /* insufficient resources */
  lazy val BadColor          = 12 /* no such colormap */
  lazy val BadGC             = 13 /* parameter not a GC */
  lazy val BadIDChoice       = 14 /* choice not in range or already used */
  lazy val BadName           = 15 /* font or color name doesn't exist */
  lazy val BadLength         = 16 /* Request length incorrect */
  lazy val BadImplementation = 17 /* server is defective */

  lazy val FirstExtensionError = 128
  lazy val LastExtensionError  = 255

  /*****************************************************************
    * WINDOW DEFINITIONS
   *****************************************************************/
  /* Window classes used by CreateWindow */
  /* Note that CopyFromParent is already defined as 0 above */

  lazy val InputOutput = 1
  lazy val InputOnly   = 2

  /* Window attributes for CreateWindow and ChangeWindowAttributes */

  lazy val CWBackPixmap: Long       = 1L << 0
  lazy val CWBackPixel: Long        = 1L << 1
  lazy val CWBorderPixmap: Long     = 1L << 2
  lazy val CWBorderPixel: Long      = 1L << 3
  lazy val CWBitGravity: Long       = 1L << 4
  lazy val CWWinGravity: Long       = 1L << 5
  lazy val CWBackingStore: Long     = 1L << 6
  lazy val CWBackingPlanes: Long    = 1L << 7
  lazy val CWBackingPixel: Long     = 1L << 8
  lazy val CWOverrideRedirect: Long = 1L << 9
  lazy val CWSaveUnder: Long        = 1L << 10
  lazy val CWEventMask: Long        = 1L << 11
  lazy val CWDontPropagate: Long    = 1L << 12
  lazy val CWColormap: Long         = 1L << 13
  lazy val CWCursor: Long           = 1L << 14

  /* ConfigureWindow structure */

  lazy val CWX: Int           = 1 << 0
  lazy val CWY: Int           = 1 << 1
  lazy val CWWidth: Int       = 1 << 2
  lazy val CWHeight: Int      = 1 << 3
  lazy val CWBorderWidth: Int = 1 << 4
  lazy val CWSibling: Int     = 1 << 5
  lazy val CWStackMode: Int   = 1 << 6

  /* Bit Gravity */

  lazy val ForgetGravity    = 0
  lazy val NorthWestGravity = 1
  lazy val NorthGravity     = 2
  lazy val NorthEastGravity = 3
  lazy val WestGravity      = 4
  lazy val CenterGravity    = 5
  lazy val EastGravity      = 6
  lazy val SouthWestGravity = 7
  lazy val SouthGravity     = 8
  lazy val SouthEastGravity = 9
  lazy val StaticGravity    = 10

  /* Window gravity + bit gravity above */

  lazy val UnmapGravity = 0

  /* Used in CreateWindow for backing-store hint */

  lazy val NotUseful  = 0
  lazy val WhenMapped = 1
  lazy val Always     = 2

  /* Used in GetWindowAttributes reply */

  lazy val IsUnmapped   = 0
  lazy val IsUnviewable = 1
  lazy val IsViewable   = 2

  /* Used in ChangeSaveSet */

  lazy val SetModeInsert = 0
  lazy val SetModeDelete = 1

  /* Used in ChangeCloseDownMode */

  lazy val DestroyAll      = 0
  lazy val RetainPermanent = 1
  lazy val RetainTemporary = 2

  /* Window stacking method (in configureWindow) */

  lazy val Above    = 0
  lazy val Below    = 1
  lazy val TopIf    = 2
  lazy val BottomIf = 3
  lazy val Opposite = 4

  /* Circulation direction */

  lazy val RaiseLowest  = 0
  lazy val LowerHighest = 1

  /* Property modes */

  lazy val PropModeReplace = 0
  lazy val PropModePrepend = 1
  lazy val PropModeAppend  = 2

  /*****************************************************************
    * GRAPHICS DEFINITIONS
   *****************************************************************/
  /* graphics functions, as in GC.alu */

  lazy val GXclear        = 0x0 /* 0 */
  lazy val GXand          = 0x1 /* src AND dst */
  lazy val GXandReverse   = 0x2 /* src AND NOT dst */
  lazy val GXcopy         = 0x3 /* src */
  lazy val GXandInverted  = 0x4 /* NOT src AND dst */
  lazy val GXnoop         = 0x5 /* dst */
  lazy val GXxor          = 0x6 /* src XOR dst */
  lazy val GXor           = 0x7 /* src OR dst */
  lazy val GXnor          = 0x8 /* NOT src AND NOT dst */
  lazy val GXequiv        = 0x9 /* NOT src XOR dst */
  lazy val GXinvert       = 0xa /* NOT dst */
  lazy val GXorReverse    = 0xb /* src OR NOT dst */
  lazy val GXcopyInverted = 0xc /* NOT src */
  lazy val GXorInverted   = 0xd /* NOT src OR dst */
  lazy val GXnand         = 0xe /* NOT src OR NOT dst */
  lazy val GXset          = 0xf /* 1 */

  /* LineStyle */

  lazy val LineSolid      = 0
  lazy val LineOnOffDash  = 1
  lazy val LineDoubleDash = 2

  /* capStyle */

  lazy val CapNotLast    = 0
  lazy val CapButt       = 1
  lazy val CapRound      = 2
  lazy val CapProjecting = 3

  /* joinStyle */

  lazy val JoinMiter = 0
  lazy val JoinRound = 1
  lazy val JoinBevel = 2

  /* fillStyle */

  lazy val FillSolid          = 0
  lazy val FillTiled          = 1
  lazy val FillStippled       = 2
  lazy val FillOpaqueStippled = 3

  /* fillRule */

  lazy val EvenOddRule = 0
  lazy val WindingRule = 1

  /* subwindow mode */

  lazy val ClipByChildren   = 0
  lazy val IncludeInferiors = 1

  /* SetClipRectangles ordering */

  lazy val Unsorted = 0
  lazy val YSorted  = 1
  lazy val YXSorted = 2
  lazy val YXBanded = 3

  /* CoordinateMode for drawing routines */

  lazy val CoordModeOrigin   = 0 /* relative to the origin */
  lazy val CoordModePrevious = 1 /* relative to previous point */

  /* Polygon shapes */

  lazy val Complex   = 0 /* paths may intersect */
  lazy val Nonconvex = 1 /* no paths intersect, but not convex */
  lazy val Convex    = 2 /* wholly convex */

  /* Arc modes for PolyFillArc */

  lazy val ArcChord    = 0 /* join endpoints of arc */
  lazy val ArcPieSlice = 1 /* join endpoints to center of arc */

  /* GC components: masks used in CreateGC, CopyGC, ChangeGC, OR'ed into
     GC.stateChanges */

  lazy val GCFunction: Long          = 1L << 0
  lazy val GCPlaneMask: Long         = 1L << 1
  lazy val GCForeground: Long        = 1L << 2
  lazy val GCBackground: Long        = 1L << 3
  lazy val GCLineWidth: Long         = 1L << 4
  lazy val GCLineStyle: Long         = 1L << 5
  lazy val GCCapStyle: Long          = 1L << 6
  lazy val GCJoinStyle: Long         = 1L << 7
  lazy val GCFillStyle: Long         = 1L << 8
  lazy val GCFillRule: Long          = 1L << 9
  lazy val GCTile: Long              = 1L << 10
  lazy val GCStipple: Long           = 1L << 11
  lazy val GCTileStipXOrigin: Long   = 1L << 12
  lazy val GCTileStipYOrigin: Long   = 1L << 13
  lazy val GCFont: Long              = 1L << 14
  lazy val GCSubwindowMode: Long     = 1L << 15
  lazy val GCGraphicsExposures: Long = 1L << 16
  lazy val GCClipXOrigin: Long       = 1L << 17
  lazy val GCClipYOrigin: Long       = 1L << 18
  lazy val GCClipMask: Long          = 1L << 19
  lazy val GCDashOffset: Long        = 1L << 20
  lazy val GCDashList: Long          = 1L << 21
  lazy val GCArcMode: Long           = 1L << 22

  lazy val GCLastBit = 22

  /*****************************************************************
    * FONTS
   *****************************************************************/
  /* used in QueryFont -- draw direction */

  lazy val FontLeftToRight = 0
  lazy val FontRightToLeft = 1

  lazy val FontChange = 255

  /*****************************************************************
    *  IMAGING
   *****************************************************************/
  /* ImageFormat -- PutImage, GetImage */

  lazy val XYBitmap = 0 /* depth 1, XYFormat */
  lazy val XYPixmap = 1 /* depth == drawable depth */
  lazy val ZPixmap  = 2 /* depth == drawable depth */

  /*****************************************************************
    *  COLOR MAP STUFF
   *****************************************************************/
  /* For CreateColormap */

  lazy val AllocNone = 0 /* create map with no entries */
  lazy val AllocAll  = 1 /* allocate entire map writeable */

  /* Flags used in StoreNamedColor, StoreColors */

  lazy val DoRed: Int   = 1 << 0
  lazy val DoGreen: Int = 1 << 1
  lazy val DoBlue: Int  = 1 << 2

  /*****************************************************************
    * CURSOR STUFF
   *****************************************************************/
  /* QueryBestSize Class */

  lazy val CursorShape  = 0 /* largest size that can be displayed */
  lazy val TileShape    = 1 /* size tiled fastest */
  lazy val StippleShape = 2 /* size stippled fastest */

  /*****************************************************************
    * KEYBOARD/POINTER STUFF
   *****************************************************************/
  lazy val AutoRepeatModeOff     = 0
  lazy val AutoRepeatModeOn      = 1
  lazy val AutoRepeatModeDefault = 2

  lazy val LedModeOff = 0
  lazy val LedModeOn  = 1

  /* masks for ChangeKeyboardControl */

  lazy val KBKeyClickPercent: Long = 1L << 0
  lazy val KBBellPercent: Long     = 1L << 1
  lazy val KBBellPitch: Long       = 1L << 2
  lazy val KBBellDuration: Long    = 1L << 3
  lazy val KBLed: Long             = 1L << 4
  lazy val KBLedMode: Long         = 1L << 5
  lazy val KBKey: Long             = 1L << 6
  lazy val KBAutoRepeatMode: Long  = 1L << 7

  lazy val MappingSuccess = 0
  lazy val MappingBusy    = 1
  lazy val MappingFailed  = 2

  lazy val MappingModifier = 0
  lazy val MappingKeyboard = 1
  lazy val MappingPointer  = 2

  /*****************************************************************
    * SCREEN SAVER STUFF
   *****************************************************************/
  lazy val DontPreferBlanking = 0
  lazy val PreferBlanking     = 1
  lazy val DefaultBlanking    = 2

  lazy val DisableScreenSaver    = 0
  lazy val DisableScreenInterval = 0

  lazy val DontAllowExposures = 0
  lazy val AllowExposures     = 1
  lazy val DefaultExposures   = 2

  /* for ForceScreenSaver */

  lazy val ScreenSaverReset  = 0
  lazy val ScreenSaverActive = 1

  /*****************************************************************
    * HOSTS AND CONNECTIONS
   *****************************************************************/
  /* for ChangeHosts */

  lazy val HostInsert = 0
  lazy val HostDelete = 1

  /* for ChangeAccessControl */

  lazy val EnableAccess  = 1
  lazy val DisableAccess = 0

  /* Display classes  used in opening the connection
   * Note that the statically allocated ones are even numbered and the
   * dynamically changeable ones are odd numbered */

  lazy val StaticGray  = 0
  lazy val GrayScale   = 1
  lazy val StaticColor = 2
  lazy val PseudoColor = 3
  lazy val TrueColor   = 4
  lazy val DirectColor = 5

  /* Byte order  used in imageByteOrder and bitmapBitOrder */

  lazy val LSBFirst = 0
  lazy val MSBFirst = 1

  // X11/keysymdef.h

  /***********************************************************
Copyright 1987, 1994, 1998  The Open Group

Permission to use, copy, modify, distribute, and sell this software and its
documentation for any purpose is hereby granted without fee, provided that
the above copyright notice appear in all copies and that both that
copyright notice and this permission notice appear in supporting
documentation.

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE OPEN GROUP BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

Except as contained in this notice, the name of The Open Group shall
not be used in advertising or otherwise to promote the sale, use or
other dealings in this Software without prior written authorization
from The Open Group.


Copyright 1987 by Digital Equipment Corporation, Maynard, Massachusetts

                        All Rights Reserved

Permission to use, copy, modify, and distribute this software and its
documentation for any purpose and without fee is hereby granted,
provided that the above copyright notice appear in all copies and that
both that copyright notice and this permission notice appear in
supporting documentation, and that the name of Digital not be
used in advertising or publicity pertaining to distribution of the
software without specific, written prior permission.

DIGITAL DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING
ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO EVENT SHALL
DIGITAL BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR
ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,
ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS
SOFTWARE.

   ******************************************************************/
  lazy val XK_VoidSymbol: Long = 0xffffff /* Void symbol */

  /*
   * TTY function keys, cleverly chosen to map to ASCII, for convenience of
   * programming, but could have been arbitrary (at the cost of lookup
   * tables in client code).
   */

  lazy val XK_BackSpace: Long   = 0xff08 /* Back space, back char */
  lazy val XK_Tab: Long         = 0xff09
  lazy val XK_Linefeed: Long    = 0xff0a /* Linefeed, LF */
  lazy val XK_Clear: Long       = 0xff0b
  lazy val XK_Return: Long      = 0xff0d /* Return, enter */
  lazy val XK_Pause: Long       = 0xff13 /* Pause, hold */
  lazy val XK_Scroll_Lock: Long = 0xff14
  lazy val XK_Sys_Req: Long     = 0xff15
  lazy val XK_Escape: Long      = 0xff1b
  lazy val XK_Delete: Long      = 0xffff /* Delete, rubout */

  /* International & multi-key character composition */

  lazy val XK_Multi_key: Long         = 0xff20 /* Multi-key character compose */
  lazy val XK_Codeinput: Long         = 0xff37
  lazy val XK_SingleCandidate: Long   = 0xff3c
  lazy val XK_MultipleCandidate: Long = 0xff3d
  lazy val XK_PreviousCandidate: Long = 0xff3e

  /* Japanese keyboard support */

  lazy val XK_Kanji: Long             = 0xff21 /* Kanji, Kanji convert */
  lazy val XK_Muhenkan: Long          = 0xff22 /* Cancel Conversion */
  lazy val XK_Henkan_Mode: Long       = 0xff23 /* Start/Stop Conversion */
  lazy val XK_Henkan: Long            = 0xff23 /* Alias for Henkan_Mode */
  lazy val XK_Romaji: Long            = 0xff24 /* to Romaji */
  lazy val XK_Hiragana: Long          = 0xff25 /* to Hiragana */
  lazy val XK_Katakana: Long          = 0xff26 /* to Katakana */
  lazy val XK_Hiragana_Katakana: Long = 0xff27 /* Hiragana/Katakana toggle */
  lazy val XK_Zenkaku: Long           = 0xff28 /* to Zenkaku */
  lazy val XK_Hankaku: Long           = 0xff29 /* to Hankaku */
  lazy val XK_Zenkaku_Hankaku: Long   = 0xff2a /* Zenkaku/Hankaku toggle */
  lazy val XK_Touroku: Long           = 0xff2b /* Add to Dictionary */
  lazy val XK_Massyo: Long            = 0xff2c /* Delete from Dictionary */
  lazy val XK_Kana_Lock: Long         = 0xff2d /* Kana Lock */
  lazy val XK_Kana_Shift: Long        = 0xff2e /* Kana Shift */
  lazy val XK_Eisu_Shift: Long        = 0xff2f /* Alphanumeric Shift */
  lazy val XK_Eisu_toggle: Long       = 0xff30 /* Alphanumeric toggle */
  lazy val XK_Kanji_Bangou: Long      = 0xff37 /* Codeinput */
  lazy val XK_Zen_Koho: Long          = 0xff3d /* Multiple/All Candidate(s) */
  lazy val XK_Mae_Koho: Long          = 0xff3e /* Previous Candidate */

  /* 0xff31 thru 0xff3f are under XK_KOREAN */

  /* Cursor control & motion */

  lazy val XK_Home: Long      = 0xff50
  lazy val XK_Left: Long      = 0xff51 /* Move left, left arrow */
  lazy val XK_Up: Long        = 0xff52 /* Move up, up arrow */
  lazy val XK_Right: Long     = 0xff53 /* Move right, right arrow */
  lazy val XK_Down: Long      = 0xff54 /* Move down, down arrow */
  lazy val XK_Prior: Long     = 0xff55 /* Prior, previous */
  lazy val XK_Page_Up: Long   = 0xff55
  lazy val XK_Next: Long      = 0xff56 /* Next */
  lazy val XK_Page_Down: Long = 0xff56
  lazy val XK_End: Long       = 0xff57 /* EOL */
  lazy val XK_Begin: Long     = 0xff58 /* BOL */

  /* Misc functions */

  lazy val XK_Select: Long        = 0xff60 /* Select, mark */
  lazy val XK_Print: Long         = 0xff61
  lazy val XK_Execute: Long       = 0xff62 /* Execute, run, do */
  lazy val XK_Insert: Long        = 0xff63 /* Insert, insert here */
  lazy val XK_Undo: Long          = 0xff65
  lazy val XK_Redo: Long          = 0xff66 /* Redo, again */
  lazy val XK_Menu: Long          = 0xff67
  lazy val XK_Find: Long          = 0xff68 /* Find, search */
  lazy val XK_Cancel: Long        = 0xff69 /* Cancel, stop, abort, exit */
  lazy val XK_Help: Long          = 0xff6a /* Help */
  lazy val XK_Break: Long         = 0xff6b
  lazy val XK_Mode_switch: Long   = 0xff7e /* Character set switch */
  lazy val XK_script_switch: Long = 0xff7e /* Alias for mode_switch */
  lazy val XK_Num_Lock: Long      = 0xff7f

  /* Keypad functions, keypad numbers cleverly chosen to map to ASCII */

  lazy val XK_KP_Space: Long     = 0xff80 /* Space */
  lazy val XK_KP_Tab: Long       = 0xff89
  lazy val XK_KP_Enter: Long     = 0xff8d /* Enter */
  lazy val XK_KP_F1: Long        = 0xff91 /* PF1, KP_A, ... */
  lazy val XK_KP_F2: Long        = 0xff92
  lazy val XK_KP_F3: Long        = 0xff93
  lazy val XK_KP_F4: Long        = 0xff94
  lazy val XK_KP_Home: Long      = 0xff95
  lazy val XK_KP_Left: Long      = 0xff96
  lazy val XK_KP_Up: Long        = 0xff97
  lazy val XK_KP_Right: Long     = 0xff98
  lazy val XK_KP_Down: Long      = 0xff99
  lazy val XK_KP_Prior: Long     = 0xff9a
  lazy val XK_KP_Page_Up: Long   = 0xff9a
  lazy val XK_KP_Next: Long      = 0xff9b
  lazy val XK_KP_Page_Down: Long = 0xff9b
  lazy val XK_KP_End: Long       = 0xff9c
  lazy val XK_KP_Begin: Long     = 0xff9d
  lazy val XK_KP_Insert: Long    = 0xff9e
  lazy val XK_KP_Delete: Long    = 0xff9f
  lazy val XK_KP_Equal: Long     = 0xffbd /* Equals */
  lazy val XK_KP_Multiply: Long  = 0xffaa
  lazy val XK_KP_Add: Long       = 0xffab
  lazy val XK_KP_Separator: Long = 0xffac /* Separator, often comma */
  lazy val XK_KP_Subtract: Long  = 0xffad
  lazy val XK_KP_Decimal: Long   = 0xffae
  lazy val XK_KP_Divide: Long    = 0xffaf

  lazy val XK_KP_0: Long = 0xffb0
  lazy val XK_KP_1: Long = 0xffb1
  lazy val XK_KP_2: Long = 0xffb2
  lazy val XK_KP_3: Long = 0xffb3
  lazy val XK_KP_4: Long = 0xffb4
  lazy val XK_KP_5: Long = 0xffb5
  lazy val XK_KP_6: Long = 0xffb6
  lazy val XK_KP_7: Long = 0xffb7
  lazy val XK_KP_8: Long = 0xffb8
  lazy val XK_KP_9: Long = 0xffb9

  /*
   * Auxiliary functions; note the duplicate definitions for left and right
   * function keys;  Sun keyboards and a few other manufacturers have such
   * function key groups on the left and/or right sides of the keyboard.
   * We've not found a keyboard with more than 35 function keys total.
   */

  lazy val XK_F1: Long  = 0xffbe
  lazy val XK_F2: Long  = 0xffbf
  lazy val XK_F3: Long  = 0xffc0
  lazy val XK_F4: Long  = 0xffc1
  lazy val XK_F5: Long  = 0xffc2
  lazy val XK_F6: Long  = 0xffc3
  lazy val XK_F7: Long  = 0xffc4
  lazy val XK_F8: Long  = 0xffc5
  lazy val XK_F9: Long  = 0xffc6
  lazy val XK_F10: Long = 0xffc7
  lazy val XK_F11: Long = 0xffc8
  lazy val XK_L1: Long  = 0xffc8
  lazy val XK_F12: Long = 0xffc9
  lazy val XK_L2: Long  = 0xffc9
  lazy val XK_F13: Long = 0xffca
  lazy val XK_L3: Long  = 0xffca
  lazy val XK_F14: Long = 0xffcb
  lazy val XK_L4: Long  = 0xffcb
  lazy val XK_F15: Long = 0xffcc
  lazy val XK_L5: Long  = 0xffcc
  lazy val XK_F16: Long = 0xffcd
  lazy val XK_L6: Long  = 0xffcd
  lazy val XK_F17: Long = 0xffce
  lazy val XK_L7: Long  = 0xffce
  lazy val XK_F18: Long = 0xffcf
  lazy val XK_L8: Long  = 0xffcf
  lazy val XK_F19: Long = 0xffd0
  lazy val XK_L9: Long  = 0xffd0
  lazy val XK_F20: Long = 0xffd1
  lazy val XK_L10: Long = 0xffd1
  lazy val XK_F21: Long = 0xffd2
  lazy val XK_R1: Long  = 0xffd2
  lazy val XK_F22: Long = 0xffd3
  lazy val XK_R2: Long  = 0xffd3
  lazy val XK_F23: Long = 0xffd4
  lazy val XK_R3: Long  = 0xffd4
  lazy val XK_F24: Long = 0xffd5
  lazy val XK_R4: Long  = 0xffd5
  lazy val XK_F25: Long = 0xffd6
  lazy val XK_R5: Long  = 0xffd6
  lazy val XK_F26: Long = 0xffd7
  lazy val XK_R6: Long  = 0xffd7
  lazy val XK_F27: Long = 0xffd8
  lazy val XK_R7: Long  = 0xffd8
  lazy val XK_F28: Long = 0xffd9
  lazy val XK_R8: Long  = 0xffd9
  lazy val XK_F29: Long = 0xffda
  lazy val XK_R9: Long  = 0xffda
  lazy val XK_F30: Long = 0xffdb
  lazy val XK_R10: Long = 0xffdb
  lazy val XK_F31: Long = 0xffdc
  lazy val XK_R11: Long = 0xffdc
  lazy val XK_F32: Long = 0xffdd
  lazy val XK_R12: Long = 0xffdd
  lazy val XK_F33: Long = 0xffde
  lazy val XK_R13: Long = 0xffde
  lazy val XK_F34: Long = 0xffdf
  lazy val XK_R14: Long = 0xffdf
  lazy val XK_F35: Long = 0xffe0
  lazy val XK_R15: Long = 0xffe0

  /* Modifiers */

  lazy val XK_Shift_L: Long    = 0xffe1 /* Left shift */
  lazy val XK_Shift_R: Long    = 0xffe2 /* Right shift */
  lazy val XK_Control_L: Long  = 0xffe3 /* Left control */
  lazy val XK_Control_R: Long  = 0xffe4 /* Right control */
  lazy val XK_Caps_Lock: Long  = 0xffe5 /* Caps lock */
  lazy val XK_Shift_Lock: Long = 0xffe6 /* Shift lock */

  lazy val XK_Meta_L: Long  = 0xffe7 /* Left meta */
  lazy val XK_Meta_R: Long  = 0xffe8 /* Right meta */
  lazy val XK_Alt_L: Long   = 0xffe9 /* Left alt */
  lazy val XK_Alt_R: Long   = 0xffea /* Right alt */
  lazy val XK_Super_L: Long = 0xffeb /* Left super */
  lazy val XK_Super_R: Long = 0xffec /* Right super */
  lazy val XK_Hyper_L: Long = 0xffed /* Left hyper */
  lazy val XK_Hyper_R: Long = 0xffee /* Right hyper */

  /*
   * Keyboard (XKB) Extension function and modifier keys
   * (from Appendix C of "The X Keyboard Extension: Protocol Specification")
   * Byte 3 = 0xfe
   */

  lazy val XK_ISO_Lock: Long             = 0xfe01
  lazy val XK_ISO_Level2_Latch: Long     = 0xfe02
  lazy val XK_ISO_Level3_Shift: Long     = 0xfe03
  lazy val XK_ISO_Level3_Latch: Long     = 0xfe04
  lazy val XK_ISO_Level3_Lock: Long      = 0xfe05
  lazy val XK_ISO_Level5_Shift: Long     = 0xfe11
  lazy val XK_ISO_Level5_Latch: Long     = 0xfe12
  lazy val XK_ISO_Level5_Lock: Long      = 0xfe13
  lazy val XK_ISO_Group_Shift: Long      = 0xff7e /* Alias for mode_switch */
  lazy val XK_ISO_Group_Latch: Long      = 0xfe06
  lazy val XK_ISO_Group_Lock: Long       = 0xfe07
  lazy val XK_ISO_Next_Group: Long       = 0xfe08
  lazy val XK_ISO_Next_Group_Lock: Long  = 0xfe09
  lazy val XK_ISO_Prev_Group: Long       = 0xfe0a
  lazy val XK_ISO_Prev_Group_Lock: Long  = 0xfe0b
  lazy val XK_ISO_First_Group: Long      = 0xfe0c
  lazy val XK_ISO_First_Group_Lock: Long = 0xfe0d
  lazy val XK_ISO_Last_Group: Long       = 0xfe0e
  lazy val XK_ISO_Last_Group_Lock: Long  = 0xfe0f

  lazy val XK_ISO_Left_Tab: Long                = 0xfe20
  lazy val XK_ISO_Move_Line_Up: Long            = 0xfe21
  lazy val XK_ISO_Move_Line_Down: Long          = 0xfe22
  lazy val XK_ISO_Partial_Line_Up: Long         = 0xfe23
  lazy val XK_ISO_Partial_Line_Down: Long       = 0xfe24
  lazy val XK_ISO_Partial_Space_Left: Long      = 0xfe25
  lazy val XK_ISO_Partial_Space_Right: Long     = 0xfe26
  lazy val XK_ISO_Set_Margin_Left: Long         = 0xfe27
  lazy val XK_ISO_Set_Margin_Right: Long        = 0xfe28
  lazy val XK_ISO_Release_Margin_Left: Long     = 0xfe29
  lazy val XK_ISO_Release_Margin_Right: Long    = 0xfe2a
  lazy val XK_ISO_Release_Both_Margins: Long    = 0xfe2b
  lazy val XK_ISO_Fast_Cursor_Left: Long        = 0xfe2c
  lazy val XK_ISO_Fast_Cursor_Right: Long       = 0xfe2d
  lazy val XK_ISO_Fast_Cursor_Up: Long          = 0xfe2e
  lazy val XK_ISO_Fast_Cursor_Down: Long        = 0xfe2f
  lazy val XK_ISO_Continuous_Underline: Long    = 0xfe30
  lazy val XK_ISO_Discontinuous_Underline: Long = 0xfe31
  lazy val XK_ISO_Emphasize: Long               = 0xfe32
  lazy val XK_ISO_Center_Object: Long           = 0xfe33
  lazy val XK_ISO_Enter: Long                   = 0xfe34

  lazy val XK_dead_grave: Long              = 0xfe50
  lazy val XK_dead_acute: Long              = 0xfe51
  lazy val XK_dead_circumflex: Long         = 0xfe52
  lazy val XK_dead_tilde: Long              = 0xfe53
  lazy val XK_dead_perispomeni: Long        = 0xfe53 /* alias for dead_tilde */
  lazy val XK_dead_macron: Long             = 0xfe54
  lazy val XK_dead_breve: Long              = 0xfe55
  lazy val XK_dead_abovedot: Long           = 0xfe56
  lazy val XK_dead_diaeresis: Long          = 0xfe57
  lazy val XK_dead_abovering: Long          = 0xfe58
  lazy val XK_dead_doubleacute: Long        = 0xfe59
  lazy val XK_dead_caron: Long              = 0xfe5a
  lazy val XK_dead_cedilla: Long            = 0xfe5b
  lazy val XK_dead_ogonek: Long             = 0xfe5c
  lazy val XK_dead_iota: Long               = 0xfe5d
  lazy val XK_dead_voiced_sound: Long       = 0xfe5e
  lazy val XK_dead_semivoiced_sound: Long   = 0xfe5f
  lazy val XK_dead_belowdot: Long           = 0xfe60
  lazy val XK_dead_hook: Long               = 0xfe61
  lazy val XK_dead_horn: Long               = 0xfe62
  lazy val XK_dead_stroke: Long             = 0xfe63
  lazy val XK_dead_abovecomma: Long         = 0xfe64
  lazy val XK_dead_psili: Long              = 0xfe64 /* alias for dead_abovecomma */
  lazy val XK_dead_abovereversedcomma: Long = 0xfe65
  lazy val XK_dead_dasia: Long              = 0xfe65 /* alias for dead_abovereversedcomma */
  lazy val XK_dead_doublegrave: Long        = 0xfe66
  lazy val XK_dead_belowring: Long          = 0xfe67
  lazy val XK_dead_belowmacron: Long        = 0xfe68
  lazy val XK_dead_belowcircumflex: Long    = 0xfe69
  lazy val XK_dead_belowtilde: Long         = 0xfe6a
  lazy val XK_dead_belowbreve: Long         = 0xfe6b
  lazy val XK_dead_belowdiaeresis: Long     = 0xfe6c
  lazy val XK_dead_invertedbreve: Long      = 0xfe6d
  lazy val XK_dead_belowcomma: Long         = 0xfe6e
  lazy val XK_dead_currency: Long           = 0xfe6f

  /* extra dead elements for German T3 layout */
  lazy val XK_dead_lowline: Long            = 0xfe90
  lazy val XK_dead_aboveverticalline: Long  = 0xfe91
  lazy val XK_dead_belowverticalline: Long  = 0xfe92
  lazy val XK_dead_longsolidusoverlay: Long = 0xfe93

  /* dead vowels for universal syllable entry */
  lazy val XK_dead_a: Long             = 0xfe80
  lazy val XK_dead_A: Long             = 0xfe81
  lazy val XK_dead_e: Long             = 0xfe82
  lazy val XK_dead_E: Long             = 0xfe83
  lazy val XK_dead_i: Long             = 0xfe84
  lazy val XK_dead_I: Long             = 0xfe85
  lazy val XK_dead_o: Long             = 0xfe86
  lazy val XK_dead_O: Long             = 0xfe87
  lazy val XK_dead_u: Long             = 0xfe88
  lazy val XK_dead_U: Long             = 0xfe89
  lazy val XK_dead_small_schwa: Long   = 0xfe8a
  lazy val XK_dead_capital_schwa: Long = 0xfe8b

  lazy val XK_dead_greek: Long = 0xfe8c

  lazy val XK_First_Virtual_Screen: Long = 0xfed0
  lazy val XK_Prev_Virtual_Screen: Long  = 0xfed1
  lazy val XK_Next_Virtual_Screen: Long  = 0xfed2
  lazy val XK_Last_Virtual_Screen: Long  = 0xfed4
  lazy val XK_Terminate_Server: Long     = 0xfed5

  lazy val XK_AccessX_Enable: Long          = 0xfe70
  lazy val XK_AccessX_Feedback_Enable: Long = 0xfe71
  lazy val XK_RepeatKeys_Enable: Long       = 0xfe72
  lazy val XK_SlowKeys_Enable: Long         = 0xfe73
  lazy val XK_BounceKeys_Enable: Long       = 0xfe74
  lazy val XK_StickyKeys_Enable: Long       = 0xfe75
  lazy val XK_MouseKeys_Enable: Long        = 0xfe76
  lazy val XK_MouseKeys_Accel_Enable: Long  = 0xfe77
  lazy val XK_Overlay1_Enable: Long         = 0xfe78
  lazy val XK_Overlay2_Enable: Long         = 0xfe79
  lazy val XK_AudibleBell_Enable: Long      = 0xfe7a

  lazy val XK_Pointer_Left: Long          = 0xfee0
  lazy val XK_Pointer_Right: Long         = 0xfee1
  lazy val XK_Pointer_Up: Long            = 0xfee2
  lazy val XK_Pointer_Down: Long          = 0xfee3
  lazy val XK_Pointer_UpLeft: Long        = 0xfee4
  lazy val XK_Pointer_UpRight: Long       = 0xfee5
  lazy val XK_Pointer_DownLeft: Long      = 0xfee6
  lazy val XK_Pointer_DownRight: Long     = 0xfee7
  lazy val XK_Pointer_Button_Dflt: Long   = 0xfee8
  lazy val XK_Pointer_Button1: Long       = 0xfee9
  lazy val XK_Pointer_Button2: Long       = 0xfeea
  lazy val XK_Pointer_Button3: Long       = 0xfeeb
  lazy val XK_Pointer_Button4: Long       = 0xfeec
  lazy val XK_Pointer_Button5: Long       = 0xfeed
  lazy val XK_Pointer_DblClick_Dflt: Long = 0xfeee
  lazy val XK_Pointer_DblClick1: Long     = 0xfeef
  lazy val XK_Pointer_DblClick2: Long     = 0xfef0
  lazy val XK_Pointer_DblClick3: Long     = 0xfef1
  lazy val XK_Pointer_DblClick4: Long     = 0xfef2
  lazy val XK_Pointer_DblClick5: Long     = 0xfef3
  lazy val XK_Pointer_Drag_Dflt: Long     = 0xfef4
  lazy val XK_Pointer_Drag1: Long         = 0xfef5
  lazy val XK_Pointer_Drag2: Long         = 0xfef6
  lazy val XK_Pointer_Drag3: Long         = 0xfef7
  lazy val XK_Pointer_Drag4: Long         = 0xfef8
  lazy val XK_Pointer_Drag5: Long         = 0xfefd

  lazy val XK_Pointer_EnableKeys: Long  = 0xfef9
  lazy val XK_Pointer_Accelerate: Long  = 0xfefa
  lazy val XK_Pointer_DfltBtnNext: Long = 0xfefb
  lazy val XK_Pointer_DfltBtnPrev: Long = 0xfefc

  /* Single-Stroke Multiple-Character N-Graph Keysyms For The X Input Method */

  lazy val XK_ch: Long  = 0xfea0
  lazy val XK_Ch: Long  = 0xfea1
  lazy val XK_CH: Long  = 0xfea2
  lazy val XK_c_h: Long = 0xfea3
  lazy val XK_C_h: Long = 0xfea4
  lazy val XK_C_H: Long = 0xfea5

  /*
   * 3270 Terminal Keys
   * Byte 3 = 0xfd
   */

  lazy val XK_3270_Duplicate: Long    = 0xfd01
  lazy val XK_3270_FieldMark: Long    = 0xfd02
  lazy val XK_3270_Right2: Long       = 0xfd03
  lazy val XK_3270_Left2: Long        = 0xfd04
  lazy val XK_3270_BackTab: Long      = 0xfd05
  lazy val XK_3270_EraseEOF: Long     = 0xfd06
  lazy val XK_3270_EraseInput: Long   = 0xfd07
  lazy val XK_3270_Reset: Long        = 0xfd08
  lazy val XK_3270_Quit: Long         = 0xfd09
  lazy val XK_3270_PA1: Long          = 0xfd0a
  lazy val XK_3270_PA2: Long          = 0xfd0b
  lazy val XK_3270_PA3: Long          = 0xfd0c
  lazy val XK_3270_Test: Long         = 0xfd0d
  lazy val XK_3270_Attn: Long         = 0xfd0e
  lazy val XK_3270_CursorBlink: Long  = 0xfd0f
  lazy val XK_3270_AltCursor: Long    = 0xfd10
  lazy val XK_3270_KeyClick: Long     = 0xfd11
  lazy val XK_3270_Jump: Long         = 0xfd12
  lazy val XK_3270_Ident: Long        = 0xfd13
  lazy val XK_3270_Rule: Long         = 0xfd14
  lazy val XK_3270_Copy: Long         = 0xfd15
  lazy val XK_3270_Play: Long         = 0xfd16
  lazy val XK_3270_Setup: Long        = 0xfd17
  lazy val XK_3270_Record: Long       = 0xfd18
  lazy val XK_3270_ChangeScreen: Long = 0xfd19
  lazy val XK_3270_DeleteWord: Long   = 0xfd1a
  lazy val XK_3270_ExSelect: Long     = 0xfd1b
  lazy val XK_3270_CursorSelect: Long = 0xfd1c
  lazy val XK_3270_PrintScreen: Long  = 0xfd1d
  lazy val XK_3270_Enter: Long        = 0xfd1e

  /*
   * Latin 1
   * (ISO/IEC 8859-1 = Unicode U+0020..U+00FF)
   * Byte 3 = 0
   */

  lazy val XK_space: Long        = 0x0020 /* U+0020 SPACE */
  lazy val XK_exclam: Long       = 0x0021 /* U+0021 EXCLAMATION MARK */
  lazy val XK_quotedbl: Long     = 0x0022 /* U+0022 QUOTATION MARK */
  lazy val XK_numbersign: Long   = 0x0023 /* U+0023 NUMBER SIGN */
  lazy val XK_dollar: Long       = 0x0024 /* U+0024 DOLLAR SIGN */
  lazy val XK_percent: Long      = 0x0025 /* U+0025 PERCENT SIGN */
  lazy val XK_ampersand: Long    = 0x0026 /* U+0026 AMPERSAND */
  lazy val XK_apostrophe: Long   = 0x0027 /* U+0027 APOSTROPHE */
  lazy val XK_quoteright: Long   = 0x0027 /* deprecated */
  lazy val XK_parenleft: Long    = 0x0028 /* U+0028 LEFT PARENTHESIS */
  lazy val XK_parenright: Long   = 0x0029 /* U+0029 RIGHT PARENTHESIS */
  lazy val XK_asterisk: Long     = 0x002a /* U+002A ASTERISK */
  lazy val XK_plus: Long         = 0x002b /* U+002B PLUS SIGN */
  lazy val XK_comma: Long        = 0x002c /* U+002C COMMA */
  lazy val XK_minus: Long        = 0x002d /* U+002D HYPHEN-MINUS */
  lazy val XK_period: Long       = 0x002e /* U+002E FULL STOP */
  lazy val XK_slash: Long        = 0x002f /* U+002F SOLIDUS */
  lazy val XK_0: Long            = 0x0030 /* U+0030 DIGIT ZERO */
  lazy val XK_1: Long            = 0x0031 /* U+0031 DIGIT ONE */
  lazy val XK_2: Long            = 0x0032 /* U+0032 DIGIT TWO */
  lazy val XK_3: Long            = 0x0033 /* U+0033 DIGIT THREE */
  lazy val XK_4: Long            = 0x0034 /* U+0034 DIGIT FOUR */
  lazy val XK_5: Long            = 0x0035 /* U+0035 DIGIT FIVE */
  lazy val XK_6: Long            = 0x0036 /* U+0036 DIGIT SIX */
  lazy val XK_7: Long            = 0x0037 /* U+0037 DIGIT SEVEN */
  lazy val XK_8: Long            = 0x0038 /* U+0038 DIGIT EIGHT */
  lazy val XK_9: Long            = 0x0039 /* U+0039 DIGIT NINE */
  lazy val XK_colon: Long        = 0x003a /* U+003A COLON */
  lazy val XK_semicolon: Long    = 0x003b /* U+003B SEMICOLON */
  lazy val XK_less: Long         = 0x003c /* U+003C LESS-THAN SIGN */
  lazy val XK_equal: Long        = 0x003d /* U+003D EQUALS SIGN */
  lazy val XK_greater: Long      = 0x003e /* U+003E GREATER-THAN SIGN */
  lazy val XK_question: Long     = 0x003f /* U+003F QUESTION MARK */
  lazy val XK_at: Long           = 0x0040 /* U+0040 COMMERCIAL AT */
  lazy val XK_A: Long            = 0x0041 /* U+0041 LATIN CAPITAL LETTER A */
  lazy val XK_B: Long            = 0x0042 /* U+0042 LATIN CAPITAL LETTER B */
  lazy val XK_C: Long            = 0x0043 /* U+0043 LATIN CAPITAL LETTER C */
  lazy val XK_D: Long            = 0x0044 /* U+0044 LATIN CAPITAL LETTER D */
  lazy val XK_E: Long            = 0x0045 /* U+0045 LATIN CAPITAL LETTER E */
  lazy val XK_F: Long            = 0x0046 /* U+0046 LATIN CAPITAL LETTER F */
  lazy val XK_G: Long            = 0x0047 /* U+0047 LATIN CAPITAL LETTER G */
  lazy val XK_H: Long            = 0x0048 /* U+0048 LATIN CAPITAL LETTER H */
  lazy val XK_I: Long            = 0x0049 /* U+0049 LATIN CAPITAL LETTER I */
  lazy val XK_J: Long            = 0x004a /* U+004A LATIN CAPITAL LETTER J */
  lazy val XK_K: Long            = 0x004b /* U+004B LATIN CAPITAL LETTER K */
  lazy val XK_L: Long            = 0x004c /* U+004C LATIN CAPITAL LETTER L */
  lazy val XK_M: Long            = 0x004d /* U+004D LATIN CAPITAL LETTER M */
  lazy val XK_N: Long            = 0x004e /* U+004E LATIN CAPITAL LETTER N */
  lazy val XK_O: Long            = 0x004f /* U+004F LATIN CAPITAL LETTER O */
  lazy val XK_P: Long            = 0x0050 /* U+0050 LATIN CAPITAL LETTER P */
  lazy val XK_Q: Long            = 0x0051 /* U+0051 LATIN CAPITAL LETTER Q */
  lazy val XK_R: Long            = 0x0052 /* U+0052 LATIN CAPITAL LETTER R */
  lazy val XK_S: Long            = 0x0053 /* U+0053 LATIN CAPITAL LETTER S */
  lazy val XK_T: Long            = 0x0054 /* U+0054 LATIN CAPITAL LETTER T */
  lazy val XK_U: Long            = 0x0055 /* U+0055 LATIN CAPITAL LETTER U */
  lazy val XK_V: Long            = 0x0056 /* U+0056 LATIN CAPITAL LETTER V */
  lazy val XK_W: Long            = 0x0057 /* U+0057 LATIN CAPITAL LETTER W */
  lazy val XK_X: Long            = 0x0058 /* U+0058 LATIN CAPITAL LETTER X */
  lazy val XK_Y: Long            = 0x0059 /* U+0059 LATIN CAPITAL LETTER Y */
  lazy val XK_Z: Long            = 0x005a /* U+005A LATIN CAPITAL LETTER Z */
  lazy val XK_bracketleft: Long  = 0x005b /* U+005B LEFT SQUARE BRACKET */
  lazy val XK_backslash: Long    = 0x005c /* U+005C REVERSE SOLIDUS */
  lazy val XK_bracketright: Long = 0x005d /* U+005D RIGHT SQUARE BRACKET */
  lazy val XK_asciicircum: Long  = 0x005e /* U+005E CIRCUMFLEX ACCENT */
  lazy val XK_underscore: Long   = 0x005f /* U+005F LOW LINE */
  lazy val XK_grave: Long        = 0x0060 /* U+0060 GRAVE ACCENT */
  lazy val XK_quoteleft: Long    = 0x0060 /* deprecated */
  lazy val XK_a: Long            = 0x0061 /* U+0061 LATIN SMALL LETTER A */
  lazy val XK_b: Long            = 0x0062 /* U+0062 LATIN SMALL LETTER B */
  lazy val XK_c: Long            = 0x0063 /* U+0063 LATIN SMALL LETTER C */
  lazy val XK_d: Long            = 0x0064 /* U+0064 LATIN SMALL LETTER D */
  lazy val XK_e: Long            = 0x0065 /* U+0065 LATIN SMALL LETTER E */
  lazy val XK_f: Long            = 0x0066 /* U+0066 LATIN SMALL LETTER F */
  lazy val XK_g: Long            = 0x0067 /* U+0067 LATIN SMALL LETTER G */
  lazy val XK_h: Long            = 0x0068 /* U+0068 LATIN SMALL LETTER H */
  lazy val XK_i: Long            = 0x0069 /* U+0069 LATIN SMALL LETTER I */
  lazy val XK_j: Long            = 0x006a /* U+006A LATIN SMALL LETTER J */
  lazy val XK_k: Long            = 0x006b /* U+006B LATIN SMALL LETTER K */
  lazy val XK_l: Long            = 0x006c /* U+006C LATIN SMALL LETTER L */
  lazy val XK_m: Long            = 0x006d /* U+006D LATIN SMALL LETTER M */
  lazy val XK_n: Long            = 0x006e /* U+006E LATIN SMALL LETTER N */
  lazy val XK_o: Long            = 0x006f /* U+006F LATIN SMALL LETTER O */
  lazy val XK_p: Long            = 0x0070 /* U+0070 LATIN SMALL LETTER P */
  lazy val XK_q: Long            = 0x0071 /* U+0071 LATIN SMALL LETTER Q */
  lazy val XK_r: Long            = 0x0072 /* U+0072 LATIN SMALL LETTER R */
  lazy val XK_s: Long            = 0x0073 /* U+0073 LATIN SMALL LETTER S */
  lazy val XK_t: Long            = 0x0074 /* U+0074 LATIN SMALL LETTER T */
  lazy val XK_u: Long            = 0x0075 /* U+0075 LATIN SMALL LETTER U */
  lazy val XK_v: Long            = 0x0076 /* U+0076 LATIN SMALL LETTER V */
  lazy val XK_w: Long            = 0x0077 /* U+0077 LATIN SMALL LETTER W */
  lazy val XK_x: Long            = 0x0078 /* U+0078 LATIN SMALL LETTER X */
  lazy val XK_y: Long            = 0x0079 /* U+0079 LATIN SMALL LETTER Y */
  lazy val XK_z: Long            = 0x007a /* U+007A LATIN SMALL LETTER Z */
  lazy val XK_braceleft: Long    = 0x007b /* U+007B LEFT CURLY BRACKET */
  lazy val XK_bar: Long          = 0x007c /* U+007C VERTICAL LINE */
  lazy val XK_braceright: Long   = 0x007d /* U+007D RIGHT CURLY BRACKET */
  lazy val XK_asciitilde: Long   = 0x007e /* U+007E TILDE */

  lazy val XK_nobreakspace: Long   = 0x00a0 /* U+00A0 NO-BREAK SPACE */
  lazy val XK_exclamdown: Long     = 0x00a1 /* U+00A1 INVERTED EXCLAMATION MARK */
  lazy val XK_cent: Long           = 0x00a2 /* U+00A2 CENT SIGN */
  lazy val XK_sterling: Long       = 0x00a3 /* U+00A3 POUND SIGN */
  lazy val XK_currency: Long       = 0x00a4 /* U+00A4 CURRENCY SIGN */
  lazy val XK_yen: Long            = 0x00a5 /* U+00A5 YEN SIGN */
  lazy val XK_brokenbar: Long      = 0x00a6 /* U+00A6 BROKEN BAR */
  lazy val XK_section: Long        = 0x00a7 /* U+00A7 SECTION SIGN */
  lazy val XK_diaeresis: Long      = 0x00a8 /* U+00A8 DIAERESIS */
  lazy val XK_copyright: Long      = 0x00a9 /* U+00A9 COPYRIGHT SIGN */
  lazy val XK_ordfeminine: Long    = 0x00aa /* U+00AA FEMININE ORDINAL INDICATOR */
  lazy val XK_guillemotleft: Long  = 0x00ab /* U+00AB LEFT-POINTING DOUBLE ANGLE QUOTATION MARK */
  lazy val XK_notsign: Long        = 0x00ac /* U+00AC NOT SIGN */
  lazy val XK_hyphen: Long         = 0x00ad /* U+00AD SOFT HYPHEN */
  lazy val XK_registered: Long     = 0x00ae /* U+00AE REGISTERED SIGN */
  lazy val XK_macron: Long         = 0x00af /* U+00AF MACRON */
  lazy val XK_degree: Long         = 0x00b0 /* U+00B0 DEGREE SIGN */
  lazy val XK_plusminus: Long      = 0x00b1 /* U+00B1 PLUS-MINUS SIGN */
  lazy val XK_twosuperior: Long    = 0x00b2 /* U+00B2 SUPERSCRIPT TWO */
  lazy val XK_threesuperior: Long  = 0x00b3 /* U+00B3 SUPERSCRIPT THREE */
  lazy val XK_acute: Long          = 0x00b4 /* U+00B4 ACUTE ACCENT */
  lazy val XK_mu: Long             = 0x00b5 /* U+00B5 MICRO SIGN */
  lazy val XK_paragraph: Long      = 0x00b6 /* U+00B6 PILCROW SIGN */
  lazy val XK_periodcentered: Long = 0x00b7 /* U+00B7 MIDDLE DOT */
  lazy val XK_cedilla: Long        = 0x00b8 /* U+00B8 CEDILLA */
  lazy val XK_onesuperior: Long    = 0x00b9 /* U+00B9 SUPERSCRIPT ONE */
  lazy val XK_masculine: Long      = 0x00ba /* U+00BA MASCULINE ORDINAL INDICATOR */
  lazy val XK_guillemotright: Long = 0x00bb /* U+00BB RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK */
  lazy val XK_onequarter: Long     = 0x00bc /* U+00BC VULGAR FRACTION ONE QUARTER */
  lazy val XK_onehalf: Long        = 0x00bd /* U+00BD VULGAR FRACTION ONE HALF */
  lazy val XK_threequarters: Long  = 0x00be /* U+00BE VULGAR FRACTION THREE QUARTERS */
  lazy val XK_questiondown: Long   = 0x00bf /* U+00BF INVERTED QUESTION MARK */
  lazy val XK_Agrave: Long         = 0x00c0 /* U+00C0 LATIN CAPITAL LETTER A WITH GRAVE */
  lazy val XK_Aacute: Long         = 0x00c1 /* U+00C1 LATIN CAPITAL LETTER A WITH ACUTE */
  lazy val XK_Acircumflex: Long    = 0x00c2 /* U+00C2 LATIN CAPITAL LETTER A WITH CIRCUMFLEX */
  lazy val XK_Atilde: Long         = 0x00c3 /* U+00C3 LATIN CAPITAL LETTER A WITH TILDE */
  lazy val XK_Adiaeresis: Long     = 0x00c4 /* U+00C4 LATIN CAPITAL LETTER A WITH DIAERESIS */
  lazy val XK_Aring: Long          = 0x00c5 /* U+00C5 LATIN CAPITAL LETTER A WITH RING ABOVE */
  lazy val XK_AE: Long             = 0x00c6 /* U+00C6 LATIN CAPITAL LETTER AE */
  lazy val XK_Ccedilla: Long       = 0x00c7 /* U+00C7 LATIN CAPITAL LETTER C WITH CEDILLA */
  lazy val XK_Egrave: Long         = 0x00c8 /* U+00C8 LATIN CAPITAL LETTER E WITH GRAVE */
  lazy val XK_Eacute: Long         = 0x00c9 /* U+00C9 LATIN CAPITAL LETTER E WITH ACUTE */
  lazy val XK_Ecircumflex: Long    = 0x00ca /* U+00CA LATIN CAPITAL LETTER E WITH CIRCUMFLEX */
  lazy val XK_Ediaeresis: Long     = 0x00cb /* U+00CB LATIN CAPITAL LETTER E WITH DIAERESIS */
  lazy val XK_Igrave: Long         = 0x00cc /* U+00CC LATIN CAPITAL LETTER I WITH GRAVE */
  lazy val XK_Iacute: Long         = 0x00cd /* U+00CD LATIN CAPITAL LETTER I WITH ACUTE */
  lazy val XK_Icircumflex: Long    = 0x00ce /* U+00CE LATIN CAPITAL LETTER I WITH CIRCUMFLEX */
  lazy val XK_Idiaeresis: Long     = 0x00cf /* U+00CF LATIN CAPITAL LETTER I WITH DIAERESIS */
  lazy val XK_ETH: Long            = 0x00d0 /* U+00D0 LATIN CAPITAL LETTER ETH */
  lazy val XK_Eth: Long            = 0x00d0 /* deprecated */
  lazy val XK_Ntilde: Long         = 0x00d1 /* U+00D1 LATIN CAPITAL LETTER N WITH TILDE */
  lazy val XK_Ograve: Long         = 0x00d2 /* U+00D2 LATIN CAPITAL LETTER O WITH GRAVE */
  lazy val XK_Oacute: Long         = 0x00d3 /* U+00D3 LATIN CAPITAL LETTER O WITH ACUTE */
  lazy val XK_Ocircumflex: Long    = 0x00d4 /* U+00D4 LATIN CAPITAL LETTER O WITH CIRCUMFLEX */
  lazy val XK_Otilde: Long         = 0x00d5 /* U+00D5 LATIN CAPITAL LETTER O WITH TILDE */
  lazy val XK_Odiaeresis: Long     = 0x00d6 /* U+00D6 LATIN CAPITAL LETTER O WITH DIAERESIS */
  lazy val XK_multiply: Long       = 0x00d7 /* U+00D7 MULTIPLICATION SIGN */
  lazy val XK_Oslash: Long         = 0x00d8 /* U+00D8 LATIN CAPITAL LETTER O WITH STROKE */
  lazy val XK_Ooblique: Long       = 0x00d8 /* U+00D8 LATIN CAPITAL LETTER O WITH STROKE */
  lazy val XK_Ugrave: Long         = 0x00d9 /* U+00D9 LATIN CAPITAL LETTER U WITH GRAVE */
  lazy val XK_Uacute: Long         = 0x00da /* U+00DA LATIN CAPITAL LETTER U WITH ACUTE */
  lazy val XK_Ucircumflex: Long    = 0x00db /* U+00DB LATIN CAPITAL LETTER U WITH CIRCUMFLEX */
  lazy val XK_Udiaeresis: Long     = 0x00dc /* U+00DC LATIN CAPITAL LETTER U WITH DIAERESIS */
  lazy val XK_Yacute: Long         = 0x00dd /* U+00DD LATIN CAPITAL LETTER Y WITH ACUTE */
  lazy val XK_THORN: Long          = 0x00de /* U+00DE LATIN CAPITAL LETTER THORN */
  lazy val XK_Thorn: Long          = 0x00de /* deprecated */
  lazy val XK_ssharp: Long         = 0x00df /* U+00DF LATIN SMALL LETTER SHARP S */
  lazy val XK_agrave: Long         = 0x00e0 /* U+00E0 LATIN SMALL LETTER A WITH GRAVE */
  lazy val XK_aacute: Long         = 0x00e1 /* U+00E1 LATIN SMALL LETTER A WITH ACUTE */
  lazy val XK_acircumflex: Long    = 0x00e2 /* U+00E2 LATIN SMALL LETTER A WITH CIRCUMFLEX */
  lazy val XK_atilde: Long         = 0x00e3 /* U+00E3 LATIN SMALL LETTER A WITH TILDE */
  lazy val XK_adiaeresis: Long     = 0x00e4 /* U+00E4 LATIN SMALL LETTER A WITH DIAERESIS */
  lazy val XK_aring: Long          = 0x00e5 /* U+00E5 LATIN SMALL LETTER A WITH RING ABOVE */
  lazy val XK_ae: Long             = 0x00e6 /* U+00E6 LATIN SMALL LETTER AE */
  lazy val XK_ccedilla: Long       = 0x00e7 /* U+00E7 LATIN SMALL LETTER C WITH CEDILLA */
  lazy val XK_egrave: Long         = 0x00e8 /* U+00E8 LATIN SMALL LETTER E WITH GRAVE */
  lazy val XK_eacute: Long         = 0x00e9 /* U+00E9 LATIN SMALL LETTER E WITH ACUTE */
  lazy val XK_ecircumflex: Long    = 0x00ea /* U+00EA LATIN SMALL LETTER E WITH CIRCUMFLEX */
  lazy val XK_ediaeresis: Long     = 0x00eb /* U+00EB LATIN SMALL LETTER E WITH DIAERESIS */
  lazy val XK_igrave: Long         = 0x00ec /* U+00EC LATIN SMALL LETTER I WITH GRAVE */
  lazy val XK_iacute: Long         = 0x00ed /* U+00ED LATIN SMALL LETTER I WITH ACUTE */
  lazy val XK_icircumflex: Long    = 0x00ee /* U+00EE LATIN SMALL LETTER I WITH CIRCUMFLEX */
  lazy val XK_idiaeresis: Long     = 0x00ef /* U+00EF LATIN SMALL LETTER I WITH DIAERESIS */
  lazy val XK_eth: Long            = 0x00f0 /* U+00F0 LATIN SMALL LETTER ETH */
  lazy val XK_ntilde: Long         = 0x00f1 /* U+00F1 LATIN SMALL LETTER N WITH TILDE */
  lazy val XK_ograve: Long         = 0x00f2 /* U+00F2 LATIN SMALL LETTER O WITH GRAVE */
  lazy val XK_oacute: Long         = 0x00f3 /* U+00F3 LATIN SMALL LETTER O WITH ACUTE */
  lazy val XK_ocircumflex: Long    = 0x00f4 /* U+00F4 LATIN SMALL LETTER O WITH CIRCUMFLEX */
  lazy val XK_otilde: Long         = 0x00f5 /* U+00F5 LATIN SMALL LETTER O WITH TILDE */
  lazy val XK_odiaeresis: Long     = 0x00f6 /* U+00F6 LATIN SMALL LETTER O WITH DIAERESIS */
  lazy val XK_division: Long       = 0x00f7 /* U+00F7 DIVISION SIGN */
  lazy val XK_oslash: Long         = 0x00f8 /* U+00F8 LATIN SMALL LETTER O WITH STROKE */
  lazy val XK_ooblique: Long       = 0x00f8 /* U+00F8 LATIN SMALL LETTER O WITH STROKE */
  lazy val XK_ugrave: Long         = 0x00f9 /* U+00F9 LATIN SMALL LETTER U WITH GRAVE */
  lazy val XK_uacute: Long         = 0x00fa /* U+00FA LATIN SMALL LETTER U WITH ACUTE */
  lazy val XK_ucircumflex: Long    = 0x00fb /* U+00FB LATIN SMALL LETTER U WITH CIRCUMFLEX */
  lazy val XK_udiaeresis: Long     = 0x00fc /* U+00FC LATIN SMALL LETTER U WITH DIAERESIS */
  lazy val XK_yacute: Long         = 0x00fd /* U+00FD LATIN SMALL LETTER Y WITH ACUTE */
  lazy val XK_thorn: Long          = 0x00fe /* U+00FE LATIN SMALL LETTER THORN */
  lazy val XK_ydiaeresis: Long     = 0x00ff /* U+00FF LATIN SMALL LETTER Y WITH DIAERESIS */

  /*
   * Latin 2
   * Byte 3 = 1
   */

  lazy val XK_Aogonek: Long      = 0x01a1 /* U+0104 LATIN CAPITAL LETTER A WITH OGONEK */
  lazy val XK_breve: Long        = 0x01a2 /* U+02D8 BREVE */
  lazy val XK_Lstroke: Long      = 0x01a3 /* U+0141 LATIN CAPITAL LETTER L WITH STROKE */
  lazy val XK_Lcaron: Long       = 0x01a5 /* U+013D LATIN CAPITAL LETTER L WITH CARON */
  lazy val XK_Sacute: Long       = 0x01a6 /* U+015A LATIN CAPITAL LETTER S WITH ACUTE */
  lazy val XK_Scaron: Long       = 0x01a9 /* U+0160 LATIN CAPITAL LETTER S WITH CARON */
  lazy val XK_Scedilla: Long     = 0x01aa /* U+015E LATIN CAPITAL LETTER S WITH CEDILLA */
  lazy val XK_Tcaron: Long       = 0x01ab /* U+0164 LATIN CAPITAL LETTER T WITH CARON */
  lazy val XK_Zacute: Long       = 0x01ac /* U+0179 LATIN CAPITAL LETTER Z WITH ACUTE */
  lazy val XK_Zcaron: Long       = 0x01ae /* U+017D LATIN CAPITAL LETTER Z WITH CARON */
  lazy val XK_Zabovedot: Long    = 0x01af /* U+017B LATIN CAPITAL LETTER Z WITH DOT ABOVE */
  lazy val XK_aogonek: Long      = 0x01b1 /* U+0105 LATIN SMALL LETTER A WITH OGONEK */
  lazy val XK_ogonek: Long       = 0x01b2 /* U+02DB OGONEK */
  lazy val XK_lstroke: Long      = 0x01b3 /* U+0142 LATIN SMALL LETTER L WITH STROKE */
  lazy val XK_lcaron: Long       = 0x01b5 /* U+013E LATIN SMALL LETTER L WITH CARON */
  lazy val XK_sacute: Long       = 0x01b6 /* U+015B LATIN SMALL LETTER S WITH ACUTE */
  lazy val XK_caron: Long        = 0x01b7 /* U+02C7 CARON */
  lazy val XK_scaron: Long       = 0x01b9 /* U+0161 LATIN SMALL LETTER S WITH CARON */
  lazy val XK_scedilla: Long     = 0x01ba /* U+015F LATIN SMALL LETTER S WITH CEDILLA */
  lazy val XK_tcaron: Long       = 0x01bb /* U+0165 LATIN SMALL LETTER T WITH CARON */
  lazy val XK_zacute: Long       = 0x01bc /* U+017A LATIN SMALL LETTER Z WITH ACUTE */
  lazy val XK_doubleacute: Long  = 0x01bd /* U+02DD DOUBLE ACUTE ACCENT */
  lazy val XK_zcaron: Long       = 0x01be /* U+017E LATIN SMALL LETTER Z WITH CARON */
  lazy val XK_zabovedot: Long    = 0x01bf /* U+017C LATIN SMALL LETTER Z WITH DOT ABOVE */
  lazy val XK_Racute: Long       = 0x01c0 /* U+0154 LATIN CAPITAL LETTER R WITH ACUTE */
  lazy val XK_Abreve: Long       = 0x01c3 /* U+0102 LATIN CAPITAL LETTER A WITH BREVE */
  lazy val XK_Lacute: Long       = 0x01c5 /* U+0139 LATIN CAPITAL LETTER L WITH ACUTE */
  lazy val XK_Cacute: Long       = 0x01c6 /* U+0106 LATIN CAPITAL LETTER C WITH ACUTE */
  lazy val XK_Ccaron: Long       = 0x01c8 /* U+010C LATIN CAPITAL LETTER C WITH CARON */
  lazy val XK_Eogonek: Long      = 0x01ca /* U+0118 LATIN CAPITAL LETTER E WITH OGONEK */
  lazy val XK_Ecaron: Long       = 0x01cc /* U+011A LATIN CAPITAL LETTER E WITH CARON */
  lazy val XK_Dcaron: Long       = 0x01cf /* U+010E LATIN CAPITAL LETTER D WITH CARON */
  lazy val XK_Dstroke: Long      = 0x01d0 /* U+0110 LATIN CAPITAL LETTER D WITH STROKE */
  lazy val XK_Nacute: Long       = 0x01d1 /* U+0143 LATIN CAPITAL LETTER N WITH ACUTE */
  lazy val XK_Ncaron: Long       = 0x01d2 /* U+0147 LATIN CAPITAL LETTER N WITH CARON */
  lazy val XK_Odoubleacute: Long = 0x01d5 /* U+0150 LATIN CAPITAL LETTER O WITH DOUBLE ACUTE */
  lazy val XK_Rcaron: Long       = 0x01d8 /* U+0158 LATIN CAPITAL LETTER R WITH CARON */
  lazy val XK_Uring: Long        = 0x01d9 /* U+016E LATIN CAPITAL LETTER U WITH RING ABOVE */
  lazy val XK_Udoubleacute: Long = 0x01db /* U+0170 LATIN CAPITAL LETTER U WITH DOUBLE ACUTE */
  lazy val XK_Tcedilla: Long     = 0x01de /* U+0162 LATIN CAPITAL LETTER T WITH CEDILLA */
  lazy val XK_racute: Long       = 0x01e0 /* U+0155 LATIN SMALL LETTER R WITH ACUTE */
  lazy val XK_abreve: Long       = 0x01e3 /* U+0103 LATIN SMALL LETTER A WITH BREVE */
  lazy val XK_lacute: Long       = 0x01e5 /* U+013A LATIN SMALL LETTER L WITH ACUTE */
  lazy val XK_cacute: Long       = 0x01e6 /* U+0107 LATIN SMALL LETTER C WITH ACUTE */
  lazy val XK_ccaron: Long       = 0x01e8 /* U+010D LATIN SMALL LETTER C WITH CARON */
  lazy val XK_eogonek: Long      = 0x01ea /* U+0119 LATIN SMALL LETTER E WITH OGONEK */
  lazy val XK_ecaron: Long       = 0x01ec /* U+011B LATIN SMALL LETTER E WITH CARON */
  lazy val XK_dcaron: Long       = 0x01ef /* U+010F LATIN SMALL LETTER D WITH CARON */
  lazy val XK_dstroke: Long      = 0x01f0 /* U+0111 LATIN SMALL LETTER D WITH STROKE */
  lazy val XK_nacute: Long       = 0x01f1 /* U+0144 LATIN SMALL LETTER N WITH ACUTE */
  lazy val XK_ncaron: Long       = 0x01f2 /* U+0148 LATIN SMALL LETTER N WITH CARON */
  lazy val XK_odoubleacute: Long = 0x01f5 /* U+0151 LATIN SMALL LETTER O WITH DOUBLE ACUTE */
  lazy val XK_rcaron: Long       = 0x01f8 /* U+0159 LATIN SMALL LETTER R WITH CARON */
  lazy val XK_uring: Long        = 0x01f9 /* U+016F LATIN SMALL LETTER U WITH RING ABOVE */
  lazy val XK_udoubleacute: Long = 0x01fb /* U+0171 LATIN SMALL LETTER U WITH DOUBLE ACUTE */
  lazy val XK_tcedilla: Long     = 0x01fe /* U+0163 LATIN SMALL LETTER T WITH CEDILLA */
  lazy val XK_abovedot: Long     = 0x01ff /* U+02D9 DOT ABOVE */

  /*
   * Latin 3
   * Byte 3 = 2
   */

  lazy val XK_Hstroke: Long     = 0x02a1 /* U+0126 LATIN CAPITAL LETTER H WITH STROKE */
  lazy val XK_Hcircumflex: Long = 0x02a6 /* U+0124 LATIN CAPITAL LETTER H WITH CIRCUMFLEX */
  lazy val XK_Iabovedot: Long   = 0x02a9 /* U+0130 LATIN CAPITAL LETTER I WITH DOT ABOVE */
  lazy val XK_Gbreve: Long      = 0x02ab /* U+011E LATIN CAPITAL LETTER G WITH BREVE */
  lazy val XK_Jcircumflex: Long = 0x02ac /* U+0134 LATIN CAPITAL LETTER J WITH CIRCUMFLEX */
  lazy val XK_hstroke: Long     = 0x02b1 /* U+0127 LATIN SMALL LETTER H WITH STROKE */
  lazy val XK_hcircumflex: Long = 0x02b6 /* U+0125 LATIN SMALL LETTER H WITH CIRCUMFLEX */
  lazy val XK_idotless: Long    = 0x02b9 /* U+0131 LATIN SMALL LETTER DOTLESS I */
  lazy val XK_gbreve: Long      = 0x02bb /* U+011F LATIN SMALL LETTER G WITH BREVE */
  lazy val XK_jcircumflex: Long = 0x02bc /* U+0135 LATIN SMALL LETTER J WITH CIRCUMFLEX */
  lazy val XK_Cabovedot: Long   = 0x02c5 /* U+010A LATIN CAPITAL LETTER C WITH DOT ABOVE */
  lazy val XK_Ccircumflex: Long = 0x02c6 /* U+0108 LATIN CAPITAL LETTER C WITH CIRCUMFLEX */
  lazy val XK_Gabovedot: Long   = 0x02d5 /* U+0120 LATIN CAPITAL LETTER G WITH DOT ABOVE */
  lazy val XK_Gcircumflex: Long = 0x02d8 /* U+011C LATIN CAPITAL LETTER G WITH CIRCUMFLEX */
  lazy val XK_Ubreve: Long      = 0x02dd /* U+016C LATIN CAPITAL LETTER U WITH BREVE */
  lazy val XK_Scircumflex: Long = 0x02de /* U+015C LATIN CAPITAL LETTER S WITH CIRCUMFLEX */
  lazy val XK_cabovedot: Long   = 0x02e5 /* U+010B LATIN SMALL LETTER C WITH DOT ABOVE */
  lazy val XK_ccircumflex: Long = 0x02e6 /* U+0109 LATIN SMALL LETTER C WITH CIRCUMFLEX */
  lazy val XK_gabovedot: Long   = 0x02f5 /* U+0121 LATIN SMALL LETTER G WITH DOT ABOVE */
  lazy val XK_gcircumflex: Long = 0x02f8 /* U+011D LATIN SMALL LETTER G WITH CIRCUMFLEX */
  lazy val XK_ubreve: Long      = 0x02fd /* U+016D LATIN SMALL LETTER U WITH BREVE */
  lazy val XK_scircumflex: Long = 0x02fe /* U+015D LATIN SMALL LETTER S WITH CIRCUMFLEX */

  /*
   * Latin 4
   * Byte 3 = 3
   */

  lazy val XK_kra: Long       = 0x03a2 /* U+0138 LATIN SMALL LETTER KRA */
  lazy val XK_kappa: Long     = 0x03a2 /* deprecated */
  lazy val XK_Rcedilla: Long  = 0x03a3 /* U+0156 LATIN CAPITAL LETTER R WITH CEDILLA */
  lazy val XK_Itilde: Long    = 0x03a5 /* U+0128 LATIN CAPITAL LETTER I WITH TILDE */
  lazy val XK_Lcedilla: Long  = 0x03a6 /* U+013B LATIN CAPITAL LETTER L WITH CEDILLA */
  lazy val XK_Emacron: Long   = 0x03aa /* U+0112 LATIN CAPITAL LETTER E WITH MACRON */
  lazy val XK_Gcedilla: Long  = 0x03ab /* U+0122 LATIN CAPITAL LETTER G WITH CEDILLA */
  lazy val XK_Tslash: Long    = 0x03ac /* U+0166 LATIN CAPITAL LETTER T WITH STROKE */
  lazy val XK_rcedilla: Long  = 0x03b3 /* U+0157 LATIN SMALL LETTER R WITH CEDILLA */
  lazy val XK_itilde: Long    = 0x03b5 /* U+0129 LATIN SMALL LETTER I WITH TILDE */
  lazy val XK_lcedilla: Long  = 0x03b6 /* U+013C LATIN SMALL LETTER L WITH CEDILLA */
  lazy val XK_emacron: Long   = 0x03ba /* U+0113 LATIN SMALL LETTER E WITH MACRON */
  lazy val XK_gcedilla: Long  = 0x03bb /* U+0123 LATIN SMALL LETTER G WITH CEDILLA */
  lazy val XK_tslash: Long    = 0x03bc /* U+0167 LATIN SMALL LETTER T WITH STROKE */
  lazy val XK_ENG: Long       = 0x03bd /* U+014A LATIN CAPITAL LETTER ENG */
  lazy val XK_eng: Long       = 0x03bf /* U+014B LATIN SMALL LETTER ENG */
  lazy val XK_Amacron: Long   = 0x03c0 /* U+0100 LATIN CAPITAL LETTER A WITH MACRON */
  lazy val XK_Iogonek: Long   = 0x03c7 /* U+012E LATIN CAPITAL LETTER I WITH OGONEK */
  lazy val XK_Eabovedot: Long = 0x03cc /* U+0116 LATIN CAPITAL LETTER E WITH DOT ABOVE */
  lazy val XK_Imacron: Long   = 0x03cf /* U+012A LATIN CAPITAL LETTER I WITH MACRON */
  lazy val XK_Ncedilla: Long  = 0x03d1 /* U+0145 LATIN CAPITAL LETTER N WITH CEDILLA */
  lazy val XK_Omacron: Long   = 0x03d2 /* U+014C LATIN CAPITAL LETTER O WITH MACRON */
  lazy val XK_Kcedilla: Long  = 0x03d3 /* U+0136 LATIN CAPITAL LETTER K WITH CEDILLA */
  lazy val XK_Uogonek: Long   = 0x03d9 /* U+0172 LATIN CAPITAL LETTER U WITH OGONEK */
  lazy val XK_Utilde: Long    = 0x03dd /* U+0168 LATIN CAPITAL LETTER U WITH TILDE */
  lazy val XK_Umacron: Long   = 0x03de /* U+016A LATIN CAPITAL LETTER U WITH MACRON */
  lazy val XK_amacron: Long   = 0x03e0 /* U+0101 LATIN SMALL LETTER A WITH MACRON */
  lazy val XK_iogonek: Long   = 0x03e7 /* U+012F LATIN SMALL LETTER I WITH OGONEK */
  lazy val XK_eabovedot: Long = 0x03ec /* U+0117 LATIN SMALL LETTER E WITH DOT ABOVE */
  lazy val XK_imacron: Long   = 0x03ef /* U+012B LATIN SMALL LETTER I WITH MACRON */
  lazy val XK_ncedilla: Long  = 0x03f1 /* U+0146 LATIN SMALL LETTER N WITH CEDILLA */
  lazy val XK_omacron: Long   = 0x03f2 /* U+014D LATIN SMALL LETTER O WITH MACRON */
  lazy val XK_kcedilla: Long  = 0x03f3 /* U+0137 LATIN SMALL LETTER K WITH CEDILLA */
  lazy val XK_uogonek: Long   = 0x03f9 /* U+0173 LATIN SMALL LETTER U WITH OGONEK */
  lazy val XK_utilde: Long    = 0x03fd /* U+0169 LATIN SMALL LETTER U WITH TILDE */
  lazy val XK_umacron: Long   = 0x03fe /* U+016B LATIN SMALL LETTER U WITH MACRON */

  /*
   * Latin 8
   */

  lazy val XK_Wcircumflex: Long = 0x1000174 /* U+0174 LATIN CAPITAL LETTER W WITH CIRCUMFLEX */
  lazy val XK_wcircumflex: Long = 0x1000175 /* U+0175 LATIN SMALL LETTER W WITH CIRCUMFLEX */
  lazy val XK_Ycircumflex: Long = 0x1000176 /* U+0176 LATIN CAPITAL LETTER Y WITH CIRCUMFLEX */
  lazy val XK_ycircumflex: Long = 0x1000177 /* U+0177 LATIN SMALL LETTER Y WITH CIRCUMFLEX */
  lazy val XK_Babovedot: Long   = 0x1001e02 /* U+1E02 LATIN CAPITAL LETTER B WITH DOT ABOVE */
  lazy val XK_babovedot: Long   = 0x1001e03 /* U+1E03 LATIN SMALL LETTER B WITH DOT ABOVE */
  lazy val XK_Dabovedot: Long   = 0x1001e0a /* U+1E0A LATIN CAPITAL LETTER D WITH DOT ABOVE */
  lazy val XK_dabovedot: Long   = 0x1001e0b /* U+1E0B LATIN SMALL LETTER D WITH DOT ABOVE */
  lazy val XK_Fabovedot: Long   = 0x1001e1e /* U+1E1E LATIN CAPITAL LETTER F WITH DOT ABOVE */
  lazy val XK_fabovedot: Long   = 0x1001e1f /* U+1E1F LATIN SMALL LETTER F WITH DOT ABOVE */
  lazy val XK_Mabovedot: Long   = 0x1001e40 /* U+1E40 LATIN CAPITAL LETTER M WITH DOT ABOVE */
  lazy val XK_mabovedot: Long   = 0x1001e41 /* U+1E41 LATIN SMALL LETTER M WITH DOT ABOVE */
  lazy val XK_Pabovedot: Long   = 0x1001e56 /* U+1E56 LATIN CAPITAL LETTER P WITH DOT ABOVE */
  lazy val XK_pabovedot: Long   = 0x1001e57 /* U+1E57 LATIN SMALL LETTER P WITH DOT ABOVE */
  lazy val XK_Sabovedot: Long   = 0x1001e60 /* U+1E60 LATIN CAPITAL LETTER S WITH DOT ABOVE */
  lazy val XK_sabovedot: Long   = 0x1001e61 /* U+1E61 LATIN SMALL LETTER S WITH DOT ABOVE */
  lazy val XK_Tabovedot: Long   = 0x1001e6a /* U+1E6A LATIN CAPITAL LETTER T WITH DOT ABOVE */
  lazy val XK_tabovedot: Long   = 0x1001e6b /* U+1E6B LATIN SMALL LETTER T WITH DOT ABOVE */
  lazy val XK_Wgrave: Long      = 0x1001e80 /* U+1E80 LATIN CAPITAL LETTER W WITH GRAVE */
  lazy val XK_wgrave: Long      = 0x1001e81 /* U+1E81 LATIN SMALL LETTER W WITH GRAVE */
  lazy val XK_Wacute: Long      = 0x1001e82 /* U+1E82 LATIN CAPITAL LETTER W WITH ACUTE */
  lazy val XK_wacute: Long      = 0x1001e83 /* U+1E83 LATIN SMALL LETTER W WITH ACUTE */
  lazy val XK_Wdiaeresis: Long  = 0x1001e84 /* U+1E84 LATIN CAPITAL LETTER W WITH DIAERESIS */
  lazy val XK_wdiaeresis: Long  = 0x1001e85 /* U+1E85 LATIN SMALL LETTER W WITH DIAERESIS */
  lazy val XK_Ygrave: Long      = 0x1001ef2 /* U+1EF2 LATIN CAPITAL LETTER Y WITH GRAVE */
  lazy val XK_ygrave: Long      = 0x1001ef3 /* U+1EF3 LATIN SMALL LETTER Y WITH GRAVE */

  /*
   * Latin 9
   * Byte 3 = 0x13
   */

  lazy val XK_OE: Long         = 0x13bc /* U+0152 LATIN CAPITAL LIGATURE OE */
  lazy val XK_oe: Long         = 0x13bd /* U+0153 LATIN SMALL LIGATURE OE */
  lazy val XK_Ydiaeresis: Long = 0x13be /* U+0178 LATIN CAPITAL LETTER Y WITH DIAERESIS */

  /*
   * Katakana
   * Byte 3 = 4
   */

  lazy val XK_overline: Long            = 0x047e /* U+203E OVERLINE */
  lazy val XK_kana_fullstop: Long       = 0x04a1 /* U+3002 IDEOGRAPHIC FULL STOP */
  lazy val XK_kana_openingbracket: Long = 0x04a2 /* U+300C LEFT CORNER BRACKET */
  lazy val XK_kana_closingbracket: Long = 0x04a3 /* U+300D RIGHT CORNER BRACKET */
  lazy val XK_kana_comma: Long          = 0x04a4 /* U+3001 IDEOGRAPHIC COMMA */
  lazy val XK_kana_conjunctive: Long    = 0x04a5 /* U+30FB KATAKANA MIDDLE DOT */
  lazy val XK_kana_middledot: Long      = 0x04a5 /* deprecated */
  lazy val XK_kana_WO: Long             = 0x04a6 /* U+30F2 KATAKANA LETTER WO */
  lazy val XK_kana_a: Long              = 0x04a7 /* U+30A1 KATAKANA LETTER SMALL A */
  lazy val XK_kana_i: Long              = 0x04a8 /* U+30A3 KATAKANA LETTER SMALL I */
  lazy val XK_kana_u: Long              = 0x04a9 /* U+30A5 KATAKANA LETTER SMALL U */
  lazy val XK_kana_e: Long              = 0x04aa /* U+30A7 KATAKANA LETTER SMALL E */
  lazy val XK_kana_o: Long              = 0x04ab /* U+30A9 KATAKANA LETTER SMALL O */
  lazy val XK_kana_ya: Long             = 0x04ac /* U+30E3 KATAKANA LETTER SMALL YA */
  lazy val XK_kana_yu: Long             = 0x04ad /* U+30E5 KATAKANA LETTER SMALL YU */
  lazy val XK_kana_yo: Long             = 0x04ae /* U+30E7 KATAKANA LETTER SMALL YO */
  lazy val XK_kana_tsu: Long            = 0x04af /* U+30C3 KATAKANA LETTER SMALL TU */
  lazy val XK_kana_tu: Long             = 0x04af /* deprecated */
  lazy val XK_prolongedsound: Long      = 0x04b0 /* U+30FC KATAKANA-HIRAGANA PROLONGED SOUND MARK */
  lazy val XK_kana_A: Long              = 0x04b1 /* U+30A2 KATAKANA LETTER A */
  lazy val XK_kana_I: Long              = 0x04b2 /* U+30A4 KATAKANA LETTER I */
  lazy val XK_kana_U: Long              = 0x04b3 /* U+30A6 KATAKANA LETTER U */
  lazy val XK_kana_E: Long              = 0x04b4 /* U+30A8 KATAKANA LETTER E */
  lazy val XK_kana_O: Long              = 0x04b5 /* U+30AA KATAKANA LETTER O */
  lazy val XK_kana_KA: Long             = 0x04b6 /* U+30AB KATAKANA LETTER KA */
  lazy val XK_kana_KI: Long             = 0x04b7 /* U+30AD KATAKANA LETTER KI */
  lazy val XK_kana_KU: Long             = 0x04b8 /* U+30AF KATAKANA LETTER KU */
  lazy val XK_kana_KE: Long             = 0x04b9 /* U+30B1 KATAKANA LETTER KE */
  lazy val XK_kana_KO: Long             = 0x04ba /* U+30B3 KATAKANA LETTER KO */
  lazy val XK_kana_SA: Long             = 0x04bb /* U+30B5 KATAKANA LETTER SA */
  lazy val XK_kana_SHI: Long            = 0x04bc /* U+30B7 KATAKANA LETTER SI */
  lazy val XK_kana_SU: Long             = 0x04bd /* U+30B9 KATAKANA LETTER SU */
  lazy val XK_kana_SE: Long             = 0x04be /* U+30BB KATAKANA LETTER SE */
  lazy val XK_kana_SO: Long             = 0x04bf /* U+30BD KATAKANA LETTER SO */
  lazy val XK_kana_TA: Long             = 0x04c0 /* U+30BF KATAKANA LETTER TA */
  lazy val XK_kana_CHI: Long            = 0x04c1 /* U+30C1 KATAKANA LETTER TI */
  lazy val XK_kana_TI: Long             = 0x04c1 /* deprecated */
  lazy val XK_kana_TSU: Long            = 0x04c2 /* U+30C4 KATAKANA LETTER TU */
  lazy val XK_kana_TU: Long             = 0x04c2 /* deprecated */
  lazy val XK_kana_TE: Long             = 0x04c3 /* U+30C6 KATAKANA LETTER TE */
  lazy val XK_kana_TO: Long             = 0x04c4 /* U+30C8 KATAKANA LETTER TO */
  lazy val XK_kana_NA: Long             = 0x04c5 /* U+30CA KATAKANA LETTER NA */
  lazy val XK_kana_NI: Long             = 0x04c6 /* U+30CB KATAKANA LETTER NI */
  lazy val XK_kana_NU: Long             = 0x04c7 /* U+30CC KATAKANA LETTER NU */
  lazy val XK_kana_NE: Long             = 0x04c8 /* U+30CD KATAKANA LETTER NE */
  lazy val XK_kana_NO: Long             = 0x04c9 /* U+30CE KATAKANA LETTER NO */
  lazy val XK_kana_HA: Long             = 0x04ca /* U+30CF KATAKANA LETTER HA */
  lazy val XK_kana_HI: Long             = 0x04cb /* U+30D2 KATAKANA LETTER HI */
  lazy val XK_kana_FU: Long             = 0x04cc /* U+30D5 KATAKANA LETTER HU */
  lazy val XK_kana_HU: Long             = 0x04cc /* deprecated */
  lazy val XK_kana_HE: Long             = 0x04cd /* U+30D8 KATAKANA LETTER HE */
  lazy val XK_kana_HO: Long             = 0x04ce /* U+30DB KATAKANA LETTER HO */
  lazy val XK_kana_MA: Long             = 0x04cf /* U+30DE KATAKANA LETTER MA */
  lazy val XK_kana_MI: Long             = 0x04d0 /* U+30DF KATAKANA LETTER MI */
  lazy val XK_kana_MU: Long             = 0x04d1 /* U+30E0 KATAKANA LETTER MU */
  lazy val XK_kana_ME: Long             = 0x04d2 /* U+30E1 KATAKANA LETTER ME */
  lazy val XK_kana_MO: Long             = 0x04d3 /* U+30E2 KATAKANA LETTER MO */
  lazy val XK_kana_YA: Long             = 0x04d4 /* U+30E4 KATAKANA LETTER YA */
  lazy val XK_kana_YU: Long             = 0x04d5 /* U+30E6 KATAKANA LETTER YU */
  lazy val XK_kana_YO: Long             = 0x04d6 /* U+30E8 KATAKANA LETTER YO */
  lazy val XK_kana_RA: Long             = 0x04d7 /* U+30E9 KATAKANA LETTER RA */
  lazy val XK_kana_RI: Long             = 0x04d8 /* U+30EA KATAKANA LETTER RI */
  lazy val XK_kana_RU: Long             = 0x04d9 /* U+30EB KATAKANA LETTER RU */
  lazy val XK_kana_RE: Long             = 0x04da /* U+30EC KATAKANA LETTER RE */
  lazy val XK_kana_RO: Long             = 0x04db /* U+30ED KATAKANA LETTER RO */
  lazy val XK_kana_WA: Long             = 0x04dc /* U+30EF KATAKANA LETTER WA */
  lazy val XK_kana_N: Long              = 0x04dd /* U+30F3 KATAKANA LETTER N */
  lazy val XK_voicedsound: Long         = 0x04de /* U+309B KATAKANA-HIRAGANA VOICED SOUND MARK */
  lazy val XK_semivoicedsound: Long     = 0x04df /* U+309C KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK */
  lazy val XK_kana_switch: Long         = 0xff7e /* Alias for mode_switch */

  /*
   * Arabic
   * Byte 3 = 5
   */

  lazy val XK_Farsi_0: Long                 = 0x10006f0 /* U+06F0 EXTENDED ARABIC-INDIC DIGIT ZERO */
  lazy val XK_Farsi_1: Long                 = 0x10006f1 /* U+06F1 EXTENDED ARABIC-INDIC DIGIT ONE */
  lazy val XK_Farsi_2: Long                 = 0x10006f2 /* U+06F2 EXTENDED ARABIC-INDIC DIGIT TWO */
  lazy val XK_Farsi_3: Long                 = 0x10006f3 /* U+06F3 EXTENDED ARABIC-INDIC DIGIT THREE */
  lazy val XK_Farsi_4: Long                 = 0x10006f4 /* U+06F4 EXTENDED ARABIC-INDIC DIGIT FOUR */
  lazy val XK_Farsi_5: Long                 = 0x10006f5 /* U+06F5 EXTENDED ARABIC-INDIC DIGIT FIVE */
  lazy val XK_Farsi_6: Long                 = 0x10006f6 /* U+06F6 EXTENDED ARABIC-INDIC DIGIT SIX */
  lazy val XK_Farsi_7: Long                 = 0x10006f7 /* U+06F7 EXTENDED ARABIC-INDIC DIGIT SEVEN */
  lazy val XK_Farsi_8: Long                 = 0x10006f8 /* U+06F8 EXTENDED ARABIC-INDIC DIGIT EIGHT */
  lazy val XK_Farsi_9: Long                 = 0x10006f9 /* U+06F9 EXTENDED ARABIC-INDIC DIGIT NINE */
  lazy val XK_Arabic_percent: Long          = 0x100066a /* U+066A ARABIC PERCENT SIGN */
  lazy val XK_Arabic_superscript_alef: Long = 0x1000670 /* U+0670 ARABIC LETTER SUPERSCRIPT ALEF */
  lazy val XK_Arabic_tteh: Long             = 0x1000679 /* U+0679 ARABIC LETTER TTEH */
  lazy val XK_Arabic_peh: Long              = 0x100067e /* U+067E ARABIC LETTER PEH */
  lazy val XK_Arabic_tcheh: Long            = 0x1000686 /* U+0686 ARABIC LETTER TCHEH */
  lazy val XK_Arabic_ddal: Long             = 0x1000688 /* U+0688 ARABIC LETTER DDAL */
  lazy val XK_Arabic_rreh: Long             = 0x1000691 /* U+0691 ARABIC LETTER RREH */
  lazy val XK_Arabic_comma: Long            = 0x05ac /* U+060C ARABIC COMMA */
  lazy val XK_Arabic_fullstop: Long         = 0x10006d4 /* U+06D4 ARABIC FULL STOP */
  lazy val XK_Arabic_0: Long                = 0x1000660 /* U+0660 ARABIC-INDIC DIGIT ZERO */
  lazy val XK_Arabic_1: Long                = 0x1000661 /* U+0661 ARABIC-INDIC DIGIT ONE */
  lazy val XK_Arabic_2: Long                = 0x1000662 /* U+0662 ARABIC-INDIC DIGIT TWO */
  lazy val XK_Arabic_3: Long                = 0x1000663 /* U+0663 ARABIC-INDIC DIGIT THREE */
  lazy val XK_Arabic_4: Long                = 0x1000664 /* U+0664 ARABIC-INDIC DIGIT FOUR */
  lazy val XK_Arabic_5: Long                = 0x1000665 /* U+0665 ARABIC-INDIC DIGIT FIVE */
  lazy val XK_Arabic_6: Long                = 0x1000666 /* U+0666 ARABIC-INDIC DIGIT SIX */
  lazy val XK_Arabic_7: Long                = 0x1000667 /* U+0667 ARABIC-INDIC DIGIT SEVEN */
  lazy val XK_Arabic_8: Long                = 0x1000668 /* U+0668 ARABIC-INDIC DIGIT EIGHT */
  lazy val XK_Arabic_9: Long                = 0x1000669 /* U+0669 ARABIC-INDIC DIGIT NINE */
  lazy val XK_Arabic_semicolon: Long        = 0x05bb /* U+061B ARABIC SEMICOLON */
  lazy val XK_Arabic_question_mark: Long    = 0x05bf /* U+061F ARABIC QUESTION MARK */
  lazy val XK_Arabic_hamza: Long            = 0x05c1 /* U+0621 ARABIC LETTER HAMZA */
  lazy val XK_Arabic_maddaonalef: Long      = 0x05c2 /* U+0622 ARABIC LETTER ALEF WITH MADDA ABOVE */
  lazy val XK_Arabic_hamzaonalef: Long      = 0x05c3 /* U+0623 ARABIC LETTER ALEF WITH HAMZA ABOVE */
  lazy val XK_Arabic_hamzaonwaw: Long       = 0x05c4 /* U+0624 ARABIC LETTER WAW WITH HAMZA ABOVE */
  lazy val XK_Arabic_hamzaunderalef: Long   = 0x05c5 /* U+0625 ARABIC LETTER ALEF WITH HAMZA BELOW */
  lazy val XK_Arabic_hamzaonyeh: Long       = 0x05c6 /* U+0626 ARABIC LETTER YEH WITH HAMZA ABOVE */
  lazy val XK_Arabic_alef: Long             = 0x05c7 /* U+0627 ARABIC LETTER ALEF */
  lazy val XK_Arabic_beh: Long              = 0x05c8 /* U+0628 ARABIC LETTER BEH */
  lazy val XK_Arabic_tehmarbuta: Long       = 0x05c9 /* U+0629 ARABIC LETTER TEH MARBUTA */
  lazy val XK_Arabic_teh: Long              = 0x05ca /* U+062A ARABIC LETTER TEH */
  lazy val XK_Arabic_theh: Long             = 0x05cb /* U+062B ARABIC LETTER THEH */
  lazy val XK_Arabic_jeem: Long             = 0x05cc /* U+062C ARABIC LETTER JEEM */
  lazy val XK_Arabic_hah: Long              = 0x05cd /* U+062D ARABIC LETTER HAH */
  lazy val XK_Arabic_khah: Long             = 0x05ce /* U+062E ARABIC LETTER KHAH */
  lazy val XK_Arabic_dal: Long              = 0x05cf /* U+062F ARABIC LETTER DAL */
  lazy val XK_Arabic_thal: Long             = 0x05d0 /* U+0630 ARABIC LETTER THAL */
  lazy val XK_Arabic_ra: Long               = 0x05d1 /* U+0631 ARABIC LETTER REH */
  lazy val XK_Arabic_zain: Long             = 0x05d2 /* U+0632 ARABIC LETTER ZAIN */
  lazy val XK_Arabic_seen: Long             = 0x05d3 /* U+0633 ARABIC LETTER SEEN */
  lazy val XK_Arabic_sheen: Long            = 0x05d4 /* U+0634 ARABIC LETTER SHEEN */
  lazy val XK_Arabic_sad: Long              = 0x05d5 /* U+0635 ARABIC LETTER SAD */
  lazy val XK_Arabic_dad: Long              = 0x05d6 /* U+0636 ARABIC LETTER DAD */
  lazy val XK_Arabic_tah: Long              = 0x05d7 /* U+0637 ARABIC LETTER TAH */
  lazy val XK_Arabic_zah: Long              = 0x05d8 /* U+0638 ARABIC LETTER ZAH */
  lazy val XK_Arabic_ain: Long              = 0x05d9 /* U+0639 ARABIC LETTER AIN */
  lazy val XK_Arabic_ghain: Long            = 0x05da /* U+063A ARABIC LETTER GHAIN */
  lazy val XK_Arabic_tatweel: Long          = 0x05e0 /* U+0640 ARABIC TATWEEL */
  lazy val XK_Arabic_feh: Long              = 0x05e1 /* U+0641 ARABIC LETTER FEH */
  lazy val XK_Arabic_qaf: Long              = 0x05e2 /* U+0642 ARABIC LETTER QAF */
  lazy val XK_Arabic_kaf: Long              = 0x05e3 /* U+0643 ARABIC LETTER KAF */
  lazy val XK_Arabic_lam: Long              = 0x05e4 /* U+0644 ARABIC LETTER LAM */
  lazy val XK_Arabic_meem: Long             = 0x05e5 /* U+0645 ARABIC LETTER MEEM */
  lazy val XK_Arabic_noon: Long             = 0x05e6 /* U+0646 ARABIC LETTER NOON */
  lazy val XK_Arabic_ha: Long               = 0x05e7 /* U+0647 ARABIC LETTER HEH */
  lazy val XK_Arabic_heh: Long              = 0x05e7 /* deprecated */
  lazy val XK_Arabic_waw: Long              = 0x05e8 /* U+0648 ARABIC LETTER WAW */
  lazy val XK_Arabic_alefmaksura: Long      = 0x05e9 /* U+0649 ARABIC LETTER ALEF MAKSURA */
  lazy val XK_Arabic_yeh: Long              = 0x05ea /* U+064A ARABIC LETTER YEH */
  lazy val XK_Arabic_fathatan: Long         = 0x05eb /* U+064B ARABIC FATHATAN */
  lazy val XK_Arabic_dammatan: Long         = 0x05ec /* U+064C ARABIC DAMMATAN */
  lazy val XK_Arabic_kasratan: Long         = 0x05ed /* U+064D ARABIC KASRATAN */
  lazy val XK_Arabic_fatha: Long            = 0x05ee /* U+064E ARABIC FATHA */
  lazy val XK_Arabic_damma: Long            = 0x05ef /* U+064F ARABIC DAMMA */
  lazy val XK_Arabic_kasra: Long            = 0x05f0 /* U+0650 ARABIC KASRA */
  lazy val XK_Arabic_shadda: Long           = 0x05f1 /* U+0651 ARABIC SHADDA */
  lazy val XK_Arabic_sukun: Long            = 0x05f2 /* U+0652 ARABIC SUKUN */
  lazy val XK_Arabic_madda_above: Long      = 0x1000653 /* U+0653 ARABIC MADDAH ABOVE */
  lazy val XK_Arabic_hamza_above: Long      = 0x1000654 /* U+0654 ARABIC HAMZA ABOVE */
  lazy val XK_Arabic_hamza_below: Long      = 0x1000655 /* U+0655 ARABIC HAMZA BELOW */
  lazy val XK_Arabic_jeh: Long              = 0x1000698 /* U+0698 ARABIC LETTER JEH */
  lazy val XK_Arabic_veh: Long              = 0x10006a4 /* U+06A4 ARABIC LETTER VEH */
  lazy val XK_Arabic_keheh: Long            = 0x10006a9 /* U+06A9 ARABIC LETTER KEHEH */
  lazy val XK_Arabic_gaf: Long              = 0x10006af /* U+06AF ARABIC LETTER GAF */
  lazy val XK_Arabic_noon_ghunna: Long      = 0x10006ba /* U+06BA ARABIC LETTER NOON GHUNNA */
  lazy val XK_Arabic_heh_doachashmee: Long  = 0x10006be /* U+06BE ARABIC LETTER HEH DOACHASHMEE */
  lazy val XK_Farsi_yeh: Long               = 0x10006cc /* U+06CC ARABIC LETTER FARSI YEH */
  lazy val XK_Arabic_farsi_yeh: Long        = 0x10006cc /* U+06CC ARABIC LETTER FARSI YEH */
  lazy val XK_Arabic_yeh_baree: Long        = 0x10006d2 /* U+06D2 ARABIC LETTER YEH BARREE */
  lazy val XK_Arabic_heh_goal: Long         = 0x10006c1 /* U+06C1 ARABIC LETTER HEH GOAL */
  lazy val XK_Arabic_switch: Long           = 0xff7e /* Alias for mode_switch */

  /*
   * Cyrillic
   * Byte 3 = 6
   */

  lazy val XK_Cyrillic_GHE_bar: Long        = 0x1000492 /* U+0492 CYRILLIC CAPITAL LETTER GHE WITH STROKE */
  lazy val XK_Cyrillic_ghe_bar: Long        = 0x1000493 /* U+0493 CYRILLIC SMALL LETTER GHE WITH STROKE */
  lazy val XK_Cyrillic_ZHE_descender: Long  = 0x1000496 /* U+0496 CYRILLIC CAPITAL LETTER ZHE WITH DESCENDER */
  lazy val XK_Cyrillic_zhe_descender: Long  = 0x1000497 /* U+0497 CYRILLIC SMALL LETTER ZHE WITH DESCENDER */
  lazy val XK_Cyrillic_KA_descender: Long   = 0x100049a /* U+049A CYRILLIC CAPITAL LETTER KA WITH DESCENDER */
  lazy val XK_Cyrillic_ka_descender: Long   = 0x100049b /* U+049B CYRILLIC SMALL LETTER KA WITH DESCENDER */
  lazy val XK_Cyrillic_KA_vertstroke: Long  = 0x100049c /* U+049C CYRILLIC CAPITAL LETTER KA WITH VERTICAL STROKE */
  lazy val XK_Cyrillic_ka_vertstroke: Long  = 0x100049d /* U+049D CYRILLIC SMALL LETTER KA WITH VERTICAL STROKE */
  lazy val XK_Cyrillic_EN_descender: Long   = 0x10004a2 /* U+04A2 CYRILLIC CAPITAL LETTER EN WITH DESCENDER */
  lazy val XK_Cyrillic_en_descender: Long   = 0x10004a3 /* U+04A3 CYRILLIC SMALL LETTER EN WITH DESCENDER */
  lazy val XK_Cyrillic_U_straight: Long     = 0x10004ae /* U+04AE CYRILLIC CAPITAL LETTER STRAIGHT U */
  lazy val XK_Cyrillic_u_straight: Long     = 0x10004af /* U+04AF CYRILLIC SMALL LETTER STRAIGHT U */
  lazy val XK_Cyrillic_U_straight_bar: Long = 0x10004b0 /* U+04B0 CYRILLIC CAPITAL LETTER STRAIGHT U WITH STROKE */
  lazy val XK_Cyrillic_u_straight_bar: Long = 0x10004b1 /* U+04B1 CYRILLIC SMALL LETTER STRAIGHT U WITH STROKE */
  lazy val XK_Cyrillic_HA_descender: Long   = 0x10004b2 /* U+04B2 CYRILLIC CAPITAL LETTER HA WITH DESCENDER */
  lazy val XK_Cyrillic_ha_descender: Long   = 0x10004b3 /* U+04B3 CYRILLIC SMALL LETTER HA WITH DESCENDER */
  lazy val XK_Cyrillic_CHE_descender: Long  = 0x10004b6 /* U+04B6 CYRILLIC CAPITAL LETTER CHE WITH DESCENDER */
  lazy val XK_Cyrillic_che_descender: Long  = 0x10004b7 /* U+04B7 CYRILLIC SMALL LETTER CHE WITH DESCENDER */
  lazy val XK_Cyrillic_CHE_vertstroke: Long = 0x10004b8 /* U+04B8 CYRILLIC CAPITAL LETTER CHE WITH VERTICAL STROKE */
  lazy val XK_Cyrillic_che_vertstroke: Long = 0x10004b9 /* U+04B9 CYRILLIC SMALL LETTER CHE WITH VERTICAL STROKE */
  lazy val XK_Cyrillic_SHHA: Long           = 0x10004ba /* U+04BA CYRILLIC CAPITAL LETTER SHHA */
  lazy val XK_Cyrillic_shha: Long           = 0x10004bb /* U+04BB CYRILLIC SMALL LETTER SHHA */

  lazy val XK_Cyrillic_SCHWA: Long    = 0x10004d8 /* U+04D8 CYRILLIC CAPITAL LETTER SCHWA */
  lazy val XK_Cyrillic_schwa: Long    = 0x10004d9 /* U+04D9 CYRILLIC SMALL LETTER SCHWA */
  lazy val XK_Cyrillic_I_macron: Long = 0x10004e2 /* U+04E2 CYRILLIC CAPITAL LETTER I WITH MACRON */
  lazy val XK_Cyrillic_i_macron: Long = 0x10004e3 /* U+04E3 CYRILLIC SMALL LETTER I WITH MACRON */
  lazy val XK_Cyrillic_O_bar: Long    = 0x10004e8 /* U+04E8 CYRILLIC CAPITAL LETTER BARRED O */
  lazy val XK_Cyrillic_o_bar: Long    = 0x10004e9 /* U+04E9 CYRILLIC SMALL LETTER BARRED O */
  lazy val XK_Cyrillic_U_macron: Long = 0x10004ee /* U+04EE CYRILLIC CAPITAL LETTER U WITH MACRON */
  lazy val XK_Cyrillic_u_macron: Long = 0x10004ef /* U+04EF CYRILLIC SMALL LETTER U WITH MACRON */

  lazy val XK_Serbian_dje: Long               = 0x06a1 /* U+0452 CYRILLIC SMALL LETTER DJE */
  lazy val XK_Macedonia_gje: Long             = 0x06a2 /* U+0453 CYRILLIC SMALL LETTER GJE */
  lazy val XK_Cyrillic_io: Long               = 0x06a3 /* U+0451 CYRILLIC SMALL LETTER IO */
  lazy val XK_Ukrainian_ie: Long              = 0x06a4 /* U+0454 CYRILLIC SMALL LETTER UKRAINIAN IE */
  lazy val XK_Ukranian_je: Long               = 0x06a4 /* deprecated */
  lazy val XK_Macedonia_dse: Long             = 0x06a5 /* U+0455 CYRILLIC SMALL LETTER DZE */
  lazy val XK_Ukrainian_i: Long               = 0x06a6 /* U+0456 CYRILLIC SMALL LETTER BYELORUSSIAN-UKRAINIAN I */
  lazy val XK_Ukranian_i: Long                = 0x06a6 /* deprecated */
  lazy val XK_Ukrainian_yi: Long              = 0x06a7 /* U+0457 CYRILLIC SMALL LETTER YI */
  lazy val XK_Ukranian_yi: Long               = 0x06a7 /* deprecated */
  lazy val XK_Cyrillic_je: Long               = 0x06a8 /* U+0458 CYRILLIC SMALL LETTER JE */
  lazy val XK_Serbian_je: Long                = 0x06a8 /* deprecated */
  lazy val XK_Cyrillic_lje: Long              = 0x06a9 /* U+0459 CYRILLIC SMALL LETTER LJE */
  lazy val XK_Serbian_lje: Long               = 0x06a9 /* deprecated */
  lazy val XK_Cyrillic_nje: Long              = 0x06aa /* U+045A CYRILLIC SMALL LETTER NJE */
  lazy val XK_Serbian_nje: Long               = 0x06aa /* deprecated */
  lazy val XK_Serbian_tshe: Long              = 0x06ab /* U+045B CYRILLIC SMALL LETTER TSHE */
  lazy val XK_Macedonia_kje: Long             = 0x06ac /* U+045C CYRILLIC SMALL LETTER KJE */
  lazy val XK_Ukrainian_ghe_with_upturn: Long = 0x06ad /* U+0491 CYRILLIC SMALL LETTER GHE WITH UPTURN */
  lazy val XK_Byelorussian_shortu: Long       = 0x06ae /* U+045E CYRILLIC SMALL LETTER SHORT U */
  lazy val XK_Cyrillic_dzhe: Long             = 0x06af /* U+045F CYRILLIC SMALL LETTER DZHE */
  lazy val XK_Serbian_dze: Long               = 0x06af /* deprecated */
  lazy val XK_numerosign: Long                = 0x06b0 /* U+2116 NUMERO SIGN */
  lazy val XK_Serbian_DJE: Long               = 0x06b1 /* U+0402 CYRILLIC CAPITAL LETTER DJE */
  lazy val XK_Macedonia_GJE: Long             = 0x06b2 /* U+0403 CYRILLIC CAPITAL LETTER GJE */
  lazy val XK_Cyrillic_IO: Long               = 0x06b3 /* U+0401 CYRILLIC CAPITAL LETTER IO */
  lazy val XK_Ukrainian_IE: Long              = 0x06b4 /* U+0404 CYRILLIC CAPITAL LETTER UKRAINIAN IE */
  lazy val XK_Ukranian_JE: Long               = 0x06b4 /* deprecated */
  lazy val XK_Macedonia_DSE: Long             = 0x06b5 /* U+0405 CYRILLIC CAPITAL LETTER DZE */
  lazy val XK_Ukrainian_I: Long               = 0x06b6 /* U+0406 CYRILLIC CAPITAL LETTER BYELORUSSIAN-UKRAINIAN I */
  lazy val XK_Ukranian_I: Long                = 0x06b6 /* deprecated */
  lazy val XK_Ukrainian_YI: Long              = 0x06b7 /* U+0407 CYRILLIC CAPITAL LETTER YI */
  lazy val XK_Ukranian_YI: Long               = 0x06b7 /* deprecated */
  lazy val XK_Cyrillic_JE: Long               = 0x06b8 /* U+0408 CYRILLIC CAPITAL LETTER JE */
  lazy val XK_Serbian_JE: Long                = 0x06b8 /* deprecated */
  lazy val XK_Cyrillic_LJE: Long              = 0x06b9 /* U+0409 CYRILLIC CAPITAL LETTER LJE */
  lazy val XK_Serbian_LJE: Long               = 0x06b9 /* deprecated */
  lazy val XK_Cyrillic_NJE: Long              = 0x06ba /* U+040A CYRILLIC CAPITAL LETTER NJE */
  lazy val XK_Serbian_NJE: Long               = 0x06ba /* deprecated */
  lazy val XK_Serbian_TSHE: Long              = 0x06bb /* U+040B CYRILLIC CAPITAL LETTER TSHE */
  lazy val XK_Macedonia_KJE: Long             = 0x06bc /* U+040C CYRILLIC CAPITAL LETTER KJE */
  lazy val XK_Ukrainian_GHE_WITH_UPTURN: Long = 0x06bd /* U+0490 CYRILLIC CAPITAL LETTER GHE WITH UPTURN */
  lazy val XK_Byelorussian_SHORTU: Long       = 0x06be /* U+040E CYRILLIC CAPITAL LETTER SHORT U */
  lazy val XK_Cyrillic_DZHE: Long             = 0x06bf /* U+040F CYRILLIC CAPITAL LETTER DZHE */
  lazy val XK_Serbian_DZE: Long               = 0x06bf /* deprecated */
  lazy val XK_Cyrillic_yu: Long               = 0x06c0 /* U+044E CYRILLIC SMALL LETTER YU */
  lazy val XK_Cyrillic_a: Long                = 0x06c1 /* U+0430 CYRILLIC SMALL LETTER A */
  lazy val XK_Cyrillic_be: Long               = 0x06c2 /* U+0431 CYRILLIC SMALL LETTER BE */
  lazy val XK_Cyrillic_tse: Long              = 0x06c3 /* U+0446 CYRILLIC SMALL LETTER TSE */
  lazy val XK_Cyrillic_de: Long               = 0x06c4 /* U+0434 CYRILLIC SMALL LETTER DE */
  lazy val XK_Cyrillic_ie: Long               = 0x06c5 /* U+0435 CYRILLIC SMALL LETTER IE */
  lazy val XK_Cyrillic_ef: Long               = 0x06c6 /* U+0444 CYRILLIC SMALL LETTER EF */
  lazy val XK_Cyrillic_ghe: Long              = 0x06c7 /* U+0433 CYRILLIC SMALL LETTER GHE */
  lazy val XK_Cyrillic_ha: Long               = 0x06c8 /* U+0445 CYRILLIC SMALL LETTER HA */
  lazy val XK_Cyrillic_i: Long                = 0x06c9 /* U+0438 CYRILLIC SMALL LETTER I */
  lazy val XK_Cyrillic_shorti: Long           = 0x06ca /* U+0439 CYRILLIC SMALL LETTER SHORT I */
  lazy val XK_Cyrillic_ka: Long               = 0x06cb /* U+043A CYRILLIC SMALL LETTER KA */
  lazy val XK_Cyrillic_el: Long               = 0x06cc /* U+043B CYRILLIC SMALL LETTER EL */
  lazy val XK_Cyrillic_em: Long               = 0x06cd /* U+043C CYRILLIC SMALL LETTER EM */
  lazy val XK_Cyrillic_en: Long               = 0x06ce /* U+043D CYRILLIC SMALL LETTER EN */
  lazy val XK_Cyrillic_o: Long                = 0x06cf /* U+043E CYRILLIC SMALL LETTER O */
  lazy val XK_Cyrillic_pe: Long               = 0x06d0 /* U+043F CYRILLIC SMALL LETTER PE */
  lazy val XK_Cyrillic_ya: Long               = 0x06d1 /* U+044F CYRILLIC SMALL LETTER YA */
  lazy val XK_Cyrillic_er: Long               = 0x06d2 /* U+0440 CYRILLIC SMALL LETTER ER */
  lazy val XK_Cyrillic_es: Long               = 0x06d3 /* U+0441 CYRILLIC SMALL LETTER ES */
  lazy val XK_Cyrillic_te: Long               = 0x06d4 /* U+0442 CYRILLIC SMALL LETTER TE */
  lazy val XK_Cyrillic_u: Long                = 0x06d5 /* U+0443 CYRILLIC SMALL LETTER U */
  lazy val XK_Cyrillic_zhe: Long              = 0x06d6 /* U+0436 CYRILLIC SMALL LETTER ZHE */
  lazy val XK_Cyrillic_ve: Long               = 0x06d7 /* U+0432 CYRILLIC SMALL LETTER VE */
  lazy val XK_Cyrillic_softsign: Long         = 0x06d8 /* U+044C CYRILLIC SMALL LETTER SOFT SIGN */
  lazy val XK_Cyrillic_yeru: Long             = 0x06d9 /* U+044B CYRILLIC SMALL LETTER YERU */
  lazy val XK_Cyrillic_ze: Long               = 0x06da /* U+0437 CYRILLIC SMALL LETTER ZE */
  lazy val XK_Cyrillic_sha: Long              = 0x06db /* U+0448 CYRILLIC SMALL LETTER SHA */
  lazy val XK_Cyrillic_e: Long                = 0x06dc /* U+044D CYRILLIC SMALL LETTER E */
  lazy val XK_Cyrillic_shcha: Long            = 0x06dd /* U+0449 CYRILLIC SMALL LETTER SHCHA */
  lazy val XK_Cyrillic_che: Long              = 0x06de /* U+0447 CYRILLIC SMALL LETTER CHE */
  lazy val XK_Cyrillic_hardsign: Long         = 0x06df /* U+044A CYRILLIC SMALL LETTER HARD SIGN */
  lazy val XK_Cyrillic_YU: Long               = 0x06e0 /* U+042E CYRILLIC CAPITAL LETTER YU */
  lazy val XK_Cyrillic_A: Long                = 0x06e1 /* U+0410 CYRILLIC CAPITAL LETTER A */
  lazy val XK_Cyrillic_BE: Long               = 0x06e2 /* U+0411 CYRILLIC CAPITAL LETTER BE */
  lazy val XK_Cyrillic_TSE: Long              = 0x06e3 /* U+0426 CYRILLIC CAPITAL LETTER TSE */
  lazy val XK_Cyrillic_DE: Long               = 0x06e4 /* U+0414 CYRILLIC CAPITAL LETTER DE */
  lazy val XK_Cyrillic_IE: Long               = 0x06e5 /* U+0415 CYRILLIC CAPITAL LETTER IE */
  lazy val XK_Cyrillic_EF: Long               = 0x06e6 /* U+0424 CYRILLIC CAPITAL LETTER EF */
  lazy val XK_Cyrillic_GHE: Long              = 0x06e7 /* U+0413 CYRILLIC CAPITAL LETTER GHE */
  lazy val XK_Cyrillic_HA: Long               = 0x06e8 /* U+0425 CYRILLIC CAPITAL LETTER HA */
  lazy val XK_Cyrillic_I: Long                = 0x06e9 /* U+0418 CYRILLIC CAPITAL LETTER I */
  lazy val XK_Cyrillic_SHORTI: Long           = 0x06ea /* U+0419 CYRILLIC CAPITAL LETTER SHORT I */
  lazy val XK_Cyrillic_KA: Long               = 0x06eb /* U+041A CYRILLIC CAPITAL LETTER KA */
  lazy val XK_Cyrillic_EL: Long               = 0x06ec /* U+041B CYRILLIC CAPITAL LETTER EL */
  lazy val XK_Cyrillic_EM: Long               = 0x06ed /* U+041C CYRILLIC CAPITAL LETTER EM */
  lazy val XK_Cyrillic_EN: Long               = 0x06ee /* U+041D CYRILLIC CAPITAL LETTER EN */
  lazy val XK_Cyrillic_O: Long                = 0x06ef /* U+041E CYRILLIC CAPITAL LETTER O */
  lazy val XK_Cyrillic_PE: Long               = 0x06f0 /* U+041F CYRILLIC CAPITAL LETTER PE */
  lazy val XK_Cyrillic_YA: Long               = 0x06f1 /* U+042F CYRILLIC CAPITAL LETTER YA */
  lazy val XK_Cyrillic_ER: Long               = 0x06f2 /* U+0420 CYRILLIC CAPITAL LETTER ER */
  lazy val XK_Cyrillic_ES: Long               = 0x06f3 /* U+0421 CYRILLIC CAPITAL LETTER ES */
  lazy val XK_Cyrillic_TE: Long               = 0x06f4 /* U+0422 CYRILLIC CAPITAL LETTER TE */
  lazy val XK_Cyrillic_U: Long                = 0x06f5 /* U+0423 CYRILLIC CAPITAL LETTER U */
  lazy val XK_Cyrillic_ZHE: Long              = 0x06f6 /* U+0416 CYRILLIC CAPITAL LETTER ZHE */
  lazy val XK_Cyrillic_VE: Long               = 0x06f7 /* U+0412 CYRILLIC CAPITAL LETTER VE */
  lazy val XK_Cyrillic_SOFTSIGN: Long         = 0x06f8 /* U+042C CYRILLIC CAPITAL LETTER SOFT SIGN */
  lazy val XK_Cyrillic_YERU: Long             = 0x06f9 /* U+042B CYRILLIC CAPITAL LETTER YERU */
  lazy val XK_Cyrillic_ZE: Long               = 0x06fa /* U+0417 CYRILLIC CAPITAL LETTER ZE */
  lazy val XK_Cyrillic_SHA: Long              = 0x06fb /* U+0428 CYRILLIC CAPITAL LETTER SHA */
  lazy val XK_Cyrillic_E: Long                = 0x06fc /* U+042D CYRILLIC CAPITAL LETTER E */
  lazy val XK_Cyrillic_SHCHA: Long            = 0x06fd /* U+0429 CYRILLIC CAPITAL LETTER SHCHA */
  lazy val XK_Cyrillic_CHE: Long              = 0x06fe /* U+0427 CYRILLIC CAPITAL LETTER CHE */
  lazy val XK_Cyrillic_HARDSIGN: Long         = 0x06ff /* U+042A CYRILLIC CAPITAL LETTER HARD SIGN */

  /*
   * Greek
   * (based on an early draft of, and not quite identical to, ISO/IEC 8859-7)
   * Byte 3 = 7
   */

  lazy val XK_Greek_ALPHAaccent: Long        = 0x07a1 /* U+0386 GREEK CAPITAL LETTER ALPHA WITH TONOS */
  lazy val XK_Greek_EPSILONaccent: Long      = 0x07a2 /* U+0388 GREEK CAPITAL LETTER EPSILON WITH TONOS */
  lazy val XK_Greek_ETAaccent: Long          = 0x07a3 /* U+0389 GREEK CAPITAL LETTER ETA WITH TONOS */
  lazy val XK_Greek_IOTAaccent: Long         = 0x07a4 /* U+038A GREEK CAPITAL LETTER IOTA WITH TONOS */
  lazy val XK_Greek_IOTAdieresis: Long       = 0x07a5 /* U+03AA GREEK CAPITAL LETTER IOTA WITH DIALYTIKA */
  lazy val XK_Greek_IOTAdiaeresis: Long      = 0x07a5 /* old typo */
  lazy val XK_Greek_OMICRONaccent: Long      = 0x07a7 /* U+038C GREEK CAPITAL LETTER OMICRON WITH TONOS */
  lazy val XK_Greek_UPSILONaccent: Long      = 0x07a8 /* U+038E GREEK CAPITAL LETTER UPSILON WITH TONOS */
  lazy val XK_Greek_UPSILONdieresis: Long    = 0x07a9 /* U+03AB GREEK CAPITAL LETTER UPSILON WITH DIALYTIKA */
  lazy val XK_Greek_OMEGAaccent: Long        = 0x07ab /* U+038F GREEK CAPITAL LETTER OMEGA WITH TONOS */
  lazy val XK_Greek_accentdieresis: Long     = 0x07ae /* U+0385 GREEK DIALYTIKA TONOS */
  lazy val XK_Greek_horizbar: Long           = 0x07af /* U+2015 HORIZONTAL BAR */
  lazy val XK_Greek_alphaaccent: Long        = 0x07b1 /* U+03AC GREEK SMALL LETTER ALPHA WITH TONOS */
  lazy val XK_Greek_epsilonaccent: Long      = 0x07b2 /* U+03AD GREEK SMALL LETTER EPSILON WITH TONOS */
  lazy val XK_Greek_etaaccent: Long          = 0x07b3 /* U+03AE GREEK SMALL LETTER ETA WITH TONOS */
  lazy val XK_Greek_iotaaccent: Long         = 0x07b4 /* U+03AF GREEK SMALL LETTER IOTA WITH TONOS */
  lazy val XK_Greek_iotadieresis: Long       = 0x07b5 /* U+03CA GREEK SMALL LETTER IOTA WITH DIALYTIKA */
  lazy val XK_Greek_iotaaccentdieresis: Long = 0x07b6 /* U+0390 GREEK SMALL LETTER IOTA WITH DIALYTIKA AND TONOS */
  lazy val XK_Greek_omicronaccent: Long      = 0x07b7 /* U+03CC GREEK SMALL LETTER OMICRON WITH TONOS */
  lazy val XK_Greek_upsilonaccent: Long      = 0x07b8 /* U+03CD GREEK SMALL LETTER UPSILON WITH TONOS */
  lazy val XK_Greek_upsilondieresis: Long    = 0x07b9 /* U+03CB GREEK SMALL LETTER UPSILON WITH DIALYTIKA */
  lazy val XK_Greek_upsilonaccentdieresis
    : Long                                = 0x07ba /* U+03B0 GREEK SMALL LETTER UPSILON WITH DIALYTIKA AND TONOS */
  lazy val XK_Greek_omegaaccent: Long     = 0x07bb /* U+03CE GREEK SMALL LETTER OMEGA WITH TONOS */
  lazy val XK_Greek_ALPHA: Long           = 0x07c1 /* U+0391 GREEK CAPITAL LETTER ALPHA */
  lazy val XK_Greek_BETA: Long            = 0x07c2 /* U+0392 GREEK CAPITAL LETTER BETA */
  lazy val XK_Greek_GAMMA: Long           = 0x07c3 /* U+0393 GREEK CAPITAL LETTER GAMMA */
  lazy val XK_Greek_DELTA: Long           = 0x07c4 /* U+0394 GREEK CAPITAL LETTER DELTA */
  lazy val XK_Greek_EPSILON: Long         = 0x07c5 /* U+0395 GREEK CAPITAL LETTER EPSILON */
  lazy val XK_Greek_ZETA: Long            = 0x07c6 /* U+0396 GREEK CAPITAL LETTER ZETA */
  lazy val XK_Greek_ETA: Long             = 0x07c7 /* U+0397 GREEK CAPITAL LETTER ETA */
  lazy val XK_Greek_THETA: Long           = 0x07c8 /* U+0398 GREEK CAPITAL LETTER THETA */
  lazy val XK_Greek_IOTA: Long            = 0x07c9 /* U+0399 GREEK CAPITAL LETTER IOTA */
  lazy val XK_Greek_KAPPA: Long           = 0x07ca /* U+039A GREEK CAPITAL LETTER KAPPA */
  lazy val XK_Greek_LAMDA: Long           = 0x07cb /* U+039B GREEK CAPITAL LETTER LAMDA */
  lazy val XK_Greek_LAMBDA: Long          = 0x07cb /* U+039B GREEK CAPITAL LETTER LAMDA */
  lazy val XK_Greek_MU: Long              = 0x07cc /* U+039C GREEK CAPITAL LETTER MU */
  lazy val XK_Greek_NU: Long              = 0x07cd /* U+039D GREEK CAPITAL LETTER NU */
  lazy val XK_Greek_XI: Long              = 0x07ce /* U+039E GREEK CAPITAL LETTER XI */
  lazy val XK_Greek_OMICRON: Long         = 0x07cf /* U+039F GREEK CAPITAL LETTER OMICRON */
  lazy val XK_Greek_PI: Long              = 0x07d0 /* U+03A0 GREEK CAPITAL LETTER PI */
  lazy val XK_Greek_RHO: Long             = 0x07d1 /* U+03A1 GREEK CAPITAL LETTER RHO */
  lazy val XK_Greek_SIGMA: Long           = 0x07d2 /* U+03A3 GREEK CAPITAL LETTER SIGMA */
  lazy val XK_Greek_TAU: Long             = 0x07d4 /* U+03A4 GREEK CAPITAL LETTER TAU */
  lazy val XK_Greek_UPSILON: Long         = 0x07d5 /* U+03A5 GREEK CAPITAL LETTER UPSILON */
  lazy val XK_Greek_PHI: Long             = 0x07d6 /* U+03A6 GREEK CAPITAL LETTER PHI */
  lazy val XK_Greek_CHI: Long             = 0x07d7 /* U+03A7 GREEK CAPITAL LETTER CHI */
  lazy val XK_Greek_PSI: Long             = 0x07d8 /* U+03A8 GREEK CAPITAL LETTER PSI */
  lazy val XK_Greek_OMEGA: Long           = 0x07d9 /* U+03A9 GREEK CAPITAL LETTER OMEGA */
  lazy val XK_Greek_alpha: Long           = 0x07e1 /* U+03B1 GREEK SMALL LETTER ALPHA */
  lazy val XK_Greek_beta: Long            = 0x07e2 /* U+03B2 GREEK SMALL LETTER BETA */
  lazy val XK_Greek_gamma: Long           = 0x07e3 /* U+03B3 GREEK SMALL LETTER GAMMA */
  lazy val XK_Greek_delta: Long           = 0x07e4 /* U+03B4 GREEK SMALL LETTER DELTA */
  lazy val XK_Greek_epsilon: Long         = 0x07e5 /* U+03B5 GREEK SMALL LETTER EPSILON */
  lazy val XK_Greek_zeta: Long            = 0x07e6 /* U+03B6 GREEK SMALL LETTER ZETA */
  lazy val XK_Greek_eta: Long             = 0x07e7 /* U+03B7 GREEK SMALL LETTER ETA */
  lazy val XK_Greek_theta: Long           = 0x07e8 /* U+03B8 GREEK SMALL LETTER THETA */
  lazy val XK_Greek_iota: Long            = 0x07e9 /* U+03B9 GREEK SMALL LETTER IOTA */
  lazy val XK_Greek_kappa: Long           = 0x07ea /* U+03BA GREEK SMALL LETTER KAPPA */
  lazy val XK_Greek_lamda: Long           = 0x07eb /* U+03BB GREEK SMALL LETTER LAMDA */
  lazy val XK_Greek_lambda: Long          = 0x07eb /* U+03BB GREEK SMALL LETTER LAMDA */
  lazy val XK_Greek_mu: Long              = 0x07ec /* U+03BC GREEK SMALL LETTER MU */
  lazy val XK_Greek_nu: Long              = 0x07ed /* U+03BD GREEK SMALL LETTER NU */
  lazy val XK_Greek_xi: Long              = 0x07ee /* U+03BE GREEK SMALL LETTER XI */
  lazy val XK_Greek_omicron: Long         = 0x07ef /* U+03BF GREEK SMALL LETTER OMICRON */
  lazy val XK_Greek_pi: Long              = 0x07f0 /* U+03C0 GREEK SMALL LETTER PI */
  lazy val XK_Greek_rho: Long             = 0x07f1 /* U+03C1 GREEK SMALL LETTER RHO */
  lazy val XK_Greek_sigma: Long           = 0x07f2 /* U+03C3 GREEK SMALL LETTER SIGMA */
  lazy val XK_Greek_finalsmallsigma: Long = 0x07f3 /* U+03C2 GREEK SMALL LETTER FINAL SIGMA */
  lazy val XK_Greek_tau: Long             = 0x07f4 /* U+03C4 GREEK SMALL LETTER TAU */
  lazy val XK_Greek_upsilon: Long         = 0x07f5 /* U+03C5 GREEK SMALL LETTER UPSILON */
  lazy val XK_Greek_phi: Long             = 0x07f6 /* U+03C6 GREEK SMALL LETTER PHI */
  lazy val XK_Greek_chi: Long             = 0x07f7 /* U+03C7 GREEK SMALL LETTER CHI */
  lazy val XK_Greek_psi: Long             = 0x07f8 /* U+03C8 GREEK SMALL LETTER PSI */
  lazy val XK_Greek_omega: Long           = 0x07f9 /* U+03C9 GREEK SMALL LETTER OMEGA */
  lazy val XK_Greek_switch: Long          = 0xff7e /* Alias for mode_switch */

  /*
   * Technical
   * (from the DEC VT330/VT420 Technical Character Set, http://vt100.net/charsets/technical.html)
   * Byte 3 = 8
   */

  lazy val XK_leftradical: Long               = 0x08a1 /* U+23B7 RADICAL SYMBOL BOTTOM */
  lazy val XK_topleftradical: Long            = 0x08a2 /*(U+250C BOX DRAWINGS LIGHT DOWN AND RIGHT)*/
  lazy val XK_horizconnector: Long            = 0x08a3 /*(U+2500 BOX DRAWINGS LIGHT HORIZONTAL)*/
  lazy val XK_topintegral: Long               = 0x08a4 /* U+2320 TOP HALF INTEGRAL */
  lazy val XK_botintegral: Long               = 0x08a5 /* U+2321 BOTTOM HALF INTEGRAL */
  lazy val XK_vertconnector: Long             = 0x08a6 /*(U+2502 BOX DRAWINGS LIGHT VERTICAL)*/
  lazy val XK_topleftsqbracket: Long          = 0x08a7 /* U+23A1 LEFT SQUARE BRACKET UPPER CORNER */
  lazy val XK_botleftsqbracket: Long          = 0x08a8 /* U+23A3 LEFT SQUARE BRACKET LOWER CORNER */
  lazy val XK_toprightsqbracket: Long         = 0x08a9 /* U+23A4 RIGHT SQUARE BRACKET UPPER CORNER */
  lazy val XK_botrightsqbracket: Long         = 0x08aa /* U+23A6 RIGHT SQUARE BRACKET LOWER CORNER */
  lazy val XK_topleftparens: Long             = 0x08ab /* U+239B LEFT PARENTHESIS UPPER HOOK */
  lazy val XK_botleftparens: Long             = 0x08ac /* U+239D LEFT PARENTHESIS LOWER HOOK */
  lazy val XK_toprightparens: Long            = 0x08ad /* U+239E RIGHT PARENTHESIS UPPER HOOK */
  lazy val XK_botrightparens: Long            = 0x08ae /* U+23A0 RIGHT PARENTHESIS LOWER HOOK */
  lazy val XK_leftmiddlecurlybrace: Long      = 0x08af /* U+23A8 LEFT CURLY BRACKET MIDDLE PIECE */
  lazy val XK_rightmiddlecurlybrace: Long     = 0x08b0 /* U+23AC RIGHT CURLY BRACKET MIDDLE PIECE */
  lazy val XK_topleftsummation: Long          = 0x08b1
  lazy val XK_botleftsummation: Long          = 0x08b2
  lazy val XK_topvertsummationconnector: Long = 0x08b3
  lazy val XK_botvertsummationconnector: Long = 0x08b4
  lazy val XK_toprightsummation: Long         = 0x08b5
  lazy val XK_botrightsummation: Long         = 0x08b6
  lazy val XK_rightmiddlesummation: Long      = 0x08b7
  lazy val XK_lessthanequal: Long             = 0x08bc /* U+2264 LESS-THAN OR EQUAL TO */
  lazy val XK_notequal: Long                  = 0x08bd /* U+2260 NOT EQUAL TO */
  lazy val XK_greaterthanequal: Long          = 0x08be /* U+2265 GREATER-THAN OR EQUAL TO */
  lazy val XK_integral: Long                  = 0x08bf /* U+222B INTEGRAL */
  lazy val XK_therefore: Long                 = 0x08c0 /* U+2234 THEREFORE */
  lazy val XK_variation: Long                 = 0x08c1 /* U+221D PROPORTIONAL TO */
  lazy val XK_infinity: Long                  = 0x08c2 /* U+221E INFINITY */
  lazy val XK_nabla: Long                     = 0x08c5 /* U+2207 NABLA */
  lazy val XK_approximate: Long               = 0x08c8 /* U+223C TILDE OPERATOR */
  lazy val XK_similarequal: Long              = 0x08c9 /* U+2243 ASYMPTOTICALLY EQUAL TO */
  lazy val XK_ifonlyif: Long                  = 0x08cd /* U+21D4 LEFT RIGHT DOUBLE ARROW */
  lazy val XK_implies: Long                   = 0x08ce /* U+21D2 RIGHTWARDS DOUBLE ARROW */
  lazy val XK_identical: Long                 = 0x08cf /* U+2261 IDENTICAL TO */
  lazy val XK_radical: Long                   = 0x08d6 /* U+221A SQUARE ROOT */
  lazy val XK_includedin: Long                = 0x08da /* U+2282 SUBSET OF */
  lazy val XK_includes: Long                  = 0x08db /* U+2283 SUPERSET OF */
  lazy val XK_intersection: Long              = 0x08dc /* U+2229 INTERSECTION */
  lazy val XK_union: Long                     = 0x08dd /* U+222A UNION */
  lazy val XK_logicaland: Long                = 0x08de /* U+2227 LOGICAL AND */
  lazy val XK_logicalor: Long                 = 0x08df /* U+2228 LOGICAL OR */
  lazy val XK_partialderivative: Long         = 0x08ef /* U+2202 PARTIAL DIFFERENTIAL */
  lazy val XK_function: Long                  = 0x08f6 /* U+0192 LATIN SMALL LETTER F WITH HOOK */
  lazy val XK_leftarrow: Long                 = 0x08fb /* U+2190 LEFTWARDS ARROW */
  lazy val XK_uparrow: Long                   = 0x08fc /* U+2191 UPWARDS ARROW */
  lazy val XK_rightarrow: Long                = 0x08fd /* U+2192 RIGHTWARDS ARROW */
  lazy val XK_downarrow: Long                 = 0x08fe /* U+2193 DOWNWARDS ARROW */

  /*
   * Special
   * (from the DEC VT100 Special Graphics Character Set)
   * Byte 3 = 9
   */

  lazy val XK_blank: Long          = 0x09df
  lazy val XK_soliddiamond: Long   = 0x09e0 /* U+25C6 BLACK DIAMOND */
  lazy val XK_checkerboard: Long   = 0x09e1 /* U+2592 MEDIUM SHADE */
  lazy val XK_ht: Long             = 0x09e2 /* U+2409 SYMBOL FOR HORIZONTAL TABULATION */
  lazy val XK_ff: Long             = 0x09e3 /* U+240C SYMBOL FOR FORM FEED */
  lazy val XK_cr: Long             = 0x09e4 /* U+240D SYMBOL FOR CARRIAGE RETURN */
  lazy val XK_lf: Long             = 0x09e5 /* U+240A SYMBOL FOR LINE FEED */
  lazy val XK_nl: Long             = 0x09e8 /* U+2424 SYMBOL FOR NEWLINE */
  lazy val XK_vt: Long             = 0x09e9 /* U+240B SYMBOL FOR VERTICAL TABULATION */
  lazy val XK_lowrightcorner: Long = 0x09ea /* U+2518 BOX DRAWINGS LIGHT UP AND LEFT */
  lazy val XK_uprightcorner: Long  = 0x09eb /* U+2510 BOX DRAWINGS LIGHT DOWN AND LEFT */
  lazy val XK_upleftcorner: Long   = 0x09ec /* U+250C BOX DRAWINGS LIGHT DOWN AND RIGHT */
  lazy val XK_lowleftcorner: Long  = 0x09ed /* U+2514 BOX DRAWINGS LIGHT UP AND RIGHT */
  lazy val XK_crossinglines: Long  = 0x09ee /* U+253C BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL */
  lazy val XK_horizlinescan1: Long = 0x09ef /* U+23BA HORIZONTAL SCAN LINE-1 */
  lazy val XK_horizlinescan3: Long = 0x09f0 /* U+23BB HORIZONTAL SCAN LINE-3 */
  lazy val XK_horizlinescan5: Long = 0x09f1 /* U+2500 BOX DRAWINGS LIGHT HORIZONTAL */
  lazy val XK_horizlinescan7: Long = 0x09f2 /* U+23BC HORIZONTAL SCAN LINE-7 */
  lazy val XK_horizlinescan9: Long = 0x09f3 /* U+23BD HORIZONTAL SCAN LINE-9 */
  lazy val XK_leftt: Long          = 0x09f4 /* U+251C BOX DRAWINGS LIGHT VERTICAL AND RIGHT */
  lazy val XK_rightt: Long         = 0x09f5 /* U+2524 BOX DRAWINGS LIGHT VERTICAL AND LEFT */
  lazy val XK_bott: Long           = 0x09f6 /* U+2534 BOX DRAWINGS LIGHT UP AND HORIZONTAL */
  lazy val XK_topt: Long           = 0x09f7 /* U+252C BOX DRAWINGS LIGHT DOWN AND HORIZONTAL */
  lazy val XK_vertbar: Long        = 0x09f8 /* U+2502 BOX DRAWINGS LIGHT VERTICAL */

  /*
   * Publishing
   * (these are probably from a long forgotten DEC Publishing
   * font that once shipped with DECwrite)
   * Byte 3 = 0x0a
   */

  lazy val XK_emspace: Long              = 0x0aa1 /* U+2003 EM SPACE */
  lazy val XK_enspace: Long              = 0x0aa2 /* U+2002 EN SPACE */
  lazy val XK_em3space: Long             = 0x0aa3 /* U+2004 THREE-PER-EM SPACE */
  lazy val XK_em4space: Long             = 0x0aa4 /* U+2005 FOUR-PER-EM SPACE */
  lazy val XK_digitspace: Long           = 0x0aa5 /* U+2007 FIGURE SPACE */
  lazy val XK_punctspace: Long           = 0x0aa6 /* U+2008 PUNCTUATION SPACE */
  lazy val XK_thinspace: Long            = 0x0aa7 /* U+2009 THIN SPACE */
  lazy val XK_hairspace: Long            = 0x0aa8 /* U+200A HAIR SPACE */
  lazy val XK_emdash: Long               = 0x0aa9 /* U+2014 EM DASH */
  lazy val XK_endash: Long               = 0x0aaa /* U+2013 EN DASH */
  lazy val XK_signifblank: Long          = 0x0aac /*(U+2423 OPEN BOX)*/
  lazy val XK_ellipsis: Long             = 0x0aae /* U+2026 HORIZONTAL ELLIPSIS */
  lazy val XK_doubbaselinedot: Long      = 0x0aaf /* U+2025 TWO DOT LEADER */
  lazy val XK_onethird: Long             = 0x0ab0 /* U+2153 VULGAR FRACTION ONE THIRD */
  lazy val XK_twothirds: Long            = 0x0ab1 /* U+2154 VULGAR FRACTION TWO THIRDS */
  lazy val XK_onefifth: Long             = 0x0ab2 /* U+2155 VULGAR FRACTION ONE FIFTH */
  lazy val XK_twofifths: Long            = 0x0ab3 /* U+2156 VULGAR FRACTION TWO FIFTHS */
  lazy val XK_threefifths: Long          = 0x0ab4 /* U+2157 VULGAR FRACTION THREE FIFTHS */
  lazy val XK_fourfifths: Long           = 0x0ab5 /* U+2158 VULGAR FRACTION FOUR FIFTHS */
  lazy val XK_onesixth: Long             = 0x0ab6 /* U+2159 VULGAR FRACTION ONE SIXTH */
  lazy val XK_fivesixths: Long           = 0x0ab7 /* U+215A VULGAR FRACTION FIVE SIXTHS */
  lazy val XK_careof: Long               = 0x0ab8 /* U+2105 CARE OF */
  lazy val XK_figdash: Long              = 0x0abb /* U+2012 FIGURE DASH */
  lazy val XK_leftanglebracket: Long     = 0x0abc /*(U+27E8 MATHEMATICAL LEFT ANGLE BRACKET)*/
  lazy val XK_decimalpoint: Long         = 0x0abd /*(U+002E FULL STOP)*/
  lazy val XK_rightanglebracket: Long    = 0x0abe /*(U+27E9 MATHEMATICAL RIGHT ANGLE BRACKET)*/
  lazy val XK_marker: Long               = 0x0abf
  lazy val XK_oneeighth: Long            = 0x0ac3 /* U+215B VULGAR FRACTION ONE EIGHTH */
  lazy val XK_threeeighths: Long         = 0x0ac4 /* U+215C VULGAR FRACTION THREE EIGHTHS */
  lazy val XK_fiveeighths: Long          = 0x0ac5 /* U+215D VULGAR FRACTION FIVE EIGHTHS */
  lazy val XK_seveneighths: Long         = 0x0ac6 /* U+215E VULGAR FRACTION SEVEN EIGHTHS */
  lazy val XK_trademark: Long            = 0x0ac9 /* U+2122 TRADE MARK SIGN */
  lazy val XK_signaturemark: Long        = 0x0aca /*(U+2613 SALTIRE)*/
  lazy val XK_trademarkincircle: Long    = 0x0acb
  lazy val XK_leftopentriangle: Long     = 0x0acc /*(U+25C1 WHITE LEFT-POINTING TRIANGLE)*/
  lazy val XK_rightopentriangle: Long    = 0x0acd /*(U+25B7 WHITE RIGHT-POINTING TRIANGLE)*/
  lazy val XK_emopencircle: Long         = 0x0ace /*(U+25CB WHITE CIRCLE)*/
  lazy val XK_emopenrectangle: Long      = 0x0acf /*(U+25AF WHITE VERTICAL RECTANGLE)*/
  lazy val XK_leftsinglequotemark: Long  = 0x0ad0 /* U+2018 LEFT SINGLE QUOTATION MARK */
  lazy val XK_rightsinglequotemark: Long = 0x0ad1 /* U+2019 RIGHT SINGLE QUOTATION MARK */
  lazy val XK_leftdoublequotemark: Long  = 0x0ad2 /* U+201C LEFT DOUBLE QUOTATION MARK */
  lazy val XK_rightdoublequotemark: Long = 0x0ad3 /* U+201D RIGHT DOUBLE QUOTATION MARK */
  lazy val XK_prescription: Long         = 0x0ad4 /* U+211E PRESCRIPTION TAKE */
  lazy val XK_permille: Long             = 0x0ad5 /* U+2030 PER MILLE SIGN */
  lazy val XK_minutes: Long              = 0x0ad6 /* U+2032 PRIME */
  lazy val XK_seconds: Long              = 0x0ad7 /* U+2033 DOUBLE PRIME */
  lazy val XK_latincross: Long           = 0x0ad9 /* U+271D LATIN CROSS */
  lazy val XK_hexagram: Long             = 0x0ada
  lazy val XK_filledrectbullet: Long     = 0x0adb /*(U+25AC BLACK RECTANGLE)*/
  lazy val XK_filledlefttribullet: Long  = 0x0adc /*(U+25C0 BLACK LEFT-POINTING TRIANGLE)*/
  lazy val XK_filledrighttribullet: Long = 0x0add /*(U+25B6 BLACK RIGHT-POINTING TRIANGLE)*/
  lazy val XK_emfilledcircle: Long       = 0x0ade /*(U+25CF BLACK CIRCLE)*/
  lazy val XK_emfilledrect: Long         = 0x0adf /*(U+25AE BLACK VERTICAL RECTANGLE)*/
  lazy val XK_enopencircbullet: Long     = 0x0ae0 /*(U+25E6 WHITE BULLET)*/
  lazy val XK_enopensquarebullet: Long   = 0x0ae1 /*(U+25AB WHITE SMALL SQUARE)*/
  lazy val XK_openrectbullet: Long       = 0x0ae2 /*(U+25AD WHITE RECTANGLE)*/
  lazy val XK_opentribulletup: Long      = 0x0ae3 /*(U+25B3 WHITE UP-POINTING TRIANGLE)*/
  lazy val XK_opentribulletdown: Long    = 0x0ae4 /*(U+25BD WHITE DOWN-POINTING TRIANGLE)*/
  lazy val XK_openstar: Long             = 0x0ae5 /*(U+2606 WHITE STAR)*/
  lazy val XK_enfilledcircbullet: Long   = 0x0ae6 /*(U+2022 BULLET)*/
  lazy val XK_enfilledsqbullet: Long     = 0x0ae7 /*(U+25AA BLACK SMALL SQUARE)*/
  lazy val XK_filledtribulletup: Long    = 0x0ae8 /*(U+25B2 BLACK UP-POINTING TRIANGLE)*/
  lazy val XK_filledtribulletdown: Long  = 0x0ae9 /*(U+25BC BLACK DOWN-POINTING TRIANGLE)*/
  lazy val XK_leftpointer: Long          = 0x0aea /*(U+261C WHITE LEFT POINTING INDEX)*/
  lazy val XK_rightpointer: Long         = 0x0aeb /*(U+261E WHITE RIGHT POINTING INDEX)*/
  lazy val XK_club: Long                 = 0x0aec /* U+2663 BLACK CLUB SUIT */
  lazy val XK_diamond: Long              = 0x0aed /* U+2666 BLACK DIAMOND SUIT */
  lazy val XK_heart: Long                = 0x0aee /* U+2665 BLACK HEART SUIT */
  lazy val XK_maltesecross: Long         = 0x0af0 /* U+2720 MALTESE CROSS */
  lazy val XK_dagger: Long               = 0x0af1 /* U+2020 DAGGER */
  lazy val XK_doubledagger: Long         = 0x0af2 /* U+2021 DOUBLE DAGGER */
  lazy val XK_checkmark: Long            = 0x0af3 /* U+2713 CHECK MARK */
  lazy val XK_ballotcross: Long          = 0x0af4 /* U+2717 BALLOT X */
  lazy val XK_musicalsharp: Long         = 0x0af5 /* U+266F MUSIC SHARP SIGN */
  lazy val XK_musicalflat: Long          = 0x0af6 /* U+266D MUSIC FLAT SIGN */
  lazy val XK_malesymbol: Long           = 0x0af7 /* U+2642 MALE SIGN */
  lazy val XK_femalesymbol: Long         = 0x0af8 /* U+2640 FEMALE SIGN */
  lazy val XK_telephone: Long            = 0x0af9 /* U+260E BLACK TELEPHONE */
  lazy val XK_telephonerecorder: Long    = 0x0afa /* U+2315 TELEPHONE RECORDER */
  lazy val XK_phonographcopyright: Long  = 0x0afb /* U+2117 SOUND RECORDING COPYRIGHT */
  lazy val XK_caret: Long                = 0x0afc /* U+2038 CARET */
  lazy val XK_singlelowquotemark: Long   = 0x0afd /* U+201A SINGLE LOW-9 QUOTATION MARK */
  lazy val XK_doublelowquotemark: Long   = 0x0afe /* U+201E DOUBLE LOW-9 QUOTATION MARK */
  lazy val XK_cursor: Long               = 0x0aff

  /*
   * APL
   * Byte 3 = 0x0b
   */

  lazy val XK_leftcaret: Long  = 0x0ba3 /*(U+003C LESS-THAN SIGN)*/
  lazy val XK_rightcaret: Long = 0x0ba6 /*(U+003E GREATER-THAN SIGN)*/
  lazy val XK_downcaret: Long  = 0x0ba8 /*(U+2228 LOGICAL OR)*/
  lazy val XK_upcaret: Long    = 0x0ba9 /*(U+2227 LOGICAL AND)*/
  lazy val XK_overbar: Long    = 0x0bc0 /*(U+00AF MACRON)*/
  lazy val XK_downtack: Long   = 0x0bc2 /* U+22A4 DOWN TACK */
  lazy val XK_upshoe: Long     = 0x0bc3 /*(U+2229 INTERSECTION)*/
  lazy val XK_downstile: Long  = 0x0bc4 /* U+230A LEFT FLOOR */
  lazy val XK_underbar: Long   = 0x0bc6 /*(U+005F LOW LINE)*/
  lazy val XK_jot: Long        = 0x0bca /* U+2218 RING OPERATOR */
  lazy val XK_quad: Long       = 0x0bcc /* U+2395 APL FUNCTIONAL SYMBOL QUAD */
  lazy val XK_uptack: Long     = 0x0bce /* U+22A5 UP TACK */
  lazy val XK_circle: Long     = 0x0bcf /* U+25CB WHITE CIRCLE */
  lazy val XK_upstile: Long    = 0x0bd3 /* U+2308 LEFT CEILING */
  lazy val XK_downshoe: Long   = 0x0bd6 /*(U+222A UNION)*/
  lazy val XK_rightshoe: Long  = 0x0bd8 /*(U+2283 SUPERSET OF)*/
  lazy val XK_leftshoe: Long   = 0x0bda /*(U+2282 SUBSET OF)*/
  lazy val XK_lefttack: Long   = 0x0bdc /* U+22A3 LEFT TACK */
  lazy val XK_righttack: Long  = 0x0bfc /* U+22A2 RIGHT TACK */

  /*
   * Hebrew
   * Byte 3 = 0x0c
   */

  lazy val XK_hebrew_doublelowline: Long = 0x0cdf /* U+2017 DOUBLE LOW LINE */
  lazy val XK_hebrew_aleph: Long         = 0x0ce0 /* U+05D0 HEBREW LETTER ALEF */
  lazy val XK_hebrew_bet: Long           = 0x0ce1 /* U+05D1 HEBREW LETTER BET */
  lazy val XK_hebrew_beth: Long          = 0x0ce1 /* deprecated */
  lazy val XK_hebrew_gimel: Long         = 0x0ce2 /* U+05D2 HEBREW LETTER GIMEL */
  lazy val XK_hebrew_gimmel: Long        = 0x0ce2 /* deprecated */
  lazy val XK_hebrew_dalet: Long         = 0x0ce3 /* U+05D3 HEBREW LETTER DALET */
  lazy val XK_hebrew_daleth: Long        = 0x0ce3 /* deprecated */
  lazy val XK_hebrew_he: Long            = 0x0ce4 /* U+05D4 HEBREW LETTER HE */
  lazy val XK_hebrew_waw: Long           = 0x0ce5 /* U+05D5 HEBREW LETTER VAV */
  lazy val XK_hebrew_zain: Long          = 0x0ce6 /* U+05D6 HEBREW LETTER ZAYIN */
  lazy val XK_hebrew_zayin: Long         = 0x0ce6 /* deprecated */
  lazy val XK_hebrew_chet: Long          = 0x0ce7 /* U+05D7 HEBREW LETTER HET */
  lazy val XK_hebrew_het: Long           = 0x0ce7 /* deprecated */
  lazy val XK_hebrew_tet: Long           = 0x0ce8 /* U+05D8 HEBREW LETTER TET */
  lazy val XK_hebrew_teth: Long          = 0x0ce8 /* deprecated */
  lazy val XK_hebrew_yod: Long           = 0x0ce9 /* U+05D9 HEBREW LETTER YOD */
  lazy val XK_hebrew_finalkaph: Long     = 0x0cea /* U+05DA HEBREW LETTER FINAL KAF */
  lazy val XK_hebrew_kaph: Long          = 0x0ceb /* U+05DB HEBREW LETTER KAF */
  lazy val XK_hebrew_lamed: Long         = 0x0cec /* U+05DC HEBREW LETTER LAMED */
  lazy val XK_hebrew_finalmem: Long      = 0x0ced /* U+05DD HEBREW LETTER FINAL MEM */
  lazy val XK_hebrew_mem: Long           = 0x0cee /* U+05DE HEBREW LETTER MEM */
  lazy val XK_hebrew_finalnun: Long      = 0x0cef /* U+05DF HEBREW LETTER FINAL NUN */
  lazy val XK_hebrew_nun: Long           = 0x0cf0 /* U+05E0 HEBREW LETTER NUN */
  lazy val XK_hebrew_samech: Long        = 0x0cf1 /* U+05E1 HEBREW LETTER SAMEKH */
  lazy val XK_hebrew_samekh: Long        = 0x0cf1 /* deprecated */
  lazy val XK_hebrew_ayin: Long          = 0x0cf2 /* U+05E2 HEBREW LETTER AYIN */
  lazy val XK_hebrew_finalpe: Long       = 0x0cf3 /* U+05E3 HEBREW LETTER FINAL PE */
  lazy val XK_hebrew_pe: Long            = 0x0cf4 /* U+05E4 HEBREW LETTER PE */
  lazy val XK_hebrew_finalzade: Long     = 0x0cf5 /* U+05E5 HEBREW LETTER FINAL TSADI */
  lazy val XK_hebrew_finalzadi: Long     = 0x0cf5 /* deprecated */
  lazy val XK_hebrew_zade: Long          = 0x0cf6 /* U+05E6 HEBREW LETTER TSADI */
  lazy val XK_hebrew_zadi: Long          = 0x0cf6 /* deprecated */
  lazy val XK_hebrew_qoph: Long          = 0x0cf7 /* U+05E7 HEBREW LETTER QOF */
  lazy val XK_hebrew_kuf: Long           = 0x0cf7 /* deprecated */
  lazy val XK_hebrew_resh: Long          = 0x0cf8 /* U+05E8 HEBREW LETTER RESH */
  lazy val XK_hebrew_shin: Long          = 0x0cf9 /* U+05E9 HEBREW LETTER SHIN */
  lazy val XK_hebrew_taw: Long           = 0x0cfa /* U+05EA HEBREW LETTER TAV */
  lazy val XK_hebrew_taf: Long           = 0x0cfa /* deprecated */
  lazy val XK_Hebrew_switch: Long        = 0xff7e /* Alias for mode_switch */

  /*
   * Thai
   * Byte 3 = 0x0d
   */

  lazy val XK_Thai_kokai: Long             = 0x0da1 /* U+0E01 THAI CHARACTER KO KAI */
  lazy val XK_Thai_khokhai: Long           = 0x0da2 /* U+0E02 THAI CHARACTER KHO KHAI */
  lazy val XK_Thai_khokhuat: Long          = 0x0da3 /* U+0E03 THAI CHARACTER KHO KHUAT */
  lazy val XK_Thai_khokhwai: Long          = 0x0da4 /* U+0E04 THAI CHARACTER KHO KHWAI */
  lazy val XK_Thai_khokhon: Long           = 0x0da5 /* U+0E05 THAI CHARACTER KHO KHON */
  lazy val XK_Thai_khorakhang: Long        = 0x0da6 /* U+0E06 THAI CHARACTER KHO RAKHANG */
  lazy val XK_Thai_ngongu: Long            = 0x0da7 /* U+0E07 THAI CHARACTER NGO NGU */
  lazy val XK_Thai_chochan: Long           = 0x0da8 /* U+0E08 THAI CHARACTER CHO CHAN */
  lazy val XK_Thai_choching: Long          = 0x0da9 /* U+0E09 THAI CHARACTER CHO CHING */
  lazy val XK_Thai_chochang: Long          = 0x0daa /* U+0E0A THAI CHARACTER CHO CHANG */
  lazy val XK_Thai_soso: Long              = 0x0dab /* U+0E0B THAI CHARACTER SO SO */
  lazy val XK_Thai_chochoe: Long           = 0x0dac /* U+0E0C THAI CHARACTER CHO CHOE */
  lazy val XK_Thai_yoying: Long            = 0x0dad /* U+0E0D THAI CHARACTER YO YING */
  lazy val XK_Thai_dochada: Long           = 0x0dae /* U+0E0E THAI CHARACTER DO CHADA */
  lazy val XK_Thai_topatak: Long           = 0x0daf /* U+0E0F THAI CHARACTER TO PATAK */
  lazy val XK_Thai_thothan: Long           = 0x0db0 /* U+0E10 THAI CHARACTER THO THAN */
  lazy val XK_Thai_thonangmontho: Long     = 0x0db1 /* U+0E11 THAI CHARACTER THO NANGMONTHO */
  lazy val XK_Thai_thophuthao: Long        = 0x0db2 /* U+0E12 THAI CHARACTER THO PHUTHAO */
  lazy val XK_Thai_nonen: Long             = 0x0db3 /* U+0E13 THAI CHARACTER NO NEN */
  lazy val XK_Thai_dodek: Long             = 0x0db4 /* U+0E14 THAI CHARACTER DO DEK */
  lazy val XK_Thai_totao: Long             = 0x0db5 /* U+0E15 THAI CHARACTER TO TAO */
  lazy val XK_Thai_thothung: Long          = 0x0db6 /* U+0E16 THAI CHARACTER THO THUNG */
  lazy val XK_Thai_thothahan: Long         = 0x0db7 /* U+0E17 THAI CHARACTER THO THAHAN */
  lazy val XK_Thai_thothong: Long          = 0x0db8 /* U+0E18 THAI CHARACTER THO THONG */
  lazy val XK_Thai_nonu: Long              = 0x0db9 /* U+0E19 THAI CHARACTER NO NU */
  lazy val XK_Thai_bobaimai: Long          = 0x0dba /* U+0E1A THAI CHARACTER BO BAIMAI */
  lazy val XK_Thai_popla: Long             = 0x0dbb /* U+0E1B THAI CHARACTER PO PLA */
  lazy val XK_Thai_phophung: Long          = 0x0dbc /* U+0E1C THAI CHARACTER PHO PHUNG */
  lazy val XK_Thai_fofa: Long              = 0x0dbd /* U+0E1D THAI CHARACTER FO FA */
  lazy val XK_Thai_phophan: Long           = 0x0dbe /* U+0E1E THAI CHARACTER PHO PHAN */
  lazy val XK_Thai_fofan: Long             = 0x0dbf /* U+0E1F THAI CHARACTER FO FAN */
  lazy val XK_Thai_phosamphao: Long        = 0x0dc0 /* U+0E20 THAI CHARACTER PHO SAMPHAO */
  lazy val XK_Thai_moma: Long              = 0x0dc1 /* U+0E21 THAI CHARACTER MO MA */
  lazy val XK_Thai_yoyak: Long             = 0x0dc2 /* U+0E22 THAI CHARACTER YO YAK */
  lazy val XK_Thai_rorua: Long             = 0x0dc3 /* U+0E23 THAI CHARACTER RO RUA */
  lazy val XK_Thai_ru: Long                = 0x0dc4 /* U+0E24 THAI CHARACTER RU */
  lazy val XK_Thai_loling: Long            = 0x0dc5 /* U+0E25 THAI CHARACTER LO LING */
  lazy val XK_Thai_lu: Long                = 0x0dc6 /* U+0E26 THAI CHARACTER LU */
  lazy val XK_Thai_wowaen: Long            = 0x0dc7 /* U+0E27 THAI CHARACTER WO WAEN */
  lazy val XK_Thai_sosala: Long            = 0x0dc8 /* U+0E28 THAI CHARACTER SO SALA */
  lazy val XK_Thai_sorusi: Long            = 0x0dc9 /* U+0E29 THAI CHARACTER SO RUSI */
  lazy val XK_Thai_sosua: Long             = 0x0dca /* U+0E2A THAI CHARACTER SO SUA */
  lazy val XK_Thai_hohip: Long             = 0x0dcb /* U+0E2B THAI CHARACTER HO HIP */
  lazy val XK_Thai_lochula: Long           = 0x0dcc /* U+0E2C THAI CHARACTER LO CHULA */
  lazy val XK_Thai_oang: Long              = 0x0dcd /* U+0E2D THAI CHARACTER O ANG */
  lazy val XK_Thai_honokhuk: Long          = 0x0dce /* U+0E2E THAI CHARACTER HO NOKHUK */
  lazy val XK_Thai_paiyannoi: Long         = 0x0dcf /* U+0E2F THAI CHARACTER PAIYANNOI */
  lazy val XK_Thai_saraa: Long             = 0x0dd0 /* U+0E30 THAI CHARACTER SARA A */
  lazy val XK_Thai_maihanakat: Long        = 0x0dd1 /* U+0E31 THAI CHARACTER MAI HAN-AKAT */
  lazy val XK_Thai_saraaa: Long            = 0x0dd2 /* U+0E32 THAI CHARACTER SARA AA */
  lazy val XK_Thai_saraam: Long            = 0x0dd3 /* U+0E33 THAI CHARACTER SARA AM */
  lazy val XK_Thai_sarai: Long             = 0x0dd4 /* U+0E34 THAI CHARACTER SARA I */
  lazy val XK_Thai_saraii: Long            = 0x0dd5 /* U+0E35 THAI CHARACTER SARA II */
  lazy val XK_Thai_saraue: Long            = 0x0dd6 /* U+0E36 THAI CHARACTER SARA UE */
  lazy val XK_Thai_sarauee: Long           = 0x0dd7 /* U+0E37 THAI CHARACTER SARA UEE */
  lazy val XK_Thai_sarau: Long             = 0x0dd8 /* U+0E38 THAI CHARACTER SARA U */
  lazy val XK_Thai_sarauu: Long            = 0x0dd9 /* U+0E39 THAI CHARACTER SARA UU */
  lazy val XK_Thai_phinthu: Long           = 0x0dda /* U+0E3A THAI CHARACTER PHINTHU */
  lazy val XK_Thai_maihanakat_maitho: Long = 0x0dde
  lazy val XK_Thai_baht: Long              = 0x0ddf /* U+0E3F THAI CURRENCY SYMBOL BAHT */
  lazy val XK_Thai_sarae: Long             = 0x0de0 /* U+0E40 THAI CHARACTER SARA E */
  lazy val XK_Thai_saraae: Long            = 0x0de1 /* U+0E41 THAI CHARACTER SARA AE */
  lazy val XK_Thai_sarao: Long             = 0x0de2 /* U+0E42 THAI CHARACTER SARA O */
  lazy val XK_Thai_saraaimaimuan: Long     = 0x0de3 /* U+0E43 THAI CHARACTER SARA AI MAIMUAN */
  lazy val XK_Thai_saraaimaimalai: Long    = 0x0de4 /* U+0E44 THAI CHARACTER SARA AI MAIMALAI */
  lazy val XK_Thai_lakkhangyao: Long       = 0x0de5 /* U+0E45 THAI CHARACTER LAKKHANGYAO */
  lazy val XK_Thai_maiyamok: Long          = 0x0de6 /* U+0E46 THAI CHARACTER MAIYAMOK */
  lazy val XK_Thai_maitaikhu: Long         = 0x0de7 /* U+0E47 THAI CHARACTER MAITAIKHU */
  lazy val XK_Thai_maiek: Long             = 0x0de8 /* U+0E48 THAI CHARACTER MAI EK */
  lazy val XK_Thai_maitho: Long            = 0x0de9 /* U+0E49 THAI CHARACTER MAI THO */
  lazy val XK_Thai_maitri: Long            = 0x0dea /* U+0E4A THAI CHARACTER MAI TRI */
  lazy val XK_Thai_maichattawa: Long       = 0x0deb /* U+0E4B THAI CHARACTER MAI CHATTAWA */
  lazy val XK_Thai_thanthakhat: Long       = 0x0dec /* U+0E4C THAI CHARACTER THANTHAKHAT */
  lazy val XK_Thai_nikhahit: Long          = 0x0ded /* U+0E4D THAI CHARACTER NIKHAHIT */
  lazy val XK_Thai_leksun: Long            = 0x0df0 /* U+0E50 THAI DIGIT ZERO */
  lazy val XK_Thai_leknung: Long           = 0x0df1 /* U+0E51 THAI DIGIT ONE */
  lazy val XK_Thai_leksong: Long           = 0x0df2 /* U+0E52 THAI DIGIT TWO */
  lazy val XK_Thai_leksam: Long            = 0x0df3 /* U+0E53 THAI DIGIT THREE */
  lazy val XK_Thai_leksi: Long             = 0x0df4 /* U+0E54 THAI DIGIT FOUR */
  lazy val XK_Thai_lekha: Long             = 0x0df5 /* U+0E55 THAI DIGIT FIVE */
  lazy val XK_Thai_lekhok: Long            = 0x0df6 /* U+0E56 THAI DIGIT SIX */
  lazy val XK_Thai_lekchet: Long           = 0x0df7 /* U+0E57 THAI DIGIT SEVEN */
  lazy val XK_Thai_lekpaet: Long           = 0x0df8 /* U+0E58 THAI DIGIT EIGHT */
  lazy val XK_Thai_lekkao: Long            = 0x0df9 /* U+0E59 THAI DIGIT NINE */

  /*
   * Korean
   * Byte 3 = 0x0e
   */

  lazy val XK_Hangul: Long                   = 0xff31 /* Hangul start/stop(toggle) */
  lazy val XK_Hangul_Start: Long             = 0xff32 /* Hangul start */
  lazy val XK_Hangul_End: Long               = 0xff33 /* Hangul end, English start */
  lazy val XK_Hangul_Hanja: Long             = 0xff34 /* Start Hangul->Hanja Conversion */
  lazy val XK_Hangul_Jamo: Long              = 0xff35 /* Hangul Jamo mode */
  lazy val XK_Hangul_Romaja: Long            = 0xff36 /* Hangul Romaja mode */
  lazy val XK_Hangul_Codeinput: Long         = 0xff37 /* Hangul code input mode */
  lazy val XK_Hangul_Jeonja: Long            = 0xff38 /* Jeonja mode */
  lazy val XK_Hangul_Banja: Long             = 0xff39 /* Banja mode */
  lazy val XK_Hangul_PreHanja: Long          = 0xff3a /* Pre Hanja conversion */
  lazy val XK_Hangul_PostHanja: Long         = 0xff3b /* Post Hanja conversion */
  lazy val XK_Hangul_SingleCandidate: Long   = 0xff3c /* Single candidate */
  lazy val XK_Hangul_MultipleCandidate: Long = 0xff3d /* Multiple candidate */
  lazy val XK_Hangul_PreviousCandidate: Long = 0xff3e /* Previous candidate */
  lazy val XK_Hangul_Special: Long           = 0xff3f /* Special symbols */
  lazy val XK_Hangul_switch: Long            = 0xff7e /* Alias for mode_switch */

  /* Hangul Consonant Characters */
  lazy val XK_Hangul_Kiyeog: Long      = 0x0ea1
  lazy val XK_Hangul_SsangKiyeog: Long = 0x0ea2
  lazy val XK_Hangul_KiyeogSios: Long  = 0x0ea3
  lazy val XK_Hangul_Nieun: Long       = 0x0ea4
  lazy val XK_Hangul_NieunJieuj: Long  = 0x0ea5
  lazy val XK_Hangul_NieunHieuh: Long  = 0x0ea6
  lazy val XK_Hangul_Dikeud: Long      = 0x0ea7
  lazy val XK_Hangul_SsangDikeud: Long = 0x0ea8
  lazy val XK_Hangul_Rieul: Long       = 0x0ea9
  lazy val XK_Hangul_RieulKiyeog: Long = 0x0eaa
  lazy val XK_Hangul_RieulMieum: Long  = 0x0eab
  lazy val XK_Hangul_RieulPieub: Long  = 0x0eac
  lazy val XK_Hangul_RieulSios: Long   = 0x0ead
  lazy val XK_Hangul_RieulTieut: Long  = 0x0eae
  lazy val XK_Hangul_RieulPhieuf: Long = 0x0eaf
  lazy val XK_Hangul_RieulHieuh: Long  = 0x0eb0
  lazy val XK_Hangul_Mieum: Long       = 0x0eb1
  lazy val XK_Hangul_Pieub: Long       = 0x0eb2
  lazy val XK_Hangul_SsangPieub: Long  = 0x0eb3
  lazy val XK_Hangul_PieubSios: Long   = 0x0eb4
  lazy val XK_Hangul_Sios: Long        = 0x0eb5
  lazy val XK_Hangul_SsangSios: Long   = 0x0eb6
  lazy val XK_Hangul_Ieung: Long       = 0x0eb7
  lazy val XK_Hangul_Jieuj: Long       = 0x0eb8
  lazy val XK_Hangul_SsangJieuj: Long  = 0x0eb9
  lazy val XK_Hangul_Cieuc: Long       = 0x0eba
  lazy val XK_Hangul_Khieuq: Long      = 0x0ebb
  lazy val XK_Hangul_Tieut: Long       = 0x0ebc
  lazy val XK_Hangul_Phieuf: Long      = 0x0ebd
  lazy val XK_Hangul_Hieuh: Long       = 0x0ebe

  /* Hangul Vowel Characters */
  lazy val XK_Hangul_A: Long   = 0x0ebf
  lazy val XK_Hangul_AE: Long  = 0x0ec0
  lazy val XK_Hangul_YA: Long  = 0x0ec1
  lazy val XK_Hangul_YAE: Long = 0x0ec2
  lazy val XK_Hangul_EO: Long  = 0x0ec3
  lazy val XK_Hangul_E: Long   = 0x0ec4
  lazy val XK_Hangul_YEO: Long = 0x0ec5
  lazy val XK_Hangul_YE: Long  = 0x0ec6
  lazy val XK_Hangul_O: Long   = 0x0ec7
  lazy val XK_Hangul_WA: Long  = 0x0ec8
  lazy val XK_Hangul_WAE: Long = 0x0ec9
  lazy val XK_Hangul_OE: Long  = 0x0eca
  lazy val XK_Hangul_YO: Long  = 0x0ecb
  lazy val XK_Hangul_U: Long   = 0x0ecc
  lazy val XK_Hangul_WEO: Long = 0x0ecd
  lazy val XK_Hangul_WE: Long  = 0x0ece
  lazy val XK_Hangul_WI: Long  = 0x0ecf
  lazy val XK_Hangul_YU: Long  = 0x0ed0
  lazy val XK_Hangul_EU: Long  = 0x0ed1
  lazy val XK_Hangul_YI: Long  = 0x0ed2
  lazy val XK_Hangul_I: Long   = 0x0ed3

  /* Hangul syllable-final (JongSeong) Characters */
  lazy val XK_Hangul_J_Kiyeog: Long      = 0x0ed4
  lazy val XK_Hangul_J_SsangKiyeog: Long = 0x0ed5
  lazy val XK_Hangul_J_KiyeogSios: Long  = 0x0ed6
  lazy val XK_Hangul_J_Nieun: Long       = 0x0ed7
  lazy val XK_Hangul_J_NieunJieuj: Long  = 0x0ed8
  lazy val XK_Hangul_J_NieunHieuh: Long  = 0x0ed9
  lazy val XK_Hangul_J_Dikeud: Long      = 0x0eda
  lazy val XK_Hangul_J_Rieul: Long       = 0x0edb
  lazy val XK_Hangul_J_RieulKiyeog: Long = 0x0edc
  lazy val XK_Hangul_J_RieulMieum: Long  = 0x0edd
  lazy val XK_Hangul_J_RieulPieub: Long  = 0x0ede
  lazy val XK_Hangul_J_RieulSios: Long   = 0x0edf
  lazy val XK_Hangul_J_RieulTieut: Long  = 0x0ee0
  lazy val XK_Hangul_J_RieulPhieuf: Long = 0x0ee1
  lazy val XK_Hangul_J_RieulHieuh: Long  = 0x0ee2
  lazy val XK_Hangul_J_Mieum: Long       = 0x0ee3
  lazy val XK_Hangul_J_Pieub: Long       = 0x0ee4
  lazy val XK_Hangul_J_PieubSios: Long   = 0x0ee5
  lazy val XK_Hangul_J_Sios: Long        = 0x0ee6
  lazy val XK_Hangul_J_SsangSios: Long   = 0x0ee7
  lazy val XK_Hangul_J_Ieung: Long       = 0x0ee8
  lazy val XK_Hangul_J_Jieuj: Long       = 0x0ee9
  lazy val XK_Hangul_J_Cieuc: Long       = 0x0eea
  lazy val XK_Hangul_J_Khieuq: Long      = 0x0eeb
  lazy val XK_Hangul_J_Tieut: Long       = 0x0eec
  lazy val XK_Hangul_J_Phieuf: Long      = 0x0eed
  lazy val XK_Hangul_J_Hieuh: Long       = 0x0eee

  /* Ancient Hangul Consonant Characters */
  lazy val XK_Hangul_RieulYeorinHieuh: Long   = 0x0eef
  lazy val XK_Hangul_SunkyeongeumMieum: Long  = 0x0ef0
  lazy val XK_Hangul_SunkyeongeumPieub: Long  = 0x0ef1
  lazy val XK_Hangul_PanSios: Long            = 0x0ef2
  lazy val XK_Hangul_KkogjiDalrinIeung: Long  = 0x0ef3
  lazy val XK_Hangul_SunkyeongeumPhieuf: Long = 0x0ef4
  lazy val XK_Hangul_YeorinHieuh: Long        = 0x0ef5

  /* Ancient Hangul Vowel Characters */
  lazy val XK_Hangul_AraeA: Long  = 0x0ef6
  lazy val XK_Hangul_AraeAE: Long = 0x0ef7

  /* Ancient Hangul syllable-final (JongSeong) Characters */
  lazy val XK_Hangul_J_PanSios: Long           = 0x0ef8
  lazy val XK_Hangul_J_KkogjiDalrinIeung: Long = 0x0ef9
  lazy val XK_Hangul_J_YeorinHieuh: Long       = 0x0efa

  /* Korean currency symbol */
  lazy val XK_Korean_Won: Long = 0x0eff /*(U+20A9 WON SIGN)*/

  /*
   * Armenian
   */

  lazy val XK_Armenian_ligature_ew: Long     = 0x1000587 /* U+0587 ARMENIAN SMALL LIGATURE ECH YIWN */
  lazy val XK_Armenian_full_stop: Long       = 0x1000589 /* U+0589 ARMENIAN FULL STOP */
  lazy val XK_Armenian_verjaket: Long        = 0x1000589 /* U+0589 ARMENIAN FULL STOP */
  lazy val XK_Armenian_separation_mark: Long = 0x100055d /* U+055D ARMENIAN COMMA */
  lazy val XK_Armenian_but: Long             = 0x100055d /* U+055D ARMENIAN COMMA */
  lazy val XK_Armenian_hyphen: Long          = 0x100058a /* U+058A ARMENIAN HYPHEN */
  lazy val XK_Armenian_yentamna: Long        = 0x100058a /* U+058A ARMENIAN HYPHEN */
  lazy val XK_Armenian_exclam: Long          = 0x100055c /* U+055C ARMENIAN EXCLAMATION MARK */
  lazy val XK_Armenian_amanak: Long          = 0x100055c /* U+055C ARMENIAN EXCLAMATION MARK */
  lazy val XK_Armenian_accent: Long          = 0x100055b /* U+055B ARMENIAN EMPHASIS MARK */
  lazy val XK_Armenian_shesht: Long          = 0x100055b /* U+055B ARMENIAN EMPHASIS MARK */
  lazy val XK_Armenian_question: Long        = 0x100055e /* U+055E ARMENIAN QUESTION MARK */
  lazy val XK_Armenian_paruyk: Long          = 0x100055e /* U+055E ARMENIAN QUESTION MARK */
  lazy val XK_Armenian_AYB: Long             = 0x1000531 /* U+0531 ARMENIAN CAPITAL LETTER AYB */
  lazy val XK_Armenian_ayb: Long             = 0x1000561 /* U+0561 ARMENIAN SMALL LETTER AYB */
  lazy val XK_Armenian_BEN: Long             = 0x1000532 /* U+0532 ARMENIAN CAPITAL LETTER BEN */
  lazy val XK_Armenian_ben: Long             = 0x1000562 /* U+0562 ARMENIAN SMALL LETTER BEN */
  lazy val XK_Armenian_GIM: Long             = 0x1000533 /* U+0533 ARMENIAN CAPITAL LETTER GIM */
  lazy val XK_Armenian_gim: Long             = 0x1000563 /* U+0563 ARMENIAN SMALL LETTER GIM */
  lazy val XK_Armenian_DA: Long              = 0x1000534 /* U+0534 ARMENIAN CAPITAL LETTER DA */
  lazy val XK_Armenian_da: Long              = 0x1000564 /* U+0564 ARMENIAN SMALL LETTER DA */
  lazy val XK_Armenian_YECH: Long            = 0x1000535 /* U+0535 ARMENIAN CAPITAL LETTER ECH */
  lazy val XK_Armenian_yech: Long            = 0x1000565 /* U+0565 ARMENIAN SMALL LETTER ECH */
  lazy val XK_Armenian_ZA: Long              = 0x1000536 /* U+0536 ARMENIAN CAPITAL LETTER ZA */
  lazy val XK_Armenian_za: Long              = 0x1000566 /* U+0566 ARMENIAN SMALL LETTER ZA */
  lazy val XK_Armenian_E: Long               = 0x1000537 /* U+0537 ARMENIAN CAPITAL LETTER EH */
  lazy val XK_Armenian_e: Long               = 0x1000567 /* U+0567 ARMENIAN SMALL LETTER EH */
  lazy val XK_Armenian_AT: Long              = 0x1000538 /* U+0538 ARMENIAN CAPITAL LETTER ET */
  lazy val XK_Armenian_at: Long              = 0x1000568 /* U+0568 ARMENIAN SMALL LETTER ET */
  lazy val XK_Armenian_TO: Long              = 0x1000539 /* U+0539 ARMENIAN CAPITAL LETTER TO */
  lazy val XK_Armenian_to: Long              = 0x1000569 /* U+0569 ARMENIAN SMALL LETTER TO */
  lazy val XK_Armenian_ZHE: Long             = 0x100053a /* U+053A ARMENIAN CAPITAL LETTER ZHE */
  lazy val XK_Armenian_zhe: Long             = 0x100056a /* U+056A ARMENIAN SMALL LETTER ZHE */
  lazy val XK_Armenian_INI: Long             = 0x100053b /* U+053B ARMENIAN CAPITAL LETTER INI */
  lazy val XK_Armenian_ini: Long             = 0x100056b /* U+056B ARMENIAN SMALL LETTER INI */
  lazy val XK_Armenian_LYUN: Long            = 0x100053c /* U+053C ARMENIAN CAPITAL LETTER LIWN */
  lazy val XK_Armenian_lyun: Long            = 0x100056c /* U+056C ARMENIAN SMALL LETTER LIWN */
  lazy val XK_Armenian_KHE: Long             = 0x100053d /* U+053D ARMENIAN CAPITAL LETTER XEH */
  lazy val XK_Armenian_khe: Long             = 0x100056d /* U+056D ARMENIAN SMALL LETTER XEH */
  lazy val XK_Armenian_TSA: Long             = 0x100053e /* U+053E ARMENIAN CAPITAL LETTER CA */
  lazy val XK_Armenian_tsa: Long             = 0x100056e /* U+056E ARMENIAN SMALL LETTER CA */
  lazy val XK_Armenian_KEN: Long             = 0x100053f /* U+053F ARMENIAN CAPITAL LETTER KEN */
  lazy val XK_Armenian_ken: Long             = 0x100056f /* U+056F ARMENIAN SMALL LETTER KEN */
  lazy val XK_Armenian_HO: Long              = 0x1000540 /* U+0540 ARMENIAN CAPITAL LETTER HO */
  lazy val XK_Armenian_ho: Long              = 0x1000570 /* U+0570 ARMENIAN SMALL LETTER HO */
  lazy val XK_Armenian_DZA: Long             = 0x1000541 /* U+0541 ARMENIAN CAPITAL LETTER JA */
  lazy val XK_Armenian_dza: Long             = 0x1000571 /* U+0571 ARMENIAN SMALL LETTER JA */
  lazy val XK_Armenian_GHAT: Long            = 0x1000542 /* U+0542 ARMENIAN CAPITAL LETTER GHAD */
  lazy val XK_Armenian_ghat: Long            = 0x1000572 /* U+0572 ARMENIAN SMALL LETTER GHAD */
  lazy val XK_Armenian_TCHE: Long            = 0x1000543 /* U+0543 ARMENIAN CAPITAL LETTER CHEH */
  lazy val XK_Armenian_tche: Long            = 0x1000573 /* U+0573 ARMENIAN SMALL LETTER CHEH */
  lazy val XK_Armenian_MEN: Long             = 0x1000544 /* U+0544 ARMENIAN CAPITAL LETTER MEN */
  lazy val XK_Armenian_men: Long             = 0x1000574 /* U+0574 ARMENIAN SMALL LETTER MEN */
  lazy val XK_Armenian_HI: Long              = 0x1000545 /* U+0545 ARMENIAN CAPITAL LETTER YI */
  lazy val XK_Armenian_hi: Long              = 0x1000575 /* U+0575 ARMENIAN SMALL LETTER YI */
  lazy val XK_Armenian_NU: Long              = 0x1000546 /* U+0546 ARMENIAN CAPITAL LETTER NOW */
  lazy val XK_Armenian_nu: Long              = 0x1000576 /* U+0576 ARMENIAN SMALL LETTER NOW */
  lazy val XK_Armenian_SHA: Long             = 0x1000547 /* U+0547 ARMENIAN CAPITAL LETTER SHA */
  lazy val XK_Armenian_sha: Long             = 0x1000577 /* U+0577 ARMENIAN SMALL LETTER SHA */
  lazy val XK_Armenian_VO: Long              = 0x1000548 /* U+0548 ARMENIAN CAPITAL LETTER VO */
  lazy val XK_Armenian_vo: Long              = 0x1000578 /* U+0578 ARMENIAN SMALL LETTER VO */
  lazy val XK_Armenian_CHA: Long             = 0x1000549 /* U+0549 ARMENIAN CAPITAL LETTER CHA */
  lazy val XK_Armenian_cha: Long             = 0x1000579 /* U+0579 ARMENIAN SMALL LETTER CHA */
  lazy val XK_Armenian_PE: Long              = 0x100054a /* U+054A ARMENIAN CAPITAL LETTER PEH */
  lazy val XK_Armenian_pe: Long              = 0x100057a /* U+057A ARMENIAN SMALL LETTER PEH */
  lazy val XK_Armenian_JE: Long              = 0x100054b /* U+054B ARMENIAN CAPITAL LETTER JHEH */
  lazy val XK_Armenian_je: Long              = 0x100057b /* U+057B ARMENIAN SMALL LETTER JHEH */
  lazy val XK_Armenian_RA: Long              = 0x100054c /* U+054C ARMENIAN CAPITAL LETTER RA */
  lazy val XK_Armenian_ra: Long              = 0x100057c /* U+057C ARMENIAN SMALL LETTER RA */
  lazy val XK_Armenian_SE: Long              = 0x100054d /* U+054D ARMENIAN CAPITAL LETTER SEH */
  lazy val XK_Armenian_se: Long              = 0x100057d /* U+057D ARMENIAN SMALL LETTER SEH */
  lazy val XK_Armenian_VEV: Long             = 0x100054e /* U+054E ARMENIAN CAPITAL LETTER VEW */
  lazy val XK_Armenian_vev: Long             = 0x100057e /* U+057E ARMENIAN SMALL LETTER VEW */
  lazy val XK_Armenian_TYUN: Long            = 0x100054f /* U+054F ARMENIAN CAPITAL LETTER TIWN */
  lazy val XK_Armenian_tyun: Long            = 0x100057f /* U+057F ARMENIAN SMALL LETTER TIWN */
  lazy val XK_Armenian_RE: Long              = 0x1000550 /* U+0550 ARMENIAN CAPITAL LETTER REH */
  lazy val XK_Armenian_re: Long              = 0x1000580 /* U+0580 ARMENIAN SMALL LETTER REH */
  lazy val XK_Armenian_TSO: Long             = 0x1000551 /* U+0551 ARMENIAN CAPITAL LETTER CO */
  lazy val XK_Armenian_tso: Long             = 0x1000581 /* U+0581 ARMENIAN SMALL LETTER CO */
  lazy val XK_Armenian_VYUN: Long            = 0x1000552 /* U+0552 ARMENIAN CAPITAL LETTER YIWN */
  lazy val XK_Armenian_vyun: Long            = 0x1000582 /* U+0582 ARMENIAN SMALL LETTER YIWN */
  lazy val XK_Armenian_PYUR: Long            = 0x1000553 /* U+0553 ARMENIAN CAPITAL LETTER PIWR */
  lazy val XK_Armenian_pyur: Long            = 0x1000583 /* U+0583 ARMENIAN SMALL LETTER PIWR */
  lazy val XK_Armenian_KE: Long              = 0x1000554 /* U+0554 ARMENIAN CAPITAL LETTER KEH */
  lazy val XK_Armenian_ke: Long              = 0x1000584 /* U+0584 ARMENIAN SMALL LETTER KEH */
  lazy val XK_Armenian_O: Long               = 0x1000555 /* U+0555 ARMENIAN CAPITAL LETTER OH */
  lazy val XK_Armenian_o: Long               = 0x1000585 /* U+0585 ARMENIAN SMALL LETTER OH */
  lazy val XK_Armenian_FE: Long              = 0x1000556 /* U+0556 ARMENIAN CAPITAL LETTER FEH */
  lazy val XK_Armenian_fe: Long              = 0x1000586 /* U+0586 ARMENIAN SMALL LETTER FEH */
  lazy val XK_Armenian_apostrophe: Long      = 0x100055a /* U+055A ARMENIAN APOSTROPHE */

  /*
   * Georgian
   */

  lazy val XK_Georgian_an: Long   = 0x10010d0 /* U+10D0 GEORGIAN LETTER AN */
  lazy val XK_Georgian_ban: Long  = 0x10010d1 /* U+10D1 GEORGIAN LETTER BAN */
  lazy val XK_Georgian_gan: Long  = 0x10010d2 /* U+10D2 GEORGIAN LETTER GAN */
  lazy val XK_Georgian_don: Long  = 0x10010d3 /* U+10D3 GEORGIAN LETTER DON */
  lazy val XK_Georgian_en: Long   = 0x10010d4 /* U+10D4 GEORGIAN LETTER EN */
  lazy val XK_Georgian_vin: Long  = 0x10010d5 /* U+10D5 GEORGIAN LETTER VIN */
  lazy val XK_Georgian_zen: Long  = 0x10010d6 /* U+10D6 GEORGIAN LETTER ZEN */
  lazy val XK_Georgian_tan: Long  = 0x10010d7 /* U+10D7 GEORGIAN LETTER TAN */
  lazy val XK_Georgian_in: Long   = 0x10010d8 /* U+10D8 GEORGIAN LETTER IN */
  lazy val XK_Georgian_kan: Long  = 0x10010d9 /* U+10D9 GEORGIAN LETTER KAN */
  lazy val XK_Georgian_las: Long  = 0x10010da /* U+10DA GEORGIAN LETTER LAS */
  lazy val XK_Georgian_man: Long  = 0x10010db /* U+10DB GEORGIAN LETTER MAN */
  lazy val XK_Georgian_nar: Long  = 0x10010dc /* U+10DC GEORGIAN LETTER NAR */
  lazy val XK_Georgian_on: Long   = 0x10010dd /* U+10DD GEORGIAN LETTER ON */
  lazy val XK_Georgian_par: Long  = 0x10010de /* U+10DE GEORGIAN LETTER PAR */
  lazy val XK_Georgian_zhar: Long = 0x10010df /* U+10DF GEORGIAN LETTER ZHAR */
  lazy val XK_Georgian_rae: Long  = 0x10010e0 /* U+10E0 GEORGIAN LETTER RAE */
  lazy val XK_Georgian_san: Long  = 0x10010e1 /* U+10E1 GEORGIAN LETTER SAN */
  lazy val XK_Georgian_tar: Long  = 0x10010e2 /* U+10E2 GEORGIAN LETTER TAR */
  lazy val XK_Georgian_un: Long   = 0x10010e3 /* U+10E3 GEORGIAN LETTER UN */
  lazy val XK_Georgian_phar: Long = 0x10010e4 /* U+10E4 GEORGIAN LETTER PHAR */
  lazy val XK_Georgian_khar: Long = 0x10010e5 /* U+10E5 GEORGIAN LETTER KHAR */
  lazy val XK_Georgian_ghan: Long = 0x10010e6 /* U+10E6 GEORGIAN LETTER GHAN */
  lazy val XK_Georgian_qar: Long  = 0x10010e7 /* U+10E7 GEORGIAN LETTER QAR */
  lazy val XK_Georgian_shin: Long = 0x10010e8 /* U+10E8 GEORGIAN LETTER SHIN */
  lazy val XK_Georgian_chin: Long = 0x10010e9 /* U+10E9 GEORGIAN LETTER CHIN */
  lazy val XK_Georgian_can: Long  = 0x10010ea /* U+10EA GEORGIAN LETTER CAN */
  lazy val XK_Georgian_jil: Long  = 0x10010eb /* U+10EB GEORGIAN LETTER JIL */
  lazy val XK_Georgian_cil: Long  = 0x10010ec /* U+10EC GEORGIAN LETTER CIL */
  lazy val XK_Georgian_char: Long = 0x10010ed /* U+10ED GEORGIAN LETTER CHAR */
  lazy val XK_Georgian_xan: Long  = 0x10010ee /* U+10EE GEORGIAN LETTER XAN */
  lazy val XK_Georgian_jhan: Long = 0x10010ef /* U+10EF GEORGIAN LETTER JHAN */
  lazy val XK_Georgian_hae: Long  = 0x10010f0 /* U+10F0 GEORGIAN LETTER HAE */
  lazy val XK_Georgian_he: Long   = 0x10010f1 /* U+10F1 GEORGIAN LETTER HE */
  lazy val XK_Georgian_hie: Long  = 0x10010f2 /* U+10F2 GEORGIAN LETTER HIE */
  lazy val XK_Georgian_we: Long   = 0x10010f3 /* U+10F3 GEORGIAN LETTER WE */
  lazy val XK_Georgian_har: Long  = 0x10010f4 /* U+10F4 GEORGIAN LETTER HAR */
  lazy val XK_Georgian_hoe: Long  = 0x10010f5 /* U+10F5 GEORGIAN LETTER HOE */
  lazy val XK_Georgian_fi: Long   = 0x10010f6 /* U+10F6 GEORGIAN LETTER FI */

  /*
   * Azeri (and other Turkic or Caucasian languages)
   */

  /* latin */
  lazy val XK_Xabovedot: Long = 0x1001e8a /* U+1E8A LATIN CAPITAL LETTER X WITH DOT ABOVE */
  lazy val XK_Ibreve: Long    = 0x100012c /* U+012C LATIN CAPITAL LETTER I WITH BREVE */
  lazy val XK_Zstroke: Long   = 0x10001b5 /* U+01B5 LATIN CAPITAL LETTER Z WITH STROKE */
  lazy val XK_Gcaron: Long    = 0x10001e6 /* U+01E6 LATIN CAPITAL LETTER G WITH CARON */
  lazy val XK_Ocaron: Long    = 0x10001d1 /* U+01D1 LATIN CAPITAL LETTER O WITH CARON */
  lazy val XK_Obarred: Long   = 0x100019f /* U+019F LATIN CAPITAL LETTER O WITH MIDDLE TILDE */
  lazy val XK_xabovedot: Long = 0x1001e8b /* U+1E8B LATIN SMALL LETTER X WITH DOT ABOVE */
  lazy val XK_ibreve: Long    = 0x100012d /* U+012D LATIN SMALL LETTER I WITH BREVE */
  lazy val XK_zstroke: Long   = 0x10001b6 /* U+01B6 LATIN SMALL LETTER Z WITH STROKE */
  lazy val XK_gcaron: Long    = 0x10001e7 /* U+01E7 LATIN SMALL LETTER G WITH CARON */
  lazy val XK_ocaron: Long    = 0x10001d2 /* U+01D2 LATIN SMALL LETTER O WITH CARON */
  lazy val XK_obarred: Long   = 0x1000275 /* U+0275 LATIN SMALL LETTER BARRED O */
  lazy val XK_SCHWA: Long     = 0x100018f /* U+018F LATIN CAPITAL LETTER SCHWA */
  lazy val XK_schwa: Long     = 0x1000259 /* U+0259 LATIN SMALL LETTER SCHWA */
  lazy val XK_EZH: Long       = 0x10001b7 /* U+01B7 LATIN CAPITAL LETTER EZH */
  lazy val XK_ezh: Long       = 0x1000292 /* U+0292 LATIN SMALL LETTER EZH */
  /* those are not really Caucasus */
  /* For Inupiak */
  lazy val XK_Lbelowdot: Long = 0x1001e36 /* U+1E36 LATIN CAPITAL LETTER L WITH DOT BELOW */
  lazy val XK_lbelowdot: Long = 0x1001e37 /* U+1E37 LATIN SMALL LETTER L WITH DOT BELOW */

  /*
   * Vietnamese
   */

  lazy val XK_Abelowdot: Long           = 0x1001ea0 /* U+1EA0 LATIN CAPITAL LETTER A WITH DOT BELOW */
  lazy val XK_abelowdot: Long           = 0x1001ea1 /* U+1EA1 LATIN SMALL LETTER A WITH DOT BELOW */
  lazy val XK_Ahook: Long               = 0x1001ea2 /* U+1EA2 LATIN CAPITAL LETTER A WITH HOOK ABOVE */
  lazy val XK_ahook: Long               = 0x1001ea3 /* U+1EA3 LATIN SMALL LETTER A WITH HOOK ABOVE */
  lazy val XK_Acircumflexacute: Long    = 0x1001ea4 /* U+1EA4 LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE */
  lazy val XK_acircumflexacute: Long    = 0x1001ea5 /* U+1EA5 LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE */
  lazy val XK_Acircumflexgrave: Long    = 0x1001ea6 /* U+1EA6 LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE */
  lazy val XK_acircumflexgrave: Long    = 0x1001ea7 /* U+1EA7 LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE */
  lazy val XK_Acircumflexhook: Long     = 0x1001ea8 /* U+1EA8 LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE */
  lazy val XK_acircumflexhook: Long     = 0x1001ea9 /* U+1EA9 LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE */
  lazy val XK_Acircumflextilde: Long    = 0x1001eaa /* U+1EAA LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE */
  lazy val XK_acircumflextilde: Long    = 0x1001eab /* U+1EAB LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE */
  lazy val XK_Acircumflexbelowdot: Long = 0x1001eac /* U+1EAC LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW */
  lazy val XK_acircumflexbelowdot: Long = 0x1001ead /* U+1EAD LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW */
  lazy val XK_Abreveacute: Long         = 0x1001eae /* U+1EAE LATIN CAPITAL LETTER A WITH BREVE AND ACUTE */
  lazy val XK_abreveacute: Long         = 0x1001eaf /* U+1EAF LATIN SMALL LETTER A WITH BREVE AND ACUTE */
  lazy val XK_Abrevegrave: Long         = 0x1001eb0 /* U+1EB0 LATIN CAPITAL LETTER A WITH BREVE AND GRAVE */
  lazy val XK_abrevegrave: Long         = 0x1001eb1 /* U+1EB1 LATIN SMALL LETTER A WITH BREVE AND GRAVE */
  lazy val XK_Abrevehook: Long          = 0x1001eb2 /* U+1EB2 LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE */
  lazy val XK_abrevehook: Long          = 0x1001eb3 /* U+1EB3 LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE */
  lazy val XK_Abrevetilde: Long         = 0x1001eb4 /* U+1EB4 LATIN CAPITAL LETTER A WITH BREVE AND TILDE */
  lazy val XK_abrevetilde: Long         = 0x1001eb5 /* U+1EB5 LATIN SMALL LETTER A WITH BREVE AND TILDE */
  lazy val XK_Abrevebelowdot: Long      = 0x1001eb6 /* U+1EB6 LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW */
  lazy val XK_abrevebelowdot: Long      = 0x1001eb7 /* U+1EB7 LATIN SMALL LETTER A WITH BREVE AND DOT BELOW */
  lazy val XK_Ebelowdot: Long           = 0x1001eb8 /* U+1EB8 LATIN CAPITAL LETTER E WITH DOT BELOW */
  lazy val XK_ebelowdot: Long           = 0x1001eb9 /* U+1EB9 LATIN SMALL LETTER E WITH DOT BELOW */
  lazy val XK_Ehook: Long               = 0x1001eba /* U+1EBA LATIN CAPITAL LETTER E WITH HOOK ABOVE */
  lazy val XK_ehook: Long               = 0x1001ebb /* U+1EBB LATIN SMALL LETTER E WITH HOOK ABOVE */
  lazy val XK_Etilde: Long              = 0x1001ebc /* U+1EBC LATIN CAPITAL LETTER E WITH TILDE */
  lazy val XK_etilde: Long              = 0x1001ebd /* U+1EBD LATIN SMALL LETTER E WITH TILDE */
  lazy val XK_Ecircumflexacute: Long    = 0x1001ebe /* U+1EBE LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE */
  lazy val XK_ecircumflexacute: Long    = 0x1001ebf /* U+1EBF LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE */
  lazy val XK_Ecircumflexgrave: Long    = 0x1001ec0 /* U+1EC0 LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE */
  lazy val XK_ecircumflexgrave: Long    = 0x1001ec1 /* U+1EC1 LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE */
  lazy val XK_Ecircumflexhook: Long     = 0x1001ec2 /* U+1EC2 LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE */
  lazy val XK_ecircumflexhook: Long     = 0x1001ec3 /* U+1EC3 LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE */
  lazy val XK_Ecircumflextilde: Long    = 0x1001ec4 /* U+1EC4 LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE */
  lazy val XK_ecircumflextilde: Long    = 0x1001ec5 /* U+1EC5 LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE */
  lazy val XK_Ecircumflexbelowdot: Long = 0x1001ec6 /* U+1EC6 LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW */
  lazy val XK_ecircumflexbelowdot: Long = 0x1001ec7 /* U+1EC7 LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW */
  lazy val XK_Ihook: Long               = 0x1001ec8 /* U+1EC8 LATIN CAPITAL LETTER I WITH HOOK ABOVE */
  lazy val XK_ihook: Long               = 0x1001ec9 /* U+1EC9 LATIN SMALL LETTER I WITH HOOK ABOVE */
  lazy val XK_Ibelowdot: Long           = 0x1001eca /* U+1ECA LATIN CAPITAL LETTER I WITH DOT BELOW */
  lazy val XK_ibelowdot: Long           = 0x1001ecb /* U+1ECB LATIN SMALL LETTER I WITH DOT BELOW */
  lazy val XK_Obelowdot: Long           = 0x1001ecc /* U+1ECC LATIN CAPITAL LETTER O WITH DOT BELOW */
  lazy val XK_obelowdot: Long           = 0x1001ecd /* U+1ECD LATIN SMALL LETTER O WITH DOT BELOW */
  lazy val XK_Ohook: Long               = 0x1001ece /* U+1ECE LATIN CAPITAL LETTER O WITH HOOK ABOVE */
  lazy val XK_ohook: Long               = 0x1001ecf /* U+1ECF LATIN SMALL LETTER O WITH HOOK ABOVE */
  lazy val XK_Ocircumflexacute: Long    = 0x1001ed0 /* U+1ED0 LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE */
  lazy val XK_ocircumflexacute: Long    = 0x1001ed1 /* U+1ED1 LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE */
  lazy val XK_Ocircumflexgrave: Long    = 0x1001ed2 /* U+1ED2 LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE */
  lazy val XK_ocircumflexgrave: Long    = 0x1001ed3 /* U+1ED3 LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE */
  lazy val XK_Ocircumflexhook: Long     = 0x1001ed4 /* U+1ED4 LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE */
  lazy val XK_ocircumflexhook: Long     = 0x1001ed5 /* U+1ED5 LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE */
  lazy val XK_Ocircumflextilde: Long    = 0x1001ed6 /* U+1ED6 LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE */
  lazy val XK_ocircumflextilde: Long    = 0x1001ed7 /* U+1ED7 LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE */
  lazy val XK_Ocircumflexbelowdot: Long = 0x1001ed8 /* U+1ED8 LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW */
  lazy val XK_ocircumflexbelowdot: Long = 0x1001ed9 /* U+1ED9 LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW */
  lazy val XK_Ohornacute: Long          = 0x1001eda /* U+1EDA LATIN CAPITAL LETTER O WITH HORN AND ACUTE */
  lazy val XK_ohornacute: Long          = 0x1001edb /* U+1EDB LATIN SMALL LETTER O WITH HORN AND ACUTE */
  lazy val XK_Ohorngrave: Long          = 0x1001edc /* U+1EDC LATIN CAPITAL LETTER O WITH HORN AND GRAVE */
  lazy val XK_ohorngrave: Long          = 0x1001edd /* U+1EDD LATIN SMALL LETTER O WITH HORN AND GRAVE */
  lazy val XK_Ohornhook: Long           = 0x1001ede /* U+1EDE LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE */
  lazy val XK_ohornhook: Long           = 0x1001edf /* U+1EDF LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE */
  lazy val XK_Ohorntilde: Long          = 0x1001ee0 /* U+1EE0 LATIN CAPITAL LETTER O WITH HORN AND TILDE */
  lazy val XK_ohorntilde: Long          = 0x1001ee1 /* U+1EE1 LATIN SMALL LETTER O WITH HORN AND TILDE */
  lazy val XK_Ohornbelowdot: Long       = 0x1001ee2 /* U+1EE2 LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW */
  lazy val XK_ohornbelowdot: Long       = 0x1001ee3 /* U+1EE3 LATIN SMALL LETTER O WITH HORN AND DOT BELOW */
  lazy val XK_Ubelowdot: Long           = 0x1001ee4 /* U+1EE4 LATIN CAPITAL LETTER U WITH DOT BELOW */
  lazy val XK_ubelowdot: Long           = 0x1001ee5 /* U+1EE5 LATIN SMALL LETTER U WITH DOT BELOW */
  lazy val XK_Uhook: Long               = 0x1001ee6 /* U+1EE6 LATIN CAPITAL LETTER U WITH HOOK ABOVE */
  lazy val XK_uhook: Long               = 0x1001ee7 /* U+1EE7 LATIN SMALL LETTER U WITH HOOK ABOVE */
  lazy val XK_Uhornacute: Long          = 0x1001ee8 /* U+1EE8 LATIN CAPITAL LETTER U WITH HORN AND ACUTE */
  lazy val XK_uhornacute: Long          = 0x1001ee9 /* U+1EE9 LATIN SMALL LETTER U WITH HORN AND ACUTE */
  lazy val XK_Uhorngrave: Long          = 0x1001eea /* U+1EEA LATIN CAPITAL LETTER U WITH HORN AND GRAVE */
  lazy val XK_uhorngrave: Long          = 0x1001eeb /* U+1EEB LATIN SMALL LETTER U WITH HORN AND GRAVE */
  lazy val XK_Uhornhook: Long           = 0x1001eec /* U+1EEC LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE */
  lazy val XK_uhornhook: Long           = 0x1001eed /* U+1EED LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE */
  lazy val XK_Uhorntilde: Long          = 0x1001eee /* U+1EEE LATIN CAPITAL LETTER U WITH HORN AND TILDE */
  lazy val XK_uhorntilde: Long          = 0x1001eef /* U+1EEF LATIN SMALL LETTER U WITH HORN AND TILDE */
  lazy val XK_Uhornbelowdot: Long       = 0x1001ef0 /* U+1EF0 LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW */
  lazy val XK_uhornbelowdot: Long       = 0x1001ef1 /* U+1EF1 LATIN SMALL LETTER U WITH HORN AND DOT BELOW */
  lazy val XK_Ybelowdot: Long           = 0x1001ef4 /* U+1EF4 LATIN CAPITAL LETTER Y WITH DOT BELOW */
  lazy val XK_ybelowdot: Long           = 0x1001ef5 /* U+1EF5 LATIN SMALL LETTER Y WITH DOT BELOW */
  lazy val XK_Yhook: Long               = 0x1001ef6 /* U+1EF6 LATIN CAPITAL LETTER Y WITH HOOK ABOVE */
  lazy val XK_yhook: Long               = 0x1001ef7 /* U+1EF7 LATIN SMALL LETTER Y WITH HOOK ABOVE */
  lazy val XK_Ytilde: Long              = 0x1001ef8 /* U+1EF8 LATIN CAPITAL LETTER Y WITH TILDE */
  lazy val XK_ytilde: Long              = 0x1001ef9 /* U+1EF9 LATIN SMALL LETTER Y WITH TILDE */
  lazy val XK_Ohorn: Long               = 0x10001a0 /* U+01A0 LATIN CAPITAL LETTER O WITH HORN */
  lazy val XK_ohorn: Long               = 0x10001a1 /* U+01A1 LATIN SMALL LETTER O WITH HORN */
  lazy val XK_Uhorn: Long               = 0x10001af /* U+01AF LATIN CAPITAL LETTER U WITH HORN */
  lazy val XK_uhorn: Long               = 0x10001b0 /* U+01B0 LATIN SMALL LETTER U WITH HORN */

  lazy val XK_EcuSign: Long       = 0x10020a0 /* U+20A0 EURO-CURRENCY SIGN */
  lazy val XK_ColonSign: Long     = 0x10020a1 /* U+20A1 COLON SIGN */
  lazy val XK_CruzeiroSign: Long  = 0x10020a2 /* U+20A2 CRUZEIRO SIGN */
  lazy val XK_FFrancSign: Long    = 0x10020a3 /* U+20A3 FRENCH FRANC SIGN */
  lazy val XK_LiraSign: Long      = 0x10020a4 /* U+20A4 LIRA SIGN */
  lazy val XK_MillSign: Long      = 0x10020a5 /* U+20A5 MILL SIGN */
  lazy val XK_NairaSign: Long     = 0x10020a6 /* U+20A6 NAIRA SIGN */
  lazy val XK_PesetaSign: Long    = 0x10020a7 /* U+20A7 PESETA SIGN */
  lazy val XK_RupeeSign: Long     = 0x10020a8 /* U+20A8 RUPEE SIGN */
  lazy val XK_WonSign: Long       = 0x10020a9 /* U+20A9 WON SIGN */
  lazy val XK_NewSheqelSign: Long = 0x10020aa /* U+20AA NEW SHEQEL SIGN */
  lazy val XK_DongSign: Long      = 0x10020ab /* U+20AB DONG SIGN */
  lazy val XK_EuroSign: Long      = 0x20ac /* U+20AC EURO SIGN */

  /* one, two and three are defined above. */
  lazy val XK_zerosuperior: Long     = 0x1002070 /* U+2070 SUPERSCRIPT ZERO */
  lazy val XK_foursuperior: Long     = 0x1002074 /* U+2074 SUPERSCRIPT FOUR */
  lazy val XK_fivesuperior: Long     = 0x1002075 /* U+2075 SUPERSCRIPT FIVE */
  lazy val XK_sixsuperior: Long      = 0x1002076 /* U+2076 SUPERSCRIPT SIX */
  lazy val XK_sevensuperior: Long    = 0x1002077 /* U+2077 SUPERSCRIPT SEVEN */
  lazy val XK_eightsuperior: Long    = 0x1002078 /* U+2078 SUPERSCRIPT EIGHT */
  lazy val XK_ninesuperior: Long     = 0x1002079 /* U+2079 SUPERSCRIPT NINE */
  lazy val XK_zerosubscript: Long    = 0x1002080 /* U+2080 SUBSCRIPT ZERO */
  lazy val XK_onesubscript: Long     = 0x1002081 /* U+2081 SUBSCRIPT ONE */
  lazy val XK_twosubscript: Long     = 0x1002082 /* U+2082 SUBSCRIPT TWO */
  lazy val XK_threesubscript: Long   = 0x1002083 /* U+2083 SUBSCRIPT THREE */
  lazy val XK_foursubscript: Long    = 0x1002084 /* U+2084 SUBSCRIPT FOUR */
  lazy val XK_fivesubscript: Long    = 0x1002085 /* U+2085 SUBSCRIPT FIVE */
  lazy val XK_sixsubscript: Long     = 0x1002086 /* U+2086 SUBSCRIPT SIX */
  lazy val XK_sevensubscript: Long   = 0x1002087 /* U+2087 SUBSCRIPT SEVEN */
  lazy val XK_eightsubscript: Long   = 0x1002088 /* U+2088 SUBSCRIPT EIGHT */
  lazy val XK_ninesubscript: Long    = 0x1002089 /* U+2089 SUBSCRIPT NINE */
  lazy val XK_partdifferential: Long = 0x1002202 /* U+2202 PARTIAL DIFFERENTIAL */
  lazy val XK_emptyset: Long         = 0x1002205 /* U+2205 NULL SET */
  lazy val XK_elementof: Long        = 0x1002208 /* U+2208 ELEMENT OF */
  lazy val XK_notelementof: Long     = 0x1002209 /* U+2209 NOT AN ELEMENT OF */
  lazy val XK_containsas: Long       = 0x100220B /* U+220B CONTAINS AS MEMBER */
  lazy val XK_squareroot: Long       = 0x100221A /* U+221A SQUARE ROOT */
  lazy val XK_cuberoot: Long         = 0x100221B /* U+221B CUBE ROOT */
  lazy val XK_fourthroot: Long       = 0x100221C /* U+221C FOURTH ROOT */
  lazy val XK_dintegral: Long        = 0x100222C /* U+222C DOUBLE INTEGRAL */
  lazy val XK_tintegral: Long        = 0x100222D /* U+222D TRIPLE INTEGRAL */
  lazy val XK_because: Long          = 0x1002235 /* U+2235 BECAUSE */
  lazy val XK_approxeq: Long         = 0x1002248 /* U+2245 ALMOST EQUAL TO */
  lazy val XK_notapproxeq: Long      = 0x1002247 /* U+2247 NOT ALMOST EQUAL TO */
  lazy val XK_notidentical: Long     = 0x1002262 /* U+2262 NOT IDENTICAL TO */
  lazy val XK_stricteq: Long         = 0x1002263 /* U+2263 STRICTLY EQUIVALENT TO */

  lazy val XK_braille_dot_1: Long         = 0xfff1
  lazy val XK_braille_dot_2: Long         = 0xfff2
  lazy val XK_braille_dot_3: Long         = 0xfff3
  lazy val XK_braille_dot_4: Long         = 0xfff4
  lazy val XK_braille_dot_5: Long         = 0xfff5
  lazy val XK_braille_dot_6: Long         = 0xfff6
  lazy val XK_braille_dot_7: Long         = 0xfff7
  lazy val XK_braille_dot_8: Long         = 0xfff8
  lazy val XK_braille_dot_9: Long         = 0xfff9
  lazy val XK_braille_dot_10: Long        = 0xfffa
  lazy val XK_braille_blank: Long         = 0x1002800 /* U+2800 BRAILLE PATTERN BLANK */
  lazy val XK_braille_dots_1: Long        = 0x1002801 /* U+2801 BRAILLE PATTERN DOTS-1 */
  lazy val XK_braille_dots_2: Long        = 0x1002802 /* U+2802 BRAILLE PATTERN DOTS-2 */
  lazy val XK_braille_dots_12: Long       = 0x1002803 /* U+2803 BRAILLE PATTERN DOTS-12 */
  lazy val XK_braille_dots_3: Long        = 0x1002804 /* U+2804 BRAILLE PATTERN DOTS-3 */
  lazy val XK_braille_dots_13: Long       = 0x1002805 /* U+2805 BRAILLE PATTERN DOTS-13 */
  lazy val XK_braille_dots_23: Long       = 0x1002806 /* U+2806 BRAILLE PATTERN DOTS-23 */
  lazy val XK_braille_dots_123: Long      = 0x1002807 /* U+2807 BRAILLE PATTERN DOTS-123 */
  lazy val XK_braille_dots_4: Long        = 0x1002808 /* U+2808 BRAILLE PATTERN DOTS-4 */
  lazy val XK_braille_dots_14: Long       = 0x1002809 /* U+2809 BRAILLE PATTERN DOTS-14 */
  lazy val XK_braille_dots_24: Long       = 0x100280a /* U+280a BRAILLE PATTERN DOTS-24 */
  lazy val XK_braille_dots_124: Long      = 0x100280b /* U+280b BRAILLE PATTERN DOTS-124 */
  lazy val XK_braille_dots_34: Long       = 0x100280c /* U+280c BRAILLE PATTERN DOTS-34 */
  lazy val XK_braille_dots_134: Long      = 0x100280d /* U+280d BRAILLE PATTERN DOTS-134 */
  lazy val XK_braille_dots_234: Long      = 0x100280e /* U+280e BRAILLE PATTERN DOTS-234 */
  lazy val XK_braille_dots_1234: Long     = 0x100280f /* U+280f BRAILLE PATTERN DOTS-1234 */
  lazy val XK_braille_dots_5: Long        = 0x1002810 /* U+2810 BRAILLE PATTERN DOTS-5 */
  lazy val XK_braille_dots_15: Long       = 0x1002811 /* U+2811 BRAILLE PATTERN DOTS-15 */
  lazy val XK_braille_dots_25: Long       = 0x1002812 /* U+2812 BRAILLE PATTERN DOTS-25 */
  lazy val XK_braille_dots_125: Long      = 0x1002813 /* U+2813 BRAILLE PATTERN DOTS-125 */
  lazy val XK_braille_dots_35: Long       = 0x1002814 /* U+2814 BRAILLE PATTERN DOTS-35 */
  lazy val XK_braille_dots_135: Long      = 0x1002815 /* U+2815 BRAILLE PATTERN DOTS-135 */
  lazy val XK_braille_dots_235: Long      = 0x1002816 /* U+2816 BRAILLE PATTERN DOTS-235 */
  lazy val XK_braille_dots_1235: Long     = 0x1002817 /* U+2817 BRAILLE PATTERN DOTS-1235 */
  lazy val XK_braille_dots_45: Long       = 0x1002818 /* U+2818 BRAILLE PATTERN DOTS-45 */
  lazy val XK_braille_dots_145: Long      = 0x1002819 /* U+2819 BRAILLE PATTERN DOTS-145 */
  lazy val XK_braille_dots_245: Long      = 0x100281a /* U+281a BRAILLE PATTERN DOTS-245 */
  lazy val XK_braille_dots_1245: Long     = 0x100281b /* U+281b BRAILLE PATTERN DOTS-1245 */
  lazy val XK_braille_dots_345: Long      = 0x100281c /* U+281c BRAILLE PATTERN DOTS-345 */
  lazy val XK_braille_dots_1345: Long     = 0x100281d /* U+281d BRAILLE PATTERN DOTS-1345 */
  lazy val XK_braille_dots_2345: Long     = 0x100281e /* U+281e BRAILLE PATTERN DOTS-2345 */
  lazy val XK_braille_dots_12345: Long    = 0x100281f /* U+281f BRAILLE PATTERN DOTS-12345 */
  lazy val XK_braille_dots_6: Long        = 0x1002820 /* U+2820 BRAILLE PATTERN DOTS-6 */
  lazy val XK_braille_dots_16: Long       = 0x1002821 /* U+2821 BRAILLE PATTERN DOTS-16 */
  lazy val XK_braille_dots_26: Long       = 0x1002822 /* U+2822 BRAILLE PATTERN DOTS-26 */
  lazy val XK_braille_dots_126: Long      = 0x1002823 /* U+2823 BRAILLE PATTERN DOTS-126 */
  lazy val XK_braille_dots_36: Long       = 0x1002824 /* U+2824 BRAILLE PATTERN DOTS-36 */
  lazy val XK_braille_dots_136: Long      = 0x1002825 /* U+2825 BRAILLE PATTERN DOTS-136 */
  lazy val XK_braille_dots_236: Long      = 0x1002826 /* U+2826 BRAILLE PATTERN DOTS-236 */
  lazy val XK_braille_dots_1236: Long     = 0x1002827 /* U+2827 BRAILLE PATTERN DOTS-1236 */
  lazy val XK_braille_dots_46: Long       = 0x1002828 /* U+2828 BRAILLE PATTERN DOTS-46 */
  lazy val XK_braille_dots_146: Long      = 0x1002829 /* U+2829 BRAILLE PATTERN DOTS-146 */
  lazy val XK_braille_dots_246: Long      = 0x100282a /* U+282a BRAILLE PATTERN DOTS-246 */
  lazy val XK_braille_dots_1246: Long     = 0x100282b /* U+282b BRAILLE PATTERN DOTS-1246 */
  lazy val XK_braille_dots_346: Long      = 0x100282c /* U+282c BRAILLE PATTERN DOTS-346 */
  lazy val XK_braille_dots_1346: Long     = 0x100282d /* U+282d BRAILLE PATTERN DOTS-1346 */
  lazy val XK_braille_dots_2346: Long     = 0x100282e /* U+282e BRAILLE PATTERN DOTS-2346 */
  lazy val XK_braille_dots_12346: Long    = 0x100282f /* U+282f BRAILLE PATTERN DOTS-12346 */
  lazy val XK_braille_dots_56: Long       = 0x1002830 /* U+2830 BRAILLE PATTERN DOTS-56 */
  lazy val XK_braille_dots_156: Long      = 0x1002831 /* U+2831 BRAILLE PATTERN DOTS-156 */
  lazy val XK_braille_dots_256: Long      = 0x1002832 /* U+2832 BRAILLE PATTERN DOTS-256 */
  lazy val XK_braille_dots_1256: Long     = 0x1002833 /* U+2833 BRAILLE PATTERN DOTS-1256 */
  lazy val XK_braille_dots_356: Long      = 0x1002834 /* U+2834 BRAILLE PATTERN DOTS-356 */
  lazy val XK_braille_dots_1356: Long     = 0x1002835 /* U+2835 BRAILLE PATTERN DOTS-1356 */
  lazy val XK_braille_dots_2356: Long     = 0x1002836 /* U+2836 BRAILLE PATTERN DOTS-2356 */
  lazy val XK_braille_dots_12356: Long    = 0x1002837 /* U+2837 BRAILLE PATTERN DOTS-12356 */
  lazy val XK_braille_dots_456: Long      = 0x1002838 /* U+2838 BRAILLE PATTERN DOTS-456 */
  lazy val XK_braille_dots_1456: Long     = 0x1002839 /* U+2839 BRAILLE PATTERN DOTS-1456 */
  lazy val XK_braille_dots_2456: Long     = 0x100283a /* U+283a BRAILLE PATTERN DOTS-2456 */
  lazy val XK_braille_dots_12456: Long    = 0x100283b /* U+283b BRAILLE PATTERN DOTS-12456 */
  lazy val XK_braille_dots_3456: Long     = 0x100283c /* U+283c BRAILLE PATTERN DOTS-3456 */
  lazy val XK_braille_dots_13456: Long    = 0x100283d /* U+283d BRAILLE PATTERN DOTS-13456 */
  lazy val XK_braille_dots_23456: Long    = 0x100283e /* U+283e BRAILLE PATTERN DOTS-23456 */
  lazy val XK_braille_dots_123456: Long   = 0x100283f /* U+283f BRAILLE PATTERN DOTS-123456 */
  lazy val XK_braille_dots_7: Long        = 0x1002840 /* U+2840 BRAILLE PATTERN DOTS-7 */
  lazy val XK_braille_dots_17: Long       = 0x1002841 /* U+2841 BRAILLE PATTERN DOTS-17 */
  lazy val XK_braille_dots_27: Long       = 0x1002842 /* U+2842 BRAILLE PATTERN DOTS-27 */
  lazy val XK_braille_dots_127: Long      = 0x1002843 /* U+2843 BRAILLE PATTERN DOTS-127 */
  lazy val XK_braille_dots_37: Long       = 0x1002844 /* U+2844 BRAILLE PATTERN DOTS-37 */
  lazy val XK_braille_dots_137: Long      = 0x1002845 /* U+2845 BRAILLE PATTERN DOTS-137 */
  lazy val XK_braille_dots_237: Long      = 0x1002846 /* U+2846 BRAILLE PATTERN DOTS-237 */
  lazy val XK_braille_dots_1237: Long     = 0x1002847 /* U+2847 BRAILLE PATTERN DOTS-1237 */
  lazy val XK_braille_dots_47: Long       = 0x1002848 /* U+2848 BRAILLE PATTERN DOTS-47 */
  lazy val XK_braille_dots_147: Long      = 0x1002849 /* U+2849 BRAILLE PATTERN DOTS-147 */
  lazy val XK_braille_dots_247: Long      = 0x100284a /* U+284a BRAILLE PATTERN DOTS-247 */
  lazy val XK_braille_dots_1247: Long     = 0x100284b /* U+284b BRAILLE PATTERN DOTS-1247 */
  lazy val XK_braille_dots_347: Long      = 0x100284c /* U+284c BRAILLE PATTERN DOTS-347 */
  lazy val XK_braille_dots_1347: Long     = 0x100284d /* U+284d BRAILLE PATTERN DOTS-1347 */
  lazy val XK_braille_dots_2347: Long     = 0x100284e /* U+284e BRAILLE PATTERN DOTS-2347 */
  lazy val XK_braille_dots_12347: Long    = 0x100284f /* U+284f BRAILLE PATTERN DOTS-12347 */
  lazy val XK_braille_dots_57: Long       = 0x1002850 /* U+2850 BRAILLE PATTERN DOTS-57 */
  lazy val XK_braille_dots_157: Long      = 0x1002851 /* U+2851 BRAILLE PATTERN DOTS-157 */
  lazy val XK_braille_dots_257: Long      = 0x1002852 /* U+2852 BRAILLE PATTERN DOTS-257 */
  lazy val XK_braille_dots_1257: Long     = 0x1002853 /* U+2853 BRAILLE PATTERN DOTS-1257 */
  lazy val XK_braille_dots_357: Long      = 0x1002854 /* U+2854 BRAILLE PATTERN DOTS-357 */
  lazy val XK_braille_dots_1357: Long     = 0x1002855 /* U+2855 BRAILLE PATTERN DOTS-1357 */
  lazy val XK_braille_dots_2357: Long     = 0x1002856 /* U+2856 BRAILLE PATTERN DOTS-2357 */
  lazy val XK_braille_dots_12357: Long    = 0x1002857 /* U+2857 BRAILLE PATTERN DOTS-12357 */
  lazy val XK_braille_dots_457: Long      = 0x1002858 /* U+2858 BRAILLE PATTERN DOTS-457 */
  lazy val XK_braille_dots_1457: Long     = 0x1002859 /* U+2859 BRAILLE PATTERN DOTS-1457 */
  lazy val XK_braille_dots_2457: Long     = 0x100285a /* U+285a BRAILLE PATTERN DOTS-2457 */
  lazy val XK_braille_dots_12457: Long    = 0x100285b /* U+285b BRAILLE PATTERN DOTS-12457 */
  lazy val XK_braille_dots_3457: Long     = 0x100285c /* U+285c BRAILLE PATTERN DOTS-3457 */
  lazy val XK_braille_dots_13457: Long    = 0x100285d /* U+285d BRAILLE PATTERN DOTS-13457 */
  lazy val XK_braille_dots_23457: Long    = 0x100285e /* U+285e BRAILLE PATTERN DOTS-23457 */
  lazy val XK_braille_dots_123457: Long   = 0x100285f /* U+285f BRAILLE PATTERN DOTS-123457 */
  lazy val XK_braille_dots_67: Long       = 0x1002860 /* U+2860 BRAILLE PATTERN DOTS-67 */
  lazy val XK_braille_dots_167: Long      = 0x1002861 /* U+2861 BRAILLE PATTERN DOTS-167 */
  lazy val XK_braille_dots_267: Long      = 0x1002862 /* U+2862 BRAILLE PATTERN DOTS-267 */
  lazy val XK_braille_dots_1267: Long     = 0x1002863 /* U+2863 BRAILLE PATTERN DOTS-1267 */
  lazy val XK_braille_dots_367: Long      = 0x1002864 /* U+2864 BRAILLE PATTERN DOTS-367 */
  lazy val XK_braille_dots_1367: Long     = 0x1002865 /* U+2865 BRAILLE PATTERN DOTS-1367 */
  lazy val XK_braille_dots_2367: Long     = 0x1002866 /* U+2866 BRAILLE PATTERN DOTS-2367 */
  lazy val XK_braille_dots_12367: Long    = 0x1002867 /* U+2867 BRAILLE PATTERN DOTS-12367 */
  lazy val XK_braille_dots_467: Long      = 0x1002868 /* U+2868 BRAILLE PATTERN DOTS-467 */
  lazy val XK_braille_dots_1467: Long     = 0x1002869 /* U+2869 BRAILLE PATTERN DOTS-1467 */
  lazy val XK_braille_dots_2467: Long     = 0x100286a /* U+286a BRAILLE PATTERN DOTS-2467 */
  lazy val XK_braille_dots_12467: Long    = 0x100286b /* U+286b BRAILLE PATTERN DOTS-12467 */
  lazy val XK_braille_dots_3467: Long     = 0x100286c /* U+286c BRAILLE PATTERN DOTS-3467 */
  lazy val XK_braille_dots_13467: Long    = 0x100286d /* U+286d BRAILLE PATTERN DOTS-13467 */
  lazy val XK_braille_dots_23467: Long    = 0x100286e /* U+286e BRAILLE PATTERN DOTS-23467 */
  lazy val XK_braille_dots_123467: Long   = 0x100286f /* U+286f BRAILLE PATTERN DOTS-123467 */
  lazy val XK_braille_dots_567: Long      = 0x1002870 /* U+2870 BRAILLE PATTERN DOTS-567 */
  lazy val XK_braille_dots_1567: Long     = 0x1002871 /* U+2871 BRAILLE PATTERN DOTS-1567 */
  lazy val XK_braille_dots_2567: Long     = 0x1002872 /* U+2872 BRAILLE PATTERN DOTS-2567 */
  lazy val XK_braille_dots_12567: Long    = 0x1002873 /* U+2873 BRAILLE PATTERN DOTS-12567 */
  lazy val XK_braille_dots_3567: Long     = 0x1002874 /* U+2874 BRAILLE PATTERN DOTS-3567 */
  lazy val XK_braille_dots_13567: Long    = 0x1002875 /* U+2875 BRAILLE PATTERN DOTS-13567 */
  lazy val XK_braille_dots_23567: Long    = 0x1002876 /* U+2876 BRAILLE PATTERN DOTS-23567 */
  lazy val XK_braille_dots_123567: Long   = 0x1002877 /* U+2877 BRAILLE PATTERN DOTS-123567 */
  lazy val XK_braille_dots_4567: Long     = 0x1002878 /* U+2878 BRAILLE PATTERN DOTS-4567 */
  lazy val XK_braille_dots_14567: Long    = 0x1002879 /* U+2879 BRAILLE PATTERN DOTS-14567 */
  lazy val XK_braille_dots_24567: Long    = 0x100287a /* U+287a BRAILLE PATTERN DOTS-24567 */
  lazy val XK_braille_dots_124567: Long   = 0x100287b /* U+287b BRAILLE PATTERN DOTS-124567 */
  lazy val XK_braille_dots_34567: Long    = 0x100287c /* U+287c BRAILLE PATTERN DOTS-34567 */
  lazy val XK_braille_dots_134567: Long   = 0x100287d /* U+287d BRAILLE PATTERN DOTS-134567 */
  lazy val XK_braille_dots_234567: Long   = 0x100287e /* U+287e BRAILLE PATTERN DOTS-234567 */
  lazy val XK_braille_dots_1234567: Long  = 0x100287f /* U+287f BRAILLE PATTERN DOTS-1234567 */
  lazy val XK_braille_dots_8: Long        = 0x1002880 /* U+2880 BRAILLE PATTERN DOTS-8 */
  lazy val XK_braille_dots_18: Long       = 0x1002881 /* U+2881 BRAILLE PATTERN DOTS-18 */
  lazy val XK_braille_dots_28: Long       = 0x1002882 /* U+2882 BRAILLE PATTERN DOTS-28 */
  lazy val XK_braille_dots_128: Long      = 0x1002883 /* U+2883 BRAILLE PATTERN DOTS-128 */
  lazy val XK_braille_dots_38: Long       = 0x1002884 /* U+2884 BRAILLE PATTERN DOTS-38 */
  lazy val XK_braille_dots_138: Long      = 0x1002885 /* U+2885 BRAILLE PATTERN DOTS-138 */
  lazy val XK_braille_dots_238: Long      = 0x1002886 /* U+2886 BRAILLE PATTERN DOTS-238 */
  lazy val XK_braille_dots_1238: Long     = 0x1002887 /* U+2887 BRAILLE PATTERN DOTS-1238 */
  lazy val XK_braille_dots_48: Long       = 0x1002888 /* U+2888 BRAILLE PATTERN DOTS-48 */
  lazy val XK_braille_dots_148: Long      = 0x1002889 /* U+2889 BRAILLE PATTERN DOTS-148 */
  lazy val XK_braille_dots_248: Long      = 0x100288a /* U+288a BRAILLE PATTERN DOTS-248 */
  lazy val XK_braille_dots_1248: Long     = 0x100288b /* U+288b BRAILLE PATTERN DOTS-1248 */
  lazy val XK_braille_dots_348: Long      = 0x100288c /* U+288c BRAILLE PATTERN DOTS-348 */
  lazy val XK_braille_dots_1348: Long     = 0x100288d /* U+288d BRAILLE PATTERN DOTS-1348 */
  lazy val XK_braille_dots_2348: Long     = 0x100288e /* U+288e BRAILLE PATTERN DOTS-2348 */
  lazy val XK_braille_dots_12348: Long    = 0x100288f /* U+288f BRAILLE PATTERN DOTS-12348 */
  lazy val XK_braille_dots_58: Long       = 0x1002890 /* U+2890 BRAILLE PATTERN DOTS-58 */
  lazy val XK_braille_dots_158: Long      = 0x1002891 /* U+2891 BRAILLE PATTERN DOTS-158 */
  lazy val XK_braille_dots_258: Long      = 0x1002892 /* U+2892 BRAILLE PATTERN DOTS-258 */
  lazy val XK_braille_dots_1258: Long     = 0x1002893 /* U+2893 BRAILLE PATTERN DOTS-1258 */
  lazy val XK_braille_dots_358: Long      = 0x1002894 /* U+2894 BRAILLE PATTERN DOTS-358 */
  lazy val XK_braille_dots_1358: Long     = 0x1002895 /* U+2895 BRAILLE PATTERN DOTS-1358 */
  lazy val XK_braille_dots_2358: Long     = 0x1002896 /* U+2896 BRAILLE PATTERN DOTS-2358 */
  lazy val XK_braille_dots_12358: Long    = 0x1002897 /* U+2897 BRAILLE PATTERN DOTS-12358 */
  lazy val XK_braille_dots_458: Long      = 0x1002898 /* U+2898 BRAILLE PATTERN DOTS-458 */
  lazy val XK_braille_dots_1458: Long     = 0x1002899 /* U+2899 BRAILLE PATTERN DOTS-1458 */
  lazy val XK_braille_dots_2458: Long     = 0x100289a /* U+289a BRAILLE PATTERN DOTS-2458 */
  lazy val XK_braille_dots_12458: Long    = 0x100289b /* U+289b BRAILLE PATTERN DOTS-12458 */
  lazy val XK_braille_dots_3458: Long     = 0x100289c /* U+289c BRAILLE PATTERN DOTS-3458 */
  lazy val XK_braille_dots_13458: Long    = 0x100289d /* U+289d BRAILLE PATTERN DOTS-13458 */
  lazy val XK_braille_dots_23458: Long    = 0x100289e /* U+289e BRAILLE PATTERN DOTS-23458 */
  lazy val XK_braille_dots_123458: Long   = 0x100289f /* U+289f BRAILLE PATTERN DOTS-123458 */
  lazy val XK_braille_dots_68: Long       = 0x10028a0 /* U+28a0 BRAILLE PATTERN DOTS-68 */
  lazy val XK_braille_dots_168: Long      = 0x10028a1 /* U+28a1 BRAILLE PATTERN DOTS-168 */
  lazy val XK_braille_dots_268: Long      = 0x10028a2 /* U+28a2 BRAILLE PATTERN DOTS-268 */
  lazy val XK_braille_dots_1268: Long     = 0x10028a3 /* U+28a3 BRAILLE PATTERN DOTS-1268 */
  lazy val XK_braille_dots_368: Long      = 0x10028a4 /* U+28a4 BRAILLE PATTERN DOTS-368 */
  lazy val XK_braille_dots_1368: Long     = 0x10028a5 /* U+28a5 BRAILLE PATTERN DOTS-1368 */
  lazy val XK_braille_dots_2368: Long     = 0x10028a6 /* U+28a6 BRAILLE PATTERN DOTS-2368 */
  lazy val XK_braille_dots_12368: Long    = 0x10028a7 /* U+28a7 BRAILLE PATTERN DOTS-12368 */
  lazy val XK_braille_dots_468: Long      = 0x10028a8 /* U+28a8 BRAILLE PATTERN DOTS-468 */
  lazy val XK_braille_dots_1468: Long     = 0x10028a9 /* U+28a9 BRAILLE PATTERN DOTS-1468 */
  lazy val XK_braille_dots_2468: Long     = 0x10028aa /* U+28aa BRAILLE PATTERN DOTS-2468 */
  lazy val XK_braille_dots_12468: Long    = 0x10028ab /* U+28ab BRAILLE PATTERN DOTS-12468 */
  lazy val XK_braille_dots_3468: Long     = 0x10028ac /* U+28ac BRAILLE PATTERN DOTS-3468 */
  lazy val XK_braille_dots_13468: Long    = 0x10028ad /* U+28ad BRAILLE PATTERN DOTS-13468 */
  lazy val XK_braille_dots_23468: Long    = 0x10028ae /* U+28ae BRAILLE PATTERN DOTS-23468 */
  lazy val XK_braille_dots_123468: Long   = 0x10028af /* U+28af BRAILLE PATTERN DOTS-123468 */
  lazy val XK_braille_dots_568: Long      = 0x10028b0 /* U+28b0 BRAILLE PATTERN DOTS-568 */
  lazy val XK_braille_dots_1568: Long     = 0x10028b1 /* U+28b1 BRAILLE PATTERN DOTS-1568 */
  lazy val XK_braille_dots_2568: Long     = 0x10028b2 /* U+28b2 BRAILLE PATTERN DOTS-2568 */
  lazy val XK_braille_dots_12568: Long    = 0x10028b3 /* U+28b3 BRAILLE PATTERN DOTS-12568 */
  lazy val XK_braille_dots_3568: Long     = 0x10028b4 /* U+28b4 BRAILLE PATTERN DOTS-3568 */
  lazy val XK_braille_dots_13568: Long    = 0x10028b5 /* U+28b5 BRAILLE PATTERN DOTS-13568 */
  lazy val XK_braille_dots_23568: Long    = 0x10028b6 /* U+28b6 BRAILLE PATTERN DOTS-23568 */
  lazy val XK_braille_dots_123568: Long   = 0x10028b7 /* U+28b7 BRAILLE PATTERN DOTS-123568 */
  lazy val XK_braille_dots_4568: Long     = 0x10028b8 /* U+28b8 BRAILLE PATTERN DOTS-4568 */
  lazy val XK_braille_dots_14568: Long    = 0x10028b9 /* U+28b9 BRAILLE PATTERN DOTS-14568 */
  lazy val XK_braille_dots_24568: Long    = 0x10028ba /* U+28ba BRAILLE PATTERN DOTS-24568 */
  lazy val XK_braille_dots_124568: Long   = 0x10028bb /* U+28bb BRAILLE PATTERN DOTS-124568 */
  lazy val XK_braille_dots_34568: Long    = 0x10028bc /* U+28bc BRAILLE PATTERN DOTS-34568 */
  lazy val XK_braille_dots_134568: Long   = 0x10028bd /* U+28bd BRAILLE PATTERN DOTS-134568 */
  lazy val XK_braille_dots_234568: Long   = 0x10028be /* U+28be BRAILLE PATTERN DOTS-234568 */
  lazy val XK_braille_dots_1234568: Long  = 0x10028bf /* U+28bf BRAILLE PATTERN DOTS-1234568 */
  lazy val XK_braille_dots_78: Long       = 0x10028c0 /* U+28c0 BRAILLE PATTERN DOTS-78 */
  lazy val XK_braille_dots_178: Long      = 0x10028c1 /* U+28c1 BRAILLE PATTERN DOTS-178 */
  lazy val XK_braille_dots_278: Long      = 0x10028c2 /* U+28c2 BRAILLE PATTERN DOTS-278 */
  lazy val XK_braille_dots_1278: Long     = 0x10028c3 /* U+28c3 BRAILLE PATTERN DOTS-1278 */
  lazy val XK_braille_dots_378: Long      = 0x10028c4 /* U+28c4 BRAILLE PATTERN DOTS-378 */
  lazy val XK_braille_dots_1378: Long     = 0x10028c5 /* U+28c5 BRAILLE PATTERN DOTS-1378 */
  lazy val XK_braille_dots_2378: Long     = 0x10028c6 /* U+28c6 BRAILLE PATTERN DOTS-2378 */
  lazy val XK_braille_dots_12378: Long    = 0x10028c7 /* U+28c7 BRAILLE PATTERN DOTS-12378 */
  lazy val XK_braille_dots_478: Long      = 0x10028c8 /* U+28c8 BRAILLE PATTERN DOTS-478 */
  lazy val XK_braille_dots_1478: Long     = 0x10028c9 /* U+28c9 BRAILLE PATTERN DOTS-1478 */
  lazy val XK_braille_dots_2478: Long     = 0x10028ca /* U+28ca BRAILLE PATTERN DOTS-2478 */
  lazy val XK_braille_dots_12478: Long    = 0x10028cb /* U+28cb BRAILLE PATTERN DOTS-12478 */
  lazy val XK_braille_dots_3478: Long     = 0x10028cc /* U+28cc BRAILLE PATTERN DOTS-3478 */
  lazy val XK_braille_dots_13478: Long    = 0x10028cd /* U+28cd BRAILLE PATTERN DOTS-13478 */
  lazy val XK_braille_dots_23478: Long    = 0x10028ce /* U+28ce BRAILLE PATTERN DOTS-23478 */
  lazy val XK_braille_dots_123478: Long   = 0x10028cf /* U+28cf BRAILLE PATTERN DOTS-123478 */
  lazy val XK_braille_dots_578: Long      = 0x10028d0 /* U+28d0 BRAILLE PATTERN DOTS-578 */
  lazy val XK_braille_dots_1578: Long     = 0x10028d1 /* U+28d1 BRAILLE PATTERN DOTS-1578 */
  lazy val XK_braille_dots_2578: Long     = 0x10028d2 /* U+28d2 BRAILLE PATTERN DOTS-2578 */
  lazy val XK_braille_dots_12578: Long    = 0x10028d3 /* U+28d3 BRAILLE PATTERN DOTS-12578 */
  lazy val XK_braille_dots_3578: Long     = 0x10028d4 /* U+28d4 BRAILLE PATTERN DOTS-3578 */
  lazy val XK_braille_dots_13578: Long    = 0x10028d5 /* U+28d5 BRAILLE PATTERN DOTS-13578 */
  lazy val XK_braille_dots_23578: Long    = 0x10028d6 /* U+28d6 BRAILLE PATTERN DOTS-23578 */
  lazy val XK_braille_dots_123578: Long   = 0x10028d7 /* U+28d7 BRAILLE PATTERN DOTS-123578 */
  lazy val XK_braille_dots_4578: Long     = 0x10028d8 /* U+28d8 BRAILLE PATTERN DOTS-4578 */
  lazy val XK_braille_dots_14578: Long    = 0x10028d9 /* U+28d9 BRAILLE PATTERN DOTS-14578 */
  lazy val XK_braille_dots_24578: Long    = 0x10028da /* U+28da BRAILLE PATTERN DOTS-24578 */
  lazy val XK_braille_dots_124578: Long   = 0x10028db /* U+28db BRAILLE PATTERN DOTS-124578 */
  lazy val XK_braille_dots_34578: Long    = 0x10028dc /* U+28dc BRAILLE PATTERN DOTS-34578 */
  lazy val XK_braille_dots_134578: Long   = 0x10028dd /* U+28dd BRAILLE PATTERN DOTS-134578 */
  lazy val XK_braille_dots_234578: Long   = 0x10028de /* U+28de BRAILLE PATTERN DOTS-234578 */
  lazy val XK_braille_dots_1234578: Long  = 0x10028df /* U+28df BRAILLE PATTERN DOTS-1234578 */
  lazy val XK_braille_dots_678: Long      = 0x10028e0 /* U+28e0 BRAILLE PATTERN DOTS-678 */
  lazy val XK_braille_dots_1678: Long     = 0x10028e1 /* U+28e1 BRAILLE PATTERN DOTS-1678 */
  lazy val XK_braille_dots_2678: Long     = 0x10028e2 /* U+28e2 BRAILLE PATTERN DOTS-2678 */
  lazy val XK_braille_dots_12678: Long    = 0x10028e3 /* U+28e3 BRAILLE PATTERN DOTS-12678 */
  lazy val XK_braille_dots_3678: Long     = 0x10028e4 /* U+28e4 BRAILLE PATTERN DOTS-3678 */
  lazy val XK_braille_dots_13678: Long    = 0x10028e5 /* U+28e5 BRAILLE PATTERN DOTS-13678 */
  lazy val XK_braille_dots_23678: Long    = 0x10028e6 /* U+28e6 BRAILLE PATTERN DOTS-23678 */
  lazy val XK_braille_dots_123678: Long   = 0x10028e7 /* U+28e7 BRAILLE PATTERN DOTS-123678 */
  lazy val XK_braille_dots_4678: Long     = 0x10028e8 /* U+28e8 BRAILLE PATTERN DOTS-4678 */
  lazy val XK_braille_dots_14678: Long    = 0x10028e9 /* U+28e9 BRAILLE PATTERN DOTS-14678 */
  lazy val XK_braille_dots_24678: Long    = 0x10028ea /* U+28ea BRAILLE PATTERN DOTS-24678 */
  lazy val XK_braille_dots_124678: Long   = 0x10028eb /* U+28eb BRAILLE PATTERN DOTS-124678 */
  lazy val XK_braille_dots_34678: Long    = 0x10028ec /* U+28ec BRAILLE PATTERN DOTS-34678 */
  lazy val XK_braille_dots_134678: Long   = 0x10028ed /* U+28ed BRAILLE PATTERN DOTS-134678 */
  lazy val XK_braille_dots_234678: Long   = 0x10028ee /* U+28ee BRAILLE PATTERN DOTS-234678 */
  lazy val XK_braille_dots_1234678: Long  = 0x10028ef /* U+28ef BRAILLE PATTERN DOTS-1234678 */
  lazy val XK_braille_dots_5678: Long     = 0x10028f0 /* U+28f0 BRAILLE PATTERN DOTS-5678 */
  lazy val XK_braille_dots_15678: Long    = 0x10028f1 /* U+28f1 BRAILLE PATTERN DOTS-15678 */
  lazy val XK_braille_dots_25678: Long    = 0x10028f2 /* U+28f2 BRAILLE PATTERN DOTS-25678 */
  lazy val XK_braille_dots_125678: Long   = 0x10028f3 /* U+28f3 BRAILLE PATTERN DOTS-125678 */
  lazy val XK_braille_dots_35678: Long    = 0x10028f4 /* U+28f4 BRAILLE PATTERN DOTS-35678 */
  lazy val XK_braille_dots_135678: Long   = 0x10028f5 /* U+28f5 BRAILLE PATTERN DOTS-135678 */
  lazy val XK_braille_dots_235678: Long   = 0x10028f6 /* U+28f6 BRAILLE PATTERN DOTS-235678 */
  lazy val XK_braille_dots_1235678: Long  = 0x10028f7 /* U+28f7 BRAILLE PATTERN DOTS-1235678 */
  lazy val XK_braille_dots_45678: Long    = 0x10028f8 /* U+28f8 BRAILLE PATTERN DOTS-45678 */
  lazy val XK_braille_dots_145678: Long   = 0x10028f9 /* U+28f9 BRAILLE PATTERN DOTS-145678 */
  lazy val XK_braille_dots_245678: Long   = 0x10028fa /* U+28fa BRAILLE PATTERN DOTS-245678 */
  lazy val XK_braille_dots_1245678: Long  = 0x10028fb /* U+28fb BRAILLE PATTERN DOTS-1245678 */
  lazy val XK_braille_dots_345678: Long   = 0x10028fc /* U+28fc BRAILLE PATTERN DOTS-345678 */
  lazy val XK_braille_dots_1345678: Long  = 0x10028fd /* U+28fd BRAILLE PATTERN DOTS-1345678 */
  lazy val XK_braille_dots_2345678: Long  = 0x10028fe /* U+28fe BRAILLE PATTERN DOTS-2345678 */
  lazy val XK_braille_dots_12345678: Long = 0x10028ff /* U+28ff BRAILLE PATTERN DOTS-12345678 */

  /*
   * Sinhala (http://unicode.org/charts/PDF/U0D80.pdf)
   * http://www.nongnu.org/sinhala/doc/transliteration/sinhala-transliteration_6.html
   */

  lazy val XK_Sinh_ng: Long         = 0x1000d82 /* U+0D82 SINHALA ANUSVARAYA */
  lazy val XK_Sinh_h2: Long         = 0x1000d83 /* U+0D83 SINHALA VISARGAYA */
  lazy val XK_Sinh_a: Long          = 0x1000d85 /* U+0D85 SINHALA AYANNA */
  lazy val XK_Sinh_aa: Long         = 0x1000d86 /* U+0D86 SINHALA AAYANNA */
  lazy val XK_Sinh_ae: Long         = 0x1000d87 /* U+0D87 SINHALA AEYANNA */
  lazy val XK_Sinh_aee: Long        = 0x1000d88 /* U+0D88 SINHALA AEEYANNA */
  lazy val XK_Sinh_i: Long          = 0x1000d89 /* U+0D89 SINHALA IYANNA */
  lazy val XK_Sinh_ii: Long         = 0x1000d8a /* U+0D8A SINHALA IIYANNA */
  lazy val XK_Sinh_u: Long          = 0x1000d8b /* U+0D8B SINHALA UYANNA */
  lazy val XK_Sinh_uu: Long         = 0x1000d8c /* U+0D8C SINHALA UUYANNA */
  lazy val XK_Sinh_ri: Long         = 0x1000d8d /* U+0D8D SINHALA IRUYANNA */
  lazy val XK_Sinh_rii: Long        = 0x1000d8e /* U+0D8E SINHALA IRUUYANNA */
  lazy val XK_Sinh_lu: Long         = 0x1000d8f /* U+0D8F SINHALA ILUYANNA */
  lazy val XK_Sinh_luu: Long        = 0x1000d90 /* U+0D90 SINHALA ILUUYANNA */
  lazy val XK_Sinh_e: Long          = 0x1000d91 /* U+0D91 SINHALA EYANNA */
  lazy val XK_Sinh_ee: Long         = 0x1000d92 /* U+0D92 SINHALA EEYANNA */
  lazy val XK_Sinh_ai: Long         = 0x1000d93 /* U+0D93 SINHALA AIYANNA */
  lazy val XK_Sinh_o: Long          = 0x1000d94 /* U+0D94 SINHALA OYANNA */
  lazy val XK_Sinh_oo: Long         = 0x1000d95 /* U+0D95 SINHALA OOYANNA */
  lazy val XK_Sinh_au: Long         = 0x1000d96 /* U+0D96 SINHALA AUYANNA */
  lazy val XK_Sinh_ka: Long         = 0x1000d9a /* U+0D9A SINHALA KAYANNA */
  lazy val XK_Sinh_kha: Long        = 0x1000d9b /* U+0D9B SINHALA MAHA. KAYANNA */
  lazy val XK_Sinh_ga: Long         = 0x1000d9c /* U+0D9C SINHALA GAYANNA */
  lazy val XK_Sinh_gha: Long        = 0x1000d9d /* U+0D9D SINHALA MAHA. GAYANNA */
  lazy val XK_Sinh_ng2: Long        = 0x1000d9e /* U+0D9E SINHALA KANTAJA NAASIKYAYA */
  lazy val XK_Sinh_nga: Long        = 0x1000d9f /* U+0D9F SINHALA SANYAKA GAYANNA */
  lazy val XK_Sinh_ca: Long         = 0x1000da0 /* U+0DA0 SINHALA CAYANNA */
  lazy val XK_Sinh_cha: Long        = 0x1000da1 /* U+0DA1 SINHALA MAHA. CAYANNA */
  lazy val XK_Sinh_ja: Long         = 0x1000da2 /* U+0DA2 SINHALA JAYANNA */
  lazy val XK_Sinh_jha: Long        = 0x1000da3 /* U+0DA3 SINHALA MAHA. JAYANNA */
  lazy val XK_Sinh_nya: Long        = 0x1000da4 /* U+0DA4 SINHALA TAALUJA NAASIKYAYA */
  lazy val XK_Sinh_jnya: Long       = 0x1000da5 /* U+0DA5 SINHALA TAALUJA SANYOOGA NAASIKYAYA */
  lazy val XK_Sinh_nja: Long        = 0x1000da6 /* U+0DA6 SINHALA SANYAKA JAYANNA */
  lazy val XK_Sinh_tta: Long        = 0x1000da7 /* U+0DA7 SINHALA TTAYANNA */
  lazy val XK_Sinh_ttha: Long       = 0x1000da8 /* U+0DA8 SINHALA MAHA. TTAYANNA */
  lazy val XK_Sinh_dda: Long        = 0x1000da9 /* U+0DA9 SINHALA DDAYANNA */
  lazy val XK_Sinh_ddha: Long       = 0x1000daa /* U+0DAA SINHALA MAHA. DDAYANNA */
  lazy val XK_Sinh_nna: Long        = 0x1000dab /* U+0DAB SINHALA MUURDHAJA NAYANNA */
  lazy val XK_Sinh_ndda: Long       = 0x1000dac /* U+0DAC SINHALA SANYAKA DDAYANNA */
  lazy val XK_Sinh_tha: Long        = 0x1000dad /* U+0DAD SINHALA TAYANNA */
  lazy val XK_Sinh_thha: Long       = 0x1000dae /* U+0DAE SINHALA MAHA. TAYANNA */
  lazy val XK_Sinh_dha: Long        = 0x1000daf /* U+0DAF SINHALA DAYANNA */
  lazy val XK_Sinh_dhha: Long       = 0x1000db0 /* U+0DB0 SINHALA MAHA. DAYANNA */
  lazy val XK_Sinh_na: Long         = 0x1000db1 /* U+0DB1 SINHALA DANTAJA NAYANNA */
  lazy val XK_Sinh_ndha: Long       = 0x1000db3 /* U+0DB3 SINHALA SANYAKA DAYANNA */
  lazy val XK_Sinh_pa: Long         = 0x1000db4 /* U+0DB4 SINHALA PAYANNA */
  lazy val XK_Sinh_pha: Long        = 0x1000db5 /* U+0DB5 SINHALA MAHA. PAYANNA */
  lazy val XK_Sinh_ba: Long         = 0x1000db6 /* U+0DB6 SINHALA BAYANNA */
  lazy val XK_Sinh_bha: Long        = 0x1000db7 /* U+0DB7 SINHALA MAHA. BAYANNA */
  lazy val XK_Sinh_ma: Long         = 0x1000db8 /* U+0DB8 SINHALA MAYANNA */
  lazy val XK_Sinh_mba: Long        = 0x1000db9 /* U+0DB9 SINHALA AMBA BAYANNA */
  lazy val XK_Sinh_ya: Long         = 0x1000dba /* U+0DBA SINHALA YAYANNA */
  lazy val XK_Sinh_ra: Long         = 0x1000dbb /* U+0DBB SINHALA RAYANNA */
  lazy val XK_Sinh_la: Long         = 0x1000dbd /* U+0DBD SINHALA DANTAJA LAYANNA */
  lazy val XK_Sinh_va: Long         = 0x1000dc0 /* U+0DC0 SINHALA VAYANNA */
  lazy val XK_Sinh_sha: Long        = 0x1000dc1 /* U+0DC1 SINHALA TAALUJA SAYANNA */
  lazy val XK_Sinh_ssha: Long       = 0x1000dc2 /* U+0DC2 SINHALA MUURDHAJA SAYANNA */
  lazy val XK_Sinh_sa: Long         = 0x1000dc3 /* U+0DC3 SINHALA DANTAJA SAYANNA */
  lazy val XK_Sinh_ha: Long         = 0x1000dc4 /* U+0DC4 SINHALA HAYANNA */
  lazy val XK_Sinh_lla: Long        = 0x1000dc5 /* U+0DC5 SINHALA MUURDHAJA LAYANNA */
  lazy val XK_Sinh_fa: Long         = 0x1000dc6 /* U+0DC6 SINHALA FAYANNA */
  lazy val XK_Sinh_al: Long         = 0x1000dca /* U+0DCA SINHALA AL-LAKUNA */
  lazy val XK_Sinh_aa2: Long        = 0x1000dcf /* U+0DCF SINHALA AELA-PILLA */
  lazy val XK_Sinh_ae2: Long        = 0x1000dd0 /* U+0DD0 SINHALA AEDA-PILLA */
  lazy val XK_Sinh_aee2: Long       = 0x1000dd1 /* U+0DD1 SINHALA DIGA AEDA-PILLA */
  lazy val XK_Sinh_i2: Long         = 0x1000dd2 /* U+0DD2 SINHALA IS-PILLA */
  lazy val XK_Sinh_ii2: Long        = 0x1000dd3 /* U+0DD3 SINHALA DIGA IS-PILLA */
  lazy val XK_Sinh_u2: Long         = 0x1000dd4 /* U+0DD4 SINHALA PAA-PILLA */
  lazy val XK_Sinh_uu2: Long        = 0x1000dd6 /* U+0DD6 SINHALA DIGA PAA-PILLA */
  lazy val XK_Sinh_ru2: Long        = 0x1000dd8 /* U+0DD8 SINHALA GAETTA-PILLA */
  lazy val XK_Sinh_e2: Long         = 0x1000dd9 /* U+0DD9 SINHALA KOMBUVA */
  lazy val XK_Sinh_ee2: Long        = 0x1000dda /* U+0DDA SINHALA DIGA KOMBUVA */
  lazy val XK_Sinh_ai2: Long        = 0x1000ddb /* U+0DDB SINHALA KOMBU DEKA */
  lazy val XK_Sinh_o2: Long         = 0x1000ddc /* U+0DDC SINHALA KOMBUVA HAA AELA-PILLA*/
  lazy val XK_Sinh_oo2: Long        = 0x1000ddd /* U+0DDD SINHALA KOMBUVA HAA DIGA AELA-PILLA*/
  lazy val XK_Sinh_au2: Long        = 0x1000dde /* U+0DDE SINHALA KOMBUVA HAA GAYANUKITTA */
  lazy val XK_Sinh_lu2: Long        = 0x1000ddf /* U+0DDF SINHALA GAYANUKITTA */
  lazy val XK_Sinh_ruu2: Long       = 0x1000df2 /* U+0DF2 SINHALA DIGA GAETTA-PILLA */
  lazy val XK_Sinh_luu2: Long       = 0x1000df3 /* U+0DF3 SINHALA DIGA GAYANUKITTA */
  lazy val XK_Sinh_kunddaliya: Long = 0x1000df4 /* U+0DF4 SINHALA KUNDDALIYA */

}
