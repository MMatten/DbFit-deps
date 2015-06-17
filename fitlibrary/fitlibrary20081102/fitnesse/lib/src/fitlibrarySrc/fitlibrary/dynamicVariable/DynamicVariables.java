package fitlibrary.dynamicVariable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fit.exception.FitFailureException;

public class DynamicVariables {
	private Properties properties = new Properties(System.getProperties());

	public DynamicVariables() {
		//
	}
	public DynamicVariables(String[] s) {
		for (int i = 0; i < s.length-1; i += 2)
			properties.put(s[i],s[i+1]);
	}
	public DynamicVariables(DynamicVariables propertyValues) {
		properties = (Properties) propertyValues.properties.clone();
	}
	public boolean addPropertiesFromFile(String fileName) {
		try {
			InputStream in = new FileInputStream(fileName);
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			throw new FitFailureException("File not found");
		} catch (IOException e) {
			throw new FitFailureException("Problem reading Properties file: "+e);
		}
		return true;
	}
	public String resolve(String locator) {
		String result = locator;
		int pos = 0;
		int loops = 0;
		while (true) {
			pos = result.indexOf("@{",pos);
			if (pos < 0)
				break;
			if (loops++ > 10000)
				return "INFINITE SUBSTITUTION!";
			int end = result.indexOf("}",pos);
			if (end >= 0) {
				String substitute = properties.getProperty(result.substring(pos+2,end));
				if (substitute != null) {
					result = result.substring(0,pos) + substitute + result.substring(end+1);
					pos = 0;
				}
				else
					pos += 2;
			}
		}
		return result;
	}
	public void put(String key, String value) {
		properties.setProperty(key, value);
	}
	public void clearAll() {
		properties.clear();
	}
	public String get(String key) {
		return properties.getProperty(key);
	}
}
