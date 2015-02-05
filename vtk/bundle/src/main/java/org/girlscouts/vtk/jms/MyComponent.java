package org.girlscouts.vtk.jms;

public class MyComponent {
}
/*
 * import javax.jms.Connection; import javax.jms.Destination; import
 * javax.jms.JMSException; import javax.jms.Message; import
 * javax.jms.MessageConsumer; import javax.jms.MessageListener; import
 * javax.jms.Session; import javax.jms.TextMessage; import
 * javax.jms.TopicConnection; import javax.jms.TopicPublisher; import
 * javax.jms.TopicSession;
 * 
 * import org.apache.felix.scr.annotations.Activate; import
 * org.apache.felix.scr.annotations.Component; import
 * org.apache.felix.scr.annotations.Deactivate; import
 * org.apache.felix.scr.annotations.Reference;
 * 
 * //import com.cognifide.jms.api.consumer.SlingMessageConsumer;
 * 
 * @SlingMessageConsumer( destinationType = DestinationType.TOPIC, subject =
 * "my	topic")
 * 
 * @Component
 * 
 * public class MyComponent implements MessageListener {
 * 
 * @Reference //private JmsConnectionProvider connectionProvider; private
 * Connection connection; private Session session; private MessageConsumer
 * consumer;
 * 
 * private TopicSession pubSession; private TopicSession subSession; private
 * TopicPublisher publisher; private TopicConnection tConnection;
 * 
 * @Activate protected void activate() throws JMSException { //connection =
 * connectionProvider.getConnection(); session = connection.createSession(false,
 * Session.AUTO_ACKNOWLEDGE); Destination dest =
 * session.createTopic("my	topic"); consumer = session.createConsumer(dest);
 * consumer.setMessageListener(this); connection.start(); }
 * 
 * @Deactivate protected void deactivate() throws JMSException {
 * consumer.close(); session.close(); connection.close(); }
 * 
 * 
 * public void onMessage(Message message) { try { TextMessage GSMessage =
 * (TextMessage) message; String text = GSMessage.getText( );
 * System.out.println(text); } catch (JMSException jmse){ jmse.printStackTrace(
 * ); } }
 * 
 * protected void writeMessage(String text) throws JMSException { TextMessage
 * message = pubSession.createTextMessage( ); message.setText(" Txt: "+text);
 * publisher.publish(message); }
 * 
 * }
 */