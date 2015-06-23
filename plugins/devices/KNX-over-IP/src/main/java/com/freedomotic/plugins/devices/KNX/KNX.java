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
import com.freedomotic.things.ThingRepository;
import com.freedomotic.reactions.Command;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.logging.Logger;

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
    final int POLLING_WAIT;
    final String KNX_IP = "127.0.0.1";
    final int KNX_PORT = 3671;
    final String KNX_group = "1/1/1";
    private boolean KNX_value = false;
    private KNXNetworkLink knxLink = null;
    private ProcessCommunicator pc = null;

    @Inject
    private ThingRepository thingsRepository;

    public KNX() {
        //every plugin needs a name and a manifest XML file
        super("KNX-over-IP", "/KNX-over-IP/KNX-over-IP-manifest.xml");
        //read a property from the manifest file below which is in
        //FREEDOMOTIC_FOLDER/plugins/devices/com.freedomotic.hello/hello-world.xml
        POLLING_WAIT = configuration.getIntProperty("time-between-reads", 2000);
        //POLLING_WAIT is the value of the property "time-between-reads" or 2000 millisecs,
        //default value if the property does not exist in the manifest
        setPollingWait(POLLING_WAIT); //millisecs interval between hardware device status reads
    }

    @Override
    protected void onShowGui() {
        /**
         * uncomment the line below to add a GUI to this plugin the GUI can be
         * started with a right-click on plugin list on the desktop frontend
         * (com.freedomotic.jfrontend plugin)
         */
        //bindGuiToPlugin(new KnxGUI(this));
    }

    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {
        try {
            KNX_value = pc.readBool(new GroupAddress(KNX_group));
            LOG.info("value read from datapoint " + KNX_group + ": " + KNX_value);
        } catch (final KNXException e) {
            LOG.info("Error reading KNX datapoint: " + e.getMessage());
        } catch (final InterruptedException e) {
            LOG.info("Interrupted: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        LOG.info("KNX plugin is started");

        try {
            // Create our network link. See other constructors if this one assumes too many
            // default settings.
            knxLink = new KNXNetworkLinkIP(KNX_IP, TPSettings.TP1);

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
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        
        // pc.write(group, value);
        
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
