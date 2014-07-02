import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class ParseInputFile {
	private String inputFile = null;
	private final String namePropFile = "name.prop"; 
	private final String addressPropFile = "address.prop";
	private Parse nameParser;
	private Parse addressParser;
	
	public ParseInputFile(String inputFileName) throws Exception{
		inputFile = inputFileName;
		nameParser = new Parse(namePropFile);
		addressParser = new Parse(addressPropFile);
		nameParser.trainData();
		addressParser.trainData();
	}
	
	public ArrayList<UserDTO> parse() throws Exception{
		
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(inputFile));

		String line;
		ArrayList<String> inList = new ArrayList<String>();
		ArrayList<String> outList = new ArrayList<String>();
		ArrayList<UserDTO> userList = new ArrayList<UserDTO>();
		ArrayList<String> addressList = new ArrayList<String>();
		UserDTO user;
		int flag = 0;
		while((line = br.readLine()) != null)
		{
			if(line.equals(""))
				continue;
			if(line.equals(";"))
			{
				addressList = splitAddress(addressList);
				/*
				if(addressList.size()>2)
					outList.addAll(addressParser.testResults(addressList));
				else
					outList.addAll(addressParser.testResults(inList));
				*/
				outList.addAll(addressParser.testResults(addressList));
				inList.clear();
				addressList.clear();
				flag = 0;
				user = structureData(outList);
				userList.add(user);
				outList = new ArrayList<String>();
				continue;
			}
			
			String[] tokens = line.trim().split("\\s+");
			for (String t : tokens) {
				t = t.trim();
				if (t.equals("") || t.equals(",") || t.equals(".")
						|| t.equals("-") || t.equals("|"))
					continue;
				inList.add(t);
			}
			if(inList.isEmpty())
				continue;
			if(flag == 0)
			{
				outList.addAll(nameParser.testResults(inList));
				inList.clear();
				flag++;
			}
			else if (flag == 1)
			{
				outList.add(inList.get(0)+"\t\tgender");
				inList.clear();
				flag++;
			}
			else if (flag == 2)
			{
				outList.add(inList.get(0)+"\t\tage");
				inList.clear();
				flag++;
			}
			else if (flag == 3)
			{
				String[] token = line.trim().split("(,|-|'|;|:|\\||\\?)|(?<=\\b[.])");
				for (String t : token) {
					t = t.trim();
					if (t.equals("") || t.equals(",") || t.equals(".")
							|| t.equals("-") || t.equals("|") || t.equals("?"))
						continue;
					addressList.add(t);
				}
				//outList.addAll(parse(addressPropFile,inList));
			}
		}
		
		br.close();
		return userList;
	}
	
	
	private UserDTO structureData(ArrayList<String> list){
		UserDTO user = new UserDTO();
		for(String str : list){
			String[] tokens = str.trim().split("\\t");
			String key = tokens[2],value = tokens[0];
			
			if(key.equals("salutation")){
				user.setSalutation(user.getSalutation().trim() + " " + value);
			}
			else if(key.equals("abbrevation")){
				user.setAbbrevation(user.getAbbrevation().trim() + " " + value);
			}
			else if(key.equals("initial")){
				user.setInitial(user.getInitial().trim() + " " + value);
			}
			else if(key.equals("firstname") && user.getFirstName().equals("")){
				user.setFirstName(value);
			}
			else if(key.equals("lastname") && user.getLastName().equals("")){
				user.setLastName(value);
			}
			
			else if(key.equals("housenumber")){
				user.setHouseNumber(user.getHouseNumber().trim() + " " + value);
			}
			else if(key.equals("housename")){
				user.setHouseName(user.getHouseName().trim() + " " + value);
			}
			else if(key.equals("street")){
				user.setStreet(user.getStreet().trim() + " " + value);
			}
			else if(key.equals("village")){
				user.setVillage(user.getVillage().trim() + " " + value);
			}
			else if(key.equals("subdistrict")){
				user.setSubDistrict(user.getSubDistrict().trim() + " " + value);
			}
			else if(key.equals("district")){
				user.setDistrict(user.getDistrict().trim() + " " + value);
			}
			else if(key.equals("state")){
				user.setState(user.getState().trim() + " " + value);
			}
			else if(key.equals("postcode")){
				user.setPostCode(user.getPostCode().trim() + " " + value);
			}
			
			else if(key.equals("gender")){
				user.setGender(Integer.parseInt(value));
			}
			
			else if(key.equals("age")){
				user.setAge(Integer.parseInt(value));
			}
		}
		return user;
	}
	
	private ArrayList<String> splitAddress(ArrayList<String> in)
	{
		ArrayList<String> out = new ArrayList<String>();
		int n = in.size();
		int n3 = n / 3;
		int n2 = n3;
		int n1 = n - n2 - n3;
		if(n == 0)
			return out;
		else if(n == 1)
		{
			String str = in.get(0);
			String[] token = str.trim().split("\\s+");
			ArrayList<String> tokens = new ArrayList<String>();
			for (String t : token) {
				t = t.trim();
				if (t.equals("") || t.equals(",") || t.equals(".")
						|| t.equals("-") || t.equals("|"))
					continue;
				tokens.add(t);
			}
			int len = tokens.size();
			if(len <=0)
				return out;
			if(len < 3)
				out.addAll(tokens);
			else if(len < 6)
			{
				int j = len;
				while(j>3)
				{
					out.add(tokens.get(len-j)+" "+tokens.get(len-j+1));
					j = j-2;
				}
				while(j>0)
				{
					out.add(tokens.get(len-j));
					j--;
				}
			}
			else
			{
				int j = len;
				while(j > 3)
				{
					int index = len - j;
					out.add(tokens.get(index)+" "+tokens.get(index+1)+" "+tokens.get(index+2));
					j = j-3;
				}
				if(j == 3)
				{
					out.add(tokens.get(len-3)+" "+tokens.get(len-2));
					out.add(tokens.get(len-1));
				}
				else if(j == 2)
				{
					out.add(tokens.get(len-2)+" "+tokens.get(len-1));
				}
				else
					out.add(tokens.get(len-1));
			}
		}
		else
		{
			if(n == 2)
			{
				n1 = n3 = 1;
			}
			for(int i=0;i<n;i++)
			{
				String str = in.get(i);
				String[] token = str.trim().split("\\s+");
				ArrayList<String> tokens = new ArrayList<String>();
				for (String t : token) {
					t = t.trim();
					if (t.equals("") || t.equals(",") || t.equals(".")
							|| t.equals("-") || t.equals("|"))
						continue;
					tokens.add(t);
				}
				int len = tokens.size();
				if(len <=0)
					continue;
				if(i<n1)
				{
					if(len < 4)
						out.add(str);
					else if(len == 4)
					{
						out.add(tokens.get(0)+" "+tokens.get(1));
						out.add(tokens.get(2)+" "+tokens.get(3));
					}
					else
					{
						int j = len;
						while(j > 3)
						{
							int index = len - j;
							out.add(tokens.get(index)+" "+tokens.get(index+1)+" "+tokens.get(index+2));
							j = j-3;
						}
						if(j == 3)
						{
							out.add(tokens.get(len-3)+" "+tokens.get(len-2));
							out.add(tokens.get(len-1));
						}
						else if(j == 2)
						{
							out.add(tokens.get(len-2)+" "+tokens.get(len-1));
						}
						else
							out.add(tokens.get(len-1));
					}
				}
				
				else if(i<n2+n1)
				{
					if(len < 3)
						out.add(str);
					else if(len == 3)
					{
						out.add(tokens.get(0)+" "+tokens.get(1));
						out.add(tokens.get(2));
					}
					else
					{
						int j = len;
						while(j>3)
						{
							out.add(tokens.get(len-j)+" "+tokens.get(len-j+1));
							j = j-2;
						}
						while(j>0)
						{
							out.add(tokens.get(len-j));
							j--;
						}
					}
				}
				
				else
				{
					out.addAll(tokens);
				}
			}
		}
		return out;
	}


	public Parse getNameParser() {
		return nameParser;
	}


	public void setNameParser(Parse nameParser) {
		this.nameParser = nameParser;
	}


	public Parse getAddressParser() {
		return addressParser;
	}


	public void setAddressParser(Parse addressParser) {
		this.addressParser = addressParser;
	}
}
