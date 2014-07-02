
public class UserDTO {

	private String firstName;
	private String lastName;
	private String salutation;
	private String abbrevation;
	private String initial;
	
	private int gender;
	
	private int age;
	
	private String village;
	private String state;
	private String subDistrict;
	private String district;
	
	private String houseNumber;
	private String houseName;
	private String street;
	private String postCode;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getSalutation() {
		return salutation;
	}
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}
	public String getAbbrevation() {
		return abbrevation;
	}
	public void setAbbrevation(String abbrevation) {
		this.abbrevation = abbrevation;
	}
	public String getInitial() {
		return initial;
	}
	public void setInitial(String initial) {
		this.initial = initial;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getSubDistrict() {
		return subDistrict;
	}
	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getHouseNumber() {
		return houseNumber;
	}
	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}
	public String getHouseName() {
		return houseName;
	}
	public void setHouseName(String houseName) {
		this.houseName = houseName;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	public UserDTO()
	{
		setFirstName("");
		setLastName("");
		setSalutation("");
		setAbbrevation("");
		setInitial("");
		setGender(-1);
		setAge(-1);
		setVillage("");
		setDistrict("");
		setSubDistrict("");
		setState("");
		setPostCode("");
		setHouseNumber("");
		setHouseName("");
		setStreet("");
	}
	
	@Override
	public String toString()
	{
		String str = "{\n";
		if(!this.salutation.equals(""))
			str = str + "\t salutation : "+ this.salutation + " \n";
		if(!this.abbrevation.equals(""))
			str = str + "\t abbrevation : "+ this.abbrevation + " \n";
		if(!this.initial.equals(""))
			str = str + "\t initial : "+ this.initial + " \n";
		if(!this.firstName.equals(""))
			str = str + "\t firstName : "+ this.firstName + " \n";
		if(!this.lastName.equals(""))
			str = str + "\t lastName : "+ this.lastName + " \n";
		if(!(this.gender == -1))
			str = str + "\t\t gender : "+ Integer.toString(this.gender) + " \n";
		if(!(this.age == -1))
			str = str + "\t\t age : "+ Integer.toString(this.age) + " \n";
		if(!this.houseNumber.equals(""))
			str = str + "\t houseNumber : "+ this.houseNumber + " \n";
		if(!this.houseName.equals(""))
			str = str + "\t houseName : "+ this.houseName + " \n";
		if(!this.street.equals(""))
			str = str + "\t street : "+ this.street + " \n";
		if(!this.village.equals(""))
			str = str + "\t village : "+ this.village + " \n";
		if(!this.subDistrict.equals(""))
			str = str + "\t subDistrict : "+ this.subDistrict + " \n";
		if(!this.district.equals(""))
			str = str + "\t district : "+ this.district + " \n";
		if(!this.state.equals(""))
			str = str + "\t state : "+ this.state + " \n";
		if(!this.postCode.equals(""))
			str = str + "\t postCode : "+ this.postCode + " \n";
		str = str + "}\n";
		return str;
	}
}
