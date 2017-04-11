package hr.fer.zemris.java.custom.scripting.exec;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Represents the utility methdo containing all of the necessary operations for the 
 * smart script executing.
 * 
 * To fetch an action, user is obligated to use the resolving method.
 *  
 * @author Marin Grbić
 * @version 1.0
 */
public class OperationsUtil {

    /** 
     * Private constructor. Does not have it's implementation.
     */
    private OperationsUtil() {};
    
    /**
     * Represents the name of the sin action.
     */
    private static final String SIN_NAME = "sin";

    /**
     * Represents the name of the decfmt action.
     */
    private static final String DECFMT_NAME = "decfmt";

    /**
     * Represents the name of the dup action.
     */
    private static final String DUP_NAME = "dup";

    /**
     * Represents the name of the swap action.
     */
    private static final String SWAP_NAME = "swap";

    /**
     * Represents the name of the set mime type action.
     */
    private static final String SET_MIME_TYPE_NAME = "setMimeType";

    /**
     * Represents the name of the param get action.
     */
    private static final String PARAM_GET_NAME = "paramGet";

    /**
     * Represents the name of the pparam get action.
     */
    private static final String PPARAM_GET_NAME = "pparamGet";

    /**
     * Represents the name of the pparam set action.
     */
    private static final String PPARAM_SET_NAME = "pparamSet";

    /**
     * Represents the name of the pparam del action.
     */
    private static final String PPARAM_DEL_NAME = "pparamDel";

    /**
     * Represents the name of the tparam get action.
     */
    private static final String TPARAM_GET_NAME = "tparamGet";

    /**
     * Represents the name of the tparam set action.
     */
    private static final String TPARAM_SET_NAME = "tparamSet";

    /**
     * Represents the name of the tparam del action.
     */
    private static final String TPARAM_DEL_NAME = "tparamDel";

    /**
     * Represents the sin action.
     * Calculates the sin(x), takes only one argument.
     */
    private static final SmartScriptBiConsumer SIN = new SmartScriptBiConsumer(
	  (e,s) -> {
	      double x = (double) s.pop();
	      double r = Math.sin(Math.toRadians(x));
		  
	      s.push(r);
	  }, 
	  SIN_NAME
	  );

    /**
     * Represents the decimal format action.
     * Formats double to the given format.
     */
    private static final SmartScriptBiConsumer DECFMT = new SmartScriptBiConsumer(
	  (e,s) -> {
	      DecimalFormat f = new DecimalFormat((String) s.pop());
	      double x = (double) s.pop();
	      String str = f.format(x);
	      double r = Double.parseDouble(str.replaceAll(",", "."));
	      s.push(r);
	  }, 
	  DECFMT_NAME
	  );
    
    /**
     * Represents the dup action.
     * Duplicates the parameter at the top of stack.
     */
    private static final SmartScriptBiConsumer DUP = new SmartScriptBiConsumer(
	  (e,s) -> {
	      Object x = s.pop();
	      s.push(x);
	      s.push(x);
	  }, 
	  DUP_NAME
	  );
    
    /**
     * Represents the swap action.
     * Swaps the two top elements of the stack.
     */
    private static final SmartScriptBiConsumer SWAP = new SmartScriptBiConsumer(
	  (e,s) -> {
	      Object a = s.pop();
	      Object b = s.pop();
	      s.push(a);
	      s.push(b);
	  }, 
	  SWAP_NAME
	  );
    
    /**
     * Represents the set mime type action.
     * Sets the mime type, takes only one argument.
     */
    private static final SmartScriptBiConsumer SET_MIME_TYPE = new SmartScriptBiConsumer(
	  (e,s) -> {
	      String x = (String) s.pop();
	      e.setMimeType(x);
	  }, 
	  SET_MIME_TYPE_NAME
	  );
    
    /**
     * Represents the param get action.
     * Returns the parameter by it's key.
     */
    private static final SmartScriptBiConsumer PARAM_GET = new ParamGetter(
	  (e, n) -> e.getParameter(n),
	  PARAM_GET_NAME);
    
    /**
     * Represents the pparam get action.
     * Returns the persistent parameter by it's key.
     */
    private static final SmartScriptBiConsumer PPARAM_GET = new ParamGetter(
	  (e, n) -> e.getPersistentParameter(n),
	  PPARAM_GET_NAME);
    
    /**
     * Represents the tparam get action.
     * Returns the temporary parameter by it's key.
     */
    private static final SmartScriptBiConsumer TPARAM_GET = new ParamGetter(
	  (e, n) -> e.getTemporaryParameter(n),
	  TPARAM_GET_NAME);
    
    /**
     * Represents the pparam set action.
     * Sets the persistent parameter to the given value.
     */
    private static final SmartScriptBiConsumer PPARAM_SET = new ParamSetter(
	  (e,p) ->  e.setPersistentParameter(p.name, p.value), 
	  PPARAM_SET_NAME);
    
    /**
     * Represents the tparam set action.
     * Sets the temporary parameter to the given value.
     */
    private static final SmartScriptBiConsumer TPARAM_SET = new ParamSetter(
	  (e,p) ->  e.setTemporaryParameter(p.name, p.value), 
	  TPARAM_SET_NAME);
    
    /**
     * Represents the pparam del action.
     * Removes the persistent parameter by the given key.
     */
    private static final SmartScriptBiConsumer PPARAM_DEL = new ParamRemover(
	  (e,n) -> e.removePersistentParameter(n),
	  PPARAM_DEL_NAME);
    
    /**
     * Represents the tparam del action.
     * Removes the temporary parameter by the given key.
     */
    private static final SmartScriptBiConsumer TPARAM_DEL = new ParamRemover(
	  (e,n) -> e.removeTemporaryParameter(n),
	  TPARAM_DEL_NAME);
   
    /**
     * Represents the map of the actions.
     * Actions can be fetched from this map only using the appropriate public method for resolving.
     */
    private static final Map<String, SmartScriptBiConsumer> map = new HashMap<>();
    
    static {
        map.put(SIN_NAME,SIN);
        map.put(DECFMT_NAME,DECFMT);
        map.put(DUP_NAME,DUP);
        map.put(SWAP_NAME,SWAP);
        map.put(SET_MIME_TYPE_NAME,SET_MIME_TYPE);
        map.put(PARAM_GET_NAME,PARAM_GET);
        map.put(PPARAM_GET_NAME,PPARAM_GET);
        map.put(PPARAM_SET_NAME,PPARAM_SET);
        map.put(PPARAM_DEL_NAME,PPARAM_DEL);
        map.put(TPARAM_GET_NAME,TPARAM_GET);
        map.put(TPARAM_SET_NAME,TPARAM_SET);
        map.put(TPARAM_DEL_NAME,TPARAM_DEL);
    }
    
    /**
     * Resolves the action for the given string key.
     * If such action does not exist, returns <code>null</code>.
     * 
     * @param key The key used for resolving the action.
     * @return Action by the given key.
     */
    public static BiConsumer<RequestContext, ObjectStack> resolve(String key) {
        return map.get(key);
    }

    /**
     * Represents the edited {@link BiConsumer} adjusted for working with the smart script
     * operations. 
     * 
     * This class wraps the original {@link BiConsumer} giving it some additional informations
     * (like action name and etc.).
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class SmartScriptBiConsumer implements BiConsumer<RequestContext, ObjectStack> {

        /**
         * Represents the actual action to be performed.
         */
        private BiConsumer<RequestContext, ObjectStack> action;
        
        /**
         * Represents the name of the action.
         */
        private String actionName;
        
        /** 
         * Public constructor.
         * Sets fields to the given values.
         * 
         * @param action Given action to execute.
         * @param actionName Name of the action.
         */
        public SmartScriptBiConsumer(BiConsumer<RequestContext, ObjectStack> action, String actionName) {
	  this.action = action;
	  this.actionName = actionName;
        }
        
        @Override
        public void accept(RequestContext e, ObjectStack s) {
	  try {
		 action.accept(e, s);
	        } catch (Exception ex) {
		  System.err.println("Error during " + actionName + " operation.");
		  ex.printStackTrace();
	        }
        }
        
    }
    
    /**
     * Represents an inherited {@link SmartScriptEngine} action which gets a parameter.
     * 
     * User must provide the {@link Function} which will asure the parameter will be get.
     * This function is usually a simple getter.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class ParamGetter extends SmartScriptBiConsumer {

        /** 
         * Public constructor.
         * Sets fields to the given values.
         * 
         * @param function Function which gets the parameter.
         * @param actionName Name of the action.
         */
        public ParamGetter(BiFunction<RequestContext,String,String> function, String actionName) {
	  super(
		(e,s) -> {
		    String defValue = s.pop().toString();
		    String name = (String) s.pop();
		    String value = function.apply(e,name);

		    s.push(value == null ? defValue : value);
		}, actionName);
        }
    }
    
    /**
     * Represents an inherited {@link SmartScriptEngine} action which sets a parameter.
     * 
     * User must provide the {@link BiConsumer} which will asure the parameter will be set.
     * This consumer is usually a simple setter.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class ParamSetter extends SmartScriptBiConsumer {

        /** 
         * Public constructor.
         * Sets fields to the given values.
         * 
         * @param action Consumer which sets the parameter.
         * @param actionName Name of the action.
         */
        public ParamSetter(BiConsumer<RequestContext, Pair> action, String actionName) {
	  super((e,s) -> {
	      String name = (String) s.pop();
	      
	      Object valueObject = s.pop();
	      String value;
	      if(valueObject instanceof Number) {
		if(valueObject instanceof Double) {
		    value = ((Double) valueObject).toString();
		} else {
		    value = ((Integer)valueObject).toString();
		}
	      } else {
		value = (String) valueObject;
	      }
	      action.accept(e, new Pair(name, value));
	  }, actionName);
        }
        
        /**
         * Represents a simple pair of name and value used just for the {@link ParamSetter}.
         * Solves the problem of sending more than two arguments to the {@link BiConsumer} method.
         * 
         * @author Marin Grbić
         * @version 1.0
         */
        public static class Pair {
	  
	  /**
	   * Represents the name of the parameter.
	   */
	  public String name;
	  
	  /**
	   * Represents the value of the parameter.
	   */
	  public String value;
	  
	  /** 
	   * Public constructor.
	   * Sets fields to the given values.
	   * 
	   * @param name Name of the parameter.
	   * @param value Value of the parameter.
	   */
	  private Pair(String name, String value) {
	      this.name = name;
	      this.value = value;
	  }
        }
        
    }
    
    /**
     * Represents the simple parameter remover.
     * 
     * User is obligated to provide the consumer through the constructor 
     * which will remove the parameter.
     * 
     * @author Marin Grbić
     * @version 1.0
     */
    private static class ParamRemover extends SmartScriptBiConsumer {

        /** 
         * Public constructor.
         * Sets fields to the given values.
         * 
         * @param action Action which performs the removing.
         * @param actionName Name of the action.
         */
        public ParamRemover(BiConsumer<RequestContext, String> action, String actionName) {
	  super((e,s) -> {
	      String name = (String) s.pop();
	      action.accept(e, name);
	  }, actionName);
        }

       
    }
}
