import java.io.*;
import java.io.IOException;

public class Print implements Values{
	private int l;
	private String file;
	private PrintWriter writer;
	
	public void Print(){
		l = 0;
	}
	
	
	public void setlevel(int i){
		l=i;
	}
	
	public void set(int level, String filename) {
		l = level;
		file = filename;
		
		try{
		writer = new PrintWriter(file, "UTF-8");
		} catch (IOException ex) {
		}
	}
	
	public void unset(){
		writer.close();
	}
	
	public void show(String s, int c){
		if (c <= l)
			synchronized (System.out) {
			System.out.println(s);
			}
	}
	
	public void write(String line) {
		try{
			writer.println(line);
		}
		
		catch (NullPointerException e) {
		e.printStackTrace();
	    }
	
	}
	
}

	
	
