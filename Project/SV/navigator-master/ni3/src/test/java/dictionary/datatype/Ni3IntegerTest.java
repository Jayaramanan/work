package dictionary.datatype;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.datatype.Ni3Integer;
import junit.framework.TestCase;

public class Ni3IntegerTest extends TestCase{

	public void testCheckValueNotNull(){
		final Ni3Integer ni3Integer = new Ni3Integer(new Attribute());

		final boolean value = ni3Integer.checkValue(100);
		assertTrue(value);
	}

	public void testCheckValueNull(){
		final Ni3Integer ni3Integer = new Ni3Integer(new Attribute());

		final boolean value = ni3Integer.checkValue(null);
		assertTrue(value);
	}

	public void testDisplayFormat(){
		// final Attribute attr = new Attribute();
		// attr.formatFactory = new DefaultFormatterFactory();
		// final AbstractFormatter formatter = new Ni3Integer(null).getDefaultDisplayFormatter();
		// attr.formatFactory.setDefaultFormatter(formatter);
		// attr.formatFactory.setDisplayFormatter(formatter);
		// attr.formatFactory.setEditFormatter(formatter);
		// final Ni3Integer ni3Integer = new Ni3Integer(attr);
		//
		// final String actualValue = ni3Integer.displayValue(54321, false);
		// assertEquals("54321", actualValue);
	}

	public void testEditValue(){
		// final Attribute attr = new Attribute();
		// attr.formatFactory = new DefaultFormatterFactory();
		// final AbstractFormatter formatter = new Ni3Integer(null).getDefaultDisplayFormatter();
		// attr.formatFactory.setDefaultFormatter(formatter);
		// attr.formatFactory.setDisplayFormatter(formatter);
		// attr.formatFactory.setEditFormatter(formatter);
		// final Ni3Integer ni3Integer = new Ni3Integer(attr);
		//
		// final String actualValue = ni3Integer.editValue(54321);
		// assertEquals("54321", actualValue);
	}

	public void testFormatValue(){
		// final Attribute attr = new Attribute();
		// final Ni3Integer ni3Integer = new Ni3Integer(attr);
		//
		// assertEquals("null", ni3Integer.formatValue(null));
		// assertEquals("54321", ni3Integer.formatValue("54321"));
	}

	public void testGetValue(){
		// final Attribute attr = new Attribute();
		// final Ni3Integer ni3Integer = new Ni3Integer(attr);
		//
		// assertNull(ni3Integer.getValue(""));
		// assertNull(ni3Integer.getValue("random junk"));
		// assertNull(ni3Integer.getValue("12345x"));
		// assertEquals(Integer.valueOf(54321), ni3Integer.getValue("54321"));
	}

}
