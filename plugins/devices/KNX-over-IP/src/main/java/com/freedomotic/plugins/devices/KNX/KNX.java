/**
 *
 * 
 * 
 * 
 */

package com.freedomotic.plugins.devices.KNX;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.reactions.Command;
import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLink;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class KNX
        extends Protocol {

    private static final Logger LOG = Logger.getLogger(KNX.class.getName());
    private String KNX_IP = "127.0.0.1";
    //private int KNX_PORT = 3671;  porta standard
    private String KNX_group = "1/1/1";
    private boolean KNX_value = false;
    private KNXNetworkLink knxLink = null;
    private ProcessCommunicator pc = null;
    private KNXGui settingsMenu = null;
    private boolean swState = false;

    @Inject

    public KNX() {
        super("KNX-over-IP", "/KNX-over-IP/KNX-over-IP-manifest.xml");
    }

    @Override
    protected void onShowGui() {
        bindGuiToPlugin(settingsMenu);
    }

    @Override
    protected void onHideGui() {
        if(settingsMenu.getParameter(0) != null){
            KNX_IP = settingsMenu.getParameter(0);
            KNX_group = settingsMenu.getParameter(1);
        }
               
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {
        
        try {
            if (pc != null) {
                KNX_value = pc.readBool(new GroupAddress(KNX_group));
                LOG.info("value read from datapoint " + KNX_group + ": " + KNX_value);
            }
        } catch (final KNXException e) {
            LOG.info("Error reading KNX datapoint: " + e.getMessage());
        } catch (final InterruptedException e) {
            LOG.info("Interrupted: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        LOG.info("KNX plugin is started");
        
        settingsMenu = new KNXGui();
        
        // inizializzazione GUI
        /*
        JFrame.setDefaultLookAndFeelDecorated(true);
        settingsMenu = new JFrame();
        settingsMenu.setTitle("KNX-over-IP settings");
        settingsMenu.setSize(500,300);
        settingsMenu.setResizable(false);
        settingsMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(3, 1));
        
        final JTextField ipField = new JTextField();
        ipField.setToolTipText("Inserire IP del bridge KNX/IP");
        ipField.setColumns(15);
        JLabel ipLab = new JLabel("KNX IP", JLabel.RIGHT);
        ipLab.setLabelFor(ipField);
        labelPanel.add(ipLab);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(ipField);
        fieldPanel.add(p);
        
        final JTextField groupField = new JTextField();
        groupField.setToolTipText("Inserire KNX group");
        groupField.setColumns(5);
        JLabel groupLab = new JLabel("KNX group", JLabel.RIGHT);
        groupLab.setLabelFor(groupField);
        labelPanel.add(groupLab);
        p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(groupField);
        fieldPanel.add(p);

        
        final JButton Switch = new JButton("Switch");
        Switch.setBackground(Color.RED);
        Switch.setOpaque(true);

        Switch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                try {
                    if (swState) {
                        if (pc != null) {
                            pc.write(new GroupAddress(KNX_group), swState);
                        }
                        Switch.setBackground(Color.RED);
                        swState = false;
                    } else {
                        if (pc != null) {
                            pc.write(new GroupAddress(KNX_group), swState);
                        }
                        Switch.setBackground(Color.GREEN);
                        swState = true;
                    }

                } catch (final KNXException e) {
                    LOG.info("Error reading KNX datapoint: " + e.getMessage());
                }
            }
        });
        labelPanel.add(Switch);


        JButton applica = new JButton("Applica");

        applica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                KNX_IP = ipField.getText();
                KNX_group = groupField.getText();
                try {
                    knxLink = new KNXNetworkLinkIP(KNX_IP, TPSettings.TP1);
                    pc = new ProcessCommunicatorImpl(knxLink);
                    LOG.info("read the group value from datapoint " + KNX_group);
                    final boolean value = pc.readBool(new GroupAddress(KNX_group));
                    LOG.info("value read from datapoint " + KNX_group + ": " + value);
                } catch (final KNXException e) {
                    LOG.info("Error reading KNX datapoint: " + e.getMessage());
                } catch (final InterruptedException e) {
                    LOG.info("Interrupted: " + e.getMessage());
                }
            }
        });
        
        settingsMenu.add(labelPanel, BorderLayout.WEST);
        settingsMenu.add(fieldPanel, BorderLayout.CENTER);
        settingsMenu.add(applica, BorderLayout.SOUTH);
                
                */
            
        try {
            // Create our network link. See other constructors if this one assumes too many
            // default settings.
            knxLink = new KNXNetworkLinkIP(KNX_IP , TPSettings.TP1);

            // create a process communicator using that network link
            pc = new ProcessCommunicatorImpl(knxLink);

            LOG.info("read the group value from datapoint " + KNX_group);
            // this is a blocking method to read a boolean from a KNX datapoint
            final boolean value = pc.readBool(new GroupAddress(KNX_group));
            LOG.info("value read from datapoint " + KNX_group + ": " + value);

            // this would write to the KNX datapoint, if you want to write back the same value we
            // just read, uncomment the next line
            // pc.write(group, value);
        } catch (final KNXException e) {
            LOG.info("Error reading KNX datapoint: " + e.getMessage());
        } catch (final InterruptedException e) {
            LOG.info("Interrupted: " + e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        // we don't need the process communicator anymore, detach it from the link
        if (pc != null) {
            pc.detach();
            LOG.info("KNX process communicator detached");
        }
        // close the KNX link
        if (knxLink != null) {
            knxLink.close();
            LOG.info("KNX link closed");
        }
        LOG.info("KNX plugin is stopped ");
    }

    @Override
    protected void onCommand(Command c)
            throws IOException, UnableToExecuteException {
        LOG.info("KNX-over-IP plugin receives a command called " + c.getName() + " with parameters "
                + c.getProperties().toString());
    }

    @Override
    protected boolean canExecute(Command c) {
        
        // pc.write(group, value);
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        
        // pc.write(group, value);
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
}