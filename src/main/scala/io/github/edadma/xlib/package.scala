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

  /*
type XKeyEvent = CStruct13[CInt, CUnsignedLong, Bool, Ptr[Display], Window, Window, Window, Time, CInt, CInt, CInt, CInt, CUnsignedInt, CUnsignedInt, Bool] //571

implicit class XKeyEvent(val ptr: Ptr[lib.XKeyEvent]) extends AnyVal {
  def type: CInt = ptr._1
  def serial: CUnsignedLong = ptr._2
  def sendEvent: Bool = ptr._3
  def display: Ptr[Display] = ptr._4
  def window: Window = ptr._5
  def root: Window = ptr._6
  def subwindow: Window = ptr._7
  def time: Time = ptr._8
  def x: CInt = ptr._9
  def y: CInt = ptr._10
  def xRoot: CInt = ptr._11
  def yRoot: CInt = ptr._12
  def state: CUnsignedInt = ptr._13
  def keycode: CUnsignedInt = ptr._14
  def sameScreen: Bool = ptr._15
  def type_=(v: CInt) = ptr._1 = v
  def serial_=(v: CUnsignedLong) = ptr._2 = v
  def sendEvent_=(v: Bool) = ptr._3 = v
  def display_=(v: Ptr[Display]) = ptr._4 = v
  def window_=(v: Window) = ptr._5 = v
  def root_=(v: Window) = ptr._6 = v
  def subwindow_=(v: Window) = ptr._7 = v
  def time_=(v: Time) = ptr._8 = v
  def x_=(v: CInt) = ptr._9 = v
  def y_=(v: CInt) = ptr._10 = v
  def xRoot_=(v: CInt) = ptr._11 = v
  def yRoot_=(v: CInt) = ptr._12 = v
  def state_=(v: CUnsignedInt) = ptr._13 = v
  def keycode_=(v: CUnsignedInt) = ptr._14 = v
  def sameScreen_=(v: Bool) = ptr._15 = v
}
   */

  class XEvent(val ptr: lib.XEvent = malloc(sizeof[CLong] * 24.toULong).asInstanceOf[lib.XEvent]) extends AnyVal {
    def getType: Int = !ptr

    def xkey: XKeyEvent = XKeyEvent(ptr.asInstanceOf[lib.XKeyEvent])

    def destroy(): Unit = free(ptr.asInstanceOf[Ptr[Byte]])
  }

  implicit class XKeyEvent(val ptr: lib.XKeyEvent) extends AnyVal {
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

}
