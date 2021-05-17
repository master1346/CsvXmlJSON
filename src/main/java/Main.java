import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);

        String jsonCSV = listToJson(list);
        writeString(jsonCSV, "data.json");

        fileName = "data.xml";
        list = parseXML(fileName);

        String jsonXML = listToJson(list);
       writeString(jsonXML, "data2.json");
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> listEmployee = csv.parse();
            return listEmployee;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }
    private static void writeString(String value, String fileName){
        try(FileWriter fileWriter = new FileWriter(fileName)){
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(value);
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.flush();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(fileName));

        List<Employee> listEmployee = new ArrayList<>();

        Node root = document.getDocumentElement();

        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;

                String[] content = element.getTextContent().trim().replaceAll("\\s+",",").split(",");

                listEmployee.add(new Employee(Long.parseLong(content[0]), content[1], content[2], content[3],Integer.parseInt(content[4])));
            }

        }
        return listEmployee;
    }
}

