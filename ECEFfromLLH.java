import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class ECEFfromLLH {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static ArrayList<String> aisData = new ArrayList<String>();
	static ArrayList<String> distanceData = new ArrayList<String>();
	
	static double PI = 3.1415926535898;
	static double A	= 6378137.0;
	static double ONE_F = 298.2577223563;
	static double B	= A * (1.0 - 1.0 / ONE_F);
	static double E2 = (1.0 / ONE_F) * (2 - (1.0 / ONE_F));
	static double ED2 = E2 * A * A / (B * B);
	
	public static void main(String[] args) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		
		double[] xyz = new double[3];
		
		// get argument
		String readfile = args[0];
		String writefile = args[1];
		double longitude = Double.parseDouble(args[2]);
		double latitude	= Double.parseDouble(args[3]);
		double height = Double.parseDouble(args[4]);
		
		// get time, mmsi, longitude, latitude from ais(dec1)
		aisRead(readfile);
		
		// convert LLH into ECEF
		xyz = convert(longitude, latitude, height);
		System.out.println("Given Position(x, y, z) [m]");
		System.out.println(xyz[0] + " " + xyz[1] + " " + xyz[2]);
		
		// calc Euclidean distance
		calcDistance(xyz);
		
		// write csv file
		writeCSV(writefile);
	}
	
	
	private static void calcDistance(double[] xyz) {
		// TODO 自動生成されたメソッド・スタブ
		double[] shipXYZ = new double[3];
		double shipLong, shipLat, distance;
		String time, mmsi;
		for(int i = 0; i < aisData.size(); i += 4) {
			time = aisData.get(i);
			mmsi = aisData.get(i + 1);
			shipLong = Double.parseDouble(aisData.get(i + 2));
			shipLat = Double.parseDouble(aisData.get(i + 3));
			shipXYZ = convert(shipLong, shipLat, 0.0);
			distance = Math.sqrt(Math.pow((xyz[0] - shipXYZ[0]), 2) + Math.pow((xyz[1] - shipXYZ[1]), 2) + Math.pow((xyz[2] - shipXYZ[2]), 2));
			distanceData.add(time);
			distanceData.add(mmsi);
			distanceData.add(Double.toString(distance));
		}
	}


	private static void writeCSV(String filename) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		File file = new File(filename);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		for(int i = 0; i < distanceData.size(); i += 3) {
			pw.println(distanceData.get(i) + "," + distanceData.get(i + 1) + "," + distanceData.get(i + 2));
		}
		pw.close();
	}


	private static double[] convert(double longitude, double latitude, double height) {
		// TODO 自動生成されたメソッド・スタブ
		double[] xyz = new double[3];
		xyz[0] = (NN(latitude)+height) * Math.cos(latitude * PI / 180) * Math.cos(longitude * PI / 180);
		xyz[1] = (NN(latitude)+height) * Math.cos(latitude * PI / 180) * Math.sin(longitude * PI / 180);
		xyz[2] = (NN(latitude) * (1 - E2) + height) * Math.sin(latitude * PI / 180);
		return xyz;
	}


	private static double NN(double d) {
		// TODO 自動生成されたメソッド・スタブ
		return (A / Math.sqrt(1.0 - (E2) * (Math.sin(d * PI / 180.0)) * (Math.sin(d * PI / 180.0))));
	}


	private static void aisRead(String filename) throws IOException {
		BufferedReader in = new BufferedReader(
				new FileReader(filename));
		
		String line;
		String[] csv;
		while((line = in.readLine()) != null) {
			csv = line.split(",");
			aisData.add(csv[0]);
			aisData.add(csv[3]);
			aisData.add(csv[8]);
			aisData.add(csv[9]);
		}
	}

}
