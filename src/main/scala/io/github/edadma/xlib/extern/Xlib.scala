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
  type XCharStruct          = CStruct0 //todo: 1017
  type XChar2b              = CStruct0 //todo: 1064
  type XMappingEvent        = CStruct0 //todo: 912
  type XRectangle           = CStruct0 //todo: 430
  type Screen               = CStruct0 //todo: 257
  type _XrmHashBucketRec    = CStruct0
  type XOM                  = CStruct0

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
  def XInternAtom(display: Display, atom_name: /*const*/ CString, only_if_exists: Bool): Atom       = extern //1536
  def XInternAtoms(dpy: Display,
                   names: Ptr[CString],
                   count: CInt,
                   onlyIfExists: Bool,
                   atoms_return: Ptr[Atom]): Status                                       = extern //1541
  def XCopyColormapAndFree(display: Display, colormap: Colormap): Colormap                = extern //1548
  def XCreateColormap(display: Display, w: Window, visual: Visual, alloc: CInt): Colormap = extern //1552
  def XCreatePixmapCursor(display: Display,
                          source: Pixmap,
                          mask: Pixmap,
                          foreground_color: Ptr[XColor],
                          background_color: Ptr[XColor],
                          x: CUnsignedInt,
                          y: CUnsignedInt): Cursor = extern //1558
  def XCreateGlyphCursor(display: Display,
                         source_font: Font,
                         mask_font: Font,
                         source_char: CUnsignedInt,
                         mask_char: CUnsignedInt,
                         foreground_color: /*const*/ Ptr[XColor],
                         background_color: /*const*/ Ptr[XColor]): Cursor = extern //1567
  def XCreateFontCursor(display: Display, shape: CUnsignedInt): Cursor    = extern //1576
  def XLoadFont(display: Display, name: /*const*/ CString): Font          = extern //1580
  def XCreateGC(display: Display, d: Drawable, valuemask: CUnsignedLong, values: Ptr[XGCValues]): GC =
    extern //1584
  def XGContextFromGC(gc: GC): GContext        = extern //1590
  def XFlushGC(display: Display, gc: GC): Unit = extern //1593
  def XCreatePixmap(display: Display,
                    d: Drawable,
                    width: CUnsignedInt,
                    height: CUnsignedInt,
                    depth: CUnsignedInt): Pixmap = extern //1597
  def XCreateBitmapFromData(display: Display,
                            d: Drawable,
                            data: /*const*/ CString,
                            width: CUnsignedInt,
                            height: CUnsignedInt): Pixmap = extern //1604
  def XCreatePixmapFromBitmapData(display: Display,
                                  d: Drawable,
                                  data: CString,
                                  width: CUnsignedInt,
                                  height: CUnsignedInt,
                                  fg: CUnsignedLong,
                                  bg: CUnsignedLong,
                                  depth: CUnsignedInt): Pixmap = extern //1611
  def XCreateSimpleWindow(display: Display,
                          parent: Window,
                          x: CInt,
                          y: CInt,
                          width: CUnsignedInt,
                          height: CUnsignedInt,
                          border_width: CUnsignedInt,
                          border: CUnsignedLong,
                          background: CUnsignedLong): Window        = extern //1621
  def XGetSelectionOwner(display: Display, selection: Atom): Window = extern //1632
  def XCreateWindow(display: Display,
                    parent: Window,
                    x: CInt,
                    y: CInt,
                    width: CUnsignedInt,
                    height: CUnsignedInt,
                    border_width: CUnsignedInt,
                    depth: CInt,
                    clas: CUnsignedInt,
                    visual: Visual,
                    valuemask: CUnsignedLong,
                    attributes: Ptr[XSetWindowAttributes]): Window                               = extern //1636
  def XListInstalledColormaps(display: Display, w: Window, num_return: Ptr[CInt]): Ptr[Colormap] = extern //1650
  def XListFonts(display: Display,
                 pattern: /*const*/ CString,
                 maxnames: CInt,
                 actual_count_return: Ptr[CInt]): Ptr[CString] = extern //1655
  def XListFontsWithInfo(display: Display,
                         pattern: /*const*/ CString,
                         maxnames: CInt,
                         count_return: Ptr[CInt],
                         info_return: Ptr[Ptr[XFontStruct]]): Ptr[CString]                = extern //1661
  def XGetFontPath(display: Display, npaths_return: Ptr[CInt]): Ptr[CString]              = extern //1668
  def XListExtensions(display: Display, nextensions_return: Ptr[CInt]): Ptr[CString]      = extern //1672
  def XListProperties(display: Display, w: Window, num_prop_return: Ptr[CInt]): Ptr[Atom] = extern //1676
  def XListHosts(display: Display, nhosts_return: Ptr[CInt], state_return: Ptr[Bool]): Ptr[XHostAddress] =
    extern //1681

  def XImageByteOrder(display: Display): CInt                      = extern //2774
  def XInstallColormap(display: Display, colormap: Colormap): CInt = extern //2778
  def XKeysymToKeycode(display: Display, keysym: KeySym): KeyCode  = extern //2783
  def XKillClient(display: Display, resource: XID): CInt           = extern //2788
  def XLookupColor(display: Display,
                   colormap: Colormap,
                   color_name: /*const*/ CString,
                   exact_def_return: Ptr[XColor],
                   screen_def_return: Ptr[XColor]): Status                        = extern //2793
  def XLowerWindow(display: Display, w: Window): CInt                             = extern //2801
  def XMapRaised(display: Display, w: Window): CInt                               = extern //2806
  def XMapSubwindows(display: Display, w: Window): CInt                           = extern //2811
  def XMapWindow(display: Display, w: Window): CInt                               = extern //2816
  def XMaskEvent(display: Display, event_mask: CLong, event_return: XEvent): CInt = extern //2821
  def XMaxCmapsOfScreen(screen: Ptr[Screen]): CInt                                = extern //2827
  def XMinCmapsOfScreen(screen: Ptr[Screen]): CInt                                = extern //2831
  def XMoveResizeWindow(display: Display,
                        w: Window,
                        x: CInt,
                        y: CInt,
                        width: CUnsignedInt,
                        height: CUnsignedInt): CInt                    = extern //2835
  def XMoveWindow(display: Display, w: Window, x: CInt, y: CInt): CInt = extern //2844
  def XNextEvent(display: Display, event_return: XEvent): CInt         = extern //2851
  def XNoOp(display: Display): CInt                                    = extern //2856
  def XParseColor(display: Display,
                  colormap: Colormap,
                  spec: /*const*/ CString,
                  exact_def_return: Ptr[XColor]): Status = extern //2860
  def XParseGeometry(parsestring: /*const*/ CString,
                     x_return: Ptr[CInt],
                     y_return: Ptr[CInt],
                     width_return: Ptr[CUnsignedInt],
                     height_return: Ptr[CUnsignedInt]): CInt = extern //2867
  def XPending(display: Display): CInt                       = extern //2891

  def XQueryPointer(display: Display,
                    w: Window,
                    root_return: Ptr[Window],
                    child_return: Ptr[Window],
                    root_x_return: Ptr[CInt],
                    root_y_return: Ptr[CInt],
                    win_x_return: Ptr[CInt],
                    win_y_return: Ptr[CInt],
                    mask_return: Ptr[CUnsignedInt]): Bool = extern //2993
  def XQueryTextExtents(display: Display,
                        font_ID: XID,
                        string: /*const*/ CString,
                        nchars: CInt,
                        direction_return: Ptr[CInt],
                        font_ascent_return: Ptr[CInt],
                        font_descent_return: Ptr[CInt],
                        overall_return: Ptr[XCharStruct]): CInt = extern //3005
  def XQueryTextExtents16(display: Display,
                          font_ID: XID,
                          string: Ptr[ /*const*/ XChar2b],
                          nchars: CInt,
                          direction_return: Ptr[CInt],
                          font_ascent_return: Ptr[CInt],
                          font_descent_return: Ptr[CInt],
                          overall_return: Ptr[XCharStruct]): CInt = extern //3016
  def XQueryTree(display: Display,
                 w: Window,
                 root_return: Ptr[Window],
                 parent_return: Ptr[Window],
                 children_return: Ptr[Ptr[Window]],
                 nchildren_return: Ptr[CUnsignedInt]): Status = extern //3027
  def XRaiseWindow(display: Display, w: Window): CInt         = extern //3036
  def XReadBitmapFile(display: Display,
                      d: Drawable,
                      filename: /*const*/ CString,
                      width_return: Ptr[CUnsignedInt],
                      height_return: Ptr[CUnsignedInt],
                      bitmap_return: Ptr[Pixmap],
                      x_hot_return: Ptr[CInt],
                      y_hot_return: Ptr[CInt]): CInt = extern //3041
  def XReadBitmapFileData(filename: /*const*/ CString,
                          width_return: Ptr[CUnsignedInt],
                          height_return: Ptr[CUnsignedInt],
                          data_return: Ptr[Ptr[CUnsignedChar]],
                          x_hot_return: Ptr[CInt],
                          y_hot_return: Ptr[CInt]): CInt = extern //3052
  def XRebindKeysym(display: Display,
                    keysym: KeySym,
                    list: Ptr[KeySym],
                    mod_count: CInt,
                    string: Ptr[ /*const*/ CUnsignedChar],
                    bytes_string: CInt): CInt = extern //3061
  def XRecolorCursor(display: Display,
                     cursor: Cursor,
                     foreground_color: Ptr[XColor],
                     background_color: Ptr[XColor]): CInt                                         = extern //3070
  def XRefreshKeyboardMapping(event_map: Ptr[XMappingEvent]): CInt                                = extern //3077
  def XRemoveFromSaveSet(display: Display, w: Window): CInt                                       = extern //3081
  def XRemoveHost(display: Display, host: Ptr[XHostAddress]): CInt                                = extern //3086
  def XRemoveHosts(display: Display, hosts: Ptr[XHostAddress], num_hosts: CInt): CInt             = extern //3091
  def XReparentWindow(display: Display, w: Window, parent: Window, x: CInt, y: CInt): CInt        = extern //3097
  def XResetScreenSaver(display: Display): CInt                                                   = extern //3105
  def XResizeWindow(display: Display, w: Window, width: CUnsignedInt, height: CUnsignedInt): CInt = extern //3109
  def XRestackWindows(display: Display, windows: Ptr[Window], nwindows: CInt): CInt               = extern //3116
  def XRotateBuffers(display: Display, rotate: CInt): CInt                                        = extern //3122
  def XRotateWindowProperties(display: Display,
                              w: Window,
                              properties: Ptr[Atom],
                              num_prop: CInt,
                              npositions: CInt): CInt                    = extern //3127
  def XScreenCount(display: Display): CInt                               = extern //3135
  def XSelectInput(display: Display, w: Window, event_mask: CLong): CInt = extern //3139
  def XSendEvent(display: Display, w: Window, propagate: Bool, event_mask: CLong, event_send: XEvent): Status =
    extern //3145
  def XSetAccessControl(display: Display, mode: CInt): CInt                                    = extern //3153
  def XSetArcMode(display: Display, gc: GC, arc_mode: CInt): CInt                              = extern //3158
  def XSetBackground(display: Display, gc: GC, background: CUnsignedLong): CInt                = extern //3164
  def XSetClipMask(display: Display, gc: GC, pixmap: Pixmap): CInt                             = extern //3170
  def XSetClipOrigin(display: Display, gc: GC, clip_x_origin: CInt, clip_y_origin: CInt): CInt = extern //3176
  def XSetClipRectangles(display: Display,
                         gc: GC,
                         clip_x_origin: CInt,
                         clip_y_origin: CInt,
                         rectangles: Ptr[XRectangle],
                         n: CInt,
                         ordering: CInt): CInt                                       = extern //3183
  def XSetCloseDownMode(display: Display, close_mode: CInt): CInt                    = extern //3193
  def XSetCommand(display: Display, w: Window, argv: Ptr[CString], argc: CInt): CInt = extern //3198
  def XSetDashes(display: Display, gc: GC, dash_offset: CInt, dash_list: /*const*/ CString, n: CInt): CInt =
    extern //3205
  def XSetFillRule(display: Display, gc: GC, fill_rule: CInt): CInt                      = extern //3213
  def XSetFillStyle(display: Display, gc: GC, fill_style: CInt): CInt                    = extern //3219
  def XSetFont(display: Display, gc: GC, font: Font): CInt                               = extern //3225
  def XSetFontPath(display: Display, directories: Ptr[CString], ndirs: CInt): CInt       = extern //3231
  def XSetForeground(display: Display, gc: GC, foreground: CUnsignedLong): CInt          = extern //3237
  def XSetFunction(display: Display, gc: GC, function: CInt): CInt                       = extern //3243
  def XSetGraphicsExposures(display: Display, gc: GC, graphics_exposures: Bool): CInt    = extern //3249
  def XSetIconName(display: Display, w: Window, icon_name: /*const*/ CString): CInt      = extern //3255
  def XSetInputFocus(display: Display, focus: Window, revert_to: CInt, time: Time): CInt = extern //3261
  def XSetLineAttributes(display: Display,
                         gc: GC,
                         line_width: CUnsignedInt,
                         line_style: CInt,
                         cap_style: CInt,
                         join_style: CInt): CInt                                                 = extern //3268
  def XSetModifierMapping(display: Display, modmap: Ptr[XModifierKeymap]): CInt                  = extern //3277
  def XSetPlaneMask(display: Display, gc: GC, plane_mask: CUnsignedLong): CInt                   = extern //3282
  def XSetPointerMapping(display: Display, map: Ptr[ /*const*/ CUnsignedChar], nmap: CInt): CInt = extern //3288
  def XSetScreenSaver(display: Display,
                      timeout: CInt,
                      interval: CInt,
                      prefer_blanking: CInt,
                      allow_exposures: CInt): CInt                                           = extern //3294
  def XSetSelectionOwner(display: Display, selection: Atom, owner: Window, time: Time): CInt = extern //3302
  def XSetState(display: Display,
                gc: GC,
                foreground: CUnsignedLong,
                background: CUnsignedLong,
                function: CInt,
                plane_mask: CUnsignedLong): CInt                                                  = extern //3309
  def XSetStipple(display: Display, gc: GC, stipple: Pixmap): CInt                                = extern //3318
  def XSetSubwindowMode(display: Display, gc: GC, subwindow_mode: CInt): CInt                     = extern //3324
  def XSetTSOrigin(display: Display, gc: GC, ts_x_origin: CInt, ts_y_origin: CInt): CInt          = extern //3330
  def XSetTile(display: Display, gc: GC, tile: Pixmap): CInt                                      = extern //3337
  def XSetWindowBackground(display: Display, w: Window, background_pixel: CUnsignedLong): CInt    = extern //3343
  def XSetWindowBackgroundPixmap(display: Display, w: Window, background_pixmap: Pixmap): CInt    = extern //3349
  def XSetWindowBorder(display: Display, w: Window, border_pixel: CUnsignedLong): CInt            = extern //3355
  def XSetWindowBorderPixmap(display: Display, w: Window, border_pixmap: Pixmap): CInt            = extern //3361
  def XSetWindowBorderWidth(display: Display, w: Window, width: CUnsignedInt): CInt               = extern //3367
  def XSetWindowColormap(display: Display, w: Window, colormap: Colormap): CInt                   = extern //3373
  def XStoreBuffer(display: Display, bytes: /*const*/ CString, nbytes: CInt, buffer: CInt): CInt  = extern //3379
  def XStoreBytes(display: Display, bytes: /*const*/ CString, nbytes: CInt): CInt                 = extern //3386
  def XStoreColor(display: Display, colormap: Colormap, color: Ptr[XColor]): CInt                 = extern //3392
  def XStoreColors(display: Display, colormap: Colormap, color: Ptr[XColor], ncolors: CInt): CInt = extern //3398
  def XStoreName(display: Display, w: Window, window_name: /*const*/ CString): CInt               = extern //3405
  def XStoreNamedColor(display: Display,
                       colormap: Colormap,
                       color: /*const*/ CString,
                       pixel: CUnsignedLong,
                       flags: CInt): CInt          = extern //3411
  def XSync(display: Display, discard: Bool): CInt = extern //3419
  def XTextExtents(font_struct: Ptr[XFontStruct],
                   string: /*const*/ CString,
                   nchars: CInt,
                   direction_return: Ptr[CInt],
                   font_ascent_return: Ptr[CInt],
                   font_descent_return: Ptr[CInt],
                   overall_return: Ptr[XCharStruct]): CInt = extern //3424
  def XTextExtents16(font_struct: Ptr[XFontStruct],
                     string: Ptr[ /*const*/ XChar2b],
                     nchars: CInt,
                     direction_return: Ptr[CInt],
                     font_ascent_return: Ptr[CInt],
                     font_descent_return: Ptr[CInt],
                     overall_return: Ptr[XCharStruct]): CInt                                          = extern //3434
  def XTextWidth(font_struct: Ptr[XFontStruct], string: /*const*/ CString, count: CInt): CInt         = extern //3444
  def XTextWidth16(font_struct: Ptr[XFontStruct], string: Ptr[ /*const*/ XChar2b], count: CInt): CInt = extern //3450
  def XTranslateCoordinates(display: Display,
                            src_w: Window,
                            dest_w: Window,
                            src_x: CInt,
                            src_y: CInt,
                            dest_x_return: Ptr[CInt],
                            dest_y_return: Ptr[CInt],
                            child_return: Ptr[Window]): Bool = extern //3456
  def XUndefineCursor(display: Display, w: Window): CInt     = extern //3467
  def XUngrabButton(display: Display, button: CUnsignedInt, modifiers: CUnsignedInt, grab_window: Window): CInt =
    extern //3472
  def XUngrabKey(display: Display, keycode: CInt, modifiers: CUnsignedInt, grab_window: Window): CInt =
    extern //3479
  def XUngrabKeyboard(display: Display, time: Time): CInt            = extern //3486
  def XUngrabPointer(display: Display, time: Time): CInt             = extern //3491
  def XUngrabServer(display: Display): CInt                          = extern //3496
  def XUninstallColormap(display: Display, colormap: Colormap): CInt = extern //3500
  def XUnloadFont(display: Display, font: Font): CInt                = extern //3505
  def XUnmapSubwindows(display: Display, w: Window): CInt            = extern //3510
  def XUnmapWindow(display: Display, w: Window): CInt                = extern //3515
  def XVendorRelease(display: Display): CInt                         = extern //3520
  def XWarpPointer(display: Display,
                   src_w: Window,
                   dest_w: Window,
                   src_x: CInt,
                   src_y: CInt,
                   src_width: CUnsignedInt,
                   src_height: CUnsignedInt,
                   dest_x: CInt,
                   dest_y: CInt): CInt                                                         = extern //3524
  def XWidthMMOfScreen(screen: Ptr[Screen]): CInt                                              = extern //3536
  def XWidthOfScreen(screen: Ptr[Screen]): CInt                                                = extern //3540
  def XWindowEvent(display: Display, w: Window, event_mask: CLong, event_return: XEvent): CInt = extern //3544
  def XWriteBitmapFile(display: Display,
                       filename: /*const*/ CString,
                       bitmap: Pixmap,
                       width: CUnsignedInt,
                       height: CUnsignedInt,
                       x_hot: CInt,
                       y_hot: CInt): CInt                            = extern //3551
  def XSupportsLocale(): Bool                                        = extern //3561
  def XSetLocaleModifiers(modifier_list: /*const*/ CString): CString = extern //3563
  def XOpenOM(display: Display,
              rdb: Ptr[_XrmHashBucketRec],
              res_name: /*const*/ CString,
              res_class: /*const*/ CString): XOM = extern //3567
  def XCloseOM(om: XOM): Status                  = extern //3574

  // macros

  @name("xlib_DefaultScreen")
  def DefaultScreen(display: Display): CInt = extern //93
  @name("xlib_DefaultRootWindow")
  def DefaultRootWindow(display: Display): CInt = extern //94

}
