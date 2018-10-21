// Example of the library pLaunchController. This sketch is a good way to test
// the library and see the dynamics of using the controller and its effect on the screen.
// It illustrates that when the sketch starts, it is not possible to read the actual position of
// knob - values are updated as soon as you move the knob.



import pLaunchController.*;

LaunchController controller;

void setup() {
  size(1000, 500, P3D);
  colorMode(HSB);


  try {
    controller = new LaunchController(this);
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

  //drawing positions
  float start = HALF_PI * 1.3;
  float end = TWO_PI + HALF_PI * .7;
  for (int i = 0; i < 16; i++) {
    //draw knob background, simple arc
    fill(255);
    arc(2*s*(i%8), 0, s, s, start, end);

    //draw knob position
    fill(80);
    Knob knob = controller.getKnob(KNOBS.values()[i]);
    float normal = knob.valueNormal();
    float realValue = knob.value();

    arc(2*s*(i%8), 0, s, s, start, start + normal*(end-start));
    fill(255);
    text(round(realValue), 2*s*(i%8), s );

    if (i == 7)
      translate(0, height/2);
  }
}
void launchControllerChanged() {
  //This method is called by any changes in pads or knobs.
  //It might be useful in debugging scenarios. You do not
  //need to include `void launchControllerChanged()` in your
  //skecth.

  //For demonstration purposes:
  //println("Launch Control: either a knob of pad has changed value");
}

void launchControllerPadChanged(PADS pad) {
  println("Launch Control: the pad has changed value: " + pad.name());

  //you can get the knob status as boolean or int (0 or 1)
  //Using int might be useful in calculations, for example,
  //you can "turn off" noise by multiplying by 0 or 1.
  boolean myBooleanVariable = controller.getPad(pad);
  int myIntVariable = controller.getPadInt(pad);
}
