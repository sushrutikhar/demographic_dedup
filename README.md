
Author - Ikhar Sushrut Meghshyam

"Demographic Data Deduplication"

WorkFlow:
   1. Free form unstructured data of the form :
        name
        gender
        age
        address
    will be first structured using Maximum-Entropy Classifier (Parser)
    Structured data is of form 
        name      <salutation, abbrevation, intial, firstname, lastname>
        gender    <0 (male) / 1 (female)> (-1 for unknown data)
        age       < positive integer> (-1 for unknown data)
        address   < housename, housenumber, street, village, subdistrict, 
                        district, state, postcode>

  2. Construct indexed-database for the Structured data generated using Elastic-Search
  
  3. Searching on indexed-data
        Analyzer = DoubleMetaphone
        Input = Structured Data from Parser

Run command::

```
java -cp .:src/:bin/:lib/*:bin/org/elasticsech/index/analysis/phonetic/* testing >Results.txt
```


*Running Executable dedup.jar*
```
java -jar dedup.jar testing >Results.txt
```

API calls - 
 
1.  Parser
 

```
    ParseInputFile obj = new ParseInputFile("input.txt");
    ArrayList<UserDAO> userList = obj.parse();
```


    Training model - 'addrTrainer.ser.gz' and 'nameTrainer.ser.gz'
    
    Changes into Parsing can be done in "prop" file
    

2.  Searching, Adding Doc , deleting

```
	indexSearch ES = new indexSearch();
	Client client = ES.createClient(clusterName,hostName,port);

	ES.deleteIndex(client, indexName);
	
	ES.createIndex(client, indexName);
	
	ES.addDoc(client,indexName, docType,userList);
	
	SearchResponse response = ES.searchDoc(client, indexName, docType, user); 

//Returns top 100 results sorted by matching scores
```

Sample Testing File is provided "testing.java"

Input File format-
line 1 - name
line 2 - gender
line 3 - age
line 4 onwards - address terminated by a single ";" on a new line


sample input  ::::


```
Mr. Ikhar Sushrut Meghshyam
0
20
Gajanan Maharaj Nagar,
Plot Number 7,
Bhusawal - 425201
Maharashtra.
;
```



sample parsed output :::


```
{
	 salutation :  Mr. 
	 firstName : Ikhar 
	 lastName : Sushrut 
		 gender : 0 
		 age : 20 
	 houseNumber :  Plot Number 7 
	 village :  Gajanan Maharaj Nagar 
	 subDistrict :  Bhusawal 
	 state :  Maharashtra. 
	 postCode :  425201 
}
```


sample search results :::


```

19.344625	6Ho64CP1RAS7pUv7QGzAlg	{lastName=Sushrut, abbrevation=, subDistrict= Bhusawal, street=, postCode= 425201, state= Maharashtra., houseNumber= Plot Number 7, houseName=, village= Gajanan Maharaj Nagar, initial=, age=20, gender=0, district=, salutation= Mr., firstName=Ikhar}

------------------------------

5.8280177	IsrXcxt5TS-zmojsHpErJA	{lastName=, abbrevation=, subDistrict= JALGAON, street=, postCode= 425201, state= RASHTRA, houseNumber=NO. 19 609, houseName=DIST M, village=P. B. SHRI RAM MANDIR WARD BHUSAVAL, initial=, age=-1, gender=-1, district=, salutation=, firstName=abc}

------------------------------

5.443898	8OtIKPHkRhKwgAQyxk4oww	{lastName=, abbrevation=, subDistrict= JALGAON, street= STATION ROAD, postCode= 425201, state= MAHARASHTRA, houseNumber= 481, houseName=HOUSE NO. DIST, village= PACHORA, initial=, age=-1, gender=-1, district=, salutation=, firstName=abc}

------------------------------

4.013788	sgqDrKCBSkmpkbHKga57Zg	{lastName=, abbrevation=, subDistrict= POINT, street= PUNE, postCode= 411007., state= MAHARASHITRA, houseNumber=S NO 15/2/1&1 69/1 OPP, houseName=SHOP NO 19 MEDI, village=20 KINGS SQUARE HOSPITAL ANUDH, initial=, age=-1, gender=-1, district=, salutation=, firstName=abc}

------------------------------

3.4818065	ro4noWJSQeqHbVh6z59YoA	{lastName=, abbrevation=, subDistrict= JALGAON, street= CINEMA ROAD, postCode= 425301, state= MAHARASHTRA, houseNumber= 163, houseName= DIST, village= YAWAL, initial=, age=-1, gender=-1, district=, salutation=, firstName=abc}

------------------------------

3.2488801	3xQ-x8YpTWeD7FKaD0o_jQ	{lastName=, abbrevation=, subDistrict=, street=, postCode= 424201, state= MAHARASHTRA, houseNumber=, houseName= DIST JALGAON, village=SHINDAD TAL. PACHORA, initial=, age=-1, gender=-1, district=, salutation=, firstName=abc}

------------------------------

3.194393	KcC-_nkbSBqwDDkteqsqDg	{lastName=, abbrevation=, subDistrict=, street=, postCode= 424201, state= MAHARASHTRA, houseNumber=, houseName= DIST JALGAON, village=GIRAD TAL. BHADGAON, initial=, age=-1, gender=-1, district=, salutation=, firstName=abc}
```

ElasticSearch configurations::

*config/elasticsearch.yml*

```
cluster.name: elasticsearch

index :
    analysis :
        analyzer :
            search_soundex :
                type : custom
                tokenizer : standard
                filter : [standard, soundex_filter]
        filter :
            soundex_filter :
                type : phonetic
                encoder : metaphone               
                replace : false

```

*installing phonetic-plugin*

```
bin/plugin -install elasticsearch/elasticsearch-analysis-phonetic/2.1.0
```

