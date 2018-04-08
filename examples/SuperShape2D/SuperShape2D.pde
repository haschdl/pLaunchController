// Example of the library pLaunchController. In this sketch, the knobs and pads of the Novation Launch Control
// are used to control the variables of the image being generated.
// This sketch is almost entirely the SuperShape3D implementation from Daniel Shiffman,
// with the additions for the MIDI controller.
// 
// Credits for supershape3D generation:
//   Daniel Shiffman
//   http://codingtra.in
//   http://patreon.com/codingtrain
//   Code for: https://youtu.be/akM4wMZIBWg

import peasy.*;
import pLaunchController.*;

PeasyCam cam;

PVector[][] globe;
int total = 75;

float offset = 0;

float m = 0;
float mchange = 0;
float r = 200;
float lat_start = -HALF_PI; float lat_end = HALF_PI;
float lon_start = -PI; float lon_end = PI;
LaunchController controller;

void setup() {
  size(800, 800, P3D);
  cam = new PeasyCam(this, 500);
  colorMode(HSB);
  globe = new PVector[total+1][total+1];
  
  try {
    controller = new LaunchController(this);
  }
  catch(Exception e) {
    println("Unfortunately we could not detect that Launch Control is connected to this computer :(");
  }
}

float a = 1;
float b = 1;

float supershape(float theta, float m, float n1, float n2, float n3) {
  float t1 = abs((1/a)*cos(m * theta / 4));
  t1 = pow(t1, n2);
  float t2 = abs((1/b)*sin(m * theta/4));
  t2 = pow(t2, n3);
  float t3 = t1 + t2;
  float r = pow(t3, - 1 / n1);
  return r;
}

void draw() {
  
  //m = map(sin(mchange), -1, 1, 0, 7);
  //mchange += 0.02;
  
  background(0);
  noStroke();
  lights();
 
  for (int i = 0; i < total+1; i++) {
    float lat = map(i, 0, total, lat_start, lat_end);
    float r2 = supershape(lat, m, 0.2, 1.7, 1.7);
    //float r2 = supershape(lat, 2, 10, 10, 10);
    for (int j = 0; j < total+1; j++) {
      float lon = map(j, 0, total, lon_start, lon_end);
      float r1 = supershape(lon, m, 0.2, 1.7, 1.7);
      //float r1 = supershape(lon, 8, 60, 100, 30);
      float x = r * r1 * cos(lon) * r2 * cos(lat);
      float y = r * r1 * sin(lon) * r2 * cos(lat);
      float z = r * r2 * sin(lat);
      globe[i][j] = new PVector(x, y, z);
    }
  }

  //stroke(255);
  //fill(255);
  //noFill();
  offset += 5;
  for (int i = 0; i < total; i++) {
    float hu = map(i, 0, total, 0, 255*6);
    fill((hu + offset) % 255 , 255, 255);
    beginShape(TRIANGLE_STRIP);
    for (int j = 0; j < total+1; j++) {
      PVector v1 = globe[i][j];
      vertex(v1.x, v1.y, v1.z);
      PVector v2 = globe[i+1][j];
      vertex(v2.x, v2.y, v2.z);
    }
    endShape();
  }
}
void launchControllerChanged() {
  //This method is called by any changes in pads or knobs.
  println("Launch Control: either a knob of pad has changed value");   
}

void launchControllerPadChanged(PADS pad) {
  println("Launch Control: the pad has changed value: " + pad.name());
  
  //you can get the knob status as boolean or int (0 or 1)
  //Using int might be useful in calculations, for example,
  //you can "turn off" noise by multiplying by 0 or 1.
  boolean myBooleanVariable = controller.getPad(pad);
  int myIntVariable = controller.getPadInt(pad);
}
void launchControllerKnobChanged(KNOBS knob) {
  println("Launch Control knob changed: " + knob.name());
  
  //maps r to the value of the first knob
  //getKnobMap() is the same as:
  //r = map(controller.getKnob(KNOBS.KNOB_1_HIGH), 0, 127, 100,500);
  r = controller.getKnob(KNOBS.KNOB_1_HIGH).range(100,500).value(); 
  m = controller.getKnob(KNOBS.KNOB_2_HIGH).range(0,100).value();
  lat_start = controller.getKnob(KNOBS.KNOB_3_HIGH).range(-HALF_PI,0).value();
  lat_end = controller.getKnob(KNOBS.KNOB_4_HIGH).range(0,HALF_PI).value();
  lon_start = controller.getKnob(KNOBS.KNOB_5_HIGH).range(-PI,0).value();
  lon_end = controller.getKnob(KNOBS.KNOB_6_HIGH).range(0,PI).value();
  
}
