# pLaunchController
A JAVA wrapper for the Novation Launch Controller aimed at using the MIDI pads and knobs as input for Processing sketches.

# Installation
## From Processing editor
** Pending approval**
In Processing, go to `Sketch`, `Import library...`, `Add library`. Search for "Launch Controller" and once found, click `Install`.
## Manual installation
Copy the file pLaunchController.jar to a folder `code` inside your sketch.

# Usage
To start using the library, make sure Novation Launch Controller is connected to your computer (at least one led is lit).
## Add reference to library
  1. At the top of your sketch, import the namespace `pLaunchController`:

        ``import pLaunchController.*;``
   
   2. Declare a global variable of type `LaunchController`
   
          LaunchController midiController;
   
   3. Instantiate the controller during `setup()`:
   

        try {
            midiController = new LaunchController(this);
        }
         catch(Exception e) {
            println("Error connecting to MIDI device! Sketch will run with UI controllers. values.");
            midiController = null;
        }
