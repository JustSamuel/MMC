import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * MMC for the Modulo Multiplication Circle
 */
public class MMC extends PApplet {

    AnimationScheduler scheduler;
    DotAnimation dotAnimation;
    ModuloAnimation moduloAnimation;
    PFont font;

    public static void main(String[] args) {
        PApplet.main("MMC");
    }

    public void settings() {
        size(displayWidth, displayHeight);
    }

    public void setup() {
        colorMode(HSB, 360, 100, 100);
        scheduler = AnimationScheduler.getInstance();
        dotAnimation = new DotAnimation();
        moduloAnimation = new ModuloAnimation();
        scheduler.addAnimation(dotAnimation);
        scheduler.addAnimation(moduloAnimation);
        font = createFont("FiraSans-Regular.otf",24);
        textFont(font);
    }

    public void draw() {
        background(0);
        drawGUI();
        for (AbstractAnimation animation: scheduler.animations
             ) {
            animation.draw();
        }
    }

    public void keyPressed() {
        switch (key) {
            case 'r':
                dotAnimation.restart();
                moduloAnimation.restart();
                break;
        }
    }

    /**
     * Singelton helper class to create linearly dependant animations in p3.
     */
    public static class AnimationScheduler {
        private static AnimationScheduler ourInstance = new AnimationScheduler();
        AnimationList<MMC.AbstractAnimation> animations;

        public static AnimationScheduler getInstance() {
            return ourInstance;
        }

        private AnimationScheduler() {
            animations = new AnimationList<MMC.AbstractAnimation>();
        }

        public void addAnimation(MMC.AbstractAnimation animation) {
            animations.addAnimation(animation);
        }

        /**
         * Custom iterator for the animations list
         */
        public class AnimationList<AbstractAnimation> implements Iterable<MMC.AbstractAnimation> {

            private ArrayList<MMC.AbstractAnimation> arrayList;
            private int currentSize;

            public AnimationList() {
                this.arrayList = new ArrayList<MMC.AbstractAnimation>();
                this.currentSize = arrayList.size();
            }

            public void addAnimation(MMC.AbstractAnimation animation) {
                this.arrayList.add(animation);
                this.currentSize = arrayList.size();
            }

            @Override
            public Iterator<MMC.AbstractAnimation> iterator() {
                Iterator<MMC.AbstractAnimation> it = new Iterator<>() {

                    private int currentIndex = 0;

                    @Override
                    public boolean hasNext() {
                        boolean temp = true;
                        if (currentIndex > 0) {
                            temp = arrayList.get(currentIndex - 1).allowNext;
                        }
                        return currentIndex  < currentSize && arrayList.get(currentIndex) != null && temp;
                    }

                    @Override
                    public MMC.AbstractAnimation next() {
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

        public boolean allowNext = false;

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

    public void drawGUI() {
        fill(0,0,100);
        noStroke();
        text("Multiplication : " + moduloAnimation.modulo,50,50);
    }

    public class ModuloAnimation extends AbstractAnimation {
        public float modulo = 0;

        @Override
        public void animation() {
            super.animation();
            for (int i = 0; i <= dotCount; i++) {
                float target = (i * modulo) % dotCount;
                float[] startC = getCircle(i, radius);
                float[] targetC = getCircle(target, radius);
                stroke((360 / dotCount) * i, 100, 100,90);
                line(startC[0], startC[1], targetC[0], targetC[1]);
            }
        }

        @Override
        public void step() {
            super.step();
            modulo += 0.01;
        }

        public void restart() {
            modulo = 0;
        }
    }


    public float[] getCircle(float i, float radius) {
        float angle = (360 / dotCount) * i + angleOffset;
        float x = (float) (displayWidth / 2.0) + cos(radians(angle)) * radius;
        float y = (float) (displayHeight / 2.0) + sin(radians(angle)) * radius;
        float[] result = {x, y};
        return result;
    }



    static float MAX_RADIUS = 400;
    float radius;
    float angleOffset = 0;
    float dotCount = 100;

    public class DotAnimation extends AbstractAnimation {

        private float maxRadius = MAX_RADIUS;
        private float stepSize = 1;

        @Override
        public void animation() {
            super.animation();
            this.allowNext = true;
            int SIZE = 10;
            for (int i = 0; i <= dotCount; i++) {
                float[] startC = getCircle(i, radius);
                noStroke();
                fill(0,0,100);
                circle(startC[0], startC[1], 10);
            }
        }

        @Override
        public void step() {
            super.step();
            angleOffset += 0.1;
            if (radius < maxRadius) {
                radius+= stepSize;
                stepSize+= 0.1;
            } else {
                radius = maxRadius;
                this.allowNext = true;
            }
        }

        public void restart() {
            this.isFinished = false;
            radius = 0;
            this.stepSize = 1;
        }

    }
}
