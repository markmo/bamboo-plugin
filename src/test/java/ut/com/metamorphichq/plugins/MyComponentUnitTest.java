package ut.com.metamorphichq.plugins;

import org.junit.Test;
import com.metamorphichq.plugins.MyPluginComponent;
import com.metamorphichq.plugins.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}