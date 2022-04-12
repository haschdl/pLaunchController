package pLaunchControl;

import javax.naming.OperationNotSupportedException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;


/**
 * An implementation of @see javax.sound.midi.Receiver
 */
class LaunchControlDeviceReceiver implements Receiver {

    private MidiDevice parent;
    private javax.sound.midi.MidiDevice device;

    LaunchControlDeviceReceiver(MidiDevice parent, javax.sound.midi.MidiDevice device) {
        this.parent = parent;
        this.device = device;
    }

    byte[] lastMessage;


    /**
     * Handles new messages coming from the Midi controller.
     *
     * @param message
     * @param timeStamp
     */
    @Override
    public void send(MidiMessage message, long timeStamp) {

        lastMessage = message.getMessage();

        if (parent.debug() && (message instanceof SysexMessage)) {
            // Print contents of sysex message
            System.out.println();
            System.out.print("Sysex MIDI message <<");
            for (int i = 0; i < lastMessage.length; i++) {
                System.out.print(" " + lastMessage[i]);
            }
            System.out.println(">>");
            return;
        }

        if (parent.debug())
            System.out.println("lastMessage [0]:" + lastMessage[0] + " [1]:" + lastMessage[1] + " [2]:" + lastMessage[2]);

        if (parent.isKnobMessage(message)) {
            parent.setKnobPosition(message);
        }
        else if (parent.isPad(message)) {
            PADS padToChange = parent.getPadFromMessage(message);
            //TODO padToChange is null when "note off" (button released) message
            //is received ( byts[2] == 0)
            if (padToChange != null) {
                if (parent.debug())
                    System.out.println(String.format("Received message for pad %s.", padToChange.toString()));
                parent.invertPad(padToChange);
                switch (parent.getPadMode()) {
                    case RADIO:
                        //switch off all other pads, without triggering events
                        for (Pad pad : parent.getPads())
                            if (!pad.equals(padToChange))
                                parent.setPad(pad.controllerPad, false, true);
                        break;
                    case TOGGLE:
                        sendLedOnOff(parent.getPad(padToChange).value(), padToChange);
                        break;
                }


            }
        }
    }


    protected void sendLedOnOff(boolean onOff, PADS pad) {
        try {
            //Hex version F0h 00h 20h 29h 02h 0Ah 78h [Template] [LED] Value F7h
            //Where Template is 00h-07h (0-7) for the 8 user templates, and 08h-0Fh (8-15) for the 8 factory
            //templates; LED is the index of the pad/button (00h-07h (0-7) for pads, 08h-0Bh (8-11) for buttons);
            //and Value is the velocity byte that defines the brightness values of both the red and green LEDs.
            byte template = 0x08;
            byte color = onOff ? LedConstants.RED_FULL : LedConstants.OFF;
            byte[] ledOn = new byte[]{(byte) 0xF0, 0x00, 0x20, 0x29, 0x02, 0x0A, 0x78, template, pad.code(), color, (byte) 0xF7};
            SysexMessage ledOnMsg = new SysexMessage(ledOn, ledOn.length);

            device.getReceiver().send(ledOnMsg, -1);
        } catch (Exception e) {
            System.out.println("Error sending Midi message: " + e);
        }

    }

    public void send(SysexMessage message, long timeStamp) {
        lastMessage = message.getMessage();
        if (parent.debug())
            System.out.println("SysexMessage lastMessage [0]:" + lastMessage[0] + " [1]:" + lastMessage[1] + " [2]:" + lastMessage[2]);
    }

    @Override
    public void close() {

    }
}

