# pLaunchController
A JAVA wrapper for the Novation Launch Controller aimed at using the MIDI pads and knobs as input for Processing sketches.
![](novation.png)

# Installation
## From Processing editor
In Processing, go to `Sketch`, `Import library...`, `Add library`. Search for "Launch Controller" and once found, click `Install`.
## Manual installation
Copy the file pLaunchController.jar to a folder `code` inside your sketch. This method makes the library available to an individual sketch.
If you intend to make the library available to all sketches, unzip the pLaunchController.zip file to the libraries of your Processing installation (you can see the library in Preferences page).

# Usage
To start using the library, make sure Novation Launch Controller is connected to your computer (at least one led is lit).
## Add reference to library
  1. At the top of your sketch, import the namespace `pLaunchController`:
      ```JAVA
      import pLaunchController.*;
      ```
   
   2. Declare a global variable of type `LaunchController`
        ```JAVA
          LaunchController midiController;
        ```
   3. Instantiate the controller during `setup()`:
   
  ```JAVA
      try {
          midiController = new LaunchController(this);
      }
       catch(Exception e) {
          println("Error connecting to MIDI device! Sketch will run with UI controllers. values.");
          midiController = null;
      }
  ```
