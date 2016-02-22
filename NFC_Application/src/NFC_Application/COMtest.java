package NFC_Application;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
public class COMtest {
   public static String COMtest()
   {
	   for (int i=0;i<50; i++)
		{
		String com="COM"+i;
		try{
		CommPortIdentifier portId =  CommPortIdentifier.getPortIdentifier(com);
		//System.out.println(portId.getName());
		return com;
		}		catch (NoSuchPortException e)
		{
		//System.err.println(com);
		}
		}
	return null;

   }
	public static void main(String[] args) {

		System.out.println(COMtest());
}
}