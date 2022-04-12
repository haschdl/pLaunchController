// Example of the library pLaunchController. This sketch is a good way to test
// the library and see the dynamics of using the controller and its effect on the screen.
// It illustrates that when the sketch starts, it is not possible to read the actual position of
// knob - values are updated as soon as you move the knob.



import pLaunchControl.*;

LaunchController controller;

        void setup() {
        size(1000, 500);

        try {
        controller = new LaunchController(this, true);
        }
        catch(Exception e) {
        println("Unfortunately we could not detect that Launch Control is connected to this computer :(");
        }
        textAlign(CENTER);
        }


        void draw() {
        background(0);
        translate(20, 20);
        stroke(255);
        strokeWeight(3);
        float s = width/16;
        translate(s/2, s);

        drawKnobs(s);
        drawPads(s);

        }

/**
 * Draws the knobs of the controller as simple arcs.
 */
        void drawKnobs(float s) {

        //drawing positions
        float start = HALF_PI * 1.3;
        float end = TWO_PI + HALF_PI * .7;

        pushMatrix();
        for (int i = 0; i < 16; i++) {
        //draw knob background, simple arc
        fill(255);
        arc(2*s*(i%8), 0, s, s, start, end);

        //draw knob position
        fill(80);
        Knob knob = controller.getKnob(i);
        float normal = knob.valueNormal();
        float realValue = knob.value();

        arc(2*s*(i%8), 0, s, s, start, start + normal*(end-start));
        fill(255);
        text(round(realValue), 2*s*(i%8), s );

        if (i == 7)
        translate(0, height * .3);
        }
        popMatrix();
        }

/**
 * Draws the pads of the controller as simple arcs.
 */
        void drawPads(float s) {
        translate(0,height * .6);
        rectMode(CENTER);

        for (int i = 0; i < 8; i++) {
        //draw pad background, simple arc
        fill(255);
        rect(2*s*i,0,s,s);

        //draw pad state position
        fill(80);
        boolean padState = controller.getPad(i).value();
        if(padState)
        rect(2*s*i,0,s,s);

        fill(255);
        text(round(i), 2*s*(i%8), s );

        }
        }
