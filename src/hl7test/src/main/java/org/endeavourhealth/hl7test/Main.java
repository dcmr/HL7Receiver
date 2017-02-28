package org.endeavourhealth.hl7test;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        if (args[0].equals("transform")){
            try {
                BulkTransformTester.processStoredMessages();
            }
            catch (Exception e) {
                System.out.println("Error : " + e.getMessage() );
            }
        } else {
            JFrame frame = new JFrame("HL7v2 -> FHIR");
            MainDialog dialog = new MainDialog(frame);
            frame.getContentPane().add(dialog.getPanelMain());
            frame.setSize(new Dimension(1000, 1000));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        }
    }
}
