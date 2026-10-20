package qm.questui.client;

import java.util.List;

/** Pixel geometry, deliberately independent of Minecraft for exhaustive boundary probes. */
public final class HudGeometry {
    public static final int GAP = 6;
    public record Rect(int x, int y, int width, int height) {
        public int right() { return x + width; }
        public int bottom() { return y + height; }
        public boolean intersects(Rect other) {
            return width > 0 && height > 0 && other.width > 0 && other.height > 0
                    && x < other.right() && right() > other.x && y < other.bottom() && bottom() > other.y;
        }
        public boolean inside(int screenWidth, int screenHeight) {
            return x >= 0 && y >= 0 && right() <= screenWidth && bottom() <= screenHeight;
        }
    }
    public record Fit(Rect rect, float scale) {}
    private HudGeometry() {}

    public static int rightColumn(int width) { return Math.max(60, Math.min(164, width / 3)); }

    public static int mapSide(int width, int height) {
        return Math.max(36, Math.min(rightColumn(width) - 2 * GAP, (int)(height * 0.32)));
    }
    public static int stackTop(List<Rect> panels) {
        return panels.stream().filter(rect -> rect.width() > 0 && rect.height() > 0)
                .mapToInt(rect -> rect.bottom() + GAP).max().orElse(GAP);
    }
    public static Fit fitTooltip(int screenWidth, int screenHeight, Rect requested) {
        float factor = Math.min(1F, Math.min((float)(screenWidth - 2 * GAP) / Math.max(1, requested.width),
                (float)(screenHeight - 2 * GAP) / Math.max(1, requested.height)));
        int width = Math.max(1, (int)(requested.width * factor));
        int height = Math.max(1, (int)(requested.height * factor));
        return new Fit(new Rect(requested.x + (requested.width - width) / 2, requested.y, width, height), factor);
    }

    public static Fit fitLeft(int screenWidth, int screenHeight, int top, int width, int height,
                              float preferredScale) {
        return fitLeft(screenWidth, screenHeight, top, width, height, preferredScale, 52);
    }
    public static Fit fitLeft(int screenWidth, int screenHeight, int top, int width, int height,
                              float preferredScale, int bottomReserve) {
        int availableWidth = Math.max(1, screenWidth - rightColumn(screenWidth) - 3 * GAP);
        int availableHeight = Math.max(1, screenHeight - bottomReserve - top - GAP);
        float scale = Math.min(preferredScale,
                Math.min((float)availableWidth / Math.max(1, width), (float)availableHeight / Math.max(1, height)));
        return new Fit(new Rect(GAP, top, Math.max(1, (int)Math.ceil(width * scale)),
                Math.max(1, (int)Math.ceil(height * scale))), scale);
    }

    public static Rect tracker(int width, int height, Rect map, int lowerReserve) {
        int panelWidth = rightColumn(width);
        int top = Math.max(GAP, map.bottom() + GAP);
        int bottom = Math.max(GAP + 18, height - lowerReserve);
        // In a very short/congested viewport a compact heading still stays on screen.
        // The renderer explicitly shows the "open quests" fallback, not clipped tasks.
        if (bottom - top < 18) top = Math.max(GAP, Math.min(height - 18 - GAP, map.bottom() + GAP));
        return new Rect(width - panelWidth - GAP, top, panelWidth,
                Math.max(0, Math.min(height - GAP - top, bottom - top)));
    }

    public static Rect compactTracker(int width, int height, Rect requested, Rect map,
                                      int lowerReserve, List<Rect> panels) {
        int y = Math.max(2, Math.min(requested.y(), height - 12));
        // Leave the native 182-pixel hotbar and its offhand slot unobscured even
        // when a tiny GUI has room only for the compact quest-book indicator.
        int compactWidth = Math.max(30, Math.min(requested.width(), width / 2 - 100));
        var result = new Rect(width - compactWidth - GAP, y, compactWidth, 11);
        var occupied = new java.util.ArrayList<>(panels);
        occupied.add(map);
        occupied.add(new Rect(width - rightColumn(width) - GAP, height - lowerReserve,
                rightColumn(width) + GAP, lowerReserve));
        occupied.add(new Rect(width / 2 - 115, height - 24, 230, 24));
        // A compact header must obey the same bottom instruments as a full
        // tracker; shrinking it alone would put it over MekaHUD/EvilCraft.
        return placeTooltip(width, height, result, occupied);
    }

    public static Rect placeTooltip(int screenWidth, int screenHeight, Rect requested, List<Rect> obstacles) {
        int w = requested.width;
        int h = requested.height;
        int x = Math.max(GAP, Math.min(requested.x, screenWidth - GAP - w));
        int y = Math.max(GAP, Math.min(requested.y, screenHeight - GAP - h));
        // Prefer the player's anchor. Then try the space left of the minimap and
        // the bottom edges of actual visible panels; no pixel-by-pixel search.
        int[] xs = {x, Math.max(GAP, (screenWidth - rightColumn(screenWidth) - w) / 2), GAP};
        for (int candidateY : candidateRows(y, obstacles)) {
            for (int candidateX : xs) {
                Rect candidate = new Rect(candidateX, candidateY, w, h);
                if (candidate.inside(screenWidth, screenHeight - 48)
                        && obstacles.stream().noneMatch(candidate::intersects)) return candidate;
            }
        }
        return new Rect(x, y, w, h);
    }

    private static int[] candidateRows(int originalY, List<Rect> obstacles) {
        return java.util.stream.IntStream.concat(java.util.stream.IntStream.of(originalY, GAP),
                obstacles.stream().mapToInt(r -> r.bottom() + GAP)).distinct().toArray();
    }
}
