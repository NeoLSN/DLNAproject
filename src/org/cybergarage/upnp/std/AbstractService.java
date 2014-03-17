
package org.cybergarage.upnp.std;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.ActionList;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;

public abstract class AbstractService implements ActionListener, QueryListener {
    Device device = null;

    Service service = null;

    public final static String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
    public final static String NOT_IMPLEMENTED_I4 = "2147483647";

    protected long seq; // event seq number

    public AbstractService(Device device) throws InvalidDescriptionException {
        // Get underlying Cybergarage service object for this service and
        // install actionControlReceived() 'dispatcher' method below for
        // all actions
        this.device = device;

        ServiceList serviceList = device.getServiceList();
        if (serviceList == null) {
            throw new InvalidDescriptionException("Null device service list");
        }

        int n;
        for (n = 0; n < serviceList.size(); n++) {
            Service service = serviceList.getService(n);
            if (service.isService(getServiceType())) {
                this.service = service;
                service.setQueryListener(this);
                ActionList actionList = service.getActionList();
                for (int j = 0; j < actionList.size(); j++) {
                    Action action = actionList.getAction(j);
                    action.setActionListener(this);
                }
                break;
            }
        }

        if (n == serviceList.size()) {
            throw new InvalidDescriptionException("Device doesn't support service '"
                    + getServiceType());
        }
        initializeStateVariables();
    }

    public AbstractService() {
    }

    abstract public String getServiceType();

    abstract protected void initializeStateVariables();

    public Device getDevice() {
        return device;
    }

    public Service getService() {
        return service;
    }

    public Action getAction(String actionName) {
        if (service == null)
            return null;

        return service.getAction(actionName);
    }

    public void setStateVariable(String varName, String value) {
        StateVariable stateVar = service.getStateVariable(varName);
        if (stateVar == null) {
            return;
        }
        stateVar.setValue(value);
    }

    public StateVariable getStateVariable(String varName) {
        return service.getStateVariable(varName);
    }

    public String getStateVariableValue(String varName) {
        return service.getStateVariable(varName).getValue();
    }

    @Override
    public boolean actionControlReceived(Action action) {
        // Convention is for this service to have methods with same name
        // as UPNP action. Look up method by name and invoke it
        // Class c = this.getClass();
        // Class[] parameterTypes = new Class[] {
        // Action.class
        // };
        // Method actionMethod;
        // Object[] arguments = new Object[] {
        // action
        // };
        //
        // try {
        // // Action methods are named using the string "action" followed
        // // by the UPNP Action name, e.g. "actionPlay". This is to
        // // clearly differentiate which methods in derived classes are
        // // action callbacks
        // actionMethod = c.getMethod("action" + action.getName(),
        // parameterTypes);
        // Boolean result = (Boolean)actionMethod.invoke(this, arguments);
        // return result.booleanValue();
        // } catch (NoSuchMethodException e) {
        //
        // e.getCause().printStackTrace();
        // } catch (IllegalAccessException e) {
        // e.getCause().printStackTrace();
        // } catch (InvocationTargetException e) {
        // e.getCause().printStackTrace();
        // }

        return false;
    }

    @Override
    public boolean queryControlReceived(StateVariable stateVar) {
        String varName = stateVar.getName();

        StateVariable serviceStateVar = getService().getStateVariable(varName);
        if (serviceStateVar == null)
            return false;

        stateVar.setValue(serviceStateVar.getValue());

        return true;
    }

    public boolean setArgumentValueFromRelatedStateVariable(Action action, String argName) {
        Argument arg = action.getArgument(argName);
        if (arg == null) {
            return false;
        }

        StateVariable stateVar = arg.getRelatedStateVariable();
        if (stateVar == null) {
            return false;
        }

        arg.setValue(stateVar.getValue());
        return true;
    }

    public boolean setRelatedStateVariableFromArgument(Action action, String argName) {
        Argument arg = action.getArgument(argName);
        if (arg == null) {
            return false;
        }

        StateVariable stateVar = arg.getRelatedStateVariable();
        if (stateVar == null) {
            return false;
        }

        if (stateVar.hasAllowedValueList()
                && !stateVar.getAllowedValueList().contains(arg.getValue())) {
            return false;
        }

        stateVar.setValue(arg.getValue());
        return true;
    }

}
