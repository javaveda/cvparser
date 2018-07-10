## Attempt to parse the CV/Resume and extract the data
#### Extract data from CV/Resume in docx format as JSON
```
CVParser cv = new CVParser("/tmp/Resume.docx");
String json = cv.extractJson();
```
#### Extract data from CV/Resume in docx format as HashMap
```
CVParser cv = new CVParser("/tmp/Resume.docx");
String json = cv.extract();
```
