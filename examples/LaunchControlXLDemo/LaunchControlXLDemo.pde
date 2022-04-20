import pLaunchControl.*;

LaunchControlXL controller;

Knob knob01, knob02, knob03, knob04, knob05, knob06, knob07, knob08;
Knob knob09, knob10, knob11, knob12, knob13, knob14, knob15, knob16;
Knob knob17, knob18, knob19, knob20, knob21, knob22, knob23, knob24;

float s = 1000/16; // common length for shape sides/radius/spacing

void setup() {
  size(980, 1000);
  try {
    controller = new LaunchControlXL(this);
  }
  catch(Exception e) {
    println("Unfortunately we could not detect that Launch Control is connected to this computer :(");
  }
  setupKnobs();
}

void draw() {

  background(0);
  translate(20, -5);
  stroke(255);
  strokeWeight(3);
  translate(s/2, s);

  drawKnobs();
  drawSliders();
  drawPads();
}

void setupKnobs() {

  // ----- row 1 ----- //
  knob01 = controller.getKnob(KNOBS.values()[0]);
  knob01.defaultValue(64);

  knob02 = controller.getKnob(KNOBS.values()[1]);
  knob02.defaultValue(64);

  knob03 = controller.getKnob(KNOBS.values()[2]);
  knob03.defaultValue(64);

  knob04 = controller.getKnob(KNOBS.values()[3]);
  knob04.defaultValue(64);

  knob05 = controller.getKnob(KNOBS.values()[4]);
  knob05.defaultValue(64);

  knob06 = controller.getKnob(KNOBS.values()[5]);
  knob06.defaultValue(64);

  knob07 = controller.getKnob(KNOBS.values()[6]);
  knob07.defaultValue(64);

  knob08 = controller.getKnob(KNOBS.values()[7]);
  knob08.defaultValue(64);

  // ----- row 2 ----- //
  knob09 = controller.getKnob(KNOBS.values()[8]);
  knob09.defaultValue(64);

  knob10 = controller.getKnob(KNOBS.values()[9]);
  knob10.defaultValue(64);

  knob11 = controller.getKnob(KNOBS.values()[10]);
  knob11.defaultValue(64);

  knob12 = controller.getKnob(KNOBS.values()[11]);
  knob12.defaultValue(64);

  knob13 = controller.getKnob(KNOBS.values()[12]);
  knob13.defaultValue(64);

  knob14 = controller.getKnob(KNOBS.values()[13]);
  knob14.defaultValue(64);

  knob15 = controller.getKnob(KNOBS.values()[14]);
  knob15.defaultValue(64);

  knob16 = controller.getKnob(KNOBS.values()[15]);
  knob16.defaultValue(64);

  // ----- row 3 ----- //
  knob17 = controller.getKnob(KNOBS.values()[16]);
  knob17.defaultValue(64);

  knob18 = controller.getKnob(KNOBS.values()[17]);
  knob18.defaultValue(64);

  knob19 = controller.getKnob(KNOBS.values()[18]);
  knob19.defaultValue(64);

  knob20 = controller.getKnob(KNOBS.values()[19]);
  knob20.defaultValue(64);

  knob21 = controller.getKnob(KNOBS.values()[20]);
  knob21.defaultValue(64);

  knob22 = controller.getKnob(KNOBS.values()[21]);
  knob22.defaultValue(64);

  knob23 = controller.getKnob(KNOBS.values()[22]);
  knob23.defaultValue(64);

  knob24 = controller.getKnob(KNOBS.values()[23]);
  knob24.defaultValue(64);
}

/**
 * Draws the knobs of the controller as simple arcs.
 */
void drawKnobs() {

  float start = HALF_PI * 1.3;
  float end = TWO_PI + HALF_PI * .7;

  pushMatrix();
  for (int i = 0; i < 24; i++) {
    //draw knob background, simple arc
    fill(255);
    arc(2*s*(i%8), 0, s, s, start, end);

    // draw knobs
    fill(80);
    Knob knob = controller.getKnob(KNOBS.values()[i]);

    float normal = knob.valueNormal();
    float realValue = knob.value();
    if (frameCount == 1) println("knob: " + i + " value: " + realValue);
    // if (normal > 0) println("knob" + i + ": normal: " + normal + " realValue: " + realValue);

    arc(2*s*(i%8), 0, s, s, start, start + normal*(end-start));

    fill(255);
    translate(-20, 0);
    text("id: " + round(i), 2*s*(i%8), s );
    text("normal: " + round(normal), 2*s*(i%8), s + 20);
    text("value: " + round(realValue), 2*s*(i%8), s + 40);
    translate(20, 0);

    if (i == 7) translate(0, height * .16);
    if (i == 15) translate(0, height * .16);
  }
}

/**
 * Draws the sliders of the controller lines with cross bars.
 */
void drawSliders() {

  translate(0, height * .16);
  for (int i = 0; i < 8; i++) {

    strokeWeight(1);
    line(2*s*(i%8), -20, 2*s*(i%8), 30);

    Slider slider = controller.getSlider(SLIDERS.values()[i]);

    float normal = slider.valueNormal();
    float realValue = slider.value();
    float y = map(realValue, 128, 0, -20, 30);

    strokeWeight(3);
    line(2*s*(i%8)-10, y, 2*s*(i%8)+10, y);

    translate(-20, 0);
    text("id: " + round(i), 2*s*(i%8), s );
    text("normal: " + round(normal), 2*s*(i%8), s + 20);
    text("value: " + round(realValue), 2*s*(i%8), s + 40);
    translate(20, 0);
  }
  popMatrix();
}

/**
 * Draws the pads of the controller as simple squares.
 */
void drawPads() {

  translate(0, height * .67);
  rectMode(CENTER);

  for (int i = 0; i < 16; i++) {
    //draw pad background, simple arc
    fill(255);
    rect(2*s*(i%8), 0, s, s);

    //draw pad state position
    fill(80);
    Pad pad = controller.getPad(PADS.values()[i]);
    if (pad.value()) {
      rect(2*s*(i%8), 0, s, s);
    }

    fill(255);
    text(round(i), 2*s*(i%8), s );

    if (i == 7) translate(0, height * .15);
  }
}
