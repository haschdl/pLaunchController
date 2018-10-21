# Using events

Attaching a knob to a variable in the sketch is the easiest way to 
use the controller. If you you however would like to do more than
just setting the value of a variable, you can use events.

One example would be to capture the knob positions, print a message 
on the console, and adjust several parameters.

To use events, you must add one of the following methods in your sketch:
  
 * `void launchControllerKnobChanged(KNOBS knob)`
 
    Called when a knob was changed.
 * `void launchControllerPadChanged(PADS pad)`
 
    Called when you push a pad.
 * `void launchControllerControlChanged()`
 
    Called when either a pad or knob changes.
  


An example where I use `LaunchController.onKnobChanged(KNOBS knob)` to set a few variables:
```JAVA
void launchControllerKnobChanged(KNOBS knob) {
  println("Launch Control knob changed: " + knob.name());

  //Updates the values of h and base_w with the knob values
  //Note that MIDI notes are 0-127, but you can override that in setup() by
  //calling `range(float minValue,float maxValue)`
  //For example: controller.getKnob(KNOBS.KNOB_1_HIGH).range(10,200)
  h = controller.getKnob(KNOBS.KNOB_1_HIGH).value();
  base_w = controller.getKnob(KNOBS.KNOB_2_HIGH).value();
}   