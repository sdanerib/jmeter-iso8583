package nz.co.breakpoint.jmeter.iso8583;

import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

public class ISO8583SamplerTest extends ISO8583TestBase {

    ISO8583Sampler instance = new ISO8583Sampler();

    @Test
    public void shouldApplyClosestConfig() {
        ISO8583Config inner = new ISO8583Config();
        inner.setHost("THIS");
        inner.setPackager(defaultPackagerFile);
        ISO8583Config outer = new ISO8583Config();
        outer.setHost("NOT_THIS");
        outer.setPort("PORT");
        instance.addTestElement(inner);
        instance.addTestElement(outer);

        assertEquals(inner.getPackager(), instance.config.getPackager());
        assertEquals(inner.getHost(), instance.config.getHost());
        assertEquals(outer.getPort(), instance.config.getPort());
        assertEquals("", instance.config.getClassname());
    }

    @Test
    public void shouldRestoreFieldsBetweenIterations() {
        instance.addTestElement(getDefaultTestConfig());
        instance.setFields(new ArrayList<>());
        instance.addField("0", "0800");
        assertEquals(1, instance.getFields().size());
        instance.setRunningVersion(true);
        instance.addField("11", "1234");
        instance.recoverRunningVersion();
        assertEquals(1, instance.getFields().size());
    }

    @Test
    public void shouldMergeNestedComponents() {
        instance.addTestElement(getDefaultTestConfig());
        instance.addField("0", "0800");
        instance.addField("11", "ALREADY_THERE");

        ISO8583Component inner = new ISO8583Component();
        inner.setFields(asMessageFields(
            new MessageField("0", "0000"),
            new MessageField("41", "THIS")
        ));
        ISO8583Component outer = new ISO8583Component();
        outer.setFields(asMessageFields(
            new MessageField("11", "IGNORED"),
            new MessageField("41", "NOT_THIS")
        ));
        instance.addTestElement(inner);
        instance.addTestElement(outer);

        assertEquals(3, instance.getFields().size());
        assertTrue(instance.getRequest().hasFields(new int[]{0, 11, 41}));
    }
}
