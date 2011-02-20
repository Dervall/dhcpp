package com.dervall.servlet;

import com.dervall.subnet.Subnet;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.dhcp4java.DHCPPacket;
import org.dhcp4java.DHCPServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: Per
 * Date: 2011-02-19
 * Time: 09:41
 */
public class StandardServlet extends DHCPServlet {
    private static final Logger logger = Logger.getLogger(StandardServlet.class.getName().toLowerCase());

    private GroovyScriptEngine scriptEngine;
    private List<Subnet> subnets;

    public StandardServlet() throws IOException {
        scriptEngine = new GroovyScriptEngine(new String[] {"./script"});
        subnets = new ArrayList<Subnet>();
    }

    @Override
    public DHCPPacket service(DHCPPacket request) {
        Boolean processRequest = executeRequestScript(request, "onservice.groovy");

        if (processRequest != null && processRequest.equals(Boolean.TRUE)) {
            logger.finer("Servicing request!");
            return super.service(request);
        } else {
            logger.finer("Service denied to request");
            return null;
        }
    }

    @Override
    protected DHCPPacket doDiscover(DHCPPacket request) {
        return executeRequestScript(request, "discover.groovy");
    }

    @Override
    protected DHCPPacket doRequest(DHCPPacket request) {
        return executeRequestScript(request, "request.groovy");
    }

    private <T> T executeRequestScript(DHCPPacket request, String scriptName) {
        Binding binding = getStandardBindings();
        binding.setVariable("request", request);

        T result = null;

        try {
            //noinspection unchecked
            result = (T)scriptEngine.run(scriptName, binding);
        } catch (ResourceException e) {
            logger.log(Level.SEVERE, "Exception running service()", e);
        } catch (ScriptException e) {
            logger.log(Level.SEVERE, "ScriptException running service()", e);
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, "Script " + scriptName + " returned the wrong class", e);
        }
        return result;
    }

    private Binding getStandardBindings() {
        Binding binding = new Binding();
        binding.setVariable("logger", logger);
        binding.setVariable("servlet", this);
        return binding;
    }

    public List<Subnet> getSubnets() {
        return subnets;
    }
}
