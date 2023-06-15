package jssc.demo.jssc_simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jssc.SerialPort;
import jssc.SerialPortException;
@SpringBootApplication
public class JsscSimulatorApplication {
	private static SerialPort m_oSerialPort;
	public static String sPort = "COM9";
	public static void main(String[] args) {
		SpringApplication.run(JsscSimulatorApplication.class, args);
		m_oSerialPort = new SerialPort(sPort);
		
		JsscInitializor oJsscInitializor = new JsscInitializor(m_oSerialPort);
		System.out.println("Runing....");
		if(oJsscInitializor.openPort()){
			try {
				m_oSerialPort.addEventListener(oJsscInitializor, SerialPort.MASK_RXCHAR);
			} catch (SerialPortException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
}
