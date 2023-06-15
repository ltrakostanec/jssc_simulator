package jssc.demo.jssc_simulator;

import jssc.*;

import java.nio.charset.StandardCharsets;

public class JsscInitializor implements SerialPortEventListener {
	private SerialPort m_oSerialPort;
	private static final char SOH = '\u0001';
	private static final char STX = '\u0002';
	private static final char ETX = '\u0003';
	private static final char EOT = '\u0004';
	private static final char ACK = '\u0006';
	private static final char NAK = '\u0015';

	public JsscInitializor(SerialPort serialPort) {
		this.m_oSerialPort = serialPort;
	}
	public boolean openPort() {
		int iBuadRate = 9600;
		int iDataBits = 8;
		int iParity = SerialPort.PARITY_NONE;
		
		//get available system ports
		String[] portNames = SerialPortList.getPortNames();
		System.out.println("START open ports:");
		for (int i = 0; i < portNames.length; i++)
			System.out.println(portNames[i]);
		System.out.println("END open ports\n");
		
		if(m_oSerialPort != null && m_oSerialPort.isOpened())
			System.out.println("Fail to build connection");
		
		try {
			System.out.println("Opening port " + JsscSimulatorApplication.sPort + " ...");
			if(!m_oSerialPort.openPort()) {
				System.out.println(JsscSimulatorApplication.sPort+" could not be open !!!");
				return false;
			}
			System.out.println(JsscSimulatorApplication.sPort+" open !!!");
		
			if(!m_oSerialPort.setParams(iBuadRate, iDataBits, SerialPort.STOPBITS_1, iParity))
				System.out.println(JsscSimulatorApplication.sPort+" Fail to build connection");
			
			m_oSerialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
		
		} catch (Exception e) {
			System.out.println("There are an error on writing string to port т: " + e);
			return false;
		}
			return true;
	}
	
	public boolean closePort() {
		if(!m_oSerialPort.isOpened())
		return false;
		
		try {
			m_oSerialPort.closePort();
		
		}catch (SerialPortException e){
			System.out.println(JsscSimulatorApplication.sPort+" port NOT CLOSED!/nERROR: "+e);
			return false;
		}
		System.out.println(JsscSimulatorApplication.sPort+" port CLOSED!");
		return true;
	}


	private String createCheckSum(byte[] oDataByte) {
		String sCheckSum = "    ";
		int iCheckSum = 0;
		int iStrLength = oDataByte.length;
		
		// convert byte to unsigned integer
		for(int i=0; i<iStrLength; i++)
			iCheckSum += oDataByte[i] & 0xFF;
		
		sCheckSum = String.format("%04X", iCheckSum);
		sCheckSum = sCheckSum.toUpperCase();
		return sCheckSum;
	}
		
	
	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		System.out.println("Packed received");
		String sSendString = "";
		String sCheckSum = "";
		String sOutletMachineId = "0954";
		if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
			try {
				String receivedData = m_oSerialPort.readString(serialPortEvent.getEventValue());
				System.out.println("Received response: " + receivedData);
				if (!receivedData.isEmpty()) {
					String sSendData = "Data sent.received OK";
					//create check sum
					sSendString = sOutletMachineId + STX + sSendData + ETX;
					sCheckSum = createCheckSum(sSendString.getBytes(StandardCharsets.UTF_8));
					sSendString = ACK + sSendString + sCheckSum + EOT;
					m_oSerialPort.writeBytes(sSendString.getBytes(StandardCharsets.UTF_8));
					System.out.println("Packed sent");
				}
			} catch (SerialPortException ex) {
				System.out.println("Error in receiving string from COM-port: " + ex);
			}
		}
	}
}
