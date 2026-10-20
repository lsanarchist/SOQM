package qm.hudprobe;

import java.util.List;
import qm.questui.client.HudGeometry;

public final class HudGeometryProbe {
    private static int assertions;
    private static void check(boolean value, String name) {
        assertions++; if (!value) throw new AssertionError(name);
    }
    public static void main(String[] args) {
        int[][] screens = {{320,180},{640,360},{960,540}};
        for (var screen : screens) {
            int width = screen[0], height = screen[1];
            var anchor = new HudGeometry.Rect(40,40,100,30);
            check(HudGeometry.placeTooltip(width,height,anchor,List.of()).equals(anchor), "custom Jade anchor changed");
            var wideJade = HudGeometry.fitTooltip(width,height,new HudGeometry.Rect(width/2-250,6,500,40));
            check(wideJade.rect().width()<=width-12, "actual wide Jade width not fitted");
            check(wideJade.rect().width()==(int)(500*wideJade.scale()), "Jade geometry differs from rendered scale");
            var hammer = new HudGeometry.Rect(width/2-90,30,180,22);
            int miningTop = HudGeometry.stackTop(List.of(hammer));
            var mineAether = HudGeometry.fitLeft(width,height,miningTop,284,67,0.85F);
            check(!mineAether.rect().intersects(hammer), "Aether/Ultimine conflict");
            var pinned = new HudGeometry.Rect(width-120,120,114,40);
            var rightJade = new HudGeometry.Rect(width-116,122,100,30);
            var avoiding = HudGeometry.placeTooltip(width,height,rightJade,List.of(pinned));
            check(!avoiding.intersects(pinned), "Jade covers pinned tracker");
            int side = HudGeometry.mapSide(width, height);
            for (int top : new int[]{6,32,58}) {
                var map = new HudGeometry.Rect(width-side-6,top,side,side+18);
                check(map.inside(width,height), "map clipped " + width + "/" + top);
                for (int textWidth : new int[]{120,280,800}) for (int rows : new int[]{3,7,12,25}) {
                    var mine = HudGeometry.fitLeft(width,height,54,textWidth,rows*9,0.85F);
                    check(mine.rect().inside(width,height-52), "Ultimine clipped");
                    check(!mine.rect().intersects(map), "map obscures Ultimine");
                    check(mine.scale()>0 && mine.scale()<=0.85F, "invalid text scale");
                }
                var tracker = HudGeometry.tracker(width,height,map,64);
                check(tracker.inside(width,height), "tracker clipped");
                if (tracker.height() >= 18) check(!tracker.intersects(map), "tracker under map");
                // An arbitrarily long upstream list cannot increase this viewport.
                int rawLines = 10_000, visible = Math.max(0, ((int)(tracker.height()/0.75)-27)/9);
                check(visible<rawLines, "unbounded list");
                if (tracker.height()/0.75 < 36) {
                    var upperRight = new HudGeometry.Rect(width-160,0,160,top);
                    var compact = HudGeometry.compactTracker(width,height,tracker,map,64,List.of(upperRight));
                    check(compact.inside(width,height), "compact tracker clipped");
                    check(!compact.intersects(map), "compact tracker under minimap");
                    check(!compact.intersects(new HudGeometry.Rect(width/2-91,height-24,182,24)), "compact tracker under hotbar");
                    check(!compact.intersects(upperRight), "compact tracker under status effects");
                    check(!compact.intersects(new HudGeometry.Rect(width-HudGeometry.rightColumn(width)-6,height-64,
                            HudGeometry.rightColumn(width)+6,64)), "compact tracker under lower right instrument");
                }
            }
            var map = new HudGeometry.Rect(width-side-6,6,side,side+18);
            var status = new HudGeometry.Rect(6,6,80,46);
            var mine = HudGeometry.fitLeft(width,height,58,200,63,0.75F).rect();
            var requested = new HudGeometry.Rect(width/2-70,6,140,36);
            var placed = HudGeometry.placeTooltip(width,height,requested,List.of(map,status,mine));
            check(placed.inside(width,height), "Jade clipped");
            if (width>=640) check(List.of(map,status,mine).stream().noneMatch(placed::intersects), "ordinary Jade collision");
            for (int textTop : new int[]{height-30,height-60}) {
                int reserve = height-Math.min(height-20,textTop)+70;
                var mineWithCompass = HudGeometry.fitLeft(width,height,30,280,81,0.85F,reserve);
                check(mineWithCompass.rect().bottom()<=height-reserve, "Mek compass obscures mining panel");
            }
        }
        System.out.println("HUD_GEOMETRY_OK assertions="+assertions+" viewports=320x180,640x360,960x540");
    }
}
