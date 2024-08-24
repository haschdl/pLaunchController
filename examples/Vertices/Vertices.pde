/**
 * Demonstrates pLaunchControl by using the pads to switch between three ways of drawing a cylinder-like geometry.
 * In this examples, the pads work as "radio-buttons", meaning only one can be activated. Note that this demo uses only pads 1 to 3.
 * 
 * Credits to original code: Processing example "Vertices", by Simon Greenwold.
 * 
 *  Draw a cylinder centered on the y-axis, going down 
 *  from y=0 to y=height. The radius at the top can be 
 *  different from the radius at the bottom, and the 
 *  number of sides drawn is variable.
 */
 
import pLaunchControl.*;

LaunchControl controller;

enum Options {
 CYLINDER,
 PYRAMID,
 MIX  
}

Options geometryOption = null;

//dimensions for the geometry. Both values are updates according to knob positions. See void launchControllerKnobChanged(KNOBS knob)
float h = 100, base_w = 150;

void setup() {
  size(640, 360, P3D);
  print("Running setup...");
  try {
    controller = new LaunchControl(this, true);
    controller.setPadMode(PADMODE.RADIO);
    
    //setting the initial value for the pad; calling setPad(PADS pad,boolean onOff) combine with PADMODE.RADIO will also "turn off" the other pads  
    //Calling setPad will also trigger launchControllerPadChanged(PADS pad) and 
    //automatically update "geometryOption". This is why is ok that geometryOption is not initialized.
    controller.setPad(PADS.PAD_1,true);  
    controller.getKnob(KNOBS.KNOB_01).range(10,200).defaultValue(h);
    controller.getKnob(KNOBS.KNOB_02).range(100,200).defaultValue(base_w);
  }
  catch(Exception e) {
    println("Unfortunately we could not detect that Launch Control is connected to this computer :(");
  }
}



void draw() {
  background(0);
  lights();
  translate(width / 2, height / 2);
  rotateY(map(mouseX, 0, width, 0, PI));
  rotateZ(map(mouseY, 0, height, 0, -PI));
  noStroke();
  fill(255, 255, 255);
  translate(0, -40, 0);
  
  switch(geometryOption) {
   case CYLINDER:
     drawCylinder(base_w, base_w, h, 64); // Draw a cylinder
     break;
   case PYRAMID:
     drawCylinder(0, base_w, h, 4); // Draw a pyramid; top radius is 0
     break;
   case MIX:
     drawCylinder(10, base_w, h, 16); // Draw a mix between a cylinder and a cone
  }
}


void drawCylinder(float topRadius, float bottomRadius, float tall, int sides) {
  float angle = 0;
  float angleIncrement = TWO_PI / sides;
  beginShape(QUAD_STRIP);
  for (int i = 0; i < sides + 1; ++i) {
    vertex(topRadius*cos(angle), 0, topRadius*sin(angle));
    vertex(bottomRadius*cos(angle), tall, bottomRadius*sin(angle));
    angle += angleIncrement;
  }
  endShape();
  
  // If it is not a cone, draw the circular top cap
  if (topRadius != 0) {
    angle = 0;
    beginShape(TRIANGLE_FAN);
    
    // Center point
    vertex(0, 0, 0);
    for (int i = 0; i < sides + 1; i++) {
      vertex(topRadius * cos(angle), 0, topRadius * sin(angle));
      angle += angleIncrement;
    }
    endShape();
  }

  // If it is not a cone, draw the circular bottom cap
  if (bottomRadius != 0) {
    angle = 0;
    beginShape(TRIANGLE_FAN);

    // Center point
    vertex(0, tall, 0);
    for (int i = 0; i < sides + 1; i++) {
      vertex(bottomRadius * cos(angle), tall, bottomRadius * sin(angle));
      angle += angleIncrement;
    }
    endShape();
  }
}


void LaunchControlPadChanged(PADS pad) {
  println("Launch Control: the pad has changed value: " + pad.name());
  
  switch(pad) {
    case PAD_1:
      geometryOption = Options.CYLINDER;
      break;
    case PAD_2:
      geometryOption = Options.PYRAMID;
      break;
    case PAD_3:
      geometryOption = Options.MIX;
      break;
    default:
      break;
  }
}


void LaunchControlKnobChanged(KNOBS knob) {
  println("Launch Control knob changed: " + knob.name());
  
  //Updates the values of h and base_w with the knob values
  //Note that MIDI notes are 0-127, but in setup() we replace that with range(minValue,maxValue): controller.getKnob(KNOBS.KNOB_1_HIGH).range(10,200)
  h = controller.getKnob(KNOBS.KNOB_01).value();
  base_w = controller.getKnob(KNOBS.KNOB_02).value();
  
}
