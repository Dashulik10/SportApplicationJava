package org.example.sportapp.models;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AthleteXMLLoader {
    public List<Athlete> loadFromXML(File file) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        List<Athlete> athletes = new ArrayList<>();
        // Запускаем разбор с базовым обработчиком (ну тут просто анонимный класс DefaultHndler)
        parser.parse(file, new DefaultHandler(){

            // для временного накопления данных для записи
            Athlete currentAthlete = null; //создаем текущего атлета
            StringBuilder currentValue = new StringBuilder(); // это для изменяемых строк

            /*
            * uri - пространство имен <ns1:Athlete xmlns:ns1="http://example.com/athlete">
            * localName - localName = "Athlete"
            * qNmae - qName = "ns1:Athlete"
            * Attributes - <Athlete id="1" role="captain">
            */

            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if ("Athlete".equals(qName)) {
                    currentAthlete = new Athlete(0, "", "",
                            "", "", "", 0, 0L, "");
                }
                currentValue.setLength(0); // на всякий, чтобы не было мусора от предыдущего чтения
            }


            // char[] - массив символов, который парсер передает в потоковом режиме (содержимое)
            // int start - начало массива сверху
            // int length - кол-во символов, которые нужно считать как часть текста
            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                currentValue.append(ch, start, length);
            }

            // String uri - для пространства имен (у меня не используется)
            // String localname - локальное без учета пространства имен
            @Override
            public void endElement (String uri, String localname, String qName) throws SAXException {
                if (currentAthlete == null) return;

                switch (qName){
                    case "SecondName":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentValue.toString(),
                                currentAthlete.getFirst_name(),
                                currentAthlete.getPatronymic(),
                                currentAthlete.getTeam_cast(),
                                currentAthlete.getPosition(),
                                currentAthlete.getTitle(),
                                currentAthlete.getSport(),
                                currentAthlete.getRank()
                        );
                        break;

                    case "FirstName":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentValue.toString(),
                                currentAthlete.getPatronymic(),
                                currentAthlete.getTeam_cast(),
                                currentAthlete.getPosition(),
                                currentAthlete.getTitle(),
                                currentAthlete.getSport(),
                                currentAthlete.getRank()
                        );
                        break;

                    case "Patronymic":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentAthlete.getFirst_name(),
                                currentValue.toString(),
                                currentAthlete.getTeam_cast(),
                                currentAthlete.getPosition(),
                                currentAthlete.getTitle(),
                                currentAthlete.getSport(),
                                currentAthlete.getRank()
                        );
                        break;

                    case "TeamCast":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentAthlete.getFirst_name(),
                                currentAthlete.getPatronymic(),
                                currentValue.toString(),
                                currentAthlete.getPosition(),
                                currentAthlete.getTitle(),
                                currentAthlete.getSport(),
                                currentAthlete.getRank()
                        );
                        break;

                    case "Position":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentAthlete.getFirst_name(),
                                currentAthlete.getPatronymic(),
                                currentAthlete.getTeam_cast(),
                                currentValue.toString(),
                                currentAthlete.getTitle(),
                                currentAthlete.getSport(),
                                currentAthlete.getRank()
                        );
                        break;

                    case "Title":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentAthlete.getFirst_name(),
                                currentAthlete.getPatronymic(),
                                currentAthlete.getTeam_cast(),
                                currentAthlete.getPosition(),
                                Integer.parseInt(currentValue.toString()),
                                currentAthlete.getSport(),
                                currentAthlete.getRank()
                        );
                        break;

                    case "Sport":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentAthlete.getFirst_name(),
                                currentAthlete.getPatronymic(),
                                currentAthlete.getTeam_cast(),
                                currentAthlete.getPosition(),
                                currentAthlete.getTitle(),
                                Long.parseLong(currentValue.toString()),
                                currentAthlete.getRank()
                        );
                        break;

                    case "Rank":
                        currentAthlete = new Athlete(
                                currentAthlete.getId(),
                                currentAthlete.getSecond_name(),
                                currentAthlete.getFirst_name(),
                                currentAthlete.getPatronymic(),
                                currentAthlete.getTeam_cast(),
                                currentAthlete.getPosition(),
                                currentAthlete.getTitle(),
                                currentAthlete.getSport(),
                                currentValue.toString()
                        );
                        break;
                }

                if ("Athlete".equals(qName)) {
                    athletes.add(currentAthlete);
                }
            }
        });


        return athletes;
    }
}