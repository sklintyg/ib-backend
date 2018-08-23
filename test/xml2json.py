import json
import xmltodict
 
with open("build/test-results/results.xml", 'r') as f:
    xmlString = f.read()
 
print("XML input (uild/test-results/results.xml):")
print(xmlString)
     
jsonString = json.dumps(xmltodict.parse(xmlString), indent=4)
 
print("\nJSON output(build/test-results/results.json):")
print(jsonString)
 
with open("build/test-results/results.json", 'w') as f:
    f.write(jsonString)
