package me.earth.earthhack.impl.util.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.util.text.ChatUtil;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.ByteBuffer;

// thanks to Mironov and the demo <a href="https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/nanovg/"></a>

@SuppressWarnings("unused")
public class NVGRenderer implements Globals {

    private final ModuleCache<FontMod> CUSTOM_FONT =
            Caches.getModule(FontMod.class);

    private static final float BLUR = 0.0f;

    private long context = 0;
    private boolean init = false;

    private ByteBuffer buf = null;
    private int id = -1;

    private NVGColor blackColor = null;

    public void initialize() {
        if (blackColor == null) {
            blackColor = NVGColor.calloc();
            blackColor.r(0.0f);
            blackColor.g(0.0f);
            blackColor.b(0.0f);
            blackColor.a(0.25f);
        }
        context = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS);
        System.out.println("NanoVG context: " + context);

        try {
            byte[] fontBytes = CUSTOM_FONT.get().getSelectedFont();

            destroyBuffer();
            buf = MemoryUtil.memAlloc(fontBytes.length);
            buf.put(fontBytes);
            buf.flip();

            if (NanoVG.nvgCreateFontMem(context, CUSTOM_FONT.get().fontName.getValue(), buf, false) == -1)
                throw new RuntimeException("Failed to create font " + CUSTOM_FONT.get().fontName.getValue());

            // font id
            id = NanoVG.nvgFindFont(context, CUSTOM_FONT.get().fontName.getValue());
            if (id == -1) {
                CUSTOM_FONT.disable();
                ChatUtil.sendMessage("Failed to find font " + CUSTOM_FONT.get().fontName.getValue() + " in memory", "FontMod");
            }

            System.out.println("Loaded font " + CUSTOM_FONT.get().fontName.getValue() + " into memory");
            init = true;
        } catch (Exception e) {
            e.printStackTrace();
            CUSTOM_FONT.disable();
            ChatUtil.sendMessage("Failed to load font " + CUSTOM_FONT.get().fontName.getValue() + " into memory", "FontMod");
        }
    }

    public void destroyBuffer() {
        if (buf != null) {
            MemoryUtil.memFree(buf);
            buf = null;
        }
    }

    private void textSized(String text, float x, float y, float size, NVGColor color) {
        NanoVG.nvgBeginPath(context);

        NanoVG.nvgFontFaceId(context, id);
        NanoVG.nvgFillColor(context, color);
        NanoVG.nvgFontSize(context, size);
        NanoVG.nvgFontBlur(context, BLUR);
        NanoVG.nvgTextAlign(context, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        NanoVG.nvgText(context, x, y, text);

        NanoVG.nvgClosePath(context);
    }

    private void textSizedShadow(String text, float x, float y, float size, NVGColor color, Color shadowColor) {
        NanoVG.nvgBeginPath(context);

        NanoVG.nvgFontFaceId(context, id);
        NanoVG.nvgFontSize(context, size);
        NanoVG.nvgTextAlign(context, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);

        NanoVG.nvgFontBlur(context, BLUR + (CUSTOM_FONT.get().blurShadow.getValue() ? 1.0f : 0.0f));
        NanoVG.nvgFillColor(context, blackColor);
        NanoVG.nvgText(context, x + CUSTOM_FONT.get().shadowOffset.getValue(), y + CUSTOM_FONT.get().shadowOffset.getValue(), text);

        NanoVG.nvgFontBlur(context, BLUR);
        NanoVG.nvgFillColor(context, color);
        NanoVG.nvgText(context, x, y, text);

        NanoVG.nvgClosePath(context);
    }

    public void drawRect(float x, float y, float x2, float y2, int color) {
        NanoVG.nvgBeginPath(context);
        NanoVG.nvgRect(context, x, y, x2 - x, y2 - y);
        NanoVG.nvgFillColor(context, getColorNVG(color));
        NanoVG.nvgFill(context);
        NanoVG.nvgClosePath(context);
    }

    public void drawText(String text, float x, float y, float size, Color color, boolean shadow) {
        String[] textParts = text.split("§");
        boolean colorChanged = false;
        Color oldColor = color;

        for (String s : textParts) {
            if (s.isEmpty() || s.equals("f"))
                continue;

            if (s.length() < 2) {
                if (shadow)
                    textSizedShadow(s, x, y, size, getColorNVG(color), color);
                else
                    textSized(s, x, y, size, getColorNVG(color));
                x += getWidth(s);
                continue;
            }

            char c = s.charAt(0);
            switch (c) {
                case 'r':
                    color = oldColor;
                    break;
                case '0':
                    color = Color.BLACK;
                    break;
                case '1':
                    color = new Color(170);
                    break;
                case '2':
                    color = new Color(43520);
                    break;
                case '3':
                    color = new Color(43690);
                    break;
                case '4':
                    color = new Color(11141120);
                    break;
                case '5':
                    color = new Color(11141290);
                    break;
                case '6':
                    color = new Color(16755200);
                    break;
                case '7':
                    color = Color.GRAY;
                    break;
                case '8':
                    color = Color.DARK_GRAY;
                    break;
                case '9':
                    color = Color.BLUE;
                    break;
                case 'a':
                    color = Color.GREEN;
                    break;
                case 'b':
                    color = new Color(5636095);
                    break;
                case 'c':
                    color = Color.RED;
                    break;
                case 'd':
                    color = new Color(16733695);
                    break;
                case 'e':
                    color = Color.YELLOW;
                    break;
                case 'f':
                    color = Color.WHITE;
                    break;
                case 'l':
                    size += 1;
                    break;
                case 'm':
                    size -= 1;
                    break;
                case 'n':
                    shadow = true;
                    break;
                case 'o':
                    shadow = false;
                    break;
            }

            if (color.getRGB() != oldColor.getRGB())
                colorChanged = true;

            String text1 = colorChanged ? s.substring(1) : s;
            if (shadow)
                textSizedShadow(text1, x, y, size, getColorNVG(color), color);
            else
                textSized(text1, x, y, size, getColorNVG(color));
            x += getWidth(text1);
            if (s.charAt(s.length() - 1) == ' ') {
                x += getWidth("a");
            }
        }
    }

    public void drawGradientRect(float x, float y, float w, float h, int startColor, int endColor) {
        NVGPaint paint = NVGPaint.create();

        NanoVG.nvgLinearGradient(context, x, y, x + w, y + h, getColorNVG(startColor), getColorNVG(endColor), paint);
        NanoVG.nvgBeginPath(context);
        NanoVG.nvgRect(context, x, y, w, h);
        NanoVG.nvgFillPaint(context, paint);
        NanoVG.nvgFill(context);
    }

    public void drawRoundedRect(float x, float y, float w, float h, float r, int color) {
        NanoVG.nvgBeginPath(context);
        NanoVG.nvgRoundedRect(context, x, y, w, h, r);
        NanoVG.nvgFillColor(context, getColorNVG(color));
        NanoVG.nvgFill(context);
        NanoVG.nvgClosePath(context);
    }

    public void drawLine(float x, float y, float x2, float y2, float w, int color) {
        NanoVG.nvgBeginPath(context);
        NanoVG.nvgMoveTo(context, x, y);
        NanoVG.nvgLineTo(context, x2, y2);
        NanoVG.nvgStrokeWidth(context, w);
        NanoVG.nvgStrokeColor(context, getColorNVG(color));
        NanoVG.nvgStroke(context);
        NanoVG.nvgClosePath(context);
    }

    public void drawScissor(float x, float y, float w, float h) {
        //NanoVG.nvgScissor(context, 100, 100, 100, 100);
        //drawRect(100, 100, 100 + w, 100 + h, new Color(255, 50, 0, 120).getRGB());
    }

    public void beginScissor(int x, int y, int w, int h) {
        NanoVG.nvgScissor(context, x, y, w, h);
    }

    public void endScissor() {
        NanoVG.nvgResetScissor(context);
    }

    public static NVGColor getColorNVG(Color color) {
        NVGColor clr = NVGColor.create();
        clr.r(color.getRed() / 255.0f);
        clr.g(color.getGreen() / 255.0f);
        clr.b(color.getBlue() / 255.0f);
        clr.a(color.getAlpha() / 255.0f);
        return clr;
    }

    public static NVGColor getColorNVG(int color) {
        NVGColor clr = NVGColor.create();
        clr.r((color >> 16 & 255) / 255.0f);
        clr.g((color >> 8 & 255) / 255.0f);
        clr.b((color & 255) / 255.0f);
        clr.a((color >> 24 & 255) / 255.0f);
        return clr;
    }

    public float getWidth(String text) {
        float[] bounds = new float[4];
        NanoVG.nvgTextBounds(context, 0, 0, text, bounds);
        return bounds[2] - bounds[0];
    }

    public float getHeight() {
        float[] bounds = new float[4];
        NanoVG.nvgTextBounds(context, 0, 0, "Aa", bounds);
        return bounds[3] - bounds[1];
    }

    public void startDraw() {
        NanoVG.nvgBeginFrame(context, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), CUSTOM_FONT.get().pixelRatio.getValue());
    }

    public void endDraw() {
        NanoVG.nvgEndFrame(context);
    }

    public boolean isInitialized() {
        return init;
    }

}