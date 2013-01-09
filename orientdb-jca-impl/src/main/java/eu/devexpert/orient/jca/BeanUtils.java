package eu.devexpert.orient.jca;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

/**
 * DOCUMENT ME!
 * 
 * @author Dumitru Ciubenco
 * @since 1.0.0
 * @created Jan 9, 2013 12:29:19 PM
 */
public final class BeanUtils {
	/**
	 * String used to separate multiple properties inside of embedded beans.
	 */
	private static final String	PROPERTY_SEPARATOR	= ".";

	/**
	 * An empty class array used for null parameter method reflection
	 */
	private static Class[]		NO_PARAMETERS_ARRAY	= new Class[] {};
	/**
	 * an empty object array used for null parameter method reflection
	 */
	private static Object[]		NO_ARGUMENTS_ARRAY	= new Object[] {};

	public static Object getObjectAttribute(Object bean, String propertyNames) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		Object result = bean;

		StringTokenizer propertyTokenizer = new StringTokenizer(propertyNames, PROPERTY_SEPARATOR);

		// Run through the tokens, calling get methods and
		// replacing result with the new object each time.
		// If the result equals null, then simply return null.
		while(propertyTokenizer.hasMoreElements() && result != null) {
			Class resultClass = result.getClass();
			String currentPropertyName = propertyTokenizer.nextToken();

			PropertyDescriptor propertyDescriptor = getPropertyDescriptor(currentPropertyName, resultClass);

			Method readMethod = propertyDescriptor.getReadMethod();
			if(readMethod == null) {
				throw new IllegalAccessException("User is attempting to " + "read from a property that has no read method.  "
						+ " This is likely a write-only bean property.  Caused " + "by property [" + currentPropertyName + "] on class [" + resultClass + "]");
			}

			result = readMethod.invoke(result, NO_ARGUMENTS_ARRAY);
		}

		return result;
	}

	private static final PropertyDescriptor getPropertyDescriptor(String propertyName, Class beanClass) {

		PropertyDescriptor resultPropertyDescriptor = null;

		char[] pNameArray = propertyName.toCharArray();
		pNameArray[0] = Character.toLowerCase(pNameArray[0]);
		propertyName = new String(pNameArray);

		try {
			resultPropertyDescriptor = new PropertyDescriptor(propertyName, beanClass);
		}
		catch(IntrospectionException e1) {
			// Read-only and write-only properties will throw this
			// exception. The properties must be looked up using
			// brute force.

			// This will get the list of all properties and iterate
			// through looking for one that matches the property
			// name passed into the method.
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);

				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

				for(int i = 0; i < propertyDescriptors.length; i++) {
					if(propertyDescriptors[i].getName().equals(propertyName)) {

						// If the names match, this this describes the
						// property being searched for. Break out of
						// the iteration.
						resultPropertyDescriptor = propertyDescriptors[i];
						break;
					}
				}
			}
			catch(IntrospectionException e2) {
				e2.printStackTrace();
			}
		}
		// If no property descriptor was found, then this bean does not
		// have a property matching the name passed in.
		if(resultPropertyDescriptor == null) {
			System.out.println("resultPropertyDescriptor == null");
		}

		return resultPropertyDescriptor;
	}

}
