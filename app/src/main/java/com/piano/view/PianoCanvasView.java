package com.piano.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import com.piano.AppConfigTrigger;
import com.piano.Preferences;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import funs.common.tools.CLogger;
import funs.games.GfThreadPool;
import funs.games.PianoConst;

public class PianoCanvasView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "PianoCanvasView";

    private static final float BEVEL_RATIO = 0.1f;

    private float bevelWidth;
//    private final float bevelWidth;

    Piano piano;
    AppConfigTrigger appConfigHandler;
    private int screen_size_y, screen_size_x;
//    final int screen_size_y, screen_size_x;

    private WeakReference<Context> contextWRef;

    private int[] KEY_COLORS ;
    final int[] KEY_COLORS001 = new int[]{
            Color.rgb(210, 10, 10),     // Red
//            Color.rgb(255, 135, 0),   // Orange
            Color.rgb(245, 245, 50),   // Yellow
//            Color.rgb(245, 245, 0),   // Yellow
            Color.rgb(251, 251, 251),  // WHITE

            Color.rgb(80, 215, 40),   // Light Green
            Color.rgb(100, 80, 185),   // Purple
            Color.rgb(10, 155, 155),   // Dark Green

            Color.rgb(233, 53, 159),  // Pink

            Color.rgb(60, 120, 240),  // blue
//            Color.WHITE
    };

    /**
     * Note that light green, orange and yellow have higher lightness than other colours, so adding just a little white doesn't have
     * the desired effect. That is why they have a larger proportion of white added in.
     */
    private int[] PRESSED_KEY_COLORS ;
    final int[] PRESSED_KEY_COLORS001 = new int[]{
            ColorUtils.blendARGB(KEY_COLORS001[0], Color.WHITE, 0.5f),    // Red
//            ColorUtils.blendARGB(KEY_COLORS[1], Color.WHITE, 0.6f),    // Orange
            ColorUtils.blendARGB(KEY_COLORS001[1], Color.WHITE, 0.75f),   // Yellow
            ColorUtils.blendARGB(KEY_COLORS001[2], Color.rgb(205, 205, 205), 0.5f),    // WHITE

            ColorUtils.blendARGB(KEY_COLORS001[3], Color.WHITE, 0.6f),    // Light Green
            ColorUtils.blendARGB(KEY_COLORS001[4], Color.WHITE, 0.5f),    // Purple
            ColorUtils.blendARGB(KEY_COLORS001[5], Color.WHITE, 0.5f),    // Dark Green

            ColorUtils.blendARGB(KEY_COLORS001[6], Color.WHITE, 0.5f),    // Pink

            ColorUtils.blendARGB(KEY_COLORS001[7], Color.WHITE, 0.5f),    // blue
    };

    final Map<Integer, Integer> touch_pointer_to_keys = new HashMap<>();

    final Paint emojiPaint = new Paint(); // 自定义
    final Paint textPaint = new Paint(); // 自定义

    public PianoCanvasView(Context context) {
        this(context, null);
    }
    public PianoCanvasView(Context context, AttributeSet as) {
        this(context, as, 0);
    }

    public PianoCanvasView(Context context, AttributeSet as, int defStyle) {
        super(context, as, defStyle);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        contextWRef = new WeakReference<>(context);

        initEmojiPaint(context);
        initTextPaint(context);
        CLogger.i(TAG, "init [%d, %d], getParent height %d ", getWidth(), getHeight(), getCalculatedHeight());
    }

    private void initSurfaceScreen() {
        CLogger.d(TAG, "initSurfaceScreen() ");
        if (contextWRef == null || contextWRef.get() == null) {
            return;
        }
//        getContext();

        Context context = contextWRef.get();
        screen_size_x = getWidth();
        screen_size_y = getHeight();
        initKeyboardStyle(context);
//        final String soundset = "piano"; //Preferences.selectedSoundSet(context);
        CLogger.i(TAG, "initSurfaceScreen() , init Piano , [%d, %d]", screen_size_x, screen_size_y);
//        initPiano(context);
        this.piano = new Piano(context, screen_size_x, screen_size_y, getKeyWidth(), "");
//        this.piano = new Piano(context, screen_size_x, screen_size_y, soundset);
        this.bevelWidth = this.piano.get_keys_width() * BEVEL_RATIO;
        this.appConfigHandler = new AppConfigTrigger((AppCompatActivity) context);

//        initEmojiPaint(context);
        if (mKeyClickListener != null) {
            piano.setOnPianoKeyClickListener(mKeyClickListener);
        }
    }

    private void initKeyboardStyle(Context context) {
        final int colorStyle = Preferences.getKeyboardStyle(context);
        if (Preferences.KEYBOARD_STYLE_CLASSICAL == colorStyle) {
            KEY_COLORS = PianoConst.CLASSICAL_KEY_COLORS;
            PRESSED_KEY_COLORS = PianoConst.CLASSICAL_PRESSED_KEY_COLORS;
            return;
        }
        setKeyboardStyleColorful();
    }

    private void setKeyboardStyleColorful() {
        KEY_COLORS = PianoConst.COLORFUL_KEY_COLORS;
        PRESSED_KEY_COLORS = PianoConst.COLORFUL_PRESSED_KEY_COLORS;
    }

    private void initEmojiPaint(Context context) {
        emojiPaint.setColor(context.getResources().getColor(android.R.color.holo_orange_light));
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        float size = 22 * fontScale + 0.5f;
        emojiPaint.setTextSize(size);
        emojiPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initTextPaint(Context context) {
        textPaint.setColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        float size = 14 * fontScale + 0.5f;
        textPaint.setTextSize(size);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setConfigRequestCallback(AppConfigTrigger.AppConfigCallback cb) {
        this.appConfigHandler.setConfigRequestCallback(cb);
    }

    private Piano.OnPianoKeyClickListener mKeyClickListener;

    public void setOnPianoKeyClickListener(Piano.OnPianoKeyClickListener clickListener) {
        this.mKeyClickListener = clickListener;
        if (piano != null) {
            piano.setOnPianoKeyClickListener(clickListener);
        }
    }

    void draw_all_keys(final Canvas canvas) {
        /* Reset canvas */
        {
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            canvas.drawPaint(p);
        }

        if (KEY_COLORS == null || null == PRESSED_KEY_COLORS || KEY_COLORS.length <1 || PRESSED_KEY_COLORS.length <1) {
            setKeyboardStyleColorful();
        }

        for (int i = 0; i < piano.get_keys_count(); i += 2) {
            // Draw big key
            final int col_idx = (i / 2) % KEY_COLORS.length;
            Paint big_key_paint = new Paint();
            big_key_paint.setColor(piano.is_key_pressed(i) ? PRESSED_KEY_COLORS[col_idx] : KEY_COLORS[col_idx]);
            Piano.Key key = piano.get_area_for_key(i);
            if (key != null) {
                draw_key(canvas, key, big_key_paint);
//                CLogger.d(TAG,  String.format("Draw big_key_(%d), %s", i, key));

                piano.onDrawPianoKey(this, canvas, i, key);
            }
        }

        // Small keys drawn after big keys to ensure z-index
        for (int i = 1; i < piano.get_keys_count(); i += 2) {
            // Draw small key
            Paint flat_key_paint = new Paint();
            flat_key_paint.setColor(piano.is_key_pressed(i) ? Color.GRAY : 0xFF333333);
            Piano.Key key = piano.get_area_for_flat_key(i);
            if (key != null) {
                draw_key(canvas, key, flat_key_paint);
//                CLogger.d(TAG,  String.format("Draw flat_key_(%d), %s", i, key));
            }
        }

        appConfigHandler.onPianoRedrawFinish(this, canvas);
        piano.onPianoRedrawFinish(this, canvas);
    }

    void draw_key(final Canvas canvas, final Piano.Key rect, final Paint p) {
        // Draw the main (solid) background of the key.

        Rect r = new Rect();
        r.left = rect.x_i;
        r.right = rect.x_f;
        r.top = rect.y_i;
        r.bottom = rect.y_f;
        canvas.drawRect(r, p);

        // Now draw the bevels around the edge of each key.
        // Just the left, bottom, and right. The top of the key doesn't have a bevel.

        // Adjust this colour brighter or darker for the bevel.
        int base = p.getColor();

        // Left bevel
        // +---+
        // |   |
        // |   |
        // |   |
        // |   |
        // |   *
        // | *
        // *

        Path left = new Path();
        left.moveTo(r.left, r.top);
        left.lineTo(r.left, r.bottom);
        left.lineTo(r.left + bevelWidth, r.bottom - bevelWidth);
        left.lineTo(r.left + bevelWidth, r.top);
        left.lineTo(r.left, r.top);

        p.setColor(ColorUtils.blendARGB(base, Color.BLACK, 0.3f));
        canvas.drawPath(left, p);

        // Right bevel
        // +---+
        // |   |
        // |   |
        // |   |
        // |   |
        // *   |
        //   * |
        //     *

        Path right = new Path();
        right.moveTo(r.right, r.top);
        right.lineTo(r.right, r.bottom);
        right.lineTo(r.right - bevelWidth, r.bottom - bevelWidth);
        right.lineTo(r.right - bevelWidth, r.top);
        right.lineTo(r.right, r.top);

        p.setColor(ColorUtils.blendARGB(base, Color.WHITE, 0.2f));
        canvas.drawPath(right, p);

        //         Bottom bevel
        //          *---------*
        //       *                *
        //    *----------------------+

        Path bottom = new Path();
        bottom.moveTo(r.left, r.bottom);
        bottom.lineTo(r.right, r.bottom);
        bottom.lineTo(r.right - bevelWidth, r.bottom - bevelWidth);
        bottom.lineTo(r.left + bevelWidth, r.bottom - bevelWidth);
        bottom.lineTo(r.left, r.bottom);

        p.setColor(ColorUtils.blendARGB(base, Color.BLACK, 0.1f));
        canvas.drawPath(bottom, p);
    }

    /**
     * Draw something on a black key. Undefined if key_idx isn't black.
     */
    void draw_icon_on_black_key(final Canvas canvas, final Drawable icon, Integer key_idx,
                                final int icon_width, final int icon_height) {
        final Piano.Key key = piano.get_area_for_flat_key(key_idx);
        int icon_x = ((key.x_f - key.x_i) / 2) + key.x_i;
        int icon_y = icon_height;

        Rect r = new Rect();
        r.left = icon_x - (icon_width / 2);
        r.right = icon_x + (icon_width / 2);
        r.top = icon_y;
        r.bottom = icon_y + icon_height;

        icon.setBounds(r);
        icon.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
        icon.draw(canvas);
//        canvas.drawText();
    }

    void draw_emoji_on_black_key(final Canvas canvas, final String emoji, int key_idx) {
        final Piano.Key key = piano.get_area_for_flat_key(key_idx);
        int icon_x = ((key.x_f - key.x_i) / 2) + key.x_i;
        int icon_y = (key.y_f - key.y_i) * 5 / 8;

        canvas.drawText(emoji, icon_x, icon_y, emojiPaint);
        CLogger.d(TAG, "draw_emoji_on_black_key: " + key_idx);
    }

    void draw_emoji_on_white_key(final Canvas canvas, final String emoji, int key_idx) {
        draw_emoji_on_white_key(canvas, emoji, key_idx, 8f / 11);
    }

    void draw_emoji_on_white_key(final Canvas canvas, final String emoji, int key_idx, float h_rate) {
        draw_text_on_white_key(canvas, emojiPaint, emoji, key_idx, h_rate);
    }
    void draw_text_on_white_key(final Canvas canvas, final String emoji, int key_idx, float h_rate) {
        draw_text_on_white_key(canvas, textPaint, emoji, key_idx, h_rate); //  replace emojiPaint
    }
    void draw_text_on_white_key(final Canvas canvas, final Paint paint, final String emoji, int key_idx, float h_rate) {
        final Piano.Key key = piano.get_area_for_key(key_idx);
//        int width = piano.get_keys_width();
        int height = key.y_f - key.y_i;
        int icon_x = ((key.x_f - key.x_i) / 2) + key.x_i;
//        int icon_y = icon_height;
//        int left = icon_x;
        int top = (int) (height * h_rate); // 8/11; //  1/3 和 1/4 之间, 4/12 - 3/12, 3/11

        canvas.drawText(emoji, icon_x, top, paint);
//        canvas.drawText(emoji, icon_x, top, emojiPaint);
//        CLogger.d(TAG, "draw_emoji_on_white_key: " + key_idx);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        CLogger.d(TAG, "surfaceCreated() ");
        CLogger.d(TAG, "surfaceCreated getWidth: " + getWidth());
        CLogger.d(TAG, "surfaceCreated getHeight: " + getHeight());
        if (null == contextWRef || null == contextWRef.get()) {
            contextWRef = new WeakReference<>(getContext());
        }
        GfThreadPool.execute(() -> {
            initSurfaceScreen();
            redraw(surfaceHolder);
        });
    }

    private void redraw() {
        GfThreadPool.execute(() -> redraw(getHolder()));
    }

    private void redraw(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) return;
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) return;

        draw_all_keys(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    void on_key_cancel() {
        CLogger.d(TAG, "on_key_cancel()");
        piano.on_key_cancel();
        redraw();
    }

    void on_key_up(int key_idx) {
        CLogger.d(TAG, "Key " + key_idx + " is now UP");
        piano.on_key_up(key_idx);
        appConfigHandler.onKeyUp(key_idx);
        redraw();
    }

    void on_key_down(int key_idx) {
        CLogger.d(TAG, "Key " + key_idx + " is now DOWN");
        piano.on_key_down(key_idx);
//        piano.on_key_down(key_idx, this);
        appConfigHandler.onKeyPress(key_idx);
        redraw();
    }

    void resetPianoState() {
        // Something has gone wrong with the piano or canvas state, and our state is out of sync
        // with the real state of the world (eg somehow we missed a touch down or up event).
        // Try to reset the state and hope the app survives.
        touch_pointer_to_keys.clear();
        piano.resetState();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
        // Override this method to make linter happy
    }

    /*
    * java.lang.NullPointerException:
    * Attempt to invoke virtual method 'int com.piano.view.a.c(float, float)' on a null object reference
    *
    * at com.piano.view.PianoCanvasView.onTouchEvent(SourceFile:27)
	at android.view.View.dispatchTouchEvent(View.java:12513)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (piano == null) {
            return super.onTouchEvent(event);
        }
        int ptr_id = event.getPointerId(event.getActionIndex());
        int key_idx = piano.pos_to_key_idx(
                event.getX(event.getActionIndex()),
                event.getY(event.getActionIndex()));

        final int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_BUTTON_PRESS:   // fallthrough
                performClick();
            case MotionEvent.ACTION_DOWN:           // fallthrough
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (touch_pointer_to_keys.containsKey(ptr_id)) {
                    CLogger.e(TAG, "Touch-track error: Repeated touch-down event received");
                    resetPianoState();
                    return super.onTouchEvent(event);
                }

                // Mark key down ptr_id
                touch_pointer_to_keys.put(ptr_id, key_idx);
                on_key_down(key_idx);

                return true;
            }
//            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:     // fallthrough
            case MotionEvent.ACTION_UP: {
                if (!touch_pointer_to_keys.containsKey(ptr_id)) {
                    CLogger.e(TAG, "Touch-track error: Repeated touch-up event received");
                    resetPianoState();
                    return super.onTouchEvent(event);
                }

                touch_pointer_to_keys.remove(ptr_id);
                on_key_up(key_idx);

                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                // action_move is special, there is no *single* `getActionIndex`,
                // as *multiple* pointers could have moved, so we must check *each* pointer.
                // https://developer.android.com/develop/ui/views/touch-and-input/gestures/multi
                for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                    ptr_id = event.getPointerId(i);  // cf precalc'd ptr_id above switch
                    key_idx = piano.pos_to_key_idx(
                            event.getX(i),
                            event.getY(i));

                    if (!touch_pointer_to_keys.containsKey(ptr_id)) {
                        CLogger.e(TAG, "Touch-track error: Missed touch-up event");
                        resetPianoState();
                        return super.onTouchEvent(event);
                    }
                    // check if key changed
                    if (touch_pointer_to_keys.get(ptr_id) != key_idx) {
                        CLogger.e(TAG, "Moved to another key");
                        // Release key before storing new key_idx for new key down
                        on_key_up(touch_pointer_to_keys.get(ptr_id));
                        touch_pointer_to_keys.put(ptr_id, key_idx);
                        on_key_down(key_idx);
                    }
                }

                return true;
            }

            case MotionEvent.ACTION_CANCEL: {
                if (!touch_pointer_to_keys.containsKey(ptr_id)) {
                    CLogger.e(TAG, "Touch-track error: Repeated touch-up event received");
                    resetPianoState();
                    return super.onTouchEvent(event);
                }

                touch_pointer_to_keys.remove(ptr_id);
                on_key_cancel();

                return true;
            }
            default:
                CLogger.i(TAG, "onTouchEvent, actionMasked: " + actionMasked);
                return super.onTouchEvent(event);
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        int sWidth = getWidth(), sHeight = getHeight();
        CLogger.i(TAG, "surfaceChanged(), [%d, %d], (%d, %d, %d)", sWidth, sHeight, format, width, height);

        if (width <= KEY_WIDTH_DEFAULT || height <= KEY_HEIGHT_MIN || (width == screen_size_x && height == screen_size_y)) {
//            postInvalidate();
            return; // || (sWidth == screen_size_x && sHeight == screen_size_y)
        }
        GfThreadPool.execute(() -> {
            initSurfaceScreen();
            redraw(surfaceHolder);
        });
//        initSurfaceScreen();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (contextWRef != null) {
            contextWRef.clear();
        }
//        Piano.releaseSoundPool();
        GfThreadPool.unInit();
        CLogger.d(TAG, "surfaceDestroyed()");
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = getCalculatedWith(widthMeasureSpec);
        setMeasuredDimension(width, height);
        CLogger.i(TAG, "onMeasure( ), [%d, %d]", width, height);
    }

    private static final int KEY_HEIGHT_MIN = 501;
    private static final int KEY_WIDTH_DEFAULT = 202;

    private int getCalculatedWith(int widthMeasureSpec) {
        // 钢琴通常有88个键盘，其中黑键占据了36个，白键则有52个。
        return 52 * _getKeyWidth(widthMeasureSpec);
    }

    private int _getKeyWidth(int widthMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        return _calKeyWidth(width);
    }
    private int _calKeyWidth(int viewGroupWidth) {
        if (viewGroupWidth < 100) return KEY_WIDTH_DEFAULT;
        return Math.min(KEY_WIDTH_DEFAULT, viewGroupWidth / getMinNumberOfKeys());
    }

    public int getKeyWidth() {
        int keyWidth = KEY_WIDTH_DEFAULT;
        ViewParent parent = getParent();
        if (null != parent && parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            int w = group.getMeasuredWidth() - group.getPaddingLeft() - group.getPaddingRight();
            CLogger.i(TAG, "getKeyWidth(), parent getMeasuredWidth %d , parent getWidth %d , calc width %d",
                    group.getMeasuredWidth(), group.getWidth(), w);
            keyWidth = _calKeyWidth(w);
        }
        return keyWidth;
    }

    private int getMinNumberOfKeys() {
        if (contextWRef != null && null!=contextWRef.get()) {
            return Preferences.getMinNumberOfKeys(contextWRef.get());
        }
        return Preferences.DEFAULT_MIN_NUMBER_OF_KEYS;
    }

    private int getCalculatedHeight() {
        ViewParent parent = getParent();
        if (null != parent && parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            int h = group.getMeasuredHeight() - group.getPaddingBottom() - group.getPaddingTop();

            CLogger.i(TAG, "getCalculatedHeight(), parent MeasuredHeight %d , parent Height %d , calc height %d",
                    group.getMeasuredHeight(), group.getHeight(), h);
            if (h > 1) return h;
        }
        CLogger.d(TAG, "getCalculatedHeight() , getParent is not ViewGroup");
        return KEY_HEIGHT_MIN;
    }

    public void printInfo() {
        if (piano != null) {
            piano.printInfo();
        }
    }

    public void updateMelodyFromAssets(String fileName) {
        if (piano != null && null != contextWRef) {
            piano.updateMelodyFromAssets(contextWRef.get(), fileName);
        }
        redraw();
    }

    public int[] getKeyRangeOfMelody() {
        if (piano == null) {
            return null;
        }
        return piano.getKeyRangeOfMelody();
    }

    public void updateKeyboardStyle() {
        if (contextWRef != null && null != contextWRef.get()) initKeyboardStyle(contextWRef.get());
        redraw();
    }
}
