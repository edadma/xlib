package io.github.edadma.xlib.extern

import scala.scalanative.unsafe._

@link("X11")
@extern
object Xlib {

  type XID                  = CUnsignedLong
  type Window               = XID
  type Drawable             = XID
  type Font                 = XID
  type Pixmap               = XID
  type Cursor               = XID
  type Colormap             = XID
  type GContext             = XID
  type KeySym               = XID
  type KeyCode              = CUnsignedChar
  type Display              = Ptr[CStruct0]
  type XEvent               = Ptr[CStruct0] //todo: 973
  type Visual_s             = CStruct0 //todo: 227
  type Visual               = Ptr[Visual_s]
  type GC                   = Ptr[CStruct0]
  type XPointer             = CString
  type Bool                 = CInt
  type XFontStruct          = CStruct0 //todo: 1035
  type Time                 = CUnsignedLong
  type XTimeCoord           = CStruct0 //todo: 468
  type XModifierKeymap      = CStruct0 //todo: 475
  type XImage               = CStruct0 //todo: 360
  type Status               = CInt
  type Atom                 = CUnsignedLong
  type XColor               = CStruct0 //todo: 410
  type XGCValues            = CStruct0 //todo: 181
  type XSetWindowAttributes = CStruct0 //todo: 290
  type XHostAddress         = CStruct0 //todo: 341

  def XLoadQueryFont(display: Display, name: /*const*/ CString): Ptr[XFontStruct] = extern //1394
  def XQueryFont(display: Display, font_ID: XID): Ptr[XFontStruct]                = extern //1399
  def XGetMotionEvents(display: Display,
                       w: Window,
                       start: Time,
                       stop: Time,
                       nevents_return: Ptr[CInt]): Ptr[XTimeCoord]  = extern //1405
  def XGetModifierMapping(display: Display): Ptr[XModifierKeymap]   = extern //1423
  def XNewModifiermap(max_keys_per_mod: CInt): Ptr[XModifierKeymap] = extern //1437
  def XCreateImage(display: Display,
                   visual: Visual,
                   depth: CUnsignedInt,
                   format: CInt,
                   offset: CInt,
                   data: CString,
                   width: CUnsignedInt,
                   height: CUnsignedInt,
                   bitmap_pad: CInt,
                   bytes_per_line: CInt): Ptr[XImage] = extern //1441
  def XInitImage(image: Ptr[XImage]): Status          = extern //1453
  def XGetImage(display: Display,
                d: Drawable,
                x: CInt,
                y: CInt,
                width: CUnsignedInt,
                height: CUnsignedInt,
                plane_mask: CUnsignedLong,
                format: CInt): Ptr[XImage] = extern //1456
  def XGetSubImage(display: Display,
                   d: Drawable,
                   x: CInt,
                   y: CInt,
                   width: CUnsignedInt,
                   height: CUnsignedInt,
                   plane_mask: CUnsignedLong,
                   format: CInt,
                   dest_image: Ptr[XImage],
                   dest_x: CInt,
                   dest_y: CInt): Ptr[XImage]                                         = extern //1466
  def XOpenDisplay(display_name: /*const*/ CString): Display                          = extern //1483
  def XrmInitialize(): Unit                                                           = extern //1487
  def XFetchBytes(display: Display, nbytes_return: Ptr[CInt]): CString                = extern //1491
  def XFetchBuffer(display: Display, nbytes_return: Ptr[CInt], buffer: CInt): CString = extern //1495
  def XGetAtomName(display: Display, atom: Atom): CString                             = extern //1500
  def XGetAtomNames(dpy: Display, atoms: Ptr[Atom], count: CInt, names_return: Ptr[CString]): Status =
    extern //1504
  def XGetDefault(display: Display, program: /*const*/ CString, option: /*const*/ CString): CString = extern //1510
  def XDisplayName(string: /*const*/ CString): CString                                              = extern //1515
  def XKeysymToString(keysym: KeySym): CString                                                      = extern //1518
  def XInternAtom(display: Ptr[Display], atom_name: /*const*/ CString, only_if_exists: Bool): Atom  = extern //1536
  def XInternAtoms(dpy: Ptr[Display],
                   names: Ptr[CString],
                   count: CInt,
                   onlyIfExists: Bool,
                   atoms_return: Ptr[Atom]): Status                                                 = extern //1541
  def XCopyColormapAndFree(display: Ptr[Display], colormap: Colormap): Colormap                     = extern //1548
  def XCreateColormap(display: Ptr[Display], w: Window, visual: Ptr[Visual], alloc: CInt): Colormap = extern //1552
  def XCreatePixmapCursor(display: Ptr[Display],
                          source: Pixmap,
                          mask: Pixmap,
                          foreground_color: Ptr[XColor],
                          background_color: Ptr[XColor],
                          x: CUnsignedInt,
                          y: CUnsignedInt): Cursor = extern //1558
  def XCreateGlyphCursor(display: Ptr[Display],
                         source_font: Font,
                         mask_font: Font,
                         source_char: CUnsignedInt,
                         mask_char: CUnsignedInt,
                         foreground_color: /*const*/ Ptr[XColor],
                         background_color: /*const*/ Ptr[XColor]): Cursor   = extern //1567
  def XCreateFontCursor(display: Ptr[Display], shape: CUnsignedInt): Cursor = extern //1576
  def XLoadFont(display: Ptr[Display], name: /*const*/ CString): Font       = extern //1580
  def XCreateGC(display: Ptr[Display], d: Drawable, valuemask: CUnsignedLong, values: Ptr[XGCValues]): GC =
    extern //1584
  def XGContextFromGC(gc: GC): GContext             = extern //1590
  def XFlushGC(display: Ptr[Display], gc: GC): Unit = extern //1593
  def XCreatePixmap(display: Ptr[Display],
                    d: Drawable,
                    width: CUnsignedInt,
                    height: CUnsignedInt,
                    depth: CUnsignedInt): Pixmap = extern //1597
  def XCreateBitmapFromData(display: Ptr[Display],
                            d: Drawable,
                            data: /*const*/ CString,
                            width: CUnsignedInt,
                            height: CUnsignedInt): Pixmap = extern //1604
  def XCreatePixmapFromBitmapData(display: Ptr[Display],
                                  d: Drawable,
                                  data: CString,
                                  width: CUnsignedInt,
                                  height: CUnsignedInt,
                                  fg: CUnsignedLong,
                                  bg: CUnsignedLong,
                                  depth: CUnsignedInt): Pixmap = extern //1611
  def XCreateSimpleWindow(display: Ptr[Display],
                          parent: Window,
                          x: CInt,
                          y: CInt,
                          width: CUnsignedInt,
                          height: CUnsignedInt,
                          border_width: CUnsignedInt,
                          border: CUnsignedLong,
                          background: CUnsignedLong): Window             = extern //1621
  def XGetSelectionOwner(display: Ptr[Display], selection: Atom): Window = extern //1632
  def XCreateWindow(display: Ptr[Display],
                    parent: Window,
                    x: CInt,
                    y: CInt,
                    width: CUnsignedInt,
                    height: CUnsignedInt,
                    border_width: CUnsignedInt,
                    depth: CInt,
                    clas: CUnsignedInt,
                    visual: Ptr[Visual],
                    valuemask: CUnsignedLong,
                    attributes: Ptr[XSetWindowAttributes]): Window                                    = extern //1636
  def XListInstalledColormaps(display: Ptr[Display], w: Window, num_return: Ptr[CInt]): Ptr[Colormap] = extern //1650
  def XListFonts(display: Ptr[Display],
                 pattern: /*const*/ CString,
                 maxnames: CInt,
                 actual_count_return: Ptr[CInt]): Ptr[CString] = extern //1655
  def XListFontsWithInfo(display: Ptr[Display],
                         pattern: /*const*/ CString,
                         maxnames: CInt,
                         count_return: Ptr[CInt],
                         info_return: Ptr[Ptr[XFontStruct]]): Ptr[CString]                     = extern //1661
  def XGetFontPath(display: Ptr[Display], npaths_return: Ptr[CInt]): Ptr[CString]              = extern //1668
  def XListExtensions(display: Ptr[Display], nextensions_return: Ptr[CInt]): Ptr[CString]      = extern //1672
  def XListProperties(display: Ptr[Display], w: Window, num_prop_return: Ptr[CInt]): Ptr[Atom] = extern //1676
  def XListHosts(display: Ptr[Display], nhosts_return: Ptr[CInt], state_return: Ptr[Bool]): Ptr[XHostAddress] =
    extern //1681

  def XNextEvent(display: Display, event: XEvent): CInt = extern //2851
  def XPending(display: Display): CInt                  = extern //2891

  // macros

  @name("xlib_DefaultScreen")
  def DefaultScreen(display: Display): CInt = extern //93
  @name("xlib_DefaultRootWindow")
  def DefaultRootWindow(display: Display): CInt = extern //94

}
