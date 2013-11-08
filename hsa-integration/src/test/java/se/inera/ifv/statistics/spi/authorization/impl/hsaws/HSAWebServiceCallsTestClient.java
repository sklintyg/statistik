package se.inera.ifv.statistics.spi.authorization.impl.hsaws;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.inera.ifv.statistics.spi.authorization.impl.HSAWebServiceCalls;


/**
 * @author rlindsjo
 *
 */
public class HSAWebServiceCallsTestClient {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "HSAWebServiceCallsTest-applicationContext.xml", "hsa-services-config.xml"});
        HSAWebServiceCalls client = (HSAWebServiceCalls)ctx.getBean("wsCalls");
        try {
            client.callPing();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ctx.close();
        }
    }
}
