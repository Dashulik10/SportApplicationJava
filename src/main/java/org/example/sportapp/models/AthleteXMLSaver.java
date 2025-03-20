package org.example.sportapp.models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

/*
* XML (eXtensible Markup Language) - язык разметки, предназначенный
    для хранения структурированных данных.
* Парсеры нужны для считывания данных из XML-файлов (в удобном формате, например в объекты Java)
* DOM (Document Object Model)
*   считывает весь файл и сохраняет в виде дерева объектов (и не только, также и для чтения,
*       но это жрет больше памяти и сложнее(но можно добавлять, удалять и тд., но для маленьких доков))
*   позволяет модифицировать, добавлять, изменять узлы, структуру и тд.
*   Подходит для небольших файлов (потому что файл полностью в оперативку загружает)
*   Поддерживет валидацию для этих файлов
* SAX (Simple API for XML)
*   считывает XML построчно
*   не загружает весь документ в память (плюсик, если нужно брать из файла только что-то определённое)
*   сложноват в реализации, так как нужно самому обрабатывать (начало, конец)
* StAX (Streaming API for XML) - стриминговый парсер, похож на предыдущий, но
*   позволяет как читать, так и писать XML потоком
*   работает только на событиях, не давая напрямую контролировать процесс парсинга.
*   StAX позволяет прямо управлять чтением данных (например, ждать конкретного тега).
* */



/*
* Мы создаем фабрику, билдер и сам документ, потому что просто напрямую создать документ нельзя.
* Ввиду того, что создание объекта документ требует настройки конфигурации (а вообще это интерфейс),
* а мы можем её или не настроить сначала,
* или захотеть поменять потом XML XSD
*
* От фабрики используем сначала билдер, потому что он умеет не только создавать документ
* Но и загружать / разгружать XML файлы
* */



public class AthleteXMLSaver {
    public void saveToXML(File file, List<Athlete> athletes) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder(); // позволяет созданить пустой XML файл (или загрузить существующий)

        Document document = builder.newDocument(); //структура дерева DOM с нуля!
        Element rootElement = document.createElement("Athletes"); // Оборачиваем всех атлетов
        document.appendChild(rootElement); // добавляем к дереву

        for (Athlete athlete : athletes) {
            Element athleteElement = document.createElement("Athlete");

            // а можно кстати не сокращать, а прописать
            // athleteElement.setAttribute() - это аттрибут id="101" , а потом к руту добавляем

            // SecondName.setTextContent("Bla bla"), а потом добавляем к athleteElement
            // Это <SecondName>John Doe</SecondName>
            athleteElement.appendChild(createElement(document, "SecondName", athlete.getSecond_name()));
            athleteElement.appendChild(createElement(document, "FirstName", athlete.getFirst_name()));
            athleteElement.appendChild(createElement(document, "Patronymic", athlete.getPatronymic()));
            athleteElement.appendChild(createElement(document, "TeamCast", athlete.getTeam_cast()));
            athleteElement.appendChild(createElement(document, "Position", athlete.getPosition()));
            athleteElement.appendChild(createElement(document, "Title", String.valueOf(athlete.getTitle())));
            athleteElement.appendChild(createElement(document, "Sport", String.valueOf(athlete.getSport())));
            athleteElement.appendChild(createElement(document, "Rank", athlete.getRank()));
            rootElement.appendChild(athleteElement); // во, добавляем к РУТУ
        }

        // Чтобы трансформировать DOM структуру (дерево) в XML файл
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Сохраняем XML  в формате с нормальными отступами
        DOMSource source = new DOMSource(document); // предоставляет источник даннх для создания XML файла
        StreamResult result = new StreamResult(file); // а тут куда файл пихать будем
        transformer.setOutputProperty("indent", "yes"); // тут свойство для разметки, в данном случае отступы
        transformer.transform(source, result); // ТРАНСФОРМАЦИЯ
    }

    public static Element createElement(Document document, String tagName, String textContent){
        Element element = document.createElement(tagName);
        element.setTextContent(textContent);
        return element;
    }
}