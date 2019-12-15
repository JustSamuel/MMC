import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * GUI for the Modulo Multiplication Circle
 */
public class GUI extends PApplet {

    AnimationScheduler scheduler;

    public static void main(String[] args) {
        PApplet.main("GUI");
    }

    public void settings() {
        size(displayWidth, displayHeight);
    }

    public void setup() {
        colorMode(HSB, 360, 100, 100);
        scheduler = AnimationScheduler.getInstance();
        scheduler.addAnimation(new DotAnimation());
        scheduler.addAnimation(new DotAnimation());
    }

    public void draw() {
        background(0);
        for (AbstractAnimation animation: scheduler.animations
             ) {
            animation.draw();
        }
    }

    /**
     * Singelton helper class to create linearly dependant animations in p3.
     */
    public static class AnimationScheduler {
        private static AnimationScheduler ourInstance = new AnimationScheduler();
        AnimationList<GUI.AbstractAnimation> animations;

        public static AnimationScheduler getInstance() {
            return ourInstance;
        }

        private AnimationScheduler() {
            animations = new AnimationList<GUI.AbstractAnimation>();
        }

        public void addAnimation(GUI.AbstractAnimation animation) {
            animations.addAnimation(animation);
        }

        /**
         * Custom iterator for the animations list
         */
        public class AnimationList<AbstractAnimation> implements Iterable<GUI.AbstractAnimation> {

            private ArrayList<GUI.AbstractAnimation> arrayList;
            private int currentSize;

            public AnimationList() {
                this.arrayList = new ArrayList<GUI.AbstractAnimation>();
                this.currentSize = arrayList.size();
            }

            public void addAnimation(GUI.AbstractAnimation animation) {
                this.arrayList.add(animation);
                this.currentSize = arrayList.size();
            }

            @Override
            public Iterator<GUI.AbstractAnimation> iterator() {
                Iterator<GUI.AbstractAnimation> it = new Iterator<>() {

                    private int currentIndex = 0;

                    @Override
                    public boolean hasNext() {
                        boolean temp = true;
                        if (currentIndex > 0) {
                            temp = arrayList.get(currentIndex - 1).isFinished();
                        }
                        return currentIndex  < currentSize && arrayList.get(currentIndex) != null && temp;
                    }

                    @Override
                    public GUI.AbstractAnimation next() {
                        return arrayList.get(currentIndex++);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
                return it;
            }
        }
    }

    public abstract class AbstractAnimation {

        // Every animation begins unfinished
        public boolean isFinished = false;

        public void draw() {
            if (!isFinished) {
                step();
            }
            animation();
        }

        /**
         * Main draw function of the animation.
         */
        public void animation() {
        }

        /**
         * A call to this should advance the animation one step
         */
        public void step() {
        }

        public boolean isFinished() {
            return this.isFinished;
        }

        public void finished() {
            this.isFinished = true;
        }

    }


    public class DotAnimation extends AbstractAnimation {

        private int radius = 0;
        private int maxRadius = 200;
        private int dotCount = 20;

        @Override
        public void animation() {
            super.animation();
            int SIZE = 10;
            for (int i = 0; i < dotCount; i++) {
                float angle = (360 / dotCount) * i;
                float x = (float) (displayWidth / 2.0) + cos(radians(angle)) * radius;
                float y = (float) (displayHeight / 2.0) + sin(radians(angle)) * radius;
                circle(x, y, 10);
            }
        }

        @Override
        public void step() {
            super.step();
            if (radius < maxRadius) {
                radius++;
            } else {
                this.finished();
            }
        }

    }
}
