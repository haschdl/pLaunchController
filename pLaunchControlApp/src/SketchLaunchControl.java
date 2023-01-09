/**
 * `SketchLaunchControl` is included in the project in order to enable testing and debugging of the library.
 *  It is not meant for distribution as a library example. Check /examples folder for PDE files
 *  that can be opened with the Processing editor.
 */

import pLaunchControl.Knob;
import pLaunchControl.LaunchControl;
import processing.core.PApplet;

public class SketchLaunchControl extends PApplet {

    private LaunchControl controller;

    public static void main(String[] args) {
        PApplet.main(new String[]{"SketchLaunchControl",});
    }

    public void settings() {
        size(1000, 500);
    }
    public void setup() {

        try {
            controller = new LaunchControl(this, true);
            // If you have renamed your Launch Control in the system
            // preferences, use this version of the constructor:
            // controller = new LaunchControl(this, true, "THE NAME YOU GAVE TO THE CONTROLLER");
        } catch (Exception e) {
            println("Unfortunately we could not detect that Launch Control is connected to this computer :(");
            println(e);
            noLoop();
        }
        textAlign(CENTER);
    }


    public void draw() {
        background(0);
        translate(20, 20);
        stroke(255);
        strokeWeight(3);
        float s = width / 16.f;
        translate(s / 2, s);
        if (controller != null) {
            drawKnobs(s);
            drawPads(s);
        }
    }

    /**
     * Draws the knobs of the controller as simple arcs.
     */
    void drawKnobs(float s) {

        //drawing positions
        float start = HALF_PI * 1.3f;
        float end = TWO_PI + HALF_PI * .7f;

        pushMatrix();
        for (int i = 0; i < 16; i++) {
            //draw knob background, simple arc
            fill(255);
            arc(2 * s * (i % 8), 0, s, s, start, end);

            //draw knob position
            fill(80);
            Knob knob = controller.getKnob(i);
            float normal = knob.valueNormal();
            float realValue = knob.value();

            arc(2 * s * (i % 8), 0, s, s, start, start + normal * (end - start));
            fill(255);
            text(round(realValue), 2 * s * (i % 8), s);

            if (i == 7)
                translate(0, height * .3f);
        }
        popMatrix();
    }

    /**
     * Draws the pads of the controller as simple arcs.
     */
    void drawPads(float s) {
        translate(0, height * .6f);
        rectMode(CENTER);

        for (int i = 0; i < 8; i++) {
            //draw pad background, simple arc
            fill(255);
            rect(2 * s * i, 0, s, s);

            //draw pad state position
            fill(80);
            boolean padState = controller.getPad(i).value();
            if (padState)
                rect(2 * s * i, 0, s, s);

            fill(255);
            text(round(i), 2 * s * (i % 8), s);
        }
    }
}
