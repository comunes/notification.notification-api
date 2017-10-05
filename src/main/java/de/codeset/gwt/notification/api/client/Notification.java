package de.codeset.gwt.notification.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import de.codeset.gwt.notification.api.client.eventing.click.HasNotificationClickHandlers;
import de.codeset.gwt.notification.api.client.eventing.click.NotificationClickEvent;
import de.codeset.gwt.notification.api.client.eventing.click.NotificationClickHandler;
import de.codeset.gwt.notification.api.client.eventing.close.HasNotificationCloseHandlers;
import de.codeset.gwt.notification.api.client.eventing.close.NotificationCloseEvent;
import de.codeset.gwt.notification.api.client.eventing.close.NotificationCloseHandler;
import de.codeset.gwt.notification.api.client.eventing.error.HasNotificationErrorHandlers;
import de.codeset.gwt.notification.api.client.eventing.error.NotificationErrorEvent;
import de.codeset.gwt.notification.api.client.eventing.error.NotificationErrorHandler;
import de.codeset.gwt.notification.api.client.eventing.show.HasNotificationShowHandlers;
import de.codeset.gwt.notification.api.client.eventing.show.NotificationShowEvent;
import de.codeset.gwt.notification.api.client.eventing.show.NotificationShowHandler;

/**
 * @author Marcel Konopka
 *
 * @see <a href="https://github.com/MarZl/notification-api">https://github.com/MarZl/notification-api</a>
 *
 */
public class Notification implements HasNotificationClickHandlers, HasNotificationErrorHandlers, HasNotificationShowHandlers, HasNotificationCloseHandlers {

  private NativeNotification nativeNotification;
  private HandlerManager handlerManager;
  private boolean initHandlers;

  private Notification(String title, NotificationOptions options) {
    nativeNotification = NativeNotification.create(this, title, options);
  }

  /**
   * Return a new Notification if supported, and null otherwise.
   *
   * @param title
   *            The title that must be shown within the notification
   * @param options
   *            (optional) An object that allows to configure the notification
   */
  public static Notification createIfSupported(String title, NotificationOptions options){
    if(isSupported())
      return new Notification(title, options);
    return null;
  }


  /**
   * Returns if the browser supports Notifications.
   *
   * @return supported
   */
  public static boolean isSupported(){
    return NativeNotification.isSupported();
  }

  /**
   * This method is used to ask the user if he allows the page to display
   * notifications.
   *
   * @param callback
   */
  public static void requestPermission(NotificationPermissionCallback callback) {
    NativeNotification.requestPermission(callback);
  }

  /**
   * A string representing the current permission to display notifications.
   * Possible value are: denied (the user refuses to have notifications
   * displayed), granted (the user accepts to have notifications displayed),
   * or default (the user choice is unknown and therefore the browser will act
   * as if the value was denied).
   *
   * @return permission
   */
  public static NotificationPermission getPermission() {
    return NotificationPermission.fromString(NativeNotification.getPermission());
  }


  /**
   * The title of the notification as specified in the options parameter of the constructor.
   *
   * @return title
   */
  public String getTitle() {
    return nativeNotification.getTitle();
  }

  /**
   * The text direction of the notification as specified in the options parameter of the constructor.
   *
   * @return dir
   */
  public String getDir() {
    return nativeNotification.getDir();
  }


  /**
   * The language code of the notification as specified in the options parameter of the constructor.
   *
   * @return lang
   */
  public String getLang() {
    return nativeNotification.getLang();
  }

  /**
   * The body string of the notification as specified in the options parameter of the constructor.
   *
   * @return
   */
  public String getBody() {
    return nativeNotification.getBody();
  }


  /**
   * The ID of the notification (if any) as specified in the options parameter of the constructor.
   *
   * @return
   */
  public String getTag() {
    return nativeNotification.getTag();
  }

  /**
   * The URL of the image used as an icon of the notification as specified in the options parameter of the constructor.
   *
   * @return icon
   */
  public String getIcon() {
    return nativeNotification.getIcon();
  }


   /**
   * The  URL of an image to be displayed as part of the notification, as specified in the options parameter of the constructor.
   *
   * @return image
   */
  public String getImage() {
    return nativeNotification.getImage();
  }

  /**
   * Returns a structured clone of the notification’s data.
   *
   * @return data
   */
  public String getData() {
    return nativeNotification.getData();
  }

  /**
   * A Boolean indicating that on devices with sufficiently large screens, a notification should remain active until the user clicks or dismisses it.
   *
   * @return requireInteraction
   */
  public boolean isInteractionRequired() {
    return nativeNotification.getRequireInteraction();
  }

  /**
   * Specifies whether the notification should be silent, i.e. no sounds or vibrations should be issued, regardless of the device settings.
   *
   * @return silent
   */
  public boolean isSilent() {
    return nativeNotification.getSilent();
  }

  /**
   * This method allows to programmatically close a notification.
   */
  public void close() {
    nativeNotification.close();
  }

  // //////////

  protected HandlerManager ensureHandlers() {
    if(!initHandlers)
      initHandler();
    return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
  }

  protected final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
    return ensureHandlers().addHandler(type, handler);
  }

  // //////////

  @Override
  public void fireEvent(GwtEvent<?> event) {
    handlerManager.fireEvent(event);
  }

  // //////////

  @Override
  public HandlerRegistration addClickHandler(NotificationClickHandler handler) {
    return addHandler(handler, NotificationClickEvent.getType());
  }

  @Override
  public HandlerRegistration addErrorHandler(NotificationErrorHandler handler) {
    return addHandler(handler, NotificationErrorEvent.getType());
  }

  @Override
  @Deprecated
  public HandlerRegistration addCloseHandler(NotificationCloseHandler handler) {
    return addHandler(handler, NotificationCloseEvent.getType());
  }

  @Override
  @Deprecated
  public HandlerRegistration addShowHandler(NotificationShowHandler handler) {
    return addHandler(handler, NotificationShowEvent.getType());
  }

  private void initHandler() {
    initHandlers = true;

    nativeNotification.addEventListener("close", new Callback() {

      @Override
      public void call(Notification notification) {
        fireEvent(new NotificationCloseEvent(notification));
      }
    });
    nativeNotification.addEventListener("click", new Callback() {

      @Override
      public void call(Notification notification) {
        fireEvent(new NotificationClickEvent(notification));
      }
    });
    nativeNotification.addEventListener("error", new Callback() {

      @Override
      public void call(Notification notification) {
        fireEvent(new NotificationErrorEvent(notification));
      }
    });
    nativeNotification.addEventListener("show", new Callback() {

      @Override
      public void call(Notification notification) {
        fireEvent(new NotificationShowEvent(notification));
      }
    });
  }

  private static class NativeNotification extends JavaScriptObject {

    protected NativeNotification() {
    }

    public native static NativeNotification create(Notification notification, String title, JavaScriptObject options)/*-{
      nativeNotification = new $wnd.Notification(title, options);
      nativeNotification.gwtNotification = notification;
      return nativeNotification;
    }-*/;

    public native static boolean isSupported()/*-{
      return (typeof(Notification) == "function" && typeof(Notification.prototype) == "object");
    }-*/;

    public native static void requestPermission(NotificationPermissionCallback callback)/*-{
      $wnd.Notification.requestPermission(function(permission) {
        if(callback != null){
          var perm = @de.codeset.gwt.notification.api.client.NotificationPermission::fromString(Ljava/lang/String;)(permission);
          callback.@de.codeset.gwt.notification.api.client.Notification.NotificationPermissionCallback::call(Lde/codeset/gwt/notification/api/client/NotificationPermission;)(perm);
        }
      });
    }-*/;

    public native final static String getPermission()/*-{
      return $wnd.Notification.permission;
    }-*/;

    public native final String getTitle() /*-{
      return this.title;
    }-*/;

    public native final String getDir() /*-{
      return this.dir;
    }-*/;

    public native final String getLang() /*-{
      return this.lang;
    }-*/;

    public native final String getBody() /*-{
      return this.body;
    }-*/;

    public native final String getTag() /*-{
      return this.tag;
    }-*/;

    public native final String getIcon() /*-{
      return this.icon;
    }-*/;

    public native final String getImage() /*-{
    return this.image;
    }-*/;

    public native final String getData() /*-{
      return this.data;
    }-*/;

    public native final boolean getRequireInteraction() /*-{
      return this.requireInteraction;
    }-*/;

    public native final boolean getSilent() /*-{
      return this.silent;
    }-*/;

    public native final void close()/*-{
      this.close();
    }-*/;

    public native final void addEventListener(String event, Callback callback)/*-{
      this.addEventListener(event, function() {
        if(callback != null){
          callback.@de.codeset.gwt.notification.api.client.Notification.Callback::call(Lde/codeset/gwt/notification/api/client/Notification;)(this.gwtNotification);
        }
      });
    }-*/;
  }

  public static class NotificationOptions extends JavaScriptObject {

    protected NotificationOptions() {
    }

    public native static NotificationOptions create()/*-{
      return {};
    }-*/;

    /**
     * @param lang
     *            The code lang used by the notification as defined within
     *            the constructor options.
     * @return this
     */
    public native final NotificationOptions lang(String lang)/*-{
      this.lang = lang;
      return this;
    }-*/;

    /**
     * @param body
     *            The body string used by the notification as defined within
     *            the constructor options.
     * @return this
     */
    public native final NotificationOptions body(String body)/*-{
      this.body = body;
      return this;
    }-*/;

    /**
     * @param tag
     *            The id of the notification (if any) as defined within the
     *            constructor options.
     * @return this
     */
    public native final NotificationOptions tag(String tag)/*-{
      this.tag = tag;
      return this;
    }-*/;

    /**
     * @param icon
     *            The URL of the image used as an icon as defined within the
     *            constructor options.
     * @return this
     */
    public native final NotificationOptions icon(String icon)/*-{
      this.icon = icon;
      return this;
    }-*/;

    public native final NotificationOptions requireInteraction(Boolean requireInteraction )/*-{
      this.requireInteraction = requireInteraction;
      return this;
    }-*/;

    /**
     * @param image
     *            The  URL of an image to be displayed as part of the notification, as specified in the options parameter of the constructor.
     * @return this
     */
    public native final NotificationOptions image(String image)/*-{
      this.image = image;
      return this;
    }-*/;

    /**
     * @param dir
     *            The direction used by the notification as defined within
     *            the constructor options.
     * @return this
     */
    public native final NotificationOptions dir(String dir)/*-{
      this.dir = dir;
      return this;
    }-*/;
  }

  public static interface NotificationPermissionCallback {

    void call(NotificationPermission permission);
  }

  protected static interface Callback {

    void call(Notification notification);
  }
}